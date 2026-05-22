package towerdefence.ui.j2d.assets;

import java.awt.image.BufferedImage;
import java.util.Random;

public class SpriteAnimation {
    private final BufferedImage[] frames;
    private final double frameDuration;
    private double timer = 0;
    private int currentFrame = 0;

    /**
     * @param sheet       de volledige sprite strip
     * @param frameCount  aantal frames naast elkaar
     * @param fps         frames per seconde voor de animatie
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
     * Update de animatie op basis van verstreken tijd.
     * @param deltaTime seconden sinds laatste update
     */
    public void update(double deltaTime) {
        timer += deltaTime;
        if (timer >= frameDuration) {
            timer -= frameDuration;
            currentFrame = (currentFrame + 1) % frames.length;
        }
    }

    /** Geeft het huidige frame terug om te tekenen. */
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }
}