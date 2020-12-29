package edu.touro.mco152.bm;

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DBPersistenceObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

public class CommandExecutor {

    private final BMCommandCenter bmCommandCenter;

    private final DiskRun run;

    private SlackManager slackManager;

    // Implement bmCommandCenter that will be assigned the respective Command Classes (i.e - write and read)
    // NOTE: In the real world, the BMCommandCenter object ref will be assigned via set method.
    public CommandExecutor(BMCommandCenter bmCommandCenter){
        this.bmCommandCenter = bmCommandCenter;
        run = bmCommandCenter.getDiskRun();
    }

    public boolean executeLogicDelegate(){
        if(bmCommandCenter.execute()){
            if( !bmCommandCenter.message.isEmpty() ){
                slackManager.setMessage(bmCommandCenter.message);
            }
            return true;
        }
        return false;
    }

    public void notifyObserversDelegate(){
        bmCommandCenter.notifyObservers();
    }

    public void registerObserverDelegate(BMObserver observer){
        bmCommandCenter.registerObserver(observer);
    }

    // Default registration

    public void defaultWriteRegistration(){
        bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
        bmCommandCenter.registerObserver(new Gui(run));
    }

    public void defaultReadRegistration(){
        bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
        bmCommandCenter.registerObserver(new Gui(run));
        // Put if here for Max speed > 3% of avg speed
        if(run.getRunMax() > (run.getRunAvg() * 0.03)){
            slackManager = new SlackManager("BadBM", run);
            bmCommandCenter.registerObserver(slackManager);
        }
    }

}
