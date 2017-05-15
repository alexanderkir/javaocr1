package com.ocr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.net.ftp.FTPClient;

import org.apache.commons.net.ftp.FTPFile;


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
		 FTPClient ftpClient=null;
	      try {  
	       ftpClient = new FTPClient();          
	      }
	 
	catch (Exception e6) {  
         e6.printStackTrace(); 
         }
	
	      /*********  work only for Dedicated IP ***********/    
	        final String FTP_HOST= "files.000webhost.com";

	        /*********  FTP USERNAME ***********/
	        final String FTP_USER = "andreynikoulin";

	        /*********  FTP PASSWORD ***********/
	        final String FTP_PASS  ="andrey.nikoulin@gmail.com";

	      try {  
	          // pass directory path on server to connect  
	          ftpClient.connect(FTP_HOST, 21);

	          // pass username and password, returned true if authentication is  
	          // successful  
	           boolean login = ftpClient.login(FTP_USER, FTP_PASS);  
	           ftpClient.enterRemotePassiveMode();
			if (login) {  

	              System.out.println("FTP Connection established...");
	              System.out.println("Status: "+ftpClient.getStatus());
	          
	              boolean folderCreated = ftpClient.makeDirectory("feeds");  
	            
	              @SuppressWarnings("unused")
				boolean success =ftpClient.changeWorkingDirectory("/feeds");
	              
	              
	              
	              // APPROACH #1: uploads first file using an InputStream
	              File firstLocalFile = new File("C:/Users/User/Downloads/WhatsApp.jpeg");
	              if(firstLocalFile.exists()){
	              String firstRemoteFile = "WhatsApp.jpeg";
	              InputStream inputStream = new FileInputStream(firstLocalFile);
	   
	              System.out.println("Start uploading first file");
	              boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
	              inputStream.close();
	              }
	              
	              
	              // success = ftpClient.changeWorkingDirectory("/upload");
	              FTPFile[] fileList=ftpClient.listFiles();

	               System.out.println("list of FTP files is:");
	              for (FTPFile ftpFile : fileList) {
	            	  System.out.println(" Name: "+ftpFile.getName());
	            }

	              boolean logout = ftpClient.logout();  
	              if (logout) {  
	            	  System.out.println("FTP Connection close..."); 
	              }  
	          } else {  
	        	  System.out.println("FTP Connection fail..."); 
	          }       
	      } catch (Exception e) {  
	          e.printStackTrace();  
	      } finally {  
	          try {  
	              ftpClient.disconnect();  
	          } catch (IOException e) {  
	              e.printStackTrace();  
	          }  
	      }
		
		
		
		
		
		
		
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
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);

    }

}
