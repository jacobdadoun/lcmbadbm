package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.BMObserver;
import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.builder.CommandValueRequest;
import edu.touro.mco152.bm.persist.DiskRun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * BMCommandCenter is an abstract class that is capable of reading or writing benchmarks.
 * However, it must be extended and have its abstract methods defined. This Command object must be
 * instantiated with the essentials: A UserInterface and a couple of integers that are vital for a
 * successful benchmark execution. This class has its capabilities with Read and Write classes.
 */
public abstract class BMCommandCenter {

    // bmObservers - A synchronized ArrayList that will store any object of type BMObserver
    private final List<BMObserver> bmObserverRegistry = Collections.synchronizedList(new ArrayList<>());

    protected UserInterface userInterface;
    protected int numOfMarks, numOfBlocks, blockSizeKb;
    protected DiskRun.BlockSequence blockSequence;
    protected DiskRun run;

    // variable assigned a message for preliminary status update.
    public String message = "";

    public BMCommandCenter(CommandValueRequest commandValueRequest){
        this.userInterface = commandValueRequest.getUserInterface();
        this.numOfMarks = commandValueRequest.getNumOfMarks();
        this.numOfBlocks = commandValueRequest.getNumOfBlocks();
        this.blockSizeKb = commandValueRequest.getBlockSizeKb();
        this.blockSequence = commandValueRequest.getBlockSequence();
    }

    // Abstracts must be defined in child class
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
        // Notify BMObservers by their .update method
        for (BMObserver bmObserverTemp: bmObserverRegistry) {
            bmObserverTemp.update();
        }
    }

    /**
     * Client: Hey man, I need a list of your observers. (A buyer)
     * BMCommandObject: Here you go. (gives observers)
     *
     * @return An object of the List interface. In this case an ArrayList.
     */
    public List<BMObserver> getBmObserverRegistry(){
        return bmObserverRegistry;
    }
}
