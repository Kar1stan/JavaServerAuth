package  com.example.sqljavaredirectserver.filters;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.example.sqljavaredirectserver.dao.UserDao;
import com.example.sqljavaredirectserver.orm.User;
import com.example.sqljavaredirectserver.services.RndService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

@Singleton
public class AuthFilter implements Filter {
    @Inject
    RndService rnd;
    @Inject
    UserDao userDao;
    // перенесем в конфиг - web.xml / filter
    private long MAX_LOGIN_TIME = 30000;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // MAX_LOGIN_TIME = Long.parseLong( filterConfig.getInitParameter("MAX_LOGIN_TIME" ) ) ;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setAttribute("rnd2", rnd.getRnd() ) ;

        // Атрибут "userDao" устанавливается в DbFilter
        // его отсутствие свидетельствует о неправильном порядке фильтров
        /*UserDao userDao = (UserDao)
                servletRequest.getAttribute( "userDao" ) ;*/
        if(userDao == null) {
            throw new ServletException("AuthFilter:UserDao inject error");
        }
        if(userDao.getUsersCount()==0) {
            userDao.addUser("Vlad","12345");
        }
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // Сессия для данного пользователя:
        HttpSession session = request.getSession() ;

        // 1. Проверяем есть ли запрос на авторизацию или выход
        if( request.getMethod().toUpperCase(Locale.ROOT).equals("POST") ) {
            // logout != null --- выход
            String logout = request.getParameter( "logout" ) ;
            if( logout != null ) {
                // сброс сессионных параметров авторизации
                session.removeAttribute( "authData" ) ;
                ((HttpServletResponse) servletResponse).sendRedirect( request.getRequestURI() );
                return;
            }

            // userLogin != null && userPass != null --- вход
            String userLogin = request.getParameter( "userLogin" ) ;
            String userPass  = request.getParameter( "userPass" ) ;
            if( userLogin != null && userPass != null ) {
                // Есть данные авторизации
                User user = userDao.getUserByCredentials(userLogin, userPass);
                if (user == null) {
                    // авторизация отклонена - сохраняем в сессии ошибку и перенаправляем
                    session.setAttribute("authData",
                            new AuthData("", "Credentials invalid").toString());
                } else {
                    // авторизация успешна - сохраняем в сессии ID пользователя, время авторизации и перенаправляем
                    session.setAttribute("authData",
                            new AuthData(user.getId(), "").toString());
                }
                ((HttpServletResponse) servletResponse).sendRedirect( request.getRequestURI() );
                return;
            }

        }

        // 2. Проверяем есть ли сохраненная (в сессии) авторизация
        String str = (String) session.getAttribute( "authData" ) ;
        if( str != null ) {
            AuthData authData = new AuthData(str);

            if( authData.getUserId().equals("") ) {   // no id - error
                request.setAttribute( "authError", authData.getError() ) ;
                // Убираем сохраненные в сессии данные чтобы ошибка повторно не выводилась
                session.removeAttribute( "authData" ) ;
            }
            else {
                // В этом месте - имеем сохраненные в сессии данные об авторизации
                //  и это не запрос на авторизацию
                // Проверяем время авторизации
                long deltaTime = new Date().getTime() - authData.getMoment();
                if( deltaTime > MAX_LOGIN_TIME ) {
                    // Время (срок) удержания авторизации превышен - сбрасываем авторизацию:
                    // Очищаем в сессии сохраненные данные
                    session.removeAttribute( "authData" ) ;
                    // Посылаем редирект для того чтобы контекст был пересобран как для гостя
                    ((HttpServletResponse) servletResponse).sendRedirect( request.getRequestURI() );
                    return;
                }
                // else - не пишем, т.к. выше есть return, но формально это else

                // Находим пользователя по id и сохраняем в контексте (request)
                request.setAttribute("user", userDao.getUserById(authData.userId));
                // после этого, в любом месте приложения наличие атрибута "user"
                // будет свидетельствовать об авторизации и хранить данные пользователя

                // обновляем момент запроса для продолжения авторизации
                authData.updateMoment() ;
                session.setAttribute( "authData", authData.toString() ) ;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    static class AuthData {
        private final String userId;
        private final String error;
        private long   moment;

        public AuthData(String userId, String error) {
            this.userId = userId;
            this.error = error;
            updateMoment() ;
        }

        public AuthData(String str) {
            String[] arr = str.split( "\n" );
            userId = arr[0];
            error = arr[1];
            moment = Long.parseLong( arr[2] ) ;
        }

        @Override
        public String toString() {
            return String.format( "%s\n%s\n%d", this.userId, this.error, this.moment);
        }

        public String getUserId() {
            return userId;
        }

        public String getError() {
            return error;
        }

        public long getMoment() {
            return moment;
        }

        public void updateMoment() {
            this.moment = new Date().getTime() ;
        }
    }
}
/*
Задания:
 Механизм "выхода" - сброс авторизации
 а) по истечению времени
 б) по действию пользователя

 */