package edu.touro.mco152.bm.executor;

import edu.touro.mco152.bm.command.BMCommandCenter;

public abstract class AbstractExecutor {

    private final BMCommandCenter bmCommandCenter;
    // boolean flag checker if checked
    public boolean wasExecuted;

    public AbstractExecutor(BMCommandCenter bmCommandCenter){
        this.bmCommandCenter = bmCommandCenter;
        wasExecuted = false;
    }

    /**
     * The client who instantiates this object should only know how to call execute with elegance,
     * this method satisfies the requirement and is designed to be called as a condition to an if-statement.
     * Essentially, this is a method that calls the executor of our (command) generic object.
     *
     * @return A boolean to the (recommended) to the client object.
     */
    public boolean execute(){
        if(bmCommandCenter.execute()){
            wasExecuted = true;
            return true;
        }
        return false;
    }

}
