package towerdefence.game.interfaces;

/**
 * Interface for entities that can attack other entities.
 */
public interface Attacker {
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
}
