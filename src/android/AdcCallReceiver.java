package io.adc.adccallreceiver;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;


import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import online.eatwithme.restaurant.MainActivity;


public class AdcCallReceiver extends CordovaPlugin {

    public static String cookie;

    public static String url;
	
    public static  CallbackContext cbc;

    public static CordovaWebView webView2;

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        AdcCallReceiver.cbc=callbackContext;
        return true;
    }


    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        ActivityCompat.requestPermissions(cordova.getActivity(),
                new String[]{Manifest.permission.READ_PHONE_STATE},
                1);
        ActivityCompat.requestPermissions(cordova.getActivity(),
                new String[]{Manifest.permission.READ_CALL_LOG},
                1);

        webView2 = webView;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch(Exception ex) {

                }

                cordova.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String js = "(function() { return localStorage.getItem('server')+'/eatwithme.server/adminService/'+localStorage.getItem('restaurantSelected')+'/call/{msg}/{number}?instance='+localStorage.getItem('instance'); })();";
                        String js2 = "(function() { return localStorage.getItem('server'); })();";
                        try {

                            webView.getEngine().evaluateJavascript(
                                    js2,
                                    new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            value = value.replaceAll("^\"|\"$", "");
                                            cookie = webView.getCookieManager().getCookie(value+"/eatwithme.server/");
                                        }
                                    });

                            webView.getEngine().evaluateJavascript(
                                    js,
                                    new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            System.out.println("RRRRR: " + value);
                                            url = value.replaceAll("^\"|\"$", "");;
                                        }
                                    });


                        } catch (Exception ex) {
                            System.out.println("RRRRR: " + ex);
                        }
                    }
                });
            }
        }).start();


        /*

        try {

            AdcCallReceiver.webView2.getEngine().evaluateJavascript(
                    "(function() { return 'aaa'; })();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            System.out.println("RRRRR: " + value);
                        }
                    });


            AdcCallReceiver.webView2.getEngine().evaluateJavascript(
                    "(function() { return localStorage.getItem('sessionId'); })();",
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            System.out.println("RRRT: " + value);
                        }
                    });
        } catch (Exception ex) {
            System.err.println("RRR: "+ex);

        }*/
    }

    public static class HttpRequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];
            try {
                System.out.println("RRR: "+urlString);
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                List<String> entry = new ArrayList<>();

                urlConnection.setRequestProperty("Cookie", cookie);
                try {
                    // Set request method to GET
                    urlConnection.setRequestMethod("GET");

                    return ""+urlConnection.getResponseCode();

                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                System.err.println("RRR: HTTP Request Error "+ e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle the result here
            if (result != null) {
                System.out.println("RRR:HTTP Response "+ result);
                // Process the result
            } else {
                System.err.println("RRR:HTTP Response Empty result");
            }
        }
    }

    public static class CallReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {

            String msg = "";
            if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
                msg="ringing";
                //sendResult(msg, intent);
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                msg = "end";
                //sendResult(msg, intent);
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                msg="IDLE";
                //sendResult(msg, intent);
                return;
            } else {
                //sendResult("x", intent);
                return;
            }
            if (intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER)!=null) {
                System.out.println("RRR "+msg);
                System.out.println("RRR "+intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER));

                new HttpRequestTask().execute(url.replace("{msg}",msg).replace("{number}", URLEncoder.encode(intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER))));

            }
        /*
            String msg="";
			if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
                msg="RINGING";
                System.out.println("RRR "+msg);
                System.out.println("RRR "+intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER));
                sendResult(msg, intent);
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                msg="OFFHOOK";
                sendResult(msg, intent);
                 
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                msg="IDLE";
				sendResult(msg, intent);
            } else {
                sendResult("x", intent);
            }
*/

            
            
        }

		public void sendResult(String msg, Intent intent){
            if (intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER)!=null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "{ \"state\": \"" + msg + "\", \"number\":\"" + intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER) + "\"}");
                result.setKeepCallback(true);

                if (AdcCallReceiver.cbc != null) {
                    AdcCallReceiver.cbc.sendPluginResult(result);
                }
            } 
            
        } 
            
            
	}

}   
   




