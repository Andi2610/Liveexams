package in.truskills.liveexams.MainScreens;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.R;

public class KitDetailsActivity extends AppCompatActivity {

    String response,description,startDate,endDate,myDateOfStart,myDateOfEnd,price,boughtProductKit,from;

    Button buy,promo;

    RecyclerView tryForFreeExamsList,examsIncludedInKitList,coursesIncludedInKitList;
    LinearLayoutManager linearLayoutManager,linearLayoutManagerForPaidExams;

    TryForFreeExamsListAdapter tryForFreeExamsListAdapter;
    ExamsIncludedInKitListAdapter examsIncludedInKitListAdapter;

    List<Values> valuesList,valuesListForPaidExams,valuesListForCourses;
    Values values;

    ArrayList<String> examsPaidName=new ArrayList<>();
    ArrayList<String> examsFreeName=new ArrayList<>();
    ArrayList<String> examsPaidId=new ArrayList<>();
    ArrayList<String> examsFreeId=new ArrayList<>();
    ArrayList<String> coursesName=new ArrayList<>();
    ArrayList<String> coursesId=new ArrayList<>();
    ArrayList<String> examsPaidStartDate=new ArrayList<>();
    ArrayList<String> examsFreeStartDate=new ArrayList<>();
    ArrayList<String> examsPaidEndDate=new ArrayList<>();
    ArrayList<String> examsFreeEndDate=new ArrayList<>();
    ArrayList<String> examsFreeExamDuration=new ArrayList<>();
    ArrayList<String> examsPaidExamDuration=new ArrayList<>();

    Bundle b=new Bundle();

    TextView startDateText,endDateText,startDateValue,endDateValue,descriptionText,descriptionValue,priceText,priceValue;
    TextView tryForFreeText,examsIncludedInKitText,coursesIncludedInKitText;
    LinearLayout footer,tryForFreeLayout,examsIncludedInKitLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        b=getIntent().getBundleExtra("bundle");
        response=b.getString("response");
        from=b.getString("from");
        String name=b.getString("name");

        getSupportActionBar().setTitle(name);

        Log.d("transfer", "onActivityCreated: "+from+" "+response);

        Log.d("responseInFragment", "onActivityCreated: "+response);

        buy=(Button)findViewById(R.id.buy);
        promo=(Button)findViewById(R.id.promo);
        startDateText=(TextView) findViewById(R.id.startDateText);
        endDateText=(TextView) findViewById(R.id.endDateText);
        startDateValue=(TextView) findViewById(R.id.startDateValue);
        endDateValue=(TextView) findViewById(R.id.endDateValue);
        descriptionText=(TextView) findViewById(R.id.descriptionText);
        descriptionValue=(TextView) findViewById(R.id.descriptionValue);
        priceText=(TextView) findViewById(R.id.priceText);
        priceValue=(TextView) findViewById(R.id.priceValue);
        tryForFreeText=(TextView) findViewById(R.id.tryForFreeText);
        examsIncludedInKitText=(TextView) findViewById(R.id.examsIncludedInKitText);
        coursesIncludedInKitText=(TextView) findViewById(R.id.coursesIncludedInKitText);

        footer=(LinearLayout)findViewById(R.id.footerForKit);
        tryForFreeLayout=(LinearLayout)findViewById(R.id.tryForFreeLayout);
        examsIncludedInKitLayout=(LinearLayout)findViewById(R.id.examsIncludedInKitLayout);

