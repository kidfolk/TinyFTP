package org.kidfolk.tinyftp;

import org.kidfolk.tinyftp.FTPService.FTPServiceBinder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

public class TinyFTPActivity extends Activity {
	private static final String TAG = "TinyFTPActivity";
	private Button startButton;
	private Button stopButton;
	private boolean isBound = false;
	private ServiceConnection conn = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		startButton = (Button) findViewById(R.id.start);
		stopButton = (Button) findViewById(R.id.stop);
		ButtonListener listener = new ButtonListener();
		startButton.setOnClickListener(listener);
		stopButton.setOnClickListener(listener);

		conn = new FTPServiceConnection();
	}

	class ButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.start:
				TinyFTPActivity.this.bindService(new Intent(
						TinyFTPActivity.this, FTPService.class), conn,
						BIND_AUTO_CREATE);
				break;
			case R.id.stop:
				if(isBound){
					TinyFTPActivity.this.unbindService(conn);
				}
				break;
			}

		}

	}

	class FTPServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			isBound = true;
			FTPServiceBinder binder = (FTPServiceBinder) service;
			binder.getFTPService().startFTPService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
		}

	}
	
	
	
}