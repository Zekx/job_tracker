package function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * This class initializes all application scope objects for the
 * application to call when needed.
 *
 */
public class Initialization implements ServletContextListener {

	
	public void contextInitialized(ServletContextEvent sce){
		// Setting path to the log file 
		String path = System.getProperty("catalina.base") + "/logs/techitLogs.txt";
		System.setProperty("logfile.name", path);
		
		// Instantiating logger
		Logger initLog = LoggerFactory.getLogger(Initialization.class); 
		
		// ------------- Email SMTP Setup -------------------
		// final String user = "techit.csula@gmail.com";
		// final String pass = ".XCGG1Bc";
		
		ServletContext context = sce.getServletContext();
		// Setting the properties object for email
		Properties props = new Properties();
		// props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", context.getInitParameter( "mail.smtp.server" ));
		// props.put("mail.smtp.port", "587");
		props.put("mail.smtp.user", context.getInitParameter( "mail.smtp.username" ));
		props.put("mail.smtp.pass", context.getInitParameter( "mail.smtp.password" ));
		
/*		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
			}
		}); */
		Session session = Session.getInstance( props );
		
		// ------------- Domain of website -------------------------
		// This is meant for URL linking in email (ex: cs3.calstatela.edu)
		String domain = sce.getServletContext().getInitParameter("domain");
		
		// ------------- List of departments in ECST ---------------
		List<String> departmentList = new ArrayList<String>(Arrays.asList(sce.getServletContext().getInitParameter("departmentList").split(",")));
		departmentList.add(0, " ");
		
		// ------------- Application scope objects ----------
		sce.getServletContext().setAttribute("departmentList", departmentList);
		sce.getServletContext().setAttribute("properties", props);
		sce.getServletContext().setAttribute("session", session);
		sce.getServletContext().setAttribute("email", context.getInitParameter( "app.email" ));
		sce.getServletContext().setAttribute("domain", domain);
		// ------------- DataSource setup -------------------

		try{
			InitialContext enc = new InitialContext();
			DataSource dataSource  = (DataSource) enc.lookup("java:comp/env/jdbc/mysqldb");
			
			if(dataSource == null){
				initLog.error("Datasource object lookup is null in Initialization!");
			}
			sce.getServletContext().setAttribute("dbSource", dataSource);
			
		}catch(Exception e){
			initLog.error("Initialization failed.", e);
			throw new RuntimeException(e);
		}
		initLog.info("Initialization succeeded.");

	}
	
	public void contextDestroyed(ServletContextEvent sce){
		// Does nothing for now
		// If there is a need for it later, code will be added here
	}
}
