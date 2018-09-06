/**
 * 
 */
package com.itpro.util;

import java.io.UnsupportedEncodingException;

/**
 * @author Giap Van Duc
 *
 */
public class StringFunctions {
	
	static final byte[] hex = {
		(byte)'0', (byte)'1', (byte)'2', (byte)'3',
		(byte)'4', (byte)'5', (byte)'6', (byte)'7',
		(byte)'8', (byte)'9', (byte)'a', (byte)'b',
		(byte)'c', (byte)'d', (byte)'e', (byte)'f'
	};    

	public static String getHexString(byte[] buff){
		byte[] hex = new byte[2 * buff.length];
		int index = 0;

		for (byte b : buff) {
			int v = b & 0xFF;
			hex[index++] = hex[v >>> 4];
			hex[index++] = hex[v & 0xF];
		}
		return new String(hex);
	}

	public static String getUTF8(byte[] ucs2) throws UnsupportedEncodingException{
		String ucs2String = new String(ucs2, "UTF-16");         
		String utf8String = new String(ucs2String.getBytes("UTF-8"));         
		return utf8String;  
	}
	
	public static boolean isAscii(String content) {
		for(int i=0;i<content.length();i++)
			if(content.charAt(i)>=0x80)
				return false;
		return true;
	}
	
	public static boolean isNumber(String str){
		for(int i=0;i<str.length();i++)
			if(str.charAt(i)<'0'||str.charAt(i)>'9')
				return false;
		return true;
	}
	
	public static boolean addrCompare(String csAddr,String csRule){
		int nRuleLen = csRule.length();
		int nLenAddr = csAddr.length();
		if(nRuleLen > nLenAddr)
			return false;
		for(int i=0;i<nRuleLen;i++){
			if(csRule.charAt(i)=='*')
				break;
			if(csRule.charAt(i)=='?')
				continue;
			if(csRule.charAt(i)!=csAddr.charAt(i))
				return false;
		}
		int nSub=nLenAddr-nRuleLen;
		for(int i=nRuleLen-1;i>=0;i--){
			if(csRule.charAt(i)=='*')
				break;
			if(csRule.charAt(i)=='?')
				continue;
			if(csRule.charAt(i)!=csAddr.charAt(i+nSub))
				return false;
		}
		return true;
	}
	
	public static String getNumberString(int number){
		return String.format("%,d", number);
	}
}
