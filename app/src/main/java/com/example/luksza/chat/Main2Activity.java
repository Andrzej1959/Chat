package com.example.luksza.chat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        nickTextView = findViewById(R.id.nickTextView);
        chatListView = findViewById(R.id.chatListView);
        messageEditText = findViewById(R.id.messageEditText);

        nick = getIntent().getStringExtra(MainActivity.NICK);
        ip = getIntent().getStringExtra(MainActivity.IP);
        topic = getIntent().getStringExtra(MainActivity.TOPIC);
        topicSub = getIntent().getStringExtra(MainActivity.TOPIC_SUB);

        nickTextView.setText(getIntent().getStringExtra(MainActivity.NICK));

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        chatListView.setAdapter(adapter);

        //uruchamiamy MQTT w tle
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();
    }

    ListView chatListView;
    TextView nickTextView;
    TextView messageEditText;
    String ip;
    String nick;
    String topic;
    String topicSub;

    ArrayList<String> listItems=new ArrayList<String>();
    //DEFINING STRING ADAPTER WHICH WILL HANDLE DATA OF LISTVIEW
    ArrayAdapter<String> adapter;


    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            listItems.add("["+msg.getData().getString("NICK") + "]" +
                    msg.getData().getString("MSG"));
            adapter.notifyDataSetChanged();
            chatListView.setSelection(listItems.size()-1);
        }
    };




    public void postOnClick(View view){
      /*  Message msg = myHandler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("NICK", "JA");
        b.putString("MSG", messageEditText.getText().toString());
        msg.setData(b);
        myHandler.sendMessage(msg);
        */

      if(sampleClient!=null) {
          MqttMessage message1 = new MqttMessage(messageEditText.getText().toString().getBytes());

          try {
              sampleClient.publish(topic, message1);
          } catch (MqttException e) {
              e.printStackTrace();
          }
      }
    }

    MqttClient sampleClient=null;

    protected void onDestroy() {
        super.onDestroy();
        if (sampleClient != null) {
            try {
                sampleClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            String broker = "tcp://"+ip+":1883";    // tcp nie mqtt
            clientId = nick;

            sampleClient = new MqttClient(broker, clientId, persistence);
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg = myHandler.obtainMessage();
                    Bundle b = new Bundle();

                    b.putString("NICK", nick);
                    b.putString("MSG", mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
                //TODO
            });

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            sampleClient.subscribe(topicSub);

        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
