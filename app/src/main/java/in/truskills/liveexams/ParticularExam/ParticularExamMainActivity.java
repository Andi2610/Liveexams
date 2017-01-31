package in.truskills.liveexams.ParticularExam;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import in.truskills.liveexams.R;

public class ParticularExamMainActivity extends AppCompatActivity implements StartPageInterface,JoinPageInterface{

    String name,enrolled;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_exam_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        b=getIntent().getBundleExtra("bundle");
        name=b.getString("name");
        Log.d("response",name+"");
        enrolled=b.getString("enrolled");

        getSupportActionBar().setTitle(name);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();

        if(enrolled.equals("true")){
            Log.d("response","Start");
            StartPageFragment fragment = new StartPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "StartPageFragment");
            trans.commit();
        }else{
            Log.d("response","Join");
            JoinPageFragment fragment = new JoinPageFragment();
            fragment.setArguments(b);
            trans.replace(R.id.fragment, fragment, "JoinPageFragment");
            trans.commit();
        }
    }

    @Override
    public void changeFragmentFromJoinPage(Fragment f,String title) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction trans=manager.beginTransaction();
        if(title.equals("name")){
            trans.replace(R.id.fragment,f,"StartPageFragment");
            getSupportActionBar().setTitle(name);
        }
        else{
            trans.replace(R.id.fragment,f,"RulesFromJoin");
            trans.addToBackStack(null);
            getSupportActionBar().setTitle(title);
        }
        trans.commit();
    }

    @Override
    public void changeTitleForJoinPage() {
        getSupportActionBar().setTitle(name);

    }

    @Override
    public void changeFragmentFromStartPage(Fragment f,String title) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction trans=manager.beginTransaction();
        if(title.equals("name")){
            trans.replace(R.id.fragment,f,"JoinPageFragment");
            getSupportActionBar().setTitle(name);
        }else{
            trans.replace(R.id.fragment,f,"RulesFragment");
            trans.addToBackStack(null);
            getSupportActionBar().setTitle(title);
        }
        trans.commit();
    }

    @Override
    public void changeTitleForStartPage() {
            getSupportActionBar().setTitle(name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
