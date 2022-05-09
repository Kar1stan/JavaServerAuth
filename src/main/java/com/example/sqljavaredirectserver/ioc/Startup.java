package com.example.sqljavaredirectserver.ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.example.sqljavaredirectserver.filters.AuthFilter;
import com.example.sqljavaredirectserver.filters.DbFilter;
import com.example.sqljavaredirectserver.servlets.AddPictureServlet;
import com.example.sqljavaredirectserver.servlets.HomeServlet;

@Singleton
public class Startup extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
                new ServletModule() {
                    @Override
                    protected void configureServlets() {
                        // Сюда переносим конфигурацию фильтров и сервлетов
                        filter("/*").through(DbFilter.class);
                        filter("/*").through(AuthFilter.class);

                        serve("/").with(HomeServlet.class);
                        serve("/add-picture").with(AddPictureServlet.class);
                    }
                },
                new RegisterServices()  // Конфигурация (регистрация) служб
        );
    }
}
