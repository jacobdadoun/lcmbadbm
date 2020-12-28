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


public class ObserverTest {

    WriteReadCommandTest writeReadCommandTest = new WriteReadCommandTest();


    @Test
    public void writeObservers(){
        writeReadCommandTest.writeCommand();

        

    }

    @Test
    public void readObservers(){

    }


}
