package in.truskills.liveexams.Miscellaneous;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Shivansh Gupta on 26-01-2017.
 */

public class VariablesDefined {
    public static final String api= "http://35.154.110.122:3001/api/";
    private static String response="response";
    private static String success="success";
    private static String id="_id";
    private static String emailAddress="emailAddress";
    private static String language="language";
    private static String profileImageUrl="profileImageUrl";
    private static String joinedExams="joinedExams";
    private static String ExamName="ExamName";
    private static String StartDate="StartDate";
    private static String EndDate="EndDate";
    private static String StartTime="StartTime";
    private static String EndTime="EndTime";
    private static String ExamDuration="ExamDuration";
    private static String enrolled="enrolled";
    private static String timestamp="timestamp";
    private static String exam="exam";
    private static String Description="Description";
    private static String leftExam="leftExam";


    public static HashMap signupParser(String result) throws JSONException {
        JSONObject jsonObject=new JSONObject(result);
        HashMap<String,String> mapper=new HashMap<>();
        mapper.put("response",jsonObject.getString(response));
        mapper.put("success",jsonObject.getString(success));
        return mapper;
    }

    public static HashMap loginParser(String result) throws JSONException {
        JSONObject jsonOb=new JSONObject(result);
        JSONObject jsonObject=jsonOb.optJSONObject(response);
        HashMap<String,String> mapper=new HashMap<>();
        mapper.put("success",jsonOb.getString(success));
        if(jsonOb.getString(success).equals("true")){
            mapper.put("id",jsonObject.getString(id));
            mapper.put("emailAddress",jsonObject.getString(emailAddress));
            mapper.put("language",jsonObject.getString(language));
            mapper.put("profileImageUrl",jsonObject.getString(profileImageUrl));
            if(jsonObject.getJSONArray(joinedExams)!=null){
                String myJoinedExams=jsonObject.getJSONArray(joinedExams).toString();
                mapper.put("joinedExams",myJoinedExams);
            }else{
                mapper.put("joinedExams","noJoinedExams");
            }
        }else{
            mapper.put("response",jsonOb.getString(response));
        }
        return mapper;
    }

    public static HashMap myExamsParser(String result) throws JSONException {
        JSONArray jsonArray=new JSONArray(result);
        ArrayList<String> ExamNameList = new ArrayList<>();
        ArrayList<String> StartDateList = new ArrayList<>();
        ArrayList<String> EndDateList = new ArrayList<>();
        ArrayList<String> ExamDurationList = new ArrayList<>();
        ArrayList<String> ExamIdList = new ArrayList<>();
        ArrayList<String> leftExamList = new ArrayList<>();
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        for(int i = 0; i < jsonArray.length(); i++)
        {
            Iterator<String> keys= jsonArray.getJSONObject(i).keys();
            while(keys.hasNext()){
                String key=keys.next();
                JSONObject jsonObject=jsonArray.getJSONObject(i).getJSONArray(key).getJSONObject(0);
                ExamNameList.add(jsonObject.getString(ExamName));
                ExamDurationList.add(jsonObject.getJSONArray(ExamDuration).get(0).toString());
                StartDateList.add(jsonObject.getString(StartDate));
                EndDateList.add(jsonObject.getString(EndDate));
                ExamIdList.add(key);
                leftExamList.add(jsonObject.getString(leftExam));
            }
        }
        mapper.put("ExamName",ExamNameList);
        mapper.put("ExamDuration",ExamDurationList);
        mapper.put("StartDate",StartDateList);
        mapper.put("EndDate",EndDateList);
        mapper.put("ExamId",ExamIdList);
        mapper.put("leftExam",leftExamList);

        for(int i=0;i<ExamIdList.size();++i)
            Log.d("response",ExamIdList.get(i));

        return mapper;
    }

    public static HashMap allExamsParser(JSONObject result) throws JSONException {
        JSONArray jsonArray=result.getJSONArray(response);
        ArrayList<String> ExamNameList = new ArrayList<>();
        ArrayList<String> StartDateList = new ArrayList<>();
        ArrayList<String> EndDateList = new ArrayList<>();
        ArrayList<String> EndTimeList = new ArrayList<>();
        ArrayList<String> StartTimeList = new ArrayList<>();
        ArrayList<String> ExamDurationList = new ArrayList<>();
        ArrayList<String> ExamIdList = new ArrayList<>();
        HashMap<String,ArrayList<String>> mapper=new HashMap<>();
        for(int i = 0; i < jsonArray.length(); i++)
        {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                ExamNameList.add(jsonObject.getString(ExamName));
                Log.d("response",jsonObject.getString(ExamName));
                ExamDurationList.add(jsonObject.getJSONArray(ExamDuration).get(0).toString());
                StartTimeList.add(jsonObject.getString(StartTime));
                EndTimeList.add(jsonObject.getString(EndTime));
                StartDateList.add(jsonObject.getString(StartDate));
                EndDateList.add(jsonObject.getString(EndDate));
                ExamIdList.add(jsonObject.getString(id));
        }
        mapper.put("ExamName",ExamNameList);
        mapper.put("ExamDuration",ExamDurationList);
        mapper.put("StartDate",StartDateList);
        mapper.put("EndDate",EndDateList);
        mapper.put("StartTime",StartTimeList);
        mapper.put("EndTime",EndTimeList);
        mapper.put("ExamId",ExamIdList);
        return mapper;
    }

    public static HashMap examDetailsParser(String result) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONObject jsonObj=new JSONObject(result);
        JSONObject jsonObject=jsonObj.getJSONObject(response);
        mapper.put("enrolled",jsonObject.getString(enrolled));
        mapper.put("timestamp",jsonObject.getString(timestamp));
        String examDetails=jsonObject.getJSONArray(exam).toString();
        mapper.put("examDetails",examDetails);
        return mapper;
    }

    public static HashMap join_start_Parser(String result) throws JSONException {
        HashMap<String,String> mapper=new HashMap<>();
        JSONArray jsonArray=new JSONArray(result);
        JSONObject jsonObject=jsonArray.getJSONObject(0);
        mapper.put("StartDate",jsonObject.getString(StartDate));
        mapper.put("EndDate",jsonObject.getString(EndDate));
        mapper.put("StartTime",jsonObject.getString(StartTime));
        mapper.put("EndTime",jsonObject.getString(EndTime));
        mapper.put("Description",jsonObject.getString(Description));
        return mapper;
    }

}
