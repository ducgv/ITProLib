/**
 * 
 */
package com.itpro.util.monitor;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import com.itpro.cli.CliCmd;
import com.itpro.util.ProcessingThread;
import com.itpro.util.monitor.CmdQueryThreadStatus;
import com.itpro.util.monitor.ProcessingThreadWithStatus;
import com.itpro.util.Queue;

/**
 * @author ducgv
 *
 */
public class CliCmdCheckModuleStatusProcess extends ProcessingThread {
	public Queue queueCliCmdReq = new Queue();
	private Queue queueQueryThreadStatusResp = null;
	private Hashtable<String, CmdQueryThreadStatus> listWaitingResp = null;
	private Hashtable<String, Object> listChecking = new Hashtable<String, Object>();
	
	private int cliCheckModuleStatusTimeout = 30;
	private long cliCmdReqTime;
	
	private CliCmd currentCliCmd = null;
	
	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#OnHeartBeat()
	 */
	@Override
	protected void OnHeartBeat() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#initialize()
	 */
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	public void registerThread(String threadName, ProcessingThreadWithStatus threadObject){
		CmdQueryThreadStatus cmdQueryThreadStatus = new CmdQueryThreadStatus();
		cmdQueryThreadStatus.threadName = threadName;
		cmdQueryThreadStatus.threadObject = threadObject;
		listChecking.put(threadName, cmdQueryThreadStatus);
	}
	
