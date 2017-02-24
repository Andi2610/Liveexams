package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
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
import com.flashphoner.fpwcsapi.layout.PercentFrameLayout;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

import static java.lang.Character.FORMAT;

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
    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE = 1, REQUEST_CODE_FOR_ALL_SUMMARY = 2;
    SharedPreferences quizPrefs;
    long start, end, diff;
    String examId, paperName, selectedLanguage, sectionTitle;
    ArrayList<Fragment> fList;
    TextView sectionName, submittedQuestions, reviewedTickedQuestions,reviewedUntickedQuestions, notAttemptedQuestions, timer, clearedQuestions;
    Button submitButton, reviewButton, clearButton;
    ViewPager pager;
    MySqlDatabase ob;
    RecyclerView questionsList;
    AllQuestionsInOneSectionAdapter allQuestionsInOneSectionAdapter;
    LinearLayoutManager linearLayoutManager;
    private static final String FORMAT = "%02d:%02d:%02d";
    ArrayList<Integer> arrayForNoOfSections, arrayForQuestions, arrayForOptions, types;
    ArrayList<String> options;
    int mySectionCount = -1, my_section, my_question, myFragmentCount = -1, my_option, questionArray[], noOfSections, num, total, myTime;
    Socket socket;
    socketFromTeacher socketfromteacher;

    FlashphonerEvents flashphoner;
    SurfaceViewRenderer extraRender;
    PercentFrameLayout parentRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        quizPrefs = getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get intent variables..
        examId = getIntent().getStringExtra("examId");
        paperName = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");

        getSupportActionBar().setTitle(paperName);

        fList = new ArrayList<>();
        timer = (TextView) findViewById(R.id.timer);
        sectionName = (TextView) findViewById(R.id.sectionName);
        submittedQuestions = (TextView) findViewById(R.id.submittedQuestions);
        reviewedTickedQuestions = (TextView) findViewById(R.id.reviewedTickedQuestions);
        reviewedUntickedQuestions = (TextView) findViewById(R.id.reviewedUntickedQuestions);
        notAttemptedQuestions = (TextView) findViewById(R.id.notAttemptedQuestions);
        clearedQuestions = (TextView) findViewById(R.id.clearedQuestions);
        submitButton = (Button) findViewById(R.id.submitButton);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        clearButton = (Button) findViewById(R.id.clearButton);
        questionsList = (RecyclerView) findViewById(R.id.questionsList);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionName.setTypeface(tff1);
        timer.setTypeface(tff1);
        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        submittedQuestions.setTypeface(tff2);
        reviewedTickedQuestions.setTypeface(tff2);
        reviewedUntickedQuestions.setTypeface(tff2);
        clearedQuestions.setTypeface(tff2);
        notAttemptedQuestions.setTypeface(tff2);
        submitButton.setTypeface(tff1);
        reviewButton.setTypeface(tff1);
        clearButton.setTypeface(tff1);

        submitButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        ob = new MySqlDatabase(QuizMainActivity.this);

        /*
        * hidden element where student video will be loaded
        * layout/activity_quiz_main.xml:156
        * */
        extraRender = (SurfaceViewRenderer) findViewById(R.id.studentView);
        parentRender = (PercentFrameLayout) findViewById(R.id.studentVideo);
        //to access all methods related to Streaming
        flashphoner = new FlashphonerEvents(QuizMainActivity.this, extraRender, parentRender);
        //Interface to communicate with Flashphoner class
        socketfromteacher = (socketFromTeacher) flashphoner;
        //CONNECT TO SOCKET..
        initSocketConnection();

        noOfSections = getIntent().getIntExtra("noOfSections", 0);
        questionArray = getIntent().getIntArrayExtra("questionArray");
        myTime = getIntent().getIntExtra("ExamDuration", 0);

        formFragmentListForViewPager();

        //Set the view pager adapter..
        pageAdapter= new MyPageAdapter(getSupportFragmentManager(),fList);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        total=myFragmentCount+1;
        pager.setOffscreenPageLimit(total);

        linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        questionsList.setLayoutManager(linearLayoutManager);
        questionsList.setItemAnimator(new DefaultItemAnimator());

        forQuiz();
    }

    public void forQuiz() {

        //Set timer..
        new CountDownTimer(myTime, 1000) { // adjust the milli seconds here

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

<<<<<<< HEAD
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

                String my_text = ob.getTextOfOneQuestion(my_section, my_question);

                int numOp = ob.getNoOfOptionsInOneQuestion(my_section, my_question);

                arrayForOptions = new ArrayList<>();
                for (int p = 0; p < 4; ++p) {
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

//                    String my_option_text=optionArray[my_section][my_question][my_option];
                    String my_option_text = ob.getTextOfOneOption(my_section, my_question, my_option);
                    options.add(my_option_text);
                }
                fList.add(MyFragment.newInstance(my_text, options, examId, my_section, my_question, myFragmentCount));
            }
        }

        //Set the view pager adapter..
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fList);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        total = myFragmentCount + 1;
        pager.setOffscreenPageLimit(total);

        //Initially.. fragmentIndex=0;
        //Getting SectionIndex, QuestionIndex and SerialNumber of fragmentIndex=0;
        int sI = ob.getIntValuesPerQuestionByFragmentIndex(0, MySqlDatabase.SectionIndex);
        int qI = ob.getIntValuesPerQuestionByFragmentIndex(0, MySqlDatabase.QuestionIndex);
        ob.updateValuesForResult(sI, qI, MySqlDatabase.ReadStatus, 1 + "");
        String srNo = ob.getStringValuesPerQuestionByFragmentIndex(0, MySqlDatabase.SerialNumber);
        int sn = Integer.parseInt(srNo);

        SharedPreferences.Editor e = quizPrefs.edit();
        e.putInt("previousIndex", 0);
        e.apply();

        start = System.currentTimeMillis();

        ArrayList<Integer> myType = ob.getTypesOfEachSection(sI);
        types = ob.getTypes(sI);
        submittedQuestions.setText(types.get(1) + "");
        reviewedQuestions.setText(types.get(2) + "");
        clearedQuestions.setText(types.get(3) + "");
        notAttemptedQuestions.setText(types.get(0) + "");
        HashMap<String, String> map = ob.getValuesPerSection(sI);
        sectionTitle = map.get("SectionName");
        sectionName.setText(sectionTitle);
