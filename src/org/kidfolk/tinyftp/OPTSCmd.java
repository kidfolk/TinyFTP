package org.kidfolk.tinyftp;

import android.util.Log;

public class OPTSCmd extends FTPCmd{
	private static String TAG = "OPTSCmd";
	private String inputStr;

	public OPTSCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		String param = getParameter(inputStr);
		String[] splits = param.split(" ");
		String optName = splits[0].toUpperCase();
		String optVal = splits[1].toUpperCase();
		String errString = null;
		if(optName.equals("UTF8")) {
			// OK, whatever. Don't really know what to do here. We
			// always operate in UTF8 mode.
			if(optVal.equals("ON")) {
				Log.v(TAG,"Got OPTS UTF8 ON");
				session.setEncoding("UTF-8");
			} else {
				Log.v(TAG,"Ignoring OPTS UTF8 for something besides ON");
			}
		} else {
			Log.v(TAG,"Unrecognized OPTS option: " + optName);
			errString = "502 Unrecognized option\r\n";
		}
		if(null==errString){
			session.sendReplyString("200 OPTS accepted\r\n");
			Log.v(TAG,"200 OPTS accepted");
		}else{
			session.sendReplyString(errString);
		}
	}

}
