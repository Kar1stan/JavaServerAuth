<%@ page import="com.example.sqljavaredirectserver.orm.User" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Image gallery</title>
</head>
<body>
<!-- блок авторизации -->
<jsp:include page="auth.jsp" />
<br/>

<!-- блок добавления нового изображения (для авторизованных пользователей) -->
<% if( user != null ) { %>
<jsp:include page="add_picture.jsp" />
<% } %>
Д.З. Создать форму для загрузки нового изображения:
картинка + описание.
Выводить форму только в том случае, если пользователь авторизовался
Иначе: сообщение "Только авторизованные пользователи могут добавлять изо"

<a href="hello-servlet">Hello Servlet</a>
</body>
</html>