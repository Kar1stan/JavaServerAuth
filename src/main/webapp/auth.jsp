<%@ page import="com.example.sqljavaredirectserver.orm.User" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%
    User user = (User) request.getAttribute("user") ;
    String authError = (String) request.getAttribute("authError") ;
%>

<%-- Блок авторизации - меняется либо логин\пароль, либо Привет-выход --%>
<div style="border: 1px solid crimson; padding: 5px 10px">
    <% if( user == null ) { %>
    <%-- Данных о входе нет - отображаем логин\пароль --%>
    <form method="post" style="display: inline">
        Авторизация
        <label><input name="userLogin" /></label>
        <label><input name="userPass" type="password" /></label>
        <button>Вход</button>
    </form>

    <%-- Если была (есть) ошибка авторизации - выводим ее --%>
    <% if( authError != null ) { %>
    <b style="color:maroon"><%= authError %></b>
    <% }
    } else { %>
    <%-- Есть пользователь, его данные в переменной user; --%>
    Привет, <b><%= user.getLogin() %></b> !
    <form method="post" style="display: inline">
        <button name="logout">Выход</button>
    </form>
    <% } %>
</div>
