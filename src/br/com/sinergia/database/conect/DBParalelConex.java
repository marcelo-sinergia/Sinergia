package br.com.sinergia.database.conect;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.properties.metods.GravaLog;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class DBParalelConex {

    public ResultSet rs;
    public PreparedStatement pst;
    private Connection con;
    private String driver = "oracle.jdbc.OracleDriver";
    private String caminho = "jdbc:oracle:thin:@" + ConexaoBD.getInstancia().getIPMaq() + ":" + ConexaoBD.getInstancia().getPorta();
    private String usuario = ConexaoBD.getInstancia().getUsuario();
    private String senha = ConexaoBD.getInstancia().getSenha();
    private int resp = 0, Index = 1;
    private String Query;
    private Boolean hideQueryInLog = false;

    public DBParalelConex(String query) throws SQLException {
        hideQueryInLog = false;
        Index = 1;
        conecta();
        this.Query = query;
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), "ExecuteQuery: " + Query);
        }
        pst = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    public DBParalelConex(String query, Boolean hide) throws SQLException {
        hideQueryInLog = hide;
        Index = 1;
        conecta();
        this.Query = query;
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), "ExecuteQuery: " + Query);
        }
        pst = con.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
    }

    private void conecta() {
        if (ConexaoBD.getInstancia().getIPMaq() == null) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar conectar com banco de dados\n" +
                            "O banco de dados ainda não foi configurado."));
            ModelException.getException().raise();
        }
        try {
            if (resp == 0) {
                System.setProperty("jdbc.Drivers", driver);
                con = DriverManager.getConnection(caminho, usuario, senha);
                resp = 1; //Já esta em conexão
            }
        } catch (SQLException ex) {
            resp = 0; //Se deu erro, volta p/ 0 para poder criar conexão() denovo
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar conectar com banco de dados\n" + ex, ex));
            ModelException.getException().raise();
        }
    }

    public void desconecta() {
        try {
            con.close();
            resp = 0;
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar desconectar com banco de dados\n" + ex, ex));
            ModelException.getException().raise();
        }
    }

    public void run() throws SQLException {
        long time = System.currentTimeMillis();
        execWithTimeOut(5);
        if (!hideQueryInLog) {
            time = System.currentTimeMillis() - time;
            GravaLog.getNewLinha().info(this.getClass(), "DB SourceConnection: Statement executado em " + time + " milisegundo(s)");
        }
    }

    public void createSet() throws SQLException {
        long time = System.currentTimeMillis();
        this.rs = pst.executeQuery();
        if (!hideQueryInLog) {
            time = System.currentTimeMillis() - time;
            GravaLog.getNewLinha().info(this.getClass(), "DB SourceConnection: ResulSet criado em " + time + " milisegundo(s)");
        }
    }

    public int countRow() throws SQLException {
        return pst.executeUpdate();
    }

    public int countNumRows() throws SQLException {
        rs.last();
        int countRow = rs.getRow();
        rs.beforeFirst();
        return countRow;
    }

    public void addParameter(Object Objeto) throws SQLException {
        if (Objeto == null) {
            addNullParameter();
            return;
        }
        String ClassType = Objeto.getClass().getTypeName();
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), Index + "º Parâmetro(" + ClassType + "): " + Objeto);
        }
        AddToStatement(Index, Objeto, ClassType);
        Index = Index + 1;
    }

    public void addParameter(Object Objeto, String Info) throws SQLException {
        if (Objeto == null) {
            addNullParameter();
            return;
        }
        String ClassType = Objeto.getClass().getTypeName();
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), Index + "º Parâmetro(" + ClassType + "): " + Objeto);
        }
        AddToStatement(Index, Objeto, ClassType);
        Index = Index + 1;
    }

    private void addNullParameter() throws SQLException {
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), Index + "º Parâmetro(java.lang.NullParam): Null");
        }
        AddToStatement(Index, null, "java.lang.NullParam");
        Index = Index + 1;
    }

    private void AddToStatement(int index, Object Objeto, String Type) throws SQLException {
        if (Objeto == null) Type = "java.lang.NullParam";
        switch (Type) {
            case "java.lang.NullParam":
                pst.setObject(Index, null);
                break;
            case "java.lang.Double":
                pst.setDouble(index, (Double) Objeto);
                break;
            case "java.lang.Integer":
                pst.setInt(index, (Integer) Objeto);
                break;
            case "java.sql.Date":
                pst.setDate(index, (Date) Objeto);
                break;
            case "java.time.LocalDate":
                pst.setDate(index, Date.valueOf((LocalDate) Objeto));
                break;
            case "java.lang.Character":
                pst.setString(index, Objeto.toString());
                break;
            case "java.lang.String":
                pst.setString(index, (String) Objeto);
                break;
            case "java.sql.Timestamp":
                pst.setTimestamp(index, (Timestamp) Objeto);
                break;
            case "byte[]":
                pst.setBytes(index, (byte[]) Objeto);
                break;
            case "java.util.ArrayList":
                ArrayList<Object> ArrayObj = (ArrayList<Object>) Objeto;
                for (Object Obj : ArrayObj) {
                    String ObjType = Obj.getClass().getTypeName();
                    GravaLog.getNewLinha().info(this.getClass(), index + "º Parâmetro(" + ObjType + "): " + Obj);
                    AddToStatement(index, Obj, ObjType);
                    index = index + 1;
                }
                Index = index - 1;
                break;
            default:
                throw new SQLException("Erro ao tentar inserir parâmetro no Statement\n"
                        + "Tipo: " + Type + ", não configurado para esta operação");
        }
    }

    private void execWithTimeOut(int seconds) throws SQLException {
        try {
            pst.setQueryTimeout(seconds);
            int qtdResult = pst.executeUpdate();
            if (!hideQueryInLog) GravaLog.getNewLinha().info(this.getClass(), qtdResult + " linha(s) afetadas!");
        } catch (SQLRecoverableException | SQLTimeoutException ex) {
            throw new SQLException("ORA-01013: o usuário solicitou o cancelamento da operação atual\n" +
                    "TOUTDBSTATEMENT atingiu o tempo limite, operação revertida\n" +
                    "Verifique se não há commit pendente no banco e refaça a operação.");
        } catch (SQLException ex) {
            throw new SQLException(ex);
        }
    }
}
