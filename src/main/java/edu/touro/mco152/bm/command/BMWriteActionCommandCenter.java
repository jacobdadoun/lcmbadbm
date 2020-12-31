package edu.touro.mco152.bm.command;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.*;
import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;


/**
 * This class has one purpose, to provide instructions for writing a benchmark. We instantiate this class with an object
 * of type SwingWorker and UserInterface (GUIBenchMark), which will be used in doBMLogic to call instruction to
 * GUIBenchMark::isBenchMarkCancelled, ::executeBenchMark, ::publishToGUI and ::provideProgress.
 *
 * Extend our BMCommandCenter to make our writeBM class an object of type BMCommandCenter
 */
public class BMWriteActionCommandCenter extends BMCommandCenter {

    public BMWriteActionCommandCenter(UserInterface userInterface, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence){
        super(userInterface, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
    }

    /**
     * At the end of the day, execution itself is dependant on one method... this one.
     * No longer do we reference important data so dependant and 'tightly coupled'. Now,
     * The essential variables needed will come defined from the client who doesn't mind
     * being tied down to App.java in order for it to set them independently.
     *
     * @return a boolean to an if-statement condition in the CommandExecutor.executeLogicDelegate
     */
    @Override
    public boolean execute() {

        msg("Running writeTest " + writeTest);
        msg("num files: " + numOfMarks + ", num blks: " + numOfBlocks
                + ", blk size (kb): " + blockSizeKb + ", blockSequence: " + blockSequence);

        /**
         * init local vars that keep track of benchmarks, and a large write buffer
         */
        int wUnitsComplete = 0,
                rUnitsComplete = 0,
                unitsComplete;

        int wUnitsTotal = writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKb*KILOBYTE;
        byte [] blockArr = new byte [blockSize];
        for (int b=0; b<blockArr.length; b++) {
            if (b%2==0) {
                blockArr[b]=(byte)0xFF;
            }
        }

        DiskMark wMark;
        int startFileNum = App.nextMarkNumber;

        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        // Tell logger and GUI to display what we know so far about the Run
        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        // Create a test data file using the default file system and config-specified location
        if (!App.multiFile) {
            testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        }

            /*
              Begin an outer loop for specified duration (number of 'marks') of benchmark,
              that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
              and is reported to the GUI for display as each Mark completes.
             */
        for (int m = startFileNum; m < startFileNum + numOfMarks && !userInterface.isBenchMarkCancelled(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            wMark = new DiskMark(WRITE);    // starting to keep track of a new bench Mark
            wMark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesWrittenInMark = 0;

            String mode = "rw";
            if (App.writeSyncEnable) {
                mode = "rwd";
            }

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek(rLoc * blockSize);
                        } else {
                            rAccFile.seek(b * blockSize);
                        }
                        rAccFile.write(blockArr, 0, blockSize);
                        totalBytesWrittenInMark += blockSize;
                        wUnitsComplete++;
                        unitsComplete = rUnitsComplete + wUnitsComplete;
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

                            /*
                              Report to GUI what percentage level of Entire BM (#Marks * #Blocks) is done.
                             */
                        userInterface.provideProgress((int) percentComplete);
                    }
                }
            } catch (FileNotFoundException fnfEx) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, fnfEx);
            }
            catch (IOException ioEx) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ioEx);
            }

                /*
                  Compute duration, throughput of this Mark's step of BM
                 */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbWritten = (double) totalBytesWrittenInMark / (double) MEGABYTE;
            wMark.setBwMbSec(mbWritten / sec);

            // DiskMark::getBwMbSecAsString
            msg("m:" + m + " write IO is " + wMark.getBwMbSecAsString() + " MB/s     "
                    + "(" + Util.displayString(mbWritten) + "MB written in "
                    + Util.displayString(sec) + " sec)");
            App.updateMetrics(wMark);

                /*
                  Let the GUI know the interim result described by the current Mark
                 */
            userInterface.publishToGUI(wMark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(wMark.getCumMax());
            run.setRunMin(wMark.getCumMin());
            run.setRunAvg(wMark.getCumAvg());
            run.setEndTime(new Date());
        } // END outer loop for specified duration (number of 'marks') for WRITE bench mark
        return true;
    }

    @Override
    public void undoBMCommand() {

    }

    /**
     * Client should be able to call for a DiskRun when one needed (Observers take DiskRuns as constructor params)
     * @return the data for a single run.
     */
    @Override
    public DiskRun getDiskRun() {
        return run;
    }

}
