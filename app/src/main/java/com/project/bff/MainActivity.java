package com.project.bff;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.midi.MidiOutputPort;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {


    List<MessageChatModel> messageChatModelList =  new ArrayList<>();
    RecyclerView recyclerView;
    MessageChatAdapter adapter ;

    EditText messageET;
    ImageView sendBtn;
    String dateFromat;

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

    public void sendMessage(View v) {

        String msg = messageET.getText().toString();
        if( msg.length() > 0 ) {
            TimeZone timeZone = TimeZone.getDefault();
            Calendar calendar = Calendar.getInstance(timeZone);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFromat);
            String data_time = simpleDateFormat.format(calendar.getTime()).toString();
            MessageChatModel model = new MessageChatModel(
                    msg,
                    data_time,
                    0
            );
            messageChatModelList.add(model);
            recyclerView.smoothScrollToPosition(messageChatModelList.size());
            adapter.notifyDataSetChanged();
            messageET.setText("");
        }

    }
}
