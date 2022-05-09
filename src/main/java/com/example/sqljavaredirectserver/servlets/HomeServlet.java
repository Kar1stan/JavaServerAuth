package com.example.sqljavaredirectserver.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.example.sqljavaredirectserver.services.RndService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Controller for index page
@Singleton
public class HomeServlet extends HttpServlet {
    @Inject
    RndService rnd;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("rnd3", rnd.getRnd() ) ;

        req.setAttribute( "fromServlet", "Works" ) ;
        req.getRequestDispatcher( "index.jsp" ).forward( req, resp ) ;
    }
}
/*
 Инверсия управления - Guice
 1--------------- POM ----------------------
  <groupId>com.google.inject</groupId>
  <groupId>com.google.inject.extensions</groupId>

 2--------------- web.xml ------------------
 Всё переключаем на фильтр guice и добавляем listener - конфигуратор (Startup)

 2.1 ----------------------- Listener's class -----------------
 @Singleton
 public class Startup extends GuiceServletContextListener

 2.2 ----------------------- Filter ---------------------------
 <filter-class>com.google.inject.servlet.GuiceFilter</filter-class>

 3 ------------------------ Mapping ---------------------------
 Маршрутизация фильтров и сервлетов переносится в класс Startup

 4 ------------------------ Регистрация служб -----------------
 Переносим работу с подключением БД из фильтра в самостоятельный класс
 - Создаем интерфейс (DbConnector)
 - Создаем реализацию интерфейса (MySqlDbConnector)
 - Регистрируем соответствие интерфейса и реализации

 4.1 --------------------- Abstract Module --------------------
 Создаем класс RegisterServices - наследник AbstractModule, в котором производим
 связывание (регистрацию) служб
 4.2 --------------------- Configure --------------------------
 Добавляем к Startup экземпляр класса RegisterServices

========================= Conclusions ========================
Регистрация служб (подключение к БД) с жизненным циклом Singleton позволяет
отказаться от самостоятельного подключения-отключения с каждым запросом, передав
эту задачу в IoC

------------------------- Исследование ------------------------
Создадим службу с генерацией случайного числа во время создания.
Понаблюдаем за изменением этого числа при работе приложения.

Д.З. Создать новый проект, внедрить Guice
Реализовать хеширование как службу:
Интерфейс Hasher{ hash(str) }
реализация Sha2Hasher
В сервлете заявить зависимость, реализовать онлайн хешер: вводишь строку,
 получаешь хеш
* +реализация Md5Hasher - и его добавить в онлайн

 */
