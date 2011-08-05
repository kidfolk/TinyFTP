package org.kidfolk.tinyftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class FTPService extends Service implements Runnable {
	private static final String TAG = "FTPService";
	private boolean isRunning = false;
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	class FTPServiceBinder extends Binder{
		public FTPService getFTPService(){
			return FTPService.this;
		}
	}
	
	public void startFTPService(){
		Log.v(TAG, "startFTPService");
		Thread serviceThread = new Thread(this);
		serviceThread.start();
	}

	@Override
	public void run() {
		isRunning = true;
		try {
			serverSocket = new ServerSocket(8888);
			Log.v(TAG, getWIFIAddress()+":8888");
		} catch (IOException e) {
			e.printStackTrace();
		}
		while(true){
			if(isRunning){
				try {
					clientSocket = serverSocket.accept();
					Session session = new Session(clientSocket,new NormalDataSocketFactory());
					Thread sessionThread = new Thread(session);
					sessionThread.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return new FTPServiceBinder();
	}
	
	@Override
	public void onDestroy() {
		Log.v(TAG, "FTPService destroyed!");
		isRunning = false;
		super.onDestroy();
		try {
			serverSocket.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public InetAddress getWIFIAddress(){
		WifiManager wifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
		int ip = wifiManager.getConnectionInfo().getIpAddress();
		return Util.intToInet(ip);
	}

}
