package edu.touro.mco152.bm.client;

/**
 *  BenchmarkClient replicates our use for DiskWorker in the context of our test.
 *  The only difference is that the UserInterface object it inherits will make an execution call to either
 *  writeBMLogicNoSwing or readBMLogicNoSwing
 */

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.executor.AbstractExecutor;
import edu.touro.mco152.bm.executor.TestExecutor;
import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMReadActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.persist.DiskRun;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkClient {

    public static UserInterface userInterface;
    public static BMCommandCenter bmCommand;
    public static AbstractExecutor commandExecutor;

    // final command parameter values defined:
    public static final int numOfMarks = 50;
    public static final int numOfBlocks = 64;
    public static final int blockSizeKb = 64;
    public static final DiskRun.BlockSequence blockSequence = DiskRun.BlockSequence.SEQUENTIAL;

    /**
     * @param userInterface variable that our logic will use to communicate with NonSwingBenchmark
     */
    public BenchmarkClient(UserInterface userInterface){
        BenchmarkClient.userInterface = userInterface;
    }

    /**
     * Delegates execution to NonSwingBenchmark.execute()
     */
    public void executionDelegate() {
        userInterface.executeBenchMark();
    }

    public void cancelDelegate(Boolean bool) {
        userInterface.cancelBenchMark(bool);
    }

    public static boolean doBMLogic_NoSwing(){

        if(App.writeTest){
            bmCommand = new BMWriteActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);

            commandExecutor = new TestExecutor(bmCommand);
            if(commandExecutor.execute()){
                bmCommand.notifyObservers();
            }
        }

        // try renaming all files to clear cache
        if (App.readTest && App.writeTest && !userInterface.isBenchMarkCancelled()) {
            userInterface.showMessagePopUp();
        }

        if(App.readTest){
            bmCommand = new BMReadActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);

            commandExecutor = new TestExecutor(bmCommand);
            if(commandExecutor.execute()){
                bmCommand.notifyObservers();
            }
        }
        return true;
    }

}
