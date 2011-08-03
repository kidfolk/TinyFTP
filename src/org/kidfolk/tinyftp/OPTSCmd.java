package org.kidfolk.tinyftp;

import android.util.Log;
/**
 * Select options for a feature.
 * @author kidfolk
 *
 */
public class OPTSCmd extends FTPCmd{
	private static final String TAG = "OPTSCmd";
	private String inputStr;

	public OPTSCmd(Session session,String inputStr) {
		super(session);
		this.inputStr = inputStr;
	}

	@Override
	public void run() {
		Log.v(TAG, "OPTSCmd executing!");
		String param = getParameter(inputStr);
		String errString = null;
		mainBlock: {
			if(param == null) {
				errString = "550 Need argument to OPTS\r\n";
				Log.v(TAG,"Couldn't understand empty OPTS command");
				break mainBlock;
			}
			String[] splits = param.split(" ");
			if(splits.length != 2) {
				errString = "550 Malformed OPTS command\r\n";
				Log.v(TAG,"Couldn't parse OPTS command");
				break mainBlock;
			}
			String optName = splits[0].toUpperCase();
			String optVal = splits[1].toUpperCase();
			if(optName.equals("UTF8")) {
				// OK, whatever. Don't really know what to do here. We
				// always operate in UTF8 mode.
				if(optVal.equals("ON")) {
					Log.v(TAG,"Got OPTS UTF8 ON");
					session.setEncoding("UTF-8");
				} else {
					Log.v(TAG,"Ignoring OPTS UTF8 for something besides ON");
				}
				break mainBlock;
			} else {
				Log.v(TAG,"Unrecognized OPTS option: " + optName);
				errString = "502 Unrecognized option\r\n";
				break mainBlock;
			}
		}
		
		if(errString != null) {
			session.sendReplyString(errString);
			Log.v(TAG,errString);
		} else {
			session.sendReplyString("200 OPTS accepted\r\n");
			Log.v(TAG,"Handled OPTS ok");
		}
	}

}
