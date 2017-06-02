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
public abstract class CmdReqProcess extends ProcessingThread {
	private int listenPort;
	private Queue queueReq = new Queue();
	private CmdListener cmdListener;
	private int requestTimeout;
	
	private String timeoutErrorString = "Result=failed,Error=Request timeout";
	private String syntaxErrorString = "Result=failed,Error=Syntax error";
	
	public void setTimeoutErrorString(String timeoutString){
		this.timeoutErrorString = timeoutString;
	}
	
	public void setSyntaxErrorString(String syntaxString){
		this.syntaxErrorString = syntaxString;
	}
	
	public void setRequestTimeout(int requestTimeout){
		this.requestTimeout = requestTimeout;
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
		cmdListener = new CmdListener(listenPort);
		cmdListener.setLogger(logger);
		cmdListener.setQueueReq(queueReq);
		cmdListener.setLogPrefix(logPrefix);
		cmdListener.setRequestTimeout(requestTimeout);
		cmdListener.setResultTimeoutError(timeoutErrorString);
		cmdListener.setResultSyntaxError(syntaxErrorString);
		cmdListener.start();
	}

	/* (non-Javadoc)
	 * @see com.itpro.util.ProcessingThread#process()
	 */
	@Override
	protected void process(){
		CmdRequest cmdRequest = (CmdRequest)queueReq.dequeue();
		if(cmdRequest!=null){
			OnRequest(cmdRequest);
		}
	}
	
	protected abstract void OnRequest(CmdRequest cmdRequest);
	
}
