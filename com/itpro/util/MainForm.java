/**
 * 
 */
package com.itpro.util;

import com.itpro.general.General;
import com.itpro.log4j.ITProLog4j;

/**
 * @author Giap Van Duc
 *
 */
public abstract class MainForm {
//	private static String systemName = "itpro";
	private static String homePath = "";
	private static String configPath = "";
	private static String logPath = "";
	private static String logConfig = "log.cfg";
	
	public static ITProLog4j logManager;
	
	public static void setLogConfig(String logConfig){
		MainForm.logConfig = logConfig;
	}
	public static void setHomePath(String systemName){
		MainForm.homePath = systemName;
	}
	
	public static String getHomePath(){
		return homePath;
	}
	
	public static String getConfigPath(){
		return configPath;
	}
	
	public static String getLogPath(){
		return logPath;
	}
	
	public void start(){
		General.checkPlatform();
		if(General.getPlatform()==General.PLATFORM_UNKNOWN){
			System.out.println("WARNING Operator System:"+General.os);			
		}
		
		if(General.getPlatform()==General.PLATFORM_WINDOW){
			homePath = homePath.replace("<root>", "C:").replace("/", "\\");
			configPath = homePath + "config\\";
			logPath = homePath + "log\\";
		}
		else{
			homePath = homePath.replace("<root>", "");
			configPath = homePath + "config/";
			logPath = homePath + "log/";
		}
		
		logManager = new ITProLog4j();
		logManager.Initialize(configPath+logConfig,logPath);
		logManager.Start();
		
		OnLoadConfig();		
		OnStartSystem();
		
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected abstract void OnStartSystem();
	protected abstract void OnLoadConfig();
}
