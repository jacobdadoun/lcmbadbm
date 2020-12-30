package edu.touro.mco152.bm;

/**
 * BMObserver is an interface that will be implemented by tasks who play an Observer roll to be extecuted in DiskWorker.
 * The update() method will define the task to be executed
 */

public interface BMObserver {

    void update();

    Boolean isUpdated();
}
