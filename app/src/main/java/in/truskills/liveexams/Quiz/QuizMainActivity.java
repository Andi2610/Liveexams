package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.MySql;
import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

//This is the quiz main activity in which my fragment is loaded..

public class QuizMainActivity extends AppCompatActivity {

    //Declare the variables..
    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE=1;
    SharedPreferences quizPrefs;
    int s,q;
    int questionArray[], languageArray[][], myOptionsArray[][], fragmentIndex[][], sectionIndex, questionIndex;
    String textArray[][], optionArray[][][], sectionTitle;
    HashMap<String, String> map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11;
    HashMap<String, Integer> mapper;
    int noOfQuestions, noOfExamName, noOfLanguage, noOfOption, noOfSections, num, fi = -1;
    RequestQueue requestQueue;
    String url, success, response, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri;
    String examId, name, selectedLanguage;
    List<Fragment> fList;
    TextView sectionName, submittedQuestions, reviewedQuestions, notAttemptedQuestions;
    Button submitButton, reviewButton, clearButton;
    ViewPager pager;
    MySql ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        Log.d("here", "in onCreate");
        quizPrefs=getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get intent variables..
        examId = getIntent().getStringExtra("examId");
        name = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");

        getSupportActionBar().setTitle(name);

        fList = new ArrayList<>();
        sectionName = (TextView) findViewById(R.id.sectionName);
        submittedQuestions = (TextView) findViewById(R.id.submittedQuestions);
        reviewedQuestions = (TextView) findViewById(R.id.reviewedQuestions);
        notAttemptedQuestions = (TextView) findViewById(R.id.notAttemptedQuestions);
        submitButton = (Button) findViewById(R.id.submitButton);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        submittedQuestions.setText("0");
        reviewedQuestions.setText("0");
        notAttemptedQuestions.setText("30");

        ob = new MySql(QuizMainActivity.this);

        //CONNECT TO SOCKET..

        //Api to be connected to get the question paper..
        url = VariablesDefined.api + "questionPaper/" + examId;
        //Make the request..
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                Log.d("here", "in response");

