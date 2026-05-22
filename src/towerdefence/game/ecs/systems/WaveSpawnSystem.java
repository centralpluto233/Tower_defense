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
            ecs.addSpawnJob(id, new SpawnScheduleComponent(wave.getIntervalSeconds(),
                    wave.getStartDelaySeconds()),
                    new SpawnCountComponent(wave.getCount()),
                    new SpawnPathComponent(levelData.getPath()),
                    new SpawnTypeComponent(wave.getEnemyType()));
        }
    }
}
