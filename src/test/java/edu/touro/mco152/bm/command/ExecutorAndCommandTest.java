package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorAndCommandTest {


    @BeforeAll
    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     *
     * @author lcmcohen
     */
    static void setupDefaultAsPerProperties() {
        /// Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();
        System.out.println(App.getConfigString());
        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference
        App.dataDir = new File(App.locationDir.getAbsolutePath() + File.separator + App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }

    /**
     * writeCommand tests the boolean return value from BMWriteActionCommandCenter.execute()
     */
    @Test
    public void writeBMTest(){
        setupDefaultAsPerProperties();

        NonSwingBenchmark nonSwingBenchmark = new NonSwingBenchmark("write");
        BenchmarkClient bmClient = new BenchmarkClient(nonSwingBenchmark);
        bmClient.executionDelegate();
    }

    /**
     * readCommand tests the boolean return value from BMReadActionCommandCenter.execute()
     */
    @Test
    public void readBMTest(){
        setupDefaultAsPerProperties();

        NonSwingBenchmark nonSwingBenchmark = new NonSwingBenchmark("read");
        BenchmarkClient bmClient = new BenchmarkClient(nonSwingBenchmark);
        bmClient.executionDelegate();
    }
}
