/**
 * 
 */
package com.itpro.cli;

import com.itpro.util.ProcessingThread;
import com.itpro.util.Queue;

/**
 * @author Giap Van Duc
 *
 */
public abstract class CliCmdReqProcess extends ProcessingThread {
	private int listenPort;
	private Queue queueReq = new Queue();
	private CliCmdListener cmdListener;
	private int defaultRequestTimeout;
	private int defaultRespFormatType = CliCmd.RESP_FORMAT_TYPE_HASH;
	
	private String timeoutRespParamsString = "Result=failed,Error=Request timeout";
	private String syntaxErrorRespParamsString = "Result=failed,Error=Syntax error";
	
	public void setTimeoutErrorString(String timeoutString){
		this.timeoutRespParamsString = timeoutString;
	}
	
	public void setSyntaxErrorString(String syntaxString){
		this.syntaxErrorRespParamsString = syntaxString;
	}
	
	public void setDefaultRequestTimeout(int requestTimeout){
		this.defaultRequestTimeout = requestTimeout;
	}
	
	public void setDefaultRespFormatType(int respFormatType){
		this.defaultRespFormatType = respFormatType;
	}
	
	public void setListenPort(int listenPort){
		this.listenPort = listenPort;
	}
	
	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#OnHeartBeat()
	 */
	@Override
	protected abstract void OnHeartBeat();

	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#initialize()
	 */
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		cmdListener = new CliCmdListener(listenPort);
		cmdListener.setLogger(logger);
		cmdListener.setQueueReq(queueReq);
		cmdListener.setLogPrefix(logPrefix);
		cmdListener.setDefaultRequestTimeout(defaultRequestTimeout);
		cmdListener.setTimeoutRespParams(timeoutRespParamsString);;
		cmdListener.setSyntaxErrorRespParams(syntaxErrorRespParamsString);
		cmdListener.setDefaultRespFormatType(defaultRespFormatType);
		cmdListener.start();
	}

	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#process()
	 */
	@Override
	protected void process(){
		CliCmd cmdRequest = (CliCmd)queueReq.dequeue();
		if(cmdRequest!=null){
			OnRequest(cmdRequest);
		}
	}
	
	protected abstract void OnRequest(CliCmd cmdRequest);
	
}
