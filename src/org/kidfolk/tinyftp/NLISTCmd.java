package org.kidfolk.tinyftp;

import java.io.File;

import android.util.Log;

public class NLISTCmd extends AbstractLISTCmd {
	private static final String TAG = "NLISTCmd";
	private String inputStr;
	public final static long MS_IN_SIX_MONTHS = 6 * 30 * 24 * 60 * 60 * 1000; 

	public NLISTCmd(Session session, String inputStr) {
		super(session, inputStr);
		this.inputStr = inputStr;
	}

	@Override
	public String makeLISTString(File file) {
		if(!file.exists()) {
			return null;
		}

		// See Daniel Bernstein's explanation of NLST format at:
		// http://cr.yp.to/ftp/list/binls.html
		// This stuff is almost entirely based on his recommendations.
		
		String lastNamePart = file.getName();
		// Many clients can't handle files containing these symbols
		if(lastNamePart.contains("*") || 
		   lastNamePart.contains("/"))
		{
			return null;
		} else {
			return lastNamePart + "\r\n";
		}
	}

	@Override
	public void run() {
		Log.v(TAG, "NLISTCmd executing!!");
		String errString = null;
		mainblock: {
			String param = getParameter(inputStr);
			if(param.startsWith("-")) {
				// Ignore options to list, which start with a dash
				param = "";
			}
			File fileToList = null;
			if(param.equals("")) {
				fileToList = session.getCurrentDir();
			} else {
				if(param.contains("*")) {
					errString = "550 NLST does not support wildcards\r\n";
					break mainblock;
				}
				session.setCurrentDir(param);
				fileToList = session.getCurrentDir();
				if(violatesChroot(fileToList)) {
					errString = "450 Listing target violates chroot\r\n";
					break mainblock;
				} else if(fileToList.isFile()) {
					// Bernstein suggests that NLST should fail when a 
					// parameter is given and the parameter names a regular 
					// file (not a directory).
					errString = "550 NLST for regular files is unsupported\r\n";
					break mainblock;
				}				
			}
			String listing;
			if(fileToList.isDirectory()) {
				StringBuilder response = new StringBuilder();
				errString = listDir(response, fileToList);
				if(errString != null) {
					break mainblock;
				}
				listing = response.toString();
			} else {
				listing = makeLISTString(fileToList);
				if(listing == null) {
					errString = "450 Couldn't list that file\r\n";
					break mainblock;
				}
			}
			errString = sendLISTString(listing);
			if(errString != null) {
				break mainblock;
			}
		}
		
		if(errString != null) {
			session.sendReplyString(errString);
			Log.v(TAG, "NLST failed with: " + errString);
		} else {
			Log.v(TAG, "NLST completed OK");
		}

	}

}
