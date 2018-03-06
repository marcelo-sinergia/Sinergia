package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.database.dicionario.DDOpcoes;
import br.com.sinergia.functions.extendeds.SearchFieldTable;
import br.com.sinergia.functions.modules.CtrlAcesso;
import br.com.sinergia.functions.natives.ControllerStatus;
import br.com.sinergia.functions.natives.CtrlStatus;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.models.usage.CadInterface;
import br.com.sinergia.models.usage.GrupoProd;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.natives.Functions.*;

public class CadGrupoProdController implements Initializable, CadInterface {

    private DBParalelConex conex;
    private CtrlAcesso AcessoTela;
    private boolean isAtualizando = true;
    private ControllerStatus classStatus = ControllerStatus.Nenhum;
    private GrupoProd grupoProd = new GrupoProd();
    private GrupoProd grupoProdPai = new GrupoProd();

    @FXML
    private AnchorPane PanelFundo;
    @FXML
    private JFXTreeView<GrupoProd> TreeGrupoProd;
    @FXML
    private CheckBox CkbAnalitico;
    @FXML
    private ToggleGroup GroupValEst;
    @FXML
    private RadioButton RadNEstoque, RadLEstoque, RadEEstoque, RadAEstoque;
    @FXML
    private JFXButton BtnAtualizar, BtnAdicionar, BtnSalvar, BtnCancelar, BtnEditar, BtnDuplicar, BtnExcluir;
    @FXML
    private JFXTextField TxtCodGrupoProd, TxtDescrGrupoProd, TxtCodGrupoPai, TxtDescrGrupoPai;
    @FXML
    private ImageView ImgCodGrupo, ImgGrupoPai;
    @FXML
    private Label LblAcao, LblFieldGrupoPai;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClass();
        loadTableValues();
    }

    @Override
    public void loadClass() {
        try {
            setLayoutTab(PanelFundo);
            try {
                AcessoTela = new CtrlAcesso("Grupo de Produtos", User.getCurrent().getCódUsu());
            } catch (Error ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage()));
                ModelException.getException().raise();
                Platform.runLater(() -> {
                    try {
                        AppObjects.getAppObjects().getTelasAbertas().remove("Grupo de Produtos"); //ToRemove
                        AppObjects.getAppObjects().getAbaPane().getTabs().remove(AppObjects.getAppObjects().getAbaPane().getSelectionModel().getSelectedIndex());
                    } catch (Exception ex1) {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar finalizar tela, contate o suporte\n" + ex1, ex1));
                        ModelException.getException().raise();
                        System.exit(0);
                    }
                });
            }
            fieldEstructure();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar aplicar Layout na tela de Grupo de Produtos\n" + ex, ex));
            ModelException.getException().raise();
        }
    }

    @Override
    public void fieldEstructure() {
        BtnAtualizar.setOnAction(e -> CtrlBtns(CtrlStatus.Atualizar));
        BtnAdicionar.setOnAction(e -> CtrlBtns(CtrlStatus.Adicionar));
        BtnSalvar.setOnAction(e -> CtrlBtns(CtrlStatus.Salvar));
        BtnEditar.setOnAction(e -> CtrlBtns(CtrlStatus.Editar));
        BtnCancelar.setOnAction(e -> CtrlBtns(CtrlStatus.Cancelar));
        BtnDuplicar.setOnAction(e -> CtrlBtns(CtrlStatus.Duplicar));
        BtnExcluir.setOnAction(e -> CtrlBtns(CtrlStatus.Excluir));
        TreeGrupoProd.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && oldV != newV &&
                    classStatus.getStatus() != ControllerStatus.Adicionando) {
                ModelTreeGrupoProd selected = (ModelTreeGrupoProd) newV;
                showInForm(selected.getCoreValues());
            }
        });
        MaskField.NumberField(TxtCodGrupoProd, 10);
        TxtCodGrupoProd.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (classStatus.getStatus() == ControllerStatus.Editando) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não é permitido alterar o código do Grupo de Produto\n" +
                                    "Caso necessário, exclua este e cadastre novamente."));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> TxtCodGrupoProd.setText(oldV));
                } else if (classStatus.getStatus() == ControllerStatus.Adicionando) {
                    if (!notifyEdit(() -> getGrupoProd().setCodGrupoProd(getOnlyNumber(newV))))
                        runEdit(() -> TxtCodGrupoProd.setText(oldV));
                }
            }
        });
        TxtCodGrupoProd.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV && !isAtualizando &&
                    classStatus.getStatus() == ControllerStatus.Nenhum) {
                Integer codTyped = getOnlyNumber(Nvl(TxtCodGrupoProd.getText()));
                if (checkIfExists("TGFGRU", "CODGRUPOPROD", codTyped) == 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não encontrado Grupo de Produto com código igual a " + codTyped));
                    ModelDialog.getDialog().raise();
                    setTreeItemSelected(0);
                } else {
                    setTreeItemSelected(codTyped);
                }
            }
        });
        SearchFieldTable keyGrupoProd = new SearchFieldTable(ImgCodGrupo, "Grupo de Produto", 5,
                new String[]{"Cód. Grupo Prod.", "Descr. Grupo Prod.", "Cód. Grupo Pai", "Analítico", "Val. Estoque"},
                "SELECT CODGRUPOPROD, DESCRGRUPOPROD, CODGRUPAI, \n" +
                        "CASE WHEN ANALITICO = 'S' THEN 'Sim'\n" +
                        "ELSE 'Não' END AS ANALITICO, \n" +
                        "CASE WHEN VALEST = 'N' THEN 'Não valida estoque'\n" +
                        "WHEN VALEST = 'L' THEN 'Valida pelo Local'\n" +
                        "WHEN VALEST = 'E' THEN 'Valida pela Empresa'\n" +
                        "WHEN VALEST = 'A' THEN 'Valida pela Empresa/Local'\n" +
                        "ELSE 'Não programada' END AS VALEST \n" +
                        "FROM TGFGRU ORDER BY CODGRUPOPROD");
        keyGrupoProd.getMainStage().setOnCloseRequest(e -> {
            if (keyGrupoProd.getKeyReturn() != null) {
                setTreeItemSelected(Integer.valueOf(keyGrupoProd.getKeyReturn().get(0)));
            }
        });
        MaskField.MaxCharField(TxtDescrGrupoProd, 30);
        TxtDescrGrupoProd.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (!notifyEdit(() -> getGrupoProd().setDescrGrupoProd(newV)))
                    runEdit(() -> TxtDescrGrupoProd.setText(oldV));
            }
        });
        CkbAnalitico.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV) {
                if (TreeGrupoProd.getSelectionModel().getSelectedItem() != null &&
                        !TreeGrupoProd.getSelectionModel().getSelectedItem().isLeaf() && !newV) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não foi possível colocar Grupo de Produtos como analítico\n" +
                                    "O Grupo em questão já tem grupos filhos."));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> CkbAnalitico.setSelected(oldV));
                } else {
                    if (!notifyEdit(() -> getGrupoProd().setAnalitico(newV)))
                        runEdit(() -> CkbAnalitico.setSelected(oldV));
                }
            }
        });
        GroupValEst.selectedToggleProperty().addListener((obs, oldV, newV) -> { //Evento só é ativado quando muda a seleção
            if (!isAtualizando && newV != null) {
                if (!notifyEdit(() -> getGrupoProd().setValEstoque(getRadValEst())))
                    runEdit(() -> oldV.setSelected(true));
            }
        });
        MaskField.MaxCharField(TxtCodGrupoPai, 10);
        TxtCodGrupoPai.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF && getOnlyNumber(Nvl(TxtCodGrupoPai.getText())) != (getGrupoProdPai().getCodGrupoProd())) {
                Integer codSearch = getOnlyNumber(Nvl(TxtCodGrupoPai.getText()));
                getGrupoProdPai(codSearch);
            }
        });
        MaskField.MaxCharField(TxtDescrGrupoPai, 30);
        SearchFieldTable keyGrupoProdPai = new SearchFieldTable(ImgGrupoPai, "Grupo de Produto", 5,
                new String[]{"Cód. Grupo Prod.", "Descr. Grupo Prod.", "Cód. Grupo Pai", "Analítico", "Val. Estoque"},
                "SELECT CODGRUPOPROD, DESCRGRUPOPROD, CODGRUPAI, \n" +
                        "CASE WHEN ANALITICO = 'S' THEN 'Sim'\n" +
                        "ELSE 'Não' END AS ANALITICO, \n" +
                        "CASE WHEN VALEST = 'N' THEN 'Não valida estoque'\n" +
                        "WHEN VALEST = 'L' THEN 'Valida pelo Local'\n" +
                        "WHEN VALEST = 'E' THEN 'Valida pela Empresa'\n" +
                        "WHEN VALEST = 'A' THEN 'Valida pela Empresa/Local'\n" +
                        "ELSE 'Não programada' END AS VALEST \n" +
                        "FROM TGFGRU ORDER BY CODGRUPOPROD");
        keyGrupoProdPai.getMainStage().setOnCloseRequest(e -> {
            if (keyGrupoProdPai.getKeyReturn() != null) {
                if (keyGrupoProdPai.getKeyReturn().get(3).equals("Não")) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Grupo de Produtos sintético não pode ser marcado como registro pai"));
                    ModelDialog.getDialog().raise();
                    e.consume();
                } else {
                    setGrupoProdPai(new GrupoProd(
                            Integer.valueOf(keyGrupoProdPai.getKeyReturn().get(0)),
                            Integer.valueOf(keyGrupoProdPai.getKeyReturn().get(2)),
                            keyGrupoProdPai.getKeyReturn().get(1),
                            getMapKeyByValue(DDOpcoes.getListaValEstoque(), keyGrupoProdPai.getKeyReturn().get(4)).toString(),
                            ToBoo(keyGrupoProdPai.getKeyReturn().get(3))));
                    TxtCodGrupoPai.setText(getGrupoProdPai().getCodGrupoProd().toString());
                    TxtDescrGrupoPai.setText(getGrupoProdPai().getDescrGrupoProd());
                }
            }
        });
    }

    @Override
    public void loadTableValues() {
        try {
            conex = new DBParalelConex("SELECT CODGRUPOPROD, DESCRGRUPOPROD, VALEST, ANALITICO FROM TGFGRU WHERE CODGRUPAI = -1 ORDER BY CODGRUPOPROD");
            conex.createSet();
            ModelTreeGrupoProd<GrupoProd> rootNode = new ModelTreeGrupoProd("Raiz");
            TreeGrupoProd.setRoot(rootNode);
            TreeGrupoProd.setShowRoot(false);
            ArrayList<GrupoProd> arrayNode = new ArrayList<>();
            while (conex.rs.next()) {
                GrupoProd grupoProd = new GrupoProd(
                        conex.rs.getInt(1),
                        -1,
                        conex.rs.getString(2),
                        conex.rs.getString(3),
                        ToBoo(conex.rs.getString(4))
                );
                arrayNode.add(grupoProd);
            }
            Boolean hasError = false;
            for (GrupoProd Node : arrayNode) {
                try {
                    if (!hasError) getNodeFilho(rootNode, Node);
                } catch (Exception ex) {
                    hasError = true;
                    ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar árvore de Grupo de Produtos\n" +
                            ex.getMessage(), ex));
                    ModelException.getException().raise();
                }
            }
            TreeGrupoProd.getSelectionModel().clearAndSelect(0);
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar árvore de Grupo de Produtos\n" + ex, ex));
            ModelException.getException().raise();
            TreeGrupoProd.setRoot(null);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar árvore de Grupo de Produtos\n" + ex, ex));
            ModelException.getException().raise();
            TreeGrupoProd.setRoot(null);
        } finally {
            conex.desconecta();
        }
    }

    private void getNodeFilho(TreeItem<GrupoProd> rootNode, GrupoProd Node) throws Exception {
        try {
            conex = new DBParalelConex("SELECT CODGRUPOPROD, CODGRUPAI, DESCRGRUPOPROD, VALEST, ANALITICO\n" +
                    "FROM TGFGRU WHERE CODGRUPAI = ? ORDER BY CODGRUPOPROD", true);
            conex.addParameter(Node.getCodGrupoProd());
            conex.createSet();
            ModelTreeGrupoProd<GrupoProd> treeItem = new ModelTreeGrupoProd(Node.getCodGrupoProd() + " - " + Node.getDescrGrupoProd());
            treeItem.setCoreValues(Node);
            rootNode.getChildren().add(treeItem);
            ArrayList<GrupoProd> arrayNodeFilho = new ArrayList<>();
            while (conex.rs.next()) {
                GrupoProd grupoProd = new GrupoProd(
                        conex.rs.getInt(1),
                        conex.rs.getInt(2),
                        conex.rs.getString(3),
                        conex.rs.getString(4),
                        ToBoo(conex.rs.getString(5))
                );
                arrayNodeFilho.add(grupoProd);
            }
            for (GrupoProd NodeFilho : arrayNodeFilho) {
                getNodeFilho(treeItem, NodeFilho);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    private void showInForm(GrupoProd grupoProd) {
        try {
            isAtualizando = true;
            classStatus.setStatus(ControllerStatus.Nenhum);
            setGrupoProd(grupoProd);
            TxtCodGrupoProd.setText(grupoProd.getCodGrupoProd().toString());
            CkbAnalitico.setSelected(grupoProd.getAnalitico());
            TxtDescrGrupoProd.setText(grupoProd.getDescrGrupoProd());
            setRadValEst(grupoProd.getValEstoque());
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar exibir Grupo de Produto\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            isAtualizando = false;
        }
    }

    @Override
    public void CtrlBtns(CtrlStatus action) {
        switch (action) {
            case ReloadButton:
                BtnAtualizar.setDisable(false);
                BtnAdicionar.setDisable(!AcessoTela.getInseri());
                BtnSalvar.setDisable(true);
                BtnCancelar.setDisable(true);
                BtnEditar.setDisable(!AcessoTela.getAltera());
                BtnDuplicar.setDisable(!AcessoTela.getInseri());
                BtnExcluir.setDisable(!AcessoTela.getExclui());
                break;
            case Limpar:
                showInForm(new GrupoProd());
                break;
            case Atualizar:
                classStatus.setStatus(ControllerStatus.Nenhum);
                showFieldsGrupoPai(false);
                runEdit(() -> {
                    TxtCodGrupoPai.clear();
                    TxtDescrGrupoPai.clear();
                });
                if (!Nvl(TxtDescrGrupoProd.getText()).equals("")) {
                    Integer cachedCod = getOnlyNumber(TxtCodGrupoProd.getText());
                    loadTableValues();
                    setTreeItemSelected(cachedCod);
                } else loadTableValues();
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Adicionar:
                if (TreeGrupoProd.getSelectionModel().getSelectedItem() != null) {
                    ModelTreeGrupoProd treeGrupoProd = (ModelTreeGrupoProd) TreeGrupoProd.getSelectionModel().getSelectedItem();
                    if (treeGrupoProd.getCoreValues().getAnalitico()) { //Recicla ele
                        TxtCodGrupoPai.setText(treeGrupoProd.getCoreValues().getCodGrupoProd().toString());
                        TxtDescrGrupoPai.setText(treeGrupoProd.getCoreValues().getDescrGrupoProd());
                        setGrupoProdPai(treeGrupoProd.getCoreValues());
                    }
                }
                TreeGrupoProd.getSelectionModel().clearSelection();
                showInForm(new GrupoProd());
                runEdit(() -> TxtCodGrupoProd.clear());
                classStatus.setStatus(ControllerStatus.Adicionando);
                showFieldsGrupoPai(true);
                BtnAtualizar.setDisable(true);
                BtnAdicionar.setDisable(true);
                BtnSalvar.setDisable(false);
                BtnCancelar.setDisable(false);
                BtnEditar.setDisable(true);
                BtnDuplicar.setDisable(true);
                BtnExcluir.setDisable(true);
                break;
            case Salvar:
                if (validChanges()) {
                    switch (classStatus.getStatus()) {
                        case Adicionando:
                            Adiciona(getGrupoProd());
                            break;
                        case Editando:
                            Altera(getGrupoProd());
                            break;
                        case Nenhum:
                            ModelException.setNewException(new ModelException(this.getClass(), null,
                                    "Erro no método de salvar\nValor recebido diferente do esperado!"));
                            ModelException.getException().raise();
                            break;
                    }
                }
                break;
            case Cancelar:
                CtrlBtns(CtrlStatus.Atualizar);
                break;
            case Editar:
                classStatus.setStatus(ControllerStatus.Editando);
                BtnAtualizar.setDisable(true);
                BtnAdicionar.setDisable(true);
                BtnSalvar.setDisable(false);
                BtnCancelar.setDisable(false);
                BtnEditar.setDisable(true);
                BtnDuplicar.setDisable(true);
                BtnExcluir.setDisable(true);
                break;
            case Excluir:
                ModelTreeGrupoProd treeGrupoProd = (ModelTreeGrupoProd) TreeGrupoProd.getSelectionModel().getSelectedItem();
                ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                        "Deseja realmente excluir o Grupo de Produto:\n" +
                                treeGrupoProd.getCoreValues().getCodGrupoProd() + " - " + treeGrupoProd.getCoreValues().getDescrGrupoProd() + " ?"));
                ButtonType[] Btns = new ButtonType[2];
                Btns[0] = new ButtonType("Confirmar");
                Btns[1] = new ButtonType("Cancelar");
                ModelDialogButton.getDialogButton().createButton(Btns);
                if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Cancelar
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                            "Operação cancelada pelo usuário"));
                    ModelDialog.getDialog().raise();
                } else {
                    Remove(treeGrupoProd.getCoreValues().getCodGrupoProd());
                }
                break;
            default:
                System.err.println("CadGrupoProdController.CtrlBtns(" + action + ") not programmed!");
                break;
        }
    }

    private void Adiciona(GrupoProd grupoProd) {
        try {
            conex = new DBParalelConex("INSERT INTO TGFGRU\n" +
                    "(CODGRUPOPROD, DESCRGRUPOPROD, CODGRUPAI, ANALITICO, VALEST)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?)");
            conex.addParameter(grupoProd.getCodGrupoProd());
            conex.addParameter(grupoProd.getDescrGrupoProd());
            if (getGrupoProdPai().getCodGrupoProd() == 0) conex.addParameter(-1);
            else conex.addParameter(getGrupoProdPai().getCodGrupoProd());
            conex.addParameter(StrFromBoo(grupoProd.getAnalitico()));
            conex.addParameter(grupoProd.getValEstoque());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Grupo de Produtos cadastrado com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar Grupo de Produtos\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Altera(GrupoProd grupoProd) {
        if (grupoProd.getCodGrupoProd() == 0) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Não é possível efetuar alterações no Grupo de Produtos 0"));
            ModelDialog.getDialog().raise();
            return;
        }
        try {
            conex = new DBParalelConex("UPDATE TGFGRU\n" +
                    "SET DESCRGRUPOPROD = ?,\n" +
                    "ANALITICO = ?,\n" +
                    "VALEST = ?\n" +
                    "WHERE CODGRUPOPROD = ?");
            conex.addParameter(grupoProd.getDescrGrupoProd());
            conex.addParameter(StrFromBoo(grupoProd.getAnalitico()));
            conex.addParameter(grupoProd.getValEstoque());
            conex.addParameter(grupoProd.getCodGrupoProd());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Grupo de Produtos alterado com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar alterar Grupo de Produto\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Remove(Integer codGrupo) {
        if (codGrupo == 0) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Não é possível excluir o Grupo de Produtos 0"));
            ModelException.getException().raise();
            return;
        }
        int countProdWithCodGrupoProd = checkIfExists("TGFPRO", "CODGRUPOPROD", codGrupo);
        if (countProdWithCodGrupoProd > 0) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Grupo de Produto vinculado a algum(ns) produto(s)\n" +
                            "Para excluir, desfaça o vínculo e tente novamente."));
            ModelDialog.getDialog().raise();
            return;
        }
        try {
            conex = new DBParalelConex("DELETE TGFGRU WHERE CODGRUPOPROD = ?");
            conex.addParameter(codGrupo);
            conex.run();
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Grupo de Produtos excluído com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar excluir Grupo de Produtos\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    @Override
    public Boolean validChanges() {
        if (classStatus.getStatus() == ControllerStatus.Adicionando && Nvl(TxtCodGrupoProd.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Informe um Código para o Grupo de Produto!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (classStatus.getStatus() == ControllerStatus.Adicionando &&
                checkIfExists("TGFGRU", "CODGRUPOPROD", TxtCodGrupoProd.getText()) > 0) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Já existe um Grupo de Produto com este Código!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (Nvl(TxtCodGrupoProd.getText()).equals("") && classStatus.getStatus() == ControllerStatus.Editando) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Não foi possível capturar qual o Grupo de Produto para alterar\n" +
                            "Favor contate o suporte e informe o ocorrido!"));
            ModelException.getException().raise();
            return false;
        } else if (Nvl(TxtDescrGrupoProd.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Descrição do Grupo de Produtos não pode ser vazia!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (getGrupoProdPai().getCodGrupoProd() != 0 && !getGrupoProdPai().getAnalitico()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Grupo de Produtos Pai é sintético\n" +
                            "Assim, não pode ter registros filhos"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (!CkbAnalitico.isSelected() &&
                classStatus.getStatus() == ControllerStatus.Editando &&
                !TreeGrupoProd.getSelectionModel().getSelectedItem().isLeaf()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Grupo de Produtos tem registros filhos\n" +
                            "Assim, não pode ser marcado como sintético"));
            ModelDialog.getDialog().raise();
            return false;

        } else return true;
    }

    @Override
    public TableColumn[] getTableColumns() {
        return new TableColumn[0];
    }

    @Override
    public void ctrlLinhasTab(int pos) {

    }

    private void setTreeItemSelected(Integer codGrupoProd) {
        /*-----------------------------------------------------------
        Por algum motivo, é necessário utilizar o .equals() e NÃO o ==. (Mesmo sendo Integer)
        Caso use o "==", ele não bate, por algum erro no Model.
         -------------------------------------------------------------*/
        for (TreeItem<GrupoProd> node : TreeGrupoProd.getRoot().getChildren()) {
            ModelTreeGrupoProd treeGrupoProd = (ModelTreeGrupoProd) node;
            if (treeGrupoProd.getCoreValues().getCodGrupoProd().equals(codGrupoProd)) {
                node.setExpanded(true);
                TreeGrupoProd.getSelectionModel().select(node);
                break;
            } else {
                seekCodInTree(node, codGrupoProd);
            }
        }
    }

    private void seekCodInTree(TreeItem<GrupoProd> treeItem, Integer codGrupoProd) {
        for (TreeItem<GrupoProd> node : treeItem.getChildren()) {
            ModelTreeGrupoProd treeGrupoProd = (ModelTreeGrupoProd) node;
            if (treeGrupoProd.getCoreValues().getCodGrupoProd().equals(codGrupoProd)) {
                node.setExpanded(true);
                TreeGrupoProd.getSelectionModel().select(node);
                break;
            } else {
                seekCodInTree(node, codGrupoProd);
            }
        }
    }

    @Override
    public void runEdit(Runnable changes) {
        isAtualizando = true;
        changes.run();
        isAtualizando = false;
    }

    @Override
    public Boolean notifyEdit(Runnable changes) {
        if (classStatus.getStatus() != ControllerStatus.Adicionando) {
            if (!AcessoTela.getAltera()) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Usuário sem acesso para alterações na tela de Grupo de Produtos"));
                ModelDialog.getDialog().raise();
                return false;
            } else {
                if (!isAtualizando) CtrlBtns(CtrlStatus.Editar);
                changes.run();
                return true;
            }
        } else {
            changes.run();
            return true;
        }
    }

    @Override
    public void setMessage(String mensagem) {
        LblAcao.setText(mensagem);
        LblAcao.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2000), //2 Segundos
                ae -> LblAcao.setVisible(false)));
        timeline.play();
    }

    private void getGrupoProdPai(Integer codGrupo) {
        TxtCodGrupoPai.clear();
        TxtDescrGrupoPai.clear();
        if (checkIfExists("TGFGRU", "CODGRUPOPROD", codGrupo) == 0) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Não encontrado Grupo de Produto Pai com código igual a " + codGrupo));
            ModelDialog.getDialog().raise();
            setGrupoProdPai(new GrupoProd());
        } else {
            try {
                conex = new DBParalelConex("SELECT DESCRGRUPOPROD, CODGRUPAI, ANALITICO, VALEST FROM TGFGRU\n" +
                        "WHERE CODGRUPOPROD = ?");
                conex.addParameter(codGrupo);
                conex.createSet();
                conex.rs.next();
                if (conex.rs.getString(3).equals("N")) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Grupo de Produto Pai informado é sintético\n" +
                                    "Não pode ter registros filhos."));
                    ModelDialog.getDialog().raise();
                    setGrupoProdPai(new GrupoProd());
                } else {
                    setGrupoProdPai(new GrupoProd(codGrupo,
                            conex.rs.getInt(2),
                            conex.rs.getString(1),
                            conex.rs.getString(4),
                            ToBoo(conex.rs.getString(3))));
                    TxtCodGrupoPai.setText(getGrupoProdPai().getCodGrupoProd().toString());
                    TxtDescrGrupoPai.setText(getGrupoProdPai().getDescrGrupoProd());
                }
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar Grupo de Produto Pai\n" + ex, ex));
                ModelException.getException().raise();
                setGrupoProdPai(new GrupoProd());
            } finally {
                conex.desconecta();
            }
        }
    }

    public GrupoProd getGrupoProd() {
        return grupoProd;
    }

    public void setGrupoProd(GrupoProd grupoProd) {
        this.grupoProd = grupoProd;
    }

    private void showFieldsGrupoPai(Boolean value) {
        LblFieldGrupoPai.setVisible(value);
        TxtCodGrupoPai.setVisible(value);
        ImgGrupoPai.setVisible(value);
        TxtDescrGrupoPai.setVisible(value);
    }

    private void setRadValEst(Object value) {
        if (value.getClass() == Character.class) {
            Character valor = (Character) value;
            switch (valor) {
                case 'N':
                    RadNEstoque.setSelected(true);
                    break;
                case 'L':
                    RadLEstoque.setSelected(true);
                    break;
                case 'E':
                    RadEEstoque.setSelected(true);
                    break;
                case 'A':
                    RadAEstoque.setSelected(true);
                    break;
                default:
                    System.err.println("CadGrupoController.setRadValEst(" + value + ") not programmed!");
            }
        } else if (value.getClass() == String.class) {
            String valor = (String) value;
            switch (valor) {
                case "Não valida estoque":
                    RadNEstoque.setSelected(true);
                    break;
                case "Valida pelo Local":
                    RadLEstoque.setSelected(true);
                    break;
                case "Valida pela Empresa":
                    RadEEstoque.setSelected(true);
                    break;
                case "Valida pela Empresa/Local":
                    RadAEstoque.setSelected(true);
                    break;
            }
        } else {
            System.err.println("CadGrupoController.setRadValEst com classe " + value.getClass() + " não programada!");
        }
    }

    private Character getRadValEst() {
        Character retorno = 'Z';
        if (RadNEstoque.isSelected()) return 'N';
        else if (RadLEstoque.isSelected()) return 'L';
        else if (RadEEstoque.isSelected()) return 'E';
        else if (RadAEstoque.isSelected()) return 'A';
        else {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar capturar valor de 'Validação de Estoque'\nValor capturado não programado"));
            ModelException.getException().raise();
        }
        return retorno;
    }

    public GrupoProd getGrupoProdPai() {
        return grupoProdPai;
    }

    public void setGrupoProdPai(GrupoProd grupoProdPai) {
        this.grupoProdPai = grupoProdPai;
    }

    public class ModelTreeGrupoProd<T> extends TreeItem<T> {

        private GrupoProd CoreValues;

        public ModelTreeGrupoProd(T value) {
            this.setValue(value);
        }

        public void setCoreValues(GrupoProd coreValues) {
            this.CoreValues = coreValues;
        }

        public GrupoProd getCoreValues() {
            return this.CoreValues;
        }
    }
}
