/**
 * 
 */
package com.itpro.log4j;

import java.util.Enumeration;
import java.util.Hashtable;

import com.itpro.cfgreader.CfgReader;

/**
 * @author Giap Van Duc
 *
 */
public class ITProLog4j {
	private Hashtable<String, ITProLog4jCategoryInfo> mapInstanceNameToCategory;
	private String configFileName;
	public boolean Initialize(String configFileName, String defaultLogPath){
		this.configFileName = configFileName;
		ITProLog4jCategory.ClassInitialize();
		mapInstanceNameToCategory = new Hashtable<String, ITProLog4jCategoryInfo>();

		CfgReader cfgReader = new CfgReader();
		cfgReader.load(configFileName);
		for(int i=0;i<cfgReader.lstGroups.size();i++){			
			ITProLog4jCategoryInfo newCategoryInfo = new ITProLog4jCategoryInfo(); 
			cfgReader.setGroup(cfgReader.lstGroups.get(i));
			newCategoryInfo.SetConfig(cfgReader,defaultLogPath);
			AddCategory(newCategoryInfo, false);
		}
		return true;
	}

	public ITProLog4jCategory GetInstance(String instanceName, 
			String defaultLogPath,
			String defaultFileNamePrefix,	
			int defaultAppend,
			int defaultDumpToFile,
			int defaultDumpToScreen,
			int defaultEnableDumpInfo,
			int defaultEnableDumpWarning,
			int defaultEnableDumpError,
			int defaultEnableDumpDebug,
			boolean bSaveToFile)
	{
		synchronized (mapInstanceNameToCategory) {		
			ITProLog4jCategoryInfo categoryInfo=mapInstanceNameToCategory.get(instanceName);
			if(categoryInfo!=null){
				return categoryInfo.logThread;
			}
			else{
				categoryInfo = new ITProLog4jCategoryInfo();
				categoryInfo.instanceName = instanceName;
				categoryInfo.logPath = defaultLogPath;
				categoryInfo.fileNamePrefix = defaultFileNamePrefix;	
				categoryInfo.append = defaultAppend;
				categoryInfo.dumpToFile = defaultDumpToFile;
				categoryInfo.dumpToScreen = defaultDumpToScreen;
				categoryInfo.enableDumpInfo = defaultEnableDumpInfo;
				categoryInfo.enableDumpWarning = defaultEnableDumpWarning;
				categoryInfo.enableDumpError = defaultEnableDumpError;
				categoryInfo.enableDumpDebug = defaultEnableDumpDebug;
				categoryInfo.bSaveToFile = bSaveToFile;
				AddCategory(categoryInfo, true);
				if(bSaveToFile)
					SaveConfigToFile();
				return categoryInfo.logThread;			
			}
		}
	}

	public void SaveConfigToFile(){
		CfgReader cfgReader = new CfgReader();
		Enumeration<String> keys = mapInstanceNameToCategory.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			ITProLog4jCategoryInfo categoryInfo= mapInstanceNameToCategory.get(key);
			if(categoryInfo!=null){
				if(categoryInfo.bSaveToFile){
					cfgReader.setGroup(categoryInfo.instanceName);
					cfgReader.setKeyValue("LogPath",categoryInfo.logPath);
					cfgReader.setKeyValue("FileNamePrefix",categoryInfo.fileNamePrefix);	
					cfgReader.setKeyValue("Append",((Integer)categoryInfo.append).toString());
					cfgReader.setKeyValue("DumpToFile",((Integer)categoryInfo.dumpToFile).toString());
					cfgReader.setKeyValue("DumpToScreen",((Integer)categoryInfo.dumpToScreen).toString());
					cfgReader.setKeyValue("EnableDumpInfo",((Integer)categoryInfo.enableDumpInfo).toString());
					cfgReader.setKeyValue("EnableDumpWarning",((Integer)categoryInfo.enableDumpWarning).toString());
					cfgReader.setKeyValue("EnableDumpError",((Integer)categoryInfo.enableDumpError).toString());
					cfgReader.setKeyValue("EnableDumpDebug",((Integer)categoryInfo.enableDumpDebug).toString());
				}
			}			
		}
		cfgReader.save(configFileName);
	}

	public void Start()
	{
		Enumeration<String> keys = mapInstanceNameToCategory.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			ITProLog4jCategoryInfo categoryInfo = mapInstanceNameToCategory.get(key);
			if(categoryInfo!=null){
				categoryInfo.logThread = new ITProLog4jCategory();
				categoryInfo.logThread.categoryInfo = categoryInfo;
				categoryInfo.logThread.start();

			}
		}

	}
	public void Stop()
	{
		Enumeration<String> keys = mapInstanceNameToCategory.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			ITProLog4jCategoryInfo categoryInfo = mapInstanceNameToCategory.get(key);
			if(categoryInfo!=null){
				if(categoryInfo.logThread!=null){
					categoryInfo.logThread.stop();					
					categoryInfo.logThread = null;
					categoryInfo = null;
				}				
			}
		}
		mapInstanceNameToCategory.clear();
	}

	public boolean AddCategory(ITProLog4jCategoryInfo newCategoryInfo, boolean startAfterCreate)
	{
		if(mapInstanceNameToCategory.contains(newCategoryInfo.instanceName)){
			return false;
		}
		newCategoryInfo.logThread = null;
		mapInstanceNameToCategory.put(newCategoryInfo.instanceName, newCategoryInfo);

		if(startAfterCreate){
			newCategoryInfo.logThread = new ITProLog4jCategory();
			newCategoryInfo.logThread.categoryInfo = newCategoryInfo;
			newCategoryInfo.logThread.start();
		}
		return true;
	}
}
