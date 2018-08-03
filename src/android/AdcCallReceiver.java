package io.adc.adccallreceiver;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.telephony.TelephonyManager;

import org.json.JSONException;
import org.json.JSONArray;



public class AdcCallReceiver extends CordovaPlugin {

    

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        

        new CallReceiver().setCallbackContext(callbackContext);

        return true;
    }
 static class CallReceiver extends BroadcastReceiver {

    private CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

	@Override
	public void onReceive(Context context, Intent intent) {
        if (callbackContext == null) return;
        String msg = "";
        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            msg="Call started...";
        }
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)){

            msg="Call ended...";
        }
        else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)){

            msg="Incoming call...";
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, msg);
        result.setKeepCallback(true);

        callbackContext.sendPluginResult(result);
    }

        
    
}
}


