package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.DataChannel;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

public class InitialInfo extends AppCompatActivity {

    String date, startTime, rank,endTime, score, attempts, totalMarks, duration, examName, bestScore, averageScore, totalStudents,myUrl;
    int noOfSections, questionArray[], totalQuestions = 0;
    Button answerKeyButton;
    TextView dateText, dateValue, startTimeText, startTimeValue, endTimeText, endTimeValue, totalMarksText, totalMarksValue, bestScoreValue, bestScoreText, averageScoreValue, averageScoreText;
    TextView myRank, totalRank, rankText, myScore, totalScore, scoreText, myPercentile, totalPercentile, percentileText, myAttempt, totalAttempt, attemptText;
    RequestQueue requestQueue;
    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        h=new Handler();


        date = getIntent().getStringExtra("date");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        score = getIntent().getStringExtra("score");
        attempts = getIntent().getStringExtra("attempts");
        totalMarks = getIntent().getStringExtra("totalMarks");
        duration = getIntent().getStringExtra("duration");
        examName = getIntent().getStringExtra("examName");
        noOfSections = getIntent().getIntExtra("noOfSections", 0);
        bestScore = getIntent().getStringExtra("bestScore");
        averageScore = getIntent().getStringExtra("averageScore");
        totalStudents = getIntent().getStringExtra("totalStudents");
        rank = getIntent().getStringExtra("rank");
        myUrl = getIntent().getStringExtra("url");
        questionArray = new int[noOfSections];
        questionArray = getIntent().getIntArrayExtra("questionArray");
        Log.d("myLengthInitialInfo=", questionArray.length + "");

        for (int i = 0; i < noOfSections; ++i) {
            totalQuestions = totalQuestions + questionArray[i];
        }


        getSupportActionBar().setTitle(examName);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);


        answerKeyButton = (Button) findViewById(R.id.answerKeyButton);
        dateText = (TextView) findViewById(R.id.dateText);
        dateValue = (TextView) findViewById(R.id.dateValue);
        startTimeText = (TextView) findViewById(R.id.startTimeText);
        startTimeValue = (TextView) findViewById(R.id.startTimeValue);
        endTimeText = (TextView) findViewById(R.id.endTimeText);
        endTimeValue = (TextView) findViewById(R.id.endTimeValue);
        totalMarksText = (TextView) findViewById(R.id.totalMarksText);
        totalMarksValue = (TextView) findViewById(R.id.totalMarksValue);
        rankText = (TextView) findViewById(R.id.rankText);
        myRank = (TextView) findViewById(R.id.myRank);
        totalRank = (TextView) findViewById(R.id.totalRank);
        myScore = (TextView) findViewById(R.id.myScore);
        totalScore = (TextView) findViewById(R.id.totalScore);
        scoreText = (TextView) findViewById(R.id.scoreText);
        myPercentile = (TextView) findViewById(R.id.myPercentile);
        totalPercentile = (TextView) findViewById(R.id.totalPercentile);
        percentileText = (TextView) findViewById(R.id.percentileText);
        myAttempt = (TextView) findViewById(R.id.myAttempt);
        totalAttempt = (TextView) findViewById(R.id.totalAttempt);
        attemptText = (TextView) findViewById(R.id.attemptText);
        bestScoreValue = (TextView) findViewById(R.id.bestScoreValue);
        bestScoreText = (TextView) findViewById(R.id.bestScoreText);
        averageScoreText = (TextView) findViewById(R.id.averageScoreText);
        averageScoreValue = (TextView) findViewById(R.id.averageScoreValue);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
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
        totalAttempt.setTypeface(tff1);
        attemptText.setTypeface(tff1);
        totalPercentile.setTypeface(tff1);
        percentileText.setTypeface(tff1);
        bestScoreText.setTypeface(tff1);
        averageScoreText.setTypeface(tff1);

        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        answerKeyButton.setTypeface(tff2);
        myRank.setTypeface(tff2);
        myScore.setTypeface(tff2);
        myAttempt.setTypeface(tff2);
        myPercentile.setTypeface(tff2);
        bestScoreValue.setTypeface(tff2);
        averageScoreValue.setTypeface(tff2);

        answerKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startMyActivity();
            }
        });

        dateValue.setText(date);
        startTimeValue.setText(startTime);
        endTimeValue.setText(endTime);
        totalMarksValue.setText(totalMarks);

        myRank.setText(rank);
        totalRank.setText("/" + totalStudents);

        myScore.setText(score);
        totalScore.setText("/" + totalMarks);

        float myPer=(((Integer.parseInt(totalStudents)-(Integer.parseInt(rank)))/(float)Integer.parseInt(totalStudents))*100);
        Log.d("per", "onCreate: "+myPer);
        String ss = String.format("%.2f", myPer);
        myPercentile.setText(ss+"%le");
        totalPercentile.setText("/100");

        myAttempt.setText(attempts);
        totalAttempt.setText("/" + totalQuestions);

        bestScoreValue.setText(bestScore);

        float tempAS=Float.parseFloat(averageScore);
        String s = String.format("%.2f", tempAS);
        averageScoreValue.setText(s);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void startMyActivity(){
        Intent intent = new Intent(InitialInfo.this, AnswersMainActivity.class);
        intent.putExtra("examName", examName);
        intent.putExtra("noOfSections", noOfSections);
        intent.putExtra("questionArray", questionArray);
        intent.putExtra("url", myUrl);
        intent.putExtra("totalStudents", totalStudents);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
