package org.metams.swsng;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Crypto 
{
	
	private boolean m_verbose = false;
	
	/*
	 * constructor for the Crypto class
	 */
	public Crypto()
	{}
	
	/*
	 * constructor for the Crypto class
	 */
	public Crypto(boolean ver)
	{
		m_verbose = ver;
	}	// constructor
	
    /**
     *  Convenience method to convert a byte array to a hex string.
     *
     * @param  data  the byte[] to convert
     * @return String the converted byte[]
     */
    public static String bytesToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buf.append(byteToHex(data[i]).toLowerCase());
        }
        return (buf.toString());
    }

    /**
     *  Convenience method to convert an int to a hex char.
     *
     * @param  i  the int to convert
     * @return char the converted char
     */
    public static char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }    

    /**
     *  method to convert a byte to a hex string.
     *
     * @param  data  the byte to convert
     * @return String the converted byte
     */
    public static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data >>> 4) & 0x0F));
        buf.append(toHexChar(data & 0x0F));
        return buf.toString();
    }	
	
	
	/*
	 * returns a hash for a given algorithm
	 */
	public String getHash(String data, String algorithm)
	{
		
		if (data == null)
			return null;
		if (algorithm == null)
			return null;
		
		byte[] theTextToDigestAsBytes = data.getBytes();
        MessageDigest md;           
		
		try
		{
			md = MessageDigest.getInstance(algorithm.toUpperCase());
			md.update( theTextToDigestAsBytes );
			byte[] digest = md.digest();
			return bytesToHex(digest);
		}
		catch (Exception e)
		{
			if (m_verbose)
			{
				String error = "Info: " + e.toString();
				System.out.println(error);
			}
			return null;
		}
	}	// getHash


	/*
	 * creates response from given parameters
	 * @in Username
	 * @in Password
	 * @in Algorithm 
	 * @in Realm
	 */
    public String createResponse(String uName,
            String passWord,
            String alg, String realm, String nonce, String method, String opaque, String uri)
    {

    	alg = alg.toLowerCase();

    	if (m_verbose) System.out.println("Crypto.createResponse: Recieved realm: " + realm);

        String HA1 = getHash(uName+":"+realm+":"+passWord, alg);      
		String hta2 = getHash(method + ":" + uri, alg);
		hta2 = hta2.toLowerCase();
		
		// fixup
		String responseText = getHash(HA1 + ":" + nonce + ":" + hta2, alg);
        
    	return "Authorization: Digest username=\""+uName+"\",realm=\""+realm+"\",nonce=\""+
		 nonce+"\",response=\""+responseText+"\",uri=\""+uri+
		 "\",algorithm="+alg+",opaque=\""+opaque+"\"\r\n";

    }	// createResponse

    
     
}
