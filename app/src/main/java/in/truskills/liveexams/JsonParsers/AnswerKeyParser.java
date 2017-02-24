package in.truskills.liveexams.JsonParsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 24-02-2017.
 */

public class AnswerKeyParser {

    private static final String _id="_id";
    private static final String examId="ExamId";
    private static final String __v="__v";
    private static final String answerKey="answerKey";
    private static final String rightAnswer="rightAnswer";
    private static final String sectionId="sectionId";
    private static final String questionId="questionId";
    private static final String correctMarks="correctMarks";
    private static final String negativeMarks="negativeMarks";

    public static HashMap<String,String> responseParser(String myResponse) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONObject jsonObject=new JSONObject(myResponse);
        map.put("_id",jsonObject.getString(_id));
        map.put("examId",jsonObject.getString(examId));
        map.put("answerKey",jsonObject.getJSONArray(answerKey).toString());
        map.put("__v",jsonObject.getString(__v));
        return map;
    }

    public static HashMap<String,String> answerKeyParser(String myResponse,int num) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONArray jsonArray=new JSONArray(myResponse);
        JSONObject jsonObject=jsonArray.getJSONObject(num);
        map.put("rightAnswer",jsonObject.getString(rightAnswer));
        map.put("sectionId",jsonObject.getString(sectionId));
        map.put("questionId",jsonObject.getString(questionId));
        map.put("correctMarks",jsonObject.getString(correctMarks));
        map.put("negativeMarks",jsonObject.getString(negativeMarks));
        return map;
    }
}
