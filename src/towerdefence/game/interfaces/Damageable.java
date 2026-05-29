package towerdefence.game.interfaces;

/**
 * Interface for objects that can take damage and be killed.
 * Implementations should keep the health between 0 and maxhealth
 */
public interface Damageable {
    /**
     * Reduces the health of the object by the specified amount.
     *
     * @param damage the amount of damage to apply
     */
    void takeDamage(int damage);

    /**
     * Checks if the object is dead.
     *
     * @return true if the object is dead, false otherwise
     */
    boolean isDead();

    /**
     * Gets the current health of the object.
     *
     * @return the current health
     */
    int getHealth();

    /**
     * Gets the maximum health of the object.
     *
     * @return the maximum health
     */
    int getMaxHealth();
}
