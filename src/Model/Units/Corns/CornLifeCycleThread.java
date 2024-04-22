package Model.Units.Corns;

public class CornLifeCycleThread extends Thread {
    private final Corn corn;
    private final float borderGrowing = 0.25f;
    private final float borderMature = 0.75f;
    private final float borderWithered = 1.0f;
    private volatile boolean running = true;

    public CornLifeCycleThread(Corn corn) {
        this.corn = corn;
    }

    @Override
    public void run() {
        while (running && !corn.isTaken() && !Thread.currentThread().isInterrupted()) {
            try {
                // Update the progress of the corn's life cycle
                corn.setProgress(corn.getProgress() + 0.01f); // Increase progress by 10% every 10 second
                // Update the life cycle state based on the progress
                if (corn.getProgress() > borderGrowing && corn.getProgress() <= borderMature) {
                    corn.setLifeCycle(Corn.LifeCycle.GROWING);
                } else if (corn.getProgress() > borderMature && corn.getProgress() <= borderWithered) {
                    corn.setLifeCycle(Corn.LifeCycle.MATURE);
                } else if (corn.getProgress() > borderWithered) {
                    corn.setLifeCycle(Corn.LifeCycle.WITHERED);
                }

                // Sleep for 1.5 seconds
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stopThread() {
        running = false;
        //this.interrupt();
    }
}