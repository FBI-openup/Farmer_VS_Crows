package Model.Units.Corns;

import Model.GameEngine;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/*
    * CornGenerationThread class
    * Thread to generate corns
*/
public class CornGenerationThread extends Thread {
    // Properties
    private final GameEngine gameEngine;
    private final boolean running = true;
    private final Timer timer;

    // Constructor
    public CornGenerationThread(GameEngine gameEngine) {
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
                if (running && gameEngine.getCorns().size() == 0) {
                    gameEngine.generateCorn();
                }
            }
        }, 10000, new Random().nextInt(10000) + 15000);
    }

}