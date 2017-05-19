package in.truskills.liveexams.MainScreens;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;

public class KitDetailsActivity extends AppCompatActivity {

    String response,description,startDate,endDate,myDateOfStart,myDateOfEnd,price,boughtProductKit,from,id,pCode;

    Button buy,promo;

    RecyclerView tryForFreeExamsList,examsIncludedInKitList,coursesIncludedInKitList;
    LinearLayoutManager linearLayoutManager,linearLayoutManagerForPaidExams,linearLayoutManagerForCourses;
    LinearLayout discountLayout;

    TryForFreeExamsListAdapter tryForFreeExamsListAdapter;
    ExamsIncludedInKitListAdapter examsIncludedInKitListAdapter;
    CoursesIncludedInKitListAdapter coursesIncludedInKitListAdapter;

    List<Values> valuesList,valuesListForPaidExams,valuesListForCourses;
    Values values;

    ProgressDialog dialog;
    String finalPrice="";

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

    Handler h;

    TextView startDateText,endDateText,startDateValue,endDateValue,descriptionText,descriptionValue,priceText,priceValue;
    TextView tryForFreeText,examsIncludedInKitText,coursesIncludedInKitText,discountOfferedText,discountOfferedValue,priceAfterDiscountText,priceAfterDiscountValue;
    LinearLayout footer,tryForFreeLayout,examsIncludedInKitLayout,coursesIncludedInKitLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kit_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        h=new Handler();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        b=getIntent().getBundleExtra("bundle");
        response=b.getString("response");
        from=b.getString("from");
        String name=b.getString("name");

        getSupportActionBar().setTitle(name);

        Log.d("transfer", "onActivityCreated: "+from+" "+response);

        Log.d("responseInFragment", "onActivityCreated: "+response);

        discountLayout=(LinearLayout)findViewById(R.id.discountLayout);
        discountLayout.setVisibility(View.GONE);
        discountOfferedText=(TextView)findViewById(R.id.discountOfferedText);
        discountOfferedValue=(TextView)findViewById(R.id.discountOfferedValue);
        priceAfterDiscountText=(TextView)findViewById(R.id.priceAfterDiscountText);
        priceAfterDiscountValue=(TextView)findViewById(R.id.priceAfterDiscountValue);
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
        coursesIncludedInKitLayout=(LinearLayout)findViewById(R.id.coursesIncludedInKitLayout);

