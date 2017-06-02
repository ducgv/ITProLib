/**
 * 
 */
package com.itpro.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

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
		return "SQLException [ErrorCode:"+e.getErrorCode()+", SQLState:"+e.getSQLState()+", Message:"+e.getMessage()+"]";
	}
}
