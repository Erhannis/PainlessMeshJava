/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.painlessmeshjava;

import com.erhannis.mathnstuff.FactoryHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *
 * @author erhannis
 */
public class IntentManager {
    public static final IntentManager SINGLETON = new IntentManager();
    
    private FactoryHashMap<String, ArrayList<BiConsumer<String, String>>> registrations = new FactoryHashMap<>((i) -> {
        return new ArrayList<BiConsumer<String, String>>();
    });
    
    public synchronized void registerReceiver(BiConsumer<String, String> broadcastReceiver, List<String> intentFilter) {
        for (String action : intentFilter) {
            registrations.get(action).add(broadcastReceiver);
        }
    }

    public synchronized void unregisterReceiver(BiConsumer<String, String> broadcastReceiver) {
        for (ArrayList<BiConsumer<String, String>> cs : registrations.values()) {
            //TODO N add, 1 remove?  HashSet?
            cs.remove(broadcastReceiver);
        }
    }
    
    public synchronized void broadcast(String action, String msg) {
        //TODO Asynchronous?
        for (BiConsumer<String, String> c : registrations.get(action)) {
            c.accept(action, msg);
        }
    }
}
