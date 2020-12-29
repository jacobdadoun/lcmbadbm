package edu.touro.mco152.bm;

public class TestObserver implements BMObserver{

    private boolean updateFlag;

    public TestObserver(){
        updateFlag = false;
    }

    @Override
    public void update() {
        updateFlag = true;
    }

    public boolean isUpdated(){
        return updateFlag;
    }
}
