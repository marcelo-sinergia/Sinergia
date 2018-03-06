package br.com.sinergia.properties.metods;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.net.URL;

public class GravaLog {
    private static GravaLog gravaLog = new GravaLog();
    Logger Log = Logger.getLogger(GravaLog.class);
    URL url = GravaLog.class.getResource("/br/com/sinergia/properties/conf/Log4j.properties");
    Boolean Configurado = false;

    public static GravaLog getNewLinha() {
        return gravaLog;
    }

    public void erro(Class Invocador, String Informação) {
        Configura();
        Log.error("[" + Invocador + "] : "
                + Informação);
    }

    public void alerta(Class Invocador, String Informação) {
        Configura();
        Log.warn("[" + Invocador + "] : "
                + Informação);
    }

    public void info(Class Invocador, String Informação) {
        Configura();
        Log.info("[" + Invocador + "] : "
                + Informação);
    }

    public void gravaTraceException(Class Invocador, Exception ex) {
        CharArrayWriter cw = new CharArrayWriter();
        PrintWriter w = new PrintWriter(cw);
        ex.printStackTrace(w);
        w.close();
        String trace = cw.toString();
        erro(Invocador, trace);
    }

    private void Configura() {
        if (!Configurado) {
            PropertyConfigurator.configure(url);
            Configurado = !Configurado;
        }
    }

}