=======
        //Initially.. fragmentIndex=0;
        //Getting SectionIndex, QuestionIndex and SerialNumber of fragmentIndex=0;
        int sI=ob.getIntValuesPerQuestionByFragmentIndex(0,MySqlDatabase.SectionIndex);
        int qI=ob.getIntValuesPerQuestionByFragmentIndex(0,MySqlDatabase.QuestionIndex);

        //Update read status..
        ob.updateValuesForResult(sI,qI,MySqlDatabase.ReadStatus,1+"");

        //Save current fragment index in sharedPrefs..
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("previousIndex",0);
        e.apply();

        //start time for this page..
        start=System.currentTimeMillis();

        //Mark as not answered..
        setDetailsForNotAnswered(sI,qI);

        //Set sectionTitle..
        setSectionTitle(sI);
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839

        //Disable buttons..
        changeButtonStatus(false);

<<<<<<< HEAD
        HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(sI);
        ArrayList<Integer> my_fragment_index_list = new ArrayList<>();
        my_fragment_index_list = my_map.get("FragmentIndexList");
        allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        questionsList.setLayoutManager(linearLayoutManager);
        questionsList.setItemAnimator(new DefaultItemAnimator());
        questionsList.setAdapter(allQuestionsInOneSectionAdapter);
        allQuestionsInOneSectionAdapter.notifyDataSetChanged();
=======
        //setTextViewsData..
        setTextViewsData(sI);

        //Set list adapter..
        setQuestionsListAdapter(0,sI);
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839

        //Whenever user swipes a screen..
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

