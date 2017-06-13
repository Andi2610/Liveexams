package in.truskills.liveexams.JsonParsers;

import android.util.Log;

import com.google.gson.JsonArray;

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

import in.truskills.liveexams.MainScreens.Values;
import in.truskills.liveexams.ParticularExamStatistics.SectionNamesdisplayAdapterForAnswers;

/**
 * MiscellaneousParser for parsing different apis used in code..
 */

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
    private static String AuthorDetails = "AuthorDetails";
    private static String Attributes = "Attributes";
    private static String Name = "Name";
    private static String question1 = "question1";
    private static String question2 = "question2";
    private static String question3 = "question3";
    private static String Question = "Question";
    private static String questionText = "questionText";
    private static String questionTopic = "questionTopic";
    private static String questionNumber = "questionNumber";
    private static String productKits = "productKits";
    private static String authorDetails = "authorDetails";
    private static String name = "name";
    private static String startDate = "startDate";
    private static String endDate = "endDate";
    private static String courses = "courses";
    private static String paid = "paid";
    private static String examName = "examName";
    private static String examId = "examId";
    private static String courseId = "courseId";
    private static String courseName = "courseName";
    private static String description = "description";
    private static String price = "price";
    private static String amount = "amount";
    private static String boughtProductKit = "boughtProductKit";

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

    public static HashMap<String,String> signupParser(String result) throws JSONException {
        JSONObject jsonObject = new JSONObject(result);
        HashMap<String, String> mapper = new HashMap<>();
        mapper.put("response", jsonObject.getString(response));
        mapper.put("success", jsonObject.getString(success));
        return mapper;
    }

    public static HashMap<String,String> checkBeforeSignUpParser(String result) throws JSONException{
        JSONObject jsonObject = new JSONObject(result);
        HashMap<String, String> mapper = new HashMap<>();
        mapper.put("response", jsonObject.getString(response));
        mapper.put("success", jsonObject.getString(success));
        return mapper;

    }

    public static HashMap<String,String> checkBeforeGivingExam(String result) throws JSONException{
        JSONObject jsonObject = new JSONObject(result);
        HashMap<String, String> mapper = new HashMap<>();
        mapper.put("response", jsonObject.getString(response));
        mapper.put("success", jsonObject.getString(success));
        return mapper;

    }
    public static HashMap<String,String> loginParser(String result) throws JSONException {
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
        Date date;
        try{
            date = simpleDateFormat.parse(myDate);
        }catch (ParseException e){
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            date = simpleDateFormat2.parse(myDate);
        }
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
        Date d;
        try{
            d = f1.parse(myTime);
        }catch (ParseException e){
            DateFormat f3 = new SimpleDateFormat("HH:mm");
            d = f3.parse(myTime);
        }
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
        ans=jsonArray.getJSONObject(0).getString("formatted_address");
        String part[]=ans.split(",");
        int len=part.length;
        ans=part[len-3];
//        JSONArray jsonArray1=jsonArray.getJSONObject(0).getJSONArray("address_components");
//        JSONObject jsonObject1=jsonArray1.getJSONObject(5);
//        ans=jsonObject1.getString("long_name");
        return ans;
    }

    public static ArrayList<String> getStreamNamesParser(JSONObject result) throws JSONException {
        ArrayList<String> ans=new ArrayList<>();
        JSONArray jsonArray=result.getJSONArray(response);
        int len=jsonArray.length();
        for(int i=0;i<len;++i){
            ans.add(jsonArray.get(i).toString());
        }
        return ans;
    }

    public static ArrayList<String> getStreamNamesForMyKitsParser(JSONObject result) throws JSONException {
        ArrayList<String> ans=new ArrayList<>();
        JSONArray jsonArray=result.getJSONArray(response);
        int len=jsonArray.length();
        for(int i=0;i<len;++i){
            ans.add(jsonArray.get(i).toString());
        }
        return ans;
    }

    public static ArrayList<String> searchExamsByStreamNameParser(JSONObject result) throws JSONException, ParseException {
        ArrayList<String> ans=new ArrayList<>();
        HashMap<String,ArrayList<String>> map=new HashMap<>();
        JSONObject jsonObject3=result.getJSONObject(response);
        JSONArray jsonArray=jsonObject3.getJSONArray(exams);
        String timestamp = jsonObject3.getString("timestamp");
        String myStartDate, myEndDate, myDateOfStart, myDateOfEnd, myDuration, myDurationTime, myStartTime, myEndTime,myTimeOfStart,myTimeOfEnd;
        String myTimestamp,myTime;

        int len=jsonArray.length();
        for(int i=0;i<len;++i){
            JSONObject jsonObject=jsonArray.getJSONObject(i);

            JSONArray jsonArray1=jsonObject.getJSONArray(AuthorDetails);
            JSONObject jsonObject1=jsonArray1.getJSONObject(0);
            JSONObject jsonObject2=jsonObject1.getJSONObject(Attributes);
            String name=jsonObject2.getString(Name);
            Log.d("author", "searchExamsByStreamNameParser: "+name);

            myStartTime=jsonObject.getJSONArray(StartTime).get(0).toString();
            myEndTime=jsonObject.getJSONArray(EndTime).get(0).toString();
            myStartDate=jsonObject.getJSONArray(StartDate).get(0).toString();
            myEndDate=jsonObject.getJSONArray(EndDate).get(0).toString();

            myDateOfStart = parseDate(myStartDate);
            myDateOfEnd = parseDate(myEndDate);
            myTimeOfStart = MiscellaneousParser.parseTimeForDetails(myStartTime);
            myTimeOfEnd = MiscellaneousParser.parseTimeForDetails(myEndTime);
            myTimestamp = MiscellaneousParser.parseTimestamp(timestamp);
            myTime = MiscellaneousParser.parseTimestampForTime(timestamp);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date start_date = simpleDateFormat.parse(myDateOfStart);
            Date end_date = simpleDateFormat.parse(myDateOfEnd);
            Date middle_date = simpleDateFormat.parse(myTimestamp);

            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h-mm a");
            Date start_time = simpleDateFormat2.parse(myTimeOfStart);
            Date end_time = simpleDateFormat2.parse(myTimeOfEnd);
            Date middle_time = simpleDateFormat2.parse(myTime);

            if (middle_date.before(start_date)) {
               //...
                if(!ans.contains(name))
                    ans.add(name);
                map.put(name,new ArrayList<String>());
            } else if (middle_date.before(end_date) || middle_date.equals(end_date)) {
                if (middle_date.equals(end_date)) {
                    if (!middle_time.after(end_time)) {
                       //..
                        if(!ans.contains(name))
                            ans.add(name);
                        map.put(name,new ArrayList<String>());
                    }
                } else {
                   //..
                    if(!ans.contains(name))
                        ans.add(name);
                    map.put(name,new ArrayList<String>());
                }
            }
        }

        return ans;
    }

    public static ArrayList<String> searchExamsByStreamNameParserForMyKits(JSONObject result) throws JSONException, ParseException {
        ArrayList<String> ans=new ArrayList<>();
        HashMap<String,ArrayList<String>> map=new HashMap<>();
        JSONObject jsonObject3=result.getJSONObject(response);
        JSONArray jsonArray=jsonObject3.getJSONArray(productKits);
        int len=jsonArray.length();
        for(int i=0;i<len;++i){
            JSONObject jsonObject=jsonArray.getJSONObject(i);

            JSONObject jsonObject1=jsonObject.getJSONObject(authorDetails);
            String name=jsonObject1.getString(Name);
            Log.d("author", "searchExamsByStreamNameParser: "+name);
            if(!ans.contains(name))
                ans.add(name);
            map.put(name,new ArrayList<String>());

        }

        return ans;
    }

    public static HashMap<String,ArrayList<String>> getExamsByAuthors(JSONObject result,String value) throws JSONException {

        JSONObject jsonObject3=result.getJSONObject(response);
        JSONArray jsonArray=jsonObject3.getJSONArray(exams);
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

            JSONArray jsonArray1=jsonObject.getJSONArray(AuthorDetails);
            JSONObject jsonObject1=jsonArray1.getJSONObject(0);
            JSONObject jsonObject2=jsonObject1.getJSONObject(Attributes);
            String Aname=jsonObject2.getString(Name);

            if(Aname.equals(value)){

                ExamNameList.add(jsonObject.getJSONArray(ExamName).get(0).toString());

                ExamDurationList.add(jsonObject.getJSONArray(ExamDuration).get(0).toString());

                StartTimeList.add(jsonObject.getJSONArray(StartTime).get(0).toString());

                EndTimeList.add(jsonObject.getJSONArray(EndTime).get(0).toString());

                StartDateList.add(jsonObject.getJSONArray(StartDate).get(0).toString());

                EndDateList.add(jsonObject.getJSONArray(EndDate).get(0).toString());

                ExamIdList.add(jsonObject.getString(id));

            }
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

    public static HashMap<String,ArrayList<String>> getKitsByAuthors(JSONObject result,String value) throws JSONException {

        JSONObject jsonObject3=result.getJSONObject(response);
        JSONArray jsonArray=jsonObject3.getJSONArray(productKits);
        ArrayList<String> ExamNameList = new ArrayList<>();
        ArrayList<String> StartDateList = new ArrayList<>();
        ArrayList<String> EndDateList = new ArrayList<>();
        ArrayList<String> ExamIdList = new ArrayList<>();
        HashMap<String, ArrayList<String>> mapper = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            JSONObject jsonObject1=jsonObject.getJSONObject(authorDetails);
            String Aname=jsonObject1.getString(Name);

            if(Aname.equals(value)){

                ExamNameList.add(jsonObject.getString(name));

                StartDateList.add(jsonObject.getString(startDate));

                EndDateList.add(jsonObject.getString(endDate));

                ExamIdList.add(jsonObject.getString(id));

            }
        }
        mapper.put("ExamName", ExamNameList);
        mapper.put("StartDate", StartDateList);
        mapper.put("EndDate", EndDateList);
        mapper.put("ExamId", ExamIdList);
        return mapper;
    }

    public static HashMap feedbackQuestionsParser(String result) throws JSONException {
        HashMap<String,ArrayList<String>> map=new HashMap<>();
        ArrayList<String> questionIdList=new ArrayList<>();
        ArrayList<String> questionTextList=new ArrayList<>();
        ArrayList<String> questionTopicList=new ArrayList<>();
        ArrayList<String> questionNumberList=new ArrayList<>();

        JSONObject jsonObject=new JSONObject(result);
        JSONObject jsonObject1=jsonObject.getJSONObject(response);
        JSONObject j1=jsonObject1.getJSONObject(question1);
        JSONObject j2=jsonObject1.getJSONObject(question2);
        JSONObject j3=jsonObject1.getJSONObject(question3);

        questionIdList.add(j1.getString(id));
        questionIdList.add(j2.getString(id));
        questionIdList.add(j3.getString(id));

        JSONObject q1=j1.getJSONObject(Question);
        JSONObject q2=j2.getJSONObject(Question);
        JSONObject q3=j3.getJSONObject(Question);

        questionTextList.add(q1.getString(questionText));
        questionTextList.add(q2.getString(questionText));
        questionTextList.add(q3.getString(questionText));

        questionTopicList.add(q1.getString(questionTopic));
        questionTopicList.add(q2.getString(questionTopic));
        questionTopicList.add(q3.getString(questionTopic));

        questionNumberList.add(q1.getString(questionNumber));
        questionNumberList.add(q2.getString(questionNumber));
        questionNumberList.add(q3.getString(questionNumber));

        map.put("id",questionIdList);
        map.put("text",questionTextList);
        map.put("topic",questionTopicList);
        map.put("number",questionNumberList);

        return map;
    }

    public static String parseDateForKit(String myDate) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date date;
        try{
            date = simpleDateFormat.parse(myDate);
        }catch (ParseException e){
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
            date = simpleDateFormat2.parse(myDate);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month++;
        String myParsedDate = day + "/" + month + "/" + year;
        return myParsedDate;
    }

    public static HashMap<String,ArrayList<String>> getExamsAndCoursesOfOneKit(String result) throws JSONException, ParseException {
        HashMap<String,ArrayList<String>> hashMap=new HashMap<>();
        JSONObject jsonObject=new JSONObject(result);
        JSONArray jsonArray=jsonObject.getJSONArray(exams);
        JSONArray jsonArray1=jsonObject.getJSONArray(courses);
        ArrayList<String> examsPaidName=new ArrayList<>();
        ArrayList<String> examsFreeName=new ArrayList<>();
        ArrayList<String> examsPaidId=new ArrayList<>();
        ArrayList<String> examsFreeId=new ArrayList<>();
        ArrayList<String> coursesName=new ArrayList<>();
        ArrayList<String> coursesId=new ArrayList<>();
        ArrayList<String> examsPaidStartDate=new ArrayList<>();
        ArrayList<String> examsFreeStartDate=new ArrayList<>();
        ArrayList<String> examsPaidEndDate=new ArrayList<>();
        ArrayList<String> examsFreeEndDate=new ArrayList<>();
        ArrayList<String> examsFreeExamDuration=new ArrayList<>();
        ArrayList<String> examsPaidExamDuration=new ArrayList<>();

        for(int i=0;i<jsonArray.length();++i){
            JSONObject jsonObject1=jsonArray.getJSONObject(i);
            String Paid = jsonObject1.getString(paid);
            String myStartDate=MiscellaneousParser.parseDate(jsonObject1.getJSONArray(startDate).get(0).toString());
            String myEndDate=MiscellaneousParser.parseDate(jsonObject1.getJSONArray(endDate).get(0).toString());
            String eName=jsonObject1.getJSONArray(examName).get(0).toString();
            String eId=jsonObject1.getString(examId);
            String eDuration=jsonObject1.getJSONArray(ExamDuration).get(0).toString();

            if(Paid.equals("true")){

                Log.d("namesOfExams", "getExamsAndCoursesOfOneKit: Paid:"+eName);
                examsPaidName.add(eName);
                examsPaidId.add(eId);
                examsPaidStartDate.add(myStartDate);
                examsPaidEndDate.add(myEndDate);
                examsPaidExamDuration.add(eDuration);
            }else{

                Log.d("namesOfExams", "getExamsAndCoursesOfOneKit: Free:"+eName);

                examsFreeName.add(eName);
                examsFreeId.add(eId);
                examsFreeStartDate.add(myStartDate);
                examsFreeEndDate.add(myEndDate);
                examsFreeExamDuration.add(eDuration);
            }
        }
        for(int i=0;i<jsonArray1.length();++i){
            JSONObject jsonObject1=jsonArray1.getJSONObject(i);
            coursesName.add(jsonObject1.getString(courseName));
            coursesId.add(jsonObject1.getString(courseId));
//            coursesName.add("courseName");
//            coursesId.add("courseId");
        }

        hashMap.put("examsPaidName",examsPaidName);
        hashMap.put("examsPaidId",examsPaidId);
        hashMap.put("examsFreeName",examsFreeName);
        hashMap.put("examsFreeId",examsFreeId);
        hashMap.put("coursesName",coursesName);
        hashMap.put("coursesId",coursesId);
        hashMap.put("examsPaidStartDate",examsPaidStartDate);
        hashMap.put("examsFreeStartDate",examsFreeStartDate);
        hashMap.put("examsPaidEndDate",examsPaidEndDate);
        hashMap.put("examsFreeEndDate",examsFreeEndDate);
        hashMap.put("examsFreeExamDuration",examsFreeExamDuration);
        hashMap.put("examsPaidExamDuration",examsPaidExamDuration);
        return hashMap;
    }

    public static HashMap<String,String> getDetailsOfOneKit(String result) throws JSONException {
        HashMap<String,String> map=new HashMap<>();
        JSONObject jsonObject1=new JSONObject(result);
        map.put("description",jsonObject1.getString(description));
        map.put("startDate",jsonObject1.getString(startDate));
        map.put("endDate",jsonObject1.getString(endDate));
        JSONArray jsonArray=jsonObject1.getJSONArray(price);
        JSONObject jsonObject11=jsonArray.getJSONObject(0);
        map.put("price",jsonObject11.getString(amount));
        map.put("boughtProductKit",jsonObject1.getString(boughtProductKit));
        map.put("id",jsonObject1.getString(id));
        return map;
    }

}
