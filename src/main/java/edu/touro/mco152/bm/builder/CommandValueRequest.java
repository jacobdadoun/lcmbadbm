package edu.touro.mco152.bm.builder;

import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * Our command classes need to take in one parameter that will provide everything it needs to execute it's logic.
 * This CommandValueRequest class will be built by CommandValueBuilder to hold all the values needed. It will then be
 * passed into a Command class' constructor to be assigned to their respective logic variables
 */
public class CommandValueRequest {

    // Values are final because once we return this object as built, we do not want to allow changes to be made
    private final UserInterface userInterface;
    private final int numOfMarks;
    private final int numOfBlocks;
    private final int blockSizeKb;
    private final DiskRun.BlockSequence blockSequence;

    public CommandValueRequest(UserInterface userInterface, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence){
        this.userInterface = userInterface;
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
        this.blockSequence = blockSequence;
    }

    // Retrievers

    public UserInterface getUserInterface(){
        return this.userInterface;
    }

    public int getNumOfMarks() {
        return this.numOfMarks;
    }

    public int getNumOfBlocks() {
        return this.numOfBlocks;
    }

    public int getBlockSizeKb() {
        return this.blockSizeKb;
    }

    public DiskRun.BlockSequence getBlockSequence() {
        return this.blockSequence;
    }
}
