package in.truskills.liveexams.MainScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import in.truskills.liveexams.JsonParsers.MiscellaneousParser;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;
import in.truskills.liveexams.authentication.ForgotPassword;

public class StatusActivity extends Activity {

	ProgressDialog dialog;
	SharedPreferences prefs;
	Intent mainIntent;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);

		prefs=getSharedPreferences("prefs", Context.MODE_PRIVATE);
		
		mainIntent = getIntent();
		TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));
		Toast.makeText(this, "Status:"+mainIntent.getStringExtra("transStatus"), Toast.LENGTH_SHORT).show();

		boolean add=mainIntent.getBooleanExtra("add",false);

		if(add){

			//Required for https connection..
			ConstantsDefined.updateAndroidSecurityProvider(StatusActivity.this);
			ConstantsDefined.beforeVolleyConnect();

			//Initialise requestQueue instance and url to be connected to for Volley connection..
			RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
			String url = ConstantsDefined.apiForKit + "purchaseProductKit";

			//While api is being connected to, display apppropriate dialog box..
			dialog = new ProgressDialog(StatusActivity.this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("Adding this kit in your database of purchased kit..Please wait..");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();

			//Make a request..
			StringRequest stringRequest = new StringRequest(Request.Method.POST,
					url, new Response.Listener<String>() {
				@Override
				public void onResponse(String response) {

					//Dismiss dialog box on successful response..
					if (dialog != null)
						dialog.dismiss();

					Log.d("purchaseResponse", "onResponse: "+response);
					Toast.makeText(StatusActivity.this, "response"+response, Toast.LENGTH_SHORT).show();

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					//In case the connection to the Api couldn't be established..

					//Dismiss the dialog box..
					if (dialog != null)
						dialog.dismiss();

					//Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
					if(ConstantsDefined.isOnline(StatusActivity.this)){
						Toast.makeText(StatusActivity.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(StatusActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
					}
				}
			}) {
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {

					//Put all the required parameters for the post request..
					Map<String, String> params = new HashMap<String, String>();
					params.put("userId",prefs.getString("userId",""));
					params.put("amount",prefs.getString("userId",""));
					params.put("productKitId",mainIntent.getStringExtra("productKitId"));
					return params;
				}
			};
			requestQueue.add(stringRequest);

		}
	}
} 