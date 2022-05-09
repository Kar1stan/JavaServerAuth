package com.example.sqljavaredirectserver.filters;

import com.example.sqljavaredirectserver.dao.PictureDao;
import com.example.sqljavaredirectserver.dao.UserDao;

import javax.servlet.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.example.sqljavaredirectserver.services.DbConnector;
import com.example.sqljavaredirectserver.services.RndService;


@Singleton
public class DbFilter implements Filter {
    FilterConfig filterConfig;

    @Inject
    DbConnector connector;

    @Inject
    RndService rnd;
    @Inject UserDao     userDao;
    @Inject PictureDao  pictureDao;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // указываем кодировки
        servletRequest.setCharacterEncoding( "UTF-8" ) ;
        servletResponse.setCharacterEncoding( "UTF-8" ) ;

        // Тест жизненного цикла службы - внедрение в разных местах и проверка на отличия
        servletRequest.setAttribute("rnd1", rnd.getRnd() ) ;

        // Флаг - признак успешных проверок
        boolean conSuccess = true ;
        Connection con = null ;
        // Проверка успешности внедрения (работы IoC)
        if( connector == null ) {
            System.out.println( "DbFilter - connector == null -- IoC failure");
            conSuccess = false ;
        }
        else {
            con = connector.getConnection();
            if (con == null) {  // Подключение к БД не установлено
                conSuccess = false ;
            }
        }
        if( conSuccess ) {
            //  внедряем зависимости от подключения в Dao          // Наша (временная) реализация инверсии управления -
            servletRequest.setAttribute( "userDao", userDao ) ;  // new UserDao(con) ); - переносим в зависимость
            servletRequest.setAttribute( "pictureDao", pictureDao ) ;  // new PictureDao(con) );

            // продолжения цепочки фильтров - сервлетов
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else {
            //- переходим на статик
            servletRequest.getRequestDispatcher("static.html")
                    .forward(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }
}

/*
Gallery:
Авторизация,
загрузка изображений с описаниями (для авторизованых пользователей)
просмотр изображений  - для всех (с фильтром)
правка своих изображений

Этап 0. Новый проект: JavaEE - Web Application - step.example.gallery

Этап 1. База данных
- открываем консоль БД (XAMPP - shell --  cd mysql/bin <Enter> -- mysql -u root <Enter> )
CREATE DATABASE gallery_912;
GRANT ALL PRIVILEGES ON gallery_912.* TO 'gallery_912_user'@'localhost' IDENTIFIED BY 'gallery_912_pass';

- Users:
CREATE TABLE Users (
    id         CHAR(36)    PRIMARY KEY,
    login      VARCHAR(64),
    pass_hash  CHAR(32),
    pass_salt  CHAR(32)
) ENGINE=InnoDB DEFAULT CHARSET = UTF8;

Этап 2. ORM
"Слой" отображения данных - создание классов, отражающих таблицы БД
(слова-синонимы: Entity, модели)
-- новый пакет - step.example.orm
-- новый класс  User
  традиция - не передавать пароль или его хеш

"Слой" доступа к данным (контекст) - DAO (Data Access Object)
-- новый пакет - dao
-- новый класс - UserDao

Этап 3. Приложение
Путь запроса
- фильтры:                        переход на статическую страницу
  = проверка подключения к БД  <
                                  нормальное продолжение
  = проверка сохраненной авторизации

Создаем статическую HTML-страницу, на которую будем попадать при проблемах с БД
-- новый пакет - filters
-- новый класс - DbFilter

Проверить работу метода хеширования:
в индексной странице извлечь объект userDao, вызвать его метод hash для
произвольной строки: userDao.hash("123")
Сравнить с результатом на онлайн-сервисе хеширования
a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3

-------------------  2022-04-15 ----------------------------------
Коррекция таблицы Users для хранения 256-битного хеша (ранее планировался 128)
[0-9A-F] - 4 бита ==> 128 бит - 32 символа; 256 - 64
ALTER TABLE Users MODIFY pass_hash  CHAR(64);
ALTER TABLE Users MODIFY pass_salt  CHAR(64);

UserDao - реализуем методы
О безопасности запросов...
Плохо:   "SELECT..." + data + "..."
 опасность иньекций - если в строке data есть "опасные" символы, они становятся
 частью команды и выполняются.
Правильно: разделить командную (кодовую) часть и данные.
Технология: подготовленные запросы.

Подготовленные запросы
 "+" - разделяют код и данные, повышают безопасность
     - не требуют предварительной обработки данных (экранирования)
     - повышают скорость повторных запросов, в которых менются только данные
       (например, выборка за разные месяцы года)
     - улучшают контроль передаваемых данных (если требуется число, то строку не примет)

 "-" - выполняются за два запроса: компиляция + исполнение
 особенность: в шаблоне запроса "?" вставляются без кавычек, даже если требуется
  строка в этом месте.
Итого: рекомендуются а) для повторяющихся запросов, б) для запросов, передающих
 данные от пользователя.


Добавили JSP с формой авторизации, подключили при помощи <jsp:include page="auth.jsp" />
Создаем фильтр, который будет заниматься вопросом авторизации

----------------------- 2022-04-22 ----------------------------
Галерея - основной контент
- Гостевой режим: просмотр изображений (и описаний)
- Авторизованный режим: просмотр, возможность загрузки новых и правки описаний
   и удаление своих загрузок

Добавляем в БД таблицу
CREATE TABLE Gallery (
    id            CHAR(36)     PRIMARY KEY,  -- UUID / GUID / NEWID
    description   TEXT         NULL,
    picture       VARCHAR(512) NOT NULL,   -- filename
    user_id       CHAR(36)     NOT NULL,
    moment        DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted       DATETIME     NULL
) ENGINE=InnoDB DEFAULT CHARSET = UTF8;

ORM - DAO
 */
