package io.adc.adccallreceiver;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


import org.json.JSONException;
import org.json.JSONArray;


public class AdcCallReceiver extends CordovaPlugin {


	
    public static  CallbackContext cbc;
  
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        AdcCallReceiver.cbc=callbackContext;
        return true;
    }
       

    public static class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
           
            String msg="";
			if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
                msg="RINGING"; 
                sendResult(msg);
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                msg="OFFHOOK";
                sendResult(msg);
                 
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
                msg="IDLE";
				sendResult(msg);
            }
            
            
        }

		public void sendResult(String msg){
			PluginResult result = new PluginResult(PluginResult.Status.OK, msg);
			result.setKeepCallback(true);

            if (AdcCallReceiver.cbc!=null){
			AdcCallReceiver.cbc.sendPluginResult(result);
            }
            
        } 
            
            
	}

}   
   




