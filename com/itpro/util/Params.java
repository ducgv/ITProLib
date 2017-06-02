/**
 * 
 */
package com.itpro.util;

import java.util.Hashtable;


/**
 * @author Giap Van Duc
 *
 */
public class Params {
	public boolean isLoaded = false;
	public Object mutex = new Object();
	public Hashtable<String, String> list = new Hashtable<String, String>();
	public String getParam(String param){
		synchronized (mutex) {			
			return list.get(param);			
		}		
	}
	public void setParam(String param, String value){
		synchronized (mutex) {			
			list.remove(param);
			list.put(param, value);
		}		
	}
}
