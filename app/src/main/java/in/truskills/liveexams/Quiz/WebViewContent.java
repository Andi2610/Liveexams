package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;

/**
 * Created by Shivansh Gupta on 27-01-2017.
 */

public class WebViewContent {


    static  Bitmap bmp;

    public void contentGenerator(final String question, final ArrayList<String> optionsList, final String examId,final WebView webView,final int mySi,final int myQi,final Context c,final MyFragmentInterface obb,String myType) {

        //Get size of options list..
        int optionsListSize = optionsList.size();
        //Design proper format of the question..
        String formattedQuestion="";
        ArrayList<String> formattedOptions= new ArrayList<>();;

        if(myType.equals("online")){
            formattedQuestion = format(question, examId);
            for (int i = 0; i < optionsListSize; ++i) {
                //Design proper format of the options..
                String formattedOption = format(optionsList.get(i), examId);
                formattedOptions.add(formattedOption);
            }
        }else{
            //Load images offline from local storage..
            formattedQuestion=question;
            for (int i = 0; i < optionsListSize; ++i) {
                //Design proper format of the options..
                String formattedOption = optionsList.get(i);
                formattedOptions.add(formattedOption);
            }
        }

        String x = "";

        //Dynamic radio buttons added depending upon the options list size..
        for (int i = 0; i < optionsListSize; ++i) {
            x = x + "<input type=\"radio\" id=\"" + i + "\" name=\"options\" value=\"" + i + "\" onclick=\"ok.performClick(this.value);\" ><label for=\""+i+"\">" + formattedOptions.get(i) + "</label></input><br>";
        }

        //Generate the html content..
        String content =
                "<html>\n" +
                "<body>\n" +
                "Question:\n" + formattedQuestion +
                "<br>\n" +
                "Options:<br>\n" +
                "<form>\n" +
                "<div>" + x + "</div>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>";
        webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);

        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface           // For API 17+
            public void performClick(String strl) {

                MySqlDatabase ob=new MySqlDatabase(c);


                //Get options ticked.. update it in final answer..
                //Also increment no. of toggles for the question by one..
                String stringVariable = strl;
                int myStr=Integer.parseInt(stringVariable);
                ob.updateValuesForResult(mySi,myQi,MySqlDatabase.FinalAnswerSerialNumber,myStr+"");
                int oi=ob.getOptionIdBySerialNumber(myStr+"");
                ob.updateValuesForResult(mySi,myQi,MySqlDatabase.FinalAnswerId,oi+"");

                String not=ob.getValuesForResult(mySi,myQi,MySqlDatabase.NumberOfToggles);
                int numOfTog=Integer.parseInt(not);
                numOfTog++;
                ob.updateValuesForResult(mySi,myQi,MySqlDatabase.NumberOfToggles,numOfTog+"");

                //Enable buttons..

                obb.enableButtons();

            }
        }, "ok");
    }

    //This function is used to format the content i.e. add <img> tag wherever required..
    public static String format(String str, String examId) {

        examId="changeThisToExamId";

        final String regex = "[ ]?([\\\\]Images[\\\\])?((([\\w])+\\.)(jpg|gif|png))";
        final String subst = "<img src=\""+VariablesDefined.imageUrl+""+examId+"/Images/$2\"/>";

        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(str);

// The substituted value will be contained in the result variable
        String result=matcher.replaceAll(subst);

        return result;
    }

    public static void DownloadImageFromPath(final String path,final String group) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream in = null;
                bmp = null;
                int responseCode = -1;
                try {

                    URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.connect();
                    responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        //download
                        Log.d("hereeeeee", "inOk");
                        in = con.getInputStream();
                        bmp = BitmapFactory.decodeStream(in);
                        try {
                            saveBitmap(bmp,group);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        in.close();
                    } else {
                        Log.d("hereeeeee", "inNotOk");
                    }

                } catch (Exception ex) {
                    Log.e("Exception", ex.toString());
                }
            }
        }).start();
    }


    public static void saveBitmap(Bitmap bmp,String group) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()+"/LiveExams"
                + File.separator + group);
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
    }

}

