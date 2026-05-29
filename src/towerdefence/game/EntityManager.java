package towerdefence.game;

import towerdefence.game.entities.Entity;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.stats.RunStats;
import towerdefence.system.logging.GameLogger;
import towerdefence.util.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EntityManager {
    private static final Logger logger = GameLogger.getLogger();

    private final List<Tower> towers = new ArrayList<>();
    private final List<EnemyEntity> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    private final RunStats stats;

    // ---------------------------------------------------------------------
    // Construction
    // ---------------------------------------------------------------------

    /**
     * Creates a new EntityManager with the given RunStats instance.
     *
     * @param stats the RunStats instance to use for tracking game statistics such as score and health
     */
    public EntityManager(RunStats stats) {
        this.stats = stats;
    }

    // ---------------------------------------------------------------------
    // Entity mutation
    // ---------------------------------------------------------------------

    /**
     * Adds entities to one of the entities lists
     *
     * @param entity entities to add to list
     */
    public void addEntity(Entity entity) {
        if (entity == null) {
            logger.warning("Attempted to add null entities to game");
            return;
        }
        if (entity instanceof Tower t) {
            towers.add(t);
        }
        if (entity instanceof Projectile p) {
            projectiles.add(p);
        }
        if (entity instanceof EnemyEntity e) {
            enemies.add(e);
        }
    }

    public void clear() {
        enemies.clear();
        towers.clear();
        projectiles.clear();
    }

    // ---------------------------------------------------------------------
    // Update loop
    // ---------------------------------------------------------------------

    /**
     * Updates all entities in the game, checks for collisions, and removes any entities that are marked for removal.
     *
     * @param deltaTime the time elapsed since the last update, used for updating entity positions and states
     * @param game      the Game instance, used to access game state and add new entities if needed during updates
     * @return true if the player is defeated (e.g. health <= 0), false otherwise
     */
    public boolean update(double deltaTime, Game game) {
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

    // ---------------------------------------------------------------------
    // Internal cleanup
    // ---------------------------------------------------------------------

    /**
     * Checks for collisions between projectiles and enemies.
     * If a projectile overlaps with an enemy, it deals damage to the enemy and marks the projectile for removal.
     */
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
    private boolean removeMarkedEntities() {
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

    // ---------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------

    /**
     * Returns an unmodifiable copy of the list of towers currently in the game.
     *
     * @return an unmodifiable list of towers
     */
    public List<Tower> getTowers() {
        return List.copyOf(towers);
    }

    /**
     * Returns an unmodifiable copy of the list of enemies currently in the game.
     *
     * @return an unmodifiable list of enemies
     */
    public List<EnemyEntity> getEnemies() {
        return List.copyOf(enemies);
    }

    /**
     * Returns an unmodifiable copy of the list of projectiles currently in the game.
     *
     * @return an unmodifiable list of projectiles
     */
    public List<Projectile> getProjectiles() {
        return List.copyOf(projectiles);
    }

    /**
     * Checks if there is a tower occupying the given spot.
     *
     * @param spot the position to check for tower occupancy
     * @return true if there is a tower at the given position, false otherwise
     */
    public boolean isTowerSpotOccupied(Vector2D spot) {
        return towers.stream()
                .anyMatch(tower -> tower.getPosition().equals(spot));
    }

    /**
     * Checks if there are no enemies currently in the game.
     *
     * @return true if there are no enemies, false otherwise
     */
    public boolean hasNoEnemies() {
        return enemies.isEmpty();
    }
}
