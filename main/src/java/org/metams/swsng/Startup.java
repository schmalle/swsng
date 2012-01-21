package org.metams.swsng;

import org.metams.utils.*;

public class Startup extends ProgramCore
{
	private String[] m_args = null;
	
	
    /**
     * prints out the usage
     */
    public void usage()
    {
        System.out.println("");
        System.out.println("SIP Workshop SIP sender...");
        System.out.println("(C) Markus \"Flake\" Schmall, 2007/2008\r\n");
        System.out.println("Following parameters are supported:\r\n\r\n");
        System.out.println("-t  transport protocol (UDP (default), TCP)");
        System.out.println("-s  sip proxy (default: tel.t-online.de");
        System.out.println("-f  file to be send");
        System.out.println("-l  local port (default 5060)");
        System.out.println("-d  remote port (default 5060)");
        System.out.println("-u  username");
        System.out.println("-p  password");
        System.out.println("-v  verbose");
        System.out.println("-c  enable \"REGISTER before INVITE\" mode");
        System.out.println("-x  do a proper deregister");
        System.out.println("-r  number of retries");
        System.out.println("-q  enable SIP stack mode (default off)");
        System.out.println("-k  sends an BYE right after SIP <n> response from server");
        System.out.println("-o  directory containing files to be send (overrides -f)");
        System.out.println("-b  xml control file");
        System.out.println("-w  delay for recieving socket (default 1000 ms)");
        System.out.println("-g  activate fuzzer mode");
        System.out.println("-y  class network network bannergrabber (e.g. 80.124.100)");
        System.out.println("-n  activate brute force mode and specify file");
        System.out.println("-na specifies additional alphabet file for bruteforcer");
        System.out.println("-nb defines minimal length for bruteforcer");
        System.out.println("-ne defines maximal length for bruteforcer");
        System.out.println("-z do bruteforcing");

    }   // usage


