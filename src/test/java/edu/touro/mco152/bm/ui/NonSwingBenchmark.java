package edu.touro.mco152.bm.ui;

import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.client.BenchmarkClient;

import java.beans.PropertyChangeListener;

/**
 * NonSwingBenchmark replicates our use of GUIBenchmark without extending SwingWorker. Although the names of the
 * overridden methods are in a gui context, they were defined here to be dependant on resources available to their
 * own class.
 */
public class NonSwingBenchmark implements UserInterface {
    // Boolean variable to determine whether the benchmark is cancelled or not.
    public boolean progressBool = true;
    // Integer variable in case we need to set isCancelled to false and carry on with logic execution.
    public int currentProgress = 0;

    public boolean bmFinished = false;

    @Override
    public void registerPropertyChangeListener(PropertyChangeListener listener) {
        // Leave empty (no need to register listeners in a non-swing implementation)
    }

    /**
     * BenchMarkClient's logic execution is determined by the string passed into the constructor
     */
    @Override
    public void executeBenchMark() {
        if(BenchmarkClient.doBMLogic_NoSwing()){
            bmFinished = true;
            System.out.println(" FINISHED EXECUTING BM");
        }
    }

    public Boolean getBMStatus(){
        return bmFinished;
    }

    /**
     * @return a boolean whether or not the Benchmark is cancelled
     */
    @Override
    public boolean isBenchMarkCancelled() {
        return progressBool;
    }

    /**
     * Takes a value x to determine if the logic should continue reading or writing. Assigns false to stop logic when
     * isBenchMarkCancelled() is called.
     * @param x integer value on progress out of completion. x == 100 will assign false.
     */
    @Override
    public void provideProgress(int x) {
        if(x == 100){
            progressBool = false;
        }
        currentProgress = x;
    }

    @Override
    public void publishToGUI(DiskMark diskMark) {
        // Leave empty (no need to publish to gui in a non-swing implementation)
    }

    /**
     *
     * @param bool to determine whether to stop or continue execution
     * @return a boolean based on its new course of action. true - yes, benchmark is cancelled. false - okay, we'll
     * continue where we left off.
     */
    @Override
    public boolean cancelBenchMark(boolean bool) {
        if(bool){
            provideProgress(100);
            return true;
        }
        provideProgress(currentProgress);
        return false;
    }


    /**
     * Same text used in the GUI pop-up, now printed to the console.
     */
    @Override
    public void showMessagePopUp() {
        System.out.println("-- For valid READ measurements please clear the disk cache by\n" +
                "-- using the included RAMMap.exe or flushmem.exe utilities.\n" +
                "-- Removable drives can be disconnected and reconnected.\n" +
                "-- For system drives use the WRITE and READ operations \n" +
                "-- independantly by doing a cold reboot after the WRITE");
    }

}
