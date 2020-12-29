package edu.touro.mco152.bm;

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMReadActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DBPersistenceObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

public class CommandExecutor {

    private final UserInterface userInterface;
    public final String command;

    // Implement bmCommandCenter that will be assigned the respective Command Classes (i.e - write and read)
    // NOTE: In the real world, the BMCommand Interface ref will be assigned via set method.
    private BMCommandCenter bmCommandCenter;

    private int numOfMarks = App.numOfMarks;
    private int numOfBlocks = App.numOfBlocks;
    private int blockSizeKb = App.blockSizeKb;
    private DiskRun.BlockSequence blockSequence = App.blockSequence;

    private DiskRun run;

    private SlackManager slackManager;

    public CommandExecutor(UserInterface userInterface, String command){
        this.userInterface = userInterface;
        this.command = command;
    }

    public void setDefaultBMParams(){
        switch (command.toLowerCase()){
            case "write":
                bmCommandCenter = new BMWriteActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
                run = bmCommandCenter.getDiskRun();
                //Persist info about the Write BM Run (e.g. into Derby Database)
                bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
                bmCommandCenter.registerObserver(new Gui(run));
            case "read":
                bmCommandCenter = new BMReadActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
                run = bmCommandCenter.getDiskRun();
                //Persist info about the Write BM Run (e.g. into Derby Database)
                bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
                bmCommandCenter.registerObserver(new Gui(run));
                // Put if here for Max speed > 3% of avg speed
                if(run.getRunMax() > (run.getRunAvg() * 0.03)){
                    slackManager = new SlackManager("BadBM", run);
                    bmCommandCenter.registerObserver(slackManager);
                }
        }
    }

    public void setCustomBMParams(int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence){
        switch (command.toLowerCase()){
            case "write":
                bmCommandCenter = new BMWriteActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
                run = bmCommandCenter.getDiskRun();
                //Persist info about the Write BM Run (e.g. into Derby Database)
                bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
                bmCommandCenter.registerObserver(new Gui(run));
            case "read":
                bmCommandCenter = new BMReadActionCommandCenter(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
                run = bmCommandCenter.getDiskRun();
                //Persist info about the Write BM Run (e.g. into Derby Database)
                bmCommandCenter.registerObserver(new DBPersistenceObserver(run));
                bmCommandCenter.registerObserver(new Gui(run));
                // Put if here for Max speed > 3% of avg speed
                if(run.getRunMax() > (run.getRunAvg() * 0.03)){
                    slackManager = new SlackManager("BadBM", run);
                    bmCommandCenter.registerObserver(slackManager);
                }
        }
    }

    public boolean executeBMCommandObject(){
        bmCommandCenter.execute();
        if( !bmCommandCenter.message.isEmpty() ){
            slackManager.setMessage(bmCommandCenter.message);
        }
        return true;
    }

    public void notifyCommandObservers(){
        bmCommandCenter.notifyObservers();
    }

}
