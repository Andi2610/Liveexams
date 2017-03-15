package in.truskills.liveexams.JsonParsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 24-02-2017.
 */

public class EndStudentAnalyticsParser {

    private static final String _id="_id";
    private static final String examId="ExamId";
    private static final String userId="userId";
    private static final String __v="__v";
    private static final String analytics="analytics";
    private static final String totalMarks="totalMarks";
    private static final String totalTimeSpent="totalTimeSpent";
    private static final String totalAttemptedQuestions="totalAttemptedQuestions";
    private static final String totalReadQuestions="totalReadQuestions";
    private static final String sectionWiseReadQuestions="sectionWiseReadQuestions";
    private static final String sectionId="sectionId";
    private static final String readQuestions="readQuestions";
    private static final String attemptedQuestions="attemptedQuestions";
    private static final String timespent="timespent";
    private static final String sectionMarks="sectionMarks";
    private static final String sectionWiseAttemptedQuestions="sectionWiseAttemptedQuestions";
    private static final String sectionWiseTimeSpent="sectionWiseTimeSpent";
    private static final String sectionWiseMarks="sectionWiseMarks";
    private static final String rank="rank";



    public static HashMap<String,String> responseParser(String myResponse) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myResponse);
        map.put("_id",jsonObject.getString(_id));
        map.put("examId",jsonObject.getString(examId));
        map.put("userId",jsonObject.getString(userId));
        map.put("__v",jsonObject.getString(__v));
        map.put("rank",jsonObject.getString(rank));
        map.put("analytics",jsonObject.getJSONArray(analytics).getJSONObject(0).toString());
        return map;
    }

    public static HashMap<String,String> analyticsParser(String myResponse) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myResponse);
        map.put("totalMarks",jsonObject.getString(totalMarks));
        map.put("totalTimeSpent",jsonObject.getString(totalTimeSpent));
        map.put("totalAttemptedQuestions",jsonObject.getString(totalAttemptedQuestions));
        map.put("totalReadQuestions",jsonObject.getString(totalReadQuestions));
        map.put("sectionWiseAttemptedQuestions",jsonObject.getJSONArray(sectionWiseAttemptedQuestions).toString());
        map.put("sectionWiseReadQuestions",jsonObject.getJSONArray(sectionWiseReadQuestions).toString());
        map.put("sectionWiseTimeSpent",jsonObject.getJSONArray(sectionWiseTimeSpent).toString());
        map.put("sectionWiseMarks",jsonObject.getJSONArray(sectionWiseMarks).toString());
        return map;
    }

    public static HashMap<String,String> sectionWiseReadQuestionsParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("_id",jsonObject.getString(_id));
        map.put("readQuestions",jsonObject.getString(readQuestions));
        return map;
    }

    public static HashMap<String,String> sectionWiseMarksParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("_id",jsonObject.getString(_id));
        map.put("sectionMarks",jsonObject.getString(sectionMarks));
        return map;
    }

    public static HashMap<String,String> sectionWiseTimeSpentParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("_id",jsonObject.getString(_id));
        map.put("timespent",jsonObject.getString(timespent));
        return map;
    }

    public static HashMap<String,String> sectionWiseAttemptedQuestionsParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("_id",jsonObject.getString(_id));
        map.put("attemptedQuestions",jsonObject.getString(attemptedQuestions));
        return map;
    }
}
