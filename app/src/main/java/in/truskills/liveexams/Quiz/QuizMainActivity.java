package in.truskills.liveexams.Quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.ConnectivityReciever;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.MyApplication;
import in.truskills.liveexams.Miscellaneous.SubmitAnswerPaper;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

//This is the quiz main activity in which my fragment is loaded..

/*
* this interface is to call methods of FlashphonerEvents from QuizMainActivity
* */
interface socketFromTeacher {
    void startStreaming(String studentId, String teacherId);

    void stopStreaming();

    void disconnectSession();
}

public class QuizMainActivity extends AppCompatActivity implements setValueOfPager, View.OnClickListener, MyFragmentInterface, socketFromStudent, ConnectivityReciever.ConnectivityReceiverListener {

    private static final String SOCKET = "socket";
    private static final String TAG = "checkkkkk-InQuiz" ;
    public static boolean visible=true;
    Handler h;
    RelativeLayout activity_quiz_main;
    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE = 1, REQUEST_CODE_FOR_ALL_SUMMARY = 2;
    SharedPreferences quizPrefs,dataPrefs;
    long start, end, diff;
    int my_fi,jumpTo;
    String examId, paperName, selectedLanguage, sectionTitle;
    ArrayList<Fragment> fList;
    TextView sectionName, submittedQuestions, reviewedTickedQuestions, reviewedUntickedQuestions, notAttemptedQuestions, timer, clearedQuestions;
    Button submitButton, reviewButton, clearButton;
    ViewPager pager;
    QuizDatabase ob;
    RecyclerView questionsList;
    AllQuestionsInOneSectionAdapter allQuestionsInOneSectionAdapter;
    LinearLayoutManager linearLayoutManager;
    private static final String FORMAT = "%02d:%02d:%02d";
    ArrayList<Integer> arrayForNoOfSections, arrayForQuestions, arrayForOptions, types;
    ArrayList<String> options;
    int mySectionCount = -1, my_section, my_question, myFragmentCount = -1, my_option, questionArray[], noOfSections, num, total, myTime;
    Socket socket;
    socketFromTeacher socketfromteacher;
    SharedPreferences prefs,firstTime,firstTimeForRules,allow;
    CountDownTimer count;
    String myDate;
    long timeUntil;
    Button left, right;
    Thread t;
    Vibrator vibrator;
    LinearLayout awareLayoutForSummary;
    TextView textForAwareForSummary;
    ImageView imageForAwareForSummary;
    RelativeLayout header,listLayout;
    TextView breakLine;
    LinearLayout footer;
    AVLoadingIndicatorView avi;

    FlashphonerEvents flashphoner;
    SurfaceViewRenderer extraRender;
    PercentFrameLayout parentRender;
    ProgressDialog progressDialog2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        h=new Handler();

        progressDialog2= new ProgressDialog(QuizMainActivity.this);


        activity_quiz_main=(RelativeLayout)findViewById(R.id.activity_quiz_main);

//        avi=(AVLoadingIndicatorView)findViewById(R.id.avi);

