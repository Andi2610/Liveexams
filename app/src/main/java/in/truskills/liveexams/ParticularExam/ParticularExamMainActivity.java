package in.truskills.liveexams.ParticularExam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import in.truskills.liveexams.R;

//This is the main activity for a particular exam in which start and join fragment are loaded..

public class ParticularExamMainActivity extends AppCompatActivity implements StartPageInterface, JoinPageInterface {

    //Declare variables..
    String name, enrolled;
    Bundle b;
    FragmentManager manager;
    FragmentTransaction trans;
    String from;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_exam_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);

        //Get intent variables..
        b = getIntent().getBundleExtra("bundle");
        name = b.getString("name");
        enrolled = b.getString("enrolled");
        from=getIntent().getStringExtra("from");

        getSupportActionBar().setTitle(name);

        manager = getSupportFragmentManager();
        trans = manager.beginTransaction();

        //Check if the user is enrolled is in the exam or not..
        if (enrolled.equals("true")) {
            //If enrolled.. directly load Start fragment..
            StartPageFragment fragment = new StartPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "StartPageFragment");
            trans.commit();
        } else {
            //Else if unenrolled.. load Join fragment..
            JoinPageFragment fragment = new JoinPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "JoinPageFragment");
            trans.commit();
        }
    }

    //This funnction is executed whenever a new fragment is loaded when join fragment is present initially..
    @Override
    public void changeFragmentFromJoinPage(Fragment f, String title) {
        manager = getSupportFragmentManager();
        trans = manager.beginTransaction();
        //Check for the title which is to be set on the loading of the new fragment..
        if (title.equals("name")) {
            //Load Start fragment..
            trans.replace(R.id.fragment, f, "StartPageFragment");
            getSupportActionBar().setTitle(name);
        } else {
            //Load Rules fragment..
            trans.replace(R.id.fragment, f, "RulesFromJoin");
            getSupportActionBar().setTitle(title);
        }
        trans.commit();
    }

    //This funnction is executed whenever a new fragment is loaded when start fragment is present initially..
    @Override
    public void changeFragmentFromStartPage(Fragment f, String title) {
        manager = getSupportFragmentManager();
        trans = manager.beginTransaction();
        //Check for the title which is to be set on the loading of the new fragment..
        if (title.equals("name")) {
            //Load Join fragment..
            trans.replace(R.id.fragment, f, "JoinPageFragment");
            getSupportActionBar().setTitle(name);
        } else {
            //Load Rules fragment..
            trans.replace(R.id.fragment, f, "RulesFragment");
            getSupportActionBar().setTitle(title);
        }
        trans.commit();
    }

    //When start fragment is loaded.. change the title of the app bar to the particular exam name..
    @Override
    public void changeTitleForStartPage() {
        getSupportActionBar().setTitle(name);
    }

    //When join fragment is loaded.. change the title of the app bar to the particular exam name..
    @Override
    public void changeTitleForJoinPage() {
        getSupportActionBar().setTitle(name);
    }

    @Override
    public void onBackPressed() {
        manager = getSupportFragmentManager();
        trans = manager.beginTransaction();
        RulesFragment f = (RulesFragment) manager.findFragmentByTag("RulesFragment");
        RulesFragment ff=(RulesFragment)manager.findFragmentByTag("RulesFromJoin");
        if(f!=null&&f.isVisible()){
            StartPageFragment fragment = new StartPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "StartPageFragment");
            trans.commit();
            getSupportActionBar().setTitle(name);
        }else if(ff!=null&&ff.isVisible()){
            JoinPageFragment fragment = new JoinPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "JoinPageFragment");
            trans.commit();
            getSupportActionBar().setTitle(name);
        }else{
            if(from.equals("home")){
                setResult(10,null);
            }
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        manager = getSupportFragmentManager();
        trans = manager.beginTransaction();
        RulesFragment f = (RulesFragment) manager.findFragmentByTag("RulesFragment");
        RulesFragment ff=(RulesFragment)manager.findFragmentByTag("RulesFromJoin");
        if(f!=null&&f.isVisible()){
            StartPageFragment fragment = new StartPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "StartPageFragment");
            trans.commit();
            getSupportActionBar().setTitle(name);
        }else if(ff!=null&&ff.isVisible()){
            JoinPageFragment fragment = new JoinPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "JoinPageFragment");
            trans.commit();
            getSupportActionBar().setTitle(name);
        }else{
            Log.d("visible","notRules");
            finish();
        }
        return true;
    }
}
