/**
 * 
 */
package com.itpro.general;

/**
 * @author Giap Van Duc
 *
 */
public class General {
	public static final byte PLATFORM_WINDOW = 1;
	public static final byte PLATFORM_SOLARIS =2;
	public static final byte PLATFORM_LINUX   = 3;
	public static final byte PLATFORM_UNKNOWN   = -1;

	private static byte platform = -1;
	public static String os;
	public static String filePathSeparator;
	public static String endOfLineCharactor;
	
	public static byte getPlatform(){
		return platform;
	}

	public static void checkPlatform(){		
		os = System.getProperty("os.name");
		os = os.toLowerCase();
		if (os.indexOf("linux") >= 0){
			platform  = PLATFORM_LINUX;			
			filePathSeparator = "/";
			endOfLineCharactor = "\n";
		}else if (os.indexOf("windows") >= 0){
			platform  = PLATFORM_WINDOW;			
			filePathSeparator = "\\";
			endOfLineCharactor = "\r\n";
		}else if (os.indexOf("solaris") >= 0){
			platform  = PLATFORM_SOLARIS;			
			filePathSeparator = "/";
			endOfLineCharactor = "\n";
		}else if (os.indexOf("sun") >= 0){
			platform  = PLATFORM_SOLARIS;			
			filePathSeparator = "/";
			endOfLineCharactor = "\n";
		}else{
			platform = PLATFORM_UNKNOWN;
			filePathSeparator = "/";
			endOfLineCharactor = "\n";
		}
	}
}
