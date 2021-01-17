package com.project.ondevicebot;

import android.util.Pair;

import com.project.bff.MainActivity;

import org.tensorflow.lite.Interpreter;

import java.util.HashMap;

public class OnDeviceBotLength1_5 extends OnDeviceBotCommon
{

    protected static String idx2wordPath = "idx2word1_5.txt";
    protected static String word2vecPath = "word2vec1_5.txt";
    protected static String modelPath = "converted_model_v1_1_5.tflite";
    protected static Interpreter tflite = null;
    protected static HashMap<Integer, String> idx2word = null;
    protected static HashMap<String, float[]> word2vec = null;
    protected static Integer embdSize = null;
    protected static Integer maxlen = 5;
    protected MainActivity mainActivity;

    public OnDeviceBotLength1_5(MainActivity _mainActivity)
    {
        mainActivity = _mainActivity;
    }

    public String generateResponse(String msg)
    {
        if( tflite == null )
        {
            tflite = OnDeviceBotCommon.createModelInterpreter(modelPath, mainActivity);
            if( tflite == null )
            {
                return OnDeviceBotCommon.ExceptionMsg;
            }
        }
        if( idx2word == null )
        {
            idx2word = OnDeviceBotCommon.readIdx2Word(idx2wordPath, mainActivity);
            if( idx2word == null )
            {
                return OnDeviceBotCommon.ExceptionMsg;
            }
        }
        if( word2vec == null )
        {
            Pair<HashMap<String, float[]>, Integer> pair = OnDeviceBotCommon.readWord2Vec(word2vecPath, mainActivity);
            if( pair == null )
            {
                return OnDeviceBotCommon.ExceptionMsg;
            }
            word2vec = pair.first;
            embdSize = pair.second;
        }
        return OnDeviceBotCommon.generateResponse(
                msg, idx2word, embdSize,
                word2vec, tflite, maxlen
        );
    }
}
