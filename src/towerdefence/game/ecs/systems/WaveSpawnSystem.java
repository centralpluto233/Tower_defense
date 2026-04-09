package towerdefence.game.ecs.systems;

import towerdefence.game.AbstractGameFactory;
import towerdefence.game.Game;
import towerdefence.game.ecs.ECSManager;
import towerdefence.game.ecs.components.SpawnCountComponent;
import towerdefence.game.ecs.components.SpawnPathComponent;
import towerdefence.game.ecs.components.SpawnScheduleComponent;
import towerdefence.game.util.Vector2D;
import towerdefence.lua.LevelData;
import towerdefence.lua.WaveData;
import towerdefence.system.logging.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class WaveSpawnSystem {
    private static final Logger logger = GameLogger.getLogger();

    public void update(double dt, ECSManager ecs, Game game, AbstractGameFactory factory) {
        List<Integer> toRemove = new ArrayList<>();

        for (int id: ecs.getAllEntities()) {
            SpawnScheduleComponent scheduleComp = ecs.getSpawnSchedules().get(id);
            SpawnCountComponent countComp = ecs.getSpawnCounts().get(id);
            SpawnPathComponent pathComp = ecs.getSpawnPaths().get(id);

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
            game.addEntity(factory.createGoblin(spawnPoint.x(), spawnPoint.y(), path));

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
            ecs.addSpawnJob(id, new SpawnScheduleComponent(wave.getIntervalSeconds(), wave.getStartDelaySeconds()), new SpawnCountComponent(wave.getCount()), new SpawnPathComponent(levelData.getPath()));
        }
    }
}
