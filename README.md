Cordova AdcCallReceiver
=======================

It is an Apache Cordova plugin (COMPATIBLE WITH IONIC) to simplify handling phone call status and events in Android devices.


## Install
	cordova plugin add io.adc.plugin.callreceiver

It is also possible to install via repo url directly :
	cordova plugin add https://github.com/ahmedb49/io.adc.plugin.callreceiver
    
******

## Quick Example (IONIC3 example)

	platform.ready().then(() => {
		..... 
		this.initMyPlugin();
		.....
	});
	initMyPlugin(){
		if ((<any>window).AdcCallReceiver){
			(<any>window).AdcCallReceiver.onCall(function (state: string) {
	  
				   if (state=="RINGING"){ //INCOMING CALL
				   .......
				   }
				   else if (state=="OFFHOOK"){ //CALL STARTED
				   .......
				   }
				   else if (state=="IDLE"){ //CALL ENDED
				   .......
				   }
				  
			});
		}
		else {
		  console.log("plugin not found");
		}
	}	
    
*********

## Supported platforms

- ONLY Android

*********

## References
	Cordova PhoneCall Trap : worked with PhoneStateListener
	https://github.com/renanoliveira/cordova-phone-call-trap

	AdcCallReceiver worked with BroadcastReceiver
********


## License

MIT
********