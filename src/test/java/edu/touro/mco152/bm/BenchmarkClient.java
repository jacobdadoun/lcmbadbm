package edu.touro.mco152.bm;

/**
 *  BenchmarkClient replicates our use for DiskWorker in the context of our test.
 *  The only difference is that the UserInterface object it inherits will make an execution call to either
 *  writeBMLogicNoSwing or readBMLogicNoSwing
 */

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMReadActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.persist.DiskRun;

public class BenchmarkClient {

    public static UserInterface userInterface;
    public static BMCommandCenter bmCommand;
    public static CommandExecutor commandExecutor;
    // final command parameter values defined:
    public static final int numOfMarks = 50;
    public static final int numOfBlocks = 64;
    public static final int blockSizeKb = 64;
    public static final DiskRun.BlockSequence blockSequence = DiskRun.BlockSequence.SEQUENTIAL;

    public static boolean writeTestComplete;
    public static boolean readTestComplete;

    /**
     * @param userInterface variable that our logic will use to communicate with NonSwingBenchmark
     */
    public BenchmarkClient(UserInterface userInterface){
        BenchmarkClient.userInterface = userInterface;
        writeTestComplete = false;
        readTestComplete = false;
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
            if(writeBMLogicNoSwing()){
                commandExecutor.notifyObserversDelegate();
            }
            writeTestComplete = true;
        }

        ExecutorTest executorTest = new ExecutorTest(commandExecutor);
        if(executorTest.test_ifExecuted()){
            executorTest.checkListForUpdateBooleans();
        }

        // try renaming all files to clear cache
        if (App.readTest && App.writeTest && !userInterface.isBenchMarkCancelled()) {
            userInterface.showMessagePopUp();
        }

        if(App.readTest){
            if(readBMLogicNoSwing()){
                commandExecutor.notifyObserversDelegate();
            }
            readTestComplete = true;
        }

        executorTest = new ExecutorTest(commandExecutor);
        if(executorTest.test_ifExecuted()){
            executorTest.checkListForUpdateBooleans();
        }

        return true;
    }

    /**
     * writeBMLogicNoSwing gets called by NonSwingBenchmark.execute()
     * @return boolean whether or not Command object was executed
     */
    private static boolean writeBMLogicNoSwing(){
        bmCommand = new BMWriteActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb,
                blockSequence);
        commandExecutor = new CommandExecutor(bmCommand);
        commandExecutor.registerObserverDelegate(new TestObserver());
        return commandExecutor.executeLogicDelegate();
    }

    /**
     * readBMLogicNoSwing gets called by NonSwingBenchmark.execute()
     * @return boolean whether or not Command object was executed
     */
    private static boolean readBMLogicNoSwing(){
        bmCommand = new BMReadActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb,
                blockSequence);
        commandExecutor = new CommandExecutor(bmCommand);
        commandExecutor.registerObserverDelegate(new TestObserver());
        return commandExecutor.executeLogicDelegate();
    }
}
