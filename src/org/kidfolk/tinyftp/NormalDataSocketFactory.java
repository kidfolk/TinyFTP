package org.kidfolk.tinyftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class NormalDataSocketFactory extends DataSocketFactory {
 
	ServerSocket server;
	 
	InetAddress remoteAddr;
	 
	int remotePort;
	 
	boolean isPasvMode;
	
 int port = 8889;

	@Override
	public boolean onPort(InetAddress dest, int port) {
		this.remoteAddr = dest;
		this.remotePort = port;
		return true;
	}

	@Override
	public int onPasv() {
		try {
			//listen a free port
//			server = new ServerSocket(port++);
			server = new ServerSocket(0);
			return server.getLocalPort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Socket onTransfer() {
		Socket socket = null;
		try {
			socket = server.accept();
		} catch (Exception e) {
			//TODO
			e.printStackTrace();
		}
		return socket;
	}

	@Override
	public InetAddress getPasvIP() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reportTraffic(long numBytes) {
		// TODO Auto-generated method stub
		
	}
	 
}
 
