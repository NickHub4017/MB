package moba.moba;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.util.Log;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    public String searchResultLinks="test";


    private WebView mWebView;
    protected static final int RESULT_SPEECH = 1;
    private ImageButton btnSpeak;
    private TextView txtText;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = (WebView) findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("http://google.com/");
        txtText = (TextView) findViewById(R.id.txtText);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        final String LOG_TAG = "myLogs";
        String htmlCode = "";
        TextToSpeech t1;

       /* t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });*/



        try {
            URL url = new URL(mWebView.getUrl());
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                htmlCode += inputLine;

            in.close();
        } catch (Exception e) {

            Log.d(LOG_TAG, "Error: " + e.getMessage());
        }


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    txtText.setText("");
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getApplicationContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    txtText.setText(text.get(0));
                    String key = txtText.getText().toString();
                    //mWebView.loadUrl("http://google.com/search?q="+key);
                    check(key);
                }
                break;
            }

        }
    }

    public void check(String key) {

        String back = new String("back");
        String next = new String("next");
        String exit = new String("exit");
        String search = new String("search");
        String up = new String("up");
        String down = new String("down");


        if (key.equals(back)) {
            onBackPressed();
        } else if (key.equals(next)) {
            goForward();
        } else if (key.equals(exit)) {
            super.onBackPressed();
        } else if (key.equals(search)) {
            mWebView.loadUrl("http://google.com/search?q=");
        } else if (key.equals(up)) {
            mWebView.pageUp(true);
        } else if (key.equals(down)) {
            mWebView.pageDown(true);
        } else {
            Log.d("debug", "search result");
            String url = "http://google.com/search?q=" + key;
            //mWebView.loadUrl(url);
            ( new ParseURL() ).execute(new String[]{url});


        }
    }

    public void goForward() {


        mWebView.goForward();

    }

    public void onBackPressed() {
        if (mWebView.copyBackForwardList().getCurrentIndex() > 0) {
            mWebView.goBack();
        } else {
            // Your exit alert code, or alternatively line below to finish
            super.onBackPressed(); // finishes activity
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://moba.moba/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://moba.moba/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class ParseURL extends AsyncTask<String, Void, ArrayList<Element>> {

        @Override
        protected ArrayList<Element> doInBackground(String... strings) {

            ArrayList<Element> elementsarry=new ArrayList<Element>();

            StringBuffer buffer = new StringBuffer();
            try {
                Log.d("cj", "Connecting to [" + strings[0] + "]");
                Document doc = Jsoup.connect(strings[0]).get();
                Log.d("cj", "Connected to [" + strings[0] + "]");

                Elements linklist = doc.select("div.g");
                int i = 1;
                for (Element link : linklist) {
                    elementsarry.add(link);
                    i++;
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return elementsarry;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Element> s) {
            super.onPostExecute(s);
            //respText.setText(s);

            ArrayList<SearchResultUtil> res=new ArrayList<SearchResultUtil>();
            for (int i=0;i<s.size();i++) {
                try {
                    String titile = s.get(i).child(0).getElementsByClass("r").get(0).getElementsByTag("a").get(0).text();
                    String link = s.get(i).child(0).getElementsByClass("r").get(0).getElementsByTag("a").get(0).attr("href");
                    String description = s.get(i).child(0).getElementsByClass("s").get(0).getElementsByClass("st").get(0).text();

                    Log.d("cj", "---> " + description);
                    res.add(new SearchResultUtil(titile,link,description));
                }
                catch (Exception e){

                }
            }

            Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
            intent.putExtra("linkSet", res);
            startActivity(intent);

        }

    }


}



