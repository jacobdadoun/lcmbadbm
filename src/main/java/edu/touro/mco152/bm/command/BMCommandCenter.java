package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * BMCommandCenter is an interface that our read/write benchmark will implement.
 * Diskworker instantiates a command object and calls it's execute method
 */
public interface BMCommandCenter {
    boolean doBMCommand();
    void undoBMCommand();
    DiskRun getDiskRun();
}
