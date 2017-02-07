package in.truskills.liveexams.Quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

    String CREATE_MY_TABLE_PER_SECTION =
            "CREATE TABLE " + TABLE_PER_SECTION + "("
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + SectionMaxMarks + " INTEGER,"
                    + SectionTime + " TEXT,"
                    + SectionDescription + " TEXT,"
                    + SectionRules + " TEXT,"
                    + SectionName + " TEXT,"
                    + SectionId + " TEXT"
                    + ")";

    String CREATE_MY_TABLE_PER_QUESTION =
            "CREATE TABLE " + TABLE_PER_QUESTION + "("
                    + SectionIndex + " INTEGER DEFAULT 0,"
                    + QuestionIndex + " INTEGER DEFAULT 0,"
                    + QuestionId + " TEXT,"
                    + CorrectAnswer + " TEXT,"
                    + QuestionCorrectMarks + " TEXT,"
                    + QuestionIncorrectMarks + " TEXT,"
                    + PassageID + " TEXT,"
                    + QuestionType + " TEXT,"
                    + QuestionTime + " TEXT,"
                    + QuestionDifficultyLevel + " TEXT,"
                    + QuestionRelativeTopic + " TEXT,"
                    + NumberOfToggles + " INTEGER DEFAULT 0,"
                    + FinalAnswer + " INTEGER DEFAULT 0,"
                    + ReadStatus + " INTEGER DEFAULT 0,"
                    + TimeSpent + " INTEGER DEFAULT 0,"
                    + ReviewButtonClicks + " INTEGER DEFAULT 0,"
                    + SubmitButtonClicks + " INTEGER DEFAULT 0,"
                    + ClearButtonClicks + " INTEGER DEFAULT 0"
                    + ")";

    public MySqlDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MY_TABLE_PER_QUESTION);
        db.execSQL(CREATE_MY_TABLE_PER_SECTION);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_SECTION);
        // Create tables again
        onCreate(db);
    }

    public void setValuesPerSection(int si, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(SectionName, name);
        db.insert(TABLE_PER_SECTION, null, values);
        db.close(); // Closing database connection
    }

    public void setValuesPerQuestionPerSection(int si, int qi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        db.insert(TABLE_PER_QUESTION, null, values);
        db.close(); // Closing database connection
    }

    public void updateValuesPerQuestionPerSection(int si,int qi,int value,String columnName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_QUESTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi,null);
        db.close();
    }

    public int getValuesPerQuestionForQuiz(int si,int qi,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        int value=0;
        String selectQuery = "SELECT "+columnName+" FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                value=cursor.getInt(cursor.getColumnIndex(columnName));
            } while (cursor.moveToNext());
        }
        return value;
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

    public HashMap<String, String> getValuesPerQuestionPerSection(int si, int qi) {
        HashMap<String, String> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
//                Log.d("here","si="+si+" qi="+qi);
//                Log.d("here","noOfToggles="+cursor.getInt(cursor.getColumnIndex(NumberOfToggles)));
//                Log.d("here","submit="+cursor.getInt(cursor.getColumnIndex(SubmitButtonClicks)));
//                Log.d("here","review="+cursor.getInt(cursor.getColumnIndex(ReviewButtonClicks)));
//                Log.d("here","clear="+cursor.getInt(cursor.getColumnIndex(ClearButtonClicks)));
//                Log.d("here","read="+cursor.getInt(cursor.getColumnIndex(ReadStatus)));
//                Log.d("here","final="+cursor.getInt(cursor.getColumnIndex(FinalAnswer)));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return map;
    }

    public void deleteMyTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PER_SECTION);
        db.execSQL("DELETE FROM " + TABLE_PER_QUESTION);
    }
}
