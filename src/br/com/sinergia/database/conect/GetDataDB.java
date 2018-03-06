package br.com.sinergia.database.conect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GetDataDB {

    private final String driver = "oracle.jdbc.OracleDriver";
    private String IPDb;
    private String PortaDb;
    private String UserDb;
    private String PassDb;

    public GetDataDB(String IPDb, String PortaDb, String UserDb, String PassDb) {
        setIPDb(IPDb);
        setPortaDb(PortaDb);
        setUserDb(UserDb);
        setPassDb(PassDb);
    }

    public void conectionIsValid() throws SQLException {
        System.setProperty("jdbc.Drivers", driver);
        String caminho = "jdbc:oracle:thin:@" + getIPDb() + ":" + getPortaDb();
        Connection con = DriverManager.getConnection(caminho, getUserDb(), getPassDb());
        con.close();
    }

    public String getIPDb() {
        return IPDb;
    }

    public void setIPDb(String IPDb) {
        this.IPDb = IPDb;
    }

    public String getPortaDb() {
        return PortaDb;
    }

    public void setPortaDb(String portaDb) {
        PortaDb = portaDb;
    }

    public String getUserDb() {
        return UserDb;
    }

    public void setUserDb(String userDb) {
        UserDb = userDb;
    }

    public String getPassDb() {
        return PassDb;
    }

    public void setPassDb(String passDb) {
        PassDb = passDb;
    }
}
