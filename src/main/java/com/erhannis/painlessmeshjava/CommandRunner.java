/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.painlessmeshjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author erhannis
 */
public class CommandRunner {

    public static String run(String cmd) {
        StringBuilder sb = new StringBuilder();
        try {
            Process p3;
            p3 = Runtime.getRuntime().exec(cmd);
            p3.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p3.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                //System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException ex) {
            sb.append("Comand error\n" + ex);
            System.out.println("There was an IO exception.");

        } catch (InterruptedException ex) {
            sb.append("Command was interrupted: " + ex);
            System.out.println("The command was interrupted.");
        }
        return sb.toString();
    }
}
