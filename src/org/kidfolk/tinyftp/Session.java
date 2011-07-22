package org.kidfolk.tinyftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import android.util.Log;

public class Session implements Runnable {
	private Socket clientSocket;//client socket
	private Socket dataSocket;//socket for dataTransfer
	private static final String TAG = "Session";
	

	public Session(Socket clientSocket) {
		super();
		this.clientSocket = clientSocket;
	}



	@Override
	public void run() {
		while(true){
			Log.v(TAG, "ready for cmd");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String cmdLine = br.readLine();
				if(null!=cmdLine){
					Log.v(TAG, cmdLine);
					FTPCmd.dispatchCmd(this,cmdLine);
				}else{
					Log.v(TAG, "get cmdLine:"+cmdLine);
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}



	public void writeString(String errString) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			bw.write(errString);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(null!=bw){
					bw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
