package com.sidhero.call_log;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView sentiment;
    ImageButton imageButton;
    Button button;
    String Sentiment;
    @SuppressLint("StaticFieldLeak")
    private class AskWatsonTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... TextToAnalysee) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    sentiment.setText("Sentiment running");
                }
            });
           NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                    "2018-03-29",
                    "51237b9d-0525-463b-a02c-6675799152e2",
                    "FUfvlSSuboaA");

            String url = "https://gateway.watsonplatform.net/natural-language-understanding/api";

            List<String> targets = new ArrayList<>();
            targets.add((String) textView.getText());

            SentimentOptions sentiment = new SentimentOptions.Builder()
                    .targets(targets)
                    .build();

            SentimentResult sentimentResult=new SentimentResult();

            Features features = new Features.Builder()
                    .sentiment(sentiment)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .url(url)
                    .features(features)
                    .build();
            AnalysisResults Sentiment = service
                    .analyze(parameters)
                    .execute();
            return Sentiment.getSentiment().getDocument().getLabel();
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            sentiment.setText("The Sentiment for the Speech : "+result);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.textView);
        imageButton=(ImageButton)findViewById(R.id.imageButton);
        button=(Button)findViewById(R.id.button);
        sentiment=(TextView)findViewById(R.id.sentiment);

        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                sentiment.setText("Sentimient to be checked For The Speech");
                AskWatsonTask task=new AskWatsonTask();
                task.execute(new String[]{});
                button.setVisibility(View.INVISIBLE);
                imageButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void getText(View view) {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,10);
        }
        else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    imageButton.setVisibility(View.INVISIBLE);
                    textView.setText(result.get(0));
                    button.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
