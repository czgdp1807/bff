package com.project.ondevicebot;

import android.content.res.AssetFileDescriptor;

import com.project.bff.MainActivity;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class OnDeviceBotCommon
{

    public static String ioException;

    public static Interpreter createModelInterpreter(String modelPath, MainActivity mainActivity)
    {
        MappedByteBuffer model = loadModelFile(modelPath, mainActivity);
        if( model != null )
        {
            return new Interpreter(model);
        }
        return null;
    }

    protected static MappedByteBuffer loadModelFile(String modelPath, MainActivity mainActivity)
    {
        try
        {
            AssetFileDescriptor fileDescriptor = mainActivity.getAssets().openFd(modelPath);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
        catch (IOException ioe)
        {
            ioException = ioe.toString();
            return null;
        }
    }

    public static String generateResponse
    (String msg, HashMap<Integer, String> idx2word,
     HashMap<String, ArrayList<Double>> word2vec, Interpreter tflite)
    {

        return new String("");
    }

    protected HashMap<Integer, String> readIdx2Word()
    {
        return null;
    }
}
