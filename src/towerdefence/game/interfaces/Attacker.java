package towerdefence.game.interfaces;

/**
 * Interface for entities that can attack other entities.
 */
public interface Attacker {
    /**
     * Attacks the specified target.
     * @param target the target to attack
     */
    void attack(Damageable target);

    /**
     * Gets the damage dealt by the attacker.
     * @return the damage amount
     */
    int getDamage();

    /**
     * Gets the range of the attacker.
     * @return the range
     */
    double getRange();

    /**
     * Checks if the attacker can attack.
     * @return true if the attacker can attack, false otherwise
     */
    boolean canAttack();
}
