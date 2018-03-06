package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.database.dicionario.ListaQuerys;
import br.com.sinergia.functions.extendeds.SearchFieldTable;
import br.com.sinergia.functions.extendeds.SearchFieldTree;
import br.com.sinergia.functions.extendeds.tableProperties.ModelTableColumn;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.usage.Estoque;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.natives.Functions.*;

public class ComCtrlEstController implements Initializable {

    DBParalelConex conex;
    ObservableList<KeyFields> listKeyFieldsGeral = FXCollections.observableArrayList();
    ObservableList<Estoque> listEstoque = FXCollections.observableArrayList();

    @FXML
    private AnchorPane PanelFundo;
    @FXML
    private TitledPane TtpGeral, TtpParceiros;
    @FXML
    private TableView<Estoque> TbEstoque;
    @FXML
    private Button BtnAplicar;
    @FXML
    private TextField TxtCodProd, TxtDescrProd, TxtCodGrupoProd, TxtDescrGrupoProd, TxtCodEmpresa, TxtDescrEmpresa;
    @FXML
    private ComboBox<String> CbbControle, CbbMarca;
    @FXML
    private Label LblEstTotal, LblEstDisp, LblEstReserv;
    @FXML
    private ImageView ImgProduto, ImgMarca, ImgGrupoProd, ImgEmpresa;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClass();
    }

    private void loadClass() {
        try {
            setLayoutTab(PanelFundo);
            CbbMarca.setItems(ListaQuerys.getListMarcas());
            TbEstoque.getColumns().addAll(getTbEstoqueColumns());
            TbEstoque.setItems(listEstoque);
            fieldEstructure();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar aplicar Layout na tela\n" + ex + "\nFavor contate o suporte", ex));
            ModelException.getException().raise();
        }
    }

    private void fieldEstructure() {
        BtnAplicar.setOnAction(e -> loadTableValues());
        MaskField.NumberField(TxtCodProd, 22);
        SearchFieldTable keyProduto = new SearchFieldTable(ImgProduto, "Produto", 6,
                new String[]{"Código", "Ativo", "Descrição", "Complemento", "Marca", "Referência"},
                "SELECT CODPROD, \n" +
                        "CASE WHEN ATIVO = 'S' THEN 'Sim'\n" +
                        "ELSE 'Não' END AS ATIVO, \n" +
                        "DESCRPROD, COMPLEMENTO, MARCA, REFERENCIA FROM TGFPRO\n" +
                        "ORDER BY CODPROD");
        keyProduto.getMainStage().setOnCloseRequest(e -> {
            if (keyProduto.getKeyReturn() != null) {
                TxtCodProd.setText(keyProduto.getKeyReturn().get(0));
                TxtDescrProd.setText(keyProduto.getKeyReturn().get(2));
            }
        });
        KeyFields keyFieldProduto = new KeyFields(TxtCodProd, "Produto", "AND PRO.CODPROD = ?");
        TxtCodProd.textProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(oldV)) {
                if (Nvl(newV).equals("")) {
                    ctrlListKeyFields(false, keyFieldProduto, listKeyFieldsGeral);
                    TxtDescrProd.setText("");
                    CbbControle.getItems().clear();
                    CbbControle.setValue("");
                } else {
                    ctrlListKeyFields(true, keyFieldProduto, listKeyFieldsGeral);
                    CbbControle.setItems(ListaQuerys.getListControle(getOnlyNumber(newV), true));
                }
            }
        });
        TxtCodProd.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !Nvl(TxtCodProd.getText()).equals("")) {
                int count = checkIfExists("TGFPRO", "CODPROD", TxtCodProd.getText());
                if (count == 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não encontrado produto com código: " + TxtCodProd.getText()));
                    ModelDialog.getDialog().raise();
                    cleanFields(TxtCodProd, TxtDescrProd);
                } else {
                    Platform.runLater(() -> {
                        try {
                            conex = new DBParalelConex("SELECT DESCRPROD FROM TGFPRO WHERE CODPROD = ?");
                            conex.addParameter(TxtCodProd.getText());
                            conex.createSet();
                            conex.rs.next();
                            TxtDescrProd.setText(conex.rs.getString(1));
                        } catch (Exception ex) {
                            ModelException.setNewException(new ModelException(this.getClass(), null,
                                    "Erro ao tentar buscar descrição do produto", ex));
                            ModelException.getException().raise();
                            cleanFields(TxtCodProd, TxtDescrProd);
                        } finally {
                            conex.desconecta();
                        }
                    });
                }
            }
        });
        KeyFields keyFieldControle = new KeyFields(CbbControle, "Controle", "AND EST.CONTROLE = ?");
        CbbControle.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(oldV)) {
                if (newV.equals("")) ctrlListKeyFields(false, keyFieldControle, listKeyFieldsGeral);
                else ctrlListKeyFields(true, keyFieldControle, listKeyFieldsGeral);
            }
        });
        KeyFields keyFieldMarca = new KeyFields(CbbMarca, "Marca", "AND PRO.MARCA = ?");
        CbbMarca.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(oldV)) {
                if (newV.equals("")) ctrlListKeyFields(false, keyFieldMarca, listKeyFieldsGeral);
                else ctrlListKeyFields(true, keyFieldMarca, listKeyFieldsGeral);
            }
        });
        MaskField.NumberField(TxtCodGrupoProd, 22);
        SearchFieldTree keyGrupoProd = new SearchFieldTree(ImgGrupoProd, "Grupo de Produtos", 3, new Integer[]{0, 1},
                "SELECT CODGRUPOPROD, DESCRGRUPOPROD, ANALITICO FROM TGFGRU WHERE CODGRUPAI = -1",
                "SELECT CODGRUPOPROD, DESCRGRUPOPROD, ANALITICO FROM TGFGRU WHERE CODGRUPAI = ?");
        keyGrupoProd.getMainStage().setOnCloseRequest(e -> {
            if (keyGrupoProd.getKeyReturn() != null) {
                if (keyGrupoProd.getKeyReturn().get(2).equals("S")) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Grupo de produtos não pode ser análitico."));
                    ModelDialog.getDialog().raise();
                    e.consume();
                } else {
                    TxtCodGrupoProd.setText(keyGrupoProd.getKeyReturn().get(0));
                    TxtDescrGrupoProd.setText(keyGrupoProd.getKeyReturn().get(1));
                }
            }
        });
        KeyFields keyFieldGrupoProd = new KeyFields(TxtCodGrupoProd, "Grupo de Produto", "AND PRO.CODGRUPOPROD = ?");
        TxtCodGrupoProd.textProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(oldV)) {
                if (Nvl(newV).equals("")) {
                    ctrlListKeyFields(false, keyFieldGrupoProd, listKeyFieldsGeral);
                    TxtDescrGrupoProd.setText("");
                } else {
                    ctrlListKeyFields(true, keyFieldGrupoProd, listKeyFieldsGeral);
                }
            }
        });
        TxtCodGrupoProd.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !Nvl(TxtCodGrupoProd.getText()).equals("")) {
                int count = checkIfExists("TGFGRU", "CODGRUPOPROD", TxtCodGrupoProd.getText());
                if (count == 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não encontrado grupo de produto com código: " + TxtCodGrupoProd.getText()));
                    ModelDialog.getDialog().raise();
                    cleanFields(TxtCodGrupoProd, TxtDescrGrupoProd);
                } else {
                    Platform.runLater(() -> {
                        try {
                            conex = new DBParalelConex("SELECT ANALITICO, DESCRGRUPOPROD FROM TGFGRU WHERE CODGRUPOPROD = ?");
                            conex.addParameter(TxtCodGrupoProd.getText());
                            conex.createSet();
                            conex.rs.next();
                            if (conex.rs.getString(1).equals("S")) {
                                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                                        "Grupo de produtos não pode ser análitico."));
                                ModelDialog.getDialog().raise();
                                cleanFields(TxtCodGrupoProd, TxtDescrGrupoProd);
                            } else {
                                TxtDescrGrupoProd.setText(conex.rs.getString(2));
                            }
                        } catch (Exception ex) {
                            ModelException.setNewException(new ModelException(this.getClass(), null,
                                    "Erro ao tentar buscar descrição do grupo de produto", ex));
                            ModelException.getException().raise();
                            cleanFields(TxtCodGrupoProd, TxtDescrGrupoProd);
                        } finally {
                            conex.desconecta();
                        }
                    });
                }
            }
        });
        MaskField.NumberField(TxtCodEmpresa, 22);
        SearchFieldTable keyEmpresa = new SearchFieldTable(ImgEmpresa, "Empresa", 4,
                new String[]{"Código", "Razão Social", "Nome Fantasia", "CNPJ"},
                "SELECT CODEMP, RAZAOSOCIAL, NOMEFANTASIA, GET_MASK('CNPJ', CNPJ) \n" +
                        "FROM TSIEMP\n" +
                        "ORDER BY CODEMP");
        keyEmpresa.getMainStage().setOnCloseRequest(e -> {
            if (keyEmpresa.getKeyReturn() != null) {
                TxtCodEmpresa.setText(keyEmpresa.getKeyReturn().get(0));
                TxtDescrEmpresa.setText(keyEmpresa.getKeyReturn().get(2));
            }
        });
        KeyFields keyFieldEmpresa = new KeyFields(TxtCodEmpresa, "Empresa", "AND EMP.CODEMP = ?");
        TxtCodEmpresa.textProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !newV.equals(oldV)) {
                if (Nvl(newV).equals("")) {
                    ctrlListKeyFields(false, keyFieldEmpresa, listKeyFieldsGeral);
                    TxtDescrEmpresa.setText("");
                } else {
                    ctrlListKeyFields(true, keyFieldEmpresa, listKeyFieldsGeral);
                }
            }
        });
        TxtCodEmpresa.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !Nvl(TxtCodEmpresa.getText()).equals("")) {
                int count = checkIfExists("TSIEMP", "CODEMP", TxtCodEmpresa.getText());
                if (count == 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não encontrado empresa com código: " + TxtCodEmpresa.getText()));
                    ModelDialog.getDialog().raise();
                    cleanFields(TxtCodEmpresa, TxtDescrEmpresa);
                } else {
                    Platform.runLater(() -> {
                        try {
                            conex = new DBParalelConex("SELECT NOMEFANTASIA FROM TSIEMP WHERE CODEMP = ?");
                            conex.addParameter(TxtCodEmpresa.getText());
                            conex.createSet();
                            conex.rs.next();
                            TxtDescrEmpresa.setText(conex.rs.getString(1));
                        } catch (Exception ex) {
                            ModelException.setNewException(new ModelException(this.getClass(), null,
                                    "Erro ao tentar buscar descrição da empresa", ex));
                            ModelException.getException().raise();
                            cleanFields(TxtCodEmpresa, TxtDescrEmpresa);
                        } finally {
                            conex.desconecta();
                        }
                    });
                }
            }
        });
    }

    private void loadTableValues() {
        try {
            loadTableEstoque();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar atualizar valores para pesquisa\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void loadTableEstoque() {
        listEstoque.clear();
        LblEstTotal.setText("Estoque Total: ");
        LblEstDisp.setText("Total Disponível: ");
        LblEstReserv.setText("Total Reservado: ");
        try {
            conex = new DBParalelConex("SELECT PRO.CODPROD, LOC.CODLOCAL, EMP.CODEMP, PRO.DESCRPROD, EST.CONTROLE,\n" +
                    "LOC.DESCRLOCAL, EMP.NOMEFANTASIA, EST.TIPO, EST.ESTOQUE, EST.RESERVADO\n" +
                    "FROM TGFEST EST\n" +
                    "INNER JOIN TSIEMP EMP\n" +
                    "ON EST.CODEMP = EMP.CODEMP\n" +
                    "INNER JOIN TGFLOC LOC\n" +
                    "ON EST.CODLOCAL = LOC.CODLOCAL\n" +
                    "INNER JOIN TGFPRO PRO\n" +
                    "ON EST.CODPROD = PRO.CODPROD\n" +
                    "WHERE 1 = 1" + getConditions() + "\n" +
                    "ORDER BY 3, 1, 2, 5");
            for (KeyFields keyFields : listKeyFieldsGeral) {
                switch (keyFields.getKeyType()) {
                    case ComboBoxType:
                        conex.addParameter(keyFields.getComboBox().getValue());
                        break;
                    case TextFieldType:
                        conex.addParameter(keyFields.getTextField().getText());
                        break;
                }
            }
            conex.createSet();
            while (conex.rs.next()) {
                Estoque estoque = new Estoque(
                        conex.rs.getInt(1),
                        conex.rs.getInt(2),
                        conex.rs.getInt(3),
                        conex.rs.getString(4),
                        conex.rs.getString(5),
                        conex.rs.getString(6),
                        conex.rs.getString(7),
                        conex.rs.getString(8).charAt(0),
                        conex.rs.getFloat(9),
                        conex.rs.getFloat(10)
                );
                listEstoque.addAll(estoque);
                showTotalEstoque();
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar atualizar valores para pesquisa\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private TableColumn[] getTbEstoqueColumns() {
        TableColumn[] tbColunas = new TableColumn[10];
        tbColunas[0] = new ModelTableColumn<Estoque, Integer>("Cód. Emp.", "CodEmpresa");
        tbColunas[1] = new ModelTableColumn<Estoque, String>("Nome Fantasia", "DescrEmpresa");
        tbColunas[2] = new ModelTableColumn<Estoque, Integer>("Cód. Local", "CodLocal");
        tbColunas[3] = new ModelTableColumn<Estoque, String>("Descr. Local", "DescrLocal");
        tbColunas[4] = new ModelTableColumn<Estoque, Integer>("Cód. Prod.", "CodProd");
        tbColunas[5] = new ModelTableColumn<Estoque, String>("Descr. Produto", "DescrProd");
        tbColunas[6] = new ModelTableColumn<Estoque, String>("Controle", "Controle");
        tbColunas[7] = new ModelTableColumn<Estoque, String>("Situação", "strTipoEstoque");
        tbColunas[8] = new ModelTableColumn<Estoque, Float>("Estoque", "Estoque");
        tbColunas[9] = new ModelTableColumn<Estoque, Float>("Reservado", "Reservado");
        return tbColunas;
    }

    private String getConditions() {
        if (listKeyFieldsGeral.isEmpty()) return "";
        else {
            String retorno = "";
            for (KeyFields keyFields : listKeyFieldsGeral) {
                retorno = retorno + "\n" + keyFields.getQuery();
            }
            return retorno;
        }
    }

    private void showTotalEstoque() {
        Float estDisp = new Float(0);
        Float estReserv = new Float(0);
        for (Estoque estoque : listEstoque) {
            estDisp = estDisp + estoque.getEstoque();
            estReserv = estReserv + estoque.getReservado();
        }
        Float estTotal = estDisp + estReserv;
        LblEstTotal.setText("Estoque Total: " + estTotal);
        LblEstDisp.setText("Total Disponível: " + estDisp);
        LblEstReserv.setText("Total Reservado: " + estReserv);
    }

    private void ctrlListKeyFields(Boolean add, KeyFields keyFields, ObservableList<KeyFields> listKeyFields) {
        if (add) {
            if (!listKeyFields.contains(keyFields)) listKeyFields.add(keyFields);
            ImageView ImgKeyFiltred = new ImageView("/br/com/sinergia/properties/images/Icone_Com_Filtro.png");
            ImgKeyFiltred.setFitWidth(20);
            ImgKeyFiltred.setFitHeight(20);
            TtpGeral.setGraphic(ImgKeyFiltred);
        } else {
            listKeyFields.remove(keyFields);
            if (listKeyFields.size() == 0) {
                ImageView ImgKeyNotFiltred = new ImageView("/br/com/sinergia/properties/images/Icone_Sem_Filtro.png");
                ImgKeyNotFiltred.setFitWidth(20);
                ImgKeyNotFiltred.setFitHeight(20);
                TtpGeral.setGraphic(ImgKeyNotFiltred);
            }
        }
    }

    private void cleanFields(TextField textField, TextField textField2) {
        textField.clear();
        textField2.clear();
    }

    public class KeyFields {
        private KeyType keyType;
        private TextField textField;
        private ComboBox comboBox;
        private String key;
        private String query;

        public KeyFields(TextField textField, String key, String query) {
            super();
            setKeyType(KeyType.TextFieldType);
            setTextField(textField);
            setKey(key);
            setQuery(query);
        }

        public KeyFields(ComboBox comboBox, String key, String query) {
            super();
            setKeyType(KeyType.ComboBoxType);
            setComboBox(comboBox);
            setKey(key);
            setQuery(query);
        }

        public TextField getTextField() {
            return textField;
        }

        public void setTextField(TextField textField) {
            this.textField = textField;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public ComboBox getComboBox() {
            return comboBox;
        }

        public void setComboBox(ComboBox comboBox) {
            this.comboBox = comboBox;
        }

        public KeyType getKeyType() {
            return keyType;
        }

        public void setKeyType(KeyType keyType) {
            this.keyType = keyType;
        }
    }

    public enum KeyType {
        ComboBoxType, TextFieldType;
    }
}
