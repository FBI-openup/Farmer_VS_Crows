package Model.Units.Corns;

import Model.GameEngine;
import Model.Units.Units;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Corn class
 */
public class Corn extends Units {
    private final Color seedColor = new Color(0, 0, 0);
    private final Color startColor = new Color(0, 128, 0);
    private final Color midColor = new Color(255, 255, 0);
    private final Color endColor = new Color(139, 69, 19);
    private boolean isTaken = false;

    private Color currentColor;

    public boolean isTaken() {
        return isTaken;
    }

    public enum LifeCycle {
        SEED,
        GROWING,
        MATURE,
        WITHERED
    }

    private LifeCycle lifeCycle = LifeCycle.SEED;
    private final CornLifeCycleThread cornLifeCycleThread = new CornLifeCycleThread(this);
    private float progress = 0.0f;

    public Corn(Point position, GameEngine gameEngine) {
        super(position, gameEngine);
        cornLifeCycleThread.start();
        startGrowingColor();
    }

    private void startGrowingColor() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (progress <= 0.25F) {
                this.currentColor = this.interpolateColor(this.seedColor, this.startColor, progress * 4);
            } else if (progress < 0.75F) {
                this.currentColor = this.interpolateColor(this.startColor, this.midColor, (progress - 0.25F) * 2);
            } else if (progress < 1F) {
                this.currentColor = this.interpolateColor(this.midColor, this.endColor, (progress - 0.75F) * 4);
            } else {
                this.currentColor = this.endColor;
                scheduler.shutdown();
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    private Color interpolateColor(Color start, Color end, float fraction) {
        int r = (int) ((float) start.getRed() + (float) (end.getRed() - start.getRed()) * fraction);
        int g = (int) ((float) start.getGreen() + (float) (end.getGreen() - start.getGreen()) * fraction);
        int b = (int) ((float) start.getBlue() + (float) (end.getBlue() - start.getBlue()) * fraction);
        return new Color(r, g, b);
    }

    public Color getCurrentColor() {
        return this.currentColor;
    }


    public void setTaken(boolean taken) {
        this.isTaken = taken;
    }

    public LifeCycle getLifeCycle() {
        return lifeCycle;
    }

    public void setLifeCycle(LifeCycle lifeCycle) {
        this.lifeCycle = lifeCycle;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public CornLifeCycleThread getCornLifeCycleThread() {
        return cornLifeCycleThread;
    }
}