/**
 * 
 */
package com.itpro.log4j;

import com.itpro.cfgreader.CfgReader;

/**
 * @author Giap Van Duc
 *
 */
public class ITProLog4jCategoryInfo {
	public String instanceName;
	public String logPath;
	public String fileNamePrefix;
	public int append;
	public int dumpToFile;
	public int dumpToScreen;
	public int enableDumpInfo;
	public int enableDumpWarning;
	public int enableDumpError;
	public int enableDumpDebug;
	public ITProLog4jCategory logThread;
	boolean bSaveToFile;
	public void SetConfig(CfgReader cfgReader, String defaultLogPath)
	{
		instanceName=cfgReader.getCurrentGroupString();
		logPath=cfgReader.getString("LogPath",defaultLogPath);
		if(logPath.equals(""))
			cfgReader.setKeyValue("LogPath", defaultLogPath);
		fileNamePrefix=cfgReader.getString("FileNamePrefix","");		
		append = cfgReader.getInt("Append",1);
		dumpToFile = cfgReader.getInt("DumpToFile",1);		
		dumpToScreen=cfgReader.getInt("DumpToScreen",1);
		enableDumpInfo=cfgReader.getInt("EnableDumpInfo",1);
		enableDumpWarning=cfgReader.getInt("EnableDumpWarning",1);
		enableDumpError=cfgReader.getInt("EnableDumpError",1);
		enableDumpDebug=cfgReader.getInt("EnableDumpDebug",1);
		bSaveToFile = true;
	}
}
