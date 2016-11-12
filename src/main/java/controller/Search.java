package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import function.RetrieveData;

@WebServlet("/Search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Does nothing since there is no search page
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int position = Integer.parseInt(request.getSession().getAttribute("position").toString());
		RetrieveData rd = new RetrieveData();
		
		if(request.getParameter("searchBtn").toString().equals("Search")){
			String term = request.getParameter("search");
			try{
				request.getSession().setAttribute("tickets", rd.searchTicket(term, request.getSession().getAttribute("user").toString(), 
						position, Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));

				response.sendRedirect("Home");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				request.setAttribute("errorMessage", "Something went wrong, please try again later.");
				request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
		}
		else{
			try{
				System.out.println("Here2");
				request.getSession().setAttribute("tickets", rd.getUserTicket(request.getSession().getAttribute("user").toString(),
						position, Integer.parseInt(request.getSession().getAttribute("unit_id").toString())));

				response.sendRedirect("Home");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				request.setAttribute("errorMessage", "Something went wrong, please try again later.");
				request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
		}
	}

}
