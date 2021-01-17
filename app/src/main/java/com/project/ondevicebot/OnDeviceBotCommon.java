package com.project.ondevicebot;

import android.content.res.AssetFileDescriptor;
import android.util.Pair;

import com.project.bff.MainActivity;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class OnDeviceBotCommon
{

    public static String ExceptionMsg;

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
        catch (Exception e)
        {
            ExceptionMsg = e.toString();
            return null;
        }
    }

    protected static String[] preprocessMsg(String msg)
    {
        String[] words = msg.toLowerCase().split(" ");
        return words;
    }

    public static String generateResponse
    (String msg, HashMap<Integer, String> idx2word, Integer embdSize,
     HashMap<String, float[]> word2vec, Interpreter tflite, Integer maxlen)
    {
        String defaultRsp = new String("I don't know what to say!");
        String[] words = preprocessMsg(msg);
        for( String word: words )
        {
            if( !word2vec.containsKey(word) )
            {
                return defaultRsp;
            }
        }
        float embdSeq[][][] = new float[1][words.length][embdSize];
        for( int i = 0; i < words.length; i++ )
        {
            embdSeq[0][i] = word2vec.get(words[i]);
        }
        float outputSeq[][][] = new float[1][maxlen][idx2word.size()+1];
        tflite.run(embdSeq, outputSeq);
        StringBuilder respMsg = new StringBuilder();
        for( int i = 0; i < maxlen; i++ )
        {
            Integer maxIdx = 0;
            float currMax = 0;
            for( int idx = 0; idx < outputSeq[0][i].length; idx++ )
            {
                if( currMax < outputSeq[0][i][idx] )
                {
                    currMax = outputSeq[0][i][idx];
                    maxIdx = idx;
                }
            }
            if( maxIdx == 0 )
            {
                respMsg.append('.');
            }
            else
            {
                respMsg.append(idx2word.get(maxIdx));
            }
        }
        return respMsg.toString();
    }

    public static HashMap<Integer, String> readIdx2Word(String idx2WordPath, MainActivity mainActivity)
    {
        try
        {
            InputStream inputStream = mainActivity.getAssets().open(idx2WordPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HashMap<Integer, String> idx2word = new HashMap<Integer, String>();
            String line = reader.readLine();
            while( line != null )
            {
                String[] idx_word = line.replaceAll("\n", "").split(" ");
                idx2word.put(Integer.parseInt(idx_word[0]), idx_word[1]);
                line = reader.readLine();
            }
            return idx2word;
        }
        catch (Exception e)
        {
            ExceptionMsg = e.toString();
            return null;
        }
    }

    public static Pair<HashMap<String, float[]>, Integer> readWord2Vec(String word2VecPath, MainActivity mainActivity)
    {
        try
        {
            InputStream inputStream = mainActivity.getAssets().open(word2VecPath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            HashMap<String, float[]> word2vec = new HashMap<String, float[]>();
            String line = reader.readLine();
            Integer embdSize = 0;
            while( line != null )
            {
                String[] word_vecs = line.replaceAll("\n", "").split(" ");
                embdSize = word_vecs.length - 1;
                float[] embd = new float[embdSize];
                for (int i = 1; i < word_vecs.length; i++) {
                    embd[i - 1] = Float.parseFloat(word_vecs[i]);
                }
                word2vec.put(word_vecs[0], embd);
                line = reader.readLine();
            }
            return new Pair<HashMap<String, float[]>, Integer>(word2vec, embdSize);
        }
        catch (Exception e)
        {
            ExceptionMsg = e.toString();
            return null;
        }
    }
}
