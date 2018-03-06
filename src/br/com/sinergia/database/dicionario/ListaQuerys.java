package br.com.sinergia.database.dicionario;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.controllers.fxml.CadUnidadesController;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.natives.Functions;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListaQuerys {

    public static ObservableList<String> getListMarcas() {
        ObservableList<String> listMarcas = FXCollections.observableArrayList();
        DBParalelConex conex = null;
        try {
            conex = new DBParalelConex("SELECT DISTINCT(MARCA) AS MARCA FROM TGFPRO ORDER BY 1 NULLS FIRST");
            conex.createSet();
            while (conex.rs.next()) {
                listMarcas.add(Functions.Nvl(conex.rs.getString("MARCA")));
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(conex.getClass(), null,
                    "Erro ao tentar obter lista de marcas dos produtos\n" + ex, ex));
            ModelException.getException().raise();
            listMarcas.clear();
            listMarcas.add("<Sem Valores>");
        } finally {
            conex.desconecta();
        }
        return listMarcas;
    }

    public static ObservableList<String> getListControle(Integer codProd, Boolean enableBlank) {
        ObservableList<String> listControle = FXCollections.observableArrayList();
        if(enableBlank) listControle.add("");
        DBParalelConex conex = null;
        try {
            conex = new DBParalelConex("SELECT LISTACTRL FROM TGFCTRL WHERE CODPROD = ? AND TIPCTRL = ?");
            conex.addParameter(codProd);
            conex.addParameter("L");
            conex.createSet();
            if(!conex.rs.next()) {
                return listControle;
            } else {
                String strListaControle = conex.rs.getString(1);
                listControle.addAll(strListaControle.split(";"));
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(conex.getClass(), null,
                    "Erro ao tentar obter lista de controles do produto: " + codProd + "\n" + ex, ex));
            ModelException.getException().raise();
            listControle.clear();
            listControle.add("<Sem Valores>");
        } finally {
            conex.desconecta();
        }
        return listControle;
    }

    public static ObservableList<String> getListUnidades() {
        ObservableList<String> listUnidades = FXCollections.observableArrayList();
        DBParalelConex conex = null;
        try {
            conex = new DBParalelConex("SELECT CODVOL FROM TGFVOL ORDER BY 1");
            conex.createSet();
            while (conex.rs.next()) {
                listUnidades.add(Functions.Nvl(conex.rs.getString("CODVOL")));
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(conex.getClass(), null,
                    "Erro ao tentar obter lista de unidades\n" + ex, ex));
            ModelException.getException().raise();
            listUnidades.clear();
            listUnidades.add("UN");
        } finally {
            conex.desconecta();
        }
        return listUnidades;
    }

    public static String getDescrVol(String unidade) {
        if (unidade.equals("")) return "";
        DBParalelConex conex = null;
        try {
            conex = new DBParalelConex("SELECT DESCRVOL FROM TGFVOL WHERE CODVOL = ?");
            conex.addParameter(unidade);
            conex.createSet();
            if (conex.rs.next()) {
                return conex.rs.getString("DESCRVOL");
            } else {
                return "";
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(conex.getClass(), null, "Erro ao tentar obter descrição do volume\n" + ex, ex));
            ModelException.getException().raise();
            return "";
        } finally {
            conex.desconecta();
        }
    }
}
