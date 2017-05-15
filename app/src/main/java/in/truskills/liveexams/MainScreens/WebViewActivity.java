package in.truskills.liveexams.MainScreens;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.utility.AvenuesParams;
import in.truskills.liveexams.utility.Constants;
import in.truskills.liveexams.utility.RSAUtility;
import in.truskills.liveexams.utility.ServiceHandler;
import in.truskills.liveexams.utility.ServiceUtility;

import in.truskills.liveexams.R;

public class WebViewActivity extends Activity {
	private ProgressDialog dialog;
	Intent mainIntent;
	String html, encVal="",myResponse="";
	Handler h;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();
		h=new Handler();
		makeRequest();
	}

	private void makeRequest() {

		RequestQueue requestQueue;

		dialog = new ProgressDialog(WebViewActivity.this);
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
		dialog.show();

			String url=mainIntent.getStringExtra(AvenuesParams.RSA_KEY_URL);

			ConstantsDefined.beforeVolleyConnect();

			//Initialise requestQueue instance and url to be connected to for Volley connection..
			requestQueue = Volley.newRequestQueue(getApplicationContext());

			//Make a request..
			StringRequest stringRequest = new StringRequest(Request.Method.POST,
					url, new Response.Listener<String>() {
				@Override
				public void onResponse(final String response) {

					//Dismiss dialog box on successful response..

					if (dialog.isShowing())
						dialog.dismiss();

					Log.d("webViewResponse", "onResponse: " + response);
					myResponse=response;

					h.post(new Runnable() {
						@Override
						public void run() {
							if(myResponse.equals("")){
								Toast.makeText(WebViewActivity.this, "An error occured..\nPlease try again later..", Toast.LENGTH_SHORT).show();
								finish();
							}else{
								StringBuffer vEncVal = new StringBuffer("");
								vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
								vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));
								encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), myResponse);

								class MyJavaScriptInterface
								{
									@JavascriptInterface
									public void processHTML(String html)
									{
										// process the html as needed by the app

										Log.d("myHtml", "processHTML: "+html);
										String status = null;
										boolean add=false;
										if(html.indexOf("Failure")!=-1){
											status = "Transaction Declined!";
										}else if(html.indexOf("Success")!=-1){
											status = "Transaction Successful!";
											add=true;
										}else if(html.indexOf("Aborted")!=-1){
											status = "Transaction Cancelled!";
										}else{
											status = "Status Not Known!";
										}
										//Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(getApplicationContext(),StatusActivity.class);
										intent.putExtra("transStatus", status);
										intent.putExtra("add",add);
										intent.putExtra("amount",mainIntent.getStringExtra(AvenuesParams.AMOUNT));
										intent.putExtra("productKitId",mainIntent.getStringExtra("productKitId"));
										startActivity(intent);
									}
								}

								final WebView webview = (WebView) findViewById(R.id.webview);
								webview.getSettings().setJavaScriptEnabled(true);
								webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
								webview.setWebViewClient(new WebViewClient(){
									@Override
									public void onPageFinished(WebView view, String url) {
										super.onPageFinished(webview, url);
										if(url.indexOf("/ccavResponseHandler.jsp")!=-1){
											webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
										}
									}

									@Override
									public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
										Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
									}
								});

			/* An instance of this class will be registered as a JavaScript interface */
								StringBuffer params = new StringBuffer();
								params.append(ServiceUtility.addToPostParams(AvenuesParams.ACCESS_CODE,mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE)));
								params.append(ServiceUtility.addToPostParams(AvenuesParams.MERCHANT_ID,mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID)));
								params.append(ServiceUtility.addToPostParams(AvenuesParams.ORDER_ID,mainIntent.getStringExtra(AvenuesParams.ORDER_ID)));
								params.append(ServiceUtility.addToPostParams(AvenuesParams.REDIRECT_URL,mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL)));
								params.append(ServiceUtility.addToPostParams(AvenuesParams.CANCEL_URL,mainIntent.getStringExtra(AvenuesParams.CANCEL_URL)));
								try {
									Log.d("valBefore", "onPostExecute: ");
									params.append(ServiceUtility.addToPostParams(AvenuesParams.ENC_VAL,URLEncoder.encode(encVal,"UTF-8")));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
									Log.d("vallll", "onPostExecute: "+e.toString());
								}

								String vPostParams = params.substring(0,params.length()-1);
								try {
									webview.postUrl(Constants.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
								} catch (Exception e) {
									Toast.makeText(WebViewActivity.this, "Exception occured while opening webview.", Toast.LENGTH_SHORT).show();
								}
							}
						}
					});
				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					if (dialog.isShowing())
						dialog.dismiss();

					//In case the connection to the Api couldn't be established..

					Log.d("webViewResponse", "onResponse: "+error);

					//Dismiss the dialog box..
					if (dialog != null)
						dialog.dismiss();

					//Display appropriate toast message depending upon internet connectivity was a reason for failure or something else..
					if(ConstantsDefined.isOnline(WebViewActivity.this)){
						Toast.makeText(WebViewActivity.this, "Couldn't connect..Please try again..", Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(WebViewActivity.this, "Sorry! No internet connection", Toast.LENGTH_SHORT).show();
					}
				}
			}){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {

					//Put all the required parameters for the post request..
					Map<String, String> params = new HashMap<String, String>();
					params.put(AvenuesParams.ORDER_ID,mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
					return params;
				}
			};
			requestQueue.add(stringRequest);

	}
} 