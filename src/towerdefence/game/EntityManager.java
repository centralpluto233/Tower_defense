package towerdefence.game;

import towerdefence.game.entities.Entity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.stats.RunStats;
import towerdefence.util.Vector2D;
import towerdefence.system.logging.GameLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EntityManager {
    private static final Logger logger = GameLogger.getLogger();

    private final List<Tower> towers = new ArrayList<>();
    private final List<EnemyEntity> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    public final RunStats stats;

    public EntityManager(RunStats stats) {
        this.stats = stats;
    }

    /**
     * Adds entities to one of the entities lists
     * @param entity entities to add to list
     */
    public void addEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to add null entities to game");
            return;
        }
        if (entity instanceof Tower) {towers.add((Tower) entity);}
        if (entity instanceof Projectile) {projectiles.add((Projectile) entity);}
        if (entity instanceof EnemyEntity) {enemies.add((EnemyEntity) entity);}
    }

    /**
     * Removes entities from correct entities-list
     * @param entity entities to remove from list
     */
    public void removeEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to remove null entities from game");
            return;
        }
        if (entity instanceof Tower) {towers.remove((Tower) entity);}
        if (entity instanceof Projectile) {projectiles.remove((Projectile) entity);}
        if (entity instanceof EnemyEntity) {enemies.remove((EnemyEntity) entity);}
    }

    public boolean update(double deltaTime, AbstractGameFactory factory, Game game) {
        for (Tower tower : towers) {
            tower.updateCombat(enemies, game);
            tower.update(deltaTime);
        }

        for (Projectile projectile : projectiles) {
            projectile.update(deltaTime);
        }

        checkProjectileCollisions();

        for (EnemyEntity enemy : enemies) {
            enemy.update(deltaTime);
        }

        return removeMarkedEntities();
    }

    private void checkProjectileCollisions() {
        for (Projectile p : projectiles) {
            if (p.isMarkedForRemoval()) continue;
            for (EnemyEntity e : enemies) {
                if (!e.isMarkedForRemoval() && p.overlaps(e)) {
                    p.dealDamage(e);
                    p.markForRemoval();
                    break;
                }
            }
        }
    }

    /**
     * Removes all entities that are marked for removal from the game
     */
    public boolean removeMarkedEntities() {
        boolean isDefeated = false;

        for (EnemyEntity enemy : enemies) {
            if (enemy.isMarkedForRemoval()) {
                if (enemy.isDead()) {
                    // Enemy was killed
                    stats.onEnemyKilled(enemy.getScoreValue());
                } else {
                    // Enemy escaped
                    stats.onEnemyEscaped(enemy.getDamage());
                    if (stats.isDefeated()) {
                        isDefeated = true;
                    }
                }
            }
        }

        enemies.removeIf(Entity::isMarkedForRemoval);
        projectiles.removeIf(Entity::isMarkedForRemoval);

        // normally don't get marked for removal
        towers.removeIf(Entity::isMarkedForRemoval);

        return isDefeated;
    }

    public List<Tower> getTowers() {return List.copyOf(towers);}

    public List<EnemyEntity> getEnemies() {return List.copyOf(enemies);}

    public List<Projectile> getProjectiles() {return List.copyOf(projectiles);}

    public boolean isTowerSpotOccupied(Vector2D spot) {
        return towers.stream()
                .anyMatch(tower -> tower.getPosition().equals(spot));
    }

    public int getTotalEntityCount() {
        return towers.size()+enemies.size()+projectiles.size();
    }

    public boolean hasNoEnemies() {
        return enemies.isEmpty();
    }
}
