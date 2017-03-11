package in.truskills.liveexams.JsonParsers;

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
import java.util.Map;

import in.truskills.liveexams.ParticularExamStatistics.SectionNamesdisplayAdapterForAnswers;

//This class contains miscellaneous functions and variables used in the entire code..

public class MiscellaneousParser {

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
    private static String exams = "exams";
    private static String Description = "Description";
    private static String leftExam = "leftExam";
    private static String message = "message";
    private static String Languages = "Languages";
    private static String Language = "Language";
    private static String examGiven = "examGiven";

    public static ArrayList<String> beforeSignupParser(String result) throws JSONException {
        ArrayList<String> list=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(result);
        String respo=jsonObject.getString(response);
        JSONObject jsonObject1=new JSONObject(respo);
        String languages=jsonObject1.getJSONArray(Languages).toString();
        JSONArray jsonArray=new JSONArray(languages);
        for(int i=0;i<jsonArray.length();++i){
            list.add(jsonArray.get(i).toString());
        }
        return list;
    }

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
        ArrayList<String> startTimeList = new ArrayList<>();
        ArrayList<String> endTimeList = new ArrayList<>();
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
                startTimeList.add(jsonObject.getJSONArray(StartTime).get(0).toString());
                endTimeList.add(jsonObject.getJSONArray(EndTime).get(0).toString());
            }
        }
        mapper.put("ExamName", ExamNameList);
        mapper.put("ExamDuration", ExamDurationList);
        mapper.put("StartDate", StartDateList);
        mapper.put("EndDate", EndDateList);
        mapper.put("ExamId", ExamIdList);
        mapper.put("leftExam", leftExamList);
        mapper.put("StartTime", startTimeList);
        mapper.put("EndTime", endTimeList);

        return mapper;
    }

    public static HashMap<String,String> allExamsApiParser(JSONObject result) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        map.put("response",result.getJSONObject(response).toString());
        map.put("success",result.getString(success));
        JSONObject jsonObject1=new JSONObject(result.getJSONObject(response).toString());
        String myTimestamp=jsonObject1.getString(timestamp);
        String myExams=jsonObject1.getJSONArray(exams).toString();
        map.put("timestamp",myTimestamp);
        map.put("exams",myExams);
        return map;
    }

    public static HashMap allExamsParser(String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
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
        mapper.put("examGiven",jsonObject.getString(examGiven));
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
        mapper.put("Languages",jsonObject.getJSONArray(Languages).get(0).toString());
        return mapper;
    }

    public static ArrayList<String> getLanguagesPerExam(String myLanguages) throws JSONException {
        ArrayList<String> result=new ArrayList<>();
        JSONObject jsonObject=new JSONObject(myLanguages);
        String language=jsonObject.getJSONArray(Language).toString();
        JSONArray jsonArray=new JSONArray(language);
        for(int i=0;i<jsonArray.length();++i){
            result.add(jsonArray.get(i).toString());
        }
        return result;
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
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

    public static String parseTimestamp(String myDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    public static String parseTimestampForTime(String myTime) throws ParseException {
        DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        Date d = f1.parse(myTime);
        DateFormat f2 = new SimpleDateFormat("h-mm a");
        String ans=f2.format(d).toLowerCase();
        return ans;
    }

    public static HashMap analyzedExamsParser(JSONObject result) throws JSONException {
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

    public static String locationParser(String result) throws JSONException {
        String ans="";
        JSONObject jsonObject=new JSONObject(result);
        JSONArray jsonArray=jsonObject.getJSONArray("results");
//        ans=jsonArray.getJSONObject(0).getString("formatted_address");
        JSONArray jsonArray1=jsonArray.getJSONObject(0).getJSONArray("address_components");
        JSONObject jsonObject1=jsonArray1.getJSONObject(5);
        ans=jsonObject1.getString("long_name");
        return ans;
    }

}
