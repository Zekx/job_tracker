package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import function.RetrieveData;

@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Does nothing since there is no search page
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int position = Integer.parseInt(request.getSession().getAttribute("position").toString());
		RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
		
		Logger searchLog = LoggerFactory.getLogger(Search.class);
		
		if(request.getParameter("searchBtn").toString().equals("Search")){
			String term = request.getParameter("search");
			try{
				request.getSession().setAttribute("tickets", rd.searchTicket(term, request.getSession().getAttribute("user").toString(), 
						position, Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));

				response.sendRedirect("Home");
			}
			catch(Exception e)
			{
				searchLog.error("Search Error @ Search.", e);
				request.setAttribute("errorMessage", "Something went wrong, please try again later.");
				request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
		}
		else{
			try{
				request.getSession().setAttribute("tickets", rd.getUserTicket(request.getSession().getAttribute("user").toString(),
						position, Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));

				response.sendRedirect("Home");
			}
			catch(Exception e)
			{
				searchLog.error("Reset Error @ Search.", e);
				request.setAttribute("errorMessage", "Something went wrong, please try again later.");
				request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
		}
	}

}
