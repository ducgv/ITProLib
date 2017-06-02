/**
 * 
 */
package com.itpro.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;

import com.logica.smpp.util.ByteBuffer;

/**
 * @author Giap Van Duc
 *
 */
public class TCPConnection {
	
	private Socket socket = null;
	public int nSendTimeout = 100;
	public int nReceiveTimeout = 500;
	
	public String address = null;
	public int port = 0;
	
	public TCPConnection(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		address = socket.getInetAddress().getHostAddress();
        port = socket.getPort();
	}

	public TCPConnection(String address, int port) {
		this.address = address;
		this.port = port;	        
	}

	public void connect() throws IOException{
		try {
			socket = new Socket(address, port);            
		} catch (IOException e) {
			throw e;
		}
	}

	public boolean isConnected() {
		if(socket!=null)
			return socket.isConnected();
		else
			return false;
	}
	
	public void send(ByteBuffer buffer) throws IOException{
		OutputStream out;
		try {
			socket.setSoTimeout(nSendTimeout);
			out = socket.getOutputStream();
			try{
				out.write(buffer.getBuffer());				
			} catch (InterruptedIOException e) {				
				throw new IOException("Send data to socket timed out");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			throw e;			
		}		
	}
	
	public void send(byte[] buffer) throws IOException{
		OutputStream out;
		try {
			socket.setSoTimeout(nSendTimeout);
			out = socket.getOutputStream();
			try{
				out.write(buffer);				
			} catch (InterruptedIOException e) {				
				throw new IOException("Send data to socket timed out");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			throw e;			
		}		
	}
	
	public void close() {
		try {
            if(socket!=null){
            	socket.shutdownInput();
            	socket.shutdownOutput();
            	socket.close();
            }
            socket = null;
            
        } catch (IOException e) {
        	socket = null;
        }
	}
	
	public ByteBuffer receive() throws IOException{
		InputStream in;
		socket.setSoTimeout(nReceiveTimeout);
		in = socket.getInputStream();			
		byte buff[] = new byte[65535];
		try{			
			int nRead=in.read(buff);
			if(nRead>0){
				ByteBuffer buffer= new ByteBuffer();
				buffer.appendBytes(buff, nRead);
				return buffer;
			}
			else {
				throw new IOException("Read data from socket");
			}
		} catch (InterruptedIOException e) {
			//timeout
			return null;
		}
	}	
	
}
