package in.truskills.liveexams.SqliteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shivansh Gupta on 25-02-2017.
 */

public class AnalyticsDatabase extends SQLiteOpenHelper {

    Context c;

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LiveExamsApplicationAnalytics";

    // Table name
    public static final String TABLE_PER_SECTION = "PerSectionDetails";
    public static final String TABLE_PER_QUESTION = "PerQuestionDetails";
    public static final String TABLE_PER_OPTION = "PerOptionDetails";

    // Table Columns names
    public static final String SectionName = "sectionName";
    public static final String SectionIndex = "SectionIndex";
    public static final String SectionMarks="sectionMarks";
    public static final String SectionWiseMarks="sectionWiseMarks";
    public static final String SectionWiseAttemptedQuestions="sectionWiseAttemptedQuestions";
    public static final String SectionWiseTimeSpent="sectionWiseTimeSpent";
    public static final String SectionWiseRank="sectionWiseRank";
    public static final String QuestionIndex = "QuestionIndex";
    public static final String RightAnswer = "rightAnswer";
    public static final String RightAnsweredBy="rightAnsweredBy";
    public static final String MinimumTime="minimumTime";
    public static final String MaximumTime="maximumTime";
    public static final String TimeSpent = "timeSpent";
    public static final String QuestionText = "questionText";
    public static final String FragmentIndex = "fragmentIndex";
    public static final String FinalAnswerId = "finalAnswerId";
    public static final String OptionIndex = "OptionIndex";
    public static final String OptionText = "optionText";


    String CREATE_MY_TABLE_PER_SECTION =
            "CREATE TABLE " + TABLE_PER_SECTION + "("
                    + SectionIndex + " INTEGER PRIMARY KEY,"
                    + SectionName + " TEXT,"
                    + SectionMarks + " TEXT,"
                    + SectionWiseMarks + " TEXT,"
                    + SectionWiseAttemptedQuestions + " TEXT,"
                    + SectionWiseTimeSpent + " TEXT,"
                    + SectionWiseRank + " TEXT,"
                    + ")";

    String CREATE_MY_TABLE_PER_QUESTION =
            "CREATE TABLE " + TABLE_PER_QUESTION + "("
                    + FragmentIndex + " INTEGER,"
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " TEXT,"
                    + QuestionText + " TEXT,"
                    + RightAnswer + " TEXT,"
                    + RightAnsweredBy + " TEXT,"
                    + MinimumTime + " TEXT,"
                    + MaximumTime + " TEXT,"
                    + TimeSpent + " TEXT,"
                    + FinalAnswerId + " TEXT,"
                    + " PRIMARY KEY ("+ SectionIndex+","+ QuestionIndex+")"
                    + ")";

    String CREATE_MY_TABLE_PER_OPTION =
            "CREATE TABLE " + TABLE_PER_OPTION + "("
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + OptionIndex + " TEXT,"
                    + OptionText + " TEXT,"
                    + " PRIMARY KEY ("+ SectionIndex+","+ QuestionIndex+","+OptionIndex+")"
                    + ")";

    public AnalyticsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c=context;
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
    }

    public void deleteMyTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PER_SECTION);
        db.execSQL("DELETE FROM " + TABLE_PER_QUESTION);
        db.execSQL("DELETE FROM " + TABLE_PER_OPTION);
    }

    public void setValuesPerSection(String si) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        db.insert(TABLE_PER_SECTION, null, values);
//        db.close(); // Closing database connection
    }

    public void setValuesPerQuestion(String si, String qi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        db.insert(TABLE_PER_QUESTION, null, values);
//        db.close(); // Closing database connection
    }

    public void setValuesPerOption(String si, String qi,String oi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        values.put(OptionIndex,oi);
        db.insert(TABLE_PER_OPTION, null, values);
//        db.close(); // Closing database connection
    }

    public void updateValuesPerSection(String si,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_SECTION,contentValues,SectionIndex+"="+si,null);
//        db.close();
    }

    public void updateValuesPerQuestion(String si,String qi,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_QUESTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi,null);
//        db.close();
    }

    public void updateValuesPerOption(String si,String qi,String oi,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_OPTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi+" AND "+OptionIndex+"="+oi,null);
//        db.close();
    }
    public int getNoOfOptionsInOneQuestion(int si,int qi){
        int num=0;
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_PER_OPTION+" WHERE "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi;
        Cursor cursor=db.rawQuery(query,null);
        num=cursor.getCount();
        cursor.close();
//        db.close();
        return num;
    }

    public String getTextOfOneQuestion(int si,int qi){
        String text="";
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT "+QuestionText+" FROM "+TABLE_PER_QUESTION+" WHERE "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                text=cursor.getString(cursor.getColumnIndex(QuestionText));
            }while(cursor.moveToNext());
        }

        cursor.close();
//        db.close();
        return text;
    }

    public String getTextOfOneOption(int si,int qi,int oi){
        String text="";
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT "+OptionText+" FROM "+TABLE_PER_OPTION+" WHERE "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi+" AND "+OptionIndex+"="+oi;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                text=cursor.getString(cursor.getColumnIndex(OptionText));
            }while(cursor.moveToNext());
        }

        cursor.close();
//        db.close();
        return text;
    }

    public String getStringValuesPerQuestion(int si,int qi,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        String ans="";
        String query="SELECT "+columnName+" FROM "+TABLE_PER_QUESTION+" WHERE "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getString(cursor.getColumnIndex(columnName));
            }while(cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return ans;
    }

}
