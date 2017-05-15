package in.truskills.liveexams.MainScreens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import org.json.JSONObject;

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
	TextView noInternetMessageForKit;
	Button retryButtonForKit;
	LinearLayout noInternetLayoutForKit;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);

		prefs=getSharedPreferences("prefs", Context.MODE_PRIVATE);

		retryButtonForKit=(Button)findViewById(R.id.retryButtonForKit);
		noInternetMessageForKit=(TextView) findViewById(R.id.noInternetMessage);
		noInternetLayoutForKit=(LinearLayout) findViewById(R.id.noInternetLayoutForKit);

		noInternetLayoutForKit.setVisibility(View.GONE);

		Typeface tff1 = Typeface.createFromAsset(getAssets(), "fonts/Comfortaa-Regular.ttf");
		noInternetMessageForKit.setTypeface(tff1);
		retryButtonForKit.setTypeface(tff1);

		mainIntent = getIntent();
		TextView tv4 = (TextView) findViewById(R.id.message);
		tv4.setText(mainIntent.getStringExtra("transStatus"));
		tv4.setTypeface(tff1);
		Toast.makeText(this, "Status:"+mainIntent.getStringExtra("transStatus"), Toast.LENGTH_SHORT).show();

		boolean add=mainIntent.getBooleanExtra("add",false);

		if(add){
			addFunc();
		}

		retryButtonForKit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addFunc();
			}
		});
	}

	public void addFunc(){

		noInternetLayoutForKit.setVisibility(View.GONE);
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

				noInternetLayoutForKit.setVisibility(View.GONE);

				//Dismiss dialog box on successful response..
				if (dialog != null)
					dialog.dismiss();

				Log.d("purchaseResponse", "onResponse: "+response);
				Toast.makeText(StatusActivity.this, "response"+response, Toast.LENGTH_SHORT).show();

				try {
					JSONObject jsonObject=new JSONObject(response);
					String success=jsonObject.getString("success");
					if(success.equals("true")){
						Toast.makeText(StatusActivity.this, "Success", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(StatusActivity.this, "Something went wrong..", Toast.LENGTH_LONG).show();
						noInternetLayoutForKit.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				//In case the connection to the Api couldn't be established..

				noInternetLayoutForKit.setVisibility(View.VISIBLE);

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
				params.put("timePeriod","4");
				return params;
			}
		};
		requestQueue.add(stringRequest);
	}
} 