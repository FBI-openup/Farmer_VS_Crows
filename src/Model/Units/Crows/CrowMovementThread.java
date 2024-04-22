package Model.Units.Crows;

import Model.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    This class is responsible for updating the crow positions in the game engine.
    It uses a thread pool to update the positions of the crows concurrently.
*/
public class CrowMovementThread extends Thread {
    private final GameEngine gameEngine;

    private volatile boolean running = true;

    public CrowMovementThread(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            // UPDATE : update the crow positions
            moveCrows();
            try {
                Thread.sleep(25);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopThread() {
        running = false;
        executorService.shutdown();
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(5); // 10 threads in the pool

    // UPDATE : update the crow positions
    private void moveCrows() {
        List<Crow> crows = gameEngine.getCrows();
        for (Crow crow : crows) {
            executorService.submit(() -> {
                try {
                    crow.move();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
