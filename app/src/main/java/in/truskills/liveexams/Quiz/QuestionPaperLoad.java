package in.truskills.liveexams.Quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.nkzawa.socketio.client.Socket;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.webrtc.SurfaceViewRenderer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

public class QuestionPaperLoad extends AppCompatActivity implements Handler.Callback{

    //Declare the variables..
    int curCount = 0,myCount=0,questionArray[],status=0;
    int languageArray[][], fragmentIndex[][];
    HashMap<String, String> map1, map2, map3, map4, map5, map6, map7, map8, map9, map10, map11,map12;
    int noOfQuestions, noOfExamName, noOfLanguage, noOfOption, noOfSections, fi = -1;
    RequestQueue requestQueue;
    String url, success, response, Paperset, Sections, Section, SectionQuestions, AttributesOfSection, Question, myAskedIn, myExamName, myYear, myLanguage;
    String myQuestionText, myOptions, myOption, nm, nmm, myOp, text, myAt, myAttri,section_id,section_max_marks,section_time,section_description,section_rules;
    String questionAttributes,opText;
    String examId, name, selectedLanguage;
    ArrayList<Fragment> fList;
    TextView myWaitMessage;
    MySqlDatabase ob;
    ProgressBar progressBar;
    ArrayList<String> urls,groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_paper_load);
        com.wang.avi.AVLoadingIndicatorView avi=(AVLoadingIndicatorView)findViewById(R.id.avi);
        avi.show();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        myWaitMessage=(TextView)findViewById(R.id.myWaitMessage);
        Typeface tff1=Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");
        myWaitMessage.setTypeface(tff1);

        examId = getIntent().getStringExtra("examId");
        name = getIntent().getStringExtra("name");
        selectedLanguage = getIntent().getStringExtra("language");

        fList = new ArrayList<>();
        urls=new ArrayList<>();
        groups=new ArrayList<>();
        ob=new MySqlDatabase(this);

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

                        languageArray = new int[noOfSections][];
                        fragmentIndex = new int[noOfSections][];
                        questionArray = new int[noOfSections];

                        //Loop through all the sections..
                        for (int i = 0; i < noOfSections; ++i) {

                            final int iiii=i;

                            //Parse one section..
                            map5 = QuestionPaperParser.SectionParser(Section, i);

                            //Get it's variables..
                            SectionQuestions = map5.get("SectionQuestions");
                            AttributesOfSection = map5.get("Attributes");
                            section_max_marks=map5.get("SectionMaxMarks");
                            section_time=map5.get("SectionTime");
                            section_description=map5.get("SectionDescription");
                            section_rules=map5.get("SectionRules");

                            //Parse one section attributes..
                            map6 = QuestionPaperParser.getAttributesOfSection(AttributesOfSection);

                            //Get it's variables..
                            name = map6.get("Name");
                            section_id=map6.get("id");

                            //Set in database..
                            ob.setValuesPerSection(i);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ob.updateValuesPerSection(iiii,MySqlDatabase.SectionName,name);
                                    ob.updateValuesPerSection(iiii,MySqlDatabase.SectionId,section_id);
                                    ob.updateValuesPerSection(iiii,MySqlDatabase.SectionMaxMarks,section_max_marks);
                                    ob.updateValuesPerSection(iiii,MySqlDatabase.SectionTime,section_time);
                                    ob.updateValuesPerSection(iiii,MySqlDatabase.SectionDescription,section_description);
                                    ob.updateValuesPerSection(iiii,MySqlDatabase.SectionRules,section_rules);
                                }
                            }).start();
                            map7 = QuestionPaperParser.SectionQuestionsParser(SectionQuestions);

                            //Get it's variables..
                            Question = map7.get("Question");

                            //Get no. of questions in one section..
                            noOfQuestions = QuestionPaperParser.getNoOfQuestionInOneSection(Question);

                            questionArray[i]=noOfQuestions;

                            fragmentIndex[i] = new int[noOfQuestions];

                            //Loop through all the questions of one section..
                            for (int j = 0; j < noOfQuestions; ++j) {

                                final int jjjj=j;

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
                                questionAttributes=map8.get("Attributes");

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

                                map12=QuestionPaperParser.getAttributesOfQuestion(questionAttributes);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionText,text);
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.CorrectAnswer,map12.get("CorrectAnswer"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionCorrectMarks,map12.get("QuestionCorrectMarks"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionIncorrectMarks,map12.get("QuestionIncorrectMarks"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.PassageID,map12.get("PassageID"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionType,map12.get("QuestionType"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionTime,map12.get("QuestionTime"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionDifficultyLevel,map12.get("QuestionDifficultyLevel"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionRelativeTopic,map12.get("QuestionRelativeTopic"));
                                        ob.updateValuesPerQuestion(iiii,jjjj,MySqlDatabase.QuestionId,map12.get("id"));
                                        ob.updateValuesForResult(iiii,jjjj,MySqlDatabase.SectionId,section_id);
                                        ob.updateValuesForResult(iiii,jjjj,MySqlDatabase.QuestionId,map12.get("id"));

                                    }
                                }).start();
                                myOption = QuestionPaperParser.OptionsParser(myOptions);

                                //Get length of option array..
                                noOfOption = QuestionPaperParser.getLengthOfOptionArray(myOption);

                                //Loop through entire option array..
                                for (int p = 0; p < noOfOption; ++p) {

                                    final int pppp=p;

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
                                    opText=map11.get("_");

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ob.updateValuesPerOption(iiii,jjjj,pppp,MySqlDatabase.OptionText,opText);
                                            ob.updateValuesPerOption(iiii,jjjj,pppp,MySqlDatabase.OptionId,myAttri);
                                        }
                                    }).start();

                                }
                            }
                        }

                        Handler handler = new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                //Back to main thread..
                                try {
                                    afterResponse();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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
                Toast.makeText(QuestionPaperLoad.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        requestQueue.add(stringRequest);
    }

    public void afterResponse() throws InterruptedException {


//        If offline required..
        for(int i=0;i<noOfSections;++i){
            for(int j=0;j<questionArray[i];++j){
                myCount++;
                String mt=ob.getTextOfOneQuestion(i,j);
                prepareForOfflineForQuestion(mt,i,j);
                Log.d("textHere","OfQuestions"+mt);
                int noo=ob.getNoOfOptionsInOneQuestion(i,j);
                for(int k=0;k<noo;++k){
                    myCount++;
                    String mo=ob.getTextOfOneOption(i,j,k);
                    Log.d("textHere","OfOptions"+mo);
                    prepareForOfflineForOption(mo,i,j,k);
                }
            }
        }

        if(urls.isEmpty()){
            Intent intent=new Intent(QuestionPaperLoad.this,QuizMainActivity.class);
            intent.putExtra("examId", examId);
            intent.putExtra("name", name);
            intent.putExtra("language", selectedLanguage);
            intent.putExtra("noOfSections",noOfSections);
            intent.putExtra("questionArray",questionArray);
            startActivity(intent);
            finish();
        }else{
            int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    NUMBER_OF_CORES * 2,
                    NUMBER_OF_CORES * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>()
            );
            for(int i=0;i<urls.size();++i){
                executor.execute(new LongThread(i, urls.get(i), new Handler(QuestionPaperLoad.this),groups.get(i),QuestionPaperLoad.this));
            }

            executor.shutdown();
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);

            if(executor.isTerminated()==true){
                Log.d("termination","true");
                Intent intent=new Intent(QuestionPaperLoad.this,QuizMainActivity.class);
                intent.putExtra("examId", examId);
                intent.putExtra("name", name);
                intent.putExtra("language", selectedLanguage);
                intent.putExtra("noOfSections",noOfSections);
                intent.putExtra("questionArray",questionArray);
                startActivity(intent);
                finish();
            }else{
                Log.d("termination","false");
            }
        }

        progressBar.setVisibility(View.VISIBLE);

