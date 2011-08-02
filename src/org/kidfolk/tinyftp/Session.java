package org.kidfolk.tinyftp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;


import android.util.Log;

public class Session implements Runnable {
	private Socket cmdSocket;//命令通道
	protected DataSocketFactory dataSocketFactory;//数据通道
	protected OutputStream ops;
	protected boolean isBinaryMode = false;
	protected String encoding = "UTF-8";
	private static final String TAG = "Session";
	

	public Session(Socket cmdSocket,DataSocketFactory dataSocketFactory) {
		super();
		this.cmdSocket = cmdSocket;
		this.dataSocketFactory = dataSocketFactory;
	}



	@Override
	public void run() {
		Log.v(TAG, "session start!");
		sendReplyString("220 SwiFTP ready\r\n");
//		sendReplyString("220 written by kidfolk\r\n");
		//读取客户端发过来的命令，并将命令分发到具体的命令类中处理
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(cmdSocket.getInputStream()));
			while(true){
				String cmdLine;
				cmdLine = br.readLine();
				if(null!=cmdLine){
					//分发命令
					FTPCmd.dispatchCmd(this, cmdLine);
					Log.v(TAG, "session dispatchCmd!");
				}else{
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
//			if(null!=br){
//				try {
//					br.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
		}
	}


	/**
	 * send reply message to client
	 * @param str
	 */
	public void sendReplyString(String str) {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(cmdSocket.getOutputStream()));
			bw.write(str);
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
//			try {
//				if(null!=bw){
//					bw.close();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
	}
	/**
	 * server listen a free port for data transfer
	 * @return port number
	 */
	public int onEPSV(){
		return dataSocketFactory.onPasv();
	}
	/**
	 * 
	 * @return
	 */
	public int onPASV(){
		return dataSocketFactory.onPasv();
	}
	/**
	 * get the dataSocket IP address
	 * the IP is the same as cmdSocket
	 * @return
	 */
	public InetAddress getDataSocketPasvIP(){
		return cmdSocket.getLocalAddress();
	}
	
	/**
	 * Sends a string over the already-established data socket
	 * 
	 * @param string
	 * @return Whether the send completed successfully
	 */
	public void sendViaDataSocket(String string) {
		try {
			byte[] bytes = string.getBytes(encoding);
			if(ops==null){
				ops = dataSocketFactory.onTransfer().getOutputStream();
			}
			ops.write(bytes);
			ops.flush();
		} catch (UnsupportedEncodingException e) {
			//TODO
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

}
