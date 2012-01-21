package org.metams.utils;

import java.io.*;

public class FileHandler 
{
	
	private	boolean	m_verbose = false;
	
	/*
	 * constructor for the FileHandler class
	 * @param verbose flag to indicate, if verbose is on
	 * 
	 */
	public FileHandler(boolean verbose)
	{
		m_verbose = verbose;
	}	// FileHandler

	/*
	 * constructor for the FileHandler class
	 * 
	 */
	public FileHandler()
	{
		m_verbose = false;
	}	// FileHandler	
	
	/*
	 * fixes all line feeds, if only a singe 0x0a exists (bogus MacOS x error!)
	 * @in: data
	 * out: data
	 */
	private String fixLineFeed(String data)
	{
		String outData = "";
		int runner = 0;
		
		if (data == null)
			return null;
		
		while (runner <= data.length())
		{
			int indexStart = data.indexOf("\n", runner);
			if (indexStart == -1)
				return outData + data.substring(runner);

			if (indexStart != 0 && data.charAt(indexStart-1) != '\r')
			{
				outData = outData + data.substring(runner, indexStart) + "\r\n";
			}
			else
			{
				outData = outData + data.substring(runner, indexStart + 1);
			}
			
			runner = indexStart + 1;

		}
		
		
		
		return outData;
	}	// fixLineFeed
	
	
	/*
	 * reads the content from a given file
	 * @param fileName file to be loaded
	 * @return file content or null if any error appeared
	 */
	public String getFile(String fileName)
	{
		
		if (fileName == null)
			return null;
	
		if (m_verbose)
			System.out.println("Loading input data from file " + fileName);
		
		try
		{
			File fileMe = new File(fileName);
			FileInputStream in = new FileInputStream(fileName);
			long length = fileMe.length();
			byte[] buffer = new byte[(int)length];
			
			// read file and check for correct length
			if (in.read(buffer) != (int)length)
			{
				System.out.println("Info: Read bytes differ from filesize ("+length+")");
				return null;
			}
			
			return fixLineFeed(new String(buffer));
		}
		catch (Exception e)
		{
			System.out.println("Error: FileHandler.getFile() caught an exception");
			return null;
		}
	}	// getFile
}
