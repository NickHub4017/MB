package moba.moba;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by nrv on 2/3/17.
 */

public class BackgroundListner extends Service {
    protected AudioManager mAudioManager;
    protected SpeechRecognizer mSpeechRecognizer;
    protected Intent mSpeechRecognizerIntent;
    protected final Messenger mServerMessenger = new Messenger(new IncomingHandler(this));

    protected boolean mIsListening;
    protected volatile boolean mIsCountDownOn;

    static final int MSG_RECOGNIZER_START_LISTENING = 1;
    static final int MSG_RECOGNIZER_CANCEL = 2;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
    }

    protected static class IncomingHandler extends Handler
    {
        private WeakReference<BackgroundListner> mtarget;

        IncomingHandler(BackgroundListner target)
        {
            mtarget = new WeakReference<BackgroundListner>(target);
        }


        @Override
        public void handleMessage(Message msg)
        {
            final BackgroundListner target = mtarget.get();

            switch (msg.what)
            {
                case MSG_RECOGNIZER_START_LISTENING:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        // turn off beep sound
                        target.mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
                    }
                    if (!target.mIsListening)
                    {
                        target.mSpeechRecognizer.startListening(target.mSpeechRecognizerIntent);
                        target.mIsListening = true;
                        //Log.d(TAG, "message start listening"); //$NON-NLS-1$
                    }
                    break;

                case MSG_RECOGNIZER_CANCEL:
                    target.mSpeechRecognizer.cancel();
                    target.mIsListening = false;
                    //Log.d(TAG, "message canceled recognizer"); //$NON-NLS-1$
                    break;
            }
        }
    }

    // Count down timer for Jelly Bean work around
    protected CountDownTimer mNoSpeechCountDown = new CountDownTimer(50000, 5000)
    {

        @Override
        public void onTick(long millisUntilFinished)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish()
        {
            mIsCountDownOn = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_CANCEL);
            try
            {
                mServerMessenger.send(message);
                message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {

            }
        }
    };

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mIsCountDownOn)
        {
            mNoSpeechCountDown.cancel();
        }
        if (mSpeechRecognizer != null)
        {
            mSpeechRecognizer.destroy();
        }
    }

    protected class SpeechRecognitionListener implements RecognitionListener
    {

        private static final String TAG = "SpeechService";

        @Override
        public void onBeginningOfSpeech()
        {
            // speech input will be processed, so there is no need for count down anymore
            if (mIsCountDownOn)
            {
              //  mIsCountDownOn = false;
               // mNoSpeechCountDown.cancel();
            }
            //Log.d(TAG, "onBeginingOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onBufferReceived(byte[] buffer)
        {

        }

        @Override
        public void onEndOfSpeech()
        {
            //Log.d(TAG, "onEndOfSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onError(int error)
        {
            if (mIsCountDownOn)
            {
                mIsCountDownOn = false;
                mNoSpeechCountDown.cancel();
            }
            mIsListening = false;
            Message message = Message.obtain(null, MSG_RECOGNIZER_START_LISTENING);
            try
            {
                mServerMessenger.send(message);
            }
            catch (RemoteException e)
            {

            }
            //Log.d(TAG, "error = " + error); //$NON-NLS-1$
        }

        @Override
        public void onEvent(int eventType, Bundle params)
        {

        }

        @Override
        public void onPartialResults(Bundle partialResults)
        {

        }

        @Override
        public void onReadyForSpeech(Bundle params)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                mIsCountDownOn = true;
                mNoSpeechCountDown.start();
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
            }
            Log.d(TAG, "onReadyForSpeech"); //$NON-NLS-1$
        }

        @Override
        public void onResults(Bundle results)
        {
            //Log.d(TAG, "onResults"); //$NON-NLS-1$

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String text = "";

            for (String result : matches) {
                if (result.toLowerCase().equals("one")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",1);
                    sendBroadcast(intnet);

                    break;

                } else if (result.toLowerCase().equals("two")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",2);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("three")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",3);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("four")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",4);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("five")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",5);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("six")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",6);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("seven")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",7);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("eight")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",8);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("nine")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",9);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("ten")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",10);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("eleven")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",11);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("twelve")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",12);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("thirteen")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",13);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("fourteen")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",14);
                    sendBroadcast(intnet);
                    break;
                } else if (result.toLowerCase().equals("fifteen")) {
                    mIsCountDownOn = false;
                    mNoSpeechCountDown.cancel();
                    Intent intnet = new Intent("com.moba.moba.select");
                    intnet.putExtra("selected",15);
                    sendBroadcast(intnet);
                    break;
                }
            }


        }

        @Override
        public void onRmsChanged(float rmsdB)
        {

        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return mServerMessenger.getBinder();

    }
}
