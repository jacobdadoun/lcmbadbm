package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.GUIBenchmark;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExecutorAndCommandTest {

    public GUIBenchmark guiBenchmark = new GUIBenchmark();
    public int numOfMark = 50;
    public int numOfBlocks = 64;
    public int blockSizeKb = 64;

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
        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

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

    @Test
    public void writeCommand(){
        setupDefaultAsPerProperties();

        BMWriteActionCommandCenter bmWriteActionCommandCenter = new BMWriteActionCommandCenter(guiBenchmark, numOfMark, numOfBlocks, blockSizeKb, App.blockSequence);

        assertTrue(bmWriteActionCommandCenter.execute());

    }

    @Test
    public void readCommand(){
        setupDefaultAsPerProperties();

        BMReadActionCommandCenter bmReadActionCommandCenter = new BMReadActionCommandCenter(guiBenchmark, numOfMark, numOfBlocks, blockSizeKb, App.blockSequence);

        assertTrue(bmReadActionCommandCenter.execute());

    }
}
