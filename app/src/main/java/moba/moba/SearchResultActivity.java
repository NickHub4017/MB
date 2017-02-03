package moba.moba;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private ArrayList<SearchResultUtil> se;
    String TAG="Search Result Activity";
    private int mBindFlag;
    private Messenger mServiceMessenger;
    private SpeechSelectResult speechSelectResult=new SpeechSelectResult();

    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.d(TAG, "onServiceConnected");

            mServiceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = BackgroundListner.MSG_RECOGNIZER_START_LISTENING;

            Toast.makeText(getApplicationContext(),"Service Connected",Toast.LENGTH_LONG).show();

            try
            {
                mServiceMessenger.send(msg);
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.d(TAG, "onServiceDisconnected");
            mServiceMessenger = null;
        }

    }; // mServiceConnection



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        Intent service = new Intent(SearchResultActivity.this, BackgroundListner.class);
        getApplicationContext().startService(service);
        mBindFlag = Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH ? 0 : Context.BIND_ABOVE_CLIENT;



        tts = new TextToSpeech(this, this);
        Intent intent = getIntent();
        se=intent.getParcelableArrayListExtra("linkSet");

        SearchResultAdapter searchResultAdapter=new SearchResultAdapter(se,getApplicationContext());

        ListView resultview=(ListView)findViewById(R.id.datalist);
        resultview.setAdapter(searchResultAdapter);

        registerReceiver(speechSelectResult, new IntentFilter("com.moba.moba.select"));

        //mResultSet.setText();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        bindService(new Intent(this, BackgroundListner.class), mServiceConnection, mBindFlag);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        unregisterReceiver(speechSelectResult);
        if (mServiceMessenger != null)
        {
            unbindService(mServiceConnection);
            mServiceMessenger = null;
        }
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            Toast.makeText(getApplicationContext(),"RES ",Toast.LENGTH_LONG).show();
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                Toast.makeText(getApplicationContext(),"This Language is not supported",Toast.LENGTH_LONG).show();
            } else {
                AnounceResult();
            }

        } else {
            Toast.makeText(getApplicationContext(),"Initilization Failed!",Toast.LENGTH_LONG).show();
            Log.e("TTS", "Initilization Failed!");
        }
    }

    public void AnounceResult(){
        for (int i=0;i<se.size();i++) {
            tts.speak("Result Number "+(i+1), TextToSpeech.QUEUE_ADD, null);
            tts.speak("Topic is "+ se.get(i).getTitile(), TextToSpeech.QUEUE_ADD, null);
            tts.speak(se.get(i).getDescription(), TextToSpeech.QUEUE_ADD, null);

        }
    }

    class SpeechSelectResult extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int selected=intent.getIntExtra("selected",0)-1;
            if(selected>=0 && selected<se.size()-1){
                Toast.makeText(getApplicationContext(),"-----> <---- "+se.get(selected).getTitile(),Toast.LENGTH_LONG).show();
            }


        }

    }
}