    /*
     * retrieve packet parameters from the commandline
     * @in: arguments from the commandline
     */
    public void handleCommandLine(String[] args)
    {

        GetOptions getOptions = new GetOptions();
        getOptions.getopt(args, "z?v?n:na:nb:ne:q?k:g?u:p:l:i:d:f:b:y:s:m:n:w:o:d:r:t:c?x?h?");

        if (getOptions.existsKey("z"))
        {
            m_doBruteForce = true;
        }
        if (getOptions.existsKey("t"))
        {
            m_transport = getOptions.getValue("t");
        }
        if (getOptions.existsKey("k"))
        {
            m_byeAfter = getOptions.getValue("k");
        }

        if (getOptions.existsKey("i"))
        {
            m_localIP = getOptions.getValue("i");
        }


        if (getOptions.existsKey("b"))
        {
            m_xmlFile = getOptions.getValue("b");
        }
        if (getOptions.existsKey("n"))
        {
            m_bruteFile = getOptions.getValue("n");
        }
        if (getOptions.existsKey("na"))
        {
            m_bruteFileAlphabet = getOptions.getValue("na");
        }
        if (getOptions.existsKey("nb"))
        {
            m_bruteMinLength = Integer.parseInt(getOptions.getValue("nb"));
        }
        if (getOptions.existsKey("ne"))
        {
            m_bruteMaxLength = Integer.parseInt(getOptions.getValue("ne"));
        }
        if (getOptions.existsKey("y"))
        {
            m_range = getOptions.getValue("y");
        }


        if (getOptions.existsKey("c"))
        {
            m_regBeforeInv = true;
        }

        if (getOptions.existsKey("r"))
        {
            m_retries = Integer.parseInt(getOptions.getValue("r"));
        }


        if (getOptions.existsKey("s"))
        {
            m_sipProxy = getOptions.getValue("s");
        }

        if (getOptions.existsKey("f"))
        {
            m_fileName = getOptions.getValue("f");
        }
        if (getOptions.existsKey("o"))
        {
            m_dirName = getOptions.getValue("o");
        }

        // fetch data from command line
        if (getOptions.existsKey("w"))
        {
            m_recieveDelay = Integer.parseInt(getOptions.getValue("w"));
        }

        // fetch data from command line
        if (getOptions.existsKey("l"))
        {
            m_localPort = Integer.parseInt(getOptions.getValue("l"));
        }

        if (getOptions.existsKey("d"))
        {
            m_remotePort = Integer.parseInt(getOptions.getValue("d"));
        }

        if (getOptions.existsKey("m"))
        {
            m_method = getOptions.getValue("m");
        }

        if (getOptions.existsKey("u"))
        {
            m_userName = getOptions.getValue("u");
        }
        if (getOptions.existsKey("p"))
        {
            m_password = getOptions.getValue("p");
        }
        if (getOptions.existsKey("v"))
        {
            m_verbose = true;
        }
        if (getOptions.existsKey("x"))
        {
            m_deregister = true;
        }
        if (getOptions.existsKey("q"))
        {
            m_stackMode = true;
        }
        if (getOptions.existsKey("g"))
        {
            m_fuzzMode = true;
        }
        if (getOptions.existsKey("h"))
        {
            usage();
        }

    }   // handleCommandLine
	
    
    /*
     * constructor for the startclass
     * @in: args - array of strings holding the input data
     */
    public Startup(String[] args)
    {
    	m_fileHandler = new org.metams.utils.FileHandler();
    	m_args = args;
    	handleCommandLine(args);
    }	// constructor for the Startup class
	
    
    /*
     * main working function for the Startup class 
     */
    public void work() throws InterruptedException
    {
     	
     	// read file, if not passed as parameter, just skip
    	m_fileContent = m_fileHandler.getFile(m_fileName);
   		SipStack sip = new SipStack(m_localPort, m_remotePort, m_sipProxy, m_userName, 
			    m_password, m_fileName, m_fileContent, null, m_verbose, 
			    m_regBeforeInv, m_stackMode, false/*oneAuthShot*/, m_deregister, m_byeAfter);
 
   		
   		if (this.m_xmlFile != null)
   		{
   			XmlParser xml = new XmlParser(m_localPort, m_remotePort, m_sipProxy, m_userName, m_password, m_verbose, m_xmlFile);
   			xml.run();
   			
   		}
    	
   		else if (m_fileContent != null && m_dirName == null)
    	{	// do the single file scan
    		
    		for (int runner = 0; runner <= m_retries; runner++)
    		{
    			sip.start(false);
    		}
    	}
    	
    	
    	else if (m_range != null)
		{
			// TODO Auto-generated method stub
			org.metams.swsng.BannerGrab bG = new org.metams.swsng.BannerGrab();
			bG.initialize(this.m_localPort, this.m_remotePort, m_range, m_transport, "localhost", m_recieveDelay);			
		}
    	else if (m_fuzzMode)
    	{   // 
    		
    		Fuzzer myFuzzer = new Fuzzer(m_fileContent, m_verbose);
    		
    		String sendData = myFuzzer.getFirstStep();
    		
    		while (sendData != null)
    		{
    			sendData = myFuzzer.getNextStep();
    		}
    	}
    	else if (m_doBruteForce)
    	{
    		String nonce = "ad942bb9ea639ad4ac2cae75160319666b735413b6a912c981919d8944bfe5db";
    		String response = "ff4eb237be82911828a43a61a506d38c";
    		String uri = "sip:tel.t-online.de";
    		String method = "register";
    		String realm = "tel.t-online.de";
    		
    		
    		m_userName = "markus-schmall";
    		/*
            Opaque Value: "ad942bb9ea639ad4ac2cae75160319666b735413b6a912c981919d8944bfe5db"   		  
    		 
    		 */
    		
    		// initiate brute forcer
    		BruteForce force = new BruteForce(nonce, response, m_userName, uri, method, m_bruteMinLength, m_bruteMaxLength, realm);
    	}
    		
    }	// work
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
	
		// initiate class the pass on the command line variables
		Startup myStartup = new Startup(args);
		
		
		if (args.length <= 1)
		{
			myStartup.usage();
			return;
		}
		
		try
		{
			myStartup.work();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
			
	}	// main

}
//TODO: XML parser
//TODO: Check Register before invite