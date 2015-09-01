package com.cevaone.illuminate;

import java.io.IOException;


import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

//test
public class MainActivity extends ActionBarActivity implements SurfaceHolder.Callback {

	Intent intent;
	Camera cam;
	Parameters p;
	boolean is_light_on = false;
	boolean camera_obtained;
	
	Button btn_power;
	ImageView locked_img;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	SurfaceHolder mHolder;
	AlertDialog.Builder about_alert;
	Configuration configuration;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        
        btn_power = (Button) findViewById(R.id.light_toggle);
        locked_img = (ImageView) findViewById(R.id.locked_img);
        
       
		surfaceView = (SurfaceView) findViewById(R.id.PREVIEW);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
        
        


        about_alert = new AlertDialog.Builder(this);
        about_alert.setTitle(R.string.about);

    	

    }

	public void light_toggle(View v) {
		if(camera_obtained) {
			if(is_light_on) {
				btn_power.setText(getText(R.string.btn_power_on));
				btn_power.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
				flash_off();
			} else {
				btn_power.setText(getText(R.string.btn_power_off));
				btn_power.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape_off));
				flash_on();
			}
		}

	}
	
	
	
	
	
	private class getCamera extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			if (cam == null) {
				try {
					cam = Camera.open();
					camera_obtained = true;
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				camera_obtained = false;
			}
			Log.d("camera_obtained", Boolean.valueOf(camera_obtained).toString());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			try {
				if((mHolder != null) && (cam != null)) {
					cam.setPreviewDisplay(mHolder);
				} else {
					Toast.makeText(getApplicationContext(), "Camera is busy, please wait or restart device", Toast.LENGTH_LONG).show();
					finish();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			startPreview();
		}
		
	}
	
	public void startPreview() {
		if(cam != null & camera_obtained == true) {
			cam.startPreview();
		}
	}
	

	
	public void flash_on() {
		if(cam != null) {
			p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			cam.setParameters(p);
			is_light_on = true;
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			locked_img.setImageResource(R.drawable.ic_locked);
		}
	}
	
	public void flash_off() {
		if(cam != null) {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			locked_img.setImageResource(R.drawable.ic_unlocked);
			p = cam.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_OFF);
			cam.setParameters(p);
			is_light_on = false;		
		}
	}
	



	
	@Override
	public void onStart() {
		Log.d("state", "onStart()");
		if(supports_torch()) {
			new getCamera().execute();
			//startPreview();
		} else {
			finish();
		}

		super.onStart();
	}
	
	public boolean supports_torch() {
		boolean has_flash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		if(has_flash) {
			return true;
		}
		
		Toast.makeText(getApplicationContext(), "Device doesn't support the flashlight!", Toast.LENGTH_LONG).show();
		return false;
		
		
	}
	
	@Override
	public void onStop() {
		Log.d("state", "onStop()");
		if(cam != null) {
			cam.stopPreview();
			cam.release();
		}

		cam = null;
		btn_power.setText(getText(R.string.btn_power_on));
		btn_power.setBackgroundDrawable(getResources().getDrawable(R.drawable.buttonshape));
		locked_img.setImageResource(R.drawable.ic_unlocked);
		super.onStop();
	}
	
	
	@Override
	public void onDestroy() {
		Log.d("state", "onDestroy()");
		
		if(cam != null) {
			cam.release();
		}
		//mNotifyMgr.cancelAll();
		cam = null;
		super.onDestroy();
	}

	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
        	View view = (View) LayoutInflater.from(getApplicationContext()).inflate(R.layout.about, null);
    		about_alert.setView(view);
    		about_alert.show();
    		return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}
	

	
	
}
