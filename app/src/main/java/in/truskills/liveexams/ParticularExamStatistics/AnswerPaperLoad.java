package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.truskills.liveexams.JsonParsers.AnswerKeyParser;
import in.truskills.liveexams.JsonParsers.AnswerPaperParser;
import in.truskills.liveexams.JsonParsers.EndExamAnalyticsParser;
import in.truskills.liveexams.JsonParsers.EndStudentAnalyticsParser;
import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.JsonParsers.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.SqliteDatabases.AnalyticsDatabase;

public class AnswerPaperLoad extends AppCompatActivity {


    //Declare the variables..
    int languageArray[][], fragmentIndex[][];
    HashMap<String, String> map2, map3, map4, map5, map6, map7, map8, map9, map10, map11, map12;
    int noOfQuestions, noOfExamName, noOfLanguage, noOfOption, noOfSections, fi = -1, questionArray[];
    RequestQueue requestQueue;
    String url, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri, section_id, section_max_marks, section_time, section_description, section_rules;
    String questionAttributes, opText, examDuration, examId, name, selectedLanguage, myExamDuration, questionPaperResponse, answerKeyResponse, endStudentAnalyticsResponse, endExamAnalyticsResponse, answerPaperResponse;
    String startTime, endTime, totalMarks, score, attempts, myStartTime, myEndTime, examName, date, bestScore, averageScore, totalStudents;
    ArrayList<Fragment> fList;
    String myUrl,rank;
    TextView myWaitMessage;
    AnalyticsDatabase ob;
    ArrayList<String> urls, groups;
    String myResponseResult,myResponseLanguage,myResponseUrl;
    SharedPreferences prefs;
    Handler h;
    //myExamDuration,myStartTime,myEndTime,

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_paper_load);
        com.wang.avi.AVLoadingIndicatorView avi = (AVLoadingIndicatorView) findViewById(R.id.aviForAnswer);
        avi.show();

        myWaitMessage = (TextView) findViewById(R.id.myWaitMessageForAnswer);
        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        myWaitMessage.setTypeface(tff1);

        examId = getIntent().getStringExtra("examId");
        examName = getIntent().getStringExtra("examName");
        h=new Handler();
