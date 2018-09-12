/**
 * 
 */
package com.itpro.util.monitor;

import java.sql.SQLException;
import javax.json.JsonObjectBuilder;
import com.itpro.util.MySQLConnection;
import com.itpro.util.Queue;

/**
 * @author Giap Van Duc
 *
 */
public class MessagesStatisticsUpdater extends ProcessingThreadWithStatus {
	public String dbHost;
	public String dbName;
	public String dbUsername;
	public String dbPassword;
	private Queue queueInsertMessagesStatisticsRecordReq = new Queue();
	private MySQLConnection connection = null;
	public boolean isConnected = false;
	
	public void insertMessagesStatisticsRecord(MessagesStatisticsRecord messagesStatisticsRecord){
		queueInsertMessagesStatisticsRecordReq.enqueue(messagesStatisticsRecord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itpro.util.ProcessingThread#OnHeartBeat()
	 */
	@Override
	protected void OnHeartBeat() {
		// TODO Auto-generated method stub
		if (connection == null) {
			Connect();
		} else if (!isConnected) {
			connection.close();
			Connect();
		}
		if(isConnected){
			try {
				connection.checkConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logError("Exception when check DB connection:"+getExceptionString(e));
				isConnected = false;
			}
		}
	}

	private void OnConnected() {

	}

	private void Connect() {
		connection = new MySQLConnection(dbHost, dbName, dbUsername, dbPassword) {
		};
		
		try {
			isConnected = connection.connect();
			OnConnected();
		} catch (Exception e) {
			isConnected = false;
			logError("Exception when connect to DB:" + getExceptionString(e));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.itpro.util.ProcessingThread#initialize()
	 */
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		setHeartBeatInterval(5000);
		Connect();
	}

	@Override
	protected void OnQueryThreadStatusReq(CmdQueryThreadStatus cmdQueryThreadStatusReq) {
		// TODO Auto-generated method stub
		JsonObjectBuilder jsonObjectBuilder = cmdQueryThreadStatusReq.getJsonObjectBuilder();
		jsonObjectBuilder.add(CmdQueryThreadStatus.FIELD_THREAD_RUNNING_STATUS, CmdQueryThreadStatus.STATUS_OK);
		if(!isConnected){
			jsonObjectBuilder.add(CmdQueryThreadStatus.FIELD_DB_CONNECTION_STATUS, CmdQueryThreadStatus.STATUS_FAIL);
		}
		else{
			try {
				connection.checkConnection();
				jsonObjectBuilder.add(CmdQueryThreadStatus.FIELD_DB_CONNECTION_STATUS, CmdQueryThreadStatus.STATUS_OK);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logError(getExceptionString(e));
				jsonObjectBuilder.add(CmdQueryThreadStatus.FIELD_DB_CONNECTION_STATUS, CmdQueryThreadStatus.STATUS_FAIL);
			}
		}
		cmdQueryThreadStatusReq.apply();
		cmdQueryThreadStatusReq.doResponse();
	}

	@Override
	protected void processEx() {
		// TODO Auto-generated method stub
		if(!isConnected)
			return;
		MessagesStatisticsRecord messagesStatisticsRecord = (MessagesStatisticsRecord) queueInsertMessagesStatisticsRecordReq.dequeue();
		if(messagesStatisticsRecord!=null){
			try {
				connection.insertMessagesStatisticsRecord(messagesStatisticsRecord);
				logInfo(messagesStatisticsRecord.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logError(messagesStatisticsRecord.toString()+ "; Exception:" + getExceptionString(e));
				queueInsertMessagesStatisticsRecordReq.enqueue(messagesStatisticsRecord);
				isConnected = false;
			}
		}
	}
}