<<<<<<< HEAD
                int SI = ob.getIntValuesPerQuestionByFragmentIndex(position, MySqlDatabase.SectionIndex);
                int QI = ob.getIntValuesPerQuestionByFragmentIndex(position, MySqlDatabase.QuestionIndex);
=======
                //For previous page..
                int preIndex=quizPrefs.getInt("previousIndex",0);
                int preSi=ob.getIntValuesPerQuestionByFragmentIndex(preIndex,MySqlDatabase.SectionIndex);
                int preQi=ob.getIntValuesPerQuestionByFragmentIndex(preIndex,MySqlDatabase.QuestionIndex);
                int prevTempSr=Integer.parseInt(ob.getValuesForResult(preSi,preQi,MySqlDatabase.TempAnswerSerialNumber));
                int prevQS=Integer.parseInt(ob.getValuesForResult(preSi,preQi,MySqlDatabase.QuestionStatus));

                Log.d("myData",prevTempSr+" "+prevQS);

                if((prevTempSr==-1&&prevQS!=2) || prevQS==4 || prevQS==3){
                   setDetailsForNotAnswered(preSi,preQi);
                    MyFragment fragment= (MyFragment) pageAdapter.getItem(preIndex);
                    fragment.update();
                }

                //For current page..
                int SI=ob.getIntValuesPerQuestionByFragmentIndex(position,MySqlDatabase.SectionIndex);
                int QI=ob.getIntValuesPerQuestionByFragmentIndex(position,MySqlDatabase.QuestionIndex);
                int curQS=Integer.parseInt(ob.getValuesForResult(SI,QI,MySqlDatabase.QuestionStatus));

                if(curQS!=0&&curQS!=1&&curQS!=2){
                    setDetailsForNotAnswered(SI,QI);
                }
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839

                end = System.currentTimeMillis();
                diff = end - start;
                diff = diff / 1000;
                String myTime = ob.getValuesForResult(SI, QI, MySqlDatabase.TimeSpent);
                long time = Long.parseLong(myTime);
                time = time + diff;
                ob.updateValuesForResult(SI, QI, MySqlDatabase.TimeSpent, time + "");

                SharedPreferences.Editor e = quizPrefs.edit();
                e.putInt("previousIndex", position);
                e.apply();

<<<<<<< HEAD
                start = System.currentTimeMillis();
                ob.updateValuesForResult(SI, QI, MySqlDatabase.ReadStatus, 1 + "");
                String srNo = ob.getStringValuesPerQuestionByFragmentIndex(position, MySqlDatabase.SerialNumber);
                int sn = Integer.parseInt(srNo);
=======
                start=System.currentTimeMillis();

                ob.updateValuesForResult(SI,QI,MySqlDatabase.ReadStatus,1+"");
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839

                int qType = ob.getTypeOfAQuestion(SI, QI);
                if (qType == -1) {
                    //Disable buttons..
                    changeButtonStatus(false);
                } else {
                    //Enable buttons..
                    changeButtonStatus(true);
                }
<<<<<<< HEAD

                HashMap<String, String> map = ob.getValuesPerSection(SI);
                sectionTitle = map.get("SectionName");
                sectionName.setText(sectionTitle);

                types = ob.getTypes(SI);
                submittedQuestions.setText(types.get(1) + "");
                reviewedQuestions.setText(types.get(2) + "");
                clearedQuestions.setText(types.get(3) + "");
                notAttemptedQuestions.setText(types.get(0) + "");

                HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(SI);
                ArrayList<Integer> my_fragment_index_list;
                my_fragment_index_list = my_map.get("FragmentIndexList");
                ArrayList<Integer> myType = ob.getTypesOfEachSection(SI);
                allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
                questionsList.setAdapter(allQuestionsInOneSectionAdapter);
                allQuestionsInOneSectionAdapter.notifyDataSetChanged();
