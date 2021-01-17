package com.project.bff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.project.dataapis.PlacesDataFetcher;
import com.project.dataapis.YouTubeDataFetcher;
import com.project.emotionapis.EmotionRecognizer;
import com.project.location.LocationTrack;
import com.project.ondevicebot.OnDeviceBotLength1_5;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import java.util.Random;
import java.util.RandomAccess;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

import org.tensorflow.lite.Interpreter;

public class MainActivity extends AppCompatActivity
{
    List<MessageChatModel> messageChatModelList =  new ArrayList<>();
    RecyclerView recyclerView;
    MessageChatAdapter adapter;

    EditText messageET;
    ImageView sendBtn;
    String dateFromat;
    private static final String[] Resources = {"YouTube", "Places", "OnDeviceBot"};
    private static final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageET = (EditText)findViewById(R.id.messageET);
        sendBtn = (ImageView) findViewById(R.id.sendBtn);
        dateFromat = "dd-MMM-yyyy, kk:mm z";

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);

        adapter = new MessageChatAdapter(messageChatModelList, MainActivity.this );
        recyclerView.setAdapter(adapter);

    }

    private void addMessageToList(String message, int viewType)
    {
        TimeZone timeZone = TimeZone.getDefault();
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFromat);
        String data_time = simpleDateFormat.format(calendar.getTime()).toString();
        MessageChatModel model = new MessageChatModel(
                message,
                data_time,
                viewType
        );
        messageChatModelList.add(model);
        recyclerView.smoothScrollToPosition(messageChatModelList.size());
        adapter.notifyDataSetChanged();
    }

    public void sendMessage(View v) throws InterruptedException, ExecutionException {

        String msg = messageET.getText().toString();
        if( msg.length() > 0 )
        {
            addMessageToList(msg, 0);
            messageET.setText("");
            generateMessageResponse(msg);
        }
    }

    private void fetchAndShowYouTubeData(EmotionRecognizer emotionRecognizer)
    throws ExecutionException,
           InterruptedException
    {
        String guessedEmotion = emotionRecognizer.getEmotionName();
        String guessedRemedy = emotionRecognizer.getSpaceSeparatedRemedyTerms();
        YouTubeDataFetcher youTubeDataFetcher = new YouTubeDataFetcher();
        youTubeDataFetcher.setAPIKey();
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("q", guessedRemedy);
        query.put("part", "snippet");
        query.put("type", "video");
        Integer status = youTubeDataFetcher.execute(query).get();
        HashMap<String, String> msgInfo = youTubeDataFetcher.toHashMap();
        String prefixResponseMessage = "It seems like you are feeling " + guessedEmotion + ". " +
                "May be you want to checkout some videos of " + guessedRemedy + ".\n\n";
        String responseMsg = "Title: " +
                msgInfo.get("title") + "\nLink: " +
                msgInfo.get("url");
        addMessageToList(prefixResponseMessage + responseMsg, 1);
    }

    private void fetchAndShowPlacesData(EmotionRecognizer emotionRecognizer, LocationTrack locationTrack)
    throws ExecutionException,
           InterruptedException
    {
        String longitude = Double.toString(locationTrack.getLongitude());
        String latitude = Double.toString(locationTrack.getLatitude());
        String guessedPlace = emotionRecognizer.getRemedyPlaces();
        PlacesDataFetcher placesDataFetcher = new PlacesDataFetcher();
        placesDataFetcher.setAPIKey();
        HashMap<String, String> query = new HashMap<String, String>();
        query.put("keyword", guessedPlace);
        query.put("location", latitude + "," + longitude);
        query.put("radius", "3000");
        Integer status = placesDataFetcher.execute(query).get();
        HashMap<String, String> msgInfo = placesDataFetcher.toHashMap();
        String prefixResponseMessage = "It seems like you are feeling " + emotionRecognizer.getEmotionName() + ". " +
                "May be you want to go out. Checkout this place, \n\n";
        String responseMsg = "Name: " +
                msgInfo.get("name") + "\nAddress: " +
                msgInfo.get("address") + "\nVicinity: " +
                msgInfo.get("vicinity") + "\nGoogle Maps Location: " +
                msgInfo.get("url");
        addMessageToList(prefixResponseMessage + responseMsg, 1);
    }

    private void generateMessageResponse(String msg)
    throws ExecutionException,
           InterruptedException {
        EmotionRecognizer emotionRecognizer = new EmotionRecognizer();
        emotionRecognizer.guessAndSetEmotion(msg);
        int resourceIndex = random.nextInt(Resources.length);
        resourceIndex = 2;
        if (Resources[resourceIndex] == "YouTube") {
            fetchAndShowYouTubeData(emotionRecognizer);
        } else if (Resources[resourceIndex] == "Places") {
            LocationTrack locationTrack = new LocationTrack(MainActivity.this);
            if (locationTrack.checkGPS()) {
                locationTrack.setLocation();
                if (locationTrack.getLocation() == null) {
                    fetchAndShowYouTubeData(emotionRecognizer);
                } else {
                    fetchAndShowPlacesData(emotionRecognizer, locationTrack);
                }
            } else {
                String backupMessage = "Hey! It seems like your GPS is not enabled." +
                        "Enable your GPS so that I can know where you are " +
                        "and we will go out together at some nearby places.\n" +
                        "Meanwhile, I am looking for something else for you.";
                addMessageToList(backupMessage, 1);
                fetchAndShowYouTubeData(emotionRecognizer);
            }
        } else if (Resources[resourceIndex] == "OnDeviceBot") {
            String resp = "I don't know what to say";
            int msgLength = computeMessageLentgh(msg);
            if( 1 <= msgLength && msgLength <= 5 ) {
                OnDeviceBotLength1_5 bot = new OnDeviceBotLength1_5(this);
                resp = bot.generateResponse(msg);
            }
            addMessageToList(resp, 1);
        }
    }

    private int computeMessageLentgh(String msg)
    {
        if( msg != null ) {
            return msg.split(" ").length;
        }
        return 0;
    }


}
