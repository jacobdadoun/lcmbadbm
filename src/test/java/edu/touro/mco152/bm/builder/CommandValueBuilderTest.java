package edu.touro.mco152.bm.builder;

import edu.touro.mco152.bm.UserInterface;
import edu.touro.mco152.bm.client.BenchmarkClient;
import edu.touro.mco152.bm.ui.NonSwingBenchmark;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommandValueBuilderTest {

    // Arrange
    IBuilder builder;
    UserInterface userInterface = new NonSwingBenchmark();

    /**
     * Tests for the "proper" order of building, to see that we get the same results as the ones we passed in.
     */
    @Test
    public void test_ValueBuilder_ProperOrder(){
        // Act
        builder = new CommandValueBuilder(userInterface).numOfMarks(BenchmarkClient.numOfMarks)
                .numOfBlocks(BenchmarkClient.numOfBlocks).blockSizeKb(BenchmarkClient.blockSizeKb)
                .blockSequence(BenchmarkClient.blockSequence);
        CommandValueRequest request = builder.getRequest();

        // Assert
        assertEquals(BenchmarkClient.numOfMarks, request.getNumOfMarks());
        assertEquals(BenchmarkClient.numOfBlocks, request.getNumOfBlocks());
        assertEquals(BenchmarkClient.blockSizeKb, request.getBlockSizeKb());
        assertEquals(BenchmarkClient.blockSequence, request.getBlockSequence());
    }

    /**
     * Tests for the "improper" order of building, to see that we get the same results as the ones we passed in.
     */
    @Test
    public void test_ValueBuilder_ImproperOrder(){
        // Act
        builder = new CommandValueBuilder(userInterface).blockSizeKb(BenchmarkClient.blockSizeKb)
                .numOfMarks(BenchmarkClient.numOfMarks).blockSequence(BenchmarkClient.blockSequence)
                .numOfBlocks(BenchmarkClient.numOfBlocks);
        CommandValueRequest request = builder.getRequest();

        // Assert
        assertEquals(BenchmarkClient.numOfMarks, request.getNumOfMarks());
        assertEquals(BenchmarkClient.numOfBlocks, request.getNumOfBlocks());
        assertEquals(BenchmarkClient.blockSizeKb, request.getBlockSizeKb());
        assertEquals(BenchmarkClient.blockSequence, request.getBlockSequence());
    }

    /**
     * Tests for the "proper", but shortened order of building, to see that we get the same results as the ones we
     * passed in. Along with the default values for those we haven't set.
     */
    @Test
    public void test_ValueBuilder_ShortOrder(){
        // Act
        // leave out numOfMarks and blockSizeKB (should be automatically set to 0)
        builder = new CommandValueBuilder(userInterface).blockSequence(BenchmarkClient.blockSequence)
                .numOfBlocks(BenchmarkClient.numOfBlocks);
        CommandValueRequest request = builder.getRequest();

        // Assert
        assertEquals(request.getNumOfMarks(), 0);
        assertEquals(BenchmarkClient.numOfBlocks, request.getNumOfBlocks());
        assertEquals(request.getBlockSizeKb(), 0);
        assertEquals(BenchmarkClient.blockSequence, request.getBlockSequence());
    }

    /**
     * Tests for the "improper" AND shortened order of building, to see that we get the same results as the ones we
     * passed in. Along with the default values for those we haven't set.
     */
    @Test
    public void test_ValueBuilder_ShortImproperOrder(){
        // Act
        // leave out numOfMarks and blockSizeKB (should be automatically set to 0)
        builder = new CommandValueBuilder(userInterface).numOfBlocks(BenchmarkClient.numOfBlocks)
                .blockSequence(BenchmarkClient.blockSequence);
        CommandValueRequest request = builder.getRequest();

        // Assert
        assertEquals(request.getNumOfMarks(), 0);
        assertEquals(BenchmarkClient.numOfBlocks, request.getNumOfBlocks());
        assertEquals(request.getBlockSizeKb(), 0);
        assertEquals(BenchmarkClient.blockSequence, request.getBlockSequence());
    }
}