//        selectedLanguage="English";

        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        fList = new ArrayList<>();
        urls = new ArrayList<>();
        groups = new ArrayList<>();
        ob = new AnalyticsDatabase(this);

        getUrl();

    }

    public void setQuestionPaperResponse(String response, String language,final String myUrl) {

        myResponseResult=response;
        myResponseLanguage=language;
        myResponseUrl=myUrl;

        AsyncTaskRunner asyncTaskRunner=new AsyncTaskRunner();
        asyncTaskRunner.execute();

    }

    public void setEndStudentAnalyticsResponse(String response) throws JSONException {
        HashMap<String, String> map = EndStudentAnalyticsParser.responseParser(response);
        int si;
        rank=map.get("rank");
        String analytics = map.get("analytics");
        JSONArray jsonArray;
        int len;
        HashMap<String, String> mapper = EndStudentAnalyticsParser.analyticsParser(analytics);
        String sectionWiseMarks = mapper.get("sectionWiseMarks");
        String sectionWiseTimeSpent = mapper.get("sectionWiseTimeSpent");
        String sectionWiseAttemptedQuestions = mapper.get("sectionWiseAttemptedQuestions");
        score = mapper.get("totalMarks");
        attempts = mapper.get("totalAttemptedQuestions");
        jsonArray = new JSONArray(sectionWiseMarks);
        len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            HashMap<String, String> mapper1 = EndStudentAnalyticsParser.sectionWiseMarksParser(sectionWiseMarks, i);
            int swm = Integer.parseInt(mapper1.get("sectionMarks"));
            si = Integer.parseInt(mapper1.get("sectionId"));
            ob.updateValuesPerSectionById(si, AnalyticsDatabase.SectionWiseMarks, swm + "");
        }
        jsonArray = new JSONArray(sectionWiseTimeSpent);
        len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            HashMap<String, String> mapper2 = EndStudentAnalyticsParser.sectionWiseTimeSpentParser(sectionWiseTimeSpent, i);
            int swts = Integer.parseInt(mapper2.get("timespent"));
            si = Integer.parseInt(mapper2.get("sectionId"));
            ob.updateValuesPerSectionById(si, AnalyticsDatabase.SectionWiseTimeSpent, swts + "");
        }
        jsonArray = new JSONArray(sectionWiseAttemptedQuestions);
        len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            HashMap<String, String> mapper3 = EndStudentAnalyticsParser.sectionWiseAttemptedQuestionsParser(sectionWiseAttemptedQuestions, i);
            int swaq = Integer.parseInt(mapper3.get("attemptedQuestions"));
            si = Integer.parseInt(mapper3.get("sectionId"));
            ob.updateValuesPerSectionById(si, AnalyticsDatabase.SectionWiseAttemptedQuestions, swaq + "");
        }
    }

    public void setEndExamAnalyticsResponse(String response) throws JSONException {
        HashMap<String, String> map = EndExamAnalyticsParser.responseParser(response);
        bestScore = map.get("bestScore");
        averageScore = map.get("avgScore");
        totalStudents = map.get("totalStudents");
        String analytics = map.get("analytics");
        int si, qi;
        JSONArray jsonArray = new JSONArray(analytics);
        int len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            HashMap<String, String> mapper = EndExamAnalyticsParser.analyticsParser(analytics, i);
            si = Integer.parseInt(mapper.get("sectionId"));
            qi = Integer.parseInt(mapper.get("questionId"));
            int rightAnsweredBy = Integer.parseInt(mapper.get("rightAnsweredBy"));
            int minimumTime = Integer.parseInt(mapper.get("minimumTime"));
            int maximumTime = Integer.parseInt(mapper.get("maximumTime"));
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.RightAnsweredBy, rightAnsweredBy + "");
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.MinimumTime, minimumTime + "");
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.MaximumTime, maximumTime + "");
        }
    }

    public void setAnswerPaperResponse(String response) throws JSONException {
        HashMap<String, String> map = AnswerPaperParser.responseParser(response);
        String answerPaper = map.get("answerPaper");
        JSONArray jsonArray = new JSONArray(answerPaper);
        int len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            HashMap<String, String> mapper = AnswerPaperParser.answerPaperParser(answerPaper, i);
            int finalAnswerId = Integer.parseInt(mapper.get("finalAnswerId"));
            int timeSpent = Integer.parseInt(mapper.get("timeSpent"));
            int si = Integer.parseInt(mapper.get("sectionId"));
            int qi = Integer.parseInt(mapper.get("questionId"));
            int qs = Integer.parseInt(mapper.get("questionStatus"));
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.FinalAnswerId, finalAnswerId + "");
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.TimeSpent, timeSpent + "");
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.QuestionStatus, qs + "");
        }
    }

    public void setAnswerKeyResponse(String response) throws JSONException {
        HashMap<String, String> map = AnswerKeyParser.responseParser(response);
        String answerKey = map.get("answerKey");
        JSONArray jsonArray = new JSONArray(answerKey);
        int len = jsonArray.length();
        for (int i = 0; i < len; ++i) {
            HashMap<String, String> mapper = AnswerKeyParser.answerKeyParser(answerKey, i);
            int rightAnswer = Integer.parseInt(mapper.get("rightAnswer"));
            int si = Integer.parseInt(mapper.get("sectionId"));
            int qi = Integer.parseInt(mapper.get("questionId"));
            ob.updateValuesPerQuestionById(si, qi, AnalyticsDatabase.RightAnswer, rightAnswer + "");
        }
    }

    public void formDataForViewPager() {
        int cnt = -1;
        for (int i = 0; i < noOfSections; ++i) {
            for (int j = 0; j < questionArray[i]; ++j) {
                cnt++;
                ob.updateValuesPerQuestion(i, j, AnalyticsDatabase.FragmentIndex, cnt + "");
                String my_text = ob.getTextOfOneQuestion(i, j);
                formatTextForQuestion(my_text, i, j);
                int numOp = ob.getNoOfOptionsInOneQuestion(i, j);
                for (int k = 0; k < numOp; ++k) {
                    String my_option_text = ob.getTextOfOneOption(i, j, k);
                    formatTextForOption(my_option_text, i, j, k);
                }
            }
        }
    }

    public void formatTextForQuestion(String text, int ii, int jj) {
        String myText = format(text, examId);
        ob.updateValuesPerQuestion(ii, jj, AnalyticsDatabase.QuestionText, myText);
    }

    public void formatTextForOption(String text, int ii, int jj, int kk) {
        String myText = format(text, examId);
        ob.updateValuesPerOption(ii, jj, kk, AnalyticsDatabase.OptionText, myText);
    }

    public  String format(String str, String examId) {

        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(jpg|gif|png))";
        final String subst = "<img src=\"" + myUrl + "" + examId + "/Images/$2\"/>";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);

