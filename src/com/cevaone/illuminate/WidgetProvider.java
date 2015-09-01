package com.cevaone.illuminate;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.Log;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	public static String TOGGLE_FLASH = "ToggleFlash";
	public static String TOGGLE_ICON = "ToggleIcon";
	static boolean is_light_on;
	static Camera camera;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		Log.d("widget", "onUpdate");
		
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Intent receiver = new Intent(context, WidgetProvider.class);
        receiver.setAction(TOGGLE_FLASH);
        receiver.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setOnClickPendingIntent(R.id.imageButton1, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, views);
		
		
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.d("widget", "onReceive");
		
		if(intent.getAction().equals(TOGGLE_ICON)) {
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			Log.d("widget", "toggleIcon");
			
			if(is_light_on) {
				views.setImageViewResource(R.id.imageButton1, R.drawable.widget_off);
				
			} else {
				views.setImageViewResource(R.id.imageButton1, R.drawable.widget_on);
			}
			is_light_on = !is_light_on;
			Intent receiver = new Intent(context, WidgetProvider.class);
	        receiver.setAction(TOGGLE_FLASH);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, 0);
	        views.setOnClickPendingIntent(R.id.imageButton1, pendingIntent);
			
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		    appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class), views);
		}
		
		
		if(intent.getAction().equals(TOGGLE_FLASH)) {
			
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			
		
			Intent receiver = new Intent(context, WidgetProvider.class);
	        receiver.setAction(TOGGLE_FLASH);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, receiver, 0);
	        views.setOnClickPendingIntent(R.id.imageButton1, pendingIntent);
			
			
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		    appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class), views);
			
		    Intent serviceIntent = new Intent(context, FlashService.class);
		    context.startService(serviceIntent);
			
			
		}
		
	}
	
	
}
