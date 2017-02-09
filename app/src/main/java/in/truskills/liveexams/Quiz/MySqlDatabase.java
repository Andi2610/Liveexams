package in.truskills.liveexams.Quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 07-02-2017.
 */

public class MySqlDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LiveExamsApplication";

    // Table name
    private static final String TABLE_PER_SECTION = "PerSectionDetails";
    private static final String TABLE_PER_QUESTION = "PerQuestionDetails";
    private static final String TABLE_PER_OPTION = "PerOptionDetails";


    // Table Columns names
    private static final String SectionIndex = "SectionIndex";
    private static final String QuestionIndex = "QuestionIndex";
    private static final String SectionMaxMarks = "SectionMaxMarks";
    private static final String SectionTime = "SectionTime";
    private static final String SectionDescription = "SectionDescription";
    private static final String SectionRules = "SectionRules";
    private static final String SectionName = "SectionName";
    private static final String SectionId = "SectionId";
    private static final String QuestionId = "QuestionId";
    private static final String CorrectAnswer = "CorrectAnswer";
    private static final String QuestionCorrectMarks = "QuestionCorrectMarks";
    private static final String QuestionIncorrectMarks = "QuestionIncorrectMarks";
    private static final String PassageID = "PassageID";
    private static final String QuestionType = "QuestionType";
    private static final String QuestionTime = "QuestionTime";
    private static final String QuestionDifficultyLevel = "QuestionDifficultyLevel";
    private static final String QuestionRelativeTopic = "QuestionRelativeTopic";
    private static final String TimeSpent = "TimeSpent";
    private static final String NumberOfToggles = "NumberOfToggles";
    private static final String FinalAnswer = "FinalAnswer";
    private static final String ReadStatus = "ReadStatus";
    private static final String ReviewButtonClicks = "ReviewButtonClicks";
    private static final String SubmitButtonClicks = "SubmitButtonClicks";
    private static final String ClearButtonClicks = "ClearButtonClicks";
    private static final String SerialNumber = "SerialNumber";
    private static final String OptionIndex = "OptionIndex";
    private static final String OptionId = "OptionId";
    private static final String OptionText = "OptionText";
    private static final String QuestionText = "QuestionText";
    private static final String FragmentIndex = "FragmentIndex";




    String CREATE_MY_TABLE_PER_SECTION =
            "CREATE TABLE " + TABLE_PER_SECTION + "("
                    + SerialNumber + " TEXT,"
                    + SectionIndex + " INTEGER PRIMARY KEY,"
                    + SectionMaxMarks + " TEXT,"
                    + SectionTime + " TEXT,"
                    + SectionDescription + " TEXT,"
                    + SectionRules + " TEXT,"
                    + SectionName + " TEXT,"
                    + SectionId + " TEXT"
                    + ")";

    String CREATE_MY_TABLE_PER_QUESTION =
            "CREATE TABLE " + TABLE_PER_QUESTION + "("
                    + SerialNumber + " TEXT,"
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + FragmentIndex + " INTEGER,"
                    + QuestionId + " TEXT,"
                    + QuestionText + " TEXT,"
                    + CorrectAnswer + " TEXT,"
                    + QuestionCorrectMarks + " TEXT,"
                    + QuestionIncorrectMarks + " TEXT,"
                    + PassageID + " TEXT,"
                    + QuestionType + " TEXT,"
                    + QuestionTime + " TEXT,"
                    + QuestionDifficultyLevel + " TEXT,"
                    + QuestionRelativeTopic + " TEXT,"
                    + " PRIMARY KEY ("+ SectionIndex+","+ QuestionIndex+")"
                    + ")";

    String CREATE_MY_TABLE_PER_OPTION =
            "CREATE TABLE " + TABLE_PER_OPTION + "("
                    + SerialNumber + " TEXT,"
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + OptionIndex + " INTEGER,"
                    + OptionId + " TEXT,"
                    + OptionText + " TEXT,"
                    + " PRIMARY KEY ("+ SectionIndex+","+ QuestionIndex+","+OptionIndex+")"
                    + ")";

    public MySqlDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MY_TABLE_PER_QUESTION);
        db.execSQL(CREATE_MY_TABLE_PER_SECTION);
        db.execSQL(CREATE_MY_TABLE_PER_OPTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_SECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_OPTION);
        // Create tables again
        onCreate(db);
    }

    public void deleteMyTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PER_SECTION);
        db.execSQL("DELETE FROM " + TABLE_PER_QUESTION);
        db.execSQL("DELETE FROM " + TABLE_PER_OPTION);
    }

    public void setValuesPerSection(int si) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        db.insert(TABLE_PER_SECTION, null, values);
        db.close(); // Closing database connection
    }

    public void setValuesPerQuestion(int si, int qi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        db.insert(TABLE_PER_QUESTION, null, values);
        db.close(); // Closing database connection
    }

    public void setValuesPerOption(int si, int qi,int oi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        values.put(OptionIndex,oi);
        db.insert(TABLE_PER_OPTION, null, values);
        db.close(); // Closing database connection
    }

    public void updateValuesPerSection(int si,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_SECTION,contentValues,SectionIndex+"="+si,null);
        db.close();
    }

    public void updateValuesPerQuestion(int si,int qi,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_QUESTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi,null);
        db.close();
    }

    public void updateValuesPerOption(int si,int qi,int oi,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_OPTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi+" AND "+OptionIndex+"="+oi,null);
        db.close();
    }

    public HashMap<String, String> getValuesPerSection(int si) {
        HashMap<String, String> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PER_SECTION + " WHERE " + SectionIndex + "=" + si;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                map.put("SectionName", cursor.getString(cursor.getColumnIndex(SectionName)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return map;
    }

    public void getAllValues(){
        SQLiteDatabase db=this.getReadableDatabase();
        String query="";
        Cursor cursor;
        query="SELECT * FROM "+TABLE_PER_SECTION;
        cursor=db.rawQuery(query,null);
        Log.d("before",cursor.getCount()+"");
        if(cursor.moveToFirst()){
            do{
                Log.d("SectionDetails=",cursor.getString(cursor.getColumnIndex(SerialNumber))+" "+cursor.getInt(cursor.getColumnIndex(SectionIndex))+" "+cursor.getString(cursor.getColumnIndex(SectionName)));
            }while(cursor.moveToNext());
        }

        query="SELECT * FROM "+TABLE_PER_QUESTION;
        cursor=db.rawQuery(query,null);
        Log.d("before",cursor.getCount()+"");
        if(cursor.moveToFirst()){
            do{
                Log.d("QuestionDetails=",cursor.getString(cursor.getColumnIndex(SerialNumber))+" "+cursor.getInt(cursor.getColumnIndex(SectionIndex))+" "+cursor.getInt(cursor.getColumnIndex(QuestionIndex))+" "+cursor.getInt(cursor.getColumnIndex(FragmentIndex)));
            }while(cursor.moveToNext());
        }

    }

    public String getStringValuesPerQuestionByFragmentIndex(int fI,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        String ans="";
        String query="SELECT "+columnName+" FROM "+TABLE_PER_QUESTION+" WHERE "+FragmentIndex+"="+fI;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getString(cursor.getColumnIndex(columnName));
            }while(cursor.moveToNext());
        }
        return ans;
    }

    public int getIntValuesPerQuestionByFragmentIndex(int fI,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        int ans=0;
        String query="SELECT "+columnName+" FROM "+TABLE_PER_QUESTION+" WHERE "+FragmentIndex+"="+fI;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getInt(cursor.getColumnIndex(columnName));
            }while(cursor.moveToNext());
        }
        return ans;
    }

    public int getIntValuesPerQuestionBySiAndSrno(int si,int srno,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        int ans=0;
        String query="SELECT "+columnName+" FROM "+TABLE_PER_QUESTION+" WHERE "+SectionIndex+"="+si+" AND "+SerialNumber+"="+srno;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getInt(cursor.getColumnIndex(columnName));
            }while(cursor.moveToNext());
        }
        return ans;
    }

    public HashMap<String,ArrayList<Integer>> getAllIntValuesPerQuestionBySectionIndex(int si){
        HashMap<String,ArrayList<Integer>> map=new HashMap<>();
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<Integer> fI=new ArrayList<>();
        String query="SELECT * FROM "+TABLE_PER_QUESTION+" WHERE "+SectionIndex+"="+si+" ORDER BY "+SerialNumber;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                fI.add(cursor.getInt(cursor.getColumnIndex(FragmentIndex)));
            }while(cursor.moveToNext());
        }
        map.put("FragmentIndexList",fI);
        return map;
    }

    public HashMap<String,ArrayList<String>> getAllStringValuesPerSection(){
        HashMap<String,ArrayList<String>> map=new HashMap<>();
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<String> fI=new ArrayList<>();
        String query="SELECT * FROM "+TABLE_PER_SECTION+" ORDER BY "+SerialNumber;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                fI.add(cursor.getString(cursor.getColumnIndex(SectionName)));
            }while(cursor.moveToNext());
        }
        map.put("SectionNameList",fI);
        return map;
    }

    public String getStringValuesPerSectionBySectionIndex(int si,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        String ans="";
        String query="SELECT "+columnName+" FROM "+TABLE_PER_SECTION+" WHERE "+SectionIndex+"="+si;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getString(cursor.getColumnIndex(columnName));
            }while(cursor.moveToNext());
        }
        return ans;
    }

    public int getIntValuesPerSectionBySerialNumber(int srNo,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        int ans=0;
        String query="SELECT "+columnName+" FROM "+TABLE_PER_SECTION+" WHERE "+SerialNumber+"="+srNo;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getInt(cursor.getColumnIndex(columnName));
            }while(cursor.moveToNext());
        }
        return ans;
    }

