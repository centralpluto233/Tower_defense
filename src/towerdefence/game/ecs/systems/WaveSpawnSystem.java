package towerdefence.game.ecs.systems;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.Game;
import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.components.SpawnCountComponent;
import towerdefence.game.ecs.components.SpawnPathComponent;
import towerdefence.game.ecs.components.SpawnScheduleComponent;
import towerdefence.game.ecs.components.SpawnTypeComponent;
import towerdefence.util.Vector2D;
import towerdefence.level.LevelData;
import towerdefence.level.WaveData;

import java.util.ArrayList;
import java.util.List;

public class WaveSpawnSystem {

    /**
     * System to manage spawning of enemies based on spawn schedules, counts, paths, and types defined in the ECS components.
     * It checks each spawn job every update cycle, spawns enemies when their schedule is due, and removes spawn jobs that are completed.
     * @param dt the time in seconds since the last update, used to tick down spawn schedules
     * @param ecs the ECSManager that holds all entities and their components, used to access spawn schedules, counts, paths, and types
     * @param game the Game instance, used to add new enemy entities when they spawn
     * @param factory the AbstractGameFactory, used to create new enemy entities based on their type when they spawn
     */
    public void update(double dt, ECSManager ecs, Game game, AbstractGameFactory factory) {
        List<Integer> toRemove = new ArrayList<>();

        for (int id: ecs.getAllEntities()) {
            SpawnScheduleComponent scheduleComp = ecs.getSpawnSchedules().get(id);
            SpawnCountComponent countComp = ecs.getSpawnCounts().get(id);
            SpawnPathComponent pathComp = ecs.getSpawnPaths().get(id);
            SpawnTypeComponent typeComp = ecs.getSpawnTypes().get(id);

            if (scheduleComp == null || countComp == null || pathComp == null) {
                continue;
            }

            if (countComp.isDone()) {
                toRemove.add(id);
                continue;
            }

            scheduleComp.tick(dt);
            if (scheduleComp.getTimeLeftSeconds() > 0.0) {
                continue;
            }

            List<Vector2D> path = pathComp.getPath();
            if (path.isEmpty()) {
                toRemove.add(id);
                continue;
            }

            Vector2D spawnPoint = path.getFirst();

            String type = (typeComp != null) ? typeComp.getEnemyType() : "goblin";
            game.addEntity(switch (type) {
                case "bat" -> factory.createBat(spawnPoint.x(), spawnPoint.y(), path);
                case "goblin" -> factory.createGoblin(spawnPoint.x(), spawnPoint.y(), path);
                default -> throw new IllegalStateException("Unknown spawn type: " + type);
            });

            countComp.consumeOne();
            scheduleComp.resetToInterval();

        }
        for (Integer id: toRemove) {
            ecs.removeEntity(id);
        }
    }

    public void init(LevelData levelData, ECSManager ecs) {
        for (WaveData wave : levelData.getWaves()) {
            int id = ecs.createEntity();
            ecs.addSpawnJob(id, new SpawnScheduleComponent(wave.intervalSeconds(),
                    wave.startDelaySeconds()),
                    new SpawnCountComponent(wave.count()),
                    new SpawnPathComponent(levelData.getPath()),
                    new SpawnTypeComponent(wave.enemyType()));
        }
    }

    public void reset(ECSManager ecs) {
        for (int id: ecs.getAllEntities()) {
            ecs.removeEntity(id);
        }
    }
}
