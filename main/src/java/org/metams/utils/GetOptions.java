package org.metams.utils;

public class GetOptions
{

    /* variable area */

    private String[]    m_hashValue = null;
    private int         m_runner = 0;
    private String[]    m_hashKey =   null;

    /* function area */

    /*
     * checks if a given key is in the hast
     *
     */
    public boolean existsKey(String key)
    {
        for (int runner = 0; runner <= m_runner - 1; runner++)
        {
            if (m_hashKey[runner].equals(key))
                return true;
        }

        return false;
    }   // existsKey


    /*
     * checks if a given key is in the hast
     *
     */
    public String getValue(String key)
    {
        if (existsKey(key))
        {

            for (int runner = 0; runner <= m_runner - 1; runner++)
            {
                if (m_hashKey[runner].equals(key))
                    return m_hashValue[runner];
            }


        }

        return null;
    }   // existsKey


    public void getopt(String[] argv, String options)
    {
        char[] optionsChars = options.toCharArray();
        m_hashKey   = new String[options.length()];
        m_hashValue = new String[options.length()];
        m_runner = 0;


        for (int runner = 0; runner <= options.length() - 1; runner++)
        {

        	// iterate through all options
        	for (int runnerArgs = 0; runnerArgs <= argv.length - 1 && runner <= options.length() - 1; runnerArgs++)
            {
        		
        		/*System.out.println("Info: argv.len" + argv.length);
        		System.out.println("Info: options:  "+options+ "  runner:  "+runner+"  argv[runnerArgs]:  " + argv[runnerArgs]);
        		System.out.println("Info: optionsChars[runner]: " + optionsChars[runner]);
        		*/
        		
                if (argv[runnerArgs].startsWith("-"))
                {   // some form of option found

                	
                    if (argv[runnerArgs].charAt(1) == optionsChars[runner])                	
                    {               	
                    	m_hashKey[m_runner] = "" + optionsChars[runner];

                        if (runner == options.length() - 1)
                        {
                            m_hashValue[m_runner] = argv[runnerArgs + 1];
                            m_runner++;
                            runner++;
                        }
                        // do the check for a singleton marker here, no additional values is passed 
                        else if (runner <= options.length() && optionsChars[runner + 1] == "?".charAt(0)) //options.substring(runner + 1, 1).equals("?"))
                        {
                            m_hashValue[m_runner] = "__true__";
                            m_runner++;
                            runner++;
                        }

                        else if (optionsChars[runner + 1] == ":".charAt(0))
                        {
                            m_hashValue[m_runner] = argv[runnerArgs + 1];
                            m_runner++;
                            runner++;
                        }
                        else
                        {
                            System.out.println("Error: Invalid options strings passed to getopt");
                        }

                    }

                }   // option sign "-" found


            }   // argv loop


        } // option loop

    }   // getopt

    public void reset()
    {

    }

    /*
     *
     */
    public GetOptions()
    {

    } // constructor for the GetOptions class


}