        header=(RelativeLayout)findViewById(R.id.header);
        listLayout=(RelativeLayout)findViewById(R.id.listLayout);
        breakLine=(TextView)findViewById(R.id.breakLine);
        footer=(LinearLayout)findViewById(R.id.footer);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        quizPrefs = getSharedPreferences("quizPrefs", Context.MODE_PRIVATE);
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        dataPrefs = getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);
        firstTime = getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        allow = getSharedPreferences("allow", Context.MODE_PRIVATE);
        firstTimeForRules = getSharedPreferences("firstTimeForRules", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get intent variables..
        examId = getIntent().getStringExtra("examId");
        paperName = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");
        myDate = getIntent().getStringExtra("date");

        awareLayoutForSummary=(LinearLayout)findViewById(R.id.awareLayoutForSummary);
        textForAwareForSummary=(TextView)findViewById(R.id.textForAwareForSummary);
        imageForAwareForSummary=(ImageView)findViewById(R.id.imageForAwareForSummary);

//        Toast.makeText(this, "date:"+myDate, Toast.LENGTH_SHORT).show();

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
        left = (Button) findViewById(R.id.left);
        right = (Button) findViewById(R.id.right);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        sectionName.setTypeface(tff1);
        timer.setTypeface(tff1);
        textForAwareForSummary.setTypeface(tff1);
        Typeface tff2 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        submittedQuestions.setTypeface(tff1);
        reviewedTickedQuestions.setTypeface(tff1);
        reviewedUntickedQuestions.setTypeface(tff1);
        clearedQuestions.setTypeface(tff1);
        notAttemptedQuestions.setTypeface(tff1);
        submitButton.setTypeface(tff1);
        reviewButton.setTypeface(tff1);
        clearButton.setTypeface(tff1);

        submitButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);

        ob = new QuizDatabase(QuizMainActivity.this);

        SharedPreferences.Editor e=dataPrefs.edit();
        e.putString("date",myDate);
        e.putString("userId",prefs.getString("userId",""));
        e.putString("examId",examId);
        e.putString("selectedLanguage",selectedLanguage);
        e.apply();

        /*
        * hidden element where student video will be loaded
        * layout/activity_quiz_main.xml:156
        * */
        extraRender = (SurfaceViewRenderer) findViewById(R.id.studentView);
        parentRender = (PercentFrameLayout) findViewById(R.id.studentVideo);
        //to access all methods related to Streaming
        flashphoner = new FlashphonerEvents(QuizMainActivity.this, extraRender, parentRender);
        //Interface to communicate with Flashphoner class
        socketfromteacher = flashphoner;
        //CONNECT TO SOCKET..
        initSocketConnection();

        noOfSections = getIntent().getIntExtra("noOfSections", 0);
        questionArray = getIntent().getIntArrayExtra("questionArray");
        myTime = getIntent().getIntExtra("ExamDuration", 0);

        linearLayoutManager = new LinearLayoutManager(QuizMainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        questionsList.setLayoutManager(linearLayoutManager);
        questionsList.setItemAnimator(new DefaultItemAnimator());

        pager = (ViewPager) findViewById(R.id.viewpager);

        formFragmentListForViewPager();
    }

    public void forQuiz() {

        awareLayoutForSummary.setVisibility(View.GONE);
        header.setAlpha((float)1);
        listLayout.setAlpha((float)1);
        footer.setAlpha((float)1);
        pager.setAlpha((float)1);
        breakLine.setAlpha((float)1);

        timer.setText("Starting.. Please wait..");


//        ob.getAllValues();

        //Set timer.

        //Initially.. fragmentIndex=0;
        //Getting SectionIndex, QuestionIndex and SerialNumber of fragmentIndex=0;
        int sI = ob.getIntValuesPerQuestionByFragmentIndex(0, QuizDatabase.SectionIndex);
        int qI = ob.getIntValuesPerQuestionByFragmentIndex(0, QuizDatabase.QuestionIndex);

//        if(sI==0&&qI==0){
//
//            progressDialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog2.setMessage("Loading... Please wait...");
//            progressDialog2.setIndeterminate(true);
//            progressDialog2.setCancelable(false);
//            progressDialog2.show();
//        }

//        count = new CountDownTimer(myTime, 1000) { // adjust the milli seconds here
//
//            public void onTick(long millisUntilFinished) {
//
//                timer.setText("" + String.format(FORMAT,
//                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
//                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
//                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
//                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
//                timeUntil = millisUntilFinished;
//            }
//
//            public void onFinish() {
//                timer.setText("done!");
//                submit();
//            }
//
//
//        };
//        count.start();

        //Update read status..
        ob.updateValuesForResult(sI, qI, QuizDatabase.ReadStatus, 1 + "");

        //Save current fragment index in sharedPrefs..
        SharedPreferences.Editor e = quizPrefs.edit();
        e.putInt("previousIndex", 0);
        e.apply();

        //start time for this page..
        start = System.currentTimeMillis();

        //Mark as not answered..
        setDetailsForNotAnswered(sI, qI);

        //Set sectionTitle..
        setSectionTitle(sI);

        //Disable buttons..
        changeButtonStatus(false);

        //setTextViewsData..
        setTextViewsData(sI);

        //Set list adapter..
        setQuestionsListAdapter(0, sI);

        //Whenever user swipes a screen..
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //For previous page..
                int preIndex = quizPrefs.getInt("previousIndex", 0);
                int preSi = ob.getIntValuesPerQuestionByFragmentIndex(preIndex, QuizDatabase.SectionIndex);
                int preQi = ob.getIntValuesPerQuestionByFragmentIndex(preIndex, QuizDatabase.QuestionIndex);
                int prevTempSr = Integer.parseInt(ob.getValuesForResult(preSi, preQi, QuizDatabase.TempAnswerSerialNumber));
                int prevQS = Integer.parseInt(ob.getValuesForResult(preSi, preQi, QuizDatabase.QuestionStatus));

                Log.d("myData", prevTempSr + " " + prevQS);

                if ((prevTempSr == -1 && prevQS != 2) || prevQS == 4 || prevQS == 3) {
                    setDetailsForNotAnswered(preSi, preQi);
                    MyFragment fragment = (MyFragment) pageAdapter.getItem(preIndex);
                    fragment.update();
                }

                //Update time spent..
                end = System.currentTimeMillis();
                diff = end - start;
                diff = diff / 1000;
                String myTime = ob.getValuesForResult(preSi, preQi, QuizDatabase.TimeSpent);
                long time = Long.parseLong(myTime);
                time = time + diff;
                ob.updateValuesForResult(preSi, preQi, QuizDatabase.TimeSpent, time + "");

                //For current page..
                int SI = ob.getIntValuesPerQuestionByFragmentIndex(position, QuizDatabase.SectionIndex);
                int QI = ob.getIntValuesPerQuestionByFragmentIndex(position, QuizDatabase.QuestionIndex);
                int curQS = Integer.parseInt(ob.getValuesForResult(SI, QI, QuizDatabase.QuestionStatus));

                if (curQS != 0 && curQS != 1 && curQS != 2) {
                    setDetailsForNotAnswered(SI, QI);
                }

                ArrayList<Integer> myType = ob.getTypesOfEachSection(SI);
                for (int i = 0; i < myType.size(); ++i) {
                    Log.d("check", "setQuestionsListAdapter: type=" + myType.get(i));
                }

                SharedPreferences.Editor e = quizPrefs.edit();
                e.putInt("previousIndex", position);
                e.apply();

                start = System.currentTimeMillis();

                ob.updateValuesForResult(SI, QI, QuizDatabase.ReadStatus, 1 + "");

                int qType = ob.getTypeOfAQuestion(SI, QI);
                if (qType == -1) {
                    //Disable buttons..
                    changeButtonStatus(false);
                } else {
                    //Enable buttons..
                    changeButtonStatus(true);
                }
                setSectionTitle(SI);
                setTextViewsData(SI);
                setQuestionsListAdapter(position, SI);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        sectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor e = quizPrefs.edit();
                e.putInt("exit",0);
                e.apply();

                int num = pager.getCurrentItem();
                int sI = ob.getIntValuesPerQuestionByFragmentIndex(num, QuizDatabase.SectionIndex);
                SharedPreferences.Editor editor=quizPrefs.edit();
                editor.putInt("sI",sI);
                editor.apply();

                //Get serial number of this section from PerSectionDetails..
                String srNo = ob.getStringValuesPerSectionBySectionIndex(sI, QuizDatabase.SerialNumber);
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
                int sI = ob.getIntValuesPerSectionBySerialNumber(mySrNo, QuizDatabase.SectionIndex);
                if(quizPrefs.getInt("sI",0)==sI){
//                    my_fi = ob.getIntValuesPerQuestionBySiAndSrno(sI, 0, QuizDatabase.FragmentIndex);
                }else{
                    //Get fragment index with section index= sI and serial number=0 from PerQuestionDetails..
                    my_fi = ob.getIntValuesPerQuestionBySiAndSrno(sI, 0, QuizDatabase.FragmentIndex);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            pager.setCurrentItem(my_fi,true);

                        }
                    });
                }
            }
        } else if (requestCode == REQUEST_CODE_FOR_ALL_SUMMARY) {
            if (data != null) {
                jumpTo = data.getIntExtra("jumpTo", 0);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        pager.setCurrentItem(jumpTo,true);
                    }
                });
            }
        }
    }

    @Override
    public void SetValue(final int pos) {
        Log.d("check", "SetValue: pos=" + pos);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                pager.setCurrentItem(pos,true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_sections_summary_menu, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.summaryIcon:

                SharedPreferences.Editor e = quizPrefs.edit();
                e.putInt("exit",0);
                e.apply();

                prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                Intent i = new Intent(QuizMainActivity.this, AllSectionsSummary.class);
                i.putExtra("examId", examId);
                i.putExtra("userId", prefs.getString("userId", ""));
                i.putExtra("selectedLanguage", selectedLanguage);
                i.putExtra("date", myDate);
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
        int ss, qq, n, position,noOfQ;
        String temp;
        switch (v.getId()) {

            case R.id.submitButton:
                if(submitButton.isEnabled())
                vibrator.vibrate(200);
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.QuestionIndex);
                setDetailsForFinalAnswer(ss, qq);
                setParticularQuestionStatus(ss, qq, 0);
                setTextViewsData(ss);
                setQuestionsListAdapter(n, ss);
//                ob.getAllValues();
                break;
            case R.id.reviewButton:
                vibrator.vibrate(200);
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.QuestionIndex);
                temp = ob.getValuesForResult(ss, qq, QuizDatabase.TempAnswerSerialNumber);
                if (temp.equals("-1")) {
                    setParticularQuestionStatus(ss, qq, 2);
                    setTextViewsData(ss);
                    setQuestionsListAdapter(n, ss);
                } else {
                    setDetailsForFinalAnswer(ss, qq);
                    setParticularQuestionStatus(ss, qq, 1);
                    setTextViewsData(ss);
                    setQuestionsListAdapter(n, ss);
                }
//                ob.getAllValues();
                break;
            case R.id.clearButton:
                if(clearButton.isEnabled())
                    vibrator.vibrate(200);
                n = pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.SectionIndex);
                qq = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.QuestionIndex);
                setDetailsForNotAnswered(ss, qq);
                changeButtonStatus(false);
                setParticularQuestionStatus(ss, qq, 3);
                setTextViewsData(ss);
                setQuestionsListAdapter(n, ss);
                MyFragment fragment = (MyFragment) pageAdapter.getItem(n);
                fragment.update();
