package edu.touro.mco152.bm;

import edu.touro.mco152.bm.Executor.IExecutor;
import edu.touro.mco152.bm.command.BMCommandCenter;

public class CommandExecutor implements IExecutor {

    BMCommandCenter command;

    public CommandExecutor(BMCommandCenter command){
        this.command = command;
    }


    @Override
    public boolean execute() {
        return command.execute();
    }

    
}
