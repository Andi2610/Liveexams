package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SurfaceViewRenderer;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

//This is the quiz main activity in which my fragment is loaded..

/*
* this interface is to call methods of FlashphonerEvents from QuizMainActivity
* */
interface socketFromTeacher {
    void startStreaming(String studentId, String teacherId);

    void stopStreaming();

    void disconnectSession();
}

public class QuizMainActivity extends AppCompatActivity implements setValueOfPager, View.OnClickListener, MyFragmentInterface, socketFromStudent {

    private static final String SOCKET = "socket";
    //Declare the variables..
    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE = 1, REQUEST_CODE_FOR_ALL_SUMMARY = 2;
    SharedPreferences quizPrefs;
    long start, end, diff;
    ArrayList<Integer> types;
    int questionArray[], languageArray[][], myOptionsArray[][], fragmentIndex[][], sectionIndex, questionIndex;
    String textArray[][], optionArray[][][], sectionTitle;
    HashMap<String, String> map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11;
    int noOfQuestions, noOfExamName, noOfLanguage, noOfOption, noOfSections, num, fi = -1;
    RequestQueue requestQueue;
    String url, success, response, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri, section_id, section_max_marks, section_time, section_description, section_rules;
    String questionAttributes;
    String examId, name, selectedLanguage;
    List<Fragment> fList;
    TextView sectionName, submittedQuestions, reviewedQuestions, notAttemptedQuestions, timer, clearedQuestions;
    Button submitButton, reviewButton, clearButton;
    ViewPager pager;
    MySqlDatabase ob;
    ProgressDialog dialog;
    RecyclerView questionsList;
    AllQuestionsInOneSectionAdapter allQuestionsInOneSectionAdapter;
    LinearLayoutManager linearLayoutManager;
    private static final String FORMAT = "%02d:%02d:%02d";
    ArrayList<Integer> arrayForNoOfSections, arrayForQuestions, arrayForOptions;
    ArrayList<String> options;
    int mySectionCount = -1, my_section, my_question, myFragmentCount = -1, my_option;
    Socket socket;
    socketFromTeacher socketfromteacher;

    FlashphonerEvents flashphoner;
    SurfaceViewRenderer extraRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        quizPrefs = getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get intent variables..
        examId = getIntent().getStringExtra("examId");
        name = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");

        getSupportActionBar().setTitle(name);

        fList = new ArrayList<>();
        timer = (TextView) findViewById(R.id.timer);
        sectionName = (TextView) findViewById(R.id.sectionName);
        submittedQuestions = (TextView) findViewById(R.id.submittedQuestions);
        reviewedQuestions = (TextView) findViewById(R.id.reviewedQuestions);
        notAttemptedQuestions = (TextView) findViewById(R.id.notAttemptedQuestions);
        clearedQuestions = (TextView) findViewById(R.id.clearedQuestions);
        submitButton = (Button) findViewById(R.id.submitButton);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        questionsList = (RecyclerView) findViewById(R.id.questionsList);

        ob = new MySqlDatabase(QuizMainActivity.this);

        /*
        * hidden element where student video will be loaded
        * layout/activity_quiz_main.xml:156
        * */
        extraRender = (SurfaceViewRenderer) findViewById(R.id.studentView);
        //to access all methods related to Streaming
        flashphoner = new FlashphonerEvents(this, extraRender);
        //Interface to communicate with Flashphoner class
        socketfromteacher = (socketFromTeacher) flashphoner;
        //CONNECT TO SOCKET..
        initSocketConnection();

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Loading. Please wait...");
        dialog.setIndeterminate(true);
        dialog.show();

        //Api to be connected to get the question paper..
        url = VariablesDefined.api + "questionPaper/" + examId;
        //Make the request..
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    //Parse the result..
                    map1 = QuestionPaperParser.resultParser(result);
                    //Get success..
                    success = map1.get("success");
                    if (success.equals("true")) {

                        dialog.dismiss();

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
                            section_max_marks = map5.get("SectionMaxMarks");
                            section_time = map5.get("SectionTime");
                            section_description = map5.get("SectionDescription");
                            section_rules = map5.get("SectionRules");

                            //Parse one section attributes..
                            map6 = QuestionPaperParser.getAttributesOfSection(AttributesOfSection);

                            //Get it's variables..
                            name = map6.get("Name");
                            section_id = map6.get("id");

                            //Set in database..
                            ob.setValuesPerSection(i);
                            ob.updateValuesPerSection(i, MySqlDatabase.SectionName, name);
                            ob.updateValuesPerSection(i, MySqlDatabase.SectionId, section_id);
                            ob.updateValuesPerSection(i, MySqlDatabase.SectionMaxMarks, section_max_marks);
                            ob.updateValuesPerSection(i, MySqlDatabase.SectionTime, section_time);
                            ob.updateValuesPerSection(i, MySqlDatabase.SectionDescription, section_description);
                            ob.updateValuesPerSection(i, MySqlDatabase.SectionRules, section_rules);

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

                                //Set in database..
                                ob.setValuesPerQuestion(i, j);
                                ob.setValuesForResult(i, j);

                                //Initialise languageArray[i][] as noOfQuestions in section i.
                                languageArray[i] = new int[noOfQuestions];

                                //Parse one section one Question..
                                map8 = QuestionPaperParser.QuestionParser(Question, j);

                                //Get it's variables..
                                myAskedIn = map8.get("AskedIn");
                                myLanguage = map8.get("Language");
                                questionAttributes = map8.get("Attributes");

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

                                HashMap<String, String> map12 = QuestionPaperParser.getAttributesOfQuestion(questionAttributes);

                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionText, text);
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.CorrectAnswer, map12.get("CorrectAnswer"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionCorrectMarks, map12.get("QuestionCorrectMarks"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionIncorrectMarks, map12.get("QuestionIncorrectMarks"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.PassageID, map12.get("PassageID"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionType, map12.get("QuestionType"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionTime, map12.get("QuestionTime"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionDifficultyLevel, map12.get("QuestionDifficultyLevel"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionRelativeTopic, map12.get("QuestionRelativeTopic"));
                                ob.updateValuesPerQuestion(i, j, MySqlDatabase.QuestionId, map12.get("id"));

                                ob.updateValuesForResult(i, j, MySqlDatabase.SectionId, section_id);
                                ob.updateValuesForResult(i, j, MySqlDatabase.QuestionId, map12.get("id"));

                                //Parse Options to get Option array..
                                myOption = QuestionPaperParser.OptionsParser(myOptions);

                                //Get length of option array..
                                noOfOption = QuestionPaperParser.getLengthOfOptionArray(myOption);

                                //Initialise the myPptionsArray[i][j] as noOfOptions in section i question j..
                                myOptionsArray[i][j] = noOfOption;

                                //Initialise optionArray[i] as optionArray of particular option of a particular question of a particular section..
                                optionArray[i][j] = new String[noOfOption];

                                //Loop through entire option array..
                                for (int p = 0; p < noOfOption; ++p) {

                                    //Set in database..
                                    ob.setValuesPerOption(i, j, p);

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

                                    ob.updateValuesPerOption(i, j, p, MySqlDatabase.OptionText, map11.get("_"));
                                    ob.updateValuesPerOption(i, j, p, MySqlDatabase.OptionId, myAttri);

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

                dialog.dismiss();

                //If connection couldn't be made..
                Toast.makeText(QuizMainActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();

                finish();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void afterResponse() {


        new CountDownTimer(10800000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                timer.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                timer.setText("done!");
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizMainActivity.this);
                builder.setMessage("Quiz is over!! Re-directing to Home page");
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            Intent intent = new Intent(QuizMainActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                            startActivity(intent);
                        }
                    }
                });
                t.start();
                AlertDialog alert = builder.create();
                alert.show();
            }
        }.start();

        arrayForNoOfSections = new ArrayList<>();

        for (int i = 0; i < noOfSections; ++i) {
            arrayForNoOfSections.add(i);
        }
        //If shuffling required, then do so..
        Collections.shuffle(arrayForNoOfSections);

        for (int i = 0; i < noOfSections; ++i) {
            //Increase the section serial number..
            mySectionCount++;
            //Getting a section randomly..
            my_section = arrayForNoOfSections.get(i);
            //Set the serial number of this section..
            ob.updateValuesPerSection(my_section, MySqlDatabase.SerialNumber, mySectionCount + "");


            //Getting total no.of questions in that section randomly..
            num = questionArray[arrayForNoOfSections.get(i)];
            arrayForQuestions = new ArrayList<>();
            for (int j = 0; j < num; ++j) {
                arrayForQuestions.add(j);
            }

            //If shuffling required, then do so..
            Collections.shuffle(arrayForQuestions);

            int myQuestionCount = -1;

            for (int k = 0; k < num; ++k) {
                //Increase question serial number..
                myQuestionCount++;
                //Increase fragment serial number..
                myFragmentCount++;
                //Getting a question randomly..
                my_question = arrayForQuestions.get(k);
                //Set the serial number of this question..
                ob.updateValuesPerQuestion(my_section, my_question, MySqlDatabase.SerialNumber, myQuestionCount + "");
                ob.updateValuesPerQuestion(my_section, my_question, MySqlDatabase.FragmentIndex, myFragmentCount + "");
                ob.updateValuesForResult(my_section, my_question, MySqlDatabase.SerialNumber, myQuestionCount + "");

                String my_text = textArray[my_section][my_question];

                int numOp = myOptionsArray[my_section][my_question];

                arrayForOptions = new ArrayList<>();
                for (int p = 0; p < numOp; ++p) {
                    arrayForOptions.add(p);
                }
                //Shuffle if required..
                Collections.shuffle(arrayForOptions);

                options = new ArrayList<>();

                int myOptionCount = -1;

                for (int s = 0; s < numOp; ++s) {
                    //Increase option serial number..
                    myOptionCount++;
                    //Getting an option randomly..
                    my_option = arrayForOptions.get(s);
                    //Set the serial number of this option..
                    ob.updateValuesPerOption(my_section, my_question, my_option, MySqlDatabase.SerialNumber, myOptionCount + "");

                    String my_option_text = optionArray[my_section][my_question][my_option];
                    options.add(my_option_text);
                }
                fList.add(MyFragment.newInstance(my_text, options, examId, my_section, my_question, myFragmentCount));
            }
        }

        //Set the view pager adapter..
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fList);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        pager.setOffscreenPageLimit(++myFragmentCount);
        //Initially when first page is displayed..
        //Get values of the section displayed at first number..

//        ArrayList<Integer> qq=new ArrayList<>();
//        int cnt=1;
//        for(int i=0;i<questionArray[0];++i){
//            qq.add(cnt);
//            ++cnt;
//        }

        //Initially.. fragmentIndex=0;
        //Getting SectionIndex, QuestionIndex and SerialNumber of fragmentIndex=0;
        int sI = ob.getIntValuesPerQuestionByFragmentIndex(0, MySqlDatabase.SectionIndex);
        int qI = ob.getIntValuesPerQuestionByFragmentIndex(0, MySqlDatabase.QuestionIndex);
        Log.d("ResultDetails=", sI + " " + qI);
        ob.updateValuesForResult(sI, qI, MySqlDatabase.ReadStatus, 1 + "");
        String srNo = ob.getStringValuesPerQuestionByFragmentIndex(0, MySqlDatabase.SerialNumber);
        int sn = Integer.parseInt(srNo);

        SharedPreferences.Editor e = quizPrefs.edit();
        e.putInt("previousIndex", 0);
        e.apply();

        Log.d("Index=", quizPrefs.getInt("previousIndex", 0) + "=previous");

        start = System.currentTimeMillis();

        Log.d("time=", start + "=start in" + quizPrefs.getInt("previousIndex", 0));

        ArrayList<Integer> myType = ob.getTypesOfEachSection(sI);

        types = ob.getTypes(sI);

        submittedQuestions.setText(types.get(1) + "");
        reviewedQuestions.setText(types.get(2) + "");
        clearedQuestions.setText(types.get(3) + "");
        notAttemptedQuestions.setText(types.get(0) + "");

        //Disable buttons..
        submitButton.setEnabled(false);
        submitButton.setBackgroundColor(getResources().getColor(R.color.light_grey));
        clearButton.setEnabled(false);
        clearButton.setBackgroundColor(getResources().getColor(R.color.light_grey));


        HashMap<String, String> map = ob.getValuesPerSection(sI);
        sectionTitle = map.get("SectionName");
        sectionName.setText(sectionTitle);

        HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(sI);
        ArrayList<Integer> my_fragment_index_list = new ArrayList<>();
        my_fragment_index_list = my_map.get("FragmentIndexList");

        Log.d("initially", sI + " " + sn + " " + sectionTitle);
        for (int l = 0; l < my_fragment_index_list.size(); ++l) {
            Log.d("initially", my_fragment_index_list.get(l) + "");
        }

        allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        questionsList.setLayoutManager(linearLayoutManager);
        questionsList.setItemAnimator(new DefaultItemAnimator());
        questionsList.setAdapter(allQuestionsInOneSectionAdapter);
        allQuestionsInOneSectionAdapter.notifyDataSetChanged();

        //Whenever user swipes a screen..
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                int SI = ob.getIntValuesPerQuestionByFragmentIndex(position, "SectionIndex");
                int QI = ob.getIntValuesPerQuestionByFragmentIndex(position, "QuestionIndex");

                Log.d("Index=", quizPrefs.getInt("previousIndex", 0) + "=previous");

                end = System.currentTimeMillis();

                Log.d("time=", end + "=end in" + quizPrefs.getInt("previousIndex", 0));

                diff = end - start;
                diff = diff / 1000;

                String myTime = ob.getValuesForResult(SI, QI, "TimeSpent");
                long time = Long.parseLong(myTime);
                time = time + diff;

                ob.updateValuesForResult(SI, QI, "TimeSpent", time + "");

                ob.getAllValues();

                Log.d("time=", diff + "=diff in" + quizPrefs.getInt("previousIndex", 0));

                SharedPreferences.Editor e = quizPrefs.edit();
                e.putInt("previousIndex", position);
                e.apply();

                start = System.currentTimeMillis();

                Log.d("time=", start + "=start in" + quizPrefs.getInt("previousIndex", 0));


                Log.d("Index=", quizPrefs.getInt("previousIndex", 0) + "=new");

                Log.d("state", "here " + position);

                Log.d("ResultDetails=", SI + " " + QI);
                ob.updateValuesForResult(SI, QI, "ReadStatus", 1 + "");
                String srNo = ob.getStringValuesPerQuestionByFragmentIndex(position, "SerialNumber");
                int sn = Integer.parseInt(srNo);

                HashMap<String, String> map = ob.getValuesPerSection(SI);
                sectionTitle = map.get("SectionName");
                sectionName.setText(sectionTitle);

                types = ob.getTypes(SI);

                submittedQuestions.setText(types.get(1) + "");
                reviewedQuestions.setText(types.get(2) + "");
                clearedQuestions.setText(types.get(3) + "");
                notAttemptedQuestions.setText(types.get(0) + "");

                int qType = ob.getTypeOfAQuestion(SI, QI);
                if (qType == -1) {
                    //Disable buttons..
                    submitButton.setEnabled(false);
                    submitButton.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    clearButton.setEnabled(false);
                    clearButton.setBackgroundColor(getResources().getColor(R.color.light_grey));

                } else {
                    //Enable buttons..
                    submitButton.setEnabled(true);
                    submitButton.setBackgroundColor(getResources().getColor(R.color.black));
                    clearButton.setEnabled(true);
                    clearButton.setBackgroundColor(getResources().getColor(R.color.black));
                }


                HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(SI);
                ArrayList<Integer> my_fragment_index_list = new ArrayList<>();
                my_fragment_index_list = my_map.get("FragmentIndexList");

                ArrayList<Integer> myType = ob.getTypesOfEachSection(SI);

                allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
                questionsList.setAdapter(allQuestionsInOneSectionAdapter);
                allQuestionsInOneSectionAdapter.notifyDataSetChanged();

                Log.d("finally", SI + " " + sn + " " + sectionTitle);
                for (int l = 0; l < my_fragment_index_list.size(); ++l) {
                    Log.d("finally", my_fragment_index_list.get(l) + "");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("state", state + " " + pager.getCurrentItem());
            }
        });

        sectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = pager.getCurrentItem();
                int sI = ob.getIntValuesPerQuestionByFragmentIndex(num, MySqlDatabase.SectionIndex);
                //Get serial number of this section from PerSectionDetails..
                String srNo = ob.getStringValuesPerSectionBySectionIndex(sI, MySqlDatabase.SerialNumber);
//                SharedPreferences.Editor e=quizPrefs.edit();
//                e.putInt("mySectionIndex",s);
//                e.putInt("myQuestionIndex",q);
//                e.apply();
                Intent i = new Intent(QuizMainActivity.this, SectionNamesDisplay.class);
                i.putExtra("serialNumber", srNo);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
        submitButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                int mySrNo = data.getIntExtra("message", 0);
                //Get SectionIndex by srNo..
                int sI = ob.getIntValuesPerSectionBySerialNumber(mySrNo, MySqlDatabase.SectionIndex);

                //Get fragment index with section index= sI and serial number=0 from PerQuestionDetails..
                int my_fi = ob.getIntValuesPerQuestionBySiAndSrno(sI, 0, MySqlDatabase.FragmentIndex);

                pager.setCurrentItem(my_fi);

                HashMap<String, String> map = ob.getValuesPerSection(sI);
                sectionTitle = map.get("SectionName");
                sectionName.setText(sectionTitle);

            }
        } else if (requestCode == REQUEST_CODE_FOR_ALL_SUMMARY) {
            if (data != null) {
                int jumpTo = data.getIntExtra("jumpTo", 0);
                pager.setCurrentItem(jumpTo);
            }
        }
    }

    @Override
    public void SetValue(int pos) {
        pager.setCurrentItem(pos);
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
                Intent i = new Intent(QuizMainActivity.this, AllSectionsSummary.class);
                startActivityForResult(i, REQUEST_CODE_FOR_ALL_SUMMARY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        int ss, qq, n, sn;
        HashMap<String, ArrayList<Integer>> my_map;
        ArrayList<Integer> my_fragment_index_list;
        ArrayList<Integer> myType;
        String srNo;
        switch (v.getId()) {

            case R.id.submitButton:
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.QuestionIndex);
                Log.d("here", ss + " " + qq);
                ob.updateValuesForResult(ss, qq, MySqlDatabase.QuestionStatus, 1 + "");
                types = ob.getTypes(ss);
                submittedQuestions.setText(types.get(1) + "");
                reviewedQuestions.setText(types.get(2) + "");
                clearedQuestions.setText(types.get(3) + "");
                notAttemptedQuestions.setText(types.get(0) + "");

                srNo = ob.getStringValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SerialNumber);
                sn = Integer.parseInt(srNo);
                my_map = ob.getAllIntValuesPerQuestionBySectionIndex(ss);
                my_fragment_index_list = my_map.get("FragmentIndexList");
                myType = ob.getTypesOfEachSection(ss);
                allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
                questionsList.setAdapter(allQuestionsInOneSectionAdapter);
                allQuestionsInOneSectionAdapter.notifyDataSetChanged();


                break;
            case R.id.reviewButton:
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.QuestionIndex);
                Log.d("here", ss + " " + qq);
                ob.updateValuesForResult(ss, qq, MySqlDatabase.QuestionStatus, 2 + "");
                types = ob.getTypes(ss);
                submittedQuestions.setText(types.get(1) + "");
                reviewedQuestions.setText(types.get(2) + "");
                clearedQuestions.setText(types.get(3) + "");
                notAttemptedQuestions.setText(types.get(0) + "");

                srNo = ob.getStringValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SerialNumber);
                sn = Integer.parseInt(srNo);
                my_map = ob.getAllIntValuesPerQuestionBySectionIndex(ss);
                my_fragment_index_list = my_map.get("FragmentIndexList");
                myType = ob.getTypesOfEachSection(ss);
                allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
                questionsList.setAdapter(allQuestionsInOneSectionAdapter);
                allQuestionsInOneSectionAdapter.notifyDataSetChanged();
                break;
            case R.id.clearButton:
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.QuestionIndex);
                Log.d("here", ss + " " + qq);
                ob.updateValuesForResult(ss, qq, MySqlDatabase.QuestionStatus, 3 + "");
                ob.updateValuesForResult(ss, qq, MySqlDatabase.FinalAnswerSerialNumber, -1 + "");
                ob.updateValuesForResult(ss, qq, MySqlDatabase.OptionId, -1 + "");
                types = ob.getTypes(ss);
                submittedQuestions.setText(types.get(1) + "");
                reviewedQuestions.setText(types.get(2) + "");
                clearedQuestions.setText(types.get(3) + "");
                notAttemptedQuestions.setText(types.get(0) + "");

                //Disable buttons..
                submitButton.setEnabled(false);
                submitButton.setBackgroundColor(getResources().getColor(R.color.light_grey));
                clearButton.setEnabled(false);
                clearButton.setBackgroundColor(getResources().getColor(R.color.light_grey));

                srNo = ob.getStringValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SerialNumber);
                sn = Integer.parseInt(srNo);
                my_map = ob.getAllIntValuesPerQuestionBySectionIndex(ss);
                my_fragment_index_list = my_map.get("FragmentIndexList");
                myType = ob.getTypesOfEachSection(ss);
                allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
                questionsList.setAdapter(allQuestionsInOneSectionAdapter);
                allQuestionsInOneSectionAdapter.notifyDataSetChanged();

//                Fragment myFragment=pageAdapter.getItem(n);
//                myFragment.onResume();
                //Clear webview content..
                //.......................................................................
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
                Intent i = new Intent("TAG_REFRESH");
                lbm.sendBroadcast(i);

                break;
        }
    }

    @Override
    public void enableButtons() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("enable", "here");
                submitButton.setEnabled(true);
                submitButton.setBackgroundColor(getResources().getColor(R.color.black));
                clearButton.setEnabled(true);
                clearButton.setBackgroundColor(getResources().getColor(R.color.black));
            }
        });
    }

    @Override
    public void startedStreaming(String teacherId, String studentId) {
        JSONObject data = new JSONObject();
        try {
            data.put("teacherId", teacherId);
            data.put("studentId", studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(Constants.STARTEDSTREAMING, data);
    }

    @Override
    public void stoppedStreaming() {
        socket.emit(Constants.STOPPEDSTREAMING, "");
    }

    //Adapter for view pager..
    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("messi", "inOnDestroy");
        ob.deleteMyTable();
        SharedPreferences.Editor e = quizPrefs.edit();
        e.clear();
        e.apply();
        //when activity will distroy student will be disconnected from session
        socketfromteacher.disconnectSession();
    }

    // in/truskills/liveexams/Quiz/QuizMainActivity.java:137
    public void initSocketConnection() {
        try {
            socket = IO.socket("http://35.154.110.122:3001/");
        } catch (URISyntaxException e) {
            Log.d("url", "http://35.154.110.122:3001/ not found");
        }
        //connect to server
        socket.connect();

        //teacher will emit STARTSTREAMING socket to request student for streaming
        socket.on(Constants.STARTSTREAMING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(SOCKET, "got startStreaming from teacher");
                final JSONObject json = (JSONObject) args[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //in/truskills/liveexams/Quiz/FlashphonerEvents.java:71
                            socketfromteacher.startStreaming(json.getString("studentID"), json.getString("teacherID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        //teacher will emit STOPSTREAMING socket to inform student to stop Streaming
        socket.on(Constants.STOPSTREAMING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(SOCKET, "got stopStreaming from teacher");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socketfromteacher.stopStreaming();
                    }
                });
            }
        });
    }
}
