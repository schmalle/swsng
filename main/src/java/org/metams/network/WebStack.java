package org.metams.network;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import org.metams.swsng.*;



/*
 * WebStack 
 */

public class WebStack extends HttpServlet
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1996005156972121892L;


	public void doPost(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		
     	BannerGrab m_bG = new BannerGrab();
		try
		{
			m_bG.initialize(5060, 5060, "80.132.132", "UDP", "localhost", 3000);			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		out.println("<html>Hello, Brave new World! SIP forever");
		out.println(m_bG.getOutputAsHtmlString());
		out.println("</html>");
		out.close();
	}
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{

		doPost(req,res);
		
		//PrintWriter out = res.getWriter();
		//out.println("Hello, Brave new World! SIP forever");
	
		
		
		
		//out.close();
	}
}


