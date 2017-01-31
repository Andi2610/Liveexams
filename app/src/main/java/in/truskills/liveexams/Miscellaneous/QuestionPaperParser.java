package in.truskills.liveexams.Miscellaneous;

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

}
