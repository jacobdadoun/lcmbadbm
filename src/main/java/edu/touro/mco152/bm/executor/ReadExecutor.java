package edu.touro.mco152.bm.executor;

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.externalsys.SlackManager;
import edu.touro.mco152.bm.persist.DBPersistenceObserver;
import edu.touro.mco152.bm.ui.Gui;

/**
 * An object that acts as an Executor for BMCommandObject.
 * Our new CommandExecutor class will allow us to execute any kind of command object we
 * please, provided we have that object extend BMCommandObject
 */
public class ReadExecutor extends AbstractExecutor{

    // Implement bmCommandCenter that will be assigned the respective Command Classes (i.e - write and read)
    // NOTE: In the real world, the BMCommandCenter object ref will be assigned via set method.
    public ReadExecutor(BMCommandCenter bmCommandCenter){
        super(bmCommandCenter);
        bmCommandCenter.registerObserver(new DBPersistenceObserver(bmCommandCenter));
        bmCommandCenter.registerObserver(new Gui(bmCommandCenter));
        bmCommandCenter.registerObserver(new SlackManager("BadBM", bmCommandCenter));
    }

}
