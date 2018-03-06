package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.modules.CtrlAtalhos;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class ConsultaDBController implements Initializable {

    DBParalelConex conex;

    @FXML
    private AnchorPane PanelFundo;
    @FXML
    private TableView TableQuery;
    @FXML
    private TextArea TxtQuery;
    @FXML
    private Text TxQtdCol, TxQtdLinha, TxTempoExec, TxTempoMont;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadClass();
    }

    private void loadClass() {
        //Functions.setLayoutTab(PanelFundo);
        Platform.runLater(() -> {
            CtrlAtalhos.getAtalhos().setNew(TxtQuery.getScene(), "CTRL+E", () -> execStatement());
        });
    }

    private void execStatement() {
        try {
            conex = new DBParalelConex(getStatement());
            if (getStatement().startsWith("INSERT") || getStatement().startsWith("UPDATE")) {
                TableQuery.getColumns().clear();
                int qtdRow = conex.countRow();
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                        qtdRow + " linha(s) afetadas."));
                ModelDialog.getDialog().raise();
            } else {
                refreshTabQuery();
            }
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro: " + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro: " + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void refreshTabQuery() {
        try {
            long time = System.currentTimeMillis();
            conex = new DBParalelConex(getStatement());
            conex.createSet();
            long timeExec = System.currentTimeMillis() - time;
            int CountRow = conex.countNumRows();
            int CountCol = conex.rs.getMetaData().getColumnCount();
            TableQuery.getColumns().clear();
            TableQuery.getColumns().setAll(createTableColumns(CountCol));
            String[][] DadosTabela = new String[CountRow][CountCol];
            for (int RowAtual = 0; RowAtual < CountRow; RowAtual++) {
                conex.rs.next();
                ArrayList<String> ArrayColuna = new ArrayList<>();
                for (int ColAtual = 1; ColAtual <= CountCol; ColAtual++) {
                    if (AddToArray(ArrayColuna, conex.rs.getMetaData().getColumnTypeName(ColAtual)))
                        ArrayColuna.add(conex.rs.getString(ColAtual));
                }
                String[] StrArray = new String[ArrayColuna.size()];
                StrArray = ArrayColuna.toArray(StrArray);
                DadosTabela[RowAtual] = ArrayColuna.toArray(StrArray);
            }
            ObservableList<String[]> Dados = FXCollections.observableArrayList();
            Dados.addAll(Arrays.asList(DadosTabela));
            TableQuery.setItems(Dados);
            time = System.currentTimeMillis() - time;
            TxQtdCol.setText("Qtd. Colunas: " + CountCol);
            TxQtdLinha.setText("Qtd. Linhas: " + CountRow);
            TxTempoExec.setText("Tempo Exec.:" + timeExec + " milisegundo(s)");
            TxTempoMont.setText("Tempo Montagem: " + time + "milisegundo(s)");
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar tabela de pesquisa\n" + ex, ex));
            ModelException.getException().raise();
            TableQuery.setItems(null);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar tabela de pesquisa\n" + ex, ex));
            ModelException.getException().raise();
            TableQuery.setItems(null);
        } finally {
            conex.desconecta();
        }
    }

    private Boolean AddToArray(ArrayList<String> array, String colType) {
        if (colType.equals("BLOB")) {
            array.add("BLOB");
            return false;
        } else {
            return true;
        }
    }

    private TableColumn[] createTableColumns(int ColumnCount) throws Exception {
        TableColumn[] TbColunas = new TableColumn[ColumnCount];
        for (int i = 0; i < ColumnCount; i++) {
            int finalI = i;
            TbColunas[finalI] = new TableColumn(conex.rs.getMetaData().getColumnName(finalI + 1));
            TbColunas[finalI].setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) p -> new SimpleStringProperty((p.getValue()[finalI])));
            TbColunas[i].setStyle("-fx-alignment: CENTER; -fx-border-color: #F8F8FF;");
        }
        return TbColunas;
    }

    private String getStatement() {
        return TxtQuery.getText();
    }
}
