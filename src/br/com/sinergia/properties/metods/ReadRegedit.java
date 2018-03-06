package br.com.sinergia.properties.metods;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class ReadRegedit {

    public static final String readRegistry(String path, String key) {
        try {
            Process process = Runtime.getRuntime().exec("reg query " + path + " /v " + key);
            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String output = reader.getResult();
            ArrayList<String> parsed = new ArrayList<String>(Arrays.asList(output.split(" ")));
            return parsed.get(parsed.size() - 1).trim();
        } catch (Exception e) {
            System.err.println(e);
            return null;
        }

    }

    static class StreamReader extends Thread {
        private InputStream is;
        private StringWriter sw = new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1)
                    sw.write(c);
            } catch (IOException e) {
                System.err.println("Run.error: " + e);
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }

}