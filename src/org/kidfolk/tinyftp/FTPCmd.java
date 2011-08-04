package org.kidfolk.tinyftp;

import java.io.File;
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
			                      /*new CmdMap(EPSVCmd.class, "EPSV"),*/
			                      new CmdMap(SIZECmd.class, "SIZE"),
			                      new CmdMap(CWDCmd.class, "CWD"),
			                      new CmdMap(LISTCmd.class, "LIST"),
			                      new CmdMap(FEATCmd.class, "FEAT"),
			                      new CmdMap(OPTSCmd.class, "OPTS"),
			                      new CmdMap(PASVCmd.class, "PASV"),
			                      new CmdMap(NLISTCmd.class, "NLIST"),
			                      new CmdMap(NOOPCmd.class, "NOOP"),
			                      new CmdMap(RETRCmd.class, "RETR"),
			                      new CmdMap(STORCmd.class, "STOR"),
			                      new CmdMap(DELECmd.class, "DELE"),
			                      new CmdMap(PORTCmd.class, "PORT")};
	
	

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
					Log.v(TAG, ftpCmdInstance.getClass().getName()+" run!");
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
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
	/**
	 * 将参数添加到根目录结构上，获得新的文件路径
	 * @param existingPrefix
	 * @param param
	 * @return
	 */
	public static File inputPathToChrootedFile(File existingPrefix, String param) {
		try {
			if(param.charAt(0) == '/') {
				// The STOR contained an absolute path
				File chroot = new File(GlobleConfig.rootdir);
				return new File(chroot, param);
			}
		} catch (Exception e) {} 
		
		// The STOR contained a relative path
		return new File(existingPrefix, param); 
	}
	
	public boolean violatesChroot(File file) {
		File chroot = new File(GlobleConfig.rootdir);
		try {
			String canonicalPath = file.getCanonicalPath();
			if(!canonicalPath.startsWith(chroot.toString())) {
				Log.v(TAG, "Path violated folder restriction, denying");
				Log.v(TAG, "path: " + canonicalPath);
				Log.v(TAG, "chroot: " + chroot.toString());
				return true; // the path must begin with the chroot path
			}
			return false;
		} catch(Exception e) {
			Log.v(TAG, "Path canonicalization problem: " + e.toString());
			Log.v(TAG, "When checking file: " + file.getAbsolutePath());
			return true;  // for security, assume violation
		}
	}

}
