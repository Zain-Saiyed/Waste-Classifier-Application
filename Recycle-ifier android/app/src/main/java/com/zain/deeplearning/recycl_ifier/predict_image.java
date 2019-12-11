package com.zain.deeplearning.recycl_ifier;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class predict_image extends Activity {

    static{
        System.loadLibrary("tensorflow_inference");
    }

    private static final int SIZE = 224 * 224;

    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/tensorflow_lite_model.pb";
    private static final String INPUT_NODE = "x";
    private static final String[] OUTPUT_NODES = {"y"};
    private static final String OUTPUT_NODE = "y";
    private static final long[] INPUT_SIZE = {1,SIZE};
    private static final int OUTPUT_SIZE = 10;

    public float[] predict(float[] data) {
        //array to store the result/output of the model
        float[] result = new float[OUTPUT_SIZE];
        //feed the input to our model, passing name of the input node, data, and size of the input
        inferenceInterface.feed(INPUT_NODE, data, INPUT_SIZE);
        //run the model on the input data, passing the name of output nodes
        inferenceInterface.run(OUTPUT_NODES);
        //get the predicted result from the model, passing name of the output node, and the array to store the result
        inferenceInterface.fetch(OUTPUT_NODE, result);
        //return the result
        return result;
    }

}
