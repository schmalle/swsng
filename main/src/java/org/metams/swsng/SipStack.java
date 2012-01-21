package org.metams.swsng;

import org.metams.*;
import org.metams.network.*;
import org.metams.utils.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SipStack extends SipStackCore
{

	/*
	 * returns the SIP mode from a given packet
	 * @param packet  packet data 
	 * 
	 * @return <code>sipcode</code> sip code
	 */
	private	String getSipMode(String packet)
	{
		if (packet == null)
			return null;
		
		if (packet.toUpperCase().startsWith(SIP_MODE_REG))
			return SIP_MODE_REG;
		
		if (packet.toUpperCase().startsWith(SIP_MODE_INV))
			return SIP_MODE_INV;

		if (packet.toUpperCase().startsWith(SIP_MODE_OPT))
			return SIP_MODE_OPT;

		if (packet.toUpperCase().startsWith(SIP_MODE_PUB))
			return SIP_MODE_PUB;
		
		return null;
	}
	
	
	/*
	 * function area
	 */
	
	
	/*
	 * constructor for the SipStack class
	 * @param packet  packet data 
	 * 
	 * @param lp local port for binding
	 * @param rp remote port
	 * @param p proxy top be used
	 * @param u username for authentication
	 * @param pw password
	 * @param fN fileName
	 * @param FC content to be send
	 * @param dn directory name
	 * @param verbose verbose flake
	 * @param registerBeforeInvite flag to do a register before invite
	 * @param fullStack flag to enable full stack operation
	 * @param oneAuthShot 
	 * @param dereg reregister flag at the end 
	 * @param send a bye request after another SIP packet was recieved 
	 */
	public SipStack(int lp, int rp, String p, String u, String pw, String fN, String fC, String dN, boolean verbose, 
				    boolean registerBeforeInvite, boolean fullStack, boolean oneAuthShot, boolean dereg, String byeAfter)
	{
		m_fileNameContent = fC;
		m_fileName = fN;
		m_dirName = dN;
		m_localPort = lp;
		m_destinationPort = rp;
		m_proxy = p;
		m_userName = u;
		m_password = pw;
		m_verbose = verbose;
		m_regBeforeInv = registerBeforeInvite;
		m_fullStack = fullStack;
		m_oneAuthShot = oneAuthShot;
		m_deregister = dereg;
		m_byeAfter = byeAfter;
	}	// constructor for the class
	
	
	/*
	 * main method of the sip stack
	 * @param servermode defines, if we run in servermode
	 * @return <code>-1</code> error
	 * @return <code>0>code> success
	 */
	public int start(boolean serverMode)
	{
		
		if (m_fileNameContent != null && m_dirName == null)
		{	// start single file attack
			if (m_verbose) System.out.println("Info(SipStack.start()): Using content from "+m_fileName);
			return startOneStep(serverMode, null);
		}
		else if (m_dirName != null && m_fileNameContent != null)
		{
			if (m_verbose) System.out.println("Info(SipStack.start()): Both filename and dirname found. Using content from "+m_fileName);
			return startOneStep(serverMode, null);
		}
		else if (m_dirName != null && m_fileName == null)
		{
			if (m_verbose) System.out.println("Info(SipStack.start()): Starting to throw all files from "+m_dirName+ " at proxy "+m_proxy);
			return startDirAttack();
		}
		
		
		return -1;
	}	// start method
	
	/*
	 * start routine for a set of "one file" attacks
	 */
	private int startDirAttack()
	{
		
	/*	while (true)
		{
			startOneStep(false);
		}
		*/
		return -1;
	}
	
	/*
	 * returns the CSeq from a given packet
	 * @param packet data to be analyse
	 * @return <code>-1<code>  error case
	 * @return <code>0<code> success
	 */
	private int getCSeq(String packet)
	{
		if (packet == null)
			return -1;
		
		if (packet.indexOf(SIP_CSEQ) == -1)
			return -1;
		
		int indexStart = packet.indexOf(SIP_CSEQ) + SIP_CSEQ.length();
		// indexStart = packet.indexOf(" ", indexStart);
		int indexEnd = packet.indexOf(" ", indexStart);
		if (indexEnd >= indexStart )
		{
			if (m_verbose) System.out.println("SipStack.getCSeq: CSeq is " + packet.substring(indexStart, indexEnd));
			
			return new Integer(packet.substring(indexStart, indexEnd)).intValue();
		}
		else
			return -1;
		
	}	// getCSeq

	
	/*
	 * returns the CSeq injected packet
	 * @out: packet in error case
	 */
	private String setCSeq(String packet, int cseq)
	{
		if (packet == null)
			return null;
		
		if (packet.indexOf(SIP_CSEQ) == -1)
			return packet;
		
		int indexStart = packet.indexOf(SIP_CSEQ) + SIP_CSEQ.length();
		int indexEnd = packet.indexOf(" ", indexStart);
		if (indexEnd >= indexStart )
		{
			String cseqString = new Integer(cseq).toString();
			return packet.substring(0, indexStart)+cseqString+packet.substring(indexEnd);		
		}
		else
			return packet;
		
	}	// setCSeq	
	
	/*
	 * returns the SIP code from a given packet
	 * @in:  packet
	 * @out: sipcode
	 */
	private int getSipCode(String data)
	{
		if (data == null)
			return 0;
		
		if (!data.startsWith(SIP_CS))
		{
			return 0;
		}	
		
		// calculate endvalue
		int indexEnd = data.indexOf(' ', SIP_CS.length());
		if (indexEnd != -1)
		{
			return new Integer(data.substring(SIP_CS.length(), indexEnd)).intValue();
		}

		return 0;
		
	} // getSipCode
	
	
	/*
	 * check the condiation, that a register must exists before an inviute
	 * @param sendData data to be send
	 * @param serverMode flag indicating the working mode
	 * @param mySocket Socket to be used for sending data
	 * @return <code>string</code>  code to be send
	 */
	private void handleRegisterBeforeInvite(String sendData, boolean serverMode, Sockets mySocket)
	{
		
		// TODO: check if user is already registered
		
		// check, if we have a register before invite condition
		if (m_regBeforeInv && SIP_MODE_INV == getSipMode(sendData.toUpperCase()))
		{
			if (this.m_verbose)
				System.out.println("handleRegisterBeforeInvite(): Starting register mode...");

		    // copy 
			String inviteData = m_fileNameContent;
			int localCSeq = getCSeq(m_fileNameContent);
			
			m_fileNameContent = replaceSipMode(sendData, "REGISTER");
			m_fileNameContent = fixURIFromInviteToRegister(m_fileNameContent);
			
			// do the SIP REGISTER handling
			startOneStep(serverMode, mySocket);
			
			// reconstruct old data
			m_fileNameContent = setCSeq(inviteData, localCSeq+3);
		}
		else
		{
			if (this.m_verbose)
				System.out.println("handleRegisterBeforeInvite(): Conditions not fullfilled...");
		}
		
	}	// handleRegisterBeforeInvite
	
	
	/*
	 * core routine to handle SIP related actions
	 * 
	 * @param servermode 		flag to signal, if the routine was called in servermode
	 * @param defaultSocket     initialized Sockets variable to be used for Sip handling
	 * @return <code>0</code>   return code, 0 on ok 
	 * @return <code>-1</code>  return code, -1 on error 
	 */
	private int startOneStep(boolean serverMode, Sockets defaultSocket)
	{
		int sipCode = 0, prevSipCode = 0, loopRunner = 0;
		String answer = null;
		int returnCode = 0;
		
		// assign socket and break of, if needed
		Sockets mySocket = defaultSocket;
		if (defaultSocket == null)
		{
			mySocket = new Sockets(m_localPort, "udp", m_verbose, "localhost", 1000);
		}
		
		 
		// check, if we have to register the user before we start the invitation
		// TODO: global status must be visible
		handleRegisterBeforeInvite(m_fileNameContent, serverMode, mySocket);
		
		String sendData = m_fileNameContent;
		
		// loop through the SIP processes as long as necessaey
		while (sipCode != 200 && loopRunner++ != 10 && sendData != null && returnCode == 0)
		{
			// show verbose information
			if (m_verbose) System.out.println("Info:\r\nSending data: "+sendData+"\r\n");
			
			try
			{
				// special check for call issues
				if (sipCode != 100 && sipCode != 183)
				{
					m_cseq = getCSeq(sendData);
					mySocket.send(m_proxy, m_destinationPort, sendData.getBytes());
				}
				
				answer = mySocket.recieve();
			}
			catch (SocketTimeoutException e)
			{
				answer = "<NONE>";
				returnCode = -1;
			}
			catch (Exception e)
			{
				if (m_verbose) System.out.println("Exception: startOneStep().. Beaking off");
				returnCode = -1;
				answer = "<NONE>";
				
			}
			
			// check if the server answered, if not break down
			if (answer == null)
				returnCode = -1;
			
			prevSipCode = sipCode;
			sipCode = getSipCode(answer);
			
			if (m_verbose)
			{
				System.out.println("Received answer: "+answer);
				System.out.println("Old Sip Code: "+prevSipCode+" New Sip Code: "+sipCode);
			}
	
			switch (sipCode)
			{
				case 200:
				{
					handleSipCode200(sendData);					
					break;
				}
				
				case 486: // busy
				{
					loopRunner = EXIT;
					break;
				}

				case 401: // unauthorized
				{
					sendData = checkUnAuthorizedCase(sendData, answer, sipCode, prevSipCode, mySocket);			
					break;
				}
			}	
			
			
			// handle the byeafter case 
			sendData = handleByeAfter(sipCode, sendData, mySocket);
						
		}	// while sipCode != 200 && loopRunner != 10
		
		// check for possible deregister actions after going down
		if (this.m_deregister)
		{
			handleDeRegister(mySocket);
		}
		
		// clean up of generated sockets
		if (defaultSocket == null)
		{
			try
			{
				mySocket.close();
			}
			catch (Exception e)
			{
				returnCode = -1;
			}
		}
		
		return returnCode;
	}	// startOneStep
	
	
	/*
	 * handles the deregistration of a client
	 */
	private void handleDeRegister(Sockets mySocket)
	{
		if (this.m_regPacketOk == null)
			return;
		
		if (m_verbose)
		{
			System.out.println("SipStack.handleDeRegister: Reregistering packet");
		}
		
		int cseq = getCSeq(this.m_regPacketOk) + 100;
		String finalPacket = this.setCSeq(m_regPacketOk, cseq);
		finalPacket = setExpires(finalPacket, 0);

		try
		{
			mySocket.send(m_proxy, m_destinationPort, finalPacket.getBytes());
			String answer = mySocket.recieve();
			
			if (m_verbose)
			{
				System.out.println("SipStack.handleDeRegister: Answer from server + "+answer);
			}
			
			
		}
		catch (Exception e)
		{
			System.out.println("Exception: handleDeRegister()");
			return;
		}
	
		
		// reset "Expires: " field
		
	}	// handleDeRegister
	
	
	/*
	 * sets a new expires value
	 * @in: packet - packet to be worked with
	 * @in: val - time to live
	 * @out: final packet
	 */
	private String setExpires(String packet, int val)
	{
		// dummy check
		if (packet == null)
			return packet;
		
		// check line start and break out if necessary
		int lineStart = packet.toLowerCase().indexOf("expires: ");
		if (-1 == lineStart)
		{
			// expires is not existing, check for CSeq: 
			lineStart = packet.toLowerCase().indexOf("cseq: ");
			if (-1 == lineStart)
				return null;
		}
		
		// calculate valid end
		int lineEnd = packet.indexOf("\r\n", lineStart);
		if (lineEnd == -1)
			return packet;
		
		// construct final packet
		packet = packet.substring(0, lineStart) +" Expires: "+val+packet.substring(lineEnd);
		
		// return packet
		return packet;
	}	// setExpires
	
	/*
	 * creates a BYE packet, if the "SEND A BYE packet after x" mode is activetd
	 * @in: sipCode - sipcode 
	 * @in: sendData - data to be send/received
	 * @out: final string
	 * 
	 */
	private String handleByeAfter(int sipCode, String sendData, Sockets mySocket)
	{
		// dummy check, if the option is set at all
		if (this.m_byeAfter == null)
			return sendData;
		
		String strSipCode = new Integer(sipCode).toString();
		if (!this.m_byeAfter.equals(strSipCode))
			return sendData;
		
		// increase CSEQ and replace current SIP mode with an ACK
		int cseq = getCSeq(sendData) + 1;
		sendData = replaceSipMode(sendData, "BYE ");
		sendData = setCSeq(sendData, cseq);
		
		try
		{
			mySocket.send(m_proxy, m_destinationPort, sendData.getBytes());
		}
		catch (Exception e)
		{
			
		}
		
		return null;
		
	}	// handleByeAfter
	
	/*
	 * replaces the current SIP mode with a given string
	 * @in: sendData - data packet
	 * @in: mode - current data mode
	 * @out: null in error code or final data 
	 */
	private String replaceSipMode(String sendData, String mode)
	{
		// return original senddata, if mode is null
		if (mode == null)
			return null;
		
		// return null, if the current data packet is null
		if (sendData == null)
			return null;
		
		// check for a valid index
		int index = sendData.indexOf(" ");
		if (index == 1)
			return null;
	
		// new mode, now fix the CSeq: portion
		mode = mode.concat(sendData.substring(index));
		
		index = sendData.toLowerCase().indexOf("cseq:");
		if (index == -1)
			return mode;
		
		return mode;
	}	// replaceSipMode

	
	
	
	/*
	 * generates a fully authenticated new register reuqest
	 * @param sendData data to be send
	 * @param answer (answer from server)
	 * @param sipCode current sipcode
	 * @param oldsipcode
	 * @param mySocket initialized socket handler
	 * 
	 * @returns <code>string</code> new packet
	 */
	private String checkUnAuthorizedCase(String sendData, String answer, int sipCode, int prevSipCode, Sockets mySocket)
	{
		// look at register case
	
		if (sendData == null)
			return null;
		
		if (/*prevSipCode == 0 && */sipCode == 401 && sendData.toUpperCase().startsWith(SIP_MODE_REG))
		{
		
			if (m_verbose) System.out.println("Info: Working with response: "+answer);
		
			sendData = handleAuthentication(answer, sendData);
		}
		else if (/*prevSipCode == 0 && */sipCode == 401 && sendData.toUpperCase().startsWith(SIP_MODE_INV))
		{
		
			if (m_verbose) System.out.println("Info: Working with response: "+answer);
		
			sendData = handleAuthentication(answer, sendData);
			
			// generate ACK packet
			String ackData = getAckData(sendData);
			
			try
			{
				mySocket.send(m_proxy, m_destinationPort, ackData.getBytes());
			}
			catch (Exception e)
			{
				
			}
			
			
		}
		else
		{
			if (m_verbose) System.out.println("Info(401): SIPCode" + new Integer(sipCode).toString()+ " PreSipCode: "+new Integer(prevSipCode).toString()+ "SendData "+sendData);			
		}
		
		
		
		return sendData;
	} // checkUnAuthorizedRegisterCase
	
	
	/* 
	 * generate ACK packet from a given data packet
	 * @param sendData data
	 * @returns <code>string</code> 
	 */
	private String getAckData(String sendData)
	{
		sendData = replaceSipMode(sendData, "ACK");
		return sendData;
	}	// getAckData

	
	
	
	/*
	 * extracts data from a given packet
	 * @in: data to be parsed
	 * @in: searchavlue
	 * @in: searchEnd
	 * @out: extracted data or NULL
	 *
	 */
	private String extract(String data, String searchValue, String endValue)
	{
		// perform dummy check
		if (data == null || searchValue == null || endValue == null)
			return null;
		
		// basic check for existance, simple, lazy
		int start = data.toUpperCase().indexOf(searchValue.toUpperCase());
		if (start == -1)
			return null;
		
		// fix length of search value to correct correct snip out thingie
		start += searchValue.length();
		
		// calculate endindex
		int endIndex = data.toUpperCase().indexOf(endValue.toUpperCase(), start);
		if (endIndex == -1 || endIndex <= start)
			return null;
		
		return data.substring(start,endIndex);
	}	// extract
	
	/*
	 * returns the NONCE
	 */
	private String getNonce(String in)
	{
		return extract(in, SIP_NONCE+"\"", "\"");
	}	// getNonce

	/*
	 * returns the Realm
	 */
	private String getRealm(String in)
	{
		return extract(in, SIP_REALM+"\"", "\"");
	}	// getRealm
	
	
	
	/*
	 * returns the OPAQUE
	 */
	private String getOpaque(String in)
	{
		return extract(in, SIP_OPAQUE+"\"", "\"");
	}	// getNonce
	
	/*
	 * returns the Algorithm for encrytion
	 */
	private String getAlg(String in)
	{
		String back =  extract(in, SIP_ALG, ",");
        if (back == null)
            return "MD5";

        return back;
	}	// getNonce
	
	
	/*
	 * creates a new packet valid for authentication an SIP request
	 * @param answer  answer from server
	 * @in: sendData - last send data
	 * @out: new string
	 */
	private String handleAuthentication(String answer, String sendData)
	{
		Crypto myCrypto = new Crypto(m_verbose);
		
		
		// increase cseq by one
		sendData = setCSeq(sendData, m_cseq +1);
		
		String response = myCrypto.createResponse(m_userName, m_password, 
												  getAlg(answer), getRealm(answer), getNonce(answer), 
												  getSipMode(sendData.toUpperCase()),
                							      getOpaque(answer), "sip:choochee467.register.prod.choochee.com");
		//										  getOpaque(answer), "sip:tel.t-online.de");

        //TODO generic fix for authentication fix
		
		// insert reponse before "Content-Length"
		return insertText(sendData, response, "Content-Length");

	}	// handleRegisterAuthentication
	
	/*
	 * inserts a given text before a dedictaed label and returns the newly constructed string
	 */
	private String insertText(String sendData, String toBeInserted, String before)
	{
		if (sendData == null || toBeInserted == null || before == null)
			return null;
		
		int index = sendData.indexOf(before);
		if (index == -1)
			return null;
		
		// construct final string and exit
		return sendData.substring(0, index) + toBeInserted + sendData.substring(index);
		
	}	// insertText
	
	/*
	 * constructor for the sip stack class
	 */
	public SipStack()
	{
		
	}	// empty constructor for lazyness
	
	/*
	 * 
	 * 
	 * 
	 * 
	 */
	
	/*
	 * fixes the top line URI from a INVITE request to a REGISTER request
	 * and fixes FROM and TO lines
	 * @param data SIP packet to be corrected
	 * 
	 * @return <code>string</code> corrected SIP packet
	 * @return <code>null</code> error case
	 */
	private String fixURIFromInviteToRegister(String data)
	{		
		if (data == null)
			return null;
		
		int start = data.toLowerCase().indexOf("sip:");
		int end = data.indexOf("@", start);
		
		if (start == 1 || end == -1)
			return null;
				
		data = (data.substring(0, start+4) + data.substring(end+1));
		
		// now fixing
		//From: "+4961519519046"<sip:+4961519519046@tel.t-online.de>
		//To: "04961516803824"<sip:04961516803824@tel.t-online.de>;tag=e0f84d13

		start = data.toLowerCase().indexOf("from: ");
		end   = data.indexOf("\r\n", start);
		if (start == 1 || end == -1)
			return null;

		String fromData = data.substring(start + 6, end);
		
		start = data.toLowerCase().indexOf("to: ");
		end   = data.indexOf("\r\n", start);
		if (start == 1 || end == -1)
			return null;
		
		data = data.substring(0, start + 4) + fromData + data.substring(end);

		if (data.toLowerCase().indexOf("expires:") == -1)
		{
			data = data.substring(0, start) + "Expires: 3600\r\n" + data.substring(start);
		}
		
		// remove all possibly existing values for SDP packets
		end = data.indexOf("Content-Length:");
		data = data.substring(0, end) + "Content-Length: 0\r\n\r\n";
		
		// construct final string and return data
		return data;
		
	} // fixURIFromInviteToRegister
	
	/*
	 * stores the correct send packet for a register/invite operation
	 * @param sendData data send to the server
	 * 
	 */
	private void handleSipCode200(String sendData)
	{
		// check for correct sip code
		if (sendData != null && getSipMode(sendData.toUpperCase()).equals(SIP_MODE_REG))
		{
			m_regPacketOk = sendData;
		}
		else if (sendData != null && getSipMode(sendData.toUpperCase()).equals(SIP_MODE_INV))
		{
			m_invPacketOk = sendData;
		}
		
	}	// handleSipCode200

}