	public void registerThreadArray(String groupName, ProcessingThreadWithStatus[] threadObjects){
		//listChecking.put(groupName, threadObjects);
		if(threadObjects.length>0){
			CmdQueryThreadStatus[] cmdQueryThreadStatuses = new CmdQueryThreadStatus[threadObjects.length];
			for(int i=0;i<threadObjects.length;i++){
				cmdQueryThreadStatuses[i] = new CmdQueryThreadStatus();
				ProcessingThreadWithStatus threadObject = threadObjects[i];
				cmdQueryThreadStatuses[i].threadName = groupName+"("+i+")";
				cmdQueryThreadStatuses[i].threadObject = threadObject;
			}
			listChecking.put(groupName, cmdQueryThreadStatuses);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#process()
	 */
	@Override
	protected void process() {
		// TODO Auto-generated method stub
		if(currentCliCmd==null){
			currentCliCmd = (CliCmd) queueCliCmdReq.dequeue();
			if (currentCliCmd != null) {
				OnCliCmdReq(currentCliCmd);
			}
		}
		else{
			CmdQueryThreadStatus cmdQueryThreadStatusResp = (CmdQueryThreadStatus) queueQueryThreadStatusResp.dequeue();
			if(cmdQueryThreadStatusResp!=null){
				OnCmdQueryThreadStatusResp(cmdQueryThreadStatusResp);
			}
			else{
				long curentTime = System.currentTimeMillis();
				if(curentTime - cliCmdReqTime>=cliCheckModuleStatusTimeout*1000){
					doRespCli();
				}
			}
		}
	}
	
	private void OnCmdQueryThreadStatusResp(CmdQueryThreadStatus cmdQueryThreadStatusResp) {
		// TODO Auto-generated method stub
		CmdQueryThreadStatus cmdQueryThreadStatusWaitResp = listWaitingResp.remove(cmdQueryThreadStatusResp.threadName);
		cmdQueryThreadStatusWaitResp.threadStatusInfo = cmdQueryThreadStatusResp.threadStatusInfo;
		if(listWaitingResp.isEmpty()){
			doRespCli();
		}
	}

	private void doRespCli() {
		// TODO Auto-generated method stub
		JsonObjectBuilder jsonObjectBuilder = currentCliCmd.getJsonObjectBuilder();
		Enumeration<String> keys = listChecking.keys();
		JsonObject jsonObjectNoRespValue = Json.createObjectBuilder().add(CmdQueryThreadStatus.FIELD_THREAD_RUNNING_STATUS, CmdQueryThreadStatus.STATUS_FAIL).build();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Object value = listChecking.get(key);
			if(!value.getClass().isArray()){
				CmdQueryThreadStatus cmdQueryThreadStatusWaitResp = (CmdQueryThreadStatus) value;
				if(cmdQueryThreadStatusWaitResp.threadStatusInfo!=null){
					jsonObjectBuilder.add(key, cmdQueryThreadStatusWaitResp.threadStatusInfo);
				}
				else{
					jsonObjectBuilder.add(key, jsonObjectNoRespValue);
				}
				cmdQueryThreadStatusWaitResp.threadStatusInfo = null;
			}
			else{
				JsonArrayBuilder jsonArray = Json.createArrayBuilder();
				CmdQueryThreadStatus[] cmdQueryThreadStatusWaitResps = (CmdQueryThreadStatus[]) listChecking.get(key);
				for(CmdQueryThreadStatus cmdQueryThreadStatusWaitResp: cmdQueryThreadStatusWaitResps){
					if(cmdQueryThreadStatusWaitResp.threadStatusInfo!=null){
						
						jsonArray.add(cmdQueryThreadStatusWaitResp.threadStatusInfo);
					}
					else{
						jsonArray.add(jsonObjectNoRespValue);
					}
					cmdQueryThreadStatusWaitResp.threadStatusInfo = null;
				}
				jsonObjectBuilder.add(key, jsonArray);
			}
		}
		currentCliCmd.apply();
		currentCliCmd.doResponse();
		logInfo(currentCliCmd.getRespString());
		currentCliCmd = null;
		queueQueryThreadStatusResp = null;
		listWaitingResp = null;
	}

	private void OnCliCmdReq(CliCmd cmdRequest) {
		// TODO Auto-generated method stub
		cliCmdReqTime = System.currentTimeMillis();
		logInfo(cmdRequest.toString());
		listWaitingResp = new Hashtable<String, CmdQueryThreadStatus>();
		queueQueryThreadStatusResp = new Queue();
		Enumeration<String> keys = listChecking.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Object value = listChecking.get(key);
			if(!value.getClass().isArray()){
				CmdQueryThreadStatus cmdQueryThreadStatusWaitResp = (CmdQueryThreadStatus) value;
				CmdQueryThreadStatus cmdQueryThreadStatusReq = new CmdQueryThreadStatus();
				cmdQueryThreadStatusReq.threadName = key;
				cmdQueryThreadStatusReq.queueResp = queueQueryThreadStatusResp;
				listWaitingResp.put(key, cmdQueryThreadStatusWaitResp);
				cmdQueryThreadStatusWaitResp.threadObject.queryThreadStatus(cmdQueryThreadStatusReq);
			}
			else{
				CmdQueryThreadStatus[] cmdQueryThreadStatusWaitResps = (CmdQueryThreadStatus[]) listChecking.get(key);
				for(CmdQueryThreadStatus cmdQueryThreadStatusWaitResp: cmdQueryThreadStatusWaitResps){
					CmdQueryThreadStatus cmdQueryThreadStatusReq = new CmdQueryThreadStatus();
					cmdQueryThreadStatusReq.threadName = cmdQueryThreadStatusWaitResp.threadName;
					cmdQueryThreadStatusReq.queueResp = queueQueryThreadStatusResp;
					listWaitingResp.put(cmdQueryThreadStatusWaitResp.threadName, cmdQueryThreadStatusWaitResp);
					cmdQueryThreadStatusWaitResp.threadObject.queryThreadStatus(cmdQueryThreadStatusReq);
				}
			}
		}
		
	}

	public int getCliCheckModuleStatusTimeout() {
		return cliCheckModuleStatusTimeout;
	}

	public void setCliCheckModuleStatusTimeout(int cliCheckModuleStatusTimeout) {
		this.cliCheckModuleStatusTimeout = cliCheckModuleStatusTimeout;
	}
}
