package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.webrtc.DataChannel;

import in.truskills.liveexams.R;

public class InitialInfo extends AppCompatActivity {

    String date,startTime,endTime,score,attempts,totalMarks,duration,examName,bestScore,averageScore,totalStudents;
    int noOfSections,questionArray[];
    Button answerKeyButton;
    TextView dateText,dateValue,startTimeText,startTimeValue,endTimeText,endTimeValue,totalMarksText,totalMarksValue;
    TextView myRank,totalRank,rankText,myScore,totalScore,scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        date=getIntent().getStringExtra("date");
        startTime=getIntent().getStringExtra("startTime");
        endTime=getIntent().getStringExtra("endTime");
        score=getIntent().getStringExtra("score");
        attempts=getIntent().getStringExtra("attempts");
        totalMarks=getIntent().getStringExtra("totalMarks");
        duration=getIntent().getStringExtra("duration");
        examName=getIntent().getStringExtra("examName");
        noOfSections=getIntent().getIntExtra("noOfSections",0);
        bestScore=getIntent().getStringExtra("bestScore");
        averageScore=getIntent().getStringExtra("averageScore");
        totalStudents=getIntent().getStringExtra("totalStudents");
        questionArray=new int[noOfSections];
        questionArray=getIntent().getIntArrayExtra("questionArray");
        Log.d("myLengthInitialInfo=",questionArray.length+"");


        getSupportActionBar().setTitle(examName);

        answerKeyButton=(Button)findViewById(R.id.answerKeyButton);
        dateText=(TextView)findViewById(R.id.dateText);
        dateValue=(TextView)findViewById(R.id.dateValue);
        startTimeText=(TextView)findViewById(R.id.startTimeText);
        startTimeValue=(TextView)findViewById(R.id.startTimeValue);
        endTimeText=(TextView)findViewById(R.id.endTimeText);
        endTimeValue=(TextView)findViewById(R.id.endTimeValue);
        totalMarksText=(TextView)findViewById(R.id.totalMarksText);
        totalMarksValue=(TextView)findViewById(R.id.totalMarksValue);
        rankText=(TextView)findViewById(R.id.rankText);
        myRank=(TextView)findViewById(R.id.myRank);
        totalRank=(TextView)findViewById(R.id.totalRank);
        myScore=(TextView)findViewById(R.id.myScore);
        totalScore=(TextView)findViewById(R.id.totalScore);
        scoreText=(TextView)findViewById(R.id.scoreText);

        Typeface tff1=Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        dateText.setTypeface(tff1);
        dateValue.setTypeface(tff1);
        startTimeText.setTypeface(tff1);
        startTimeValue.setTypeface(tff1);
        endTimeText.setTypeface(tff1);
        endTimeValue.setTypeface(tff1);
        totalMarksText.setTypeface(tff1);
        totalMarksValue.setTypeface(tff1);
        rankText.setTypeface(tff1);
        totalRank.setTypeface(tff1);
        totalScore.setTypeface(tff1);
        scoreText.setTypeface(tff1);

        Typeface tff2=Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        answerKeyButton.setTypeface(tff2);
        myRank.setTypeface(tff2);
        myScore.setTypeface(tff2);

        answerKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(InitialInfo.this,AnswersMainActivity.class);
                intent.putExtra("examName",examName);
                intent.putExtra("noOfSections",noOfSections);
                intent.putExtra("questionArray",questionArray);
                startActivity(intent);
            }
        });

        dateValue.setText(date);
        startTimeValue.setText(startTime);
        endTimeValue.setText(endTime);
        totalMarksValue.setText(totalMarks);

        myRank.setText(score);
        totalRank.setText("/"+totalMarks);

        myScore.setText(score);
        totalScore.setText("/"+totalMarks);
    }
}
