package org.kidfolk.tinyftp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class Session implements Runnable {
	private Socket cmdSocket;// 命令通道
	protected Socket dataSocket = null;
	protected DataSocketFactory dataSocketFactory;// 数据通道
	protected OutputStream dataOutputStream;
	protected File currentDir = new File(GlobleConfig.rootdir);//当前目录，随着用户的操作而改变
	protected boolean isBinaryMode = false;
	protected String encoding = "UTF-8";
	private static final String TAG = "Session";

	public Session(Socket cmdSocket, DataSocketFactory dataSocketFactory) {
		super();
		this.cmdSocket = cmdSocket;
		this.dataSocketFactory = dataSocketFactory;
	}

	@Override
	public void run() {
		Log.v(TAG, "session start!");
		sendReplyString("220 SwiFTP ready\r\n");
		// sendReplyString("220 written by kidfolk\r\n");
		// 读取客户端发过来的命令，并将命令分发到具体的命令类中处理
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					cmdSocket.getInputStream()));
			while (true) {
				String cmdLine;
				cmdLine = br.readLine();
				if (null != cmdLine) {
					// 分发命令
					FTPCmd.dispatchCmd(this, cmdLine);
					Log.v(TAG, "session dispatchCmd!");
				} else {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
	}

	/**
	 * send reply message to client
	 * 
	 * @param str
	 */
	public void sendReplyString(String str) {
		byte[] bytes = str.getBytes();
		sendReplyByte(bytes);
	}
	
	public void sendReplyByte(byte[] bytes) {
		try {
			// TODO: do we really want to do all of this on each write? Why?
			BufferedOutputStream out = new BufferedOutputStream(cmdSocket
					.getOutputStream());
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			Log.v(TAG, "Exception writing socket");
			closeCmdSocket();
			return;
		}
	}

	/**
	 * server listen a free port for data transfer
	 * 
	 * @return port number
	 */
	public int onEPSV() {
		return dataSocketFactory.onPasv();
	}

	/**
	 * 
	 * @return
	 */
	public int onPASV() {
		return dataSocketFactory.onPasv();
	}

	/**
	 * get the dataSocket IP address the IP is the same as cmdSocket
	 * 
	 * @return
	 */
	public InetAddress getDataSocketPasvIP() {
		return cmdSocket.getLocalAddress();
	}

	/**
	 * Sends a string over the already-established data socket
	 * 
	 * @param string
	 * @return Whether the send completed successfully
	 */
	public boolean sendViaDataSocket(String string) {
		try {
			byte[] bytes = string.getBytes(encoding);
			Log.v(TAG,"Using data connection encoding: " + encoding);
			return sendViaDataSocket(bytes, bytes.length);
		} catch (UnsupportedEncodingException e) {
			Log.v(TAG, "Unsupported encoding for data socket send");
			return false;
		}
	}

	public boolean sendViaDataSocket(byte[] bytes, int len) {
		return sendViaDataSocket(bytes, 0, len);
	}

	/**
	 * Sends a byte array over the already-established data socket
	 * 
	 * @param bytes
	 * @param len
	 * @return
	 */
	public boolean sendViaDataSocket(byte[] bytes, int start, int len) {

		if (dataOutputStream == null) {
			Log.v(TAG, "Can't send via null dataOutputStream");
			return false;
		}
		if (len == 0) {
			return true; // this isn't an "error"
		}
		try {
			dataOutputStream.write(bytes, start, len);
		} catch (IOException e) {
			Log.v(TAG, "Couldn't write output stream for data socket");
			Log.v(TAG, e.toString());
			return false;
		}
		dataSocketFactory.reportTraffic(len);
		return true;
	}
	
	/**
	 * Will be called by (e.g.) CmdSTOR, CmdRETR, CmdLIST, etc. when they are
	 * about to start actually doing IO over the data socket.
	 * 
	 * @return
	 */
	public boolean startUsingDataSocket() {
		try {
			dataSocket = dataSocketFactory.onTransfer();
			if (dataSocket == null) {
				Log.v(TAG,
						"dataSocketFactory.onTransfer() returned null");
				return false;
			}
			dataOutputStream = dataSocket.getOutputStream();
			return true;
		} catch (IOException e) {
			Log.v(TAG,
					"IOException getting OutputStream for data socket");
			dataSocket = null;
			return false;
		}
	}
	
	public void closeDataSocket() {
		Log.v(TAG, "Closing data socket");
		if (dataOutputStream != null) {
			try {
				dataOutputStream.close();
			} catch (IOException e) {
			}
			dataOutputStream = null;
		}
		if (dataSocket != null) {
			try {
				dataSocket.close();
			} catch (IOException e) {
			}
		}
		dataSocket = null;
	}
	
	public void closeCmdSocket() {
		if (cmdSocket == null) {
			return;
		}
		try {
			cmdSocket.close();
		} catch (IOException e) {}
	}

	public boolean isBinaryMode() {
		return isBinaryMode;
	}

	public void setBinaryMode(boolean isBinaryMode) {
		this.isBinaryMode = isBinaryMode;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(String subdir) {
		this.currentDir = new File(this.currentDir,subdir);
	}
	
	

}
