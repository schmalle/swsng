package org.metams.swsng;

import java.io.*;
import java.net.*;




public class BannerGrab
{
	
	// scanner array for a complete c class network
	private Scanner m_scanner[] = new Scanner[256];
	
	/*
	 * returns the result string
	 */
	public String getOutputAsString()
	{
		String result = "";
		
		for (int runner = 0; runner <= 255; runner++)
		{
			if (m_scanner[runner].gotAnswer())
				result = result.concat(m_scanner[runner].getOutput());
		}
		
		return result;
	} // getOutput

	/*
	 * returns the result string
	 */
	public String getOutputAsHtmlString()
	{
		String result = "";
		
		for (int runner = 0; runner <= 255; runner++)
		{
			if (m_scanner[runner].gotAnswer())
			{
				result = result.concat(m_scanner[runner].getOutput());
				result = result.concat("<br>");
			}
		}
		
		return result;
	} // getOutput
	
	
	
	// private class for a scanner
	private class Scanner extends Thread
	{
		private String 						m_packet = "";
		private org.metams.network.Sockets 	m_socket = null;
		private int 						m_port = 5060;
		private String 						m_destination = null;
		private String						m_result = null;
	
		/*
		 * returns the result string
		 */
		public String getOutput()
		{
			return m_result;
		} // getOutput
		
		/*
		 * returns true, if the tested ip gave any form of answer
		 * @out: boolean
		 */
		public boolean gotAnswer()
		{
			return (m_result != null);
		}	// gotAnswer
		
		/**
		 * 
		 * @param socket
		 * @param port
		 * @param destination
		 * @param packet
		 */
		public Scanner(org.metams.network.Sockets socket, int port, String destination, String packet)
		{
			m_socket = socket;
			m_packet = packet;
			m_port = port;
			m_destination = destination;
			
		}	// constructor for the scanner class
		
		
		/*
		 * creates output based on the current ip and the result from the server
		 * @in: ip - currently scanned ip
		 * @in: result - result from the server
		 * @out: final result or NULL in an error case  
		 */
		private String createOutput(String ip, String result)
		{
			
			if (ip == null)
			{
				return "<BOGUS IP FOR Bannergrabbing>";
			}
			
			if (result == null)
			{
				return ip+": no answer";
			}

			if (result == "")
			{
				return ip+": empty answer";
			}
		
			int uaStart = result.indexOf("User-Agent");
			if (uaStart == -1)
			{
								
				return ip+": none";
			}
			else
			{	// now parse
				int uaEnd = result.indexOf("\r", uaStart);
				if (uaEnd == -1)
				{
					uaEnd = result.indexOf("\n", uaStart);
				}
				
				if (uaEnd == -1)
					return ip+": unable to determine end of user agent";
				else
					return ip+": "+result.substring(uaStart, uaEnd);
				
			
			}	// UserAgent found
			
		}	// createOutput
		
		/**
		 * run Method to start the thread
		 */
	    public void run() 
	    {
	    	try 
	    	{
	    		//System.out.println("Info: Starting grabbing "+m_destination);
	    		m_socket.send(m_destination, m_port, m_packet.getBytes());	
				String result = m_socket.recieve();
				m_result = createOutput(m_destination, result);
				
				System.out.println(m_result);
	            
	        }
	        catch(Exception e) 
	        {
	        	m_result = null;
	        	//System.out.println("Error/Exception scanning ip: " + m_destination);
	        	//e.printStackTrace();
	        }
	    }	// run Methode

		
	}	// scanner class
	

	private org.metams.network.Sockets[] m_sockets = new org.metams.network.Sockets[256];
	private int		m_localPort = 5060;
	private String	m_destinationRange = null;
	private String	m_transport = "udp";
	private boolean verbose = false;
	private String  m_host = "localhost";
    private int		m_active = 0;
	
    /* ab hier wieder Bannergrab class !!! */
    
	private  String m_options = "OPTIONS sip:032 SIP/2.0\r\n" +
    				"Via: SIP/2.0/UDP xxx.xxx.xxx.xxx:xyz;branch=z9hG4bKnp1948369250-495fab4484.xxx.xxx.xxx;rport\r\n" +
    				"From: <sip:032@xxx.de>;tag=7421c168\r\n" +
    				"To: <sip:032@xxx.de>\r\n" +
    				"Call-ID: 1948961188-49db43b8@1948961192-49db43b4\r\n" +
				    "Contact: <sip:032@xxx.xxx.xxx.xxx:5070;line=0e3e8f6bf272f7d4>;expires=600\r\n" +
				    "Expires: 600\r\n" +
				    "CSeq: 1 OPTIONS\r\n" +
				    "Content-Length: 0\r\n" +
				    "Max-Forwards: 70\r\n" +
				    "User-Agent: None\0\0";


	/*
	 * init all important data
	 */
	public void initialize(int localPort, int destinationPort, String destinationRange, String transport,String hostBind, int delay) throws InterruptedException
	{
		m_localPort = localPort;
		m_destinationRange = destinationRange;
		m_transport = transport;
		m_host = hostBind;
		m_active = 0;
		
		// instantiate all sockets and starts threats
		for (int runner = 0; runner <= 255; runner++)
		{
			String newDest = destinationRange.concat(".").concat(new Integer(runner).toString());
			
			m_sockets[runner] = new org.metams.network.Sockets(localPort+runner, m_transport, false, m_host, delay);
			m_scanner[runner] = new Scanner(m_sockets[runner], destinationPort, newDest, m_options);
			m_scanner[runner].start();
		}
		
		// wait two times the delay time for the socket
		Thread.sleep(delay + 1000);
		
		// collect all answers from evices
		for (int runner = 0; runner <= 255; runner++)
		{
			if (m_scanner[runner].gotAnswer())
				m_active++;
		}
		
		System.out.println("Scanned a total of 255 hosts, "+m_active+" hosts up");
	}
	
	/*
	 * empty constructor for the banner grab class
	 */
	public BannerGrab()
	{
		
	
	}	// BannerGrab constructor
		
}
