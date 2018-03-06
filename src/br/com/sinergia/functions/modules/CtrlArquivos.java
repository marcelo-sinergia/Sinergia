package br.com.sinergia.functions.modules;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.models.intern.User;

import java.sql.SQLException;

public class CtrlArquivos {

    public static CtrlArquivos ctrlArquivos = new CtrlArquivos();
    public Boolean noError = true;
    DBParalelConex conex;

    public static CtrlArquivos getArquivos() {
        return ctrlArquivos;
    }

    public void Registra(int CódUsuário, String Arquivo, String Lista) {
        noError = true;
        int retorno = 0;
        try {
            conex = new DBParalelConex("SELECT COUNT(1) AS COUNT\n"
                    + "FROM TSIARQ WHERE CODUSU = ? AND ARQUIVO = ?");
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(Arquivo);
            conex.createSet();
            conex.rs.next();
            retorno = conex.rs.getInt("COUNT");
        } catch (SQLException ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar contar: '" + Arquivo + "'\n"
                    + ex.getMessage(), ex));
            ModelException.getException().raise();
        } catch (Exception ex ){
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar contar: '" + Arquivo + "'\n"
                    + ex.getMessage(), ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
        if (noError) {
            try {
                if (retorno == 0) {
                    conex = new DBParalelConex("INSERT INTO TSIARQ\n"
                            + "(CODUSU, ARQUIVO, LISTA)\n"
                            + "VALUES\n"
                            + "(?, ?, ?)");
                    conex.addParameter(User.getCurrent().getCódUsu());
                    conex.addParameter(Arquivo);
                    conex.addParameter(Lista);
                } else {
                    conex = new DBParalelConex("UPDATE TSIARQ SET LISTA = ? \n"
                            + "WHERE CODUSU = ? AND ARQUIVO = ?");
                    conex.addParameter(Lista);
                    conex.addParameter(User.getCurrent().getCódUsu());
                    conex.addParameter(Arquivo);
                }
                conex.run();
            } catch (SQLException ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar gravar registro o arquivo: '" + Arquivo + "'"
                        + ex.getMessage(), ex));
                ModelException.getException().raise();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar gravar registro o arquivo: '" + Arquivo + "'"
                        + ex.getMessage(), ex));
                ModelException.getException().raise();
            } finally {
                conex.desconecta();
            }
        }
    }

    public String Busca(int CódUsuário, String Arquivo) {
        String Retorno = "";
        try {
            conex = new DBParalelConex("SELECT LISTA FROM TSIARQ\n"
                    + "WHERE CODUSU = ?\n"
                    + "AND ARQUIVO = ?");
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(Arquivo);
            conex.createSet();
            if (conex.rs.next()) {
                Retorno = conex.rs.getString("LISTA");
            } else {
                Retorno = "0";
            }
        } catch (SQLException ex) {
            Retorno = "-1";
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar arquivo: '" + Arquivo + "'\n"
                    + ex.getMessage(), ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            Retorno = "-1";
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar arquivo: '" + Arquivo + "'\n"
                    + ex.getMessage(), ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
        return Retorno;
    }

}
