package in.truskills.liveexams.JsonParsers;

import android.graphics.Path;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used for json parsing the entire question paper obtained for the quiz..
 */

public class QuestionPaperParser {

    //All the fields present in the json response..
    private static final String response="response";
    private static final String success="success";
    private static final String _id="_id";
    private static final String ExamName="ExamName";
    private static final String ExamId="ExamId";
    private static final String MaximumMarks="MaximumMarks";
    private static final String EndDate="EndDate";
    private static final String StartTime="StartTime";
    private static final String EndTime="EndTime";
    private static final String ExamDuration="ExamDuration";
    private static final String Instructions="Instructions";
    private static final String Description="Description";
    private static final String ExamImage="ExamImage";
    private static final String AuthorDetails="AuthorDetails";
    private static final String Paperset="Paperset";
    private static final String _v="__v";
    private static final String LanguagesAvailable="Languages";
    private static final String StartDate="StartDate";
    private static final String id="id";
    private static final String Name="Name";
    private static final String Emailid="Emailid";
    private static final String Contact="Contact";
    private static final String Qualification="Qualification";
    private static final String Location="Location";
    private static final String Purpose="Purpose";
    private static final String Sections="Sections";
    private static final String Attributes="Attributes";
    private static final String Section="Section";
    private static final String SectionMaxMarks="SectionMaxMarks";
    private static final String SectionTime="SectionTime";
    private static final String SectionDescription="SectionDescription";
    private static final String SectionRules="SectionRules";
    private static final String SectionQuestions="SectionQuestions";
    private static final String Question="Question";
    private static final String AskedIn="AskedIn";
    private static final String Language="Language";
    private static final String CorrectAnswer="CorrectAnswer";
    private static final String QuestionCorrectMarks="QuestionCorrectMarks";
    private static final String QuestionIncorrectMarks="QuestionIncorrectMarks";
    private static final String PassageID="PassageID";
    private static final String QuestionType="QuestionType";
    private static final String QuestionTime="QuestionTime";
    private static final String QuestionDifficultyLevel="QuestionDifficultyLevel";
    private static final String QuestionRelativeTopic="QuestionRelativeTopic";
    private static final String Year="Year";
    private static final String QuestionText="QuestionText";
    private static final String Options="Options";
    private static final String Option="Option";
    private static final String _="_";

