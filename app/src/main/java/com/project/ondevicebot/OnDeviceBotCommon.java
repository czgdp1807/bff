package com.project.ondevicebot;

import android.content.res.AssetFileDescriptor;

import com.project.bff.MainActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class OnDeviceBotCommon
{

    protected String ioException;

    protected MappedByteBuffer loadModelFile(MainActivity mainActivity)
    {
        try
        {
            AssetFileDescriptor fileDescriptor = mainActivity.getAssets().openFd(this.getModelPath());
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

    protected String getModelPath()
    {
        return new String("");
    }
}
