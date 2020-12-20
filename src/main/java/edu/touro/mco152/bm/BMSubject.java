package edu.touro.mco152.bm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BMSubject {

    // bmObservers - A synchronized ArrayList that will store any object of type BMObserver
    private static List<BMObserver> bmObserverRegistry = Collections.synchronizedList(new ArrayList<>());

    /**
     * Adds a BMObserver object to the array
     * @param bmObserver is what we pass in to be added for execution
     */
    public static void registerObserver(BMObserver bmObserver){
        bmObserverRegistry.add(bmObserver);
    }

    /**
     * Removes a BMObserver object from the array
     * @param bmObserver is what we pass in to remove from execution process
     * ( i.e. the object we don't want to be notified )
     */
    public static void unRegisterObserver(BMObserver bmObserver){
        bmObserverRegistry.remove(bmObserver);
    }

    /**
     * Iterates through the list of Observers who want to be notified, and one
     * by one notifies each of them by calling their update()
     */
    public static void notifyObservers(){
        // Notify BMObservers by their .updateMethods
        for (BMObserver bmObserverTemp: bmObserverRegistry) {
            bmObserverTemp.update();
        }
        unRegisterAllObservers();
    }

    public static void unRegisterAllObservers(){
        bmObserverRegistry = Collections.synchronizedList(new ArrayList<>());
    }

}