        tryForFreeExamsList=(RecyclerView)findViewById(R.id.tryForFreeExamsList);
        examsIncludedInKitList=(RecyclerView)findViewById(R.id.examsIncludedInKitList);
        coursesIncludedInKitList=(RecyclerView)findViewById(R.id.coursesIncludedInKitList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManagerForPaidExams = new LinearLayoutManager(this);
        linearLayoutManagerForCourses = new LinearLayoutManager(this);

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
        discountOfferedText.setTypeface(tff1);
        discountOfferedValue.setTypeface(tff1);
        priceAfterDiscountValue.setTypeface(tff1);
        priceAfterDiscountText.setTypeface(tff1);

        try {
            HashMap<String,String> map= MiscellaneousParser.getDetailsOfOneKit(response);
            price=map.get("price");
            finalPrice=price;
            description=map.get("description");
            startDate=map.get("startDate");
            endDate=map.get("endDate");
            id=map.get("id");
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

            if(coursesId.isEmpty()){
                coursesIncludedInKitLayout.setVisibility(View.GONE);
            }else{
                coursesIncludedInKitLayout.setVisibility(View.VISIBLE);
                coursesIncludedInKitListAdapter=new CoursesIncludedInKitListAdapter(coursesName,coursesId,this,from);
                coursesIncludedInKitList.setLayoutManager(linearLayoutManagerForCourses);
                coursesIncludedInKitList.setItemAnimator(new DefaultItemAnimator());
                coursesIncludedInKitList.setAdapter(coursesIncludedInKitListAdapter);
                coursesIncludedInKitListAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer randomNum = randInt(0, 9999999);
                String orderId=randomNum.toString();
                Intent intent = new Intent(KitDetailsActivity.this, WebViewActivity.class);
                intent.putExtra(ConstantsDefined.ACCESS_CODE, ConstantsDefined.accessCode);
                intent.putExtra(ConstantsDefined.MERCHANT_ID, ConstantsDefined.merchantId);
                intent.putExtra(ConstantsDefined.ORDER_ID, orderId);
                Log.d("valBefore", "onClick: "+orderId);
                intent.putExtra(ConstantsDefined.CURRENCY, ConstantsDefined.currency);
                intent.putExtra(ConstantsDefined.AMOUNT, "1");
                intent.putExtra(ConstantsDefined.REDIRECT_URL, ConstantsDefined.redirectUrl);
                intent.putExtra(ConstantsDefined.CANCEL_URL, ConstantsDefined.cancelUrl);
                intent.putExtra(ConstantsDefined.RSA_KEY_URL, ConstantsDefined.rsaUrl);
                intent.putExtra("productKitId",id);
                startActivity(intent);

            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(KitDetailsActivity.this);
                View promptsView = li.inflate(R.layout.alert_label_editor, null);
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(KitDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("PROMO CODE");
                builder.setMessage("Enter your promo code here");
                builder.setView(promptsView);
                final EditText text=(EditText)promptsView.findViewById(R.id.input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        pCode=text.getText().toString();
                        if(pCode.equals("")){
                            Toast.makeText(KitDetailsActivity.this, "Please enter a promo code", Toast.LENGTH_SHORT).show();
                        }else{
                            getPromoCode(pCode);
                        }
                    }
                });
                builder.setNegativeButton("CANCEL",null);
                builder.setCancelable(true);
                builder.show();

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void getPromoCode(String code){

        RequestQueue requestQueue;

        String url=ConstantsDefined.api+"applyPromocode/"+id+"/"+code;
        ConstantsDefined.updateAndroidSecurityProvider(KitDetailsActivity.this);
        ConstantsDefined.beforeVolleyConnect();

        //Initialise requestQueue instance and url to be connected to for Volley connection..
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //While api is being connected to, display apppropriate dialog box..
        dialog = new ProgressDialog(KitDetailsActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Applying your promo code.. Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //Make a request..
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Dismiss dialog box on successful response..
                if (dialog != null)
                    dialog.dismiss();

                Log.d("promoResponse", "onResponse: "+response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String success=jsonObject.getString("success");
                    if(success.equals("true")){

                        JSONObject jsonObject1=jsonObject.getJSONObject("response");
                        String active=jsonObject1.getString("active");
                        if(active.equals("true")){
                            final String percentageDiscount=jsonObject1.getString("percentageDiscount");
                            Log.d("discount", "onResponse: "+percentageDiscount);
                            Toast.makeText(KitDetailsActivity.this, "Discount applied successfully..", Toast.LENGTH_SHORT).show();
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    discountLayout.setVisibility(View.VISIBLE);
                                    discountOfferedValue.setText(percentageDiscount+"%");
                                    double pd=Double.parseDouble(percentageDiscount);
                                    double pr=Double.parseDouble(price);
                                    double finalPr=pr-(pd/100*pr);
                                    priceAfterDiscountValue.setText(finalPr+"");
                                    finalPrice=finalPr+"";
                                }
                            });
                        }else{
                            Toast.makeText(KitDetailsActivity.this, "Sorry! No such promocode exists!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(KitDetailsActivity.this, "Sorry! No such promocode exists!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //In case the connection to the Api couldn't be established..

                //Dismiss the dialog box..
                if (dialog != null)
                    dialog.dismiss();

                //Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
                if(ConstantsDefined.isOnline(KitDetailsActivity.this)){
                    Toast.makeText(KitDetailsActivity.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(KitDetailsActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(stringRequest);
    }

    public static int randInt(int min, int max) {
        // Usually this should be a field rather than a method variable so
        // that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
