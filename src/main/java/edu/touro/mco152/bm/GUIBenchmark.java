package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import static edu.touro.mco152.bm.App.*;

/**
 * SwingWorker<T,V>
 * T - the result type returned by this SwingWorker's doInBackground and get methods
 * V - the type used for carrying out intermediate results by this SwingWorker's publish and process methods
 */
public class GUIBenchmark extends SwingWorker<Boolean, DiskMark> implements UserInterface {

    @Override
    public void registerPropertyChangeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(listener);
    }

    @Override
    public void executeBenchMark() {
        execute();
    }

    @Override
    public boolean isBenchMarkCancelled() {
        return isCancelled();
    }


    @Override
    public void provideProgress(int x) {
        setProgress(x);
    }


    @Override
    public void publishToGUI(DiskMark diskMark) {
        publish(diskMark);
    }


    @Override
    public boolean cancelBenchMark(boolean bool) {
        return cancel(bool);
    }


    @Override
    protected Boolean doInBackground(){
        return DiskWorker.doBMLogic();
    }

    @Override
    public void process(List<DiskMark> markList) {
        // We are passed a list of one or more DiskMark objects that our thread has previously
        // published to Swing. Watch Professor Cohen's video - Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }


    @Override
    protected void done() {
        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
        System.err.println("---FINISHED BENCHMARK---") ;
    }
}
