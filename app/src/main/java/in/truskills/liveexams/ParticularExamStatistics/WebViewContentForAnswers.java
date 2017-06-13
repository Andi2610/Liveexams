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

    public void contentGenerator(final String question, final ArrayList<String> optionsList, WebView webView, String myAnswer, String correctAnswer,String myUrl,final Updateable c) {

        //Get size of options list..
        int optionsListSize = optionsList.size();
        int myAnswerId = Integer.parseInt(myAnswer);
        --myAnswerId;
        int correctanswerId = Integer.parseInt(correctAnswer);
        --correctanswerId;
        //Design proper format of the question..
        String formattedQuestion = "";
        ArrayList<String> formattedOptions = new ArrayList<>();
        formattedQuestion = question;
        Log.d("text", formattedQuestion);
        for (int i = 0; i < optionsListSize; ++i) {
            //Design proper format of the options..
            String formattedOption = optionsList.get(i);
            Log.e("text", formattedOption);
            formattedOptions.add(formattedOption);
        }

        String x = "";

        //Dynamic radio buttons added depending upon the options list size..
        for (int i = 0; i < optionsListSize; ++i) {
            if (i == correctanswerId) {

                if(myAnswerId==correctanswerId){
                    x = x + "<div>\n" +
//                        "\t<span><img src=\"https://nlsblogdotorg.files.wordpress.com/2011/09/approve.png\" height=30 width=30/></span>\n" +
                            "\t<p><img src=\"file:///android_asset/right_answer_icon.png\" height=20 width=20 align=\"middle\"/>\n" +
                             formattedOptions.get(i).substring(3,formattedOptions.get(i).length()-4) + "</p>\n" +
                            "</div>\n" +
                            "<br>";
                }else{
                    x = x + "<div>\n" +
//                        "\t<span><img src=\"https://nlsblogdotorg.files.wordpress.com/2011/09/approve.png\" height=30 width=30/></span>\n" +
                            "\t<p><img src=\"file:///android_asset/right_answer_icon.png\" height=30 width=30 align=\"middle\"/>\n" +
                            formattedOptions.get(i).substring(3,formattedOptions.get(i).length()-4) + "\n" +
                            "<img src=\"file:///android_asset/explanation_icon.png\" height=30 width=30  align=\"middle\" style=\"float:right;\" onclick=\"ok.performClick(this.value);\"/></p>" +
                            "</div>\n" +
                            "<br>";
                }
            } else if (i == myAnswerId) {
                if (myAnswerId == correctanswerId) {
                    x = x + "<div>\n" +
//                            "\t<span><img src=\"https://nlsblogdotorg.files.wordpress.com/2011/09/approve.png\" height=30 width=30/></span>\n" +
                            "\t<p><img src=\"file:///android_asset/right_answer_icon.png\" height=30 width=30 align=\"middle\"/>\n" +
                            formattedOptions.get(i).substring(3,formattedOptions.get(i).length()-4) + "</p>\n" +
                            "</div>\n" +
                            "<br>";
                } else {
                    x = x + "<div>\n" +
//                            "\t<span><img src=\"https://uploads.wishloop.com/uploads/img_f4325850c9cbe4473e780daa08bffa4b3656b8f6.png\" height=30 width=30/></span>\n" +
                            "\t<p><img src=\"file:///android_asset/wrong_answer_icon.png\" height=30 width=30 align=\"middle\"/>\n" +
                            formattedOptions.get(i).substring(3,formattedOptions.get(i).length()-4) + "</p>\n" +
                            "</div>\n" +
                            "<br>";
                }
            } else {
                x = x + "<div>\n" +
//                        "\t<span><img src=\"https://data.unhcr.org/horn-of-africa/images/circle_grey.png\" height=30 width=30/></span>\n" +
                        "\t<p><img src=\"file:///android_asset/no_answer_icon.png\" height=30 width=30 align=\"middle\"/>\n" +
                        formattedOptions.get(i).substring(3,formattedOptions.get(i).length()-4) + "</p>\n" +
                        "</div>\n" +
                        "<br>";
            }
        }

        //Generate the html content..
        String content =
                "<html>\n" +
                        "<body>\n" +
                        "Question:\n<br>" + formattedQuestion +
                        "<br>\n" +
                        "Options:<br>\n" +
                        "<form>\n" +
                        "<div>" + x + "</div>\n" +
                        "</form>\n" +
                        "</body>\n" +
                        "</html>";
        Log.e("HTML",content);
        webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface           // For API 17+
            public void performClick(String strl) {

                c.update();
            }
        }, "ok");
    }
//style="padding:10px;background-color:green;margin-left:10px"
}

