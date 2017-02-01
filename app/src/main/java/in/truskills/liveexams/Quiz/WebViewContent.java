package in.truskills.liveexams.Quiz;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shivansh Gupta on 27-01-2017.
 */

public class WebViewContent {

    public static String contentGenerator(final String question, final ArrayList<String> optionsList){

        int optionsListSize=optionsList.size();

        String formattedQuestion=format(question);
        ArrayList<String> formattedOptions=new ArrayList<>();
        for(int i=0;i<optionsListSize;++i){
            String formattedOption=format(optionsList.get(i));
            formattedOptions.add(formattedOption);
        }
        String x="";
        for(int i=0;i<optionsListSize;++i){
            x=x+"<input type=\"radio\" id=\""+i+"\" name=\"options\" value=\""+i+"\" onclick=\"ok.performClick(this.value);\" ><label for=\"value\">"+formattedOptions.get(i)+"</label></input><br>";
        }

        String content=
                "<html>\n" +
                "<body>\n" +
                "Question:\n" +formattedQuestion+
                "<br>\n" +
                "Options:<br>\n" +
                "<form>\n" +
                "<div>"+x+"</div>\n" +
                "</form>\n" +
                "</body>\n" +
                "</html>;";

        return content;

    }

    public static String format(String str){
        ArrayList<String> jpgSubStr=new ArrayList<>();
        ArrayList<String> pngSubStr=new ArrayList<>();
        String searchableString = str;
        String myStr=str;
        String jpgStartSubStr,pngStartSubStr,s;

        Pattern jpgStartPattern=Pattern.compile("[a-zA-Z0-9_-]*.jpg");
        Matcher jpgStartMatcher=jpgStartPattern.matcher(searchableString);
        if(jpgStartMatcher.find()){
            int jpgStart=jpgStartMatcher.start();
            jpgStart++;
            int jpgEnd=jpgStartMatcher.end();
            s=searchableString.substring(jpgStart,jpgEnd);
            jpgStartSubStr="<img src='"+s+"'>";
            myStr=myStr.replaceFirst("[a-zA-Z0-9_-]*.jpg",jpgStartSubStr);
        }

        Pattern pngStartPattern=Pattern.compile("[a-zA-Z0-9_-]*.png");
        Matcher pngStartMatcher=pngStartPattern.matcher(searchableString);
        if(pngStartMatcher.find()){
            int pngStart=pngStartMatcher.start();
            pngStart++;
            int pngEnd=pngStartMatcher.end();
            s=searchableString.substring(pngStart,pngEnd);
            pngStartSubStr="<img src='"+s+"'>";
            myStr=myStr.replaceFirst("[a-zA-Z0-9_-]*.png",pngStartSubStr);
        }

        Pattern jpgPattern=Pattern.compile("[ ][a-zA-Z0-9_-]*.jpg");
        Matcher jpgMatcher=jpgPattern.matcher(searchableString);
        while(jpgMatcher.find()){
            int start=jpgMatcher.start();
            start++;
            int end=jpgMatcher.end();
            s=searchableString.substring(start,end);
            jpgSubStr.add("<img src='"+s+"'>");
        }
        Pattern pngPattern=Pattern.compile("[ ][a-zA-Z0-9_-]*.png");
        Matcher pngMatcher=pngPattern.matcher(searchableString);
        while(pngMatcher.find()){
            int start=pngMatcher.start();
            start++;
            int end=pngMatcher.end();
            s=searchableString.substring(start,end);
            pngSubStr.add("<img src='"+s+"'>");
        }

        for(int i=0;i<jpgSubStr.size();++i){
            myStr=myStr.replaceFirst("[ ][a-zA-Z0-9_-]*.jpg",jpgSubStr.get(i));
        }

        for(int i=0;i<pngSubStr.size();++i){
            myStr=myStr.replaceFirst("[ ][a-zA-Z0-9_-]*.png",pngSubStr.get(i));
        }

        Log.d("result","Final:"+myStr);

        return myStr;
    }

}
