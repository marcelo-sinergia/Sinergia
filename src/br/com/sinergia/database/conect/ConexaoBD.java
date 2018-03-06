package br.com.sinergia.database.conect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexaoBD {

    private static ConexaoBD instancia = new ConexaoBD();

    private final String driver = "oracle.jdbc.OracleDriver";
    public Connection con;
    private Boolean Conectado = false;
    private String ipmaq = "127.0.0.1";
    private String porta = "1521/xe";
    private String caminho = "jdbc:oracle:thin:@" + getIPMaq() + ":" + getPorta();
    private String usuario = "tesla";
    private String senha = "tecsis";

    public ConexaoBD(){};

    public ConexaoBD(Boolean conexParalela) {
        if(conexParalela) {
            setPorta(getInstancia().getPorta());
            setIPMaq(getInstancia().getIPMaq());
            setUsuario(getInstancia().getUsuario());
            setSenha(getInstancia().getSenha());
            setCaminho("jdbc:oracle:thin:@" + getInstancia().getIPMaq() + ":" + getInstancia().getPorta());
        }
    };

    public static ConexaoBD getInstancia() {
        return instancia;
    }

    public static void setInstancia(ConexaoBD instancia) {
        ConexaoBD.instancia = instancia;
    }

    public void Open() {
        try {
            if (!isConectado()) {
                System.setProperty("jdbc.Drivers", driver);
                con = DriverManager.getConnection(getCaminho(), getUsuario(), getSenha());
                setConectado(!isConectado()); //Já esta em conexão
            }
        } catch (SQLException ex) {
            setConectado(false); //Se deu erro, volta p/ 0 para poder criar conexão() denovo
            System.err.println(ex);
        }
    }


    public void Close() {
        try {
            con.close();
            setConectado(false);
        } catch (Exception ex) {
        }
    }

    public String getIPMaq() {
        return ipmaq;
    }

    public void setIPMaq(String ipmaq) {
        this.ipmaq = ipmaq;
    }

    public String getPorta() {
        return porta;
    }

    public void setPorta(String porta) {
        this.porta = porta;
    }

    public String getCaminho() {
        return "jdbc:oracle:thin:@" + getIPMaq() + ":" + getPorta();
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean isConectado() {
        return Conectado;
    }

    public void setConectado(Boolean conectado) {
        Conectado = conectado;
    }
}
