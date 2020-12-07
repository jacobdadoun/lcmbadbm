package edu.touro.mco152.bm;

import edu.touro.mco152.bm.command.BMActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;

import static edu.touro.mco152.bm.App.msg;

/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Cooperates with Swing to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to do 'read' or 'write' disk benchmarks. It is instantiated in the
 * startBenchmark() method.
 * <p>
 * This class has one purpose, to provide instructions to run a benchmark. We instantiate this class with an object
 * of type SwingWorker and UserInterface (GUIBenchMark), which will be used in doBMLogic to call instruction to
 * GUIBenchMark::isBenchMarkCancelled, ::executeBenchMark, ::cancelBenchMark, ::publishToGUI and ::provideProgress.
 * Instead of SwingWorker being dependent on DiskWorker (a class with an entirely different responsibility),
 * DiskWorker is now dependent on SwingWorker via an instance type passed in to its constructor.
 */

public class DiskWorker {

    static private UserInterface userInterface;

    public DiskWorker(UserInterface userInterface) {
        DiskWorker.userInterface = userInterface;
    }

    public void executionDelegate() {
        userInterface.executeBenchMark();
    }

    public void cancelDelegate(Boolean bool) {
        userInterface.cancelBenchMark(bool);
    }

    static Boolean doBMLogic(){

        /**
         * We 'got here' because: a) End-user clicked 'Start' on the benchmark UI,
         * which triggered the start-benchmark event associated with the App::startBenchmark()
         * method.  b) startBenchmark() then instantiated a DiskWorker, and called
         * its (super class's) execute() method, causing Swing to eventually
         * call this doInBackground() method.
         */
        System.out.println("*** starting new worker thread");
        msg("Running readTest " + App.readTest + "   writeTest " + App.writeTest);
        msg("num files: " + App.numOfMarks + ", num blks: " + App.numOfBlocks
                + ", blk size (kb): " + App.blockSizeKb + ", blockSequence: " + App.blockSequence);




        Gui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }

        /**
         * The GUI allows either a write, read, or both types of BMs to be started. They are done serially.
         */
        
        BMActionCommandCenter bmWriteActionCommandCenter = new BMWriteActionCommandCenter();

        /**
         * Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
         * make it more 'fair'. For example a networking benchmark might close and re-open sockets,
         * a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */

        // try renaming all files to clear cache
        if (App.readTest && App.writeTest && !userInterface.isBenchMarkCancelled()) {
            JOptionPane.showMessageDialog(Gui.mainFrame,
                    "For valid READ measurements please clear the disk cache by\n" +
                            "using the included RAMMap.exe or flushmem.exe utilities.\n" +
                            "Removable drives can be disconnected and reconnected.\n" +
                            "For system drives use the WRITE and READ operations \n" +
                            "independantly by doing a cold reboot after the WRITE",
                    "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
        }

        // Same as above, just for Read operations instead of Writes.

        App.nextMarkNumber += App.numOfMarks;
        return true;
    }
}



