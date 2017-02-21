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

    public void contentGenerator(final String question, final ArrayList<String> optionsList, final WebView webView,final int mySi,final int myQi,final Context c,final MyFragmentInterface obb) {

        //Get size of options list..
        int optionsListSize = optionsList.size();
        //Design proper format of the question..
        String formattedQuestion="";
        ArrayList<String> formattedOptions= new ArrayList<>();
        formattedQuestion=question;
        Log.d("text",formattedQuestion);
        for (int i = 0; i < optionsListSize; ++i) {
            //Design proper format of the options..
            String formattedOption = optionsList.get(i);
            Log.d("text",formattedOption);
            formattedOptions.add(formattedOption);
        }

        String x = "";

        //Dynamic radio buttons added depending upon the options list size..
        for (int i = 0; i < optionsListSize; ++i) {
            x = x + "<input type=\"radio\" id=\"" + i + "\" name=\"options\" value=\"" + i + "\" onclick=\"ok.performClick(this.value);\" ><label for=\""+i+"\">" + formattedOptions.get(i) + "</label></input><br>";
        }

        //Generate the html content..
        String content =
                "<html>\n" +
                "<head>"+
                        "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"\n" +
                        "        integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\n" +
                        "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css\"\n" +
                        "        integrity=\"sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp\" crossorigin=\"anonymous\">\n" +
                        "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>\n" +
                        "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"\n" +
                        "          integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\"\n" +
                        "          crossorigin=\"anonymous\"></script>"+
                "</head>"+
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

}

