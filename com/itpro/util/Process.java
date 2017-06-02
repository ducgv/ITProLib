/**
 * 
 */
package com.itpro.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author ducgv
 *
 */
public class Process {
	public static String Exec(String cmd) throws IOException, InterruptedException{
		Runtime run = Runtime.getRuntime();
		java.lang.Process pr;
		pr = run.exec(cmd);
		pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		String line1;
		while ((line1=buf.readLine())!=null) {
			line+=line1+"\n";
		}
		line = line.substring(0,line.length()-1);
		return line;
	}
}
