/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.painlessmeshjava;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author erhannis
 */
public class WifiManager {
    public static class WifiInfo {
        //TODO There are quite a few assumptions here
        
        public String getSSID() {
            String output = CommandRunner.run("iwconfig");
            System.out.println("getSSID result: " + output);
            Matcher m = Pattern.compile("ESSID:\"(.*)\"").matcher(output);
            m.find();
            return m.group(1);
        }
        
        public String getBSSID() {
            Matcher m = Pattern.compile("Access Point: ([a-fA-F0-9:]+)").matcher(CommandRunner.run("iwconfig"));
            m.find();
            return m.group(1);
        }
        
        public String getProbableGateway() {
            Matcher m = Pattern.compile("default via ([0-9.]+) dev ").matcher(CommandRunner.run("ip r"));
            m.find();
            return m.group(1);
        }
    }

    public static class WifiConfiguration {
        public String SSID;
        public String preSharedKey;
    }

    public static final WifiManager SINGLETON = new WifiManager();

    public static final String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    
    public WifiInfo getConnectionInfo() {
        return new WifiInfo();
    }
    
    public int addNetwork(WifiConfiguration config) {
        return 0;
    }
    
    public void disconnect() {
        IntentManager.SINGLETON.broadcast(CONNECTIVITY_ACTION, null);
    }
    
    public void reconnect() {
        IntentManager.SINGLETON.broadcast(CONNECTIVITY_ACTION, null);
    }
    
    public void enableNetwork(int id, boolean dunno) {
        
    }
}
