package in.truskills.liveexams.ParticularExam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import in.truskills.liveexams.Quiz.QuestionPaperLoad;
import in.truskills.liveexams.R;

public class RulesBeforeQuiz extends Activity {


    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8,tv9,tv10,tv11;
    Button continueButton,exitButton;
    String examId,paperName,selectedLanguage,myDate,myUrl,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rules_before_quiz);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        tv9 = (TextView) findViewById(R.id.tv9);
        tv10 = (TextView) findViewById(R.id.tv10);
        tv11 = (TextView) findViewById(R.id.tv11);
        continueButton = (Button) findViewById(R.id.continueButton);
        exitButton = (Button) findViewById(R.id.exitButton);

        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        tv1.setTypeface(tff);
        tv2.setTypeface(tff);
        tv3.setTypeface(tff);
        tv4.setTypeface(tff);
        tv5.setTypeface(tff);
        tv6.setTypeface(tff);
        tv7.setTypeface(tff);
        tv8.setTypeface(tff);
        tv9.setTypeface(tff);
        tv10.setTypeface(tff);
        tv11.setTypeface(tff);
        continueButton.setTypeface(tff);
        exitButton.setTypeface(tff);

        examId = getIntent().getStringExtra("examId");
        paperName = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");
        myDate = getIntent().getStringExtra("date");
        myUrl = getIntent().getStringExtra("url");
        name = getIntent().getStringExtra("name");

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RulesBeforeQuiz.this, QuestionPaperLoad.class);
                i.putExtra("examId", examId);
                i.putExtra("name", name);
                i.putExtra("language", selectedLanguage);
                i.putExtra("date",myDate);
                i.putExtra("url",myUrl);
                startActivity(i);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    protected void onPause()
    {
        super.onPause();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
}
