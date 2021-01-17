package com.project.ondevicebot;

import com.project.bff.MainActivity;

import org.python.core.PyFile;
import org.python.core.PyObject;
import org.tensorflow.lite.Interpreter;
import org.python.modules.cPickle;

import java.io.IOException;
import java.nio.MappedByteBuffer;

public class OnDeviceBotLength1_5
       extends OnDeviceBotCommon implements OnDeviceBot
{

    private static Interpreter tflite;

    public OnDeviceBotLength1_5()
    {
        tflite = null;
    }

    @Override
    protected String getModelPath()
    {
        return new String("converted_model_v1_1_5.tflite");
    }

    @Override
    public String generateResponse(String msg, MainActivity mainActivity)
    {
        if( tflite == null )
        {
            MappedByteBuffer model = loadModelFile(mainActivity);
            if( model == null )
            {
                return ioException;
            }
            tflite = new Interpreter(model);
        }

        cPickle pickler = new cPickle();
        PyFile file = new PyFile("idx2word1_5.pkl", "r", 1024);
        return file.toString();
    }
}
