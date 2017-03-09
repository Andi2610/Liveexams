package in.truskills.liveexams.SqliteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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
    public static final String SectionMarks = "sectionMarks";
    public static final String SectionWiseMarks = "sectionWiseMarks";
    public static final String SectionWiseAttemptedQuestions = "sectionWiseAttemptedQuestions";
    public static final String SectionWiseTimeSpent = "sectionWiseTimeSpent";
    public static final String SectionWiseRank = "sectionWiseRank";
    public static final String QuestionIndex = "QuestionIndex";
    public static final String RightAnswer = "rightAnswer";
    public static final String RightAnsweredBy = "rightAnsweredBy";
    public static final String MinimumTime = "minimumTime";
    public static final String MaximumTime = "maximumTime";
    public static final String TimeSpent = "timeSpent";
    public static final String QuestionText = "questionText";
    public static final String FragmentIndex = "fragmentIndex";
    public static final String FinalAnswerId = "finalAnswerId";
    public static final String OptionIndex = "OptionIndex";
    public static final String OptionText = "optionText";
    public static final String SectionId = "sectionId";
    public static final String QuestionId = "questionId";
    public static final String OptionId = "optionId";
    public static final String QuestionStatus = "questionStatus";
    public static final String SectionTime = "sectionTime";


    String CREATE_MY_TABLE_PER_SECTION =
            "CREATE TABLE " + TABLE_PER_SECTION + "("
                    + SectionIndex + " INTEGER PRIMARY KEY,"
                    + SectionId + " TEXT,"
                    + SectionName + " TEXT,"
                    + SectionMarks + " TEXT,"
                    + SectionWiseMarks + " TEXT,"
                    + SectionWiseAttemptedQuestions + " TEXT,"
                    + SectionWiseTimeSpent + " TEXT,"
                    + SectionWiseRank + " TEXT DEFAULT '1',"
                    + SectionTime + " TEXT"
                    + ")";

    String CREATE_MY_TABLE_PER_QUESTION =
            "CREATE TABLE " + TABLE_PER_QUESTION + "("
                    + FragmentIndex + " INTEGER,"
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + SectionId + " TEXT,"
                    + QuestionId + " TEXT,"
                    + QuestionText + " TEXT,"
                    + RightAnswer + " TEXT,"
                    + RightAnsweredBy + " TEXT,"
                    + MinimumTime + " TEXT,"
                    + MaximumTime + " TEXT,"
                    + TimeSpent + " TEXT,"
                    + FinalAnswerId + " TEXT,"
                    + QuestionStatus + " TEXT,"
                    + " PRIMARY KEY (" + SectionIndex + "," + QuestionIndex + ")"
                    + ")";

    String CREATE_MY_TABLE_PER_OPTION =
            "CREATE TABLE " + TABLE_PER_OPTION + "("
                    + SectionIndex + " INTEGER,"
                    + QuestionIndex + " INTEGER,"
                    + OptionIndex + " INTEGER,"
                    + SectionId + " TEXT,"
                    + QuestionId + " TEXT,"
                    + OptionId + " TEXT,"
                    + OptionText + " TEXT,"
                    + " PRIMARY KEY (" + SectionIndex + "," + QuestionIndex + "," + OptionIndex + ")"
                    + ")";

    public AnalyticsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c = context;
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

    public void deleteMyTable() {
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
//        db.close(); // Closing database connection
    }

    public void setValuesPerQuestion(int si, int qi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        db.insert(TABLE_PER_QUESTION, null, values);
//        db.close(); // Closing database connection
    }

    public void setValuesPerOption(int si, int qi, int oi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        values.put(OptionIndex, oi);
        db.insert(TABLE_PER_OPTION, null, values);
//        db.close(); // Closing database connection
    }

    public void updateValuesPerSection(int si, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        db.update(TABLE_PER_SECTION, contentValues, SectionIndex + "=" + si, null);
//        db.close();
    }

    public void updateValuesPerQuestion(int si, int qi, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        db.update(TABLE_PER_QUESTION, contentValues, SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi, null);
//        db.close();
    }

    public void updateValuesPerOption(int si, int qi, int oi, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        db.update(TABLE_PER_OPTION, contentValues, SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi + " AND " + OptionIndex + "=" + oi, null);
//        db.close();
    }

    public void updateValuesPerSectionById(int si, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        db.update(TABLE_PER_SECTION, contentValues, SectionId + "='" + si + "'", null);
//        db.close();
    }

    public void updateValuesPerQuestionById(int si, int qi, String columnName, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(columnName, value);
        db.update(TABLE_PER_QUESTION, contentValues, SectionId + "='" + si + "' AND " + QuestionId + "='" + qi + "'", null);
//        db.close();
    }

    public int getNoOfOptionsInOneQuestion(int si, int qi) {
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PER_OPTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
        Cursor cursor = db.rawQuery(query, null);
        num = cursor.getCount();
        cursor.close();
//        db.close();
        return num;
    }

    public String getTextOfOneQuestion(int si, int qi) {
        String text = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + QuestionText + " FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                text = cursor.getString(cursor.getColumnIndex(QuestionText));
            } while (cursor.moveToNext());
        }

        cursor.close();
