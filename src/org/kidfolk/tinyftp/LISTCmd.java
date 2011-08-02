package org.kidfolk.tinyftp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class LISTCmd extends FTPCmd {
	// The approximate number of milliseconds in 6 months
	public final static long MS_IN_SIX_MONTHS = 6 * 30 * 24 * 60 * 60 * 1000;
	private static String TAG = "LISTCmd";
	private String inputStr;
	
	

	public LISTCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}



	@Override
	public void run() {
		String errString = null;
		String param = getParameter(inputStr);
		Log.v(TAG, "LISTCmd operation!");
		session.sendReplyString("210 CWD successful\r\n");
		File fileToList = new File(GlobleConfig.rootdir);
		String list;
		if(fileToList.isDirectory()){
			StringBuilder response = new StringBuilder();
			errString = listDirectory(response, fileToList);
			sendListing(response.toString());
		}
	}



	public String listDirectory(StringBuilder response, File dir) {
		if(!dir.isDirectory()) {
			return "500 Internal error, listDirectory on non-directory\r\n";
		}
		Log.v(TAG, "Listing directory: " + dir.toString());
		
		// Get a listing of all files and directories in the path
		File[] entries = dir.listFiles();
		if(entries == null) {
			return "500 Couldn't list directory. Check config and mount status.\r\n";
		}
		Log.v(TAG, "Dir len " + entries.length);
		for(File entry : entries) {
			String curLine = makeLsString(entry);
			if(curLine != null) {
				response.append(curLine);
			}
		}
		return null;
	}
	
	// Generates a line of a directory listing in the traditional /bin/ls
	// format.
	protected String makeLsString(File file) {
		StringBuilder response = new StringBuilder();
		
		if(!file.exists()) {
			Log.v(TAG, "makeLsString had nonexistent file");
			return null;
		}

		// See Daniel Bernstein's explanation of /bin/ls format at:
		// http://cr.yp.to/ftp/list/binls.html
		// This stuff is almost entirely based on his recommendations.
		
		String lastNamePart = file.getName();
		// Many clients can't handle files containing these symbols
		if(lastNamePart.contains("*") || 
		   lastNamePart.contains("/"))
		{
			Log.v(TAG, "Filename omitted due to disallowed character");
			return null;
		} else {
			// The following line generates many calls in large directories
			//staticLog.l(Log.DEBUG, "Filename: " + lastNamePart);
		}
				
		
		if(file.isDirectory()) {
			response.append("drwxr-xr-x 1 owner group");
		} else {
			// todo: think about special files, symlinks, devices
			response.append("-rw-r--r-- 1 owner group");
		}
		
		// The next field is a 13-byte right-justified space-padded file size
		long fileSize = file.length();
		String sizeString = new Long(fileSize).toString();
		int padSpaces = 13 - sizeString.length();
		while(padSpaces-- > 0) {
			response.append(' ');
		}
		response.append(sizeString);
		
		// The format of the timestamp varies depending on whether the mtime
		// is 6 months old
		long mTime = file.lastModified();
		SimpleDateFormat format;
		// Temporarily commented out.. trying to fix Win7 display bug
		if(System.currentTimeMillis() - mTime > MS_IN_SIX_MONTHS) {
			// The mtime is less than 6 months ago
			format = new SimpleDateFormat(" MMM dd HH:mm ", Locale.US);
		} else {
			// The mtime is more than 6 months ago
			format = new SimpleDateFormat(" MMM dd  yyyy ", Locale.US);
		}
		response.append(format.format(new Date(file.lastModified())));
		response.append(lastNamePart);
		response.append("\r\n");
		return response.toString();
	}
	
	// Send the directory listing over the data socket. Used by CmdLIST and
	// CmdNLST.
	// Returns an error string on failure, or returns null if successful.
	protected String sendListing(String listing) {
		
		String mode = session.isBinaryMode() ? "BINARY" : "ASCII";
		session.sendReplyString(
				"150 Opening "+mode+" mode data connection for file list\r\n");
		session.sendViaDataSocket(listing);
		session.sendReplyString("226 Data transmission OK\r\n");
		return null;
	}
 
}
 
