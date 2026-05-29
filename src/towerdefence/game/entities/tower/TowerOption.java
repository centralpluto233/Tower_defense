package towerdefence.game.entities.tower;

/**
 * A record that holds the information about a tower type that is used to display the tower options in the shop and to create towers when the player selects an option.
 * @param type the type of tower, as a string. This should correspond to a key in the AbstractGameFactory that the shop system uses to create towers when the player selects an option.
 * @param cost the cost of the tower, as an integer. This is used to determine how much money the player needs to have in order to purchase the tower.
 * @param spritePath the path to the sprite image for the tower, as a string. This is used to display the tower option in the shop with the correct image.
 * @param gameWidth the width of the game window, as a double. This is used to scale the sprite image for the tower option in the shop so that it fits well within the shop interface.
 * @param gameHeight the height of the game window, as a double. This is used to scale the sprite image for the tower option in the shop so that it fits well within the shop interface.
 */
public record TowerOption(String type, int cost, String spritePath, double gameWidth, double gameHeight) {}