//                ob.getAllValues();
                break;
            case R.id.left:
                Log.d("checking", "onClick: first=" + linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                position=linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if(position<4){
                    questionsList.getLayoutManager().scrollToPosition(0);
                }else{
                    questionsList.getLayoutManager().scrollToPosition(linearLayoutManager.findFirstCompletelyVisibleItemPosition() - 4);
                }
                break;
            case R.id.right:
                n=pager.getCurrentItem();
                ss = ob.getIntValuesPerQuestionByFragmentIndex(n, QuizDatabase.SectionIndex);
                noOfQ=ob.getNoOfQinOneSec(ss);
                Log.d("checking", "onClick: last=" + linearLayoutManager.findLastCompletelyVisibleItemPosition());
                position=linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(position>(noOfQ-4)){
                    questionsList.getLayoutManager().scrollToPosition(noOfQ-1);
                }else{
                    questionsList.getLayoutManager().scrollToPosition(linearLayoutManager.findLastCompletelyVisibleItemPosition() + 4);
                }
                break;
        }
    }

    public void changeButtonStatus(boolean status) {
        if (status) {
            submitButton.setEnabled(true);
//            submitButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            clearButton.setEnabled(true);
//            clearButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            submitButton.setEnabled(false);
//            submitButton.setBackgroundColor(getResources().getColor(R.color.light_colorPrimary));
            clearButton.setEnabled(false);
//            clearButton.setBackgroundColor(getResources().getColor(R.color.light_colorPrimary));
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
    public void hideDialog() {

//        if(progressDialog2!=null)
//        progressDialog2.hide();
//        Toast.makeText(this, "fully_loaded", Toast.LENGTH_SHORT).show();

        count = new CountDownTimer(myTime, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                timer.setText("" + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                timeUntil = millisUntilFinished;
            }

            public void onFinish() {
                timer.setText("done!");
                submit();
            }


        };
        count.start();

    }

    @Override
    public void putDetailsForNotAnswered(final int si, final int qi, final int fi) {

        Log.d("function", "in putDetailsForNotAnswered");

        ob.updateValuesForResult(si, qi, QuizDatabase.QuestionStatus, 3 + "");
//        ob.updateValuesForResult(si,qi,QuizDatabase.TempAnswerSerialNumber,-1+"");
        ob.updateValuesForResult(si, qi, QuizDatabase.FinalAnswerSerialNumber, -1 + "");
        ob.updateValuesForResult(si, qi, QuizDatabase.FinalAnswerId, -1 + "");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTextViewsData(si);
                setQuestionsListAdapter(fi, si);
            }
        });
    }

    public void setDetailsForNotAnswered(int si, int qi) {

        ob.updateValuesForResult(si, qi, QuizDatabase.QuestionStatus, 3 + "");
        ob.updateValuesForResult(si, qi, QuizDatabase.TempAnswerSerialNumber, -1 + "");
        ob.updateValuesForResult(si, qi, QuizDatabase.FinalAnswerSerialNumber, -1 + "");
        ob.updateValuesForResult(si, qi, QuizDatabase.FinalAnswerId, -1 + "");
    }

    public void setTextViewsData(int si) {
        types = ob.getTypes(si);
        submittedQuestions.setText(types.get(0) + "");
        reviewedTickedQuestions.setText(types.get(1) + "");
        reviewedUntickedQuestions.setText(types.get(2) + "");
        clearedQuestions.setText(types.get(3) + "");
        notAttemptedQuestions.setText(types.get(4) + "");
    }

    public void setQuestionsListAdapter(int fi, int si) {
        String srNo = ob.getStringValuesPerQuestionByFragmentIndex(fi, QuizDatabase.SerialNumber);
        int sn = Integer.parseInt(srNo);
        HashMap<String, ArrayList<Integer>> my_map = ob.getAllIntValuesPerQuestionBySectionIndex(si);
        ArrayList<Integer> my_fragment_index_list = my_map.get("FragmentIndexList");
        for (int i = 0; i < my_fragment_index_list.size(); ++i) {
            Log.d("check", "setQuestionsListAdapter: fragment=" + my_fragment_index_list.get(i));
        }
        ArrayList<Integer> myType = ob.getTypesOfEachSection(si);
        for (int i = 0; i < myType.size(); ++i) {
            Log.d("check", "setQuestionsListAdapter: type=" + myType.get(i));
        }
//        Log.d("check", "setQuestionsListAdapter: srNo="+sn);
        allQuestionsInOneSectionAdapter = new AllQuestionsInOneSectionAdapter(my_fragment_index_list, QuizMainActivity.this, sn, myType);
        questionsList.setAdapter(allQuestionsInOneSectionAdapter);
        allQuestionsInOneSectionAdapter.notifyDataSetChanged();
        int myPosition = linearLayoutManager.findFirstVisibleItemPosition();
        Log.d("myPosition", "setQuestionsListAdapter: " + myPosition);
//        int noOfQ=questionArray[si];
//        int pos=0;
//        if(sn<=noOfQ/2)
//            pos=noOfQ/2;
//        else if(sn>noOfQ/2)
//            pos=noOfQ-1;
//        linearLayoutManager.scrollToPositionWithOffset(pos, 0);
        linearLayoutManager.scrollToPositionWithOffset(sn, 0);
    }

    public void setDetailsForFinalAnswer(int si, int qi) {
        String temp = ob.getValuesForResult(si, qi, QuizDatabase.TempAnswerSerialNumber);
        int tempSr = Integer.parseInt(temp);
        Log.d("myData", "tempStr=" + tempSr);
        ob.updateValuesForResult(si, qi, QuizDatabase.FinalAnswerSerialNumber, tempSr + "");
        int oi = Integer.parseInt(ob.getOptionIdBySerialNumber(tempSr + "", si, qi));
        ob.updateValuesForResult(si, qi, QuizDatabase.FinalAnswerId, oi + "");
        Log.d("MyData", "setDetailsForFinalAnswer: " + oi);
    }

    public void setParticularQuestionStatus(int si, int qi, int status) {
        ob.updateValuesForResult(si, qi, QuizDatabase.QuestionStatus, status + "");
    }

    public void setSectionTitle(int si) {
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
        socket.emit(ConstantsDefined.STARTEDSTREAMING, data);
    }

    @Override
    public void stoppedStreaming() {
        socket.emit(ConstantsDefined.STOPPEDSTREAMING, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        JSONObject data = new JSONObject();
        try {
            data.put("userId", prefs.getString("userId", ""));
            data.put("examId", examId);
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        Log.d(SOCKET, "onDestroy studentLeft" + data);
        socket.emit(ConstantsDefined.STUDENTLEFT, data);

        socket.disconnect();
        socket.off();
        socket.close();
        //when activity will distroy student will be disconnected from session
        socketfromteacher.disconnectSession();

    }

    // in/truskills/liveexams/Quiz/QuizMainActivity.java:137
    public void initSocketConnection() {
        try {
            socket = IO.socket(ConstantsDefined.socketConnectionUrl);
            Log.d("url", ConstantsDefined.socketConnectionUrl+ "found");
        } catch (URISyntaxException e) {
            Log.d("url", ConstantsDefined.socketConnectionUrl+ " not found");
        }
        //connect to server
        socket.connect();

        //in/truskills/liveexams/Quiz/FlashphonerEvents.java:71
        //teacher will emit STARTSTREAMING socket to request student for streaming
        socket.on(ConstantsDefined.STARTSTREAMING, new Emitter.Listener() {
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
                                Log.d(SOCKET, "error " + e.toString());
                            }
                            socketfromteacher.startStreaming(json.getString("studentSocketId"), json.getString("teacherSocketId"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        socket.on(ConstantsDefined.GETINFO, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SharedPreferences prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                JSONObject data = new JSONObject();
                try {
                    data.put("userId", prefs.getString("userId", ""));
                    data.put("userName",prefs.getString("userName",""));
                    data.put("examId", examId);
                    data.put("isTeacher", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(SOCKET, "got get Info " + data);
                socket.emit(ConstantsDefined.SETINFO, data);
            }
        });

        socket.on(ConstantsDefined.CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(SOCKET, "connected");
            }
        });

        socket.on(ConstantsDefined.RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(SOCKET, "reconnected");
            }
        });

        socket.on(ConstantsDefined.DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(SOCKET, "disconnected");
            }
        });


        //teacher will emit STOPSTREAMING socket to inform student to stop Streaming
        socket.on(ConstantsDefined.STOPSTREAMING, new Emitter.Listener() {
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


    public void formFragmentListForViewPager() {
        AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();
        asyncTaskRunner.execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener(QuizMainActivity.this);
        SharedPreferences.Editor e=quizPrefs.edit();
        e.putInt("exit",1);
        e.apply();
        visible=true;
        if(t!=null&&t.isAlive()){
            t.interrupt();
        }
    }

    @Override
    public void onNetworkConnectionChanged(final boolean isConnected) {
//        Toast.makeText(this, isConnected + "", Toast.LENGTH_SHORT).show();
//        if (!isConnected) {
//            builder = new android.support.v7.app.AlertDialog.Builder(QuizMainActivity.this, R.style.AppCompatAlertDialogStyle);
//            builder.setTitle("Internet Connectivity Lost..");
//            builder.setMessage("Please re-connect to resume your quiz");
//            builder.setCancelable(false);
//            mAlertDialog = builder.create();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(15000);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!isConnected) {
//                                    mAlertDialog.show();
//                                    end = System.currentTimeMillis();
//                                    diff = end - start;
//                                    diff = diff / 1000;
//                                    int preIndex = quizPrefs.getInt("previousIndex", 0);
//                                    int preSi = ob.getIntValuesPerQuestionByFragmentIndex(preIndex, QuizDatabase.SectionIndex);
//                                    int preQi = ob.getIntValuesPerQuestionByFragmentIndex(preIndex, QuizDatabase.QuestionIndex);
//                                    String myTime = ob.getValuesForResult(preSi, preQi, QuizDatabase.TimeSpent);
//                                    long time = Long.parseLong(myTime);
//                                    time = time + diff;
//                                    ob.updateValuesForResult(preSi, preQi, QuizDatabase.TimeSpent, time + "");
//                                    count.cancel();
//                                }
//                            }
//                        });
//                    }
//                }
//            }).start();
//        } else {
//            if (mAlertDialog != null) {
//                mAlertDialog.dismiss();
//            }
//            start = System.currentTimeMillis();
//            count = new CountDownTimer(timeUntil, 1000) { // adjust the milli seconds here
//
//                public void onTick(long millisUntilFinished) {
//
//                    timer.setText("" + String.format(FORMAT,
//                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
//                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
//                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
//                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
//                    timeUntil = millisUntilFinished;
//                }
//
//                public void onFinish() {
//                    timer.setText("done!");
//                    AlertDialog.Builder builder = new AlertDialog.Builder(QuizMainActivity.this);
//                    builder.setMessage("Quiz is over!! Re-directing to Home page");
//                    Thread t = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(3000);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                Intent intent = new Intent(QuizMainActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
//                                startActivity(intent);
//                            }
//                        }
//                    });
//                    t.start();
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                }
//
//            };
//            count.start();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (quizPrefs.getInt("exit", 0) == 0) {
//            Toast.makeText(this, "don'tSubmitQuiz", Toast.LENGTH_SHORT).show();
        } else {
            visible = false;
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(ConstantsDefined.time);

                    } catch (Exception e) {

                    } finally {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if (visible) {

                                } else {
                                    JSONArray jsonArray = ob.getQuizResult();
                                    final JSONObject jsonObject = new JSONObject();
                                    String selectedLanguage = dataPrefs.getString("selectedLanguage", "");
                                    String myDate = dataPrefs.getString("date", "");
                                    String userId = dataPrefs.getString("userId", "");
                                    String examId = dataPrefs.getString("examId", "");

                                    try {
                                        jsonObject.put("result", jsonArray);
                                        jsonObject.put("selectedLanguage", selectedLanguage);
                                        jsonObject.put("date", myDate);

                                        SubmitAnswerPaper submitAnswerPaper = new SubmitAnswerPaper();
                                        submitAnswerPaper.submit(ob, QuizMainActivity.this, jsonObject.toString(), userId, examId);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }
                }
            });
            t.start();
        }
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        ProgressDialog dialog;


        @Override
        protected String doInBackground(String... params) {
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
                ob.updateValuesPerSection(my_section, QuizDatabase.SerialNumber, mySectionCount + "");

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
                    ob.updateValuesPerQuestion(my_section, my_question, QuizDatabase.SerialNumber, myQuestionCount + "");
                    ob.updateValuesPerQuestion(my_section, my_question, QuizDatabase.FragmentIndex, myFragmentCount + "");
                    ob.updateValuesForResult(my_section, my_question, QuizDatabase.SerialNumber, myQuestionCount + "");
                    ob.updateValuesForResult(my_section, my_question, QuizDatabase.FragmentIndex, myFragmentCount + "");
                    String my_text = ob.getTextOfOneQuestion(my_section, my_question);

                    int numOp = ob.getNoOfOptionsInOneQuestion(my_section, my_question);

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
                        ob.updateValuesPerOption(my_section, my_question, my_option, QuizDatabase.SerialNumber, myOptionCount + "");

//                    String my_option_text=optionArray[my_section][my_question][my_option];
                        String my_option_text = ob.getTextOfOneOption(my_section, my_question, my_option);
                        options.add(my_option_text);
                    }
                    fList.add(MyFragment.newInstance(my_text, options, examId, my_section, my_question, myFragmentCount));
                }
            }
            return "done";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //Set the view pager adapter..
            if(dialog!=null)
            dialog.dismiss();
            activity_quiz_main.setVisibility(View.VISIBLE);

            pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fList);
            pager.setAdapter(pageAdapter);
            total = myFragmentCount + 1;
            pager.setOffscreenPageLimit(total);

            if(firstTime.getInt("firstTime",1)==1){
                SharedPreferences.Editor e=firstTime.edit();
                e.putInt("firstTime",0);
                e.apply();
                header.setAlpha((float)0.2);
                listLayout.setAlpha((float)0.2);
                footer.setAlpha((float)0.2);
                pager.setAlpha((float)0.2);
                breakLine.setAlpha((float)0.2);
                forAware();
            }

            else
                forQuiz();
        }


        @Override
        protected void onPreExecute() {
            activity_quiz_main.setVisibility(View.GONE);
            dialog = new ProgressDialog(QuizMainActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Preparing your questions.. Please wait...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
        }

    }

    public void forAware(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(5000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            forQuiz();
                        }
                    });

                }
            }
        }).start();
    }

    public void submit() {

        RequestQueue requestQueue;

        requestQueue = Volley.newRequestQueue(getApplicationContext());
//        ob.getAllValues();
        JSONArray jsonArray = ob.getQuizResult();
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("result", jsonArray);
            jsonObject.put("selectedLanguage", selectedLanguage);
            jsonObject.put("date", myDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ob.setSubmitTrue();

        SharedPreferences.Editor e=dataPrefs.edit();
        e.putInt("submit",1);
        e.apply();

//        try {
//            File root = new File(Environment.getExternalStorageDirectory(), "MyResult");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            File gpxfile = new File(root, "MyResult");
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(jsonObject.toString());
//            writer.flush();
//            writer.close();
////                                    Toast.makeText(AllSectionsSummary.this, "Saved", Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //Api to be connected to..
        ConstantsDefined.updateAndroidSecurityProvider(this);
        ConstantsDefined.beforeVolleyConnect();

        String myurl = ConstantsDefined.api + "answerPaper";
        Log.d("myurl", "submit: "+myurl);

        final ProgressDialog progressDialog = new ProgressDialog(QuizMainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Quiz is over..Submitting your answers.. Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                myurl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //On getting the response..
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    String success = jsonObject1.getString("success");
                    String result = jsonObject1.getString("response");
                    if (success.equals("true")) {
                        String folder_main = ".LiveExams";
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            ConstantsDefined.deleteDir(f);
                        }
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        SharedPreferences.Editor ee=quizPrefs.edit();
                        ee.clear();
                        ee.apply();
                        SharedPreferences.Editor eee=firstTime.edit();
                        eee.clear();
                        eee.apply();
                        SharedPreferences.Editor eeeee=firstTimeForRules.edit();
                        eeeee.clear();
                        eeeee.apply();
                        Toast.makeText(QuizMainActivity.this, "Your answers for the quiz have been successfully submitted..", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuizMainActivity.this, FeedbackActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    } else {
                        JSONObject jsonObject2 = new JSONObject(result);
                        ob.deleteMyTable();
                        SharedPreferences.Editor e=dataPrefs.edit();
                        e.clear();
                        e.apply();
                        String folder_main = ".LiveExams";
                        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                        if (f.exists()) {
                            ConstantsDefined.deleteDir(f);
                        }
                        Toast.makeText(QuizMainActivity.this, jsonObject2.getString("errmsg") + "", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuizMainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //In case the connection to the Api couldn't be established..
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.d("error", error.toString() + "");

                    SharedPreferences.Editor e=allow.edit();
                    e.putInt("allow",0);
                    e.apply();
                    Log.d("prefsAllow",allow.getInt("allow",1)+"");
                    Toast.makeText(QuizMainActivity.this, "Sorry! No internet connection\nYour answers will be submitted once reconnected to internet", Toast.LENGTH_LONG).show();
                    String folder_main = ".LiveExams";
                    File f = new File(Environment.getExternalStorageDirectory(), folder_main);
                    if (f.exists()) {
                        ConstantsDefined.deleteDir(f);
                    }
                    Intent intent = new Intent(QuizMainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                    startActivity(intent);
                    finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", prefs.getString("userId",""));
                params.put("examId", examId);
                params.put("answerPaper", jsonObject.toString());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}
