/*
 * Copyright (c) 1996-2001
 * Logica Mobile Networks Limited
 * All rights reserved.
 *
 * This software is distributed under Logica Open Source License Version 1.0
 * ("Licence Agreement"). You shall use it and distribute only in accordance
 * with the terms of the License Agreement.
 *
 */
package com.itpro.util;

import com.itpro.log4j.ITProLog4jCategory;

/**
 * Implements <code>Runnable</code> which does a repetitive processing in
 * a cycle and can be started in a thread and stopped in more
 * controlled manner than <code>Thread</code> class.
 * Provides final exception reporting (if there is any).
 *
 * @author Logica Mobile Networks SMPP Open Source Team
 * @version 1.0, 01 Oct 2001
 */
public abstract class ProcessingThread implements Runnable {
	//2009/05/05 ducgv add
	private int heartBeatInterval = 1000;
	public void setHeartBeatInterval(int heartBeatInterval){
		this.heartBeatInterval = heartBeatInterval;
	}
	//end of ducgv add	
	//2009/05/18 ducgv add
	private boolean needToSleep = true;
	//end of ducgv add
	
	private boolean keepProcessing = true;
	
	/*2011/05/11 ducgv add*/
	protected ITProLog4jCategory logger = null;
	protected String logPrefix = "";
	
	public void setLogger(ITProLog4jCategory logger){
		this.logger = logger;
	}
	
	public void setLogPrefix(String logPrefix){
		this.logPrefix = logPrefix;
	}
	
	protected void logInfo(String logString){
		logger.Info(logPrefix+logString);
	}
	
	protected void logWarning(String logString){
		logger.Warning(logPrefix+logString);
	}
	
	protected void logError(String logString){
		logger.Error(logPrefix+logString);
	}
	
	protected void logDebug(String logString){
		logger.Debug(logPrefix+logString);
	}
	
	/*end of ducgv add*/
	
	/**
	 * State variable indicating status of the thread. Don't confuse with
	 * <code>keepProcessing</code> which stops the main loop, but
	 * can be set to <code>false</code> far before the end of the loop
	 * using <code>stopProcessing</code> method.
	 * 
	 * @see #keepProcessing
	 * @see #run()
	 * @see #start()
	 * @see #stop()
	 * @see #setProcessingStatus(byte)
	 * @see #isInitialising()
	 * @see #isProcessing()
	 * @see #isFinished()
	 */
	private byte processingStatus = PROC_INITIALISING;

	/**
	 * The processing thread is in initialisation phase.
	 * It's the phase after calling <code>start</code>
	 * but before entering while-loop in the <code>run</code> method.
	 *
	 * @see #processingStatus
	 * @see #isInitialising()
	 */
	private static final byte PROC_INITIALISING = 0;

	/**
	 * The processing thread is in running phase.
	 * It's the phase when the thread is in the while-loop
	 * in the <code>process</code> method or method called from the loop
	 * that method.
	 *
	 * @see #processingStatus
	 * @see #isProcessing()
	 * @see #run()
	 */
	private static final byte PROC_RECEIVING = 1;

	/**
	 * The processing thread is finished.
	 * The finished phase is phase when the thread has exited the while-loop
	 * in the <code>run</code> method. It is possible to run it again
	 * by calling <code>start</code> method again.
	 *
	 * @see #processingStatus
	 * @see #isFinished()
	 */
	private static final byte PROC_FINISHED = 2;

	/**
	 * Object for monitoring the access to the <code>processingStatus</code>
	 * variable.
	 */
	private Object processingStatusLock = new Object();

	/**
	 * Contains the last caught exception.
	 * As there is no means how to catch an exception thrown from the
	 * <code>run</code> method, for case that it's necessary to examine an
	 * exception thrown in <code>run</code> it is stored to this variable.<br>
	 * Descendants of <code>ProcessingThread</code> will use this variable
	 * to store the exception which will cause termination of the thread.
	 * It is also set by <code>run</code> method if it'll catch an
	 * exception when calling <code>process</code>.<br>
	 * The exception is also set by the <code>stopProcessing</code>
	 * method.
	 *
	 * @see #stopProcessing(Exception)
	 * @see #setTermException(Exception)
	 * @see #getTermException()
	 */
	private Exception termException = null;

	/**
	 * The thread which runs the code of this class.
	 */
	private Thread processingThread = null;

	/**
	 * The method which is repeatedly called from the <code>run</code>
	 * method. This/ is supposed the hearth of the actual processing. The
	 * derived classes should implement their code in this method.
	 */
	protected abstract void process();

	/**
	 * Creates new thread and passes it <code>this</code> as
	 * the <code>Runnable</code> to run. Resets private variables to defaults.
	 * Starts the newly created thread.
	 *
	 * @see Thread
	 */
	public void start(){
		if (!isProcessing()) { // i.e. is initialising or finished
			setProcessingStatus(PROC_INITIALISING);
			termException = null;
			keepProcessing = true;
			processingThread = new Thread(this);			
			processingThread.start();
			while (isInitialising()) {
				Thread.yield(); // we're waiting for the proc thread to start
			}
		}		
	}

