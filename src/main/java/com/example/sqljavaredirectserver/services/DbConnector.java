package com.example.sqljavaredirectserver.services;

import java.sql.Connection;

public interface DbConnector {
    Connection getConnection();
}
