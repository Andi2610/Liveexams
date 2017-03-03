package in.truskills.liveexams.JsonParsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 24-02-2017.
 */

public class AnswerPaperParser {

    private static final String _id="_id";
    private static final String examId="ExamId";
    private static final String userId="userId";
    private static final String __v="__v";
    private static final String answerPaper="answerPaper";
    private static final String numberOfToggles="numberOfToggles";
    private static final String readStatus="readStatus";
    private static final String finalAnswerId="finalAnswerId";
    private static final String questionStatus="questionStatus";
    private static final String sectionId="sectionId";
    private static final String timeSpent="timeSpent";
    private static final String questionId="questionId";
    private static final String date="date";
    private static final String selectedLanguage="selectedLanguage";


    public static HashMap<String,String> responseParser(String myResponse) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myResponse);
        map.put("_id",jsonObject.getString(_id));
        map.put("examId",jsonObject.getString(examId));
        map.put("userId",jsonObject.getString(userId));
        map.put("__v",jsonObject.getString(__v));
        map.put("selectedLanguage",jsonObject.getString(selectedLanguage));
        map.put("date",jsonObject.getString(date));
        map.put("answerPaper",jsonObject.getJSONArray(answerPaper).toString());
        return map;
    }

    public static HashMap<String,String> answerPaperParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("numberOfToggles",jsonObject.getString(numberOfToggles));
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("questionId",jsonObject.getString(questionId));
        map.put("readStatus",jsonObject.getString(readStatus));
        map.put("finalAnswerId",jsonObject.getString(finalAnswerId));
        map.put("timeSpent",jsonObject.getString(timeSpent));
        map.put("questionStatus",jsonObject.getString(questionStatus));
        map.put("_id",jsonObject.getString(_id));
        return map;
    }
}
