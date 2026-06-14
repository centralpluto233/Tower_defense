package towerdefence.game.entities;

import towerdefence.game.Game;
import towerdefence.game.entities.enemy.EnemyEntity;
import towerdefence.game.entities.projectile.Projectile;
import towerdefence.game.entities.tower.Tower;
import towerdefence.game.stats.RunStats;
import towerdefence.util.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class EntityManager {
    private final List<Tower> towers = new ArrayList<>();
    private final List<EnemyEntity> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private final RunStats stats;
    private Optional<Consumer<EnemyEntity>> onEnemyKilledCallback = Optional.empty();
    private Optional<Consumer<EnemyEntity>> onEnemyEscapedCallback = Optional.empty();
    private Optional<Consumer<Tower>> onTowerPlacedCallback = Optional.empty();
    private Optional<Consumer<Tower>> onTowerSoldCallback = Optional.empty();

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

    // --------------------------------------------------------------------
    // Callbacks
    // --------------------------------------------------------------------

    /**
     * Sets a callback function to be called whenever an enemy is killed.
     * used by view to trigger kill animations and sound effects
     *
     * @param callback receives killed {@link EnemyEntity}
     */
    public void setOnEnemyKilledCallback(Consumer<EnemyEntity> callback) {
        this.onEnemyKilledCallback = Optional.of(callback);
    }

    /**
     * Sets a callback function to be called whenever a tower is placed.
     * used by view to trigger build animations and sound effects
     *
     * @param callback receives placed {@link Tower}
     */
    public void setOnTowerPlacedCallback(Consumer<Tower> callback) {
        this.onTowerPlacedCallback = Optional.of(callback);
    }

    /**
     * Sets a callback function to be called whenever a tower is sold.
     * used by view to trigger sell animations and sound effects
     *
     * @param callback receives sold {@link Tower}
     */
    public void setOnTowerSoldCallback(Consumer<Tower> callback) {
        this.onTowerSoldCallback = Optional.of(callback);
    }

    /**
     * Sets a callback function to be called whenever an enemy is killed.
     * used by view to trigger escape animations and sound effects
     *
     * @param callback receives escaped {@link EnemyEntity}
     */
    public void setOnEnemyEscapedCallback(Consumer<EnemyEntity> callback) {
        this.onEnemyEscapedCallback = Optional.of(callback);
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
        if (entity instanceof Tower t) {
            towers.add(t);
            onTowerPlacedCallback.ifPresent(callback -> callback.accept(t));
        }
        if (entity instanceof Projectile p) {
            projectiles.add(p);
        }
        if (entity instanceof EnemyEntity e) {
            enemies.add(e);
        }
    }

    /**
     * Clears all entities from the game, used when resetting the game to prepare for a new run
     */
    public void clear() {
        enemies.clear();
        towers.clear();
        projectiles.clear();
    }

    /**
     * Sells the tower at the given index, marking it for removal and returning the amount of money refunded to the player.
     *
     * @param towerIndex the index of the tower to sell in the towers list
     * @return the amount of money refunded to the player for selling the tower
     */
    public int sellTower(int towerIndex) {
        Tower tower = towers.get(towerIndex);
        int refund = tower.getRefund();
        tower.markForRemoval();
        onTowerSoldCallback.ifPresent(callback -> callback.accept(tower));
        return refund;
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

            boolean impacted = false;

            // check for direct hit on enemies
            for (EnemyEntity e : enemies) {
                if (!e.isMarkedForRemoval() && p.overlaps(e)) {
                    p.dealDamage(e);
                    impacted = true;
                    break;
                }
            }

            // check for splash damage if projectile has reached target position and has not already impacted an enemy
            if (!impacted && p.hasReachedTarget()) {
                impacted = true;
            }

            // if the projectile has impacted an enemy or reached its target position, apply splash damage if it has a SplashComponent and mark the projectile for removal
            if (!impacted) continue;

            p.getSplashComponent().ifPresent(sc ->
                    sc.apply(p.getPosition(), p.getDamage(), enemies));

            p.markForRemoval();
        }
    }

    /**
     * Removes entities that are marked for removal from the game.
     *
     * @return true if the player is defeated (e.g. health <= 0) after processing escaped enemies, false otherwise
     */
    private boolean removeMarkedEntities() {
        boolean isDefeated = false;

        for (EnemyEntity enemy : enemies) {
            if (enemy.isMarkedForRemoval()) {
                if (enemy.isDead()) {
                    // Enemy was killed
                    stats.onEnemyKilled(enemy.getScoreValue());
                    onEnemyKilledCallback.ifPresent(enemyEntityConsumer -> enemyEntityConsumer.accept(enemy));
                } else {
                    // Enemy escaped
                    stats.onEnemyEscaped(enemy.getDamage());
                    onEnemyEscapedCallback.ifPresent(enemyEntityConsumer -> enemyEntityConsumer.accept(enemy));
                    if (stats.isDefeated()) {
                        isDefeated = true;
                    }
                }
            }
        }

        enemies.removeIf(Entity::isMarkedForRemoval);
        projectiles.removeIf(Entity::isMarkedForRemoval);
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