        tryForFreeExamsList=(RecyclerView)findViewById(R.id.tryForFreeExamsList);
        examsIncludedInKitList=(RecyclerView)findViewById(R.id.examsIncludedInKitList);
        coursesIncludedInKitList=(RecyclerView)findViewById(R.id.coursesIncludedInKitList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManagerForPaidExams = new LinearLayoutManager(this);


        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
        startDateText.setTypeface(tff1);
        endDateText.setTypeface(tff1);
        startDateValue.setTypeface(tff1);
        endDateValue.setTypeface(tff1);
        descriptionText.setTypeface(tff1);
        descriptionValue.setTypeface(tff1);
        priceText.setTypeface(tff1);
        priceValue.setTypeface(tff1);
        tryForFreeText.setTypeface(tff1);
        examsIncludedInKitText.setTypeface(tff1);
        coursesIncludedInKitText.setTypeface(tff1);
        buy.setTypeface(tff1);
        promo.setTypeface(tff1);

        try {
            HashMap<String,String> map= MiscellaneousParser.getDetailsOfOneKit(response);
            price=map.get("price");
            description=map.get("description");
            startDate=map.get("startDate");
            endDate=map.get("endDate");
            boughtProductKit=map.get("boughtProductKit");
            myDateOfStart=MiscellaneousParser.parseDateForKit(startDate);
            myDateOfEnd=MiscellaneousParser.parseDateForKit(endDate);

            HashMap<String,ArrayList<String>> mapper=MiscellaneousParser.getExamsAndCoursesOfOneKit(response);
            examsPaidId=mapper.get("examsPaidId");
            examsPaidName=mapper.get("examsPaidName");
            examsFreeId=mapper.get("examsFreeId");
            examsFreeName=mapper.get("examsFreeName");
            coursesName=mapper.get("coursesName");
            coursesId=mapper.get("coursesId");
            examsPaidStartDate=mapper.get("examsPaidStartDate");
            examsFreeStartDate=mapper.get("examsFreeStartDate");
            examsPaidEndDate=mapper.get("examsPaidEndDate");
            examsFreeEndDate=mapper.get("examsFreeEndDate");
            examsFreeExamDuration=mapper.get("examsFreeExamDuration");
            examsPaidExamDuration=mapper.get("examsPaidExamDuration");

            startDateValue.setText(myDateOfStart);
            endDateValue.setText(myDateOfEnd);
            descriptionValue.setText(description);
            priceValue.setText(price);

            if(boughtProductKit.equals("false")){
                footer.setVisibility(View.VISIBLE);
            }else{
                footer.setVisibility(View.GONE);
            }

            valuesList=new ArrayList<>();
            valuesListForPaidExams=new ArrayList<>();

            for(int i=0;i<examsFreeId.size();++i){

                values=new Values(examsFreeName.get(i),examsFreeStartDate.get(i),examsFreeEndDate.get(i),examsFreeExamDuration.get(i),examsFreeId.get(i));
                valuesList.add(values);
            }
            if(valuesList.isEmpty()){
                tryForFreeLayout.setVisibility(View.GONE);
            }else{
                tryForFreeLayout.setVisibility(View.VISIBLE);
                tryForFreeExamsListAdapter=new TryForFreeExamsListAdapter(valuesList,this,from,response);
                tryForFreeExamsList.setLayoutManager(linearLayoutManager);
                tryForFreeExamsList.setItemAnimator(new DefaultItemAnimator());
                tryForFreeExamsList.setAdapter(tryForFreeExamsListAdapter);
                tryForFreeExamsListAdapter.notifyDataSetChanged();
            }

            for(int i=0;i<examsPaidId.size();++i){

                values=new Values(examsPaidName.get(i),examsPaidStartDate.get(i),examsPaidEndDate.get(i),examsPaidExamDuration.get(i),examsPaidId.get(i));
                valuesListForPaidExams.add(values);

            }
            if(valuesListForPaidExams.isEmpty()){
                examsIncludedInKitLayout.setVisibility(View.GONE);
            }else{
                examsIncludedInKitLayout.setVisibility(View.VISIBLE);
                examsIncludedInKitListAdapter=new ExamsIncludedInKitListAdapter(valuesListForPaidExams,this,from,response);
                examsIncludedInKitList.setLayoutManager(linearLayoutManagerForPaidExams);
                examsIncludedInKitList.setItemAnimator(new DefaultItemAnimator());
                examsIncludedInKitList.setAdapter(examsIncludedInKitListAdapter);
                examsIncludedInKitListAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(KitDetailsActivity.this, InitialScreenActivity.class);
                startActivity(i);
            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
