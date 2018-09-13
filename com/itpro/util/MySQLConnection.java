/**
 * 
 */
package com.itpro.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;

import com.itpro.util.monitor.MessagesStatisticsRecord;

/**
 * @author Giap Van Duc
 *
 */
public abstract class MySQLConnection {
	private String serverIpAddr;
	private String databaseName;
	private String userName;
	private String password;
	
	public MySQLConnection(String serverIpAddr, String databaseName, String userName, String password) {
		// TODO Auto-generated constructor stub
		this.serverIpAddr = serverIpAddr;
		this.databaseName = databaseName;
		this.userName = userName;
		this.password = password;
	}
	
	public Connection connection = null;	
	public void close() {
		if(connection != null){
			try{
				connection.close();											
			}
			catch(SQLException e){				
			}
			catch (Exception e){
			}
			connection = null;			
		}

	}
	
	public boolean connect() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {		
		//	Load the JDBC driver
		String driverName = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC driver
		Class.forName(driverName).newInstance();
		
		//	Create a connection to the database		
		String url = "jdbc:mysql://" + serverIpAddr + "/" + databaseName; // a JDBC url
		connection = DriverManager.getConnection(url, userName, password);			
		return true;		
	}
	
	public void checkConnection() throws SQLException{
		PreparedStatement ps=connection.prepareStatement(
				"SELECT 1");
		ps.execute();
		
		ResultSet rs = ps.getResultSet();
		if(rs.next()) {
			rs.getInt(1);			
		}
		rs.close();
		ps.close();
	}
	
	public void getParams(Params params, String tblParamName) throws SQLException{
		PreparedStatement ps=connection.prepareStatement(
				"SELECT param, `value` FROM "+tblParamName);
		ps.execute();
		ResultSet rs = ps.getResultSet();

		Hashtable<String, String> list = new Hashtable<String, String>();
		while(rs.next()) {
			String key = rs.getString(1);
			String value;
			try {
				value = new String(rs.getBytes(2),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				value = "";
			}
			list.put(key, value);
		}
		rs.close();
		ps.close();
		synchronized (params.mutex) {
			params.list = list;
			params.isLoaded = true;
		}	
	}
	
	public static String getSQLExceptionString(SQLException e){
		//return "SQLException [ErrorCode:"+e.getErrorCode()+", SQLState:"+e.getSQLState()+", Message:"+e.getMessage()+"]";
		String exceptionString = e.toString()+"\n";
		for(StackTraceElement stackTraceElement:e.getStackTrace()){
			exceptionString+="\t at "+stackTraceElement.getClassName()+"."+stackTraceElement.getMethodName()+"("+stackTraceElement.getFileName()+":"+stackTraceElement.getLineNumber()+")\n";
		}
		return exceptionString;
	}
	
	public void insertMessagesStatisticsRecord(MessagesStatisticsRecord statisticsMessageInfo) throws SQLException{
		PreparedStatement ps = null;		
		
		String sql = "INSERT INTO "+statisticsMessageInfo.dbTableName+ 
				"(";
		Enumeration<String> fields = statisticsMessageInfo.messageInfo.keys();
		String sqlFields = "";
		String sqlValues="";
		
		while(fields.hasMoreElements()){
			String field = fields.nextElement();
			sqlFields += ","+field;
			Object value=statisticsMessageInfo.messageInfo.get(field);
			if (value instanceof String || value instanceof Timestamp || value instanceof Date ) {
				sqlValues +=",'"+value.toString()+"'";
				
			}
			else {
				sqlValues +=","+value.toString();
				
			}
		}
		sqlFields = sqlFields.substring(1);
		sqlValues = sqlValues.substring(1);
		sql+=sqlFields+") VALUES ("+sqlValues+")";
		
		ps=connection.prepareStatement(sql);	
		ps.execute();
		ps.close();
	}
}
