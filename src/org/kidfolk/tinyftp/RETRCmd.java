package org.kidfolk.tinyftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.util.Log;

public class RETRCmd extends FTPCmd {
	private static final String TAG = "RETRCmd";
	private String inputStr;

	public RETRCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "RETRCmd executing!");
		String param = getParameter(inputStr);
		File fileToRetr;
		String errString = null;

		mainblock: {
			fileToRetr = inputPathToChrootedFile(session.getCurrentDir(), param);
			if (violatesChroot(fileToRetr)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break mainblock;
			} else if (fileToRetr.isDirectory()) {
				Log.v(TAG, "Ignoring RETR for directory");
				errString = "550 Can't RETR a directory\r\n";
				break mainblock;
			} else if (!fileToRetr.exists()) {
				Log.v(TAG,
						"Can't RETR nonexistent file: "
								+ fileToRetr.getAbsolutePath());
				errString = "550 File does not exist\r\n";
				break mainblock;
			} else if (!fileToRetr.canRead()) {
				Log.v(TAG, "Failed RETR permission (canRead() is false)");
				errString = "550 No read permissions\r\n";
				break mainblock;
			} /*
			 * else if(!sessionThread.isBinaryMode()) { myLog.l(Log.INFO,
			 * "Failed RETR in text mode"); errString =
			 * "550 Text mode RETR not supported\r\n"; break mainblock; }
			 */
			try {
				FileInputStream in = new FileInputStream(fileToRetr);
				byte[] buffer = new byte[1024];
				int bytesRead;
				if (session.startUsingDataSocket()) {
					Log.v(TAG, "RETR opened data socket");
				} else {
					errString = "425 Error opening socket\r\n";
					Log.v(TAG, "Error in initDataSocket()");
					break mainblock;
				}
				session.sendReplyString("150 Sending file\r\n");
				if (session.isBinaryMode()) {
					Log.v(TAG, "Transferring in binary mode");
					while ((bytesRead = in.read(buffer)) != -1) {
						if (session.sendViaDataSocket(buffer, bytesRead) == false) {
							errString = "426 Data socket error\r\n";
							Log.v(TAG, "Data socket error");
							break mainblock;
						}
					}
				} else { // We're in ASCII mode
					Log.v(TAG, "Transferring in ASCII mode");
					// We have to convert all solitary \n to \r\n
					boolean lastBufEndedWithCR = false;
					while ((bytesRead = in.read(buffer)) != -1) {
						int startPos = 0, endPos = 0;
						byte[] crnBuf = { '\r', '\n' };
						for (endPos = 0; endPos < bytesRead; endPos++) {
							if (buffer[endPos] == '\n') {
								// Send bytes up to but not including the
								// newline
								session.sendViaDataSocket(buffer, startPos,
										endPos - startPos);
								if (endPos == 0) {
									// handle special case where newline occurs
									// at
									// the beginning of a buffer
									if (!lastBufEndedWithCR) {
										// Send an \r only if the the previous
										// buffer didn't end with an \r
										session.sendViaDataSocket(crnBuf, 1);
									}
								} else if (buffer[endPos - 1] != '\r') {
									// The file did not have \r before \n, add
									// it
									session.sendViaDataSocket(crnBuf, 1);
								} else {
									// The file did have \r before \n, don't
									// change
								}
								startPos = endPos;
							}
						}
						// Now endPos has finished traversing the array, send
						// remaining
						// data as-is
						session.sendViaDataSocket(buffer, startPos, endPos
								- startPos);
						if (buffer[bytesRead - 1] == '\r') {
							lastBufEndedWithCR = true;
						} else {
							lastBufEndedWithCR = false;
						}
					}
				}
			} catch (FileNotFoundException e) {
				errString = "550 File not found\r\n";
				break mainblock;
			} catch (IOException e) {
				errString = "425 Network error\r\n";
				break mainblock;
			}
		}
		session.closeDataSocket();
		if (errString != null) {
			session.sendReplyString(errString);
		} else {
			session.sendReplyString("226 Transmission finished\r\n");
		}
		Log.v(TAG, "RETR done");
	}

}
