package org.metams.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/*
 

	<task>
    <fork>false</fork>
		<repeat>1</repeat>
		<stackmode>1</stackmode>
		<fileName>e:\source_vc\metasip\requests\register_test1_ok.txt</fileName>
		<resultstack>401,200,200</resultstack>
	</task>	

 
 
 */

public class XmlParser 
{

	private String		m_xmlFileName = null;
	private int			m_localPort =  5060;
	private int			m_remotePort = 5060;
	private String		m_sipProxy = "tel.t-online.de";
	private String		m_userName = null;
	private String		m_passWord = null;
	private	boolean 	m_verbose = false;
	private String		m_transport = "udp";
	private String  	m_outboundProxy = null;
	private List 		m_taskList = null;
	private Document 	m_dom = null;

	
	public class Task
	{
		private int		m_repeat = 0;
		private String	m_fileName = null;
		private int[]   m_expectedAnswers;
		private int		m_result = 200;
		private boolean m_stackMode = false;

		/*
		 * constructor for the task class
		 */
		public Task(int repeat, String fn, int[] answers, int result, boolean stack)
		{
			m_repeat = repeat;
			m_fileName = fn;
			m_expectedAnswers = answers;
			m_result = result;
			m_stackMode = stack;
		}
		
	}	// Task inner class
	

	/*
	 * constructor for the XmlParser class
	 * @in: xmlFile - name
	 */
	public XmlParser(int localPort, int remotePort, String sipProxy,  String userName, String password, boolean verbose, String xmlFile)
	{
		m_taskList = new ArrayList();
		m_xmlFileName = xmlFile;
		m_localPort = localPort;
		m_remotePort = remotePort;
		m_sipProxy = sipProxy;
		m_userName = userName;
		m_passWord = password;
		m_verbose = verbose;
	}	// XmlParser

	

	/*
	 * fetch all tasks within the array
	 */
	private void fetchTasks()
	{


		//get the root elememt
		Element docEle = m_dom.getDocumentElement();

		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("task");

		nl = nl.item(0).getChildNodes();

		// check, if a node is existing
		if (nl != null && nl.getLength() != -1)
		{
			// iterate through all elements
			for (int runner = 0; runner <= nl.getLength() -1 ; runner++)
			{
				Node nd = nl.item(runner);

				String nodeName = nd.getNodeName();
				String nodeValue = nd.getTextContent();

				if (nodeName.equalsIgnoreCase("username")) 
				{
					m_userName =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("password")) 
				{
					m_passWord =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("localport")) 
				{
					m_localPort = Integer.parseInt(nodeValue);
				}
				else if (nodeName.equalsIgnoreCase("remoteport")) 
				{
					m_remotePort = Integer.parseInt(nodeValue);
				}
				else if (nodeName.equalsIgnoreCase("transport")) 
				{
					m_transport =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("proxy")) 
				{
					m_sipProxy =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("outboundproxy")) 
				{
					m_outboundProxy =  nodeValue;
				}

				//System.out.println("Nodename: "+nd.getNodeName() + " Node value" + nd.getTextContent());

			}
		}

	}
	
	
	/*
	 * run method
	 */
	public void run() 
	{
		
		//parse the xml file and get the dom object
		parseXmlFile();
		
		fetchGlobalSettings();
		fetchTasks();
		
		//get each employee element and create a Employee object
		//parseDocument();
		
		//Iterate through the list and print the data
		//printData();
		
	}
	
	/*
	 * parses the xml file generically
	 */
	private void parseXmlFile()
	{
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		if (m_xmlFileName == null)
			return;
		
		try 
		{
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			m_dom = db.parse(m_xmlFileName);
			
		}
		catch(ParserConfigurationException pce) 
		{
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	
	/*
	 * parses the global settings 
	 * @out: true on success
	 */
	private boolean fetchGlobalSettings()
	{

		//get the root element
		Element docEle = m_dom.getDocumentElement();
		
		//get a nodelist of <employee> elements
		NodeList nl = docEle.getElementsByTagName("globalsettings");
		
		nl = nl.item(0).getChildNodes();
		
		// check, if a node is existing
		if (nl != null && nl.getLength() != -1)
		{
			// iterate through all elements
			for (int runner = 0; runner <= nl.getLength() -1 ; runner++)
			{
				Node nd = nl.item(runner);
				
				String nodeName = nd.getNodeName();
				String nodeValue = nd.getTextContent();
				
				if (nodeName.equalsIgnoreCase("username")) 
				{
					m_userName =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("password")) 
				{
					m_passWord =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("localport")) 
				{
					m_localPort = Integer.parseInt(nodeValue);
				}
				else if (nodeName.equalsIgnoreCase("remoteport")) 
				{
					m_remotePort = Integer.parseInt(nodeValue);
				}
				else if (nodeName.equalsIgnoreCase("transport")) 
				{
					m_transport =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("proxy")) 
				{
					m_sipProxy =  nodeValue;
				}
				else if (nodeName.equalsIgnoreCase("outboundproxy")) 
				{
					m_outboundProxy =  nodeValue;
				}
				
				//System.out.println("Nodename: "+nd.getNodeName() + " Node value" + nd.getTextContent());
				
			}
		}
		
		return true;
	}
	

}
