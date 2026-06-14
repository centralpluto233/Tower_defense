package towerdefence.game.ecs.systems;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.Game;
import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.components.spawn.SpawnCountComponent;
import towerdefence.game.ecs.components.spawn.SpawnPathComponent;
import towerdefence.game.ecs.components.spawn.SpawnScheduleComponent;
import towerdefence.game.ecs.components.spawn.SpawnTypeComponent;
import towerdefence.level.LevelData;
import towerdefence.level.WaveData;
import towerdefence.util.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class WaveSpawnSystem {
    private final Logger logger = Logger.getLogger(WaveSpawnSystem.class.getName());
    private double nextWaveCountdown = 0;
    private ECSManager ecsManager;
    private List<Double> distinctStartDelays = new ArrayList<>();

    /**
     * updates the wavespawnsystem with all of its components
     *
     * @param dt      time passed since last update
     * @param ecs     the ECSManager to access spawn jobs and their components, allowing the system to manage enemy spawning based on the defined waves
     * @param game    the game to add new entities
     * @param factory the factory to create new entities
     * @throws Exception if an unknown spawn type is encountered in the SpawnTypeComponent, indicating a configuration error in the lua levels
     */
    public void update(double dt, ECSManager ecs, Game game, AbstractGameFactory factory) throws Exception {
        List<Integer> toRemove = new ArrayList<>();

        for (int id : ecs.getAllEntities()) {
            Optional<SpawnScheduleComponent> scheduleCompOpt = Optional.ofNullable(ecs.getSpawnSchedules().get(id));
            Optional<SpawnCountComponent> countCompOpt = Optional.ofNullable(ecs.getSpawnCounts().get(id));
            Optional<SpawnPathComponent> pathCompOpt = Optional.ofNullable(ecs.getSpawnPaths().get(id));
            SpawnTypeComponent typeComp = ecs.getSpawnTypes().get(id);

            // Skip entities that don't have the required spawn components
            if (scheduleCompOpt.isEmpty() || countCompOpt.isEmpty() || pathCompOpt.isEmpty()) {
                continue;
            }

            SpawnScheduleComponent scheduleComp = scheduleCompOpt.get();
            SpawnCountComponent countComp = countCompOpt.get();
            SpawnPathComponent pathComp = pathCompOpt.get();

            if (countComp.isDone()) {
                toRemove.add(id);
                continue;
            }

            scheduleComp.tick(dt);
            if (scheduleComp.getTimeLeftSeconds() > 0.0) {
                continue;
            }

            List<Vector2D> path = pathComp.path();
            if (path == null || path.isEmpty()) {
                toRemove.add(id);
                continue;
            }

            // Spawpoint is first point of path
            Vector2D spawnPoint = path.getFirst();

            String type = (typeComp != null) ? typeComp.enemyType() : "goblin";
            game.addEntity(switch (type) {
                case "bat" -> factory.createBat(spawnPoint.x(), spawnPoint.y(), path);
                case "goblin" -> factory.createGoblin(spawnPoint.x(), spawnPoint.y(), path);
                case "bigslime" -> factory.createBigSlime(spawnPoint.x(), spawnPoint.y(), path);
                case "skeleton" -> factory.createSkeleton(spawnPoint.x(), spawnPoint.y(), path);
                default -> throw new Exception("Unknown spawn type: " + type);
            });

            // spawned one enemy, update count and schedule
            countComp.consumeOne();
            scheduleComp.resetToInterval();
        }
        // calculate next wave countdown as the minimum time left among all spawn schedules that haven't started yet
        nextWaveCountdown = ecs.getAllEntities().stream()
                .filter(id -> {
                    SpawnCountComponent count = ecs.getSpawnCounts().get(id);
                    SpawnScheduleComponent schedule = ecs.getSpawnSchedules().get(id);
                    return count != null && schedule != null
                            && count.hasNotSpawnedAny()
                            && schedule.getTimeLeftSeconds() > 0;
                })
                .map(id -> ecs.getSpawnSchedules().get(id))
                .mapToDouble(SpawnScheduleComponent::getTimeLeftSeconds)
                .min()
                .orElse(0);

        // remove entities that are done spawning
        for (Integer id : toRemove) {
            ecs.removeEntity(id);
        }
    }

    /**
     * Returns the current wave number based on the total waves and the number of spawn jobs that are still active in the ECSManager.
     *
     * @return the current wave number, calculated as totalWaves minus the count of entities that have spawn jobs (i.e., those with SpawnCountComponent)
     */
    public int getCurrentWave() {
        if (ecsManager == null) {
            logger.warning("ecsManager is null");
            return 0;
        }

        // collect startDelays of waves that haven't started yet
        Set<Double> notStartedDelays = ecsManager.getAllEntities().stream()
                .filter(id -> {
                    SpawnCountComponent count = ecsManager.getSpawnCounts().get(id);
                    SpawnScheduleComponent schedule = ecsManager.getSpawnSchedules().get(id);
                    return count != null && schedule != null && count.hasNotSpawnedAny();
                })
                .map(id -> ecsManager.getSpawnSchedules().get(id).getStartDelay())
                .collect(java.util.stream.Collectors.toSet());

        // count how many distinct startDelays are still in the notStartedDelays set
        long notStartedGroups = distinctStartDelays.stream()
                .filter(notStartedDelays::contains)
                .count();

        return (int) (distinctStartDelays.size() - notStartedGroups);
    }

    /**
     * Returns the total number of waves based on the distinct start delays collected during initialization.
     *
     * @return the total number of waves, which is the size of the distinctStartDelays list
     */
    public int getTotalWaves() {
        return distinctStartDelays.size();
    }

    /**
     * Returns the countdown in seconds until the next wave starts, based on the minimum time left among all spawn schedules that haven't started yet.
     *
     * @return the countdown in seconds until the next wave starts, or 0 if there are no upcoming waves
     */
    public double getNextWaveCountdown() {
        return nextWaveCountdown;
    }

    /**
     * Initializes the WaveSpawnSystem with the given LevelData and ECSManager. It creates spawn jobs for each wave defined in the LevelData, setting up their spawn schedules, counts, paths, and types in the ECSManager.
     *
     * @param levelData the LevelData containing wave definitions, including start delays, intervals, counts, paths, and enemy types
     * @param ecs       the ECSManager to which the spawn jobs will be added, allowing the system to manage enemy spawning based on the defined waves
     */
    public void init(LevelData levelData, ECSManager ecs) {
        this.ecsManager = ecs;

        this.distinctStartDelays = levelData.waves().stream()
                .map(WaveData::startDelaySeconds)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());

        for (WaveData wave : levelData.waves()) {
            int id = ecs.createEntity();
            ecs.addSpawnJob(id, new SpawnScheduleComponent(wave.intervalSeconds(),
                            wave.startDelaySeconds()),
                    new SpawnCountComponent(wave.count()),
                    new SpawnPathComponent(levelData.getPath(wave.pathIndex())),
                    new SpawnTypeComponent(wave.enemyType()));
        }
    }

    /**
     * Resets the WaveSpawnSystem by removing all entities from the ECSManager and resetting the next wave countdown to zero. This effectively clears all spawn jobs and prepares the system for a fresh start.
     */
    public void reset() {
        if (ecsManager == null) {
            logger.fine("reset() called before init(), skipping");
            return;
        }
        for (int id : ecsManager.getAllEntities()) {
            ecsManager.removeEntity(id);
        }
        nextWaveCountdown = 0;
    }
}
