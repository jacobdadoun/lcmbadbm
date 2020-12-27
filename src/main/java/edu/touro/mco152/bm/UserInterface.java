package edu.touro.mco152.bm;

public interface UserInterface {

    void registerPropertyChangeListener(java.beans.PropertyChangeListener listener);

    void executeBenchMark();

    boolean isBenchMarkCancelled();

    void provideProgress(int x);

    void publishToGUI(DiskMark diskMark);

    boolean cancelBenchMark(boolean bool);

    void showMessagePopUp();

}
