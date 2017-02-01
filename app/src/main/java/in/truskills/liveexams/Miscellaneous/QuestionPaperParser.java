package in.truskills.liveexams.Miscellaneous;

import android.graphics.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 30-01-2017.
 */

public class QuestionPaperParser {

    private static String response="response";
    private static String success="success";
    private static String _id="_id";
    private static String ExamName="ExamName";
    private static String ExamId="ExamId";
    private static String MaximumMarks="MaximumMarks";
    private static String EndDate="EndDate";
    private static String StartTime="StartTime";
    private static String EndTime="Endtime";
    private static String ExamDuration="ExamDuration";
    private static String Instructions="Instructions";
    private static String Description="Description";
    private static String ExamImage="ExamImage";
    private static String AuthorDetails="AuthorDetails";
    private static String Paperset="Paperset";
    private static String _v="_v";
    private static String LanguagesAvailible="LanguagesAvailable";
    private static String StartDate="StartDate";
    private static String id="id";
    private static String Name="Name";
    private static String Emailid="Emailid";
    private static String Contact="Contact";
    private static String Qualification="Qualification";
    private static String Location="Location";
    private static String Purpose="Purpose";
    private static String Sections="Sections";
    private static String Attributes="Attributes";
    private static String Section="Section";
    private static String SectionMaxMarks="SectionMaxMarks";
    private static String SectionTime="SectionTime";
    private static String SectionDescription="SectionDescription";
    private static String SectionRules="SectionRules";
    private static String SectionQuestions="SectionQuestions";
    private static String Question="Question";
    private static String AskedIn="AskedIn";
    private static String Language="Language";
    private static String CorrectAnswer="CorrectAnswer";
    private static String QuestionCorrectMarks="QuestionCorrectMarks";
    private static String QuestionIncorrectMarks="QuestionIncorrectMarks";
    private static String PassageID="PassageID";
    private static String QuestionType="QuestionType";
    private static String QuestionTime="QuestionTime";
    private static String QuestionDifficultyLevel="QuestionDifficultyLevel";
    private static String QuestionRelativeTopic="QuestionRelativeTopic";
    private static String Year="Year";
    private static String QuestionText="QuestionText";
    private static String Options="Options";
    private static String Option="Option";
    private static String _="_";

