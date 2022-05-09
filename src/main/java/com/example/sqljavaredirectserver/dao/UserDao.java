package com.example.sqljavaredirectserver.dao;

import com.example.sqljavaredirectserver.orm.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.example.sqljavaredirectserver.services.DbConnector;
import com.example.sqljavaredirectserver.services.Hasher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;

@Singleton
public class UserDao {

    private final Connection con;
    private final Hasher hasher;

    @Inject  // @Inject может применяться перед конструктором - внедрение через конструктор
    public UserDao( DbConnector connector, Hasher hasher ) {
        this.con = connector.getConnection() ;
        this.hasher = hasher;
    }

    public int getUsersCount(){
        try(Statement statement =con.createStatement()){
            ResultSet res=statement.executeQuery("SELECT COUNT(*) FROM Users");
            res.next();
            return res.getInt(1);
        }catch(Exception ex){
            System.out.println("Get User By Id:"+ex.getMessage());
        }
        return -1;
    }

    public User getUserById( String uid ) {
        String query = "SELECT u.id, u.login, u.pass_salt FROM Users u WHERE u.id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, uid);
            try( ResultSet res = ps.executeQuery() ) {
                if (res.next()) {
                    return new User(
                            res.getString(1),
                            res.getString(2),
                            res.getString(3));
                } else {
                    throw new Exception("Invalid id - empty result set");
                }
            }
        } catch (Exception ex) {
            System.out.println("Get User By Id: " + ex.getMessage());
        }
        return null ;
    }

    public User getUserByCredentials( String login, String pass ) {
        // Извлекаем из БД соль и хеш по логину
        try( PreparedStatement ps = con.prepareStatement(
                "SELECT u.id, u.pass_hash, u.pass_salt FROM Users u" +
                        " WHERE u.login = ?" ) ) {
            ps.setString( 1, login ) ;
            try( ResultSet res = ps.executeQuery() ) {
                if( res.next() ) {  // Есть данные - пользователь найден
                    // проверяем - стыкуем соль и переданный пароль, сверяем с хешем из БД
                    if (hasher.hash(pass + res.getString(3))
                            .equals(res.getString(2))) {
                        // если совпадает, создаем объект User с принятыми из БД данными
                        return new User(
                                res.getString(1),       // id
                                login,                            // login
                                res.getString(3));  // salt
                    }
                }
            }  // если не найден, return null ;
        }
        catch( Exception ex ) {
            System.out.println( ex.getMessage() ) ;
        }

        return null ;
    }

    public boolean isLoginFree( String login ) {
        // Prepared statement technology
        // 1. Query pattern: query text with placeholders (?)
        String query = "SELECT COUNT(id) FROM Users WHERE login = ?" ;

        // 2. Compiling query pattern
        try( PreparedStatement ps = con.prepareStatement( query ) ) {

            // 3. Binding placeholders with data
            ps.setString(1, login ) ;  // 1st "?" will be login

            // 4. Executing query
            try( ResultSet res = ps.executeQuery() ) {

                // Next - the same as simple query
                res.next();
                int cnt = res.getInt(1);
                if (cnt == 0) {
                    return true;
                }
            }
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
        }
        return false ;
    }

    public boolean addUser( String login, String pass ) {
        // check login
        if( ! isLoginFree( login ) ) {
            System.out.println( "addUser error: Login in use" ) ;
            return false;
        }

        String queryPattern = "INSERT INTO Users(id, login, pass_hash, pass_salt) VALUES (UUID(),?,?,?)" ;
        try( PreparedStatement ps = con.prepareStatement( queryPattern ) ) {
            String saltHash = hasher.hash(new Date().getTime() + "" ) ;
            String passHash = hasher.hash(pass + saltHash ) ;
            ps.setString(1, login ) ;
            ps.setString(2, passHash ) ;
            ps.setString(3, saltHash ) ;
            ps.executeUpdate() ;
        }
        catch( SQLException ex ) {
            System.out.println( ex.getMessage() ) ;
            return false ;
        }
        return true;
    }

    /* public String hash( String str ) {
        try {
            MessageDigest md = MessageDigest.getInstance( "SHA-256" ) ;
            md.update( str.getBytes() ) ;
            byte[] digest = md.digest() ;
            StringBuilder sb = new StringBuilder() ;
            for( byte b : digest ) {
                sb.append( String.format("%02x", b) ) ;
            }
            return sb.toString() ;
        }
        catch( NoSuchAlgorithmException ex ) {
            System.out.println( ex.getMessage() ) ;
        }
        return null ;
    } */
}
