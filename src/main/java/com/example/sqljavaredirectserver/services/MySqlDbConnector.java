package com.example.sqljavaredirectserver.services;

import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;

@Singleton
public class MySqlDbConnector implements DbConnector {
    private Connection connection;

    @Override
    public Connection getConnection() {
        try{
            if(connection == null) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Driver mySqlDriver = new com.mysql.cj.jdbc.Driver();
                // DriverManager.registerDriver( mySqlDriver ) ;
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/gallery_vlad?useUnicode=true&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC",
                        "vl112user",
                        "pass112"
                );
            }
        }
        catch( Exception ex ) {
            System.out.println( ex.getMessage() ) ;
        }
        return connection;
    }
}
