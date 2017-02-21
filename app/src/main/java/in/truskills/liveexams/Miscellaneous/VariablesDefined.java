package in.truskills.liveexams.Miscellaneous;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

//This class contains miscellaneous functions and variables used in the entire code..

public class VariablesDefined {

    //Api used to connect to the server..
    public static final String api = "http://35.154.110.122:3001/api/";

    //Url for image..
    public static final String imageUrl = "https://s3.ap-south-1.amazonaws.com/live-exams/";

    //All fields that are parsed..
    private static String response = "response";
    private static String success = "success";
    private static String id = "_id";
    private static String userName = "userName";
    private static String emailAddress = "emailAddress";
    private static String language = "language";
    private static String profileImageUrl = "profileImageUrl";
    private static String joinedExams = "joinedExams";
    private static String ExamName = "ExamName";
    private static String StartDate = "StartDate";
    private static String EndDate = "EndDate";
    private static String StartTime = "StartTime";
    private static String EndTime = "EndTime";
    private static String ExamDuration = "ExamDuration";
    private static String enrolled = "enrolled";
    private static String timestamp = "timestamp";
    private static String exam = "exam";
    private static String Description = "Description";
    private static String leftExam = "leftExam";
    private static String message = "message";



    public static HashMap signupParser(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        HashMap<String, String> mapper = new HashMap<>();
        mapper.put("response", jsonObject.getString(response));
        mapper.put("success", jsonObject.getString(success));
        return mapper;
    }

    public static HashMap loginParser(String result) throws JSONException {
        JSONObject jsonOb = new JSONObject(result);
        JSONObject jsonObject = jsonOb.optJSONObject(response);
        HashMap<String, String> mapper = new HashMap<>();
        mapper.put("success", jsonOb.getString(success));
        if (jsonOb.getString(success).equals("true")) {
            mapper.put("id", jsonObject.getString(id));
            mapper.put("userName",jsonObject.getString(userName));
            mapper.put("emailAddress", jsonObject.getString(emailAddress));
            mapper.put("language", jsonObject.getString(language));
            mapper.put("profileImageUrl", jsonObject.getString(profileImageUrl));
            if (jsonObject.getJSONArray(joinedExams) != null) {
                String myJoinedExams = jsonObject.getJSONArray(joinedExams).toString();
                mapper.put("joinedExams", myJoinedExams);
            } else {
                mapper.put("joinedExams", "noJoinedExams");
            }
        } else {
            mapper.put("response", jsonOb.getString(response));
        }
        return mapper;
    }