//        db.close();
        return text;
    }

    public String getTextOfOneOption(int si, int qi, int oi) {
        String text = "";
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + OptionText + " FROM " + TABLE_PER_OPTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi + " AND " + OptionIndex + "=" + oi;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                text = cursor.getString(cursor.getColumnIndex(OptionText));
            } while (cursor.moveToNext());
        }

        cursor.close();
//        db.close();
        return text;
    }

    public String getStringValuesPerQuestion(int si, int qi, String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String ans = "";
        String query = "SELECT " + columnName + " FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " AND " + QuestionIndex + "=" + qi;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ans = cursor.getString(cursor.getColumnIndex(columnName));
            } while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return ans;
    }

    public String getValuesPerQuestionByFragmentIndex(int fi, String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String ans = "";
        String query = "SELECT " + columnName + " FROM " + TABLE_PER_QUESTION + " WHERE " + FragmentIndex + "=" + fi;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ans = cursor.getString(cursor.getColumnIndex(columnName));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ans;
    }

    public String getValuesPerSection(int si, String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String ans = "";
        String query = "SELECT " + columnName + " FROM " + TABLE_PER_SECTION + " WHERE " + SectionIndex + "=" + si;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ans = cursor.getString(cursor.getColumnIndex(columnName));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ans;
    }

    public ArrayList<String> getAllStringValuesPerSection(String columnName) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> ans = new ArrayList<>();
        String query = "SELECT " + columnName + " FROM " + TABLE_PER_SECTION;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ans.add(cursor.getString(cursor.getColumnIndex(columnName)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ans;
    }

    public ArrayList<Integer> getIntValuesOfEachSection(int si, String columnName) {
        ArrayList<Integer> types = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + columnName + " FROM " + TABLE_PER_QUESTION + " WHERE " + SectionIndex + "=" + si + " ORDER BY " + FragmentIndex;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String s = cursor.getString(cursor.getColumnIndex(columnName));
                int n = Integer.parseInt(s);
                types.add(n);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return types;
    }

    public ArrayList<Integer> getTypes(int si) {
        ArrayList<Integer> types = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + QuestionStatus + "=0 AND " + SectionIndex + "=" + si;

        Cursor cursor = db.rawQuery(query, null);
        types.add(cursor.getCount());
        cursor.close();

        query = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + QuestionStatus + "=1 AND " + SectionIndex + "=" + si;
        cursor = db.rawQuery(query, null);
        types.add(cursor.getCount());
        cursor.close();

        query = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + QuestionStatus + "=2 AND " + SectionIndex + "=" + si;
        cursor = db.rawQuery(query, null);
        types.add(cursor.getCount());
        cursor.close();

        query = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + QuestionStatus + "=3 AND " + SectionIndex + "=" + si;
        cursor = db.rawQuery(query, null);
        types.add(cursor.getCount());
        cursor.close();

        query = "SELECT * FROM " + TABLE_PER_QUESTION + " WHERE " + QuestionStatus + "=4 AND " + SectionIndex + "=" + si;
        cursor = db.rawQuery(query, null);
        types.add(cursor.getCount());
        cursor.close();
//        db.close();

        return types;

    }

}
