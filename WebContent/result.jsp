<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Captcha Demo - Servlet</title>
</head>
<body>
<% Boolean B = (Boolean)session.getAttribute( "PassedCaptcha" );
   if ( B!=null && B.booleanValue() )
   {
%>
  Congrats!  You passed the Captcha test!
<% } else { %>
  Sorry, you failed the Captcha test.
<% } %>
  <p>Please <a href="login.jsp">Try Again</a></p>
</body>
</html>