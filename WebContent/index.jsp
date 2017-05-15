<jsp:directive.page contentType="text/html;charset=UTF-8"/>
<html>
   <head>
      <title>File Uploading Form</title>
   </head>
   
   <body>
   
Absolute Path is:<%= getServletContext().getRealPath("/")  %>
      
      <form action = "ocr" method = "post"
         enctype = "multipart/form-data">
         <input type = "file" name = "file" id = "file" size = "50" />
         <br />
         <input type = "submit" value = "Upload File" />
      </form>
      <%= request.getAttribute("result")%>
      
   </body>
   
</html>