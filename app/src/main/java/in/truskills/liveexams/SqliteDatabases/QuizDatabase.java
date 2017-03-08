package in.truskills.liveexams.SqliteDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.MainScreens.MainActivity;

/**
 * Created by Shivansh Gupta on 07-02-2017.
 */

public class QuizDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "LiveExamsApplication";

    // Table name
    public static final String TABLE_PER_SECTION = "PerSectionDetails";
    public static final String TABLE_PER_QUESTION = "PerQuestionDetails";
    public static final String TABLE_PER_OPTION = "PerOptionDetails";
    public static final String RESULT_TABLE = "ResultTable";

    // Table Columns names
    public static final String SectionIndex = "sectionIndex";
    public static final String QuestionIndex = "questionIndex";
    public static final String SectionMaxMarks = "sectionMaxMarks";
    public static final String SectionTime = "sectionTime";
    public static final String SectionDescription = "sectionDescription";
    public static final String SectionRules = "sectionRules";
    public static final String SectionName = "sectionName";
    public static final String SectionId = "sectionId";
    public static final String QuestionId = "questionId";
    public static final String CorrectAnswer = "correctAnswer";
    public static final String QuestionCorrectMarks = "questionCorrectMarks";
    public static final String QuestionIncorrectMarks = "questionIncorrectMarks";
    public static final String PassageID = "passageID";
    public static final String QuestionType = "questionType";
    public static final String QuestionTime = "questionTime";
    public static final String QuestionDifficultyLevel = "questionDifficultyLevel";
    public static final String QuestionRelativeTopic = "questionRelativeTopic";
    public static final String TimeSpent = "timeSpent";
    public static final String NumberOfToggles = "numberOfToggles";
    public static final String FinalAnswerSerialNumber = "finalAnswerSerialNumber";
    public static final String FinalAnswerId = "finalAnswerId";
    public static final String ReadStatus = "readStatus";
    public static final String SerialNumber = "serialNumber";
    public static final String OptionIndex = "optionIndex";
    public static final String OptionId = "optionId";
    public static final String OptionText = "optionText";
    public static final String QuestionText = "questionText";
    public static final String FragmentIndex = "fragmentIndex";
    public static final String QuestionStatus = "questionStatus";
    public static final String TempAnswerSerialNumber = "tempAnswerSerialNumber";



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

    String CREATE_MY_RESULT_TABLE =
            "CREATE TABLE " + RESULT_TABLE + "("
                    + SectionIndex + " INTEGER DEFAULT 0,"
                    + QuestionIndex + " INTEGER DEFAULT 0,"
                    + FragmentIndex + " INTEGER DEFAULT 0,"
                    + SerialNumber + " TEXT DEFAULT '0',"
                    + FinalAnswerSerialNumber + " TEXT DEFAULT '-1',"
                    + TempAnswerSerialNumber + " TEXT DEFAULT '-1',"
                    + QuestionStatus + " TEXT DEFAULT '4',"
                    + SectionId + " TEXT DEFAULT 'SectionId',"
                    + QuestionId + " TEXT DEFAULT 'QuestionId',"
                    + FinalAnswerId + " TEXT DEFAULT '-1',"
                    + NumberOfToggles + " TEXT DEFAULT '-1',"
                    + ReadStatus + " TEXT DEFAULT '0',"
                    + TimeSpent + " TEXT DEFAULT '0',"
                    + " PRIMARY KEY (" + SectionIndex + "," + QuestionIndex + ")"
                    + ")";

    Context c;

    public QuizDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        c=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MY_TABLE_PER_QUESTION);
        db.execSQL(CREATE_MY_TABLE_PER_SECTION);
        db.execSQL(CREATE_MY_TABLE_PER_OPTION);
        db.execSQL(CREATE_MY_RESULT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_QUESTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_SECTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PER_OPTION);
        db.execSQL("DROP TABLE IF EXISTS " + RESULT_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void deleteMyTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PER_SECTION);
        db.execSQL("DELETE FROM " + TABLE_PER_QUESTION);
        db.execSQL("DELETE FROM " + TABLE_PER_OPTION);
        db.execSQL("DELETE FROM " + RESULT_TABLE);

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

    public void setValuesPerOption(int si, int qi,int oi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        values.put(OptionIndex,oi);
        db.insert(TABLE_PER_OPTION, null, values);
//        db.close(); // Closing database connection
    }

    public void setValuesForResult(int si, int qi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SectionIndex, si);
        values.put(QuestionIndex, qi);
        db.insert(RESULT_TABLE, null, values);
