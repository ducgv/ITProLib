/**
 * 
 */
package com.itpro.cli;

import java.util.Enumeration;
import java.util.Hashtable;

import com.itpro.util.Queue;

/**
 * @author Giap Van Duc
 *
 */
public class CmdRequest {
	public Queue queueResp;
	public String cmd;
	
	public Hashtable<String, String> params = new Hashtable<String, String>();
	public Hashtable<String, String> result = new Hashtable<String, String>();
	
	public String toString(){
		String sResult=cmd+":";
		Enumeration<String> keys = params.keys();		
		String sParam="";		
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = params.get(key);
			sParam+=", "+key+"="+value;				
		}		
		if(!sParam.equals(""))
			sParam=sParam.substring(1);
		sResult+=sParam;
		return sResult;
	}
	public String getRespString(){
		String sResult=cmd+"Resp:";
		Enumeration<String> keys = result.keys();		
		String sParam="";		
		while (keys.hasMoreElements()){
			String key = keys.nextElement();
			String value = result.get(key);
			sParam+=","+key+"="+value;				
		}		
		if(!sParam.equals(""))
			sParam=sParam.substring(1);
		sResult+=sParam;
		return sResult;
	}
}
