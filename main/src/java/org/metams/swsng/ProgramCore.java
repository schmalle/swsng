package org.metams.swsng;

import org.metams.utils.*;

public class ProgramCore 
{
    public int          m_recieveDelay = 1000;
    public int          m_localPort = 5060;
    public int          m_remotePort = 5060;
    public String       m_sipProxy = "tel.t-online.de";
    public String       m_nonce = "fad476e5bef138c3fad468dfeb78f068abde8a29ff1d88c70036765fa83dc8ee";
    public String       m_xmlFile = null;
    public String       m_userName = null;
    public boolean      m_regBeforeInv = false;
    public String       m_password = null;
    public String       m_fileName = null;
    public String       m_method = null;
    public boolean      m_verbose = false;
    public boolean      m_deregister = false;
    public boolean      m_doBruteForce = false;
    public String       m_transport = "udp";
    public int          m_retries = 0;
    public boolean      m_stackMode = false;
    public String       m_dirName = null;
    public String       m_range = null;
    public String       m_bruteFile = null;
    public boolean      m_fuzzMode = false;
    public int          m_bruteMinLength = 4;
    public int          m_bruteMaxLength = 8;
    public String       m_bruteFileAlphabet = null;
    public String       m_byeAfter = null;
    public String       m_localIP = null;
    public String 		m_fileContent = null;
    public FileHandler	m_fileHandler = null;
    
}