package in.truskills.liveexams.MainScreens;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.ParticularExam.CustomSpinnerForDetailsAdapter;
import in.truskills.liveexams.R;

public class DetailsOfExamsIncludedInKitActivity extends AppCompatActivity {

    //Declare variables..
    TextView start_TimeJoinPage, end_TimeJoinPage, start_DateJoinPage, end_DateJoinPage, descriptionJoinPage, sponsorTextJoinPage;
    Spinner myLanguageJoinPage;
    String selectedLanguage, timestamp, examDetails, examId, Languages, examGiven;
    SharedPreferences prefs;
    Button join_button;
    Handler h;
    HashMap<String, String> mapper;
    Bundle b;
    ViewFlipper viewFlipperJoinPage;
    CustomSpinnerForDetailsAdapter customSpinnerForDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_exams_included_in_kit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        start_DateJoinPage = (TextView) findViewById(R.id.startDateDetailsPage);
        end_DateJoinPage = (TextView) findViewById(R.id.endDateDetailsPage);
        start_TimeJoinPage = (TextView) findViewById(R.id.startTimeDetailsPage);
        end_TimeJoinPage = (TextView) findViewById(R.id.endTimeDetailsPage);
        sponsorTextJoinPage = (TextView) findViewById(R.id.sponsorTextDetailsPage);
        descriptionJoinPage = (TextView) findViewById(R.id.descriptionDetailsPage);
        myLanguageJoinPage = (Spinner) findViewById(R.id.myLanguageDetailsPage);

        viewFlipperJoinPage = (ViewFlipper) findViewById(R.id.viewFlipperDetailsPage);

        int[] resources = {
                R.drawable.first,
                R.drawable.second,
                R.drawable.third,
                R.drawable.fourth,
        };

        for (int i = 0; i < resources.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(resources[i]);
            viewFlipperJoinPage.addView(imageView);
        }

        viewFlipperJoinPage.setInAnimation(this, android.R.anim.fade_in);
        viewFlipperJoinPage.setOutAnimation(this, android.R.anim.fade_out);

        viewFlipperJoinPage.setAutoStart(true);
        viewFlipperJoinPage.setFlipInterval(2000);


        Typeface tff = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        start_DateJoinPage.setTypeface(tff);
        end_DateJoinPage.setTypeface(tff);
        start_TimeJoinPage.setTypeface(tff);
        end_TimeJoinPage.setTypeface(tff);
        descriptionJoinPage.setTypeface(tff);
        sponsorTextJoinPage.setTypeface(tff);

        //Get arguments..
        b = getIntent().getBundleExtra("bundle");
        examDetails = b.getString("examDetails");
        String name=b.getString("name");
        getSupportActionBar().setTitle(name);

        h = new Handler();

        //Parse the exam details..
        try {
            HashMap<String, String> mapper = MiscellaneousParser.join_start_Parser(examDetails);
            descriptionJoinPage.setText(mapper.get("Description"));

            String startDate = mapper.get("StartDate");
            String myStartDate = MiscellaneousParser.parseDate(startDate);
            String endDate = mapper.get("EndDate");
            String myEndDate = MiscellaneousParser.parseDate(endDate);
            String startTime = mapper.get("StartTime");
            String myStartTime = MiscellaneousParser.parseTimeForDetails(startTime);
            String endTime = mapper.get("EndTime");
            String myEndTime = MiscellaneousParser.parseTimeForDetails(endTime);
            Languages = mapper.get("Languages");

            start_DateJoinPage.setText(myStartDate);
            start_TimeJoinPage.setText(myStartTime);
            end_DateJoinPage.setText(myEndDate);
            end_TimeJoinPage.setText(myEndTime);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> listOfLanguages = new ArrayList<>();
        try {
            listOfLanguages = MiscellaneousParser.getLanguagesPerExam(Languages);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        customSpinnerForDetailsAdapter = new CustomSpinnerForDetailsAdapter(this, listOfLanguages);
        myLanguageJoinPage.setAdapter(customSpinnerForDetailsAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
