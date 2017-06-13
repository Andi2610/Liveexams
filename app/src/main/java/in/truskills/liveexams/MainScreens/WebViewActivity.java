package in.truskills.liveexams.MainScreens;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.util.EncodingUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
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
import javax.crypto.Cipher;
import in.truskills.liveexams.Miscellaneous.ConstantsDefined;
import in.truskills.liveexams.R;


public class WebViewActivity extends Activity {
	private ProgressDialog dialog;
	Intent mainIntent;
	String encVal="",myResponse="";
	Handler h;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_webview);
		mainIntent = getIntent();
		Log.e("Verification",mainIntent.getStringExtra(ConstantsDefined.AMOUNT));
		h=new Handler();
		makeRequest();
	}

	private void makeRequest() {

		RequestQueue requestQueue;

		dialog = new ProgressDialog(WebViewActivity.this);
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
		dialog.show();

			String url=mainIntent.getStringExtra(ConstantsDefined.RSA_KEY_URL);

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

					Log.e("webViewResponse", "onResponse: " + response);
					myResponse=response;

					h.post(new Runnable() {
						@Override
						public void run() {
							if(myResponse.equals("")){
								Toast.makeText(WebViewActivity.this, "An error occured..\nPlease try again later..", Toast.LENGTH_SHORT).show();
								finish();
							}else{
								StringBuffer vEncVal = new StringBuffer("");
								vEncVal.append(addToPostParams(ConstantsDefined.AMOUNT, mainIntent.getStringExtra(ConstantsDefined.AMOUNT)));
								vEncVal.append(addToPostParams(ConstantsDefined.CURRENCY, mainIntent.getStringExtra(ConstantsDefined.CURRENCY)));
								encVal = encrypt(vEncVal.substring(0, vEncVal.length() - 1), myResponse);

								class MyJavaScriptInterface
								{
									@JavascriptInterface
									public void processHTML(String html)
									{
										// process the html as needed by the app

										Log.d("myHtml", "processHTML: "+html);
										String status = null;
										boolean add=false;
										if(html.contains("Failure")){
											status = "Transaction Declined!";
										}else if(html.contains("Success")){
											status = "Transaction Successful!";
											add=true;
										}else if(html.contains("Aborted")){
											status = "Transaction Cancelled!";
										}else{
											status = "Status Not Known!";
										}
										//Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
										Intent intent = new Intent(getApplicationContext(),StatusActivity.class);
										intent.putExtra("transStatus", status);
										intent.putExtra("add",add);
										intent.putExtra("amount",mainIntent.getStringExtra(ConstantsDefined.AMOUNT));
										intent.putExtra("productKitId",mainIntent.getStringExtra("productKitId"));
										intent.putExtra("orderId",mainIntent.getStringExtra(ConstantsDefined.ORDER_ID));
										startActivity(intent);
									}
								}

								final WebView webview = (WebView) findViewById(R.id.webview);
								webview.getSettings().setJavaScriptEnabled(true);
								webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
								webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
								webview.setWebViewClient(new WebViewClient(){
									@Override
									public void onPageFinished(WebView view, String url) {
										super.onPageFinished(webview, url);

										if(url.indexOf("/ccavResponseHandler.php")!=-1){
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
								params.append(addToPostParams(ConstantsDefined.ACCESS_CODE,mainIntent.getStringExtra(ConstantsDefined.ACCESS_CODE)));
								params.append(addToPostParams(ConstantsDefined.MERCHANT_ID,mainIntent.getStringExtra(ConstantsDefined.MERCHANT_ID)));
								params.append(addToPostParams(ConstantsDefined.ORDER_ID,mainIntent.getStringExtra(ConstantsDefined.ORDER_ID)));
								params.append(addToPostParams(ConstantsDefined.REDIRECT_URL,mainIntent.getStringExtra(ConstantsDefined.REDIRECT_URL)));
								params.append(addToPostParams(ConstantsDefined.CANCEL_URL,mainIntent.getStringExtra(ConstantsDefined.CANCEL_URL)));
								try {
									Log.d("valBefore", "onPostExecute: "+encVal+" "+mainIntent.getStringExtra(ConstantsDefined.ORDER_ID));
									params.append(addToPostParams(ConstantsDefined.ENC_VAL,URLEncoder.encode(encVal,"UTF-8")));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
									Log.d("vallll", "onPostExecute: "+e.toString());
								}

								String vPostParams = params.substring(0,params.length()-1);
								try {
									webview.postUrl(ConstantsDefined.TRANS_URL, EncodingUtils.getBytes(vPostParams, "UTF-8"));
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
					finish();
				}
			}){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {

					//Put all the required parameters for the post request..
					Map<String, String> params = new HashMap<String, String>();
					params.put(ConstantsDefined.ORDER_ID,mainIntent.getStringExtra(ConstantsDefined.ORDER_ID));
					return params;
				}
			};
			requestQueue.add(stringRequest);

	}

	public String encrypt(String plainText, String key){
		try{
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(key, Base64.DEFAULT)));
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return Base64.encodeToString(cipher.doFinal(plainText.getBytes("UTF-8")),Base64.DEFAULT);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String addToPostParams(String paramKey, String paramValue){
		if(paramValue!=null)
			return paramKey.concat(ConstantsDefined.PARAMETER_EQUALS).concat(paramValue)
					.concat(ConstantsDefined.PARAMETER_SEP);
		return "";
	}


} 