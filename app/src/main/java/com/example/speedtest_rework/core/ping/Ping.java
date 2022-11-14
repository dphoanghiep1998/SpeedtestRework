package com.example.speedtest_rework.core.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ping {
    public String getPingResult(String a) {
        String str = "";
        String result = "";
        BufferedReader reader = null;
        char[] buffer = new char[4096];
        StringBuffer output = new StringBuffer();

        try {
            Runtime r = Runtime.getRuntime();
            Process process = r.exec("/system/bin/ping -c 3 " + a);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            int i;

            while ((i = reader.read(buffer)) > 0)
                output.append(buffer, 0, i);


            str = output.toString();

            final String[] b = str.split("---");
            final String[] c = b[2].split("rtt");

            if (b.length == 0 || c.length == 0)
                return null;

            if (b.length == 1 || c.length == 1)
                return null;

            result = b[1].substring(1, b[1].length()) + c[0] + c[1].substring(1, c[1].length());

        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ie) {
                }
            }
        }

        return result;
    }
}