=======
                setSectionTitle(SI);
                setTextViewsData(SI);
                setQuestionsListAdapter(position,SI);
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        sectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = pager.getCurrentItem();
                int sI = ob.getIntValuesPerQuestionByFragmentIndex(num, MySqlDatabase.SectionIndex);
                //Get serial number of this section from PerSectionDetails..
                String srNo = ob.getStringValuesPerSectionBySectionIndex(sI, MySqlDatabase.SerialNumber);
                Intent i = new Intent(QuizMainActivity.this, SectionNamesDisplay.class);
                i.putExtra("serialNumber", srNo);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
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
<<<<<<< HEAD
                int my_fi = ob.getIntValuesPerQuestionBySiAndSrno(sI, 0, MySqlDatabase.FragmentIndex);

=======
                int my_fi=ob.getIntValuesPerQuestionBySiAndSrno(sI,0,MySqlDatabase.FragmentIndex);
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839
                pager.setCurrentItem(my_fi);
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
<<<<<<< HEAD
        int ss, qq, n;
        switch (v.getId()) {

            case R.id.submitButton:
                n = pager.getCurrentItem();
                clickMethods(n, 1);
                break;
            case R.id.reviewButton:
                n = pager.getCurrentItem();
                clickMethods(n, 2);
                break;
            case R.id.clearButton:
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.QuestionIndex);
                ob.updateValuesForResult(ss, qq, MySqlDatabase.FinalAnswerSerialNumber, -1 + "");
                ob.updateValuesForResult(ss, qq, MySqlDatabase.FinalAnswerId, -1 + "");
                changeButtonStatus(false);
                clickMethods(n, 3);
                MyFragment fragment = (MyFragment) pageAdapter.getItem(n);
=======
        int ss,qq,n;
        String temp;
        switch (v.getId()){

            case R.id.submitButton:
                n=pager.getCurrentItem();
                ss=ob.getIntValuesPerQuestionByFragmentIndex(n,MySqlDatabase.SectionIndex);
                qq=ob.getIntValuesPerQuestionByFragmentIndex(n,MySqlDatabase.QuestionIndex);
                setDetailsForFinalAnswer(ss,qq);
                setParticularQuestionStatus(ss,qq,0);
                setTextViewsData(ss);
                setQuestionsListAdapter(n,ss);
                break;
            case R.id.reviewButton:
                n=pager.getCurrentItem();
                ss=ob.getIntValuesPerQuestionByFragmentIndex(n,MySqlDatabase.SectionIndex);
                qq=ob.getIntValuesPerQuestionByFragmentIndex(n,MySqlDatabase.QuestionIndex);
                temp=ob.getValuesForResult(ss,qq,MySqlDatabase.TempAnswerSerialNumber);
                if(temp.equals("-1")){
                    setParticularQuestionStatus(ss,qq,2);
                    setTextViewsData(ss);
                    setQuestionsListAdapter(n,ss);
                }else{
                    setDetailsForFinalAnswer(ss,qq);
                    setParticularQuestionStatus(ss,qq,1);
                    setTextViewsData(ss);
                    setQuestionsListAdapter(n,ss);
                }
                break;
            case R.id.clearButton:
                n=pager.getCurrentItem();
                ss=ob.getIntValuesPerQuestionByFragmentIndex(n,MySqlDatabase.SectionIndex);
                qq=ob.getIntValuesPerQuestionByFragmentIndex(n,MySqlDatabase.QuestionIndex);
                setDetailsForNotAnswered(ss,qq);
                changeButtonStatus(false);
                setParticularQuestionStatus(ss,qq,3);
                setTextViewsData(ss);
                setQuestionsListAdapter(n,ss);
                MyFragment fragment= (MyFragment) pageAdapter.getItem(n);
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839
                fragment.update();
                break;
        }
    }

