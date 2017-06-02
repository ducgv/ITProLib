/**
 * 
 */
package com.itpro.log4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import com.itpro.util.ProcessingThread;
import com.itpro.util.Queue;

/**
 * @author Giap Van Duc
 *
 */
public class ITProLog4jCategory extends ProcessingThread {

	public ITProLog4jCategoryInfo categoryInfo;
	private Queue messageQueue;
	Calendar m_sLastTimeStamp;
	private static final int TIME_AM = 0;
	private static final int TIME_PM = 1;
	private static String [] meridiemString;	
	
	int m_lastMeridiem; //=0: AM
						//=1: PM
	

	FileOutputStream fileOutputStream;
	boolean logFileOpened;
	private static String[] logLevelString;

	public static void ClassInitialize(){
		logLevelString = new String[4];
		logLevelString[0] = "INFO";
		logLevelString[1] = "WARNING";
		logLevelString[2] = "ERROR";
		logLevelString[3] = "DEBUG";
		meridiemString = new String[2];
		meridiemString[0] = "AM";
		meridiemString[1] = "PM";
	}

	public ITProLog4jCategory() {
		// TODO Auto-generated constructor stub
		messageQueue = new Queue();
	}
	/* (non-Javadoc)
	 * @see com.logica.smpp.util.ProcessingThread#OnHeartBeat()
	 */
	@Override
	public void OnHeartBeat() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.logica.smpp.util.ProcessingThread#initialize()
	 */
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		Date date = new Date(System.currentTimeMillis());
		m_sLastTimeStamp = Calendar.getInstance();
		m_sLastTimeStamp.setTime(date);
		if(m_sLastTimeStamp.get(Calendar.HOUR_OF_DAY)>=0&&m_sLastTimeStamp.get(Calendar.HOUR_OF_DAY)<12)
			m_lastMeridiem = TIME_AM;
		else
			m_lastMeridiem = TIME_PM;

		
		if(categoryInfo.append!=0){
			String csFileName = String.format("%s%s.%04d%02d%02d.%s.log",
					categoryInfo.logPath,categoryInfo.fileNamePrefix,
					m_sLastTimeStamp.get(Calendar.YEAR),m_sLastTimeStamp.get(Calendar.MONTH)+1,m_sLastTimeStamp.get(Calendar.DAY_OF_MONTH),
					meridiemString[m_lastMeridiem]);

			File file = new File(csFileName);			
			boolean checked = true;
			if(!file.exists()){
				try {
					file.createNewFile();
					checked = true;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.out.println(csFileName);
					checked = false;						
				}
			}
			if(!checked){
				logFileOpened=true;
				return;
			}

			try {		
				fileOutputStream=new FileOutputStream(file, true);				
				logFileOpened=true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logFileOpened = false;
				fileOutputStream = null;
			}		
		}
	}

	/* (non-Javadoc)
	 * @see com.logica.smpp.util.ProcessingThread#process()
	 */
	@Override
	public void process() {
		// TODO Auto-generated method stub
		ITProLog4jMessage message = (ITProLog4jMessage) messageQueue.dequeue();
		if(message!=null){
			try{
				OnLog(message);
			}catch (Exception e){
				//do nothing
			}
			DoNotSleep();
		}
	}

	public void Info(String content){
		if(categoryInfo.dumpToFile==0&&categoryInfo.dumpToScreen==0)
			return;
		if(categoryInfo.enableDumpInfo==0)
			return;
		ITProLog4jMessage logMessage=new ITProLog4jMessage();
		logMessage.logLevel=LogLevel.INFO;
		logMessage.content=content;		
		messageQueue.enqueue(logMessage);
	}

	public void Warning(String content){
		if(categoryInfo.dumpToFile==0&&categoryInfo.dumpToScreen==0)
			return;
		if(categoryInfo.enableDumpWarning==0)
			return;
		ITProLog4jMessage logMessage=new ITProLog4jMessage();
		logMessage.logLevel=LogLevel.WARNING;
		logMessage.content=content;		
		messageQueue.enqueue(logMessage);
	}

	public void Error(String content){
		if(categoryInfo.dumpToFile==0&&categoryInfo.dumpToScreen==0)
			return;
		if(categoryInfo.enableDumpError==0)
			return;
		ITProLog4jMessage logMessage=new ITProLog4jMessage();
		logMessage.logLevel=LogLevel.ERROR;
		logMessage.content=content;

		messageQueue.enqueue(logMessage);
	}

	public void Debug(String content){
		if(categoryInfo.dumpToFile==0&&categoryInfo.dumpToScreen==0)
			return;
		if(categoryInfo.enableDumpDebug==0)
			return;
		ITProLog4jMessage logMessage=new ITProLog4jMessage();
		logMessage.logLevel=LogLevel.DEBUG;
		logMessage.content=content;

		messageQueue.enqueue(logMessage);
	}

	private void OnChangeTime(){
		if(logFileOpened)
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String csFileName = String.format("%s%s.%04d%02d%02d.%s.log",
					categoryInfo.logPath,categoryInfo.fileNamePrefix,
					m_sLastTimeStamp.get(Calendar.YEAR),m_sLastTimeStamp.get(Calendar.MONTH)+1,m_sLastTimeStamp.get(Calendar.DAY_OF_MONTH),
					meridiemString[m_lastMeridiem]);

			File file = new File(csFileName);	
			boolean checked = true;
			if(!file.exists()){
				try {
					file.createNewFile();
					checked = true;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					checked = false;						
				}
			}
			if(!checked){
				logFileOpened = false;			
				return;
			}
			try {
				fileOutputStream=new FileOutputStream(file, true);
				logFileOpened=true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logFileOpened = false;
				fileOutputStream = null;
			}
	}

	private void OnLog(ITProLog4jMessage pELog4cppPacket){
		
		Date date = new Date(System.currentTimeMillis());
		Calendar currentTimestamp = Calendar.getInstance();
		currentTimestamp.setTime(date);
		int currentMeridiem;
		if(currentTimestamp.get(Calendar.HOUR_OF_DAY)>=0&&currentTimestamp.get(Calendar.HOUR_OF_DAY)<12)
			currentMeridiem = TIME_AM;
		else
			currentMeridiem = TIME_PM;
		
		String csDump= String.format("%02d/%02d/%02d %02d:%02d:%02d [%-5s] - %s\n",currentTimestamp.get(Calendar.DAY_OF_MONTH),currentTimestamp.get(Calendar.MONTH)+1,currentTimestamp.get(Calendar.YEAR),currentTimestamp.get(Calendar.HOUR_OF_DAY),currentTimestamp.get(Calendar.MINUTE),currentTimestamp.get(Calendar.SECOND),logLevelString[pELog4cppPacket.logLevel],pELog4cppPacket.content);

		if(categoryInfo.dumpToScreen!=0)
			System.out.print(csDump);

		if(categoryInfo.append!=0){
			if((m_sLastTimeStamp.get(Calendar.YEAR)!=currentTimestamp.get(Calendar.YEAR))||
					(m_sLastTimeStamp.get(Calendar.MONTH)+1!=currentTimestamp.get(Calendar.MONTH)+1)||
					(m_sLastTimeStamp.get(Calendar.DAY_OF_MONTH)!=currentTimestamp.get(Calendar.DAY_OF_MONTH))||
					(m_lastMeridiem!=currentMeridiem)){
				m_sLastTimeStamp=currentTimestamp;
				m_lastMeridiem = currentMeridiem;
				OnChangeTime();
			}			
		}
		
		if(categoryInfo.dumpToFile!=0){
			if(categoryInfo.append!=0){
				if(logFileOpened){
					try {
						fileOutputStream.write(csDump.getBytes());					
						fileOutputStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else {

				String csFileName = String.format("%s%s.log",
						categoryInfo.logPath,categoryInfo.fileNamePrefix);
				File file = new File(csFileName);
				boolean checked = true;
				if(!file.exists()){
					try {
						file.createNewFile();
						checked = true;
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						checked = false;						
					}
				}
				if(!checked)
					return;
				try {
					fileOutputStream=new FileOutputStream(file, true);					
					fileOutputStream.write(csDump.getBytes());					
					fileOutputStream.flush();
					fileOutputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
