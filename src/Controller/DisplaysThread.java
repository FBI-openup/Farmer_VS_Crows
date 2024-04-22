package Controller;

import View.GameInterface;

public class DisplaysThread extends Thread {
    private final GameInterface gameInterface;
    public DisplaysThread(GameInterface gameInterface) {
        this.gameInterface = gameInterface;
    }
    private final boolean running = true;
    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                gameInterface.updateDisplays();
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
