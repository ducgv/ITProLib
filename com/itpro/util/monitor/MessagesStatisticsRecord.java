package com.itpro.util.monitor;

import java.sql.Timestamp;
import java.util.Hashtable;

public class MessagesStatisticsRecord {
	public String dbTableName;
	public Timestamp dateTime;
	public String moduleName;
	public String messageName;
	public Hashtable<String, Object> messageFields = new Hashtable<String, Object>();
}
