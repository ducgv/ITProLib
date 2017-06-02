/**
 * 
 */
package com.itpro.cli;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import com.itpro.util.ProcessingThread;
import com.itpro.util.Queue;
import com.itpro.util.TCPConnection;

/**
 * @author Giap Van Duc
 *
 */
public class CmdListener extends ProcessingThread {
	private boolean isListening = false;
	private boolean needToTerminate = false;
	private int		requestTimeout;
	private ServerSocket serverSocket;
	private int listenPort;
	private Queue queueReq;
	
	private Hashtable<String, String> resultTimeoutError;
	private Hashtable<String, String> resultSyntaxError;
	
	public void setResultTimeoutError(String timeoutString){
		resultTimeoutError = new Hashtable<String, String>();
		String[] arrString = timeoutString.split(",");				
		for(String property: arrString){
			if(!property.equals("")){
				String[] csTemp = property.trim().split("=");
				if(csTemp.length==2){
					resultTimeoutError.put(csTemp[0].trim(), csTemp[1].trim());
				}
			}
		}
	}
	
	public void setResultSyntaxError(String syntaxErrorString){
		resultSyntaxError = new Hashtable<String, String>();
		String[] arrString = syntaxErrorString.split(",");				
		for(String property: arrString){
			if(!property.equals("")){
				String[] csTemp = property.trim().split("=");
				if(csTemp.length==2){
					resultSyntaxError.put(csTemp[0].trim(), csTemp[1].trim());
				}
			}
		}
	}
	
	public void setRequestTimeout(int requestTimeout){
		this.requestTimeout = requestTimeout;
	}
	
	public void setQueueReq(Queue queue){
		queueReq = queue;
	}
	
	public CmdListener(int listenPort) {
		// TODO Auto-generated constructor stub
		this.listenPort = listenPort;
	}
	public void process() {
		if(needToTerminate){
			OnTerminate();
			return;
		}
		if(isListening)
			Listen();

	}

	private void OnTerminate() {
		// TODO Auto-generated method stub
		
	}

	private void Listen(){
		// TODO Auto-generated method stub
		try {
			try{
				serverSocket.setSoTimeout(5000);
				Socket socket = serverSocket.accept();  // accept connection
				if(socket!=null){					
					TCPConnection newConnection = new TCPConnection(socket);
					logInfo("Accept new connection from " + newConnection.address + "/" + newConnection.port);
					CmdSession cmdSession = new CmdSession(newConnection);
					cmdSession.setLogPrefix(logPrefix);
					cmdSession.setHeartBeatInterval(requestTimeout*1000);
					cmdSession.setLogger(logger);
					cmdSession.setQueueReq(queueReq);
					cmdSession.setTimeoutErrorParam(resultTimeoutError);
					cmdSession.setSyntaxErrorParam(resultSyntaxError);
					cmdSession.start();
				}
			}catch(SocketTimeoutException e){
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block			
//			e.printStackTrace();
			logWarning("IOException - " + e.getMessage());
		} 
	}

	@Override
	public void OnHeartBeat() {
		// TODO Auto-generated method stub
		if(!isListening)
			CreateListener();
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub		
		CreateListener();
	}
	public void CreateListener(){
		try{
			serverSocket = new ServerSocket(listenPort);
			serverSocket.setReuseAddress(true);
			serverSocket.setSoTimeout(5000);
			logInfo("Server listening on port " + serverSocket.getLocalPort());
			isListening = true;
		} catch (IOException e) {
			logError("Exception on new ServerSocket: " + e);
			isListening = false;
		}
	}
}
