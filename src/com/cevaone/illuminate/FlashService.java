package com.cevaone.illuminate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class FlashService extends Service {

	private boolean is_light_on;
	private Camera camera;
	Context context;
	PackageManager pm;
	NotificationManager mNotifyMgr;
	NotificationCompat.Builder mBuilder;
	private int notification_id = 1234;
	
	@Override
	public void onCreate() {
		context = getApplicationContext();
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		pm = context.getPackageManager();
		
		Intent toggleIntent = new Intent(this, WidgetProvider.class);
		toggleIntent.setAction(WidgetProvider.TOGGLE_ICON);
		
		
		
		if(!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(getApplicationContext(), "Device doesn't have a camera!", Toast.LENGTH_SHORT).show();
			return 0;
		}
		
		if (is_light_on) {
       	 
        	if (camera != null) {
        		Log.d("flashlight", "light on - stopping");    
        		camera.stopPreview();
                    camera.release();
                    camera = null;
                    is_light_on = false;
                    sendBroadcast(toggleIntent);
                    stopForeground(true);
                    stopSelf();
            }

        } else {
        	startForeground(notification_id, getNotificiation());
        	Log.d("flashlight", "light off - starting");  
        	try {
        		Log.d("flashlight", "trying to open camera"); 
        		camera = Camera.open();
			} catch (Exception e) {
				Log.d("flashlight", "failed opening");
				e.printStackTrace();
				camera.release();
				camera = null;
			}
            if(camera != null) {
            	Log.d("flashlight", "camera was obtained");
                Log.d("widget broadcast", "tried to start camera");
                        Parameters param = camera.getParameters();
                        param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(param);
                        try {
                        	camera.setPreviewTexture(new SurfaceTexture(0));
                                camera.startPreview();
                                Log.d("flashlight", "camera was started");
                                is_light_on = true;
                                sendBroadcast(toggleIntent);
                                
                        } catch (Exception e) {
                        	Log.d("flashlight", "couldn't start  preview");
                        }
                }
        }
		
		
		
		
		
		
		
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	
	public Notification getNotificiation() {
		int requestID = (int) System.currentTimeMillis();
		
		mBuilder = new NotificationCompat.Builder(FlashService.this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Flashlight")
		.setContentText("Turn off flashlight")
		.setOngoing(true);
		
		Intent resultIntent = new Intent(this, FlashService.class);
		PendingIntent resultPendingIntent = PendingIntent.getService(FlashService.this,  requestID,  resultIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		
		return mBuilder.build();
		
	}
	
	
	
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
}
