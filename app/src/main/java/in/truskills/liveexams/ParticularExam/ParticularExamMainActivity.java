package in.truskills.liveexams.ParticularExam;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import in.truskills.liveexams.MainScreens.Home;
import in.truskills.liveexams.R;

public class ParticularExamMainActivity extends AppCompatActivity implements StartPageInterface,JoinPageInterface{

    boolean joined;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_particular_exam_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name=getIntent().getStringExtra("name");
        joined=getIntent().getBooleanExtra("joined",false);

        getSupportActionBar().setTitle(name);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();

        if(joined){
            StartPage fragment = new StartPage();
            trans.replace(R.id.fragment, fragment, "StartPage");
            trans.commit();
        }else{
            JoinPage fragment = new JoinPage();
            trans.replace(R.id.fragment, fragment, "JoinPage");
            trans.commit();
        }
    }

    @Override
    public void changeFragmentFromJoinPage(Fragment f,String title) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction trans=manager.beginTransaction();
        if(title.equals("name")){
            trans.replace(R.id.fragment,f,"StartPage");
            getSupportActionBar().setTitle(name);
        }
        else{
            trans.replace(R.id.fragment,f,"RulesFromJoin");
            getSupportActionBar().setTitle(title);
        }
        trans.commit();
    }

    @Override
    public void changeFragmentFromStartPage(Fragment f,String title) {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction trans=manager.beginTransaction();
        trans.replace(R.id.fragment,f,"Rules");
        getSupportActionBar().setTitle(title);
        trans.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        StartPage f=(StartPage) manager.findFragmentByTag("StartPage");
        JoinPage ff=(JoinPage) manager.findFragmentByTag("JoinPage");
        Rules fff=(Rules) manager.findFragmentByTag("RulesFromJoin");
        if(f!=null && f.isVisible())
            finish();
        else if(ff!=null && ff.isVisible())
            finish();
        else if(fff!=null && fff.isVisible()){
            JoinPage fragment = new JoinPage();
            trans.replace(R.id.fragment, fragment, "JoinPage");
            trans.commit();
            getSupportActionBar().setTitle(name);
        }
        else {
            StartPage fragment = new StartPage();
            trans.replace(R.id.fragment, fragment, "StartPage");
            trans.commit();
            getSupportActionBar().setTitle(name);
        }
    }
}
