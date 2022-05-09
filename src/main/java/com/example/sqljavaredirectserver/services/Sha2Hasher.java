package com.example.sqljavaredirectserver.services;

import com.google.inject.Singleton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton
public class Sha2Hasher implements Hasher {
    @Override
    public String hash(String str) {
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
    }
}