    public static HashMap<String,String> resultParser(String result) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(result);
        mapper.put("response",jsonObject.getJSONArray(response).getJSONObject(0).toString());
        mapper.put("success",jsonObject.getString(success));
        return mapper;
    }

    public static HashMap<String,String> responseParser(String myResponse) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myResponse);
        mapper.put("_id",jsonObject.getString(_id));
        mapper.put("ExamName",jsonObject.getJSONArray(ExamName).get(0).toString());
        mapper.put("ExamId",jsonObject.getJSONArray(ExamId).get(0).toString());
        mapper.put("MaximumMarks",jsonObject.getJSONArray(MaximumMarks).get(0).toString());
        mapper.put("EndDate",jsonObject.getJSONArray(EndDate).get(0).toString());
        mapper.put("StartTime",jsonObject.getJSONArray(StartTime).get(0).toString());
        mapper.put("EndTime",jsonObject.getJSONArray(EndTime).get(0).toString());
        mapper.put("ExamDuration",jsonObject.getJSONArray(ExamDuration).toString());
        mapper.put("Instructions",jsonObject.getJSONArray(Instructions).get(0).toString());
        mapper.put("Description",jsonObject.getJSONArray(Description).get(0).toString());
        mapper.put("ExamImage",jsonObject.getJSONArray(ExamImage).get(0).toString());
        mapper.put("AuthorDetails",jsonObject.getJSONArray(AuthorDetails).getJSONObject(0).toString());
        mapper.put("Paperset",jsonObject.getJSONArray(Paperset).getJSONObject(0).toString());
        mapper.put("_v",jsonObject.getString(_v));
        mapper.put("LanguagesAvailable",jsonObject.getJSONArray(LanguagesAvailable).toString());
        mapper.put("StartDate",jsonObject.getJSONArray(StartDate).get(0).toString());
        return mapper;
    }

    public static String getExamDuration(String myExamDuration) throws JSONException {
        JSONArray jsonArray=new JSONArray(myExamDuration);
        String duration=jsonArray.get(0).toString();
        return duration;
    }

    public static HashMap<String,String> AuthorDetailsParser(String myAuthourDetails) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myAuthourDetails);
        mapper.put("id",jsonObject.getString(id));
        mapper.put("Name",jsonObject.getString(Name));
        mapper.put("EmailId",jsonObject.getString(Emailid));
        mapper.put("Contact",jsonObject.getString(Contact));
        mapper.put("Qualification",jsonObject.getString(Qualification));
        mapper.put("Location",jsonObject.getString(Location));
        return mapper;
    }

    public static ArrayList<String> getLanguagesAvailable(String myLanguagesAvailable) throws JSONException {
        ArrayList<String> lang=new ArrayList<>();
        JSONArray jsonArray=new JSONArray(myLanguagesAvailable);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        JSONArray jsonArray1=jsonObject.getJSONArray(Language);
        for(int i=0;i<jsonArray1.length();++i){
            lang.add(jsonArray.get(i).toString());
        }

        return lang;
    }

    public static HashMap<String,String> PapersetParser(String myPaperSet) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myPaperSet);
        mapper.put("Purpose",jsonObject.getJSONArray(Purpose).toString());
        mapper.put("Sections",jsonObject.getJSONArray(Sections).getJSONObject(0).toString());
        mapper.put("Attributes",jsonObject.getJSONObject(Attributes).toString());
        return mapper;
    }

    public static String getPurpose(String myPurpose) throws JSONException {
        JSONArray jsonArray=new JSONArray(myPurpose);
        String purpose=jsonArray.get(0).toString();
        return purpose;
    }

    public static String getAttributesOfPaperset(String myAttributes) throws JSONException {
        JSONObject jsonObject=new JSONObject(myAttributes);
        String myAttr=jsonObject.getString(id);
        return myAttr;
    }

    public static HashMap<String,String> SectionsParser(String mySections) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(mySections);
        mapper.put("Section",jsonObject.getJSONArray(Section).toString());
        return mapper;
    }

    public static HashMap<String,String> SectionParser(String mySection,int i) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(mySection);
        String mySectionMaxMarks,mySectionTime,mySectionDescription,mySectionRules,mySectionQuestions,mySectionAttributes;

        JSONObject jsonObject=jsonArray.getJSONObject(i);

        mySectionMaxMarks=jsonObject.getJSONArray(SectionMaxMarks).get(0).toString();
        mySectionTime=jsonObject.getJSONArray(SectionTime).get(0).toString();
        mySectionDescription=jsonObject.getJSONArray(SectionDescription).get(0).toString();
        mySectionRules=jsonObject.getJSONArray(SectionRules).get(0).toString();
        mySectionQuestions=jsonObject.getJSONArray(SectionQuestions).getJSONObject(0).toString();
        mySectionAttributes=jsonObject.getJSONObject(Attributes).toString();

        mapper.put("SectionMaxMarks",mySectionMaxMarks);
        mapper.put("SectionTime",mySectionTime);
        mapper.put("SectionDescription",mySectionDescription);
        mapper.put("SectionRules",mySectionRules);
        mapper.put("SectionQuestions",mySectionQuestions);
        mapper.put("Attributes",mySectionAttributes);
        return mapper;
    }

    public static HashMap<String,String> getAttributesOfSection(String myAttributes) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        String myId,myName;
        JSONObject jsonObject=new JSONObject(myAttributes);
        myId=jsonObject.getString(id);
        myName=jsonObject.getString(Name);
        mapper.put("id",myId);
        mapper.put("Name",myName);
        return mapper;
    }

    public static int getNoOfSections(String mySection) throws JSONException {
        JSONArray jsonArray=new JSONArray(mySection);
        int length=jsonArray.length();
        return length;
    }

    public static HashMap<String,String> SectionQuestionsParser(String mySectionQuestions) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(mySectionQuestions);
        mapper.put("Question",jsonObject.getJSONArray(Question).toString());
        return mapper;
    }

    public static HashMap<String,String> QuestionParser(String myQuestion,int i) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myQuestion);
        String myAskedIn,myLanguage,myAttributes;
        JSONObject jsonObject=jsonArray.getJSONObject(i);
        myAskedIn=jsonObject.getJSONArray(AskedIn).get(0).toString();
        myLanguage=jsonObject.getJSONArray(Language).toString();
        myAttributes=jsonObject.getJSONObject(Attributes).toString();
        mapper.put("AskedIn",myAskedIn);
        mapper.put("Language",myLanguage);
        mapper.put("Attributes",myAttributes);
        return mapper;
    }

    public static int getNoOfQuestionInOneSection(String myQuestion) throws JSONException {
        JSONArray jsonArray=new JSONArray(myQuestion);
        int length=jsonArray.length();
        return length;
    }

    public static HashMap<String,String> getAttributesOfQuestion(String myAttributesOfQuestion) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myAttributesOfQuestion);
        String myid=jsonObject.getString(id);
        String correct_answer=jsonObject.getString(CorrectAnswer);
        String question_correct_marks=jsonObject.getString(QuestionCorrectMarks);
        String question_incorrect_marks=jsonObject.getString(QuestionIncorrectMarks);
        String passage_id=jsonObject.getString(PassageID);
        String question_type=jsonObject.getString(QuestionType);
        String question_time=jsonObject.getString(QuestionTime);
        String difficulty_level=jsonObject.getString(QuestionDifficultyLevel);
        String relative_topic=jsonObject.getString(QuestionRelativeTopic);
        mapper.put("id",myid);
        mapper.put("CorrectAnswer",correct_answer);
        mapper.put("QuestionCorrectMarks",question_correct_marks);
        mapper.put("QuestionIncorrectMarks",question_incorrect_marks);
        mapper.put("PassageID",passage_id);
        mapper.put("QuestionType",question_type);
        mapper.put("QuestionTime",question_time);
        mapper.put("QuestionDifficultyLevel",difficulty_level);
        mapper.put("QuestionRelativeTopic",relative_topic);

        return mapper;
    }

    public static HashMap<String,String>  AskedInParser(String myAskedIn) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        String myExamName,myYear;
        JSONObject jsonObject=new JSONObject(myAskedIn);
        myExamName=jsonObject.getJSONArray(ExamName).toString();
        myYear=jsonObject.getJSONArray(Year).toString();
        mapper.put("ExamName",myExamName);
        mapper.put("Year",myYear);
        return mapper;
    }

    public static int getLengthOfExamName(String myExamName) throws JSONException {
        JSONArray jsonArray=new JSONArray(myExamName);
        int length=jsonArray.length();
        return length;
    }

    public static String getExamNamesOfOneQuestion(String myExamName,int i) throws JSONException {
        JSONArray jsonArray=new JSONArray(myExamName);
        String nm=jsonArray.get(i).toString();
        return nm;
    }

    public static String getYearsOfOneQuestion(String myYear,int i) throws JSONException {
        JSONArray jsonArray=new JSONArray(myYear);
        String nm=jsonArray.get(i).toString();
        return nm;
    }

    public static HashMap<String,String> LanguageParser(String myLanguage,int i) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myLanguage);
       String myQuestionText,myAttributes,myOptions;
        JSONObject jsonObject=jsonArray.getJSONObject(i);
        myQuestionText=jsonObject.getJSONArray(QuestionText).toString();
        myAttributes=jsonObject.getJSONObject(Attributes).toString();
        myOptions=jsonObject.getJSONArray(Options).get(0).toString();
        mapper.put("QuestionText",myQuestionText);
        mapper.put("Attributes",myAttributes);
        mapper.put("Options",myOptions);
        return mapper;
    }

    public static String getQuestionText(String myQuestion) throws JSONException {
        JSONArray jsonArray=new JSONArray(myQuestion);
        String text=jsonArray.get(0).toString();
        return text;
    }

    public static int getLengthOfLanguageOfOneQuestion(String myLanguage) throws JSONException {
        JSONArray jsonArray=new JSONArray(myLanguage);
        int length=jsonArray.length();
        return length;
    }

    public static int getIndex(String selectedLanguage,String myLanguage) throws JSONException {
        JSONArray jsonArray=new JSONArray(myLanguage);
        int length=jsonArray.length();
        String myAttributes;
        for(int i=0;i<length;++i){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            myAttributes=jsonObject.getJSONObject(Attributes).toString();
            String name=getAttributesOfOneLanguageOfOneQuestion(myAttributes);
            if(name.equals(selectedLanguage)){
                return i;
            }
        }
        return -1;
    }

    public static String getAttributesOfOneLanguageOfOneQuestion(String myAttribute) throws JSONException {
        JSONObject jsonObject=new JSONObject(myAttribute);
        String myName=jsonObject.getString(Name);
        return myName;
    }

    public static String OptionsParser(String myOption) throws JSONException {
        JSONObject jsonObject=new JSONObject(myOption);
        String myOp=jsonObject.getJSONArray(Option).toString();
        return myOp;
    }

    public static int getLengthOfOptionArray(String myOption) throws JSONException {
        JSONArray jsonArray=new JSONArray(myOption);
        int l=jsonArray.length();
        return l;
    }

    public static String OptionParser(String myOption,int i) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myOption);
        String myOp=jsonArray.getJSONObject(i).toString();
        return myOp;
    }

    public static HashMap<String,String> oneOptionParser(String myOption) throws JSONException {
        HashMap<String,String > mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myOption);
        mapper.put("_",jsonObject.getString(_));
        mapper.put("Attributes",jsonObject.getString(Attributes));
        return mapper;
    }

    public static String getAttributesOfOneOption(String myOption) throws JSONException {
        JSONObject jsonObject=new JSONObject(myOption);
        String str=jsonObject.getString(id);
        return str;
    }

}
