package org.kidfolk.tinyftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public abstract class AbstractSTORCmd extends FTPCmd {
	private static final String TAG = "AbstractSTORCmd";
	private String inputStr;

	public AbstractSTORCmd(Session session, String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	public void doStorOrAppe(String param, boolean append) {
		Log.v(TAG, "STOR/APPE executing with append=" + append);
		// 在文件前加上路径
		File storeFile = inputPathToChrootedFile(session.getCurrentDir(), param);
		String errString = null;
		FileOutputStream out = null;
		storing: {
			// Get a normalized absolute path for the desired file
			if (violatesChroot(storeFile)) {
				errString = "550 Invalid name or chroot violation\r\n";
				break storing;
			}
			if (storeFile.isDirectory()) {
				errString = "451 Can't overwrite a directory\r\n";
				break storing;
			}

			try {
				if (storeFile.exists()) {
					// 文件存在
					if (!append) {
						// 不是追加模式
						if (!storeFile.delete()) {
							// 删除文件
							errString = "451 Couldn't truncate file\r\n";
							break storing;
						}
						// Notify other apps that we just deleted a file
						/* Util.deletedFileNotify(storeFile.getPath()); */
					}
				}
				out = new FileOutputStream(storeFile, append);
			} catch (FileNotFoundException e) {
				try {
					errString = "451 Couldn't open file \"" + param
							+ "\" aka \"" + storeFile.getCanonicalPath()
							+ "\" for writing\r\n";
				} catch (IOException io_e) {
					errString = "451 Couldn't open file, nested exception\r\n";
				}
				break storing;
			}
			if (!session.startUsingDataSocket()) {
				errString = "425 Couldn't open data socket\r\n";
				break storing;
			}
			Log.v(TAG, "Data socket ready");
			session.sendReplyString("150 Data socket ready\r\n");
			byte[] buffer = new byte[1024];
			int numRead;
			// Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			// int newPriority = Thread.currentThread().getPriority();
			// myLog.l(Log.DEBUG, "New STOR prio: " + newPriority);
			if (session.isBinaryMode()) {
				Log.v(TAG, "Mode is binary");
			} else {
				Log.v(TAG, "Mode is ascii");
			}
			// int bytesSinceReopen = 0;
			// int bytesSinceFlush = 0;
			while (true) {
				switch (numRead = session.receiveFromDataSocket(buffer)) {
				case -1:
					Log.v(TAG, "Returned from final read");
					// We're finished reading
					break storing;
				case 0:
					errString = "426 Couldn't receive data\r\n";
					break storing;
				case -2:
					errString = "425 Could not connect data socket\r\n";
					break storing;
				default:
					try {
						if (session.isBinaryMode()) {
							out.write(buffer, 0, numRead);
						} else {
							// ASCII mode, substitute \r\n to \n
							int startPos = 0, endPos;
							for (endPos = 0; endPos < numRead; endPos++) {
								if (buffer[endPos] == '\r') {
									// Our hacky method is to drop all \r
									out.write(buffer, startPos, endPos
											- startPos);
									startPos = endPos + 1;
								}
							}
							if (startPos < numRead) {
								out.write(buffer, startPos, endPos - startPos);
							}
						}
						out.flush();

					} catch (IOException e) {
						errString = "451 File IO problem. Device might be full.\r\n";
						Log.v(TAG, "Exception while storing: " + e);
						Log.v(TAG, "Message: " + e.getMessage());
						Log.v(TAG, "Stack trace: ");
						StackTraceElement[] traceElems = e.getStackTrace();
						for (StackTraceElement elem : traceElems) {
							Log.v(TAG, elem.toString());
						}
						break storing;
					}
					break;
				}
			}
		}

		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException e) {
		}

		if (errString != null) {
			Log.v(TAG, "STOR error: " + errString.trim());
			session.sendReplyString(errString);
		} else {
			session.sendReplyString("226 Transmission complete\r\n");
			// Notify the music player (and possibly others) that a few file has
			// been uploaded.
			// Util.newFileNotify(storeFile.getPath());
		}
		session.closeDataSocket();
		Log.v(TAG, "STOR finished");
	}

}
