package Model.Units.Famers;

import Model.GameEngine;

public class FarmerHealthThread extends Thread {
    // Properties
    private final GameEngine gameEngine;

    private volatile boolean running = true;

    // Constructor
    public FarmerHealthThread(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    // Run the thread
    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(1500);
                gameEngine.decreaseFarmerHealth(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Stop the thread
    public void stopThread() {
        running = false;
    }

}
