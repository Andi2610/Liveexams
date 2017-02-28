package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

import in.truskills.liveexams.Quiz.AllQuestionsInOneSectionAdapter;
import in.truskills.liveexams.Quiz.MyPageAdapter;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.AnalyticsDatabase;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class AnswersMainActivity extends AppCompatActivity implements setValueOfPager{

    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE = 1, REQUEST_CODE_FOR_ALL_SUMMARY = 2;
    String examName, SectionIndex;
    ArrayList<Fragment> fList;
    TextView sectionNameForAnswers, submittedQuestionsForAnswers, reviewedTickedQuestionsForAnswers,reviewedUntickedQuestionsForAnswers, notAttemptedQuestionsForAnswers,clearedQuestionsForAnswers;
    TextView yourTimeText,maximumTimeText,minimumTimeText,correctlyAnsweredText,yourTimeValue,maximumTimeValue,minimumTimeValue,correctlyAnsweredValue;
    ViewPager pager;
    AnalyticsDatabase ob;
    RecyclerView questionsList;
    AllQuestionsInOneSectionAdapterForAnswers allQuestionsInOneSectionAdapterForAnswers;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String> options;
    String minimumTime,maximumTime,yourTime,correctlyAnsweredBy;
    int my_section, my_question, my_option, questionArray[], noOfSections, num;
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
        questionsList=(RecyclerView) findViewById(R.id.questionsListForAnswers);

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

        linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        questionsList.setLayoutManager(linearLayoutManager);
        questionsList.setItemAnimator(new DefaultItemAnimator());

        noOfSections = getIntent().getIntExtra("noOfSections", 0);
        questionArray = getIntent().getIntArrayExtra("questionArray");
        Log.d("myLength=",questionArray.length+"");

        ob=new AnalyticsDatabase(this);

        formFragmentListForViewPager();

        //Set the view pager adapter..
        pageAdapter= new MyPageAdapter(getSupportFragmentManager(),fList);
        pager = (ViewPager) findViewById(R.id.viewpagerForAnswers);
        pager.setAdapter(pageAdapter);

        forQuiz();

    }

    public void forQuiz(){


        setValuesForHeader(0);

        setValuesForQuestionList(0);

        setValuesForTextViews(0);

        setValuesForFooter(0);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                setValuesForHeader(position);
                setValuesForQuestionList(position);
                setValuesForTextViews(position);
                setValuesForFooter(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        sectionNameForAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = pager.getCurrentItem();
                int sI=Integer.parseInt(ob.getValuesPerQuestionByFragmentIndex(num,AnalyticsDatabase.SectionIndex));
                Intent i = new Intent(AnswersMainActivity.this, SectionNamesDisplayForAnswers.class);
                i.putExtra("sectionIndex", sI);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

    }

    public void formFragmentListForViewPager(){

        for (int i = 0; i < noOfSections; ++i) {
            my_section = i;
            num = questionArray[i];
            for (int k = 0; k < num; ++k) {
                my_question=k;
                String my_text=ob.getTextOfOneQuestion(my_section,my_question);
                String myAnswer=ob.getStringValuesPerQuestion(my_section,my_question,AnalyticsDatabase.FinalAnswerId);
                String correctAnswer=ob.getStringValuesPerQuestion(my_section,my_question,AnalyticsDatabase.RightAnswer);

                int numOp=ob.getNoOfOptionsInOneQuestion(my_section,my_question);
                options=new ArrayList<>();
                for (int s=0;s<numOp;++s){
                    my_option=s;
                    String my_option_text=ob.getTextOfOneOption(my_section,my_question,my_option);
                    options.add(my_option_text);
                }
                fList.add(MyFragmentForAnswers.newInstance(my_text, options,myAnswer,correctAnswer));
            }
        }

    }

    public void setValuesForFooter(int fi){
        minimumTime=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.MinimumTime);
        maximumTime=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.MaximumTime);
        yourTime=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.TimeSpent);
        correctlyAnsweredBy=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.RightAnsweredBy);
        minimumTimeValue.setText(minimumTime);
        maximumTimeValue.setText(maximumTime);
        yourTimeValue.setText(yourTime);
        correctlyAnsweredValue.setText(correctlyAnsweredBy);
    }

    public void setValuesForHeader(int fi){
        SectionIndex=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.SectionIndex);
        int si=Integer.parseInt(SectionIndex);
        String name=ob.getValuesPerSection(si,AnalyticsDatabase.SectionName);
        sectionNameForAnswers.setText(name);
    }

    public void setValuesForQuestionList(int fi){
        SectionIndex=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.SectionIndex);
        int si=Integer.parseInt(SectionIndex);
        int qi=Integer.parseInt(ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.QuestionIndex));
        ArrayList<Integer> types=ob.getIntValuesOfEachSection(si,AnalyticsDatabase.QuestionStatus);
        ArrayList<Integer> listOfFi=ob.getIntValuesOfEachSection(si,AnalyticsDatabase.FragmentIndex);
        allQuestionsInOneSectionAdapterForAnswers=new AllQuestionsInOneSectionAdapterForAnswers(listOfFi,AnswersMainActivity.this,qi,types);
        questionsList.setAdapter(allQuestionsInOneSectionAdapterForAnswers);
        allQuestionsInOneSectionAdapterForAnswers.notifyDataSetChanged();
    }

    public void setValuesForTextViews(int fi){
        SectionIndex=ob.getValuesPerQuestionByFragmentIndex(fi,AnalyticsDatabase.SectionIndex);
        int si=Integer.parseInt(SectionIndex);
        ArrayList<Integer> types=ob.getTypes(si);
        submittedQuestionsForAnswers.setText(types.get(0)+"");
        reviewedTickedQuestionsForAnswers.setText(types.get(1)+"");
        reviewedUntickedQuestionsForAnswers.setText(types.get(2)+"");
        clearedQuestionsForAnswers.setText(types.get(3)+"");
        notAttemptedQuestionsForAnswers.setText(types.get(4)+"");
    }

    @Override
    public void SetValue(int pos) {
        pager.setCurrentItem(pos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                int sI = data.getIntExtra("message", 0);
                int fi=Integer.parseInt(ob.getStringValuesPerQuestion(sI,0,AnalyticsDatabase.FragmentIndex));
                pager.setCurrentItem(fi);
            }
        } else if (requestCode == REQUEST_CODE_FOR_ALL_SUMMARY) {
            if (data != null) {
                int jumpTo = data.getIntExtra("jumpTo", 0);
                pager.setCurrentItem(jumpTo);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rules_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rulesIcon:
                prefs=getSharedPreferences("prefs", Context.MODE_PRIVATE);
                Intent i=new Intent(AnswersMainActivity.this,AllSectionsSummaryForAnswers.class);
                startActivityForResult(i,REQUEST_CODE_FOR_ALL_SUMMARY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