//        db.close(); // Closing database connection
    }

    public void updateValuesPerSection(int si,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_SECTION,contentValues,SectionIndex+"="+si,null);
//        db.close();
    }

    public void updateValuesPerQuestion(int si,int qi,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_QUESTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi,null);
//        db.close();
    }

    public void updateValuesPerOption(int si,int qi,int oi,String columnName,String value){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(TABLE_PER_OPTION,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi+" AND "+OptionIndex+"="+oi,null);
//        db.close();
    }

    public void updateValuesForResult(int si,int qi,String columnName,String value){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnName,value);
        db.update(RESULT_TABLE,contentValues,SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi,null);
//        db.close();
    }

    public String getValuesForResult(int si,int qi,String columnName){
        SQLiteDatabase db=this.getReadableDatabase();
        String ans="";
        String query="SELECT "+columnName+" FROM "+RESULT_TABLE+" WHERE "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi;
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

    public ArrayList<Integer> getTypes(int si){
        ArrayList<Integer> types=new ArrayList<>();
        Cursor cursor;
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+RESULT_TABLE+" WHERE "+QuestionStatus+"=0 AND "+SectionIndex+"="+si;

        cursor=db.rawQuery(query,null);
        types.add(cursor.getCount());
        cursor.close();

        query="SELECT * FROM "+RESULT_TABLE+" WHERE "+QuestionStatus+"=1 AND "+SectionIndex+"="+si;
        cursor=db.rawQuery(query,null);
        types.add(cursor.getCount());
        cursor.close();

        query="SELECT * FROM "+RESULT_TABLE+" WHERE "+QuestionStatus+"=2 AND "+SectionIndex+"="+si;
        cursor=db.rawQuery(query,null);
        types.add(cursor.getCount());
        cursor.close();

        query="SELECT * FROM "+RESULT_TABLE+" WHERE "+QuestionStatus+"=3 AND "+SectionIndex+"="+si;
        cursor=db.rawQuery(query,null);
        types.add(cursor.getCount());
        cursor.close();

        query="SELECT * FROM "+RESULT_TABLE+" WHERE "+QuestionStatus+"=4 AND "+SectionIndex+"="+si;
        cursor=db.rawQuery(query,null);
        types.add(cursor.getCount());
        cursor.close();
//        db.close();

        return types;

    }

    public ArrayList<Integer> getTypesOfEachSection(int si){
        ArrayList<Integer> types=new ArrayList<>();
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT "+QuestionStatus+" FROM "+RESULT_TABLE+" WHERE "+SectionIndex+"="+si+" ORDER BY "+FragmentIndex;

        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                String s=cursor.getString(cursor.getColumnIndex(QuestionStatus));
                int n=Integer.parseInt(s);
                types.add(n);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return types;
    }

    public int getTypeOfAQuestion(int si,int qi){

        String ans="";
        int type=-1;
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT "+FinalAnswerSerialNumber+" FROM "+RESULT_TABLE+" WHERE "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi;

        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                ans=cursor.getString(cursor.getColumnIndex(FinalAnswerSerialNumber));
                type=Integer.parseInt(ans);
            }while(cursor.moveToNext());
        }
        cursor.close();
//        db.close();

        return type;

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
//        db.close();
        return map;
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
        cursor.close();
//        db.close();
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
        cursor.close();
//        db.close();
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
        cursor.close();
//        db.close();
        return ans;
    }

    public HashMap<String,ArrayList<Integer>> getAllIntValuesPerQuestionBySectionIndex(int si){
        HashMap<String,ArrayList<Integer>> map=new HashMap<>();
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<Integer> fI=new ArrayList<>();
        String query="SELECT * FROM "+TABLE_PER_QUESTION+" WHERE "+SectionIndex+"="+si+" ORDER BY "+FragmentIndex;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                fI.add(cursor.getInt(cursor.getColumnIndex(FragmentIndex)));
            }while(cursor.moveToNext());
        }
        map.put("FragmentIndexList",fI);

        cursor.close();
