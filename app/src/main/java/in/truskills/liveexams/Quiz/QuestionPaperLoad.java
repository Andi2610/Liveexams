package in.truskills.liveexams.Quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import in.truskills.liveexams.JsonParsers.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.ConnectivityReciever;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.Miscellaneous.MyApplication;
import in.truskills.liveexams.authentication.SplashScreen;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

public class QuestionPaperLoad extends AppCompatActivity {

    //Declare the variables..
    int languageArray[][], fragmentIndex[][];
    LinkedList ll = new LinkedList();
    HashMap<Integer,String> myMap=new HashMap<>();
    byte[] key, iv;
    String html;
    LinkedList llGroup = new LinkedList();
    public static boolean visible = true, gone = false, download = false;
    ImageRequest ir;
    HashMap<String, String> map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11, map12;
    int noOfQuestions = 0, noOfExamName, noOfLanguage, noOfOption, noOfSections, fi = -1, hour, minute, myTime,myC=0, curCount = 0, myCount = 0, questionArray[];
    RequestQueue requestQueue;
    String url, success, response, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri, section_id, section_max_marks, section_time, section_description, section_rules;
    String questionAttributes, opText, examDuration, examId, name, selectedLanguage, myExamDuration, paperName, myDate, myUrl;
    ArrayList<Fragment> fList;
    float per;
    String myResponseResult;
    QuizDatabase ob;
    ArrayList<String> urls;
    SharedPreferences prefs, dataPrefs;
    Handler handler = new Handler();

    Button retryButtonForDownload, exitButtonForDownload;
    LinearLayout paperGettingReadyLayout, paperGettingDownloadLayout, noInternetLayout,paperGettingStartedLayout;
    TextView paperGettingReadyMessage, paperGettingDownloadMessage,paperGettingStartedMessage, noInternetMessage, paperGettingDownloadPercentage;
    ProgressBar progressBar;
    com.wang.avi.AVLoadingIndicatorView avi,aviStarted;

