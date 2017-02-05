package in.truskills.liveexams.Miscellaneous;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Shivansh Gupta on 04-02-2017.
 */

public class MySql extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "LiveExams";

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
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + QuestionId + " TEXT,"
                    + CorrectAnswer + " TEXT,"
                    + QuestionCorrectMarks + " TEXT,"
                    + QuestionIncorrectMarks + " TEXT,"
                    + PassageID + " TEXT,"
                    + QuestionType + " TEXT,"
                    + QuestionTime + " TEXT,"
                    + QuestionDifficultyLevel + " TEXT,"
                    + QuestionRelativeTopic + " TEXT"
                    + ")";


    public MySql(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MY_TABLE_PER_QUESTION);
        db.execSQL(CREATE_MY_TABLE_PER_SECTION);
    }

    public void setValuesPerSection(int si, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(SectionName, name);
        db.insert(TABLE_PER_SECTION, null, values);
        db.close(); // Closing database connection
    }

    public void setValuesPerQuestionPerSection(int si, int qi, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        db.insert(TABLE_PER_QUESTION, null, values);
        db.close(); // Closing database connection
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

        String selectQuery = "SELECT  * FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return map;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_SECTION);

        // Create tables again
        onCreate(db);
    }

    public void deleteMyTable() {
        Log.d("here","in delete table");

        //Delete all the entries in the table..
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PER_QUESTION);
        db.execSQL("DELETE FROM " + TABLE_PER_SECTION);
    }
}
