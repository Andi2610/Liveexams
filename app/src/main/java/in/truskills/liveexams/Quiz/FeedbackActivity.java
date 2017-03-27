package in.truskills.liveexams.Quiz;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.R;

public class FeedbackActivity extends AppCompatActivity {

    RecyclerView feedbackList;
    FeedbackListAdapter feedbackListAdapter;
    Button submit,cancel;
    TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);

        getSupportActionBar().setTitle("FEEDBACK");

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);

        ArrayList<String> list=new ArrayList<>();

        list.add("How was your experience giving the quiz?");
        list.add("How was the content of the quiz?");
        list.add("How was the time allocation for the quiz?");

        submit=(Button)findViewById(R.id.submit);
        cancel=(Button)findViewById(R.id.cancel);
        heading=(TextView) findViewById(R.id.heading);

        Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Bold.ttf");

        heading.setTypeface(tff1);

        feedbackList=(RecyclerView)findViewById(R.id.feedbackList);
        feedbackListAdapter= new FeedbackListAdapter(list, this);
        feedbackList.setLayoutManager(linearLayoutManager);
        feedbackList.setItemAnimator(new DefaultItemAnimator());
        feedbackList.setAdapter(feedbackListAdapter);
        feedbackListAdapter.notifyDataSetChanged();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FeedbackActivity.this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                startActivity(intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(FeedbackActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
    }
}
