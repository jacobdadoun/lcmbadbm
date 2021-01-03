package edu.touro.mco152.bm;

public class TestObserver implements BMObserver{

    private boolean hasBeenUpdated;

    public TestObserver(){
        hasBeenUpdated = false;
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