                Log.d("myQuestionPaper", "result=" + result.toString() + "");
                try {
                    //Parse the result..
                    map1 = QuestionPaperParser.resultParser(result);
                    //Get success..
                    success = map1.get("success");
                    if (success.equals("true")) {
                        //Get response..
                        response = map1.get("response");

                        //Parse response..
                        map2 = QuestionPaperParser.responseParser(response);

                        //Get Paperset..
                        Paperset = map2.get("Paperset");

                        //Parse Paperset..
                        map3 = QuestionPaperParser.PapersetParser(Paperset);

                        //Get Sections..
                        Sections = map3.get("Sections");

                        //Parse Sections..
                        map4 = QuestionPaperParser.SectionsParser(Sections);

                        //Get Section..
                        Section = map4.get("Section");

                        //Get no. of sections..
                        noOfSections = QuestionPaperParser.getNoOfSections(Section);

                        //Initialize arrays..
                        questionArray = new int[noOfSections];
                        languageArray = new int[noOfSections][];
                        optionArray = new String[noOfSections][][];
                        textArray = new String[noOfSections][];
                        fragmentIndex = new int[noOfSections][];
                        myOptionsArray = new int[noOfSections][];

                        //Loop through all the sections..
                        for (int i = 0; i < noOfSections; ++i) {

                            //Parse one section..
                            map5 = QuestionPaperParser.SectionParser(Section, i);

                            //Get it's variables..
                            SectionQuestions = map5.get("SectionQuestions");
                            AttributesOfSection = map5.get("Attributes");

                            //Parse one section attributes..
                            map6 = QuestionPaperParser.getAttributesOfSection(AttributesOfSection);

                            //Get it's variables..
                            String name = map6.get("Name");

                            //Set in database..
                            ob.setValuesPerSection(i, name);

                            //Parse one section SectionQuestions..
                            map7 = QuestionPaperParser.SectionQuestionsParser(SectionQuestions);

                            //Get it's variables..
                            Question = map7.get("Question");

                            //Get no. of questions in one section..
                            noOfQuestions = QuestionPaperParser.getNoOfQuestionInOneSection(Question);

                            //Set value questionArray[i]=noOfQuestions in section i.
                            questionArray[i] = noOfQuestions;

                            //Initialise the arrays as array[noOfSections][noOfQuestions]..
                            textArray[i] = new String[noOfQuestions];
                            fragmentIndex[i] = new int[noOfQuestions];
                            myOptionsArray[i] = new int[noOfQuestions];

                            //Initialise the optionArray[i] as optionArray[noOfSections][noOfQuestions]..
                            optionArray[i] = new String[noOfQuestions][];

                            //Loop through all the questions of one section..
                            for (int j = 0; j < noOfQuestions; ++j) {

                                //Increment fragment index..
                                fi++;
                                //Assign it's value to the array..
                                fragmentIndex[i][j] = fi;

                                //Initialise languageArray[i][] as noOfQuestions in section i.
                                languageArray[i] = new int[noOfQuestions];

                                //Parse one section one Question..
                                map8 = QuestionPaperParser.QuestionParser(Question, j);

                                //Get it's variables..
                                myAskedIn = map8.get("AskedIn");
                                myLanguage = map8.get("Language");

                                //Parse one section one question askedIn..
                                map9 = QuestionPaperParser.AskedInParser(myAskedIn);

                                //Get it's variables..
                                myExamName = map9.get("ExamName");
                                myYear = map9.get("Year");

                                //Get no. of Exam names in which the question has been asked..
                                noOfExamName = QuestionPaperParser.getLengthOfExamName(myExamName);

                                //Loop through the entire exam and year array..
                                for (int k = 0; k < noOfExamName; ++k) {
                                    //Get exam name one by one..
                                    nm = QuestionPaperParser.getExamNamesOfOneQuestion(myExamName, k);
                                    //Get Year one by one..
                                    nmm = QuestionPaperParser.getYearsOfOneQuestion(myYear, k);
                                }

                                //Get length of language array of one question of one section..
                                noOfLanguage = QuestionPaperParser.getLengthOfLanguageOfOneQuestion(myLanguage);

                                //Get index of the language array which has to get parsed..
                                int index = QuestionPaperParser.getIndex(selectedLanguage, myLanguage);
                                if (index == -1) {
                                    //Language not found..
                                    Log.d("debug", "notFound");
                                } else {
                                    //Parse the desired index jsonObject og the language array..
                                    map10 = QuestionPaperParser.LanguageParser(myLanguage, index);
                                }

                                //Get it's variables..
                                myQuestionText = map10.get("QuestionText");
                                myOptions = map10.get("Options");

                                //Get question text to be displayed..
                                text = QuestionPaperParser.getQuestionText(myQuestionText);

                                //Set value of textArray[i][j] as text of question j of section i.
                                textArray[i][j] = text;

                                //Parse Options to get Option array..
                                myOption = QuestionPaperParser.OptionsParser(myOptions);

                                //Get length of option array..
                                noOfOption = QuestionPaperParser.getLengthOfOptionArray(myOption);

                                //Initialise the optionArray[i][j] as optionArray[noOfSections][noOfQuestions][noOfOptions]..
                                myOptionsArray[i][j] = noOfOption;

                                //Initialise optionArray[i] as optionArray of particular option of a particular question of a particular section..
                                optionArray[i][j] = new String[noOfOption];

                                //Loop through entire option array..
                                for (int p = 0; p < noOfOption; ++p) {

                                    //Parse Option Array at the desirex index to get one option..
                                    myOp = QuestionPaperParser.OptionParser(myOption, p);

                                    //Parse one option..
                                    map11 = QuestionPaperParser.oneOptionParser(myOp);

                                    //Get Attributes of one option..
                                    myAt = map11.get("Attributes");

                                    //Parse Attributes of one option..
                                    myAttri = QuestionPaperParser.getAttributesOfOneOption(myAt);

                                    //Assign value to optionArray as option value of a particular option of a question of a section..
                                    optionArray[i][j][p] = map11.get("_");

                                    Log.d("debug", "option " + i + " " + j + " " + p + " = " + optionArray[i][j][p]);
                                }
                            }
                        }

                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Back to main thread..
                                afterResponse();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //If connection couldn't be made..
                Log.d("response", error.getMessage());
                Toast.makeText(QuizMainActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void afterResponse(){

        Log.d("here","inAfterResponse");

        ArrayList<String> options;
        for (int i = 0; i < noOfSections; ++i) {
            for (int j = 0; j < questionArray[i]; ++j) {
                options = new ArrayList<String>();
                for (int p = 0; p < myOptionsArray[i][j]; ++p) {
                    options.add(optionArray[i][j][p]);
                }
                //Add new instance of the fragment for every question..
                fList.add(MyFragment.newInstance(textArray[i][j], options, examId));
            }
        }

        //Set the view pager adapter..
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fList);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        //Initially when first page is displayed..
        HashMap<String, String> map = ob.getValuesPerSection(0);
        sectionTitle = map.get("SectionName");
        sectionName.setText(sectionTitle);
        //Whenever user swipes a screen..
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d("position", position + "");
                mapper = QuestionPaperParser.getSectionAndQuestion(position, fragmentIndex);
                sectionIndex = mapper.get("SectionIndex");
                questionIndex = mapper.get("QuestionIndex");
                HashMap<String, String> map = ob.getValuesPerSection(sectionIndex);
                sectionTitle = map.get("SectionName");
                sectionName.setText(sectionTitle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        sectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=pager.getCurrentItem();
                HashMap<String,Integer> map=QuestionPaperParser.getSectionAndQuestion(num,fragmentIndex);
                s=map.get("SectionIndex");
                q=map.get("QuestionIndex");
                SharedPreferences.Editor e=quizPrefs.edit();
                e.putInt("mySectionIndex",s);
                e.putInt("myQuestionIndex",q);
                e.apply();
                Intent i =new Intent(QuizMainActivity.this,SectionNamesDisplay.class);
                i.putExtra("noOfSections",noOfSections);
                i.putExtra("currentSection",s);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if(data!=null){
                int myS=data.getIntExtra("message",quizPrefs.getInt("mySectionIndex",0));
                int n=QuestionPaperParser.getFragmentIndex(myS,0,fragmentIndex);
                if(myS==quizPrefs.getInt("mySectionIndex",0)){
                    SharedPreferences.Editor e=quizPrefs.edit();
                    e.clear();
                    e.apply();
                }else{
                    pager.setCurrentItem(n);
                }
            }
            else{
                SharedPreferences.Editor e=quizPrefs.edit();
                e.clear();
                e.apply();
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
        switch (item.getItemId()){
            case R.id.rulesIcon:
                Intent i=new Intent(QuizMainActivity.this,AllSectionsSummary.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        int ss=quizPrefs.getInt("mySectionIndex",-1);
        int qq=quizPrefs.getInt("myQuestionIndex",-1);
        if(ss==-1&&qq==-1)
            super.onBackPressed();
        else{
            int n=QuestionPaperParser.getFragmentIndex(ss,qq,fragmentIndex);
            pager.setCurrentItem(n);
            SharedPreferences.Editor e=quizPrefs.edit();
            e.putInt("mySectionIndex",-1);
            e.putInt("myQuestionIndex",-1);
            e.apply();
        }
    }

    //Adapter for view pager..
    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ob.deleteMyTable();
        SharedPreferences.Editor e=quizPrefs.edit();
        e.clear();
        e.apply();
    }
}
