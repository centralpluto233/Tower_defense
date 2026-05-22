package towerdefence.game.ecs;

import towerdefence.game.ecs.components.SpawnScheduleComponent;
import towerdefence.game.ecs.components.SpawnCountComponent;
import towerdefence.game.ecs.components.SpawnPathComponent;
import towerdefence.game.ecs.components.SpawnTypeComponent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ECSManager {
    private int nextEntityId = 1;
    private final Set<Integer> ECSentities = new HashSet<>();

    private final Map<Integer, SpawnScheduleComponent> spawnSchedules = new HashMap<>();
    private final Map<Integer, SpawnCountComponent> spawnCounts = new HashMap<>();
    private final Map<Integer, SpawnPathComponent> spawnPaths = new HashMap<>();
    private final Map<Integer, SpawnTypeComponent> spawnTypes = new HashMap<>();

    public int createEntity() {
        int id = nextEntityId++;
        ECSentities.add(id);
        return id;
    }

    public void removeEntity(int id) {
        ECSentities.remove(id);
        spawnSchedules.remove(id);
        spawnCounts.remove(id);
        spawnPaths.remove(id);
        spawnTypes.remove(id);
    }

    public Set<Integer> getAllEntities() {
        return Set.copyOf(ECSentities);
    }

    public Map<Integer, SpawnScheduleComponent> getSpawnSchedules() {
        return Map.copyOf(spawnSchedules);
    }

    public Map<Integer, SpawnCountComponent> getSpawnCounts() {
        return Map.copyOf(spawnCounts);
    }

    public Map<Integer, SpawnPathComponent> getSpawnPaths() {
        return Map.copyOf(spawnPaths);
    }

    public Map<Integer, SpawnTypeComponent> getSpawnTypes() {return Map.copyOf(spawnTypes);}

    public void addSpawnJob(int id, SpawnScheduleComponent spawnSchedule, SpawnCountComponent spawnCount, SpawnPathComponent spawnPath, SpawnTypeComponent spawnType) {
        this.spawnSchedules.put(id, spawnSchedule);
        this.spawnCounts.put(id, spawnCount);
        this.spawnPaths.put(id, spawnPath);
        this.spawnTypes.put(id, spawnType);
    }
}
