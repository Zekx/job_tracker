package controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import function.RetrieveData;
import model.Ticket;
@WebServlet("/Details")
public class Details extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if(request.getParameter("id") == null){
			response.sendRedirect("Home");
		}else{
			int id = Integer.parseInt(request.getParameter("id"));
			RetrieveData rd = new RetrieveData((DataSource)request.getServletContext().getAttribute("dbSource"));
			
			Ticket ticket = null;
			
			ticket = rd.getTicket(id);
			
			if (request.getSession().getAttribute("user") == null ) {
				request.getSession().setAttribute("dReferred", id);
				response.sendRedirect("Login");
			}
			else if(!ticket.getUser().equals(request.getSession().getAttribute("user").toString()) 
					&& !(ticket.isTechnician(request.getSession().getAttribute("user").toString()))
					&& !((int)request.getSession().getAttribute("position") <= 2)){
				request.setAttribute("errorMessage", "Invalid ticket request!");
				request.getRequestDispatcher("/WEB-INF/Home.jsp").forward(request, response);
			}
			else{
				
				request.setAttribute("unitList", rd.getAllUnits());
				request.setAttribute("ticket", ticket);
				if(request.getSession().getAttribute("pSuccessMessage") != null){
					request.setAttribute("successMessage", request.getSession().getAttribute("pSuccessMessage").toString());
					request.getSession().removeAttribute("pSuccessMessage");
				}
				if(Integer.parseInt(request.getSession().getAttribute("position").toString()) == 2){
					request.setAttribute("assigned", rd.checkAssignment(request.getSession().getAttribute("user").toString(), id));
				}
				request.getRequestDispatcher("/WEB-INF/Details.jsp").forward(request, response);
			}
			
		}
	}
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Does nothing
		doGet(request, response);
	}

}
