package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.BMObserver;
import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.persist.DiskRun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BMCommandCenter is an interface that our read/write benchmark will implement.
 * Diskworker instantiates a command object and calls it's execute method
 */
public abstract class BMCommandCenter {

    // bmObservers - A synchronized ArrayList that will store any object of type BMObserver
    private final List<BMObserver> bmObserverRegistry = Collections.synchronizedList(new ArrayList<>());

    protected UserInterface userInterface;
    protected int numOfMarks, numOfBlocks, blockSizeKb;
    protected DiskRun.BlockSequence blockSequence;
    protected DiskRun run;
    public String message = "";

    public BMCommandCenter(UserInterface userInterface, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence){
        this.userInterface = userInterface;
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
        this.blockSequence = blockSequence;
        run = new DiskRun(DiskRun.IOMode.WRITE, this.blockSequence);
    }

    public abstract boolean execute();
    public abstract void undoBMCommand();
    public abstract DiskRun getDiskRun();

    /**
     * Adds a BMObserver object to the array
     * @param bmObserver is what we pass in to be added for execution
     */
    public void registerObserver(BMObserver bmObserver){
        bmObserverRegistry.add(bmObserver);
    }

    /**
     * Removes a BMObserver object from the array
     * @param bmObserver is what we pass in to remove from execution process
     * ( i.e. the object we don't want to be notified )
     */
    public void unRegisterObserver(BMObserver bmObserver){
        bmObserverRegistry.remove(bmObserver);
    }

    /**
     * Iterates through the list of Observers who want to be notified, and one
     * by one notifies each of them by calling their update()
     */
    public void notifyObservers(){
        // Notify BMObservers by their .updateMethods
        for (BMObserver bmObserverTemp: bmObserverRegistry) {
            bmObserverTemp.update();
        }
    }
}
