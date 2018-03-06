package br.com.sinergia.database.conect;

import br.com.sinergia.properties.metods.GravaLog;

import java.sql.*;
import java.util.ArrayList;

public class Statement {

    public ResultSet rs;
    PreparedStatement Pst;
    String Query;
    int Index = 1;
    Boolean hideQueryInLog = false;

    public Statement() {

    }

    public Statement(String Query) throws SQLException {
        hideQueryInLog = false;
        Index = 1;
        if (!ConexaoBD.getInstancia().isConectado()) {
            ConexaoBD.getInstancia().Open();
        }
        this.Query = Query;
        GravaLog.getNewLinha().info(this.getClass(), "ExecuteQuery: " + Query);
        Pst = ConexaoBD.getInstancia().con.prepareStatement(Query);
    }

    public Statement(String Query, boolean hide) throws SQLException {
        hideQueryInLog = hide;
        Index = 1;
        if (!ConexaoBD.getInstancia().isConectado()) {
            ConexaoBD.getInstancia().Open();
        }
        this.Query = Query;
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), "ExecuteQuery: " + Query);
        }
        Pst = ConexaoBD.getInstancia().con.prepareStatement(Query);
    }

    public void newStatement(String Query) throws SQLException {
        hideQueryInLog = false;
        Index = 1;
        if (!ConexaoBD.getInstancia().isConectado()) {
            ConexaoBD.getInstancia().Open();
        }
        this.Query = Query;
        GravaLog.getNewLinha().info(this.getClass(), "ExecuteQuery: " + Query);
        Pst = ConexaoBD.getInstancia().con.prepareStatement(Query);
    }

    public void run() throws SQLException {
        Pst.execute();
        ConexaoBD.getInstancia().Close();
    }

    public void createSet() throws SQLException {
        this.rs = Pst.executeQuery();
    }

    public void end() {
        ConexaoBD.getInstancia().Close();
    }

    public int countRow() throws SQLException {
        return Pst.executeUpdate();
    }

    public void addParameter(Object Objeto) throws SQLException {
        String ClassType = Objeto.getClass().getTypeName();
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), Index + "º Parâmetro(" + ClassType + "): " + Objeto);
        }
        AddToStatement(Index, Objeto, ClassType);
        Index = Index + 1;
    }

    public void addParameter(Object Objeto, String Info) throws SQLException {
        String ClassType = Objeto.getClass().getTypeName();
        if (!hideQueryInLog) {
            GravaLog.getNewLinha().info(this.getClass(), Index + "º Parâmetro(" + ClassType + "): " + Objeto);
        }
        AddToStatement(Index, Objeto, ClassType);
        Index = Index + 1;
    }

    private void AddToStatement(int index, Object Objeto, String Type) throws SQLException {
        switch (Type) {
            case "java.lang.Integer":
                Pst.setInt(index, (int) Objeto);
                break;
            case "java.sql.Date":
                Pst.setDate(index, (Date) Objeto);
                break;
            case "java.lang.String":
                Pst.setString(index, (String) Objeto);
                break;
            case "java.sql.Timestamp":
                Pst.setTimestamp(index, (Timestamp) Objeto);
                break;
            case "byte[]":
                Pst.setBytes(index, (byte[]) Objeto);
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

}