// The substituted value will be contained in the result variable
        String result = matcher.replaceAll(subst);

        return result;
    }

    public void getUrl(){
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = ConstantsDefined.api + "getS3Url";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    myUrl=response.getString("response");
                    Log.d("myDateeeee", "run: "+myUrl);
                    h.post(new Runnable() {
                        @Override
                        public void run() {
//                            Log.d("dateeeee", "run: "+myDate);
//                            afterResponse(myDate);

                            afterConnect(myUrl);


                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(ConstantsDefined.isOnline(AnswerPaperLoad.this)){
                    //Do nothing..
                    Toast.makeText(AnswerPaperLoad.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AnswerPaperLoad.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void afterConnect(final String myUrl){
        //Api to be connected to get the question paper..
        url = ConstantsDefined.api + "getAnswerAnalytics";
        //Make the request..
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String response = jsonObject.getString("response");
                    String success = jsonObject.getString("success");
                    if (success.equals("true")) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        questionPaperResponse = jsonObject1.getString("questionPaper");
                        answerKeyResponse = jsonObject1.getString("answerKey");
                        answerPaperResponse = jsonObject1.getString("answerPaper");
                        endExamAnalyticsResponse = jsonObject1.getString("endExamAnalytics");
                        endStudentAnalyticsResponse = jsonObject1.getString("EndStudentAnalytics");
                        HashMap<String, String> map = AnswerPaperParser.responseParser(answerPaperResponse);
                        date = map.get("date");
                        selectedLanguage = map.get("selectedLanguage");
                        setQuestionPaperResponse(questionPaperResponse, selectedLanguage,myUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //If connection couldn't be made..
                Toast.makeText(AnswerPaperLoad.this, "Sorry! Couldn't connect..\nPlease try again later..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                //Put all the required parameters for the post request..
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", prefs.getString("userId", ""));
                params.put("examId", examId);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(String... params) {
            try {
                //Parse response..
                map2 = QuestionPaperParser.responseParser(myResponseResult);
                examDuration = map2.get("ExamDuration");
                examDuration = QuestionPaperParser.getExamDuration(examDuration);
                myExamDuration = MiscellaneousParser.parseDuration(examDuration);
                startTime = map2.get("StartTime");
                myStartTime = MiscellaneousParser.parseTimeForDetails(startTime);
                endTime = map2.get("EndTime");
                myEndTime = MiscellaneousParser.parseTimeForDetails(endTime);
                totalMarks = map2.get("MaximumMarks");

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
                    String my_section_time = MiscellaneousParser.parseDuration(section_time);

                    //Parse one section attributes..
                    map6 = QuestionPaperParser.getAttributesOfSection(AttributesOfSection);

                    //Get it's variables..
                    name = map6.get("Name");
                    section_id = map6.get("id");

                    //Set in database..
                    ob.setValuesPerSection(i);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    ob.updateValuesPerSection(iiii, AnalyticsDatabase.SectionId, section_id);
                    ob.updateValuesPerSection(iiii, AnalyticsDatabase.SectionName, name);
                    ob.updateValuesPerSection(iiii, AnalyticsDatabase.SectionMarks, section_max_marks);
                    ob.updateValuesPerSection(iiii, AnalyticsDatabase.SectionTime, my_section_time);
//                        }
//                    }).start();
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
                        int index = QuestionPaperParser.getIndex(myResponseLanguage, myLanguage);
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

//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
                        ob.updateValuesPerQuestion(iiii, jjjj, AnalyticsDatabase.SectionId, section_id);
                        ob.updateValuesPerQuestion(iiii, jjjj, AnalyticsDatabase.QuestionId, map12.get("id"));
                        ob.updateValuesPerQuestion(iiii, jjjj, AnalyticsDatabase.QuestionText, text);
//                            }
//                        }).start();
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

//                            new Thread(new Runnable() {
//                                @Override
//                                public void run() {
                            ob.updateValuesPerOption(iiii, jjjj, pppp, AnalyticsDatabase.OptionId, myAttri);
                            ob.updateValuesPerOption(iiii, jjjj, pppp, AnalyticsDatabase.OptionText, opText);
//                                }
//                            }).start();

                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {

                setAnswerKeyResponse(answerKeyResponse);
                setAnswerPaperResponse(answerPaperResponse);
                setEndExamAnalyticsResponse(endExamAnalyticsResponse);
                setEndStudentAnalyticsResponse(endStudentAnalyticsResponse);
                formDataForViewPager();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "done";
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //Set the view pager adapter..

                Intent intent = new Intent(AnswerPaperLoad.this, InitialInfo.class);
                intent.putExtra("startTime", myStartTime);
                intent.putExtra("endTime", myEndTime);
                intent.putExtra("duration", myExamDuration);
                intent.putExtra("totalMarks", totalMarks);
                intent.putExtra("score", score);
                intent.putExtra("attempts", attempts);
                intent.putExtra("examName", examName);
                intent.putExtra("noOfSections", noOfSections);
                intent.putExtra("date", date);
                intent.putExtra("bestScore", bestScore);
                intent.putExtra("averageScore", averageScore);
                intent.putExtra("totalStudents", totalStudents);
                intent.putExtra("rank", rank);
                intent.putExtra("url", myResponseUrl);
                Log.d("myLength=", questionArray.length + "");
                intent.putExtra("questionArray", questionArray);
                startActivity(intent);
                finish();
        }



    }
}
