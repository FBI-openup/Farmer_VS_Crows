package Model.Units.Crows;

import Model.GameEngine;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
    * CrowGenerationThread class
*/
public class CrowGenerationThread extends Thread {
    // Properties
    private final GameEngine gameEngine;
    private volatile boolean running = true;
    private final Timer timer;

    // Constructor
    public CrowGenerationThread(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        this.timer = new Timer();
    }

    // Run the thread
    @Override
    public void run() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Generate a crow every 15-25 seconds if there are less than 3 crows and more than 0 corns
                if (running && gameEngine.getCrows().size() < 3 && gameEngine.getCorns().size() > 0) {
                    gameEngine.generateCrow();
                }
            }
        }, 5000, new Random().nextInt(10000) + 15000);
    }

    // Stop the thread
    public void stopThread() {
        running = false;
        timer.cancel();
    }
}