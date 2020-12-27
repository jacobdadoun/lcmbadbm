package edu.touro.mco152.bm;

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMReadActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DBPersistenceObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static edu.touro.mco152.bm.App.*;

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

public class DiskWorker extends BMSubject {

    private static UserInterface userInterface;

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

        /*
          We 'got here' because: a) End-user clicked 'Start' on the benchmark UI,
          which triggered the start-benchmark event associated with the App::startBenchmark()
          method.  b) startBenchmark() then instantiated a DiskWorker, and called
          its (super class's) execute() method, causing Swing to eventually
          call this doInBackground() method.
         */

        Gui.updateLegend();  // init chart legend info

        if (App.autoReset) {
            App.resetTestData();
            Gui.resetTestData();
        }

        // implement bmCommandCenter that will be assigned the respective Command Classes (i.e - write and read)
        BMCommandCenter bmCommand;
        // implement DiskRun type to be assigned the diskrun from read and write respectively. It will then be passed to
        // our GUI and DB Observers
        DiskRun diskRunTemp;

        /*
          The GUI allows either a write, read, or both types of BMs to be started. They are done serially.
         */

        // Execute, Register and Notify
        if(App.writeTest) {
            // Instantiation of bmCommand with
            bmCommand = new BMWriteActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
            if(bmCommand.doBMCommand()){
                diskRunTemp = bmCommand.getDiskRun();
                //Persist info about the Write BM Run (e.g. into Derby Database)
                DiskWorker.registerObserver(new DBPersistenceObserver(diskRunTemp));

                Gui gui = new Gui(diskRunTemp);
                DiskWorker.registerObserver(gui);
            }

            notifyObservers();
        }

        /*
          Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
          make it more 'fair'. For example a networking benchmark might close and re-open sockets,
          a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
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

        // Execute and Register. Then instantiate for slack and send a message when read is complete and Notify.
        if (App.readTest) {
            bmCommand = new BMReadActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
            if(bmCommand.doBMCommand()){
                diskRunTemp = bmCommand.getDiskRun();
                //Persist info about the Write BM Run (e.g. into Derby Database)
                DiskWorker.registerObserver(new DBPersistenceObserver(diskRunTemp));

                Gui gui = new Gui(diskRunTemp);
                DiskWorker.registerObserver(gui);
            }
            SlackManager slackManager = new SlackManager("BadBM");
            slackManager.setMessage(":smile: Benchmark completed");
            DiskWorker.registerObserver(slackManager);
            notifyObservers();
        }

        App.nextMarkNumber += App.numOfMarks;
        return true;
    }
}
