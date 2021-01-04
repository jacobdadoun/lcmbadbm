package edu.touro.mco152.bm;

import edu.touro.mco152.bm.executor.AbstractExecutor;
import edu.touro.mco152.bm.executor.ReadExecutor;
import edu.touro.mco152.bm.executor.WriteExecutor;
import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMReadActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;

/**
 * Run the disk benchmarking as a Swing-compliant thread (only one of these threads can run at
 * once.) Cooperates with UserInterface to provide and make use of interim and final progress and
 * information, which is also recorded as needed to the the observers, persistence store, and log.
 * <p>
 * Depends on static values that describe the benchmark to be done having been set in App and Gui classes.
 * The DiskRun class is used to keep track of and persist info about each benchmark at a higher level (a run),
 * while the DiskMark class described each iteration's result, which is displayed by the UI as the benchmark run
 * progresses.
 * <p>
 * This class only knows how to execute 'read' or 'write' disk benchmarks via the CommandExecutor class. It is
 * instantiated in the startBenchmark() method.
 * <p>
 * Instead of SwingWorker being dependent on DiskWorker (a class with an entirely different responsibility),
 * DiskWorker is now dependent on SwingWorker via a UserInterface type passed in to this classes constructor.
 */

public class DiskWorker {

    private static UserInterface userInterface;

    public DiskWorker(UserInterface userInterface) {
        DiskWorker.userInterface = userInterface;
    }

    /**
     * Called from App.startBenchmark(), this delegate-execution method will make a call to the
     * userInterface execute method.
     */
    public void executionDelegate() {
        userInterface.executeBenchMark();
    }

    /**
     * Called from App.cancelBenchmark(), this delegate-execution canceller will make a call to the
     * userInterface cancel method.
     */
    public void cancelDelegate(Boolean bool) {
        userInterface.cancelBenchMark(bool);
    }

    /**
     * Called from UserInterface's doInBackground (using SwingWorker) or executeBenchMark (without using SwingWorker)
     * method. This static method defines the master-plan of running benchmark logic. It puts an Executor to use by
     * giving it an instance like BMWriteActionCommandCenter and BMReadActionCommandCenter.
     *
     * @return a boolean to doInBackground (when using SwingWorker) or to executeBenchMark (when not using SwingWorker).
     */
    public static Boolean doBMLogic(){

        // bmCommand will be interchangeable with BMWriteActionCommandCenter and BMReadActionCommandCenter
        BMCommandCenter bmCommand;
        AbstractExecutor commandExecutor;

        /*
          The GUI allows either a write, read, or both types of BMs to be started. They are done serially. Only now,
          we call the GUI from the command objects.
         */

        // Execute, Register and Notify
        if(App.writeTest) {
            bmCommand = new BMWriteActionCommandCenter(userInterface, App.numOfMarks, App.numOfBlocks, App.blockSizeKb,
                    App.blockSequence);

            commandExecutor = new WriteExecutor(bmCommand);
            if(commandExecutor.execute()){
                bmCommand.notifyObservers();
            }
        }

        /*
          Most benchmarking systems will try to do some cleanup in between 2 benchmark operations to
          make it more 'fair'. For example a networking benchmark might close and re-open sockets,
          a memory benchmark might clear or invalidate the Op Systems TLB or other caches, etc.
         */
        // try renaming all files to clear cache
        if (App.readTest && App.writeTest && !userInterface.isBenchMarkCancelled()) {
            userInterface.showMessagePopUp();
        }

        // Execute and Register. Then instantiate for slack and provide a message when read is complete and Notifies.
        if (App.readTest) {
            bmCommand = new BMReadActionCommandCenter(userInterface, App.numOfMarks, App.numOfBlocks, App.blockSizeKb,
                    App.blockSequence);

            commandExecutor = new ReadExecutor(bmCommand);
            if(commandExecutor.execute()){
                bmCommand.notifyObservers();
            }
        }

        App.nextMarkNumber += App.numOfMarks;
        return true;
    }
}