//        Else if Online..
//        for(int i=0;i<noOfSections;++i){
//            for(int j=0;j<questionArray[i];++j){
//                myCount++;
//                String mt=ob.getTextOfOneQuestion(i,j);
//                prepareForOnlineForQuestion(i,j,mt);
//                int noo=ob.getNoOfOptionsInOneQuestion(i,j);
//                for(int k=0;k<noo;++k){
//                    myCount++;
//                    String mo=ob.getTextOfOneOption(i,j,k);
//                    prepareForOnlineForOption(i,j,k,mo);
//                }
//            }
//        }
//        progressBar.setVisibility(View.GONE);
//
//        Intent intent=new Intent(QuestionPaperLoad.this,QuizMainActivity.class);
//        intent.putExtra("examId", examId);
//        intent.putExtra("name", name);
//        intent.putExtra("language", selectedLanguage);
//        intent.putExtra("noOfSections",noOfSections);
//        intent.putExtra("questionArray",questionArray);
//        startActivity(intent);

    }

    public void prepareForOfflineForQuestion(String text,int ii,int jj) throws InterruptedException {
        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(jpg|gif|png))";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        final Matcher matcher1= pattern.matcher(text);
        String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        String subst="<img src=\"file://"+base+"/LiveExams/$2\"/>";
        String result=matcher1.replaceAll(subst);
        ob.updateValuesPerQuestion(ii,jj,MySqlDatabase.QuestionText,result);

        while (matcher.find()){
            Log.d("messi","matcher.findInLoop");
            String group=matcher.group(2);
            String imageUrl = VariablesDefined.imageUrl+"changeThisToExamId"+"/Images/"+group;
            Log.d("imageDownload",imageUrl);
            urls.add(imageUrl);
            groups.add(group);
        }
    }

    public void prepareForOfflineForOption(String text,int ii,int jj,int kk) throws InterruptedException {
        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(jpg|gif|png))";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(text);
        final Matcher matcher1= pattern.matcher(text);
        String base = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        String subst="<img src=\"file://"+base+"/LiveExams/$2\"/>";
        String result=matcher1.replaceAll(subst);
        ob.updateValuesPerOption(ii,jj,kk,MySqlDatabase.OptionText,result);

        while (matcher.find()){
            Log.d("messi","matcher.findInLoop");
            String group=matcher.group(2);
            String imageUrl = VariablesDefined.imageUrl+"changeThisToExamId"+"/Images/"+group;
            Log.d("imageDownload",imageUrl);
            urls.add(imageUrl);
            groups.add(group);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        curCount++;
        Log.d("count",curCount+" "+myCount);
        float per = (curCount / (float)myCount) * 100;
        Log.d("myPer=",per+"");
        progressBar.setProgress((int) per);
        if(progressBar.getProgress()==100){
            Log.d("place","complete");
//            Intent intent=new Intent(QuestionPaperLoad.this,QuizMainActivity.class);
//            intent.putExtra("examId", examId);
//            intent.putExtra("name", name);
//            intent.putExtra("language", selectedLanguage);
//            intent.putExtra("noOfSections",noOfSections);
//            intent.putExtra("questionArray",questionArray);
//            startActivity(intent);
//            finish();
        }
        else
            Log.d("place","incomplete");
//        if (per < 100)
//            Toast.makeText(this, "\"Downloaded [" + curCount + "/" + (int)myCount + "]\"****"+per, Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(this, "All images downloaded.****"+per, Toast.LENGTH_SHORT).show();
        return true;
    }

    public static String format(String str, String examId) {

        examId="changeThisToExamId";

        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(jpg|gif|png))";
        final String subst = "<img src=\""+VariablesDefined.imageUrl+""+examId+"/Images/$2\"/>";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);

// The substituted value will be contained in the result variable
        String result=matcher.replaceAll(subst);

        return result;
    }

    public void prepareForOnlineForQuestion(int ii,int jj,String text){
        String myText=format(text,examId);
        ob.updateValuesPerQuestion(ii,jj,MySqlDatabase.QuestionText,myText);
    }

    public void prepareForOnlineForOption(int ii,int jj,int kk,String text){
        String myText=format(text,examId);
        ob.updateValuesPerOption(ii,jj,kk,MySqlDatabase.OptionText,myText);
    }

    private class DownloadImages extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            File dir=new File(Environment.getExternalStorageDirectory(),"/LiveExams");
            if(!dir.exists())
                dir.mkdir();

            for(int i=0;i<urls.size();++i){
                File f=new File(dir+"/"+groups.get(i));
                if(!f.exists()){

                }
            }
            return null;
        }
    }
}
