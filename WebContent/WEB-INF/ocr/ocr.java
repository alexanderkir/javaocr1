package com.ocr;
permission java.lang.RuntimePermission "accessClassInPackage.org.apache.tomcat";
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


@WebServlet("/ocr")
@MultipartConfig(maxFileSize = 16177215)   
public class ocr extends HttpServlet{
	private static final long serialVersionUID = 1L;
       
	public ServletContext context;
    private static String savePath="";
    /**
     * @throws IOException 
     */ 
	
    public ocr() throws IOException {
        super();
        }
  
    
  
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		 String hh= request.getServletContext().getRealPath("/").toString();

		java.io.InputStream     inputStream=null;
		Part filePart = request.getPart("file");
		 if (filePart != null) {
	            System.out.println(filePart.getName());
	            System.out.println(filePart.getSize());
	            System.out.println(filePart.getContentType());
	             
	          inputStream = filePart.getInputStream();
		 }
		
			String result= Main.main(hh,inputStream,savePath);
					 PrintWriter out = response.getWriter();
			 out.println("<jsp:directive.page contentType=\"text/html;charset=UTF-8\"/>");
			response.getWriter().append(result);
			request.getRequestDispatcher("index.jsp").forward(request, response);
            request.setAttribute("result", result);
          
	}

    /**
     * handles file upload
     */
    protected void doPost(HttpServletRequest request,
           HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);

    }

}
