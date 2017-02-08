package in.truskills.liveexams.Quiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import in.truskills.liveexams.MainScreens.MainActivity;
import in.truskills.liveexams.R;

public class AllSectionsSummary extends AppCompatActivity {

    LinearLayoutManager linearLayoutManager;
    AllSectionsSummaryAdapter allSectionsSummaryAdapter;
    RecyclerView allSectionsList;
    Bundle b;
    int noOfSections,questionArray[];
    ArrayList<String> sectionName;
    MySqlDatabase ob;
    Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_sections_summary);

        b=getIntent().getBundleExtra("bundle");
        noOfSections=b.getInt("noOfSections");
        questionArray=b.getIntArray("questionArray");

        allSectionsList=(RecyclerView)findViewById(R.id.allSectionsList);
        finishButton=(Button)findViewById(R.id.finishButton);

        ob=new MySqlDatabase(AllSectionsSummary.this);
        sectionName=new ArrayList<>();


        for(int i=0;i<noOfSections;++i){
            HashMap<String,String> map=ob.getValuesPerSection(i);
            String name=map.get("SectionName");
            sectionName.add(name);
        }

        allSectionsSummaryAdapter =new AllSectionsSummaryAdapter(sectionName,questionArray,AllSectionsSummary.this);

        linearLayoutManager=new LinearLayoutManager(this);
        allSectionsList.setLayoutManager(linearLayoutManager);
        allSectionsList.setItemAnimator(new DefaultItemAnimator());
        allSectionsList.setAdapter(allSectionsSummaryAdapter);
        allSectionsSummaryAdapter.notifyDataSetChanged();

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AllSectionsSummary.this);
                builder.setMessage("Are you sure you want to exit the quiz?")
                        .setIcon(R.drawable.alert_icon)
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(AllSectionsSummary.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Removes other Activities from stack
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
}
