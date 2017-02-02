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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.QuestionPaperParser;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

public class QuizMainActivity extends AppCompatActivity {

    MyPageAdapter pageAdapter;
    ArrayList<String> options;
    HashMap<Integer,Integer> noOfQuestionsList;
    HashMap<Integer,HashMap<Integer,Integer>> noOfLanguageList;
    HashMap<Integer,HashMap<Integer,HashMap<Integer,Integer>>> noOfOptionList;
    HashMap<Integer,String> idOfSection,nameOfSection;
    HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> ExamName,Year;
    HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> AttributesOfLanguage,QuestionText;
    HashMap<Integer,HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>>> _OfOption,AttributesOfOneOption;
    int noOfQuestions,noOfExamName,noOfLanguage,noOfOption,noOfSections;
    RequestQueue requestQueue;
    String url;
    String examId,name;
    List<Fragment> fList;
    TextView sectionName,submittedQuestions,reviewedQuestions,notAttemptedQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        examId=getIntent().getStringExtra("examId");
        name=getIntent().getStringExtra("name");
        fList = new ArrayList<Fragment>();

        sectionName=(TextView)findViewById(R.id.sectionName);
        submittedQuestions=(TextView)findViewById(R.id.submittedQuestions);
        reviewedQuestions=(TextView)findViewById(R.id.reviewedQuestions);
        notAttemptedQuestions=(TextView)findViewById(R.id.notAttemptedQuestions);

//        sectionName.setText("PHYSICS");
        submittedQuestions.setText("0");
        reviewedQuestions.setText("0");
        notAttemptedQuestions.setText("30");

        getSupportActionBar().setTitle(name);

