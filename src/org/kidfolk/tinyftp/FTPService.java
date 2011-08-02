package org.kidfolk.tinyftp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class FTPService extends Service implements Runnable {
	private static final String TAG = "FTPService";
	
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
		try {
			serverSocket = new ServerSocket(8888);
//			serverSocket.bind(localAddr);
			Log.v(TAG, serverSocket.getLocalSocketAddress().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			try {
				clientSocket = serverSocket.accept();
				Session session = new Session(clientSocket,new NormalDataSocketFactory());
				Thread sessionThread = new Thread(session);
				sessionThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		super.onDestroy();
		try {
			serverSocket.close();
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
