package in.truskills.liveexams.JsonParsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 24-02-2017.
 */

public class EndExamAnalyticsParser {

    private static final String _id="_id";
    private static final String examId="ExamId";
    private static final String __v="__v";
    private static final String analytics="analytics";
    private static final String sectionId="sectionId";
    private static final String questionId="questionId";
    private static final String rightAnsweredBy="rightAnsweredBy";
    private static final String wrongAnsweredBy="wrongAnsweredBy";
    private static final String minimumTime="minimumTime";
    private static final String maximumTime="maximumTime";
    private static final String sectionWiseAnalytics="sectionWiseAnalytics";

    public static HashMap<String,String> responseParser(String myResponse) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myResponse);
        map.put("_id",jsonObject.getString(_id));
        map.put("examId",jsonObject.getString(examId));
        map.put("sectionWiseAnalytics",jsonObject.getJSONArray(sectionWiseAnalytics).toString());
        map.put("__v",jsonObject.getString(__v));
        map.put("analytics",jsonObject.getJSONArray(analytics).toString());
        return map;
    }

    public static HashMap<String,String> analyticsParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("questionId",jsonObject.getString(questionId));
        map.put("rightAnsweredBy",jsonObject.getString(rightAnsweredBy));
        map.put("wrongAnsweredBy",jsonObject.getString(wrongAnsweredBy));
        map.put("minimumTime",jsonObject.getString(minimumTime));
        map.put("maximumTime",jsonObject.getString(maximumTime));
        return map;
    }
}
