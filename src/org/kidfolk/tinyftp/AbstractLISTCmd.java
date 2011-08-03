package org.kidfolk.tinyftp;

import java.io.File;

import android.util.Log;

/**
 * abstract LISTCmd for LIST and NLIST
 * 
 * @author kidfolk
 * 
 */
public abstract class AbstractLISTCmd extends FTPCmd {
	private static final String TAG = "AbstractLISTCmd";
	private String inputStr;

	public AbstractLISTCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	/**
	 * 
	 * @param response
	 * @param dir
	 * @return list string or null
	 */
	public String listDir(StringBuilder response, File dir) {
		if (!dir.isDirectory()) {
			return "500 listDir on non-directory\r\n";
		}
		Log.v(TAG, "Listing directory: " + dir.toString());
		File[] files = dir.listFiles();
		if (null == files) {
			return "500 couldn't list directory\r\n";
		}
		Log.v(TAG, "dir len:" + files.length);
		int length = files.length;
		for (int i = 0; i < length; i++) {
			String str = makeLISTString(files[i]);
			if (null != str) {
				response.append(str);
			}
		}
		return null;
	}

	protected String sendLISTString(String listStr) {
		if (session.startUsingDataSocket()) {
			Log.v(TAG, "LIST/NLST done making socket");
		} else {
			session.closeDataSocket();
			return "425 Error opening data socket\r\n";
		}
		String mode = session.isBinaryMode() ? "BINARY" : "ASCII";
		session.sendReplyString("150 Opening " + mode
				+ " mode data connection for file list\r\n");
		Log.v(TAG, "Sent code 150, sending listing string now");
		if (!session.sendViaDataSocket(listStr)) {
			Log.v(TAG, "sendViaDataSocket failure");
			session.closeDataSocket();
			return "426 Data socket or network error\r\n";
		}
		session.closeDataSocket();
		Log.v(TAG, "Listing sendViaDataSocket success");
		session.sendReplyString("226 Data transmission OK\r\n");
		return null;
	}

	public abstract String makeLISTString(File file);

}