    Handler h;
    AsyncTaskRunner asyncTaskRunner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_question_paper_load);

        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        aviStarted = (AVLoadingIndicatorView) findViewById(R.id.aviStarted);
        h = new Handler();

        paperGettingDownloadLayout = (LinearLayout) findViewById(R.id.paperGettingDownloadLayout);
        paperGettingStartedLayout = (LinearLayout) findViewById(R.id.paperGettingStartedLayout);
        noInternetLayout = (LinearLayout) findViewById(R.id.noInternetLayout);
        paperGettingReadyLayout = (LinearLayout) findViewById(R.id.paperGettingReadyLayout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        dataPrefs = getSharedPreferences("dataPrefs", Context.MODE_PRIVATE);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Drawable customDrawable = getResources().getDrawable(R.drawable.custom_progressbar);

        // set the drawable as progress drawavle

        progressBar.setProgressDrawable(customDrawable);

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        retryButtonForDownload = (Button) findViewById(R.id.retryButtonForDownload);
        exitButtonForDownload = (Button) findViewById(R.id.exitButtonForDownload);

        noInternetMessage = (TextView) findViewById(R.id.noInternetMessage);
        paperGettingDownloadPercentage = (TextView) findViewById(R.id.paperGettingDownloadPercentage);
        paperGettingDownloadMessage = (TextView) findViewById(R.id.paperGettingDownloadMessage);
        paperGettingReadyMessage = (TextView) findViewById(R.id.paperGettingReadyMessage);
        paperGettingStartedMessage = (TextView) findViewById(R.id.paperGettingStartedMessage);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        noInternetMessage.setTypeface(tff1);
        paperGettingDownloadMessage.setTypeface(tff1);
        paperGettingReadyMessage.setTypeface(tff1);
        retryButtonForDownload.setTypeface(tff1);
        exitButtonForDownload.setTypeface(tff1);
        paperGettingDownloadPercentage.setTypeface(tff1);
        paperGettingStartedMessage.setTypeface(tff1);

        examId = getIntent().getStringExtra("examId");
        paperName = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");
        myDate = getIntent().getStringExtra("date");
        myUrl = getIntent().getStringExtra("url");

        fList = new ArrayList<>();
        urls = new ArrayList<>();
        ob = new QuizDatabase(this);

        paperGettingDownloadLayout.setVisibility(View.GONE);
        paperGettingReadyLayout.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);
        paperGettingStartedLayout.setVisibility(View.GONE);

        downloadQP();
    }

    public void downloadQP() {

        Log.d("heree", "downloadQP: ");

        download = false;

        paperGettingDownloadLayout.setVisibility(View.GONE);
        paperGettingReadyLayout.setVisibility(View.VISIBLE);
        noInternetLayout.setVisibility(View.GONE);
        paperGettingStartedLayout.setVisibility(View.GONE);

//        ConstantsDefined.updateAndroidSecurityProvider(this);
//        ConstantsDefined.beforeVolleyConnect();

        //Api to be connected to get the question paper..
        url = ConstantsDefined.api + "questionPaper/" + examId;
//        url = ConstantsDefined.api2 + "questionPaper/" + "58f1f0b822857c29f51db394";
        //Make the request..
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String result) {

                Log.d("myResponse", "onResponse: "+result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                download = true;
                                myResponseResult = result;
                                asyncTaskRunner = new AsyncTaskRunner();
                                asyncTaskRunner.execute();
                            }
                        });
                    } else {
                        paperGettingDownloadLayout.setVisibility(View.GONE);
                        paperGettingReadyLayout.setVisibility(View.GONE);
                        noInternetLayout.setVisibility(View.VISIBLE);
                        paperGettingStartedLayout.setVisibility(View.GONE);
                        Toast.makeText(QuestionPaperLoad.this, "Something went wrong..\n" +
                                "Please try again..", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //If connection couldn't be made..
                Toast.makeText(QuestionPaperLoad.this, "Sorry! Couldn't connect..Please try again..", Toast.LENGTH_SHORT).show();
                paperGettingDownloadLayout.setVisibility(View.GONE);
                paperGettingReadyLayout.setVisibility(View.GONE);
                noInternetLayout.setVisibility(View.VISIBLE);
                paperGettingStartedLayout.setVisibility(View.GONE);
            }
        });
        requestQueue.add(stringRequest);
    }

    public void afterResponse() throws Exception {

        if (ll.isEmpty()) {
            setData();
        } else {
            downloadImages();
        }

        retryButtonForDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "onClick: retry");
                try {
                    checkFunction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        exitButtonForDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ob.deleteMyTable();
                SharedPreferences.Editor e = dataPrefs.edit();
                e.clear();
                e.apply();
                finish();
            }
        });

    }

    public void prepareForOfflineForQuestion(String text, int ii, int jj) throws InterruptedException {
        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(JPG|jpg|gif|png))";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            myCount++;
            Log.d("ronaldo", "matcher.findInLoop");
            String group = matcher.group(2);
            String imageUrl = myUrl + examId + "/Images/" + group;
            Log.d("imageUrl", "prepareForOfflineForQuestion: "+imageUrl);
            urls.add(imageUrl);
            ll.add(imageUrl);
            llGroup.add(group);
        }
    }

    public void prepareForOfflineForOption(String text, int ii, int jj, int kk) throws InterruptedException {
        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(JPG|jpg|gif|png))";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            myCount++;
            Log.d("messi", "matcher.findInLoop");
            String group = matcher.group(2);
            String imageUrl = myUrl + examId + "/Images/" + group;
            Log.d("imageDownload", imageUrl);
            urls.add(imageUrl);
            ll.add(imageUrl);
            llGroup.add(group);
        }
    }

    public void setQuestionData(String text, int ii, int jj) throws InterruptedException {

        final String regex = "([ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(JPG|jpg|gif|png)))";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        String result=text;
        while (matcher.find()) {
            myC++;
            Log.d("ronaldo", "matcher.findInLoop");
            String myGr=matcher.group(1);
            String image=myMap.get(myC);
            result = result.replace(myGr,"<img src=\""+image+"\" style=\"max-width:100%;\"/>");
        }

        Log.d("result", "prepareForOfflineForQuestionDecrypt: after:"+result);

        ob.updateValuesPerQuestion(ii, jj, QuizDatabase.QuestionText, result);
    }

    public void setOptionData(String text, int ii, int jj, int kk) throws InterruptedException {
        final String regex = "([ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(JPG|jpg|gif|png)))";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        String result=text;
        while (matcher.find()) {
            myC++;
            Log.d("messi", "matcher.findInLoop");
            String myGr=matcher.group(1);
            String image=myMap.get(myC);
            result = result.replace(myGr,"<img src=\""+image+"\" style=\"max-width:100%;\"/>");
        }

        Log.d("result", "prepareForOfflineForOptionDecrypt: after:"+result);

        ob.updateValuesPerOption(ii, jj, kk, QuizDatabase.OptionText, result);
    }

    public String format(String str, String examId) {

        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(jpg|gif|png|JPG))";
        final String subst = "<img src=\"" + myUrl + "" + examId + "/Images/$2\"/>";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);

