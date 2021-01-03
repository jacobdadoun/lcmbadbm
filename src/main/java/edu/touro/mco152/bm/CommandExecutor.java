package edu.touro.mco152.bm;
/**
 * An object that acts as a broker between buyer (Client) and seller (BMCommandObject).
 *
 * We need a way to execute generic commands and register observers without compromise.
 * Our new CommandExecutor class will allow us to run any kind of command object we
 * please, provided we have that object extend BMCommandObject
 */


import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DBPersistenceObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import java.util.ArrayList;

public class CommandExecutor {

    private final BMCommandCenter bmCommandCenter;
    private SlackManager slackManager;

    // boolean flag checker if shes
    public boolean wasExecuted;

    // Implement bmCommandCenter that will be assigned the respective Command Classes (i.e - write and read)
    // NOTE: In the real world, the BMCommandCenter object ref will be assigned via set method.
    public CommandExecutor(BMCommandCenter bmCommandCenter){
        this.bmCommandCenter = bmCommandCenter;
        wasExecuted = false;
    }

    /**
     * The client who instantiates this object should only know how to call execute with elegance,
     * this method satisfies the requirement and is designed to be called as a condition to an if-statement.
     * Essentially, this is a delegate method that calls the executor of our (command) generic object, after
     * it will do any preliminary tasks in case it needs to prepare an observer with a benchmark status,
     * (Like we do here for slackManager).
     *
     * @return A boolean to the (recommended) to the client object.
     */
    public boolean executeLogicDelegate(){
        if(bmCommandCenter.execute()){
            // check flag set to true - used in tests
            wasExecuted = true;
            // Here, we just want to check if the value of message was set to anything. If it was, then we
            // have some preliminary work to do before whatever class that instantiates this object has to call notify.
            if( !bmCommandCenter.message.isEmpty() ){
                // All we're doing here, is sending whatever this message may be to be stored in slackManager.
                // Cool! and it works? nope.
                slackManager.setMessage(bmCommandCenter.message);
            }
            return true;
        }
        return false;
    }

    /**
     * It is recommended that the client calls this method in the body of the if-statement executor.
     * This is a delegate method used by the client to call notify int the observable command class.
     */
    public void notifyObserversDelegate(){
        bmCommandCenter.notifyObservers();
    }

    /**
     * The client must calls this method before calling execute. Whatever observer object we pass in
     * will be called when the client reaches notifyObserversDelegate. But remember: nothing will be
     * notified if you do not call this BEFORE executeLogicDelegate.
     */
    public void registerObserverDelegate(BMObserver observer){
        bmCommandCenter.registerObserver(observer);
    }

    // Default registration

    /**
     * Default registration for the write logic portion. This is only here to help tidy up dependencies
     * from the client. Makes use of
     */
    public void defaultWriteRegistration(DiskRun run){
        bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
        bmCommandCenter.registerObserver(new Gui(run));
    }

    /**
     * Default registration for the read logic portion. This is only here to help tidy up dependencies
     * from the client.
     */
    public void defaultReadRegistration(DiskRun run){
        bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
        bmCommandCenter.registerObserver(new Gui(run));
        // Put if here for Max speed > 3% of avg speed
        if(run.getRunMax() > (run.getRunAvg() * 0.03)){
            slackManager = new SlackManager("BadBM", run);
            bmCommandCenter.registerObserver(slackManager);
        }
    }

    /**
     * Client: Hey man, sometimes I might need a list of your observers. (customer)
     * CommandExecutor: Okay. Hey, BMCommandObject. I want a list of observers (retailer)
     * BMCommandObject: Yeah? Ok here you go (wholesaler sells wholesale)
     * CommandExecutor: Hey, Client. I have those observers you wanted (retailer sells retail - gives observers)
     *
     * @return An object of the List interface. In this case an ArrayList.
     */
    public ArrayList<BMObserver> getObserverDelegate(){
        return new ArrayList<>(bmCommandCenter.getBmObserverRegistry());
    }

}
