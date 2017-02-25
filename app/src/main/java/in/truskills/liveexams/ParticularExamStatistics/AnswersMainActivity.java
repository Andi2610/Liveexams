package in.truskills.liveexams.ParticularExamStatistics;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

import in.truskills.liveexams.Quiz.AllQuestionsInOneSectionAdapter;
import in.truskills.liveexams.Quiz.MyPageAdapter;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.AnalyticsDatabase;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class AnswersMainActivity extends AppCompatActivity {

    private static final String SOCKET = "socket";
    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE = 1, REQUEST_CODE_FOR_ALL_SUMMARY = 2;
    SharedPreferences quizPrefs;
    long start, end, diff;
    String examId, examName, selectedLanguage, sectionTitle;
    ArrayList<Fragment> fList;
    TextView sectionNameForAnswers, submittedQuestionsForAnswers, reviewedTickedQuestionsForAnswers,reviewedUntickedQuestionsForAnswers, notAttemptedQuestionsForAnswers,clearedQuestionsForAnswers;
    TextView yourTimeText,maximumTimeText,minimumTimeText,correctlyAnsweredText,yourTimeValue,maximumTimeValue,minimumTimeValue,correctlyAnsweredValue;
    ViewPager pager;
    AnalyticsDatabase ob;
    RecyclerView questionsList;
    AllQuestionsInOneSectionAdapter allQuestionsInOneSectionAdapter;
    LinearLayoutManager linearLayoutManager;
    private static final String FORMAT = "%02d:%02d:%02d";
    ArrayList<Integer> arrayForNoOfSections, arrayForQuestions, arrayForOptions, types;
    ArrayList<String> options;
    int mySectionCount = -1, my_section, my_question, myFragmentCount = -1, my_option, questionArray[], noOfSections, num, total, myTime;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        examName=getIntent().getStringExtra("examName");

        getSupportActionBar().setTitle(examName);


        fList=new ArrayList<>();
        sectionNameForAnswers = (TextView) findViewById(R.id.sectionNameForAnswers);
        submittedQuestionsForAnswers = (TextView) findViewById(R.id.submittedQuestionsForAnswers);
        reviewedTickedQuestionsForAnswers = (TextView) findViewById(R.id.reviewedTickedQuestionsForAnswers);
        reviewedUntickedQuestionsForAnswers = (TextView) findViewById(R.id.reviewedUntickedQuestionsForAnswers);
        notAttemptedQuestionsForAnswers = (TextView) findViewById(R.id.notAttemptedQuestionsForAnswers);
        clearedQuestionsForAnswers = (TextView) findViewById(R.id.clearedQuestionsForAnswers);

        yourTimeText = (TextView) findViewById(R.id.yourTimeText);
        yourTimeValue = (TextView) findViewById(R.id.yourTimeValue);
        minimumTimeText = (TextView) findViewById(R.id.minimumTimeText);
        minimumTimeValue = (TextView) findViewById(R.id.minimumTimeValue);
        maximumTimeText = (TextView) findViewById(R.id.maximumTimeText);
        maximumTimeValue = (TextView) findViewById(R.id.maximumTimeValue);
        correctlyAnsweredText = (TextView) findViewById(R.id.correctlyAnsweredText);
        correctlyAnsweredValue = (TextView) findViewById(R.id.correctlyAnsweredValue);


        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionNameForAnswers.setTypeface(tff1);
        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        submittedQuestionsForAnswers.setTypeface(tff2);
        reviewedTickedQuestionsForAnswers.setTypeface(tff2);
        reviewedUntickedQuestionsForAnswers.setTypeface(tff2);
        clearedQuestionsForAnswers.setTypeface(tff2);
        notAttemptedQuestionsForAnswers.setTypeface(tff2);
        yourTimeText.setTypeface(tff2);
        yourTimeValue.setTypeface(tff2);
        minimumTimeText.setTypeface(tff2);
        minimumTimeValue.setTypeface(tff2);
        maximumTimeText.setTypeface(tff2);
        maximumTimeValue.setTypeface(tff2);
        correctlyAnsweredValue.setTypeface(tff2);
        correctlyAnsweredText.setTypeface(tff2);

        noOfSections = getIntent().getIntExtra("noOfSections", 0);
        questionArray = getIntent().getIntArrayExtra("questionArray");

        ob=new AnalyticsDatabase(this);

        formFragmentListForViewPager();

        //Set the view pager adapter..
        pageAdapter= new MyPageAdapter(getSupportFragmentManager(),fList);
        pager = (ViewPager) findViewById(R.id.viewpagerForAnswers);
        pager.setAdapter(pageAdapter);

    }

    public void formFragmentListForViewPager(){
        arrayForNoOfSections = new ArrayList<>();

        for (int i = 0; i < noOfSections; ++i) {
            arrayForNoOfSections.add(i);
        }

        for (int i = 0; i < noOfSections; ++i) {
            my_section = arrayForNoOfSections.get(i);
            num = questionArray[arrayForNoOfSections.get(i)];
            arrayForQuestions = new ArrayList<>();
            for (int j = 0; j < num; ++j) {
                arrayForQuestions.add(j);
            }
            for (int k = 0; k < num; ++k) {
                my_question=arrayForQuestions.get(k);
                String my_text=ob.getTextOfOneQuestion(my_section,my_question);
                String myAnswer=ob.getStringValuesPerQuestion(my_section,my_question,AnalyticsDatabase.FinalAnswerId);
                String correctAnswer=ob.getStringValuesPerQuestion(my_section,my_question,AnalyticsDatabase.RightAnswer);

                int numOp=ob.getNoOfOptionsInOneQuestion(my_section,my_question);
                arrayForOptions=new ArrayList<>();
                for(int p=0;p<numOp;++p){
                    arrayForOptions.add(p);
                }
                options=new ArrayList<>();
                for (int s=0;s<numOp;++s){
                    my_option=arrayForOptions.get(s);
                    String my_option_text=ob.getTextOfOneOption(my_section,my_question,my_option);
                    options.add(my_option_text);
                }
                fList.add(MyFragmentForAnswers.newInstance(my_text, options,myAnswer,correctAnswer));
            }
        }

    }
}
