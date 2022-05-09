package com.example.sqljavaredirectserver.dao;

import com.example.sqljavaredirectserver.orm.Picture;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.example.sqljavaredirectserver.services.DbConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// Data Access for Gallery table
@Singleton
public class PictureDao {
    private final Connection con;

    @Inject
    public PictureDao(DbConnector connector) {
        this.con = connector.getConnection() ;
    }

    public ArrayList<Picture> getPicturesList() {
        ArrayList<Picture> ret = null;
        try( Statement statement = con.createStatement() ) {
            try( ResultSet res = statement.executeQuery(
                    "SELECT * FROM Gallery"
            ) ) {
                ret = new ArrayList<>();
                while( res.next() ) {
                    ret.add( new Picture( res ) ) ;
                }
            }
        } catch( Exception ex ) {
            System.out.println("getPicturesList: " + ex.getMessage());
        }
        return ret;
    }

    public boolean addPicture( Picture picture ) {
        if( picture == null ) return false ;
        try( PreparedStatement ps = con.prepareStatement(
                "INSERT INTO Gallery( id, description, picture, user_id, moment, deleted )" +
                        "VALUES( UUID(), ?, ?, ?, CURRENT_TIMESTAMP, NULL ) "
        ) ) {
            ps.setString( 1, picture.getDescription() );
            ps.setString( 2, picture.getPicture() );
            ps.setString( 3, picture.getUserId() );
            ps.executeUpdate();
            return true;
        }
        catch( Exception ex ) {
            System.out.println("addPicture: " + ex.getMessage());
        }
        return false ;
    }
}
