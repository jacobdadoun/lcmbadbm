package edu.touro.mco152.bm;

import edu.touro.mco152.bm.command.BMCommandCenter;
import edu.touro.mco152.bm.persist.DiskRun;

public class TestObserver implements BMObserver{

    private final DiskRun diskRun;
    private boolean hasBeenUpdated;

    public TestObserver(BMCommandCenter command){
        hasBeenUpdated = false;
        this.diskRun = command.getDiskRun();
        command.registerObserver(this);
    }

    @Override
    public void update() {
        hasBeenUpdated = true;
    }

    @Override
    public Boolean isUpdated() {
        return hasBeenUpdated;
    }
}
