package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    // Arrange
    NonSwingBenchmark nonSwingBenchmark;
    BenchmarkClient bmClientTest;

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
     * tests to initialize the program without gui
     */
    @Test
    public void main_Test(){
        // - APP
        // Act
        setupDefaultAsPerProperties();
        // Assert
        assertTrue(App.writeTest);

        // NonSwingBenchmark implementation of UserInterface.
        NonSwingBenchmark nonSwingBenchmark = new NonSwingBenchmark();
        // Test in NonSwingBMTest class.
        NonSwingBenchMarkTest nonSwingBenchMarkTest = new NonSwingBenchMarkTest();
        nonSwingBenchMarkTest.nonSwingBMTemp = nonSwingBenchmark;
        nonSwingBenchMarkTest.test_init();

        // bmClient test "replica" of DiskWorker from main.
        BenchmarkClient bmClient = new BenchmarkClient(nonSwingBenchmark);
        // execute bmClient from NonSwingBenchmark
        bmClient.executionDelegate();


    }

}
