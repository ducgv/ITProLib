/**
 * 
 */
package com.itpro.cli;

import java.io.IOException;
import javax.json.JsonObject;
import com.itpro.util.ProcessingThread;
import com.itpro.util.Queue;
import com.itpro.util.TCPConnection;
import com.logica.smpp.util.ByteBuffer;
/**
 * @author Giap Van Duc
 *
 */
public class CmdSession extends ProcessingThread {
	private TCPConnection connection;
	private CmdRequest request = null;
	private Queue queueResp = null;
	private Queue queueReq = null;
	private boolean isReceived = false;
	private boolean needToTerminate = false;
	/*
	private Hashtable<String, String> resultTimeoutError;
	private Hashtable<String, String> resultSyntaxError;
	*/
	private JsonObject timeoutRespParams;
	private JsonObject syntaxErrorRespParams;
	private int respFormatType;
	
	public void setTimeoutRespParams(JsonObject timeoutRespParams){
		this.timeoutRespParams = timeoutRespParams;
	}
	
	public void setSyntaxErrorRespParams(JsonObject syntaxErrorRespParams){
		this.syntaxErrorRespParams = syntaxErrorRespParams;
	}
	
	public void setQueueReq(Queue queue){
		queueReq = queue;
	}
	
	public CmdSession(TCPConnection connection) {
		// TODO Auto-generated constructor stub
		this.connection = connection;
		connection.nSendTimeout = 1000;
		connection.nReceiveTimeout = 1000;		
	}
	
	JsonObject parseErrorParamsString(String errorParramsString){
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.logica.smpp.util.ProcessingThread#OnHeartBeat()
	 */
	@Override
	public void OnHeartBeat() {
		// TODO Auto-generated method stub
		if (request != null) {
			request.setRespParams(timeoutRespParams);
			try {
				connection.send((request.getRespString()+"\n").getBytes());
				logInfo("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString());
			}catch (IOException e) {
				// TODO Auto-generated catch block					
				//					e.printStackTrace();
				logError("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString()+"; error:"+e.getMessage());
			}
			Terminate();
		}
		else{
			request = new CmdRequest();
			request.setRespParams(timeoutRespParams);
			try {
				connection.send((request.getRespString()+"\n").getBytes());
				logInfo("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString());
			}catch (IOException e) {
				// TODO Auto-generated catch block					
				//					e.printStackTrace();
				logError("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString()+"; error:"+e.getMessage());
			}
			Terminate();
		}
	}

	public void Terminate(){
		needToTerminate  = true;
	}
	
	private void OnTerminate() {
		// TODO Auto-generated method stub		
		connection.close();		
		connection = null;
		stop();
	}

	/* (non-Javadoc)
	 * @see com.logica.smpp.util.ProcessingThread#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub		
		queueResp = new Queue();
	}

	/* (non-Javadoc)
	 * @see com.logica.smpp.util.ProcessingThread#process()
	 */
	@Override
	public void process() {
		// TODO Auto-generated method stub
		if(needToTerminate){
			OnTerminate();			
			return;
		}
		Receive();
		CmdRequest messageResp = (CmdRequest)queueResp.dequeue();
		if(messageResp!=null){			
			try {
				connection.send((request.getRespString()+"\n").getBytes());
				logInfo("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logInfo("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString()+"; error:"+e.getMessage());
			}	
			OnTerminate();
		}
	}
	
	public void OnCmdRequest(){
		logInfo("<- " + connection.address + "/" + connection.port + ": cmd:"+ request.cmd+"; param:"+ request.params);
		if(queueReq!=null){
			queueReq.enqueue(request);
		}		
	}	
	
	public void OnReceive(ByteBuffer buffer){
		String csReceived = new String(buffer.getBuffer());
		request = new CmdRequest();
		request.respFormatType = respFormatType;
		request.queueResp = queueResp;
		if(csReceived!=null&&(!csReceived.equals(""))){				
			csReceived=csReceived.replaceAll("\r\n", "");
			csReceived=csReceived.replaceAll("\n", "");
			int pos = csReceived.indexOf(":");
			if(pos<=0){
				request.cmd = csReceived.trim();
			}
			else{
				request.cmd = csReceived.substring(0,pos).trim();
				if(csReceived.length()>pos){
					csReceived = csReceived.substring(pos+1);
					String[] arrString = csReceived.split(",");				
					for(String property: arrString){
						if(!property.equals("")){
							String[] csTemp = property.trim().split("=",2);
							if(csTemp.length>=2){
								request.params.put(csTemp[0].trim(), csTemp[1].trim());
							}
							else {
								request.params.put(csTemp[0].trim(), "");
							}
						}
					}
				}
			}
			isReceived = true;
			OnCmdRequest();

		}
		else {
			request.setRespParams(syntaxErrorRespParams);
			try {
				connection.send((request.getRespString()+"\n").getBytes());
				logInfo("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logInfo("-> " + connection.address + "/" + connection.port + ": result:"+ request.getRespString()+"; error:"+e.getMessage());
			}
			Terminate();
			return;
		}		
	}
	
	public void Receive(){
		if(!isReceived){
			try {
				ByteBuffer buffer = connection.receive();
				if(buffer!=null&&buffer.length()>0){				
					OnReceive(buffer);
					DoNotSleep();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				logError("<- " + connection.address + "/" + connection.port + ": Receive() - error:"+e.getMessage());
				Terminate();
			}			
		}	
	}

	public void setRespFormatType(int respFormatType) {
		// TODO Auto-generated method stub
		this.respFormatType = respFormatType;
	}
}
