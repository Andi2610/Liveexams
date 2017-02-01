package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.truskills.liveexams.Miscellaneous.VariablesDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.Signup_Login;

public class QuizMainActivity extends AppCompatActivity {

    MyPageAdapter pageAdapter;
    ArrayList<String> options;
    String Question;
    RequestQueue requestQueue;
    String url;
    String examId,name;
    TextView sectionName,submittedQuestions,reviewedQuestions,notAttemptedQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        examId=getIntent().getStringExtra("examId");
        name=getIntent().getStringExtra("name");

        sectionName=(TextView)findViewById(R.id.sectionName);
        submittedQuestions=(TextView)findViewById(R.id.submittedQuestions);
        reviewedQuestions=(TextView)findViewById(R.id.reviewedQuestions);
        notAttemptedQuestions=(TextView)findViewById(R.id.notAttemptedQuestions);

        sectionName.setText("PHYSICS");
        submittedQuestions.setText("0");
        reviewedQuestions.setText("0");
        notAttemptedQuestions.setText("30");

        getSupportActionBar().setTitle(name);

        url=VariablesDefined.api+"questionPaper/"+examId;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("questionPaper",response.toString()+"");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response", error.getMessage());
                Toast.makeText(QuizMainActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(stringRequest);

        List<Fragment> fragments = getFragments();
        pageAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);
        ViewPager pager =
                (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        for(int i=0;i<100;++i){
            options=new ArrayList<>();
            options.add("op1");
            options.add("op2");
            fList.add(MyFragment.newInstance("Q1",options));
        }
        return fList;
    }
    class MyPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        public MyPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }
        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }
}
