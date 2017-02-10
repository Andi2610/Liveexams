package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

import static java.lang.Character.FORMAT;

//This is the quiz main activity in which my fragment is loaded..

public class QuizMainActivity extends AppCompatActivity implements setValueOfPager,View.OnClickListener,MyFragmentInterface{

    //Declare the variables..
    MyPageAdapter pageAdapter;
    private static final int REQUEST_CODE=1,REQUEST_CODE_FOR_ALL_SUMMARY=2;
    SharedPreferences quizPrefs;
    ArrayList<Integer> types;
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
    TextView sectionName, submittedQuestions, reviewedQuestions, notAttemptedQuestions,timer,clearedQuestions;
    Button submitButton, reviewButton, clearButton;
    ViewPager pager;
    MySqlDatabase ob;
    ProgressDialog dialog;
    RecyclerView questionsList;
    AllQuestionsInOneSectionAdapter allQuestionsInOneSectionAdapter;
    LinearLayoutManager linearLayoutManager;
    private static final String FORMAT = "%02d:%02d:%02d";
    ArrayList<Integer> arrayForNoOfSections,arrayForQuestions,arrayForOptions;
    ArrayList<String> options;
    int mySectionCount=-1,my_section,my_question,myFragmentCount=-1,my_option;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);

        quizPrefs=getSharedPreferences("quizPrefs",Context.MODE_PRIVATE);

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
        questionsList=(RecyclerView)findViewById(R.id.questionsList);

        ob = new MySqlDatabase(QuizMainActivity.this);

        //CONNECT TO SOCKET..
        mySocketConnection();

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

                            //Parse one section attributes..
                            map6 = QuestionPaperParser.getAttributesOfSection(AttributesOfSection);

                            //Get it's variables..
                            String name = map6.get("Name");

                            //Set in database..
                            ob.setValuesPerSection(i);

                            ob.updateValuesPerSection(i,"SectionName",name);

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
                                ob.setValuesPerQuestion(i,j);
                                ob.setValuesForResult(i,j);

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

                                ob.updateValuesPerQuestion(i,j,"QuestionText",text);

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
                                    ob.setValuesPerOption(i,j,p);

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

                                    ob.updateValuesPerOption(i,j,p,"OptionText",map11.get("_"));

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

    public void afterResponse(){


        new CountDownTimer(10800000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                timer.setText(""+String.format(FORMAT,
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
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(3000);
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
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

        arrayForNoOfSections=new ArrayList<>();

        for(int i=0;i<noOfSections;++i){
            arrayForNoOfSections.add(i);
        }
        //If shuffling required, then do so..
        Collections.shuffle(arrayForNoOfSections);

        for(int i=0;i<noOfSections;++i){
            //Increase the section serial number..
            mySectionCount++;
            //Getting a section randomly..
            my_section=arrayForNoOfSections.get(i);
            //Set the serial number of this section..
            ob.updateValuesPerSection(my_section,"SerialNumber",mySectionCount+"");


            //Getting total no.of questions in that section randomly..
            num=questionArray[arrayForNoOfSections.get(i)];
            arrayForQuestions=new ArrayList<>();
            for(int j=0;j<num;++j){
                arrayForQuestions.add(j);
            }

            //If shuffling required, then do so..
            Collections.shuffle(arrayForQuestions);

            int myQuestionCount=-1;

            for (int k=0;k<num;++k){
                //Increase question serial number..
                myQuestionCount++;
                //Increase fragment serial number..
                myFragmentCount++;
                //Getting a question randomly..
                my_question=arrayForQuestions.get(k);
                //Set the serial number of this question..
                ob.updateValuesPerQuestion(my_section,my_question,"SerialNumber",myQuestionCount+"");
                ob.updateValuesPerQuestion(my_section,my_question,"FragmentIndex",myFragmentCount+"");

                String my_text=textArray[my_section][my_question];

                int numOp=myOptionsArray[my_section][my_question];

                arrayForOptions=new ArrayList<>();
                for(int p=0;p<numOp;++p){
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
                    ob.updateValuesPerOption(my_section,my_question,my_option,"SerialNumber",myOptionCount+"");

                    String my_option_text=optionArray[my_section][my_question][my_option];
                    options.add(my_option_text);
                }
                fList.add(MyFragment.newInstance(my_text, options,examId,my_section,my_question,myFragmentCount));
            }
        }


        ob.getAllValues();

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
        int sI=ob.getIntValuesPerQuestionByFragmentIndex(0,"SectionIndex");
        int qI=ob.getIntValuesPerQuestionByFragmentIndex(0,"QuestionIndex");
        Log.d("ResultDetails=",sI+" "+qI);
        ob.updateValuesForResult(sI,qI,"ReadStatus",1+"");
        String srNo=ob.getStringValuesPerQuestionByFragmentIndex(0,"SerialNumber");
        int sn=Integer.parseInt(srNo);

        ob.getAllValues();

        types=ob.getTypes();

        submittedQuestions.setText(types.get(1)+"");
        reviewedQuestions.setText(types.get(2)+"");
        clearedQuestions.setText(types.get(3)+"");
        notAttemptedQuestions.setText(types.get(0)+"");

        HashMap<String, String> map = ob.getValuesPerSection(sI);
        sectionTitle = map.get("SectionName");
        sectionName.setText(sectionTitle);

        HashMap<String,ArrayList<Integer>> my_map=ob.getAllIntValuesPerQuestionBySectionIndex(sI);
        ArrayList<Integer> my_fragment_index_list=new ArrayList<>();
        my_fragment_index_list=my_map.get("FragmentIndexList");

        Log.d("initially",sI+" "+sn+" "+sectionTitle);
        for(int l=0;l<my_fragment_index_list.size();++l){
            Log.d("initially",my_fragment_index_list.get(l)+"");
        }

        allQuestionsInOneSectionAdapter=new AllQuestionsInOneSectionAdapter(my_fragment_index_list,QuizMainActivity.this,sn);

        linearLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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
                Log.d("finally","here");

                int SI=ob.getIntValuesPerQuestionByFragmentIndex(position,"SectionIndex");
                int QI=ob.getIntValuesPerQuestionByFragmentIndex(position,"QuestionIndex");
                Log.d("ResultDetails=",SI+" "+QI);
                ob.updateValuesForResult(SI,QI,"ReadStatus",1+"");
                String srNo=ob.getStringValuesPerQuestionByFragmentIndex(position,"SerialNumber");
                int sn=Integer.parseInt(srNo);
                ob.getAllValues();

//                MyFragment fragment =(MyFragment) pageAdapter.getRegisteredFragment(position);
//                Bundle newBundle=new Bundle();
//                newBundle.putString("here","new");
//                if (fragment.getArguments() == null) {
//                    fragment.setArguments(newBundle);
//                    Log.d("here","inIf");
//
//                } else {
//                    //Consider explicitly clearing arguments here
//                    Log.d("here","inElse");
//                    fragment.getArguments().putAll(newBundle);
//                }

                HashMap<String, String> map = ob.getValuesPerSection(SI);
                sectionTitle = map.get("SectionName");
                sectionName.setText(sectionTitle);

                HashMap<String,ArrayList<Integer>> my_map=ob.getAllIntValuesPerQuestionBySectionIndex(SI);
                ArrayList<Integer> my_fragment_index_list=new ArrayList<>();
                my_fragment_index_list=my_map.get("FragmentIndexList");
                allQuestionsInOneSectionAdapter=new AllQuestionsInOneSectionAdapter(my_fragment_index_list,QuizMainActivity.this,sn);
                questionsList.setAdapter(allQuestionsInOneSectionAdapter);
                allQuestionsInOneSectionAdapter.notifyDataSetChanged();

                Log.d("finally",SI+" "+sn+" "+sectionTitle);
                for(int l=0;l<my_fragment_index_list.size();++l){
                    Log.d("finally",my_fragment_index_list.get(l)+"");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        sectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num=pager.getCurrentItem();
                int sI=ob.getIntValuesPerQuestionByFragmentIndex(num,"SectionIndex");
                //Get serial number of this section from PerSectionDetails..
                String srNo=ob.getStringValuesPerSectionBySectionIndex(sI,"SerialNumber");
//                SharedPreferences.Editor e=quizPrefs.edit();
//                e.putInt("mySectionIndex",s);
//                e.putInt("myQuestionIndex",q);
//                e.apply();
                Intent i =new Intent(QuizMainActivity.this,SectionNamesDisplay.class);
                i.putExtra("serialNumber",srNo);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
        submitButton.setOnClickListener(this);
        reviewButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE){
            if(data!=null){
                int mySrNo=data.getIntExtra("message",0);
                //Get SectionIndex by srNo..
                int sI=ob.getIntValuesPerSectionBySerialNumber(mySrNo,"SectionIndex");

                //Get fragment index with section index= sI and serial number=0 from PerQuestionDetails..
                int my_fi=ob.getIntValuesPerQuestionBySiAndSrno(sI,0,"FragmentIndex");

                pager.setCurrentItem(my_fi);

                HashMap<String, String> map = ob.getValuesPerSection(sI);
                sectionTitle = map.get("SectionName");
                sectionName.setText(sectionTitle);

            }
        }else if(requestCode==REQUEST_CODE_FOR_ALL_SUMMARY){
            if(data!=null){
                int jumpTo=data.getIntExtra("jumpTo",0);
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
        switch (item.getItemId()){
            case R.id.rulesIcon:
                Intent i=new Intent(QuizMainActivity.this,AllSectionsSummary.class);
                startActivityForResult(i,REQUEST_CODE_FOR_ALL_SUMMARY);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        int ss,qq,n;
        switch (v.getId()){

            case R.id.submitButton:
                n=pager.getCurrentItem();
                ss=ob.getIntValuesPerQuestionByFragmentIndex(n,"SectionIndex");
                qq=ob.getIntValuesPerQuestionByFragmentIndex(n,"QuestionIndex");
                Log.d("here",ss+" "+qq);
                ob.updateValuesForResult(ss,qq,"QuestionStatus",1+"");
                ob.getAllValues();
                types=ob.getTypes();
                submittedQuestions.setText(types.get(1)+"");
                reviewedQuestions.setText(types.get(2)+"");
                clearedQuestions.setText(types.get(3)+"");
                notAttemptedQuestions.setText(types.get(0)+"");
                break;
            case R.id.reviewButton:
                n=pager.getCurrentItem();
                ss=ob.getIntValuesPerQuestionByFragmentIndex(n,"SectionIndex");
                qq=ob.getIntValuesPerQuestionByFragmentIndex(n,"QuestionIndex");
                Log.d("here",ss+" "+qq);
                ob.updateValuesForResult(ss,qq,"QuestionStatus",2+"");
                ob.getAllValues();
                types=ob.getTypes();
                submittedQuestions.setText(types.get(1)+"");
                reviewedQuestions.setText(types.get(2)+"");
                clearedQuestions.setText(types.get(3)+"");
                notAttemptedQuestions.setText(types.get(0)+"");
                break;
            case R.id.clearButton:
                n=pager.getCurrentItem();
                ss=ob.getIntValuesPerQuestionByFragmentIndex(n,"SectionIndex");
                qq=ob.getIntValuesPerQuestionByFragmentIndex(n,"QuestionIndex");
                Log.d("here",ss+" "+qq);
                ob.updateValuesForResult(ss,qq,"QuestionStatus",3+"");
                ob.updateValuesForResult(ss,qq,"FinalAnswerSerialNumber",-1+"");
                //Clear webview content..
                //.......................................................................
                ob.getAllValues();
                types=ob.getTypes();
                submittedQuestions.setText(types.get(1)+"");
                reviewedQuestions.setText(types.get(2)+"");
                clearedQuestions.setText(types.get(3)+"");
                notAttemptedQuestions.setText(types.get(0)+"");
                break;
        }
    }

    @Override
    public void enableButtons() {
        submitButton.setEnabled(true);
        clearButton.setEnabled(true);
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
        Log.d("messi","inOnDestroy");
        ob.deleteMyTable();
    }

    public void mySocketConnection(){

    }
}
