package com.itpro.util.monitor;

import java.sql.Timestamp;
import java.util.Hashtable;

public class MessagesStatisticsRecord {
	public String dbTableName;
	public Timestamp date_time;
	public String module_name;
	public String message_type;
	public Hashtable<String, Object> messageInfo = new Hashtable<String, Object>();
}
