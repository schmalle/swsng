package org.metams.swsng;

public class SipStackCore 
{
	
	public static String  SIP_CS = "SIP/2.0 ";
	public static String  SIP_MODE_REG = "REGISTER";
	public static String  SIP_MODE_OPT = "OPTIONS";
	public static String  SIP_MODE_PUB = "PUBLISH";
	public static String  SIP_MODE_INV = "INVITE";
	public static String  SIP_CSEQ = "CSeq: ";
	public static String  SIP_ALG = "Digest algorithm=";
	public static String  SIP_NONCE = "nonce=";
	public static String  SIP_OPAQUE = "opaque=";
	public static String  SIP_REALM = "realm=";
	public static int     EXIT = 9; 
	
	
	/*
	 * variable area
	 */
	
	public int		m_localPort = 6000;
	public int 		m_destinationPort = 5060;
	public String	m_byeAfter = null;
	public String	m_proxy	= "tel.t-online.de";
	public String	m_userName = null;
	public String	m_password = null;
	public String	m_fileName = null;
	public String	m_fileNameContent = null;
	public String	m_dirName = null;
	public int		m_cseq = 1;
	public boolean 	m_verbose = false;
	public boolean 	m_regBeforeInv = true;
	public boolean 	m_fullStack = false;
	public boolean 	m_oneAuthShot = true;
	public boolean 	m_deregister = false;
	
	public String  	m_regPacketOk = null;
	public String  	m_invPacketOk = null;
	

}
