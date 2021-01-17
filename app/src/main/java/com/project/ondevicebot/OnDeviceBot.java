package com.project.ondevicebot;

import android.content.res.AssetFileDescriptor;

import com.project.bff.MainActivity;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public interface OnDeviceBot
{
    public String generateResponse(String msg, MainActivity mainActivity);
}
