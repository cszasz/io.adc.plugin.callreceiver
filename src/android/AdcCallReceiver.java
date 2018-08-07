package io.adc.adccallreceiver;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONArray;

import org.apache.cordova.CordovaWebView;

public class AdcCallReceiver extends CordovaPlugin {


	CallReceiver callReceiver;
    public  CallbackContext callbackContext;
    public static  CallbackContext cbc;
  
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
     

        if (callbackContext!=null){
            Context context=this.cordova.getActivity().getApplicationContext(); 
            SharedPreferences prefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(callbackContext);
            prefsEditor.putString("callbackContext", json);
            prefsEditor.commit();
        }

		if (callReceiver==null){
			callReceiver= new CallReceiver();
            callReceiver.setCallbackContext(callbackContext);
            AdcCallReceiver.cbc=callbackContext;
            
		}
        
        return true;
    }

    

    public static class CallReceiver extends BroadcastReceiver {

	
		private CallbackContext callbackContext;

		public void setCallbackContext(CallbackContext callbackContext) {
			this.callbackContext = callbackContext;
		}
        
        @Override
        public void onReceive(Context context, Intent intent) {
           
        
            
             String msg="";
            if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                msg="Call started...";
                sendResult(msg);
                 
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){
              
              
                msg="Call ended...";
				sendResult(msg);
            }
            else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){
                
             
             
                msg="Incoming call..."; 
                sendResult("Incoming call...");
            }
            
        }
		public void sendResult(String msg){
			PluginResult result = new PluginResult(PluginResult.Status.OK, msg);
			result.setKeepCallback(true);

            if (AdcCallReceiver.cbc!=null){
			AdcCallReceiver.cbc.sendPluginResult(result);
            }
            else {
                Gson gson = new Gson();
                String json = prefs.getString("callbackContext", "");
                CallbackContext callbackContext = gson.fromJson(json, CallbackContext.class);
                if (callbackContext!=null){
                    callbackContext.sendPluginResult(result);
                }
                
            }
            
		}

    }   
   

}


