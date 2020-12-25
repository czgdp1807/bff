package com.project.bff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.project.dataapis.YouTubeDataFetcher;
import com.project.emotionapis.EmotionRecognizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Random;
import java.util.RandomAccess;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    List<MessageChatModel> messageChatModelList =  new ArrayList<>();
    RecyclerView recyclerView;
    MessageChatAdapter adapter ;

    EditText messageET;
    ImageView sendBtn;
    String dateFromat;
    private static final String[] Resources = {"YouTube"};
    private static final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void generateMessageResponse(String msg)
    throws ExecutionException,
           InterruptedException
    {
        EmotionRecognizer emotionRecognizer = new EmotionRecognizer();
        emotionRecognizer.guessAndSetEmotion(msg);
        String guessedEmotion = emotionRecognizer.getEmotionName();
        int resourceIndex = random.nextInt(Resources.length);
        if( Resources[resourceIndex] == "YouTube" )
        {
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
    }
}