    public static HashMap myExamsParser(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
        ArrayList<String> ExamNameList = new ArrayList<>();
        ArrayList<String> StartDateList = new ArrayList<>();
        ArrayList<String> EndDateList = new ArrayList<>();
        ArrayList<String> ExamDurationList = new ArrayList<>();
        ArrayList<String> ExamIdList = new ArrayList<>();
        ArrayList<String> leftExamList = new ArrayList<>();
        HashMap<String, ArrayList<String>> mapper = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Iterator<String> keys = jsonArray.getJSONObject(i).keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject jsonObject = jsonArray.getJSONObject(i).getJSONArray(key).getJSONObject(0);
                ExamNameList.add(jsonObject.getJSONArray(ExamName).get(0).toString());
                ExamDurationList.add(jsonObject.getJSONArray(ExamDuration).get(0).toString());
                StartDateList.add(jsonObject.getJSONArray(StartDate).get(0).toString());
                EndDateList.add(jsonObject.getJSONArray(EndDate).get(0).toString());
                ExamIdList.add(key);
                leftExamList.add(jsonObject.getString(leftExam));
            }
        }
        mapper.put("ExamName", ExamNameList);
        mapper.put("ExamDuration", ExamDurationList);
        mapper.put("StartDate", StartDateList);
        mapper.put("EndDate", EndDateList);
        mapper.put("ExamId", ExamIdList);
        mapper.put("leftExam", leftExamList);

        return mapper;
    }

    public static HashMap allExamsParser(JSONObject result) throws JSONException {
        JSONArray jsonArray = result.getJSONArray(response);
        ArrayList<String> ExamNameList = new ArrayList<>();
        ArrayList<String> StartDateList = new ArrayList<>();
        ArrayList<String> EndDateList = new ArrayList<>();
        ArrayList<String> EndTimeList = new ArrayList<>();
        ArrayList<String> StartTimeList = new ArrayList<>();
        ArrayList<String> ExamDurationList = new ArrayList<>();
        ArrayList<String> ExamIdList = new ArrayList<>();
        HashMap<String, ArrayList<String>> mapper = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ExamNameList.add(jsonObject.getJSONArray(ExamName).get(0).toString());
            ExamDurationList.add(jsonObject.getJSONArray(ExamDuration).get(0).toString());
            StartTimeList.add(jsonObject.getJSONArray(StartTime).get(0).toString());
            EndTimeList.add(jsonObject.getJSONArray(EndTime).get(0).toString());
            StartDateList.add(jsonObject.getJSONArray(StartDate).get(0).toString());
            EndDateList.add(jsonObject.getJSONArray(EndDate).get(0).toString());
            ExamIdList.add(jsonObject.getString(id));
        }
        mapper.put("ExamName", ExamNameList);
        mapper.put("ExamDuration", ExamDurationList);
        mapper.put("StartDate", StartDateList);
        mapper.put("EndDate", EndDateList);
        mapper.put("StartTime", StartTimeList);
        mapper.put("EndTime", EndTimeList);
        mapper.put("ExamId", ExamIdList);
        return mapper;
    }

    public static HashMap examDetailsParser(String result) throws JSONException {
        HashMap<String, String> mapper = new HashMap<>();
        JSONObject jsonObj = new JSONObject(result);
        JSONObject jsonObject = jsonObj.getJSONObject(response);
        mapper.put("enrolled", jsonObject.getString(enrolled));
        mapper.put("timestamp", jsonObject.getString(timestamp));
        String examDetails = jsonObject.getJSONArray(exam).toString();
        mapper.put("examDetails", examDetails);
        return mapper;
    }

    public static HashMap join_start_Parser(String result) throws JSONException {
        HashMap<String, String> mapper = new HashMap<>();
        JSONArray jsonArray = new JSONArray(result);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        mapper.put("StartDate", jsonObject.getJSONArray(StartDate).get(0).toString());
        mapper.put("EndDate", jsonObject.getJSONArray(EndDate).get(0).toString());
        mapper.put("StartTime", jsonObject.getJSONArray(StartTime).get(0).toString());
        mapper.put("EndTime", jsonObject.getJSONArray(EndTime).get(0).toString());
        mapper.put("Description", jsonObject.getJSONArray(Description).get(0).toString());
        mapper.put("ExamName", jsonObject.getJSONArray(ExamName).get(0).toString());

        return mapper;
    }

    public static HashMap<String, String> enrollUserParser(String myResponse) throws JSONException {
        HashMap<String, String> mapper = new HashMap<>();
        JSONObject jsonObject = new JSONObject(myResponse);
        mapper.put("success", jsonObject.getString(success));
        mapper.put("response", jsonObject.getString(response));
        return mapper;
    }

    public static HashMap<String, String> unenrollUserParser(String myResponse) throws JSONException {
        HashMap<String, String> mapper = new HashMap<>();
        JSONObject jsonObject = new JSONObject(myResponse);
        mapper.put("success", jsonObject.getString(success));
        mapper.put("response", jsonObject.getString(response));
        return mapper;
    }

    public static String getJoinedExams(String myResponse) throws JSONException {
        String myJoinedExams="";
        JSONObject jsonObject=new JSONObject(myResponse);
        if (jsonObject.getJSONArray(joinedExams) != null) {
            myJoinedExams = jsonObject.getJSONArray(joinedExams).toString();
        } else {
            myJoinedExams="noJoinedExams";
        }
        return myJoinedExams;
    }

    public static String parseDate(String myDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd yyyy");
        Date date = simpleDateFormat.parse(myDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        String myParsedDate = day + "/" + month + "/" + year;
        return myParsedDate;
    }

    public static String parseDuration(String myDuration) {
        String[] parts = myDuration.split("-");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String myDurationTime = "";
        if (minute == 0) {
            myDurationTime = hour + " hours";
        } else {
            myDurationTime = hour + " hours " + minute + " minutes";
        }
        return myDurationTime;
    }

    public static HashMap changePasswordParser(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        HashMap<String, String> mapper = new HashMap<>();
        mapper.put("response", jsonObject.getString(response));
        mapper.put("success", jsonObject.getString(success));
        mapper.put("message",jsonObject.getString(message));
        return mapper;
    }

    public static String parseTimeForDetails(String myTime) throws ParseException {
        DateFormat f1 = new SimpleDateFormat("HH-mm");
        Date d = f1.parse(myTime);
        DateFormat f2 = new SimpleDateFormat("h-mm a");
        String ans=f2.format(d).toLowerCase();
        return ans;
    }


}
