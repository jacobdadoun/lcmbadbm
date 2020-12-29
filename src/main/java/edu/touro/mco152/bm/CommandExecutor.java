package edu.touro.mco152.bm;

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.command.BMReadActionCommandCenter;
import edu.touro.mco152.bm.command.BMWriteActionCommandCenter;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DBPersistenceObserver;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

public class CommandExecutor {

    BMCommandCenter bmCommandCenter;

    final int numOfMarks = App.numOfMarks;
    final int numOfBlocks = App.numOfBlocks;
    final int blockSizeKb = App.blockSizeKb;
    final DiskRun.BlockSequence blockSequence = App.blockSequence;

    private SlackManager slackManager;

    public CommandExecutor(UserInterface userInterface, String command){
        DiskRun run;

        // Implement bmCommandCenter that will be assigned the respective Command Classes (i.e - write and read)
        // NOTE: In the real world, the BMCommand Interface ref will be assigned via set method.
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
