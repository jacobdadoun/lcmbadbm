package edu.touro.mco152.bm.builder;

import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.persist.DiskRun;

/**
 * We want the possibility to implement as many parameters as we may need without having to
 * specify potential unnecessary parameters. With the builder pattern, we can BUILD an object
 * that will store our values. Then, it'll return that object so we can pass it to whichever
 * class needs them to execute.
 */
public class CommandValueBuilder implements IBuilder {

    private final UserInterface userInterface;
    // default set to 0 so we can test for that outcome
    private int numOfMarks = 0;
    private int numOfBlocks = 0;
    private int blockSizeKb = 0;
    private DiskRun.BlockSequence blockSequence;

    // With userInterface in the constructor, we're saying that at the very least,
    // we must have a UserInterface to communicate with.
    public CommandValueBuilder(UserInterface userInterface){
        this.userInterface = userInterface;
    }

    // The following methods "set" the values and return THIS object with the new value passed in.

    /**
     * "sets" and returns this CommandValueBuilder object with the specified Number of Marks.
     * @param numOfMarks number of marks we want
     * @return THIS CommandValueBuilder with numOfMarks
     */
    public CommandValueBuilder numOfMarks(int numOfMarks){
        this.numOfMarks = numOfMarks;
        return this;
    }

    /**
     * "sets" and returns this CommandValueBuilder object with the specified Number of Blocks.
     * @param numOfBlocks number of blocks we want
     * @return THIS CommandValueBuilder with numOfBlocks
     */
    public CommandValueBuilder numOfBlocks(int numOfBlocks){
        this.numOfBlocks = numOfBlocks;
        return this;
    }

    /**
     * "sets" and returns this CommandValueBuilder object with the specified Number of Kbs.
     * @param blockSizeKb number of Kbs we want
     * @return THIS CommandValueBuilder with Kbs
     */
    public CommandValueBuilder blockSizeKb(int blockSizeKb){
        this.blockSizeKb = blockSizeKb;
        return this;
    }

    /**
     * "sets" and returns this CommandValueBuilder object with the specified Sequence type.
     * @param blockSequence type of sequence we want
     * @return THIS CommandValueBuilder with the Sequence set
     */
    public CommandValueBuilder blockSequence(DiskRun.BlockSequence blockSequence){
        this.blockSequence = blockSequence;
        return this;
    }

    /**
     * Makes sure that we dont need to set the Request's values in order every time. When we call this method
     * we always get...
     * @return a class with values for our BM command's, ready to go.
     */
    @Override
    public CommandValueRequest getRequest(){
        return new CommandValueRequest(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
    }
}
