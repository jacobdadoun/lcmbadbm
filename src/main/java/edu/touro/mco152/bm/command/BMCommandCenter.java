package edu.touro.mco152.bm.command;

/**
 * BMCommandCenter is an interface that our read/write benchmark will implement.
 * Diskworker instantiates a command object and calls it's execute method
 */
public interface BMCommandCenter {
    void doBMCommand();
    void undoBMCommand();
}
