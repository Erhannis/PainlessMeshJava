/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.painlessmeshjava;

/**
 *
 * @author erhannis
 */
public class ConnectivityManager {
    public static class NetworkInfo {
        public boolean isConnected() {
            return true;
        }
    }
    
    public static final ConnectivityManager SINGLETON = new ConnectivityManager();
    
    public static final String TYPE_WIFI = "WIFI";
    
    public NetworkInfo getNetworkInfo(String type) {
        return new NetworkInfo();
    }
}
