package edu.touro.mco152.bm;

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

    public static Boolean doBMLogic(){

        CommandExecutor commandExecutor;

        /*
          The GUI allows either a write, read, or both types of BMs to be started. They are done serially. Only now,
          we call the GUI from the command classes in order to DiskWorker independent of Gui
         */

        // Execute, Register and Notify
        if(App.writeTest) {
            commandExecutor = new CommandExecutor(userInterface, "write");
            if(commandExecutor.executeBMCommandObject()){
                commandExecutor.notifyCommandObservers();
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

        // Execute and Register. Then instantiate for slack and send a message when read is complete and Notify.
        if (App.readTest) {
            commandExecutor = new CommandExecutor(userInterface, "write");
            if(commandExecutor.executeBMCommandObject()){
                commandExecutor.notifyCommandObservers();
            }
        }

        App.nextMarkNumber += App.numOfMarks;
        return true;
    }
}
