package in.truskills.liveexams.ParticularExamStatistics;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.ArrayList;

import in.truskills.liveexams.SqliteDatabases.QuizDatabase;

/**
 * Created by Shivansh Gupta on 27-01-2017.
 */

public class WebViewContentForAnswers {

    public void contentGenerator(final String question, final ArrayList<String> optionsList,WebView webView,String myAnswer,String correctAnswer) {

        //Get size of options list..
        int optionsListSize = optionsList.size();
        int myAnswerId=Integer.parseInt(myAnswer);
        --myAnswerId;
        int correctanswerId=Integer.parseInt(correctAnswer);
        --correctanswerId;
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
        for (int i = 0; i <optionsListSize; ++i) {
            if(i==correctanswerId){
                x=x+"<div>\n" +
                        "\t<div style=\"padding:10px;background-color:green;height:3px;width:3px;float:left\"></div>\n" +
                        "\t<div style=\"margin-left:20px;float:left\">"+formattedOptions.get(i)+"</div>\n" +
                        "</div>\n" +
                        "<br><br><br><br>";
            }else if(i==myAnswerId){
                if(myAnswerId==correctanswerId){
                    x=x+"<div>\n" +
                            "\t<div style=\"padding:10px;background-color:green;height:3px;width:3px;float:left\"></div>\n" +
                            "\t<div style=\"margin-left:20px;float:left\">"+formattedOptions.get(i)+"</div>\n" +
                            "</div>\n" +
                            "<br><br>";
                }else{
                    x=x+"<div>\n" +
                            "\t<div style=\"padding:10px;background-color:red;height:3px;width:3px;float:left\"></div>\n" +
                            "\t<div style=\"margin-left:20px;float:left\">"+formattedOptions.get(i)+"</div>\n" +
                            "</div>\n" +
                            "<br><br>";
                }
            }else{
                x=x+"<div>\n" +
                        "\t<div style=\"padding:10px;background-color:black;height:3px;width:3px;float:left\"></div>\n" +
                        "\t<div style=\"margin-left:20px;float:left\">"+formattedOptions.get(i)+"</div>\n" +
                        "</div>\n" +
                        "<br><br>";
            }
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

    }

}