	/**
	 * Stops the receiving by setting flag <code>keepProcessing</code> to false.
	 * Waits until the receiving is really stopped.
	 */
	public void stop(){
		if (isProcessing()) {
			stopProcessing(null);
//			while (!isFinished()) {
//			Thread.yield(); // we're waiting for the proc thread to stop
//			}
		}		
	}

	/**
	 * Causes stoping of the while-loop in the <code>run</code> method.
	 * Called from <code>stop</code> method or can be used to terminate
	 * the processing thread in case of an exceptional situation while
	 * processing (from <code>process</code> method.)
	 */
	protected void stopProcessing(Exception e){
		setTermException(e);
		keepProcessing = false;
	}

	//2009/05/17 ducgv add
	protected abstract void initialize();
	//end of ducgv add
	/**
	 * Calls <code>process</code> in cycle until stopped.
	 * This method is called from <code>Thread</code>'s code as the code which
	 * has to be executed by the thread.
	 *
	 * @see Thread
	 */
	public void run()
	{
		setProcessingStatus(PROC_RECEIVING);

		//2009/05/17 ducgv add
		try{
			initialize();
		}catch(Exception e){
			if(logger!=null){
				//e.printStackTrace();
				logError("initialize: Exception:"+getExceptionString(e));
			}
		}
		
		//end of ducgv add
		//2009/05/05 ducgv add            
		long lastTime=System.currentTimeMillis();
		long currentTime=System.currentTimeMillis();
		//end of ducgv add	
		
		
		while (keepProcessing) {
			needToSleep = true;
			try{
				process();
			}catch(Exception e){
				if(logger!=null){
					//e.printStackTrace();
					logError("process: Exception:"+getExceptionString(e));
				}
			}
			//2009/05/05 ducgv add
			currentTime = System.currentTimeMillis();
			if(currentTime-lastTime>=heartBeatInterval){
				lastTime=currentTime;
				try{
					if(keepProcessing){
						OnHeartBeat();
					}
				}catch(Exception e){
					if(logger!=null){
						//e.printStackTrace();
						logError("OnHeartBeat: Exception:"+getExceptionString(e));
					}
				}
			}
			//end of ducgv add

			Thread.yield();

			//2009/05/18 ducgv add
			if(needToSleep){
				try{
					Thread.sleep(5);
				}
				catch(InterruptedException e){
					//e.printStackTrace();
					if(logger!=null){
						logWarning("sleep: Exception:"+getExceptionString(e));
					}
				}
			}
			//end of ducgv add
		}
		setProcessingStatus(PROC_FINISHED);		
	}

	//2009/05/18 ducgv add
	protected void DoNotSleep(){
		needToSleep = false;
	}
	//end of ducgv add

	//2009/05/05 ducgv add
	protected abstract void OnHeartBeat();
	//end of ducgv add
	
	/**
	 * As there is no means how to catch an exception thrown from
	 * <code>run</code> method, in case that it's necessary to throw an
	 * exception it's rather remembered by calling of this method.
	 *
	 * @param e the exception to remember
	 */
	protected void setTermException(Exception e) { termException = e; }

	/**
	 * Returns the last exception caught during processing.
	 *
	 * @return the last exception caught during processing
	 */
	public Exception getTermException(){
		return termException;
	}

	public boolean isFinished(){
		synchronized (processingStatusLock) {
			return processingStatus == PROC_FINISHED;
		}
	}
	
	/**
	 * Sets the <code>processingStatus</code> to value provided.
	 */
	private void setProcessingStatus(byte value){
		synchronized (processingStatusLock) {
			processingStatus = value;
		}
	}

	/**
	 * Returns if the <code>processingStatus</code> indicates that
	 * the receiving is in initialisation stage, i.e. the processing loop
	 * has not been entered yet.
	 */
	public boolean isInitialising(){ 
		synchronized (processingStatusLock) {
			return processingStatus == PROC_INITIALISING;
		}
	}

	/**
	 * Returns if the <code>processingStatus</code> indicates that
	 * the receiving has started, but it didn't finished yet, i.e. the
	 * the thread is still in the processing loop.
	 */
	public boolean isProcessing(){
		synchronized (processingStatusLock) {
			return processingStatus == PROC_RECEIVING;
		}
	}
	
	/*
	 * 2018/09/04 ducgv add 
	 */
	public static String getExceptionString(Exception e){
		String exceptionString = e.toString()+"\n";
		for(StackTraceElement stackTraceElement:e.getStackTrace()){
			exceptionString+="\t at "+stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")\n";
		}
		return exceptionString;
	}
}