//    public int getValuesPerQuestionForQuiz(int si,int qi,String columnName){
//        SQLiteDatabase db=this.getReadableDatabase();
//        int value=0;
//        String selectQuery = "SELECT "+columnName+" FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        if (cursor.moveToFirst()) {
//            do {
//                value=cursor.getInt(cursor.getColumnIndex(columnName));
//            } while (cursor.moveToNext());
//        }
//        return value;
//    }
//
//
//    public HashMap<String, String> getValuesPerQuestionPerSection(int si, int qi) {
//        HashMap<String, String> map = new HashMap<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        String selectQuery = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
//        Cursor cursor = db.rawQuery(selectQuery, null);
//        Log.d("messi","noOfRows="+cursor.getCount());
//        if (cursor.moveToFirst()) {
//            do {
////                Log.d("here","si="+si+" qi="+qi);
////                Log.d("here","noOfToggles="+cursor.getInt(cursor.getColumnIndex(NumberOfToggles)));
////                Log.d("here","submit="+cursor.getInt(cursor.getColumnIndex(SubmitButtonClicks)));
////                Log.d("here","review="+cursor.getInt(cursor.getColumnIndex(ReviewButtonClicks)));
////                Log.d("here","clear="+cursor.getInt(cursor.getColumnIndex(ClearButtonClicks)));
////                Log.d("here","read="+cursor.getInt(cursor.getColumnIndex(ReadStatus)));
////                Log.d("here","final="+cursor.getInt(cursor.getColumnIndex(FinalAnswer)));
//
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//
//        return map;
//    }

}