    public static HashMap<String,String> resultParser(String result) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(result);
        mapper.put("response",jsonObject.getJSONArray(response).getJSONObject(0).toString());
        mapper.put("success",jsonObject.getString(success));
        return mapper;
    }

    public static HashMap<String,String> responseParser(String myResponse) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        mapper.put("_id",jsonObject.getString(_id));
        mapper.put("ExamName",jsonObject.getString(ExamName));
        mapper.put("ExamId",jsonObject.getString(ExamId));
        mapper.put("MaximumMarks",jsonObject.getString(MaximumMarks));
        mapper.put("EndDate",jsonObject.getString(EndDate));
        mapper.put("StartTime",jsonObject.getString(StartTime));
        mapper.put("EndTime",jsonObject.getString(EndTime));
        mapper.put("ExamDuration",jsonObject.getJSONArray(ExamDuration).toString());
        mapper.put("Instructions",jsonObject.getString(Instructions));
        mapper.put("Description",jsonObject.getString(Description));
        mapper.put("ExamImage",jsonObject.getString(ExamImage));
        mapper.put("AuthorDetails",jsonObject.getJSONArray(AuthorDetails).getJSONObject(0).toString());
        mapper.put("Paperset",jsonObject.getJSONArray(Paperset).getJSONObject(0).toString());
        mapper.put("_v",jsonObject.getString(_v));
        mapper.put("LanguagesAvailable",jsonObject.getJSONArray(LanguagesAvailible).toString());
        mapper.put("StartDate",jsonObject.getString(StartDate));
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
        for(int i=0;i<jsonArray.length();++i){
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

    public static HashMap<String,ArrayList<String>> SectionParser(String mySection) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(mySection);
        int length=jsonArray.length();
        ArrayList<String> SectionMaxMarksList=new ArrayList<>();
        ArrayList<String> SectionTimeList=new ArrayList<>();
        ArrayList<String> SectionDescriptionList=new ArrayList<>();
        ArrayList<String> SectionRulesList=new ArrayList<>();
        ArrayList<String> SectionQuestionsList=new ArrayList<>();
        ArrayList<String> AttributesList=new ArrayList<>();

        for(int i=0;i<length;++i){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            SectionMaxMarksList.add(jsonObject.getJSONArray(SectionMaxMarks).get(0).toString());
            SectionTimeList.add(jsonObject.getJSONArray(SectionTime).get(0).toString());
            SectionDescriptionList.add(jsonObject.getJSONArray(SectionDescription).get(0).toString());
            SectionRulesList.add(jsonObject.getJSONArray(SectionRules).get(0).toString());
            SectionQuestionsList.add(jsonObject.getJSONArray(SectionQuestions).getJSONObject(0).toString());
            AttributesList.add(jsonObject.getJSONObject(Attributes).toString());
        }

        mapper.put("SectionMaxMarks",SectionMaxMarksList);
        mapper.put("SectionTime",SectionTimeList);
        mapper.put("SectionDescription",SectionDescriptionList);
        mapper.put("SectionRules",SectionRulesList);
        mapper.put("SectionQuestions",SectionQuestionsList);
        mapper.put("Attributes",AttributesList);
        return mapper;
    }

    public static HashMap<String,ArrayList<String>> getAttributesOfSection(ArrayList<String> myAttributesList) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        int length=myAttributesList.size();
        ArrayList<String> idList=new ArrayList<>();
        ArrayList<String> NameList=new ArrayList<>();
        for(int i=0;i<length;++i){
            JSONObject jsonObject=new JSONObject(myAttributesList.get(i));
            idList.add(jsonObject.getString(id));
            NameList.add(jsonObject.getString(Name));
        }
        mapper.put("id",idList);
        mapper.put("Name",NameList);
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

    public static HashMap<String,ArrayList<String>> QuestionParser(String myQuestion) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myQuestion);
        int length=jsonArray.length();
        ArrayList<String> AskedInList=new ArrayList<>();
        ArrayList<String> LanguageList=new ArrayList<>();
        ArrayList<String> AttributeList=new ArrayList<>();
        for(int i=0;i<length;++i){
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            AskedInList.add(jsonObject.getJSONArray(AskedIn).get(0).toString());
            LanguageList.add(jsonObject.getJSONArray(Language).toString());
            AttributeList.add(jsonObject.getJSONObject(Attributes).toString());
        }

        mapper.put("AskedIn",AskedInList);
        mapper.put("Language",LanguageList);
        mapper.put("Attributes",AttributeList);
        return mapper;
    }

    public static int getNoOfQuestionInOneSection(String myQuestion) throws JSONException {
        JSONArray jsonArray=new JSONArray(myQuestion);
        int length=jsonArray.length();
        return length;
    }

    public static HashMap<String,ArrayList<String>> getAttributesOfQuestion(ArrayList<String> myAttributesOfQuestion) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        int length=myAttributesOfQuestion.size();
        ArrayList<String> idList=new ArrayList<>();
        ArrayList<String> CorrectAnswerList=new ArrayList<>();
        ArrayList<String> QuestionCorrectMarksList=new ArrayList<>();
        ArrayList<String> QuestionIncorrectMarksList=new ArrayList<>();
        ArrayList<String> PassageIDList=new ArrayList<>();
        ArrayList<String> QuestionTypeList=new ArrayList<>();
        ArrayList<String> QuestionTimeList=new ArrayList<>();
        ArrayList<String> QuestionDifficultyLevelList=new ArrayList<>();
        ArrayList<String> QuestionRelativeTopicList=new ArrayList<>();

        for(int i=0;i<length;++i){
            JSONObject jsonObject=new JSONObject(myAttributesOfQuestion.get(i));
            idList.add(jsonObject.getString(id));
            CorrectAnswerList.add(jsonObject.getString(CorrectAnswer));
            QuestionCorrectMarksList.add(jsonObject.getString(QuestionCorrectMarks));
            QuestionIncorrectMarksList.add(jsonObject.getString(QuestionIncorrectMarks));
            PassageIDList.add(jsonObject.getString(PassageID));
            QuestionTypeList.add(jsonObject.getString(QuestionType));
            QuestionTimeList.add(jsonObject.getString(QuestionTime));
            QuestionDifficultyLevelList.add(jsonObject.getString(QuestionDifficultyLevel));
            QuestionRelativeTopicList.add(jsonObject.getString(QuestionRelativeTopic));
        }
        return mapper;
    }

    public static HashMap<String,ArrayList<String>>  AskedInParser(ArrayList<String> myAskedIn) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        ArrayList<String> ExamNameList=new ArrayList<>();
        ArrayList<String> YearList=new ArrayList<>();
        int length=myAskedIn.size();
        for(int i=0;i<length;++i){
            JSONObject jsonObject=new JSONObject(myAskedIn.get(i));
            ExamNameList.add(jsonObject.getJSONArray(ExamName).toString());
            YearList.add(jsonObject.getJSONArray(Year).toString());
        }
        mapper.put("ExamName",ExamNameList);
        mapper.put("Year",YearList);
        return mapper;
    }

    public static ArrayList<String> getExamNamesOfOneQuestion(ArrayList<String> myExamName) throws JSONException {
        int length=myExamName.size();
        ArrayList<String> ExamNameList=new ArrayList<>();
        for(int i=0;i<length;++i){
            JSONArray jsonArray=new JSONArray(myExamName.get(i));
            ExamNameList.add(jsonArray.get(i).toString());
        }
        return ExamNameList;
    }

    public static ArrayList<String> getYearsOfOneQuestion(ArrayList<String> myYear) throws JSONException {
        int length=myYear.size();
        ArrayList<String> YearList=new ArrayList<>();
        for(int i=0;i<length;++i){
            JSONArray jsonArray=new JSONArray(myYear.get(i));
            YearList.add(jsonArray.get(i).toString());
        }
        return YearList;
    }

    public static HashMap<String,ArrayList<String>> LanguageParser(String myLanguage) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myLanguage);
        ArrayList<String> QuestionTextList=new ArrayList<>();
        ArrayList<String> OptionsList=new ArrayList<>();
        ArrayList<String> AttributesList=new ArrayList<>();
        int length=jsonArray.length();
        for(int i=0;i<length;++i){
            JSONObject jsonObject=new JSONObject(jsonArray.get(i).toString());
            QuestionTextList.add(jsonObject.getJSONArray(QuestionText).get(0).toString());
            AttributesList.add(jsonObject.getJSONObject(Attributes).toString());
            OptionsList.add(jsonObject.getJSONArray(Options).get(0).toString());
        }
        mapper.put("QuestionText",QuestionTextList);
        mapper.put("Attributes",AttributesList);
        mapper.put("Options",OptionsList);
        return mapper;
    }

    public static String getAttributesOfOneLanguageOfOneQuestion(String myAttribute) throws JSONException {
        JSONObject jsonObject=new JSONObject(myAttribute);
        String myName=jsonObject.getString(Name);
        return myName;
    }

    public static HashMap<String,ArrayList<String>> OptionsParser(ArrayList<String> myOption) throws JSONException {
        HashMap<String,ArrayList<String >> mapper=new HashMap<>();
        int length=myOption.size();
        ArrayList<String> OptionList=new ArrayList<>();
        for(int i=0;i<length;++i){
            JSONObject jsonObject=new JSONObject(myOption.get(i));
            OptionList.add(jsonObject.getJSONArray(Option).toString());
        }
        mapper.put("Option",OptionList);
        return mapper;
    }

    public HashMap<String,ArrayList<String>> OptionParser(String myOption) throws JSONException {
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        ArrayList<String> OptionList = new ArrayList<>();
        JSONArray jsonArray=new JSONArray(myOption);
        int length=jsonArray.length();
        for(int i=0;i<length;++i){
            OptionList.add(jsonArray.getJSONObject(i).toString());
        }
        mapper.put("Option",OptionList);
        return mapper;
    }

    public static HashMap<String,String> oneOptionParser(String myOption) throws JSONException {
        HashMap<String,String > mapper=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myOption);
        mapper.put("_",jsonObject.getString(_));
        mapper.put("Attributes",jsonObject.getString(Attributes));
        return mapper;
    }


}
