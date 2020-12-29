package edu.touro.mco152.bm;

/**
 *  BenchmarkClient replicates our use for DiskWorker in the context of our test.
 *  The only difference is that the UserInterface object it inherits will make an execution call to either
 *  writeBMLogicNoSwing or readBMLogicNoSwing
 */

import edu.touro.mco152.bm.persist.DiskRun;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkClient {

    // Arrange
    private static UserInterface userInterface;
    // final command parameter values defined:
    public static final int numOfMark = 50;
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

    /**
     * writeBMLogicNoSwing gets called by NonSwingBenchmark.execute()
     * @return boolean whether or not Command object was executed
     */
    @Test
    public static boolean writeBMLogicNoSwing(){
        // Act
        CommandExecutor commandExecutor = new CommandExecutor(userInterface, "write");
        commandExecutor.setCustomBMParams(numOfMark, numOfBlocks, blockSizeKb, blockSequence);
        // Assert
        assertTrue(commandExecutor.executeBMCommandObject());
        return commandExecutor.executeBMCommandObject();
    }

    /**
     * radBMLogicNoSwing gets called by NonSwingBenchmark.execute()
     * @return boolean whether or not Command object was executed
     */
    @Test
    public static boolean readBMLogicNoSwing(){
        // Act
        CommandExecutor commandExecutor = new CommandExecutor(userInterface, "read");
        commandExecutor.setCustomBMParams(numOfMark, numOfBlocks, blockSizeKb, blockSequence);
        // Assert
        assertTrue(commandExecutor.executeBMCommandObject());
        return commandExecutor.executeBMCommandObject();
    }
}