// The substituted value will be contained in the result variable
        String result = matcher.replaceAll(subst);

        return result;
    }

    public void prepareForOnlineForQuestion(int ii, int jj, String text) {
        String myText = format(text, examId);
        ob.updateValuesPerQuestion(ii, jj, QuizDatabase.QuestionText, myText);
    }

    public void prepareForOnlineForOption(int ii, int jj, int kk, String text) {
        String myText = format(text, examId);
        ob.updateValuesPerOption(ii, jj, kk, QuizDatabase.OptionText, myText);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        visible = true;
        handler.removeCallbacks(sendData);
    }

    private class AsyncTaskRunner2 extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            paperGettingDownloadLayout.setVisibility(View.GONE);
            paperGettingReadyLayout.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.GONE);
            paperGettingStartedLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            for (int i = 0; i < noOfSections; ++i) {
                for (int j = 0; j < questionArray[i]; ++j) {
                    String mt = ob.getTextOfOneQuestion(i, j);
                    try {
                        setQuestionData(mt, i, j);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("textHere", "OfQuestions" + mt);
                    int noo = ob.getNoOfOptionsInOneQuestion(i, j);
                    for (int k = 0; k < noo; ++k) {
                        String mo = ob.getTextOfOneOption(i, j, k);
                        Log.d("textHere", "OfOptions" + mo);
                        try {
                            setOptionData(mo, i, j, k);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            return "done";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            gone = true;

            Intent intent = new Intent(QuestionPaperLoad.this, QuizMainActivity.class);
            intent.putExtra("examId", examId);
            intent.putExtra("name", paperName);
            intent.putExtra("language", selectedLanguage);
            intent.putExtra("noOfSections", noOfSections);
            intent.putExtra("questionArray", questionArray);
            intent.putExtra("ExamDuration", myTime);
            intent.putExtra("date", myDate);
            startActivity(intent);
            finish();

        }
    }

    public void setData() {

        AsyncTaskRunner2 a=new AsyncTaskRunner2();
        a.execute();

    }

    public void downloadImages() throws Exception {

        if (!ll.isEmpty()) {
            myFunc((String) ll.get(0), (String) llGroup.get(0));
        } else {
            setData();
        }

    }

    public void checkFunction() throws Exception {
        if (!download) {
            downloadQP();
        } else if (!ll.isEmpty()) {
            downloadImages();
        } else {
            setData();
        }
    }

    public void myFunc(String myUrl, final String myGroup) {

        Log.d("myUrl", "myFunc: "+myUrl);

        paperGettingDownloadLayout.setVisibility(View.VISIBLE);
        paperGettingReadyLayout.setVisibility(View.GONE);
        noInternetLayout.setVisibility(View.GONE);
        paperGettingStartedLayout.setVisibility(View.GONE);

        ir = new ImageRequest(myUrl, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap bitmap) {
                curCount++;
                Log.d("count", curCount + " " + myCount);
                per = (curCount / (float) myCount) * 100;
                paperGettingDownloadPercentage.setText((int) per + "%");
                progressBar.setProgress((int) per);
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    String image = "data:image/png;base64," + imgageBase64;
                    myMap.put(curCount,image);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("save", "onResponse: " + e.toString());
                }
                ll.removeFirst();
                llGroup.removeFirst();
                if (!ll.isEmpty())
                    myFunc((String) ll.get(0), (String) llGroup.get(0));
                else {
                    setData();
                }
            }
        }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(QuestionPaperLoad.this, "Sorry! Couldn't connect..", Toast.LENGTH_LONG).show();
                        paperGettingDownloadLayout.setVisibility(View.GONE);
                        paperGettingReadyLayout.setVisibility(View.GONE);
                        noInternetLayout.setVisibility(View.VISIBLE);
                        paperGettingStartedLayout.setVisibility(View.GONE);
                    }
                });
        requestQueue.add(ir);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            try {
                //Parse the result..
                map1 = QuestionPaperParser.resultParser(myResponseResult);
                //Get success..
                success = map1.get("success");
                if (success.equals("true")) {


                    //Get response..
                    response = map1.get("response");

                    //Parse response..
                    map2 = QuestionPaperParser.responseParser(response);
                    examDuration = map2.get("ExamDuration");
                    myExamDuration = QuestionPaperParser.getExamDuration(examDuration);
                    String[] parts = myExamDuration.split("-");
                    hour = Integer.parseInt(parts[0]);
                    minute = Integer.parseInt(parts[1]);
                    myTime = hour * 60 * 60 * 1000 + minute * 60 * 1000;
                    Log.d("myTime", myTime + "");

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

                    languageArray = new int[noOfSections][];
                    fragmentIndex = new int[noOfSections][];
                    questionArray = new int[noOfSections];

                    //Loop through all the sections..
                    for (int i = 0; i < noOfSections; ++i) {

                        final int iiii = i;

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
                        Log.d("ID:", i + "-" + section_id);

                        //Set in database..
                        ob.setValuesPerSection(i);
                        ob.updateValuesPerSection(iiii, QuizDatabase.SectionName, name);
                        ob.updateValuesPerSection(iiii, QuizDatabase.SectionId, section_id);
                        ob.updateValuesPerSection(iiii, QuizDatabase.SectionMaxMarks, section_max_marks);
                        ob.updateValuesPerSection(iiii, QuizDatabase.SectionTime, section_time);
                        ob.updateValuesPerSection(iiii, QuizDatabase.SectionDescription, section_description);
                        ob.updateValuesPerSection(iiii, QuizDatabase.SectionRules, section_rules);
                        map7 = QuestionPaperParser.SectionQuestionsParser(SectionQuestions);

                        //Get it's variables..
                        Question = map7.get("Question");

                        //Get no. of questions in one section..
                        noOfQuestions = QuestionPaperParser.getNoOfQuestionInOneSection(Question);

                        questionArray[i] = noOfQuestions;

                        fragmentIndex[i] = new int[noOfQuestions];

                        //Loop through all the questions of one section..
                        for (int j = 0; j < noOfQuestions; ++j) {

                            final int jjjj = j;

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
//                            myAskedIn = map8.get("AskedIn");
                            myLanguage = map8.get("Language");
                            questionAttributes = map8.get("Attributes");

//                            //Parse one section one question askedIn..
//                            map9 = QuestionPaperParser.AskedInParser(myAskedIn);
//
//                            //Get it's variables..
//                            myExamName = map9.get("ExamName");
//                            myYear = map9.get("Year");
//
//                            //Get no. of Exam names in which the question has been asked..
//                            noOfExamName = QuestionPaperParser.getLengthOfExamName(myExamName);
//
//                            Log.d("noOfExamName", noOfExamName + "");

                            //Loop through the entire exam and year array..
//                            for (int k = 0; k < noOfExamName; ++k) {
//                                //Get exam name one by one..
//                                nm = QuestionPaperParser.getExamNamesOfOneQuestion(myExamName, k);
//                                //Get Year one by one..
//                                nmm = QuestionPaperParser.getYearsOfOneQuestion(myYear, k);
//                            }

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

                            map12 = QuestionPaperParser.getAttributesOfQuestion(questionAttributes);
                            Log.d("QID:", i + "-" + j + "-" + section_id + "-" + map12.get("id"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionText, text);
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.CorrectAnswer, map12.get("CorrectAnswer"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionCorrectMarks, map12.get("QuestionCorrectMarks"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionIncorrectMarks, map12.get("QuestionIncorrectMarks"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.PassageID, map12.get("PassageID"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionType, map12.get("QuestionType"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionTime, map12.get("QuestionTime"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionDifficultyLevel, map12.get("QuestionDifficultyLevel"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionRelativeTopic, map12.get("QuestionRelativeTopic"));
                            ob.updateValuesPerQuestion(iiii, jjjj, QuizDatabase.QuestionId, map12.get("id"));
                            ob.updateValuesForResult(iiii, jjjj, QuizDatabase.SectionId, section_id);
                            ob.updateValuesForResult(iiii, jjjj, QuizDatabase.QuestionId, map12.get("id"));
                            ob.updateValuesForResult(iiii, jjjj, QuizDatabase.TempAnswerSerialNumber, -1 + "");
                            myOption = QuestionPaperParser.OptionsParser(myOptions);

                            //Get length of option array..
                            noOfOption = QuestionPaperParser.getLengthOfOptionArray(myOption);

                            //Loop through entire option array..
                            for (int p = 0; p < noOfOption; ++p) {

                                final int pppp = p;

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
                                opText = map11.get("_");

                                ob.updateValuesPerOption(iiii, jjjj, pppp, QuizDatabase.OptionText, opText);
                                ob.updateValuesPerOption(iiii, jjjj, pppp, QuizDatabase.OptionId, myAttri);

                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //        If offline required..
            for (int i = 0; i < noOfSections; ++i) {
                for (int j = 0; j < questionArray[i]; ++j) {
                    String mt = ob.getTextOfOneQuestion(i, j);
                    try {
                        prepareForOfflineForQuestion(mt, i, j);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Log.d("textHere", "OfQuestions" + mt);
                    int noo = ob.getNoOfOptionsInOneQuestion(i, j);
                    for (int k = 0; k < noo; ++k) {
                        String mo = ob.getTextOfOneOption(i, j, k);
                        Log.d("textHere", "OfOptions" + mo);
                        try {
                            prepareForOfflineForOption(mo, i, j, k);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            return "done";
        }


        @Override
        protected void onPostExecute(String result) {
            try {
                afterResponse();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gone) {

        } else {
            visible = false;
            handler.postDelayed(sendData, ConstantsDefined.time);

        }
    }

    private final Runnable sendData = new Runnable() {
        public void run() {
            if (visible) {

            } else {
                if (gone) {

                } else {
                    ob.deleteMyTable();
                    SharedPreferences.Editor e = dataPrefs.edit();
                    e.clear();
                    e.apply();
                    Toast.makeText(QuestionPaperLoad.this, "Couldn't start your quiz.. Please try again..", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(QuestionPaperLoad.this, SplashScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