//        db.close();

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

        cursor.close();
//        db.close();

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

        cursor.close();
//        db.close();

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

        cursor.close();
//        db.close();

        return ans;
    }

    public String getOptionIdBySerialNumber(String srno,int si,int qi){
        String sr="";
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT "+OptionId+" FROM "+TABLE_PER_OPTION+" WHERE "+SerialNumber+"="+srno+" AND "+SectionIndex+"="+si+" AND "+QuestionIndex+"="+qi;
        Cursor cursor=db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                sr=cursor.getString(cursor.getColumnIndex(OptionId));
            }while(cursor.moveToNext());
        }

        cursor.close();
//        db.close();

        return sr;

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

    public JSONArray getResults(String table_name)
    {

//        String myPath = DATABASE_NAME;// Set path to your database
        File path=c.getDatabasePath(DATABASE_NAME);
        String db_path=path.getAbsolutePath();

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READONLY);


        String searchQuery = "SELECT  * FROM "+table_name;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet 	= new JSONArray();
        JSONObject returnObj 	= new JSONObject();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {

                    try
                    {

                        if( cursor.getString(i) != null )
                        {
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                    }
                }

            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;

    }

    public JSONArray getQuizResult(){
        File path=c.getDatabasePath(DATABASE_NAME);
        String db_path=path.getAbsolutePath();

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READONLY);


        String searchQuery = "SELECT "+SectionId+","+QuestionId+","+FinalAnswerId+","+QuestionStatus+","+NumberOfToggles+","+ReadStatus+","+TimeSpent+" FROM "+RESULT_TABLE;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet 	= new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {

                    try
                    {

                        if( cursor.getString(i) != null )
                        {
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                    }
                }

            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        return resultSet;
    }
//
//    public void getAllValues(){
//        SQLiteDatabase db=this.getReadableDatabase();
//        String query="SELECT * FROM "+TABLE_PER_SECTION;
//        Cursor cursor=db.rawQuery(query,null);
//        if(cursor.moveToFirst()){
//            do{
//                Log.d("sectionWise",cursor.getString(cursor.getColumnIndex(SerialNumber))+"**"+cursor.getString(cursor.getColumnIndex(SectionIndex))+"**"+cursor.getString(cursor.getColumnIndex(SectionId)));
//            }while(cursor.moveToNext());
//        }
//        query="SELECT * FROM "+TABLE_PER_QUESTION;
//        cursor=db.rawQuery(query,null);
//        if(cursor.moveToFirst()){
//            do{
//                Log.d("questionWise",cursor.getString(cursor.getColumnIndex(SerialNumber))+"**"+cursor.getString(cursor.getColumnIndex(SectionIndex))+"**"+cursor.getString(cursor.getColumnIndex(QuestionIndex))+"**"+cursor.getString(cursor.getColumnIndex(QuestionId)));
//            }while(cursor.moveToNext());
//        }
//        query="SELECT * FROM "+TABLE_PER_OPTION;
//        cursor=db.rawQuery(query,null);
//        if(cursor.moveToFirst()){
//            do{
//                Log.d("questionWise",cursor.getString(cursor.getColumnIndex(SerialNumber))+"**"+cursor.getString(cursor.getColumnIndex(SectionIndex))+"**"+cursor.getString(cursor.getColumnIndex(QuestionIndex))+"**"+cursor.getString(cursor.getColumnIndex(OptionIndex))+"**"+cursor.getString(cursor.getColumnIndex(OptionId)));
//            }while(cursor.moveToNext());
//        }
//        query="SELECT * FROM "+RESULT_TABLE;
//        cursor=db.rawQuery(query,null);
//        if(cursor.moveToFirst()){
//            do{
//                Log.d("result",cursor.getString(cursor.getColumnIndex(SectionId))+"**"+cursor.getString(cursor.getColumnIndex(QuestionId))+"**"+cursor.getString(cursor.getColumnIndex(SectionIndex))+"**"+cursor.getString(cursor.getColumnIndex(QuestionIndex))+"**"+cursor.getString(cursor.getColumnIndex(FinalAnswerSerialNumber))+"**"+cursor.getString(cursor.getColumnIndex(FinalAnswerId)));
//            }while(cursor.moveToNext());
//        }
//        cursor.close();
//    }

}
