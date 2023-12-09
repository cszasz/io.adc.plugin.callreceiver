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
import android.os.Bundle;
import android.telephony.TelephonyManager;


import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONArray;


public class AdcCallReceiver extends CordovaPlugin {


	
    public static  CallbackContext cbc;
  
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        AdcCallReceiver.cbc=callbackContext;
        return true;
    }


    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
           ActivityCompat.requestPermissions(cordova.getActivity(),
                        new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG},
                        1);

    }

    public static class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           
            String msg="";
			if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
                msg="RINGING"; 
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


            
            
        }

		public void sendResult(String msg, Intent intent){
            if (intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER)!=null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, "{ state: \"" + msg + "\", number:\"" + intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER) + "\"}");
                result.setKeepCallback(true);

                if (AdcCallReceiver.cbc != null) {
                    AdcCallReceiver.cbc.sendPluginResult(result);
                }
            }
            
        } 
            
            
	}

}   
   



