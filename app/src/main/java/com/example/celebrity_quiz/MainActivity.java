package com.example.celebrity_quiz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs=new ArrayList<String>();
    ArrayList<String> celebNames=new ArrayList<String>();
    TextView scoretxt;
    int numberquestions=0;
    int chosen=0;
    int score=0;

String[] answers=new String[4];
int locationcorrect=0;
    ImageView imageView;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebchosen(View view){


        if (view.getTag().toString().equals(Integer.toString(locationcorrect))) {
            Toast.makeText(getApplicationContext(),"Correct",Toast.LENGTH_SHORT).show();
            score++;
        }

        else{
            Toast.makeText(getApplicationContext(),"Wrong answer\n  Correct= "+celebNames.get(chosen),Toast.LENGTH_SHORT).show();
        }
        numberquestions++;
        scoretxt.setText(Integer.toString(score) +"/"+Integer.toString(numberquestions));
        newquestion();
    }

public class imagedown extends AsyncTask <String,Void, Bitmap>{

    @Override
    protected Bitmap doInBackground(String... urls) {
        try{
            URL url =new URL(urls[0]);

            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();

            httpURLConnection.connect();
            InputStream inputStream=httpURLConnection.getInputStream();

            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);

            return bitmap;

        }catch (Exception e){
            e.printStackTrace();

        return null;
        }
    }
}

    public class download extends AsyncTask<String, Void , String>{

        @Override
        protected String doInBackground(String... urls) {
           String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url= new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream =urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);

                   int data =reader.read();
                while (data!=-1){
                    char current=(char)data;
                    result+=current;
                    data =reader.read();
                }
                return result;

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    public void newquestion(){
        try {
            Random random = new Random();
            chosen = random.nextInt(celebURLs.size());

            imagedown imagedown = new imagedown();

            Bitmap celebimage = imagedown.execute(celebURLs.get(chosen)).get();

            imageView.setImageBitmap(celebimage);

            locationcorrect = random.nextInt(4);

            int incorrectanswer;

            for (int i = 0; i < 4; i++) {
                if (i == locationcorrect) {
                    answers[i] = celebNames.get(chosen);
                } else {
                    incorrectanswer = random.nextInt(celebURLs.size());


                    while (incorrectanswer == chosen) {
                        incorrectanswer = random.nextInt(celebURLs.size());
                    }
                    answers[i] = celebNames.get(incorrectanswer);
                }

            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=findViewById(R.id.imageView);

        button0=findViewById(R.id.button0);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        scoretxt=findViewById(R.id.scoretxt);

        download task =new download();
        String result= null;




        try{
            result=task.execute("http://www.posh24.se/kandisar").get();

            String[] splt=result.split("<div class=\"facebookContainer\">");

            Pattern pattern=Pattern.compile("img src=\"(.*?)\"");
            Matcher matcher=pattern.matcher(splt[0]);

            while(matcher.find()){
                celebURLs.add(matcher.group(1));
            }
            pattern=Pattern.compile("alt=\"(.*?)\"");
            matcher=pattern.matcher(splt[0]);

            while(matcher.find()){
                celebNames.add(matcher.group(1));
            }
            newquestion();


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
