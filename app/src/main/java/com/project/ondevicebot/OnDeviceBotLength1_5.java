package com.project.ondevicebot;

import android.content.res.AssetFileDescriptor;

import com.project.bff.MainActivity;

import org.python.core.PyDictionary;
import org.python.core.PyException;
import org.python.core.PyFile;
import org.python.core.PyObject;
import org.tensorflow.lite.Interpreter;
import org.python.modules.cPickle;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class OnDeviceBotLength1_5 extends OnDeviceBotCommon
{

    protected static String idx2wordPath = "idx2word1_5.txt";
    protected static String word2vecPath = "word2vec1_5.txt";
    protected static String modelPath = "converted_model_v1_1_5.tflite";
    protected static Interpreter tflite = null;
    protected static HashMap<Integer, String> idx2word = null;
    protected static HashMap<String, ArrayList<Float>> word2vec = null;
    protected MainActivity mainActivity;

    public OnDeviceBotLength1_5(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

}
