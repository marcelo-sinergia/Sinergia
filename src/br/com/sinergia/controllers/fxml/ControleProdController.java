package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.database.dicionario.DDOpcoes;
import br.com.sinergia.functions.modules.CtrlAcesso;
import br.com.sinergia.functions.natives.ControllerStatus;
import br.com.sinergia.functions.natives.CtrlStatus;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.models.usage.ControleProd;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;

import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.natives.Functions.*;

public class ControleProdController implements Initializable {

    DBParalelConex conex;
    private Integer CodProd, idxChangeCtrl;
    private CtrlAcesso AcessoTela;
    private Boolean isAtualizando = true;
    private ControleProd controleProd = new ControleProd();
    private ControllerStatus classStatus = ControllerStatus.Nenhum;

    @FXML
    private ComboBox<String> CbbControle;
    @FXML
    private TextField TxtDescrControle;
    @FXML
    private JFXButton BtnAtualizar, BtnAdicionar, BtnSalvar, BtnCancelar, BtnExcluir;
    @FXML
    private ListView<String> ListaControle;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClass();
    }

    private void loadClass() {
        fieldEstructure();
        CtrlBtns(CtrlStatus.ReloadButton);
    }

    private void fieldEstructure() {
        AcessoTela = new CtrlAcesso("Controle Prod.", User.getCurrent().getCódUsu());
        BtnAtualizar.setOnAction(e -> CtrlBtns(CtrlStatus.Atualizar));
        BtnAdicionar.setOnAction(e -> CtrlBtns(CtrlStatus.Adicionar));
        BtnSalvar.setOnAction(e -> CtrlBtns(CtrlStatus.Salvar));
        BtnCancelar.setOnAction(e -> CtrlBtns(CtrlStatus.Cancelar));
        BtnExcluir.setOnAction(e -> CtrlBtns(CtrlStatus.Excluir));
        MaskField.MaxCharField(TxtDescrControle, 30);
        CbbControle.setItems(FXCollections.observableArrayList(new ArrayList(DDOpcoes.getListaControle().values())));
        CbbControle.setValue("Sem controle");
        ListaControle.itemsProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && oldV != newV && newV.size() > 0) {
                ListaControle.getSelectionModel().clearSelection();
                ListaControle.getSelectionModel().select(0);
            }
        });
        ListaControle.setCellFactory(TextFieldListCell.forListView());
        CbbControle.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (!AcessoTela.getInseri()) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Usuário logado não tem acesso para incluir controle adicional!"));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> CbbControle.setValue(oldV));
                } else if (getControleProd().getTipCtrl().equals('N')) {
                    classStatus.setStatus(ControllerStatus.Adicionando);
                    TxtDescrControle.requestFocus();
                } else if (newV.equals("Sem controle") && !getControleProd().getTipCtrl().equals('N')) {
                    ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                            "Deseja realmente remover " + CbbControle.getValue() + "?"));
                    ButtonType[] Btns = new ButtonType[2];
                    Btns[0] = new ButtonType("Confirmar");
                    Btns[1] = new ButtonType("Cancelar");
                    ModelDialogButton.getDialogButton().createButton(Btns);
                    if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Cancelar
                        runEdit(() -> CbbControle.setValue(oldV));
                    } else {
                        try {
                            conex = new DBParalelConex("DELETE TGFCTRL WHERE CODCTRL = ?");
                            conex.addParameter(getControleProd().getCodCtrl());
                            conex.run();
                        } catch (Exception ex) {
                            ModelException.setNewException(new ModelException(this.getClass(), null,
                                    "Erro ao tentar retirar controle adicional do produto\n" + ex, ex));
                            ModelException.getException().raise();
                        } finally {
                            loadControleProd();
                            conex.desconecta();
                        }
                    }
                } else if (!getControleProd().getTipCtrl().equals('N')
                        && !newV.equals("Sem controle")
                        && getControleProd().getCodCtrl() != 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não é possível alterar o tipo de controle deste produto\n" +
                                    "Coloque-o primeiro como sem controle"));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> CbbControle.setValue(oldV));
                }
            }
        });
        TxtDescrControle.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (CbbControle.getValue().equals("Sem controle")) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Cadastre algum controle para inserir descrição de controle!"));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> TxtDescrControle.setText(oldV));
                } else if (!AcessoTela.getInseri()) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Usuário logado não tem acesso para alterar controle adicional!"));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> TxtDescrControle.setText(oldV));
                }
            }
        });
        TxtDescrControle.focusedProperty().addListener((obs, wasF, isF) -> {
            if (!isAtualizando) {
                if (wasF && !CbbControle.getValue().equals("Sem controle") &&
                        TxtDescrControle.getText().equals("") &&
                        classStatus.getStatus() == ControllerStatus.Adicionando &&
                        !CbbControle.isFocused()) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Descrição do controle não pode ser vazia!"));
                    ModelDialog.getDialog().raise();
                    Platform.runLater(() -> TxtDescrControle.requestFocus());
                }
                if (wasF && !TxtDescrControle.getText().equals(getControleProd().getDescrCtrl())) {
                    getControleProd().setDescrCtrl(TxtDescrControle.getText());
                    changeProdController(classStatus.getStatus());
                }
            }
        });
    }

    private void showInForm(ControleProd controleProd) {
        isAtualizando = true;
        try {
            if (controleProd.getCodCtrl() == 0) {
                CbbControle.setValue("Sem controle");
                TxtDescrControle.setText("");
                ListaControle.getItems().clear();
                CtrlBtns(CtrlStatus.ReloadButton);
            } else {
                CbbControle.setValue(DDOpcoes.getListaControle().get(controleProd.getTipCtrl()));
                TxtDescrControle.setText(controleProd.getDescrCtrl());
                ListaControle.getItems().clear();
                if (!controleProd.getListCtrl().isEmpty())
                    ListaControle.setItems(FXCollections.observableArrayList(controleProd.getListCtrl()));
                CtrlBtns(CtrlStatus.ReloadButton);
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar exibir Controle Adicional do Produto\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            isAtualizando = false;
        }
    }

    private void changeProdController(ControllerStatus status) {
        if (CbbControle.getValue().equals("Sem controle"))
            return; //Se caiu aqui, é alguma validação que perdeu foco. ignora.
        switch (status) {
            case Adicionando:
                ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                        "Deseja realmente cadastrar " + CbbControle.getValue() + "?\n" +
                                "Operação irreversível após novos lançamentos com controle"));
                ButtonType[] Btns = new ButtonType[2];
                Btns[0] = new ButtonType("Confirmar");
                Btns[1] = new ButtonType("Cancelar");
                ModelDialogButton.getDialogButton().createButton(Btns);
                if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Cancelar
                    CtrlBtns(CtrlStatus.Atualizar);
                } else {
                    try {
                        conex = new DBParalelConex("SELECT GET_COD('CODCTRL', 'TGFCTRL') AS COD FROM DUAL");
                        conex.createSet();
                        conex.rs.next();
                        getControleProd().setCodCtrl(conex.rs.getInt("COD"));
                        conex = new DBParalelConex("INSERT INTO TGFCTRL\n" +
                                "(CODCTRL, CODPROD, TIPCTRL, DESCRCTRL, LISTACTRL, DHCRIACAO, DHALTER, CODUSU)\n" +
                                "VALUES\n" +
                                "(?, ?, ?, ?, ?, ?, ?, ?)");
                        conex.addParameter(getControleProd().getCodCtrl());
                        conex.addParameter(getCodProd());
                        conex.addParameter(getMapKeyByValue(DDOpcoes.getListaControle(), CbbControle.getValue()));
                        conex.addParameter(getControleProd().getDescrCtrl());
                        conex.addParameter(null);
                        conex.addParameter(Timestamp.from(Instant.now()));
                        conex.addParameter(Timestamp.from(Instant.now()));
                        conex.addParameter(User.getCurrent().getCódUsu());
                        conex.run();
                    } catch (Exception ex) {
                        ModelException.setNewException(new ModelException(this.getClass(), null,
                                "Erro ao tentar cadastrar controle adicional\n" + ex, ex));
                        ModelException.getException().raise();
                    } finally {
                        CtrlBtns(CtrlStatus.Atualizar);
                    }
                }
                break;
            case Nenhum:
                try {
                    conex = new DBParalelConex("UPDATE TGFCTRL\n" +
                            "SET DESCRCTRL = ?,\n" +
                            "DHALTER = ?," +
                            "CODUSU = ?" +
                            "WHERE CODCTRL = ?");
                    conex.addParameter(getControleProd().getDescrCtrl());
                    conex.addParameter(Timestamp.from(Instant.now()));
                    conex.addParameter(User.getCurrent().getCódUsu());
                    conex.addParameter(getControleProd().getCodCtrl());
                    conex.run();
                } catch (Exception ex) {
                    ModelException.setNewException(new ModelException(this.getClass(), null,
                            "Erro ao tentar cadastrar controle adicional\n" + ex, ex));
                    ModelException.getException().raise();
                } finally {
                    CtrlBtns(CtrlStatus.Atualizar);
                }
                break;
        }
    }

    private void cadNewControl(String controle) {
        if (controle == null || controle.isEmpty() || Nvl(controle).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Nova lista para controle não pode ser vazia"));
            ModelDialog.getDialog().raise();
            return;
        }
        try {
            if (getControleProd().getListCtrl().size() == 1 && Nvl(getControleProd().getListCtrl().get(0)).equals("")) {
                getControleProd().getListCtrl().clear();
            }
            getControleProd().getListCtrl().add(controle);
            conex = new DBParalelConex("UPDATE TGFCTRL\n" +
                    "SET LISTACTRL = ?," +
                    "DHALTER = ?," +
                    "CODUSU = ?\n" +
                    "WHERE CODCTRL = ?");
            conex.addParameter(getStrControle(getControleProd().getListCtrl()));
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(getControleProd().getCodCtrl());
            conex.run();
            CtrlBtns(CtrlStatus.Atualizar);
            ListaControle.setEditable(false);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar nova linha para controle\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void editListMode() {
        classStatus.setStatus(ControllerStatus.Adicionando);
        idxChangeCtrl = ListaControle.getItems().size();
        if (idxChangeCtrl == 1 && Nvl(ListaControle.getItems().get(0)).equals("")) {
            idxChangeCtrl = 0;
            ListaControle.setEditable(true);
        } else {
            ListaControle.getItems().add(idxChangeCtrl, "");
            ListaControle.getSelectionModel().selectedIndexProperty().addListener((obs, oldV, newV) -> {
                if ((Integer) newV == idxChangeCtrl && classStatus.getStatus() == ControllerStatus.Adicionando)
                    ListaControle.setEditable(true);
                else ListaControle.setEditable(false);
            });
        }
        ListaControle.getSelectionModel().clearAndSelect(idxChangeCtrl);
    }

    private void excControl(ArrayList<String> control) {
        if (control.isEmpty() || control.size() == 0) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Exclusão impossível pois não há linhas selecionadas"));
            ModelDialog.getDialog().raise();
            return;
        } else {
            ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                    "Deseja realmente excluir o controle: " + control.toString()));
            ButtonType[] Btns = new ButtonType[2];
            Btns[0] = new ButtonType("Confirmar");
            Btns[1] = new ButtonType("Cancelar");
            ModelDialogButton.getDialogButton().createButton(Btns);
            if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Cancelar
                CtrlBtns(CtrlStatus.ReloadButton);
            } else {
                control.forEach(ctrl -> getControleProd().getListCtrl().remove(ctrl));
                try {
                    conex = new DBParalelConex("UPDATE TGFCTRL\n" +
                            "SET LISTACTRL = ?\n" +
                            "WHERE CODCTRL = ?");
                    conex.addParameter(getStrControle(getControleProd().getListCtrl()));
                    conex.addParameter(getControleProd().getCodCtrl());
                    conex.run();
                    loadControleProd();
                } catch (Exception ex) {
                    ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar remover linha de controle\n" + ex, ex));
                    ModelException.getException().raise();
                } finally {
                    conex.desconecta();
                }
            }
        }
    }

    private void loadControleProd() {
        if (getLogicNumber(getCodProd()) == -1) {
            CtrlBtns(CtrlStatus.Limpar);
        } else {
            try {
                conex = new DBParalelConex("SELECT COUNT(1) AS COUNT\n" +
                        "FROM TGFCTRL\n" +
                        "WHERE CODPROD = ?");
                conex.addParameter(getCodProd());
                conex.createSet();
                conex.rs.next();
                if (conex.rs.getInt("COUNT") == 0) {
                    setControleProd(new ControleProd());
                    showInForm(getControleProd());
                } else {
                    conex = new DBParalelConex("SELECT CODCTRL, DESCRCTRL, TIPCTRL, LISTACTRL FROM TGFCTRL\n" +
                            "WHERE CODPROD = ?");
                    conex.addParameter(getCodProd());
                    conex.createSet();
                    conex.rs.next();
                    setControleProd(new ControleProd(getCodProd(),
                            conex.rs.getInt("CODCTRL"),
                            conex.rs.getString("DESCRCTRL"),
                            conex.rs.getString("TIPCTRL"),
                            new ArrayList<>(Arrays.asList(Nvl(conex.rs.getString("LISTACTRL")).split(";")))));
                    showInForm(getControleProd());
                }
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar controle adicional do produto\n" + ex, ex));
                ModelException.getException().raise();
                setControleProd(new ControleProd());
                showInForm(getControleProd());
            } finally {
                conex.desconecta();
            }
        }
    }

    public void CtrlBtns(CtrlStatus action) {
        switch (action) {
            case ReloadButton:
                classStatus.setStatus(ControllerStatus.Nenhum);
                BtnAtualizar.setDisable(false);
                BtnAdicionar.setDisable(!AcessoTela.getInseri());
                BtnSalvar.setDisable(true);
                BtnCancelar.setDisable(true);
                BtnExcluir.setDisable(!AcessoTela.getExclui());
                break;
            case Limpar:
                showInForm(new ControleProd());
                break;
            case Atualizar:
                loadControleProd();
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Adicionar:
                if (CbbControle.getValue().equals("Sem controle")) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Cadastre algum controle para inserir lista de controle!"));
                    ModelDialog.getDialog().raise();
                    return;
                } else if (!getControleProd().getTipCtrl().equals('L')) {
                    Character tipControle = getControleProd().getTipCtrl();
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                            "No controle adicional por " + DDOpcoes.getDescrControle(tipControle) + " não existe lista\n" +
                                    "A " + DDOpcoes.getDescrControle(tipControle) + " é digitada no ato da Compra/Venda do produto"));
                    ModelDialog.getDialog().raise();
                    return;
                }
                editListMode();
                BtnAtualizar.setDisable(true);
                BtnAdicionar.setDisable(true);
                BtnSalvar.setDisable(false);
                BtnCancelar.setDisable(false);
                BtnExcluir.setDisable(true);
                break;
            case Salvar:
                cadNewControl(ListaControle.getItems().get(idxChangeCtrl));
                break;
            case Cancelar:
                ListaControle.setEditable(false);
                loadControleProd();
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Excluir:
                if (ListaControle.getSelectionModel().getSelectedItems() == null) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Selecione um controle para exclusão"));
                    ModelDialog.getDialog().raise();
                    return;
                }
                ArrayList<String> controlToExc = new ArrayList<>();
                ListaControle.getSelectionModel().getSelectedItems().forEach(control -> controlToExc.add(control));
                excControl(controlToExc);
                break;
            default:
                System.err.println("Erro!");
                break;
        }
    }

    private Boolean validChanges() {
        return null;
    }

    private void runEdit(Runnable changes) {
        isAtualizando = true;
        changes.run();
        isAtualizando = false;
    }

    private Boolean notifyEdit(Runnable changes) {
        if (!AcessoTela.getAltera()) {

        }
        return null;
    }

    private Integer getCodProd() {
        return CodProd;
    }

    public void setCodProd(Integer codProd) {
        if (codProd == null || codProd == 0) codProd = -1;
        CodProd = codProd;
        loadControleProd();
    }

    public ControleProd getControleProd() {
        return controleProd;
    }

    public void setControleProd(ControleProd controleProd) {
        this.controleProd = controleProd;
    }

    public String getStrControle(ArrayList<String> arrayControle) {
        String retorno = "";
        if (arrayControle.isEmpty()) return retorno;
        if (arrayControle.size() == 1) return arrayControle.get(0);
        for (int i = 0; i < arrayControle.size(); i++) {
            if (Nvl(arrayControle.get(i)).equals("")) continue;
            else if (i == 0) retorno = arrayControle.get(i);
            else retorno = retorno + ";" + arrayControle.get(i);
        }
        return retorno;
    }
}
