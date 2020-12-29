package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeListener;

public class NonSwingBenchmark implements UserInterface{

    public String readOrWrite;
    public boolean progressBool = true;

    public NonSwingBenchmark(String readOrWrite){
        this.readOrWrite = readOrWrite;
    }


    @Override
    public void registerPropertyChangeListener(PropertyChangeListener listener) {

    }

    @Override
    public void executeBenchMark() {
        if(readOrWrite.equals("write")){
            BenchmarkClient.writeBMLogicNoSwing();
        }
        else if(readOrWrite.equals("read")){
            BenchmarkClient.readBMLogicNoSwing();
        }
    }

    @Override
    public boolean isBenchMarkCancelled() {
        return progressBool;
    }

    @Override
    public void provideProgress(int x) {
        if(x == 100){
            progressBool = false;
        }
    }

    @Override
    public void publishToGUI(DiskMark diskMark) {

    }

    @Override
    public boolean cancelBenchMark(boolean bool) {
        provideProgress(100);
        return true;
    }

    @Override
    public void showMessagePopUp() {
        System.out.println("-- For valid READ measurements please clear the disk cache by\n" +
                "-- using the included RAMMap.exe or flushmem.exe utilities.\n" +
                "-- Removable drives can be disconnected and reconnected.\n" +
                "-- For system drives use the WRITE and READ operations \n" +
                "-- independantly by doing a cold reboot after the WRITE");
    }

}