<<<<<<< HEAD
    public void clickMethods(int n, int status) {
        int ss, qq, sn;
        HashMap<String, ArrayList<Integer>> my_map;
        ArrayList<Integer> my_fragment_index_list;
        ArrayList<Integer> myType;
        String srNo;
        ss = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.SectionIndex);
        qq = ob.getIntValuesPerQuestionByFragmentIndex(n, MySqlDatabase.QuestionIndex);
        ob.updateValuesForResult(ss, qq, MySqlDatabase.QuestionStatus, status + "");
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
        int next = n + 1;
        if (next <= total)
            pager.setCurrentItem(next);
    }

    public void changeButtonStatus(boolean status) {
        if (status) {
=======
    public void changeButtonStatus(boolean status){
        if(status){
>>>>>>> 7736a64f86b38950d637887cedde0cc424ccd839
            submitButton.setEnabled(true);
            submitButton.setBackgroundColor(getResources().getColor(R.color.black));
            clearButton.setEnabled(true);
            clearButton.setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackgroundColor(getResources().getColor(R.color.light_black));
            clearButton.setEnabled(false);
            clearButton.setBackgroundColor(getResources().getColor(R.color.light_black));
        }
    }

    @Override
    public void enableButtons() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeButtonStatus(true);
            }
        });
    }

    @Override
    public void putDetailsForNotAnswered(final int si, final int qi,final int fi) {

        Log.d("function","in putDetailsForNotAnswered");

        ob.updateValuesForResult(si,qi,MySqlDatabase.QuestionStatus,3+"");
//        ob.updateValuesForResult(si,qi,MySqlDatabase.TempAnswerSerialNumber,-1+"");
        ob.updateValuesForResult(si,qi,MySqlDatabase.FinalAnswerSerialNumber,-1+"");
        ob.updateValuesForResult(si,qi,MySqlDatabase.FinalAnswerId,-1+"");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextViewsData(si);
                setQuestionsListAdapter(fi,si);
            }
        });
    }

    public void setDetailsForNotAnswered(int si, int qi) {

        ob.updateValuesForResult(si,qi,MySqlDatabase.QuestionStatus,3+"");
        ob.updateValuesForResult(si,qi,MySqlDatabase.TempAnswerSerialNumber,-1+"");
        ob.updateValuesForResult(si,qi,MySqlDatabase.FinalAnswerSerialNumber,-1+"");
        ob.updateValuesForResult(si,qi,MySqlDatabase.FinalAnswerId,-1+"");
    }

    public void setTextViewsData(int si){
        types=ob.getTypes(si);
        submittedQuestions.setText(types.get(0)+"");
        reviewedTickedQuestions.setText(types.get(1)+"");
        reviewedUntickedQuestions.setText(types.get(2)+"");
        clearedQuestions.setText(types.get(3)+"");
        notAttemptedQuestions.setText(types.get(4)+"");
    }

    public void setQuestionsListAdapter(int fi,int si){
        String srNo=ob.getStringValuesPerQuestionByFragmentIndex(fi,MySqlDatabase.SerialNumber);
        int sn=Integer.parseInt(srNo);
        HashMap<String,ArrayList<Integer>>  my_map=ob.getAllIntValuesPerQuestionBySectionIndex(si);
        ArrayList<Integer> my_fragment_index_list=my_map.get("FragmentIndexList");
        ArrayList<Integer> myType=ob.getTypesOfEachSection(si);
        allQuestionsInOneSectionAdapter=new AllQuestionsInOneSectionAdapter(my_fragment_index_list,QuizMainActivity.this,sn,myType);
        questionsList.setAdapter(allQuestionsInOneSectionAdapter);
        allQuestionsInOneSectionAdapter.notifyDataSetChanged();
    }

    public void setDetailsForFinalAnswer(int si,int qi){
        String temp=ob.getValuesForResult(si,qi,MySqlDatabase.TempAnswerSerialNumber);
        int tempSr=Integer.parseInt(temp);
        Log.d("myData","tempStr="+tempSr);
        ob.updateValuesForResult(si,qi,MySqlDatabase.FinalAnswerSerialNumber,tempSr+"");
        int oi=ob.getOptionIdBySerialNumber(tempSr+"");
        ob.updateValuesForResult(si,qi,MySqlDatabase.FinalAnswerId,oi+"");
    }

    public void setParticularQuestionStatus(int si,int qi,int status){
        ob.updateValuesForResult(si,qi,MySqlDatabase.QuestionStatus,status+"");
    }

    public void setSectionTitle(int si){
        HashMap<String, String> map = ob.getValuesPerSection(si);
        sectionTitle = map.get("SectionName");
        sectionName.setText(sectionTitle);
    }

    @Override
    public void startedStreaming(String teacherId, String studentId) {
        JSONObject data = new JSONObject();
        try {
            data.put("teacherSocketId", teacherId);
            data.put("studentSocketId", studentId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit(Constants.STARTEDSTREAMING, data);
    }

    @Override
    public void stoppedStreaming() {
        socket.emit(Constants.STOPPEDSTREAMING, "");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ob.deleteMyTable();
        SharedPreferences.Editor e = quizPrefs.edit();
        e.clear();
        e.apply();
        //when activity will distroy student will be disconnected from session
        socketfromteacher.disconnectSession();
        String folder_main = "LiveExams";

        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (f.exists()) {
            deleteDir(f);
        }

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    // in/truskills/liveexams/Quiz/QuizMainActivity.java:137
    public void initSocketConnection() {
        try {
            socket = IO.socket("http://35.154.110.122:3002/");
            Log.d("url", "http://35.154.110.122:3002/ found");
        } catch (URISyntaxException e) {
            Log.d("url", "http://35.154.110.122:3002/ not found");
        }
        //connect to server
        socket.connect();

        //in/truskills/liveexams/Quiz/FlashphonerEvents.java:71
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
                            try {
                                Log.d(SOCKET, "studentSocketId" + json.getString("studentSocketId"));
                                Log.d(SOCKET, "teacherSocketId" + json.getString("teacherSocketId"));
                            } catch (Exception e) {
                                Log.d(SOCKET,"error "+ e.toString());
                            }
                            socketfromteacher.startStreaming(json.getString("studentSocketId"), json.getString("teacherSocketId"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.on(Constants.GETINFO, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                JSONObject data = new JSONObject();
                try {
                    data.put("userId", prefs.getString("userId", ""));
                    data.put("examId", examId);
                    data.put("isTeacher", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(SOCKET, "got get Info " + data);
                socket.emit(Constants.SETINFO, data);
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

    public void formFragmentListForViewPager(){
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
                my_question=arrayForQuestions.get(k);
                //Set the serial number of this question..
                ob.updateValuesPerQuestion(my_section,my_question,MySqlDatabase.SerialNumber,myQuestionCount+"");
                ob.updateValuesPerQuestion(my_section,my_question,MySqlDatabase.FragmentIndex,myFragmentCount+"");
                ob.updateValuesForResult(my_section,my_question,MySqlDatabase.SerialNumber,myQuestionCount+"");

                String my_text=ob.getTextOfOneQuestion(my_section,my_question);

                int numOp=ob.getNoOfOptionsInOneQuestion(my_section,my_question);

                arrayForOptions=new ArrayList<>();
                for(int p=0;p<4;++p){
                    arrayForOptions.add(p);
                }
                //Shuffle if required..
                Collections.shuffle(arrayForOptions);

                options=new ArrayList<>();

                int myOptionCount=-1;

                for (int s=0;s<numOp;++s){
                    //Increase option serial number..
                    myOptionCount++;
                    //Getting an option randomly..
                    my_option=arrayForOptions.get(s);
                    //Set the serial number of this option..
                    ob.updateValuesPerOption(my_section,my_question,my_option,MySqlDatabase.SerialNumber,myOptionCount+"");

//                    String my_option_text=optionArray[my_section][my_question][my_option];
                    String my_option_text=ob.getTextOfOneOption(my_section,my_question,my_option);
                    options.add(my_option_text);
                }
                fList.add(MyFragment.newInstance(my_text, options,examId,my_section,my_question,myFragmentCount));
            }
        }

    }
}