        url=VariablesDefined.api+"questionPaper/"+examId;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                Log.d("myQuestionPaper","result="+result.toString()+"");
                try {
                    HashMap<String,String> map1= QuestionPaperParser.resultParser(result);
                    String success=map1.get("success");
                    if(success.equals("true")){

                        String response=map1.get("response");

                        HashMap<String,String> map2=QuestionPaperParser.responseParser(response);
                        String Paperset=map2.get("Paperset");

                        HashMap<String,String> map3=QuestionPaperParser.PapersetParser(Paperset);
                        String Sections=map3.get("Sections");
                        HashMap<String,String> map4=QuestionPaperParser.SectionsParser(Sections);
                        String Section=map4.get("Section");
                        noOfSections=QuestionPaperParser.getNoOfSections(Section);

                        idOfSection=new HashMap<>();
                        nameOfSection=new HashMap<>();
                        noOfQuestionsList=new HashMap<>();
                        noOfLanguageList=new HashMap<>();
                        noOfOptionList=new HashMap<>();

                        HashMap<Integer,Integer> noOfLanguageListTemp=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,Integer>> noOfOptionListTemp=new HashMap<>();
                        HashMap<Integer,Integer> noOfOptionListTemp2=new HashMap<>();


                        ExamName=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,String>> tempExamName=new HashMap<>();
                        HashMap<Integer,String> tempExamName2=new HashMap<>();

                        Year=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,String>> tempYear=new HashMap<>();
                        HashMap<Integer,String> tempYear2=new HashMap<>();

                        AttributesOfLanguage=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,String>> tempAttributesOfLanguage=new HashMap<>();
                        HashMap<Integer,String> tempAttributesOfLanguage2=new HashMap<>();

                        QuestionText=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,String>> tempQuestionText=new HashMap<>();
                        HashMap<Integer,String> tempQuestionText2=new HashMap<>();

                        _OfOption=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> _OfOptionTemp1=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,String>> _OfOptionTemp2=new HashMap<>();
                        HashMap<Integer,String> _OfOptionTemp3=new HashMap<>();

                        AttributesOfOneOption=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,HashMap<Integer,String>>> AttributesOfOneOptionTemp1=new HashMap<>();
                        HashMap<Integer,HashMap<Integer,String>> AttributesOfOneOptionTemp2=new HashMap<>();
                        HashMap<Integer,String> AttributesOfOneOptionTemp3=new HashMap<>();

                        HashMap<Integer,String> Language=new HashMap<>();
                        HashMap<Integer,String> AttributesOfQuestion=new HashMap<>();

                        //Loop through all the sections..
                        for(int i=0;i<noOfSections;++i){

                            //Parse one section..
                            HashMap<String,String> map5=QuestionPaperParser.SectionParser(Section,i);
                            String SectionQuestions=map5.get("SectionQuestions");
                            String AttributesOfSection=map5.get("Attributes");

                            //Parse one section attributes..
                            HashMap<String,String> map6=QuestionPaperParser.getAttributesOfSection(AttributesOfSection);
                            idOfSection.put(i,map6.get("id"));
                            nameOfSection.put(i,map6.get("Name"));

                            sectionName.setText(nameOfSection.get(i));

                            //Parse one section SectionQuestions..
                            HashMap<String,String> map7=QuestionPaperParser.SectionQuestionsParser(SectionQuestions);
                            String Question=map7.get("Question");

                            //Get no. of questions in one section..
                            noOfQuestions=QuestionPaperParser.getNoOfQuestionInOneSection(Question);

                            noOfQuestionsList.put(i,noOfQuestions);

                            //Loop through all the questions of one section..
                            for(int j=0;j<noOfQuestions;++j){

                                //Parse one section one Question..
                                HashMap<String,String> map8=QuestionPaperParser.QuestionParser(Question,j);

                                String myAskedIn=map8.get("AskedIn");
                                //Parse one section one question askedIn..
                                HashMap<String,String> map9=QuestionPaperParser.AskedInParser(myAskedIn);
                                String myExamName=map9.get("ExamName");
                                String myYear=map9.get("Year");

                                noOfExamName=QuestionPaperParser.getLengthOfExamName(myExamName);

                                //Loop through the entire exam and year array..
                                for(int k=0;k<noOfExamName;++k){

                                    //Get exam name one by one..
                                    String nm=QuestionPaperParser.getExamNamesOfOneQuestion(myExamName,k);
                                    tempExamName2.put(k,nm);
                                    tempExamName.put(j,tempExamName2);
                                    ExamName.put(i,tempExamName);

                                    //Get Year one by one..
                                    String nmm=QuestionPaperParser.getYearsOfOneQuestion(myYear,k);
                                    tempYear2.put(k,nmm);
                                    tempYear.put(j,tempYear2);
                                    Year.put(i,tempYear);
                                }

                                String myLanguage=map8.get("Language");

                                //Get length of language array of one question of one section..
                                noOfLanguage=QuestionPaperParser.getLengthOfLanguageOfOneQuestion(myLanguage);

                                noOfLanguageListTemp.put(j,noOfLanguage);
                                noOfLanguageList.put(i,noOfLanguageListTemp);

                                //Loop through the entire language array..
                                for(int k=0;k<noOfLanguage;++k){


                                    HashMap<String,String> map10=QuestionPaperParser.LanguageParser(myLanguage,k);
                                    String myQuestionText=map10.get("QuestionText");
                                    String myAttributesOfLanguage=map10.get("Attributes");
                                    String myOptions=map10.get("Options");

                                    String text=QuestionPaperParser.getQuestionText(myQuestionText);

                                    tempQuestionText2.put(k,text);
                                    tempQuestionText.put(j,tempQuestionText2);
                                    QuestionText.put(i,tempQuestionText);

                                    String nameOfLanguage=QuestionPaperParser.getAttributesOfOneLanguageOfOneQuestion(myAttributesOfLanguage);

                                    tempAttributesOfLanguage2.put(k,nameOfLanguage);
                                    tempAttributesOfLanguage.put(j,tempAttributesOfLanguage2);
                                    AttributesOfLanguage.put(i,tempAttributesOfLanguage);

                                    String myOption=QuestionPaperParser.OptionsParser(myOptions);
                                    //Get length of option array..
                                    noOfOption=QuestionPaperParser.getLengthOfOptionArray(myOption);
                                    noOfOptionListTemp2.put(k,noOfOption);
                                    noOfOptionListTemp.put(j,noOfOptionListTemp2);
                                    noOfOptionList.put(i,noOfOptionListTemp);


                                    ArrayList<String> opt=new ArrayList<>();

                                    //Loop through entire option array..
                                    for(int p=0;p<noOfOption;++p){

                                        String myOp=QuestionPaperParser.OptionParser(myOption,p);

                                        HashMap<String,String> map11=QuestionPaperParser.oneOptionParser(myOp);
                                        String myAt=map11.get("Attributes");
                                        String myAttri=QuestionPaperParser.getAttributesOfOneOption(myAt);
                                        _OfOptionTemp3.put(p,map11.get("_"));
                                        AttributesOfOneOptionTemp3.put(p,myAttri);
                                        _OfOptionTemp2.put(k,_OfOptionTemp3);
                                        AttributesOfOneOptionTemp2.put(k,AttributesOfOneOptionTemp3);
                                        _OfOptionTemp1.put(j,_OfOptionTemp2);
                                        AttributesOfOneOptionTemp1.put(j,AttributesOfOneOptionTemp2);
                                        _OfOption.put(i,_OfOptionTemp1);
                                        AttributesOfOneOption.put(i,AttributesOfOneOptionTemp1);

                                        opt.add(map11.get("_"));

                                    }
                                    fList.add(MyFragment.newInstance(text,opt));
                                    Log.d("check","text="+text);
                                    setPager(fList);

                                }
                            }
                        }


                        //After entire question paper has been loaded..
                        Handler handler=new Handler();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("check","flistSize="+fList.size());
                                pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fList);
                                ViewPager pager =
                                        (ViewPager)findViewById(R.id.viewpager);
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


    public void setPager(List<Fragment> fList){

        Log.d("check","inFunc"+fList.size());
    }
    class MyPageAdapter extends FragmentPagerAdapter{
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
