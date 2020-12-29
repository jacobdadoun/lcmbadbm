package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BenchmarkClient {

    private static UserInterface userInterface;

    public static final int numOfMark = 50;
    public static final int numOfBlocks = 64;
    public static final int blockSizeKb = 64;
    public static final DiskRun.BlockSequence blockSequence = DiskRun.BlockSequence.SEQUENTIAL;

    public BenchmarkClient(UserInterface userInterface){
        BenchmarkClient.userInterface = userInterface;
    }

    public void executionDelegate() {
        userInterface.executeBenchMark();
    }

    public void cancelDelegate(Boolean bool) {
        userInterface.cancelBenchMark(bool);
    }

    @Test
    public static boolean writeBMLogicNoSwing(){
        CommandExecutor commandExecutor = new CommandExecutor(userInterface, "write");

        commandExecutor.setCustomBMParams(numOfMark, numOfBlocks, blockSizeKb, blockSequence);

        assertTrue(commandExecutor.executeBMCommandObject());

        return commandExecutor.executeBMCommandObject();
    }

    @Test
    public static boolean readBMLogicNoSwing(){
        CommandExecutor commandExecutor = new CommandExecutor(userInterface, "read");

        commandExecutor.setCustomBMParams(numOfMark, numOfBlocks, blockSizeKb, blockSequence);

        assertTrue(commandExecutor.executeBMCommandObject());

        return commandExecutor.executeBMCommandObject();
    }
}
