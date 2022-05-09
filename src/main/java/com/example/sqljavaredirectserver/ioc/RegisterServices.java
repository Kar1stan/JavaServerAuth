package com.example.sqljavaredirectserver.ioc;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.example.sqljavaredirectserver.services.DbConnector;
import com.example.sqljavaredirectserver.services.MySqlDbConnector;
import com.example.sqljavaredirectserver.services.RndService;
import com.example.sqljavaredirectserver.services.*;

public class RegisterServices extends AbstractModule {

    @Override
    protected void configure() {
        // Interface - implementation
        bind(DbConnector.class).to(MySqlDbConnector.class);
        bind(Hasher.class).to(Sha2Hasher.class);
        // class (implementation only)
        bind(RndService.class);
    }
}
