package org.metams.utils;

public class Fuzzer
{

	private String		m_template = null;
	private boolean 	m_verbose = false;
	
	// define maximal sums
	private int			m_repeatFuzzSum = 3; // same line 1 times up to 3 three times
	private int 		m_decimalFuzzSum = 256;
	private int         m_asciiFuzzSumSingle = 52;
	private int         m_asciiFuzzSum = 52*3;
	private int			m_removeFuzzSum = 2; // binary 1 or 0
	private int			m_runner = 0;
	private int			m_markerRunner = 0;
	private int         m_permutationArray[] = null;
	private int         m_permutationArrayMaxValues[] = null;
	
	
	/*
	 * initializes all internal structures 
	 */
	public void setupFuzzer()
	{
		
		m_permutationArray = new int[getNumberOfPermutationMarkers()];
		m_permutationArrayMaxValues = new int[getNumberOfPermutationMarkers()];
		for (int runner = 0; runner <= getNumberOfPermutationMarkers() - 1 && getNumberOfPermutationMarkers() != 0 ; runner++)
		{
			m_permutationArray[runner] = 0;
		}
	}	// setupFuzzer
	

	/*
	 * returns the total number of permutations possibilites 
	 * @out: #number of possibilities
	 */
	public int getNumberOfPermutationMarkers()
	{
		if (m_template == null)
			return 0;
		
		if (!m_template.contains("[_"))
			return 0;
		
		return getNumberOfAsciiPermutationsSingle(m_template) + getNumberOfAsciiPermutations(m_template) +
		getNumberOfDecimalPermutations(m_template) + getNumberRepeatPermutations(m_template) +
		getNumberOfRemovePermutations(m_template);
	}	// getNumberOfPermutationMarkers	
	
	
	/*
	 * returns the total number of permutations possibilites 
	 * @out: #number of possibilities
	 */
	public int getNumberOfSteps()
	{
		if (m_template == null)
			return 0;
		
		if (!m_template.contains("[_"))
			return 0;
		
		return m_asciiFuzzSumSingle * getNumberOfAsciiPermutationsSingle(m_template) +
		m_asciiFuzzSum * getNumberOfAsciiPermutations(m_template) +
		m_decimalFuzzSum * getNumberOfDecimalPermutations(m_template) +
		m_repeatFuzzSum * getNumberRepeatPermutations(m_template) +
		m_removeFuzzSum * getNumberOfRemovePermutations(m_template);
	}	// getNumberOfSteps
	
	/*
	 * resets the runner
	 */
	public void resetRunner()
	{
		m_runner = 0;
		m_markerRunner = 0;
	}	// resetRunner
	
	public Fuzzer(String template, boolean ver)
	{
		m_template = template;
		m_verbose = ver;

		// setup all other data
		setupFuzzer();
		resetRunner();
	
	}	// constructor for the Fuzzer class

	/*
	[_a_] permutate ASCII values (a-z, A-z)
	[_A_] permutate ASCII values (a-z, A-z, but in steps 1000, 4096, 16384)
	[_D_] permutate decimal values (1- 64000)
	[_L_] repeat current line
	[_R_] remove current line
	*/

	
	
	private int getNumberOfAsciiPermutationsSingle(String data)
	{
		return getNumberOfPermutationsOfType(data, "[_a_]");
	}

	private int getNumberOfAsciiPermutations(String data)
	{
		return getNumberOfPermutationsOfType(data, "[_A_]");
	}

	private int getNumberOfDecimalPermutations(String data)
	{
		return getNumberOfPermutationsOfType(data, "[_D_]");
	}

	private int getNumberRepeatPermutations(String data)
	{
		return getNumberOfPermutationsOfType(data, "[_L_]");
	}

	private int getNumberOfRemovePermutations(String data)
	{
		return getNumberOfPermutationsOfType(data, "[_R_]");
	}


	/*
	 * returns the number of permutation markers for a dedicated type
	 * @in: data - data to be checked
	 * @in: cmpValue
	 * @out: cleaned string
	 */
	private int getNumberOfPermutationsOfType(String data, String cmpValue)
	{
		int counter = 0;
		int runner = 0;
		
		// dummy check
		if (data == null)
			return 0;

		if (cmpValue == null)
			return 0;
		
		while (runner <= data.length() - 1 && data.length() != 0)
		{
			if (data.indexOf(cmpValue, runner) != -1)
			{
				counter++;
				runner = data.indexOf(cmpValue, runner) + cmpValue.length();
			}
			else
			{
				return counter;
			}
		}
		return counter;
	}	// getNumberOfPermutationsOfType
	
	
	/*
	 * returns a string without the control markers
	 * @in data to be cleared
	 * @out: cleared data
	 */
	private String getBlankData(String data)
	{
		int runner = 0;
		String dataOut = "";
		
		// dummy check
		if (data == null)
			return null;
		
		while (runner <= data.length() - 1 && data.length() != 0)
		{
			int startBeginMarker = data.indexOf("[_", runner);
			int startEndMarker   = data.indexOf("_]", startBeginMarker + 4);
			
			// sanity check
			if (startEndMarker == startBeginMarker +4)
			{
				dataOut += data.substring(runner, startBeginMarker);
				runner = startEndMarker + 2;
			}
			else
			{	// overjump bogus data
				runner = startBeginMarker + 2;
			}
			
		}
		
		
		return dataOut;
	}	// getBlankData
	
	
	/*
	 * returns the first permutation as string
	 * @out: string
	 */
	public String getFirstStep()
	{
		return getBlankData(m_template);
		
	}	// getFirstStep
	
	/*
	 * returns the next permutation from the basic template
	 */
	public String getNextStep()
	{
		String data = "";
		
		if (m_runner <= getNumberOfSteps() - 1)
		{
			
		}
		else
		{
			return null;
		}
		
		return data;
	}	// getNextStep
	

	/***
	 * permutate every values on its own
	 */
	
}
