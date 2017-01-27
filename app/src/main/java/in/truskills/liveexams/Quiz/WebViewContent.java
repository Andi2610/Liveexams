package in.truskills.liveexams.Quiz;

import java.util.ArrayList;

/**
 * Created by Shivansh Gupta on 27-01-2017.
 */

public class WebViewContent {

    public String contentGenerator(final String question, final ArrayList<String> optionsList,final ArrayList<String> imagesList){

        int optionsListSize=optionsList.size();
        int imagesListSize=imagesList.size();
        int size=5;
        String content="<html>\n" +
                "                <body onload=\"addInput('options')\">\n" +
                "                Question:\n" +
                "                question<br>\n" +
                "                Options:<br>\n" +
                "                <form>\n" +
                "                <div id=\"options\"></div>\n" +
                "                </form>\n" +
                "                <script>\n" +
                "                       document.getElementById('options').innerHTML =\"abc\";\n" +
                "\t\t\t\t\t\tvar counter=0; var limit="+size+"; \n" +
                "                        function addInput(divName){ \n" +
                "\t\t\t\t\t\t\tconsole.log(counter+\" \"+limit);\n" +
                "\t\t\t\t\t\t\t   while(counter<limit){\n" +
                "\t\t\t\t\t\t\t\tvar newdiv = document.createElement(\"div\"); \n" +
                "\t\t\t\t\t\t\t\tnewdiv.innerHTML = \"<input type='radio' onclick=\\\"ok.performClick(this.value);\\\" id='\"+counter+\"' value='\"+counter+\"'><label for='\"+counter+\"'> text <img src='https://www.cleverfiles.com/howto/wp-content/uploads/2016/08/mini.jpg' height=20 width=20/></label></input>\"\n" +
                "\t\t\t\t\t\t\t\tdocument.getElementById(divName).appendChild(newdiv); \n" +
                "\t\t\t\t\t\t\t\tcounter++; \n" +
                "\t\t\t\t\t\t\t} \n" +
                "                        }; \n" +
                "\t\t\t\t\t\n" +
                "                </script>\n" +
                "                end\n" +
                "                </body>\n" +
                "                </html>;";


        return content;

    }

}
