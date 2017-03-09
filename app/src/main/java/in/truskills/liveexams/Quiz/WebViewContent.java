package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;

import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

/**
 * Created by Shivansh Gupta on 27-01-2017.
 */

public class WebViewContent {

    public void contentGenerator(final String question, final ArrayList<String> optionsList, final WebView webView, final int mySi, final int myQi, final Context c, final MyFragmentInterface obb, final int myFragmentCount) {

        //Get size of options list..
        int optionsListSize = optionsList.size();
        //Design proper format of the question..
        String formattedQuestion = "";
        ArrayList<String> formattedOptions = new ArrayList<>();
        formattedQuestion = question;
        Log.d("text", formattedQuestion);
        for (int i = 0; i < optionsListSize; ++i) {
            //Design proper format of the options..
            String formattedOption = optionsList.get(i);
            Log.d("text", formattedOption);
            formattedOptions.add(formattedOption);
        }

        String x = "";

        //Dynamic radio buttons added depending upon the options list size..
        for (int i = 0; i < optionsListSize; ++i) {
            x = x + "<input type=\"radio\" id=\"" + i + "\" name=\"options\" value=\"" + i + "\" onclick=\"ok.performClick(this.value);\" style=\"margin:10px\" ><label for=\"" + i + "\">" + formattedOptions.get(i) + "</label></input><br>";
        }

        //Generate the html content..
        String content =
                "<html>\n" +
                        "<head>" +
                        "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\"\n" +
                        "        integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\n" +
                        "  <link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css\"\n" +
                        "        integrity=\"sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp\" crossorigin=\"anonymous\">\n" +
                        "  <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>\n" +
                        "  <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"\n" +
                        "          integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\"\n" +
                        "          crossorigin=\"anonymous\"></script>" +
                        "</head>" +
                        "<body>\n" +
                        "Question:\n <br> " + formattedQuestion +
                        "<br><br>\n" +
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

                QuizDatabase ob = new QuizDatabase(c);


                //Get options ticked..
                String stringVariable = strl;
                int myStr = Integer.parseInt(stringVariable);
                //update it in temp answer..
                ob.updateValuesForResult(mySi, myQi, QuizDatabase.TempAnswerSerialNumber, myStr + "");
                String temp = ob.getValuesForResult(mySi, myQi, QuizDatabase.TempAnswerSerialNumber);
                Log.d("myData", "tempStr=" + temp);

                //Also increment no. of toggles for the question by one..
                String not = ob.getValuesForResult(mySi, myQi, QuizDatabase.NumberOfToggles);
                int numOfTog = Integer.parseInt(not);
                numOfTog++;
                ob.updateValuesForResult(mySi, myQi, QuizDatabase.NumberOfToggles, numOfTog + "");
                //Put status of question as not answered.. i.e. in red..
                //Enable buttons..
                obb.enableButtons();
                int status = Integer.parseInt(ob.getValuesForResult(mySi, myQi, QuizDatabase.QuestionStatus));
                obb.putDetailsForNotAnswered(mySi, myQi, myFragmentCount);

            }
        }, "ok");
    }

}

