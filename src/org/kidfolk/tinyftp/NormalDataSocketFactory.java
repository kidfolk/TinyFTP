package org.kidfolk.tinyftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class NormalDataSocketFactory extends DataSocketFactory {
	private static final String TAG = "NormalDataSocketFactory";
 
	ServerSocket server;
	 
	InetAddress remoteAddr;
	 
	int remotePort;
	 
	boolean isPasvMode;
	
	public NormalDataSocketFactory() {
		clearState();
	}
	

	@Override
	public boolean onPort(InetAddress dest, int port) {
		this.remoteAddr = dest;
		this.remotePort = port;
		return true;
	}
	
	/**
	 * Return the port number that the remote client should be informed of (in the body
	 * of the PASV response).
	 * @return The port number, or -1 if error.
	 */
	public int getPortNumber() {
		if(server != null) {
			return server.getLocalPort(); // returns -1 if serversocket is unbound 
		} else {
			return -1;
		}
	}

	@Override
	public int onPasv() {
		clearState();
		try {
			//pasv mode 服务器监听一个指定的端口，但是每次重新绑定的时候需要做clearState的动作
			server = new ServerSocket(8889);
			return server.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
			clearState();
			return 0;
		}
	}
	
	private void clearState() {
		/**
		 * Clears the state of this object, as if no pasv() or port() had occurred.
		 * All sockets are closed.
		 */
		if(server != null) {
			try {
				server.close();
			} catch (IOException e) {}
		}
		server = null;
		remoteAddr = null;
		remotePort = 0;
		Log.v(TAG, "NormalDataSocketFactory state cleared");
	}

	@Override
	public Socket onTransfer() {
		if(server == null) {
			// We're in PORT mode (not PASV)
			if(remoteAddr == null || remotePort == 0) {
				Log.v(TAG, "PORT mode but not initialized correctly");
				clearState();
				return null;
			}
			Socket socket;
			try {
				socket = new Socket(remoteAddr, remotePort);
			} catch (IOException e) {
				Log.v(TAG, 
						"Couldn't open PORT data socket to: " +
						remoteAddr.toString() + ":" + remotePort);
				clearState();
				return null;
			}
			
			// Kill the socket if nothing happens for X milliseconds
			try {
				socket.setSoTimeout(GlobleConfig.SO_TIMEOUT_MS);
			} catch (Exception e) {
				Log.v(TAG, "Couldn't set SO_TIMEOUT");
				clearState();
				return null;
			}
			
			return socket;
		} else {
			// We're in PASV mode (not PORT)
			Socket socket = null;
			try {
				socket = server.accept();
				Log.v(TAG, "onTransfer pasv accept successful");
			} catch (Exception e) {
				Log.v(TAG, "Exception accepting PASV socket");
				socket = null;
			}
			clearState();
			return socket;  // will be null if error occurred
		}
	}

	@Override
	public InetAddress getPasvIP() {
		
		return null;
	}

	@Override
	public void reportTraffic(long numBytes) {
		// TODO Auto-generated method stub
		
	}
	 
}
 
