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
                        "\t<span style=\"padding:10px;background-color:green;margin-left:10px\"></span>\n" +
                        "\t<span style=\"margin-left:20px\">"+formattedOptions.get(i)+"</span>\n" +
                        "</div>\n" +
                        "<br>";
            }else if(i==myAnswerId){
                if(myAnswerId==correctanswerId){
                    x=x+"<div>\n" +
                            "\t<span style=\"padding:10px;background-color:green;margin-left:10px\"></span>\n" +
                            "\t<span style=\"margin-left:20px\">"+formattedOptions.get(i)+"</span>\n" +
                            "</div>\n" +
                            "<br>";
                }else{
                    x=x+"<div>\n" +
                            "\t<span style=\"padding:10px;background-color:red;margin-left:10px\"></span>\n" +
                            "\t<span style=\"margin-left:20px\">"+formattedOptions.get(i)+"</span>\n" +
                            "</div>\n" +
                            "<br>";
                }
            }else{
                x=x+"<div>\n" +
                        "\t<span style=\"padding:10px;background-color:black;margin-left:10px\"></span>\n" +
                        "\t<span style=\"margin-left:20px\">"+formattedOptions.get(i)+"</span>\n" +
                        "</div>\n" +
                        "<br>";
            }
        }

        //Generate the html content..
        String content =
                "<html>\n" +
                        "<body>\n" +
                        "Question:\n<br>" + formattedQuestion +
                        "<br><br>\n" +
                        "Options:<br>\n" +
                        "<form>\n" +
                        "<div>" + x + "</div>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>";
        webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);

    }

}

