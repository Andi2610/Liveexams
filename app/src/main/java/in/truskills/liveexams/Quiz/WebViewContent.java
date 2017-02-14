package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

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

    public void contentGenerator(final String question, final ArrayList<String> optionsList, final String examId,final WebView webView,final int mySi,final int myQi,final Context c,final MyFragmentInterface obb) {

        //Get size of options list..
        int optionsListSize = optionsList.size();
        //Design proper format of the question..
        String formattedQuestion = format(question, examId);

        ArrayList<String> formattedOptions = new ArrayList<>();

        for (int i = 0; i < optionsListSize; ++i) {
            //Design proper format of the options..
            String formattedOption = format(optionsList.get(i), examId);
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

                //Enable submit and clear button..
//                o.enableButtons();

                MySqlDatabase ob=new MySqlDatabase(c);

                Log.d("clicked","here");

                //Get options ticked.. update it in final answer..
                //Also increment no. of toggles for the question by one..
                String stringVariable = strl;
                int myStr=Integer.parseInt(stringVariable);
                Log.d("here","mySi="+mySi+" myQi="+myQi);
                ob.updateValuesForResult(mySi,myQi,MySqlDatabase.FinalAnswerSerialNumber,myStr+"");
                int oi=ob.getOptionIdBySerialNumber(myStr+"");
                Log.d("optionId=",oi+"");
                ob.updateValuesForResult(mySi,myQi,MySqlDatabase.FinalAnswerId,oi+"");

                String not=ob.getValuesForResult(mySi,myQi,MySqlDatabase.NumberOfToggles);
                int numOfTog=Integer.parseInt(not);
                numOfTog++;
                ob.updateValuesForResult(mySi,myQi,MySqlDatabase.NumberOfToggles,numOfTog+"");
                ob.getAllValues();

                //Enable buttons..

                obb.enableButtons();


            }
        }, "ok");
    }

    //This function is used to format the content i.e. add <img> tag wherever required..
    public static String format(String str, String examId) {

        examId="changeThisToExamId";
        ArrayList<String> jpgSubStr = new ArrayList<>();
        ArrayList<String> pngSubStr = new ArrayList<>();
        String jpgStartSubStr, pngStartSubStr, s, ss,myS;

        if(str.contains("\\Images\\"))
            myS=str.replace("\\Images\\","");
        else myS=str;
        String myStr = myS;
        String searchableString = myS;


        //Get the image if present at the starting of the sentence..
        //Get jpg image..
        Pattern jpgStartPattern = Pattern.compile("[a-zA-Z0-9_-]*.jpg");
        Matcher jpgStartMatcher = jpgStartPattern.matcher(searchableString);
        if (jpgStartMatcher.find()) {
            int jpgStart = jpgStartMatcher.start();
            int jpgEnd = jpgStartMatcher.end();
            s = searchableString.substring(jpgStart, jpgEnd);
            ss = VariablesDefined.imageUrl + examId + "/" + "Images/" + s;
            jpgStartSubStr = "<img src='" + ss + "'/>";
            myStr = myStr.replaceFirst("[a-zA-Z0-9_-]*.jpg", jpgStartSubStr);
        }
        //Get png image..
        Pattern pngStartPattern = Pattern.compile("[a-zA-Z0-9_-]*.png");
        Matcher pngStartMatcher = pngStartPattern.matcher(searchableString);
        if (pngStartMatcher.find()) {
            int pngStart = pngStartMatcher.start();
            int pngEnd = pngStartMatcher.end();
            s = searchableString.substring(pngStart, pngEnd);
            ss = VariablesDefined.imageUrl + examId + "/" + "Images/" + s;
            pngStartSubStr = "<img src='" + ss + "'/>";
            myStr = myStr.replaceFirst("[a-zA-Z0-9_-]*.png", pngStartSubStr);
        }

        //Get the images if present in the middle of the sentence..
        //Get jpg image..
        Pattern jpgPattern = Pattern.compile("[ ][a-zA-Z0-9_-]*.jpg");
        Matcher jpgMatcher = jpgPattern.matcher(searchableString);
        while (jpgMatcher.find()) {
            int start = jpgMatcher.start();
            start++;
            int end = jpgMatcher.end();
            s = searchableString.substring(start, end);
            ss = VariablesDefined.imageUrl + examId + "/" + "Images/" + s;
            jpgSubStr.add("<img src='" + ss + "'/>");
        }
        //Get png image..
        Pattern pngPattern = Pattern.compile("[ ][a-zA-Z0-9_-]*.png");
        Matcher pngMatcher = pngPattern.matcher(searchableString);
        while (pngMatcher.find()) {
            int start = pngMatcher.start();
            start++;
            int end = pngMatcher.end();
            s = searchableString.substring(start, end);
            ss = VariablesDefined.imageUrl + examId + "/" + s;
            pngSubStr.add("<img src='" + ss + "'/>");
        }

        //Add image tag to jpg images..
        for (int i = 0; i < jpgSubStr.size(); ++i) {
            myStr = myStr.replaceFirst("[ ][a-zA-Z0-9_-]*.jpg", jpgSubStr.get(i));
        }
        //Add image tag to png images..
        for (int i = 0; i < pngSubStr.size(); ++i) {
            myStr = myStr.replaceFirst("[ ][a-zA-Z0-9_-]*.png", pngSubStr.get(i));
        }

        return myStr;
    }

}

