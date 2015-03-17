/*
 * PocketSphinx Continue Recognition Demo
 * Developed by Luis G III
 * e-mail: loiis.x14@gmail.com
 * visit: http://hellospoonpr@gmail.com and get your own HelloSpoon robot!
 * */
package com.qolbotics.pocketsphinx_sample;

import java.io.File;
import java.io.IOException;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity implements RecognitionListener{
	
	SpeechRecognizer recognizer;
	TextView recognizer_state, recognized_word;
	int conteo = 0;
	int permiso_flag=0;
	Handler a = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		recognizer_state = (TextView) findViewById(R.id.textView2);
		recognized_word = (TextView) findViewById(R.id.textView3);
		
		new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(getApplicationContext());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }
        
            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                } else {
                	FireRecognition();
                }
            }
        }.execute(); 
		
	}
	
	@Override
	public void onStop(){
		super.onStop();
		recognizer.removeListener(this);
	}
	
	
	public void FireRecognition(){
		Log.d("Recognition","Recognition Started");
        recognizer_state.setText("Recognition Started!");
        conteo = 0;
		recognizer.stop();
        recognizer.startListening("digits");
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		
	}	

	private void setupRecognizer(File assetsDir) {
	    File modelsDir = new File(assetsDir, "models");
	    recognizer = defaultSetup()
	            .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
	            .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
	            .setRawLogDir(assetsDir).setKeywordThreshold(1e-40f)
	            .getRecognizer();
	    recognizer.addListener(this);

	    
	    File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
	    recognizer.addGrammarSearch("digits", digitsGrammar);
	    
	       }
	
	@Override
	public void onResult(Hypothesis hup) {
		conteo +=1;
		if(conteo==1){
			//recognizer.stop();
			Timer();
		}
	}
	
	public void Timer(){
		 new Thread(new Runnable() {
		        @Override
		        public void run() {
		                try {
		                    Thread.sleep(500);
		                    a.post(new Runnable() {
		                        @Override
		                        public void run() {
		                        	
		                        	FireRecognition();
		                        }
		                    });
		                } catch (Exception e) {
		                }
		            }
		        
		    }).start();
	}

	@Override
	public void onPartialResult(Hypothesis arg0) {
		if(arg0 == null){ return; }
		String comando = arg0.getHypstr();
		recognized_word.setText(comando);
		conteo +=1;
		if(conteo==1){
			conteo = 0;
			Log.d("Res", comando);
			//recognizer.stop();
			Timer();
		}
		
	}

}
