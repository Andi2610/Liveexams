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

import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

public class QuizMainActivity extends AppCompatActivity {

    MyPageAdapter pageAdapter;
    int questionArray[], languageArray[][],myOptionsArray[][];
    String textArray[][], optionArray[][][];
    HashMap<String, String> map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11;
    int noOfQuestions, noOfExamName, noOfLanguage, noOfOption, noOfSections;
    RequestQueue requestQueue;
    String url, success, response, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myAttributesOfLanguage, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri, nameOfLanguage;
    String examId, name, selectedLanguage;
    List<Fragment> fList;
    TextView sectionName, submittedQuestions, reviewedQuestions, notAttemptedQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        examId = getIntent().getStringExtra("examId");
        name = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");
        fList = new ArrayList<>();
        sectionName = (TextView) findViewById(R.id.sectionName);
        submittedQuestions = (TextView) findViewById(R.id.submittedQuestions);
        reviewedQuestions = (TextView) findViewById(R.id.reviewedQuestions);
        notAttemptedQuestions = (TextView) findViewById(R.id.notAttemptedQuestions);

//        sectionName.setText("PHYSICS");
        submittedQuestions.setText("0");
        reviewedQuestions.setText("0");
        notAttemptedQuestions.setText("30");

        getSupportActionBar().setTitle(name);

        url = VariablesDefined.api + "questionPaper/" + examId;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.d("myQuestionPaper", "result=" + result.toString() + "");
                try {
                    map1 = QuestionPaperParser.resultParser(result);
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
                        myOptionsArray = new int[noOfSections][];

                        //Loop through all the sections..
                        for (int i = 0; i < noOfSections; ++i) {

                            //Parse one section..
                            map5 = QuestionPaperParser.SectionParser(Section, i);
                            //Get SectionQuestions..
                            SectionQuestions = map5.get("SectionQuestions");
                            //Get AttributesOfSection..
                            AttributesOfSection = map5.get("Attributes");
                            //Parse one section attributes..
                            map6 = QuestionPaperParser.getAttributesOfSection(AttributesOfSection);
                            //Parse one section SectionQuestions..
                            map7 = QuestionPaperParser.SectionQuestionsParser(SectionQuestions);
                            //Get one section Question..
                            Question = map7.get("Question");
                            //Get no. of questions in one section..
                            noOfQuestions = QuestionPaperParser.getNoOfQuestionInOneSection(Question);
                            //Set value questionArray[i]=noOfQuestions in section i.
                            questionArray[i] = noOfQuestions;

                            //Initialise the textArray[i] as textArray[noOfSections][noOfQuestions]..
                            textArray[i] = new String[noOfQuestions];
                            myOptionsArray[i] = new int[noOfQuestions];

                            //Initialise the optionArray[i] as optionArray[noOfSections][noOfQuestions]..
                            optionArray[i] = new String[noOfQuestions][];

                            //Loop through all the questions of one section..
                            for (int j = 0; j < noOfQuestions; ++j) {

                                //Initialise languageArray[i][] as noOfQuestions in section i.
                                languageArray[i] = new int[noOfQuestions];

                                //Parse one section one Question..
                                map8 = QuestionPaperParser.QuestionParser(Question, j);
                                //Get AskedIn..
                                myAskedIn = map8.get("AskedIn");
                                //Parse one section one question askedIn..
                                map9 = QuestionPaperParser.AskedInParser(myAskedIn);
                                //Get ExamName..
                                myExamName = map9.get("ExamName");
                                //Get Year..
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

                                //Get Language..
                                myLanguage = map8.get("Language");

                                //Get length of language array of one question of one section..
                                noOfLanguage = QuestionPaperParser.getLengthOfLanguageOfOneQuestion(myLanguage);

                                //Get index of the language array which has to get parsed..
                                int index = QuestionPaperParser.getIndex(selectedLanguage, myLanguage);

                                if (index == -1) {
                                    Log.d("debug", "notFound");
                                } else {
                                    //Parse the desired index jsonObject og the language array..
                                    map10 = QuestionPaperParser.LanguageParser(myLanguage, index);
                                }

                                //Get QuestionText array of that question in that language..
                                myQuestionText = map10.get("QuestionText");
                                //Get Options of the question..
                                myOptions = map10.get("Options");
                                //Get question text to be displayed..
                                text = QuestionPaperParser.getQuestionText(myQuestionText);
                                //Set value of textArray[i][j] as text of question j of section i.
                                textArray[i][j] = text;
                                //Parse Options to get Option array..
                                myOption = QuestionPaperParser.OptionsParser(myOptions);
                                //Get length of option array..
                                noOfOption = QuestionPaperParser.getLengthOfOptionArray(myOption);
                                //Loop through entire option array..

                                //Initialise the optionArray[i][j] as optionArray[noOfSections][noOfQuestions][noOfOptions]..
                                myOptionsArray[i][j]=noOfOption;

                                //Initialise optionArray[i] as optionArray of particular option of a particular question of a particular section..
                                optionArray[i][j] = new String[noOfOption];

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
                                    Log.d("super",optionArray[i][j][p]+" "+i+" "+j+" "+p);
                                }
                            }
                        }

                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList<String> options;
                                for (int i = 0; i < noOfSections; ++i) {
                                    Log.d("super","noOfQuestionsinSection"+i+"="+ questionArray[i]);
                                    for (int j = 0; j < questionArray[i]; ++j) {
                                        Log.d("super", i+"-"+j+"-"+textArray[i][j] );
                                        Log.d("super","noOfOptions"+i+"-"+j+"-"+ myOptionsArray[i][j]);
                                        options=new ArrayList<String>();
                                        for (int p = 0; p <myOptionsArray[i][j] ; ++p) {
                                            Log.d("super", optionArray[i][j][p]+"");
                                            options.add(optionArray[i][j][p]);
                                        }
                                        fList.add(MyFragment.newInstance(textArray[i][j],options));
                                    }
                                }
                                pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fList);
                                ViewPager pager =
                                        (ViewPager) findViewById(R.id.viewpager);
                                pager.setAdapter(pageAdapter);
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
                Log.d("response", error.getMessage());
                Toast.makeText(QuizMainActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);
    }

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
}
