package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.List;

import static edu.touro.mco152.bm.App.*;

/**
 * SwingWorker<T,V>
 * T - the result type returned by this SwingWorker's doInBackground and get methods
 * V - the type used for carrying out intermediate results by this SwingWorker's publish and process methods
 */
public class GUIBenchmark extends SwingWorker<Boolean, DiskMark> implements UserInterface {

    /**
     * registerPropertyChangeListener calls to addPropertyChangeListener from SwingWorker with:
     * @param listener - A java.beans.PropertyChangeListener type.
     */
    @Override
    public void registerPropertyChangeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(listener);
    }

    /**
     * Calls SwingWorker's execute method, which will have multiple WorkerThreads executing what's
     * defined in ::doInBackground
     */
    @Override
    public void executeBenchMark() {
        execute();
    }

    /**
     * isBenchMarkCancelled is called in doBMLogic to determine whether it should proceed through it's if statements.
     * @return Returns a boolean to doBMLogic.
     */
    @Override
    public boolean isBenchMarkCancelled() {
        return isCancelled();
    }

    /**
     * Called in doBMLogic to set progress with a given integer
     * @param x - integer passed to SwingWorker::setProgress
     */
    @Override
    public void provideProgress(int x) {
        setProgress(x);
    }

    /**
     * called in doBMLogic to pass a data for SwingWorker to run
     * @param diskMark - a chunk of data to produce a new AccumulativeRunnable for execution.
     */
    @Override
    public void publishToGUI(DiskMark diskMark) {
        publish(diskMark);
    }


    /**
     * Called by DiskWorker::cancelDelegate to cancel the benchmark from being executed in future run
     * (see SwingWorker::cancel)
     * @param bool sets if future should cancel
     * @return - a boolean from future.cancel(mayInterruptIfRunning);
     */
    @Override
    public boolean cancelBenchMark(boolean bool) {
        return cancel(bool);
    }

    @Override
    public void showMessagePopUp() {
        JOptionPane.showMessageDialog(Gui.mainFrame,
                "For valid READ measurements please clear the disk cache by\n" +
                        "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                        "Removable drives can be disconnected and reconnected.\n" +
                        "For system drives use the WRITE and READ operations \n" +
                        "independantly by doing a cold reboot after the WRITE",
                "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
    }


    /**
     * Called by WorkerThreads from execute. What's defined here will be executed by the threads.
     * @return a boolean to it's caller from SwingWorker
     */
    @Override
    public Boolean doInBackground(){
        /*
          We 'got here' because: a) End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the App::startBenchmark()
          method.  b) startBenchmark() then instantiated a DiskWorker, and passed it an instance of this class.
          Then, App.java called DiskWorker.executionDelegate which will call executeBenchmark in this class.
         */
        Gui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }

        return DiskWorker.doBMLogic();
    }

    /**
     * Processes the data to be posted to the GUI
     * @param markList - list of data to write to GUI
     */
    @Override
    public void process(List<DiskMark> markList) {
        // We are passed a list of one or more DiskMark objects that our thread has previously
        // published to Swing. Watch Professor Cohen's video - Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }


    /**
     * Called by Swing. When finished executing, will remove data from screen, sets the state of the application to IDLE.
     */
    @Override
    protected void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
        System.err.println("---FINISHED BENCHMARK---") ;

    }
}