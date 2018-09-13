package com.itpro.util.monitor;
import java.util.Hashtable;

public class MessagesStatisticsRecord {
	public String dbTableName;
	public Hashtable<String, Object> messageInfo = new Hashtable<String, Object>();
	public String toString(){
		String result="MessagesStatisticsRecord:"+
				"dbTableName:"+dbTableName+
				", messageInfo:"+messageInfo.toString();
		return result;
	}
}
