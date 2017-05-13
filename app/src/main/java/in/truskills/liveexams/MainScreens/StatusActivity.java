package in.truskills.liveexams.MainScreens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import in.truskills.liveexams.R;

public class StatusActivity extends Activity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);
		
		Intent mainIntent = getIntent();
		TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));
		Toast.makeText(this, "Status:"+mainIntent.getStringExtra("transStatus"), Toast.LENGTH_SHORT).show();
	}
} 