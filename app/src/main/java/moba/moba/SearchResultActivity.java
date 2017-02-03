package moba.moba;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        tts = new TextToSpeech(this, this);
        Intent intent = getIntent();
        se=intent.getParcelableArrayListExtra("linkSet");

        SearchResultAdapter searchResultAdapter=new SearchResultAdapter(se,getApplicationContext());

        ListView resultview=(ListView)findViewById(R.id.datalist);
        resultview.setAdapter(searchResultAdapter);
        //mResultSet.setText();
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
}
