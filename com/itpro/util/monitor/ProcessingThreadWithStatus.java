/**
 * 
 */
package com.itpro.util.monitor;

import com.itpro.util.ProcessingThread;
import com.itpro.util.Queue;

/**
 * @author ducgv
 *
 */
public abstract class ProcessingThreadWithStatus extends ProcessingThread{
	protected Queue queueQueryThreadStatusReq = new Queue();
	public void queryThreadStatus(CmdQueryThreadStatus cmdQueryThreadStatusReq){
		queueQueryThreadStatusReq.enqueue(cmdQueryThreadStatusReq);
	}
	
	protected abstract void OnQueryThreadStatusReq(CmdQueryThreadStatus cmdQueryThreadStatusReq);
	
	protected void checkQueueQueryThreadStatusReq(){
		CmdQueryThreadStatus cmdQueryThreadStatusReq = (CmdQueryThreadStatus)queueQueryThreadStatusReq.dequeue();
		if(cmdQueryThreadStatusReq!=null){
			OnQueryThreadStatusReq(cmdQueryThreadStatusReq);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itpro.util.ProcessingThread#process()
	 */
	@Override
	protected void process() {
		checkQueueQueryThreadStatusReq();
		processEx();
	}

	protected abstract void processEx();
}
