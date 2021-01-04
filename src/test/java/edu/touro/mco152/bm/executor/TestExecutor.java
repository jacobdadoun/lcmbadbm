package edu.touro.mco152.bm.executor;

import edu.touro.mco152.bm.TestObserver;
import edu.touro.mco152.bm.command.BMCommandCenter;

public class TestExecutor extends AbstractExecutor{

    public TestExecutor(BMCommandCenter command) {
        super(command);
        command.registerObserver(new TestObserver(command));
    }

}
