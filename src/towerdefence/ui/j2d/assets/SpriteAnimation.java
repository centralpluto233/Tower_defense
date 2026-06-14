package towerdefence.ui.j2d.assets;

import java.awt.image.BufferedImage;
import java.util.Random;

public class SpriteAnimation {
    private final BufferedImage[] frames;
    private final double frameDuration;
    private double timer = 0;
    private int currentFrame;

    /**
     * creates a sprite-animation from a sprite sheet
     * @param sheet the sprite sheet containing the animation frames, arranged horizontally. The width of the sheet should be divisible by the number of frames.
     * @param frameCount the number of frames in the animation, which determines how the sprite sheet is divided into individual frames. The frames are extracted from the sheet in order, starting from the leftmost frame.
     * @param fps the frames per second for the animation, higher fps -> faster animation
     */
    public SpriteAnimation(BufferedImage sheet, int frameCount, double fps) {
        this.frameDuration = 1.0 / fps;
        this.frames = new BufferedImage[frameCount];

        int frameWidth = sheet.getWidth() / frameCount;
        int frameHeight = sheet.getHeight();

        for (int i = 0; i < frameCount; i++) {
            frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }
        Random rand = new Random();
        currentFrame = rand.nextInt(frameCount);
    }

    /**
     * updates the animation based on the elapsed time since last update
     * @param deltaTime the time elapsed since the last update, in seconds
     */
    public void update(double deltaTime) {
        timer += deltaTime;
        if (timer >= frameDuration) {
            timer -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }

    /**
     * gets the current frame of the animation as a BufferedImage. This method should be called after update() to get the correct frame based on the elapsed time.
     * @return the current frame of the animation as a BufferedImage
     */
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }
}