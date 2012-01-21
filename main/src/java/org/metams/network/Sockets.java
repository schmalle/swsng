package org.metams.network;

import java.io.*;
import java.net.*;

public class Sockets 
{
	private DatagramSocket 	m_udpSocket = null;
	private Socket			m_tcpSocket = null;
	private String 			m_type = "udp";	
	private boolean			m_verbose = false; 
	private String			m_host = "localhost";
	
	/*
	 * closes the opened socket
	 */
	public void close() throws IOException
	{
		if (m_udpSocket != null)
			m_udpSocket.close();
		if (m_tcpSocket != null)
			m_tcpSocket.close();
	}	// close
	
	/*
	 * sends given data to a dedicated server
	 * @param host to be addressed
	 * @param port to be addressed
	 * @param packet - data to be send 
	 */
	public void send(String host, int port, byte[] packet) throws IOException
	{
		
		if (m_verbose) 
		{
			System.out.println("Sockets.send(): to host "+host+" with protocol "+m_type);
		}
		
		if (m_type.equalsIgnoreCase("tcp"))
		{
			
		}
		else if (m_type.equalsIgnoreCase("udp"))	
		{
			    InetAddress address = InetAddress.getByName(host);
			    DatagramPacket packetOut = new DatagramPacket(packet, packet.length, address, port);
			    m_udpSocket.send(packetOut);
			
		}
		else if (m_verbose)
		{
			System.out.println("Sockets.send: Unkown protocol used...");
		}
		
		
	} // send
	
	
	public String recieve() throws IOException
	{
	    byte[] buf = new byte[16384];
	    DatagramPacket packet = new DatagramPacket(buf, buf.length);
	    if (m_type.equals("udp"))
	    {
	    	//m_udpSocket.se
	    	
	    	m_udpSocket.receive(packet);
	    }
	    else
	    	return null;
	    
	    byte[] inData= packet.getData();
	    return new String(inData);
	    
	    //return packet.getData().toString();
	    
	  }	// recieve


	
	
	/*
	 * constructor for the Sockets class
	 * @param port - port to be used
	 * @param type - UDP / TCP
	 * @param ver  - flag for boolean
	 * @param host
	 */
	public Sockets(int port, String type, boolean ver, String host, int delay)
	{
		
		// bogus default code
		if (type != null)
			m_type = type.toLowerCase();
		
		if (host == null)
			m_host = "localhost";
		else
			m_host = host;
		
		m_verbose = ver;
		
		try
		{
					
			if (type.equalsIgnoreCase("udp"))
			{
				m_udpSocket = new DatagramSocket(port);
				m_udpSocket.setSoTimeout(delay);			
			}
			else if (type.equalsIgnoreCase("tcp"))
				m_tcpSocket = new Socket(m_host, port);
			else
				System.out.println("Sockets::Sockets: Error, no valid type for socket found !");
			
			
		}	// try block
		catch (Exception e)
		{
			if (m_verbose)
				e.printStackTrace();
		}
	
	}	// sockets 

}
