package org.kidfolk.tinyftp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.util.Log;
/**
 * ftp命令基类，子类实现run抽象方法做具体处理
 * @author kidfolk
 *
 */
public abstract class FTPCmd implements Runnable {
	private static final String TAG = "FTPCmd";
	protected Session session;
	//cmd array
	protected static CmdMap[] cmdMaps = {new CmdMap(USERCmd.class, "USER"),
			                      new CmdMap(PASSCmd.class, "PASS"),
			                      new CmdMap(QUITCmd.class, "QUIT"),
			                      new CmdMap(SYSTCmd.class, "SYST"),
			                      new CmdMap(PWDCmd.class, "PWD"),
			                      new CmdMap(TYPECmd.class, "TYPE"),
			                      new CmdMap(EPSVCmd.class, "EPSV"),
			                      new CmdMap(SIZECmd.class, "SIZE"),
			                      new CmdMap(CWDCmd.class, "CWD"),
			                      new CmdMap(LISTCmd.class, "LIST"),
			                      new CmdMap(FEATCmd.class, "FEAT"),
			                      new CmdMap(OPTSCmd.class, "OPTS"),
			                      new CmdMap(PASVCmd.class, "PASV")};
	
	

	public FTPCmd(Session session) {
		super();
		this.session = session;
	}

	@Override
	public abstract void run();
	
	public static void dispatchCmd(Session session,String inputStr){
		String[] strings = inputStr.split(" ");
		String unrecognizedCmdMsg = "502 Command not recognized\r\n";
		if(null==strings){
			//nothing get from client
			String errString = "502 Command parse error\r\n";
			Log.v(TAG, errString);
			session.sendReplyString(errString);
			return;
		}
		if(strings.length<1){
			//cmd is ""
			Log.v(TAG, unrecognizedCmdMsg);
			session.sendReplyString(unrecognizedCmdMsg);
			return;
		}
		
		String cmdAction = strings[0];
		FTPCmd ftpCmdInstance;
		//get the normal case
		cmdAction = cmdAction.trim();
		cmdAction = cmdAction.toUpperCase();
		int length = cmdMaps.length;
		for(int i=0;i<length;i++){
			if(cmdAction.equals(cmdMaps[i].getName())){
				//find the right ftp command
				Constructor<? extends FTPCmd> constructor;
				try {
					//find the right constructor
					constructor = cmdMaps[i].getCmdClass().getConstructor(new Class[]{Session.class,String.class});
					//create ftpcmd object
					ftpCmdInstance = constructor.newInstance(new Object[]{session,inputStr});
					//处理命令
					ftpCmdInstance.run();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
	}
	
	/**
	 * An FTP parameter is that part of the input string that occurs
	 * after the first space, including any subsequent spaces. Also,
	 * we want to chop off the trailing '\r\n', if present.
	 * 
	 * Some parameters shouldn't be logged or output (e.g. passwords),
	 * so the caller can use silent==true in that case.
	 */
	static public String getParameter(String input, boolean silent) {
		if(input == null) {
			return "";
		}
		int firstSpacePosition = input.indexOf(' ');
		if(firstSpacePosition == -1) {
			return "";
		}
		String retString = input.substring(firstSpacePosition+1);
		
		// Remove trailing whitespace
		// todo: trailing whitespace may be significant, just remove \r\n
		retString = retString.replaceAll("\\s+$", "");
		
		if(!silent) {
			Log.v(TAG, "parameter: "+retString);
		}
		return retString; 
	}
	
	/**
	 * A wrapper around getParameter, for when we don't want it to be silent.
	 */
	static public String getParameter(String input) {
		return getParameter(input, false);
	}

}
