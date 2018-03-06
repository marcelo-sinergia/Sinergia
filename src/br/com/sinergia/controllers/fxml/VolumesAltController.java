package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.database.dicionario.ListaQuerys;
import br.com.sinergia.functions.extendeds.SearchFieldTable;
import br.com.sinergia.functions.extendeds.tableProperties.ModelTableColumn;
import br.com.sinergia.functions.modules.CtrlAcesso;
import br.com.sinergia.functions.natives.ControllerStatus;
import br.com.sinergia.functions.natives.CtrlStatus;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.models.usage.CadInterface;
import br.com.sinergia.models.usage.UnidadeAlt;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VolumesAltController implements Initializable, CadInterface {

    private DBParalelConex conex;
    private CtrlAcesso AcessoTela;
    private Integer CodProd, lastLoadedCodigo = -1, qtdLinhasTab = 0;
    private Boolean isAtualizando = true;
    private UnidadeAlt unidadeAlt = new UnidadeAlt();
    private String descrVolPrincipal = "";
    private String descrVolCached = "";
    private ControllerStatus classStatus = ControllerStatus.Nenhum;
    private ObservableList<UnidadeAlt> unidadeAltObsList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane PanelGrade, PanelForm;
    @FXML
    private TableView<UnidadeAlt> TbUnidadesAlt;
    @FXML
    private Label LblDHCriacao, LblAcao;
    @FXML
    private Spinner<Integer> SpnQtdOper;
    @FXML
    private ComboBox CbbVolume, CbbOper;
    @FXML
    private TextField TxtDescricao, TxtCodBarras;
    @FXML
    private JFXButton BtnFormGrade, BtnAtualizar, BtnAdicionar, BtnSalvar, BtnCancelar, BtnEditar, BtnDuplicar, BtnExcluir,
            BtnPrimeiro, BtnAnterior, BtnProximo, BtnUltimo;
    @FXML
    private ImageView ImgUnidade;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadClass();
    }

    public void loadClass() {
        try {
            TbUnidadesAlt.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            TbUnidadesAlt.setItems(unidadeAltObsList);
            TbUnidadesAlt.getColumns().clear();
            TbUnidadesAlt.getColumns().addAll(getTableColumns());
            try {
                AcessoTela = new CtrlAcesso("Unidades Alt.", User.getCurrent().getCódUsu());
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage()));
                ModelException.getException().raise();
                Platform.runLater(() -> {
                    try {
                        AppObjects.getAppObjects().getTelasAbertas().remove("Produtos"); //ToRemove
                        AppObjects.getAppObjects().getAbaPane().getTabs().remove(AppObjects.getAppObjects().getAbaPane().getSelectionModel().getSelectedIndex());
                    } catch (Exception ex1) {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar finalizar tela, contate o suporte\n" + ex1, ex1));
                        ModelException.getException().raise();
                        System.exit(0);
                    }
                });
            }
            TbUnidadesAlt.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null) {
                    showInFormMode(newV);
                    ctrlLinhasTab(TbUnidadesAlt.getSelectionModel().getSelectedIndex());
                } else {
                    CtrlBtns(CtrlStatus.Limpar);
                }
            });
            TbUnidadesAlt.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    PanelForm.setVisible(true);
                }
            });
            PanelForm.visibleProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    PanelGrade.setVisible(false);
                    ImageView ImgFormGrade = new ImageView("/br/com/sinergia/properties/images/Icone_Modo_Formulario.png");
                    ImgFormGrade.setFitWidth(30);
                    ImgFormGrade.setFitHeight(26);
                    BtnFormGrade.setGraphic(ImgFormGrade);
                    BtnFormGrade.setTooltip(new Tooltip("Voltar p/ Modo Grade"));
                } else {
                    PanelGrade.setVisible(true);
                    ImageView ImgFormGrade = new ImageView("/br/com/sinergia/properties/images/Icone_Modo_Grade.png");
                    ImgFormGrade.setFitWidth(30);
                    ImgFormGrade.setFitHeight(26);
                    BtnFormGrade.setGraphic(ImgFormGrade);
                    BtnFormGrade.setTooltip(new Tooltip("Voltar p/ Modo Formulário"));
                }
            });
            PanelForm.setVisible(false);
            BtnFormGrade.setOnAction(e -> PanelForm.setVisible(!PanelForm.isVisible()));
            BtnAtualizar.setOnAction(e -> CtrlBtns(CtrlStatus.Atualizar));
            BtnAdicionar.setOnAction(e -> CtrlBtns(CtrlStatus.Adicionar));
            BtnSalvar.setOnAction(e -> CtrlBtns(CtrlStatus.Salvar));
            BtnEditar.setOnAction(e -> CtrlBtns(CtrlStatus.Editar));
            BtnCancelar.setOnAction(e -> CtrlBtns(CtrlStatus.Cancelar));
            BtnDuplicar.setOnAction(e -> CtrlBtns(CtrlStatus.Duplicar));
            BtnExcluir.setOnAction(e -> CtrlBtns(CtrlStatus.Excluir));
            BtnPrimeiro.setOnAction(e -> CtrlBtns(CtrlStatus.Primeiro));
            BtnAnterior.setOnAction(e -> CtrlBtns(CtrlStatus.Anterior));
            BtnProximo.setOnAction(e -> CtrlBtns(CtrlStatus.Proximo));
            BtnUltimo.setOnAction(e -> CtrlBtns(CtrlStatus.Ultimo));
            fieldEstructure();
            CtrlBtns(CtrlStatus.ReloadButton);
        } catch (Error ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage()));
            ModelException.getException().raise();
            Platform.runLater(() -> {
                try {
                    AppObjects.getAppObjects().getTelasAbertas().remove("Produtos"); //ToRemove
                    AppObjects.getAppObjects().getAbaPane().getTabs().remove(AppObjects.getAppObjects().getAbaPane().getSelectionModel().getSelectedIndex());
                } catch (Exception ex1) {
                    ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar finalizar tela, contate o suporte\n" + ex1, ex1));
                    ModelException.getException().raise();
                    System.exit(0);
                }
            });
        }
    }

    public void fieldEstructure() {
        MaskField.SpnFieldCtrl(SpnQtdOper, 1, 999);
        ObservableList<String> listOper = FXCollections.observableArrayList();
        listOper.addAll("Multiplica", "Divide");
        CbbOper.setItems(listOper);
        CbbVolume.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != oldV && newV != null) {
                TxtDescricao.setText(ListaQuerys.getDescrVol(Functions.Nvl(CbbVolume.getValue().toString())));
            }
            if (!isAtualizando && oldV != newV && newV != null) {
                if (!notifyEdit(() -> getUnidadeAlt().setCodVol(newV.toString())))
                    runEdit(() -> CbbVolume.setValue(oldV));
            }
        });
        CbbOper.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && oldV != newV) {
                if (!notifyEdit(() -> getUnidadeAlt().setMultiDivid(newV.toString())))
                    runEdit(() -> CbbOper.setValue(oldV));
            }
        });
        SpnQtdOper.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != oldV && newV != null)
                if (!notifyEdit(() -> getUnidadeAlt().setQtdMultiDivid(newV)))
                    runEdit(() -> SpnQtdOper.getValueFactory().setValue(1));
        });
        TxtCodBarras.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.matches(oldV))
                if (!notifyEdit(() -> getUnidadeAlt().setCodBarras(newV)))
                    runEdit(() -> TxtCodBarras.setText(oldV));
        });
        SearchFieldTable keyUnidade = new SearchFieldTable(ImgUnidade, "Unidade", 2,
                new String[]{"Sigla Unidade", "Descr. Unidade"},
                "SELECT CODVOL, DESCRVOL FROM TGFVOL WHERE ROWNUM <= 50");
        keyUnidade.getMainStage().setOnCloseRequest(e -> {
            if (keyUnidade.getKeyReturn() != null) {
                CbbVolume.setValue(keyUnidade.getKeyReturn().get(0));
                TxtDescricao.setText(keyUnidade.getKeyReturn().get(1));
            }
        });
    }

    private void showInFormMode(UnidadeAlt unidadeAlt) {
        try {
            isAtualizando = true;
            if (unidadeAlt == null) {
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar exibir unidade alternativa em modo formulário\nFavor tente novamente"));
                ModelException.getException().raise();
                return;
            }
            setUnidadeAlt(unidadeAlt);
            setDescrVolCached(unidadeAlt.getCodVol());
            CbbVolume.setValue(unidadeAlt.getCodVol());
            TxtDescricao.setText(unidadeAlt.getDescrVol());
            CbbOper.setValue(unidadeAlt.getMultiDivid());
            SpnQtdOper.getValueFactory().setValue(unidadeAlt.getQtdMultiDivid());
            TxtCodBarras.setText(Functions.Nvl(unidadeAlt.getCodBarras()));
            LblDHCriacao.setText("Data/Hora Criação: " + unidadeAlt.getDHCriacao());
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar exibir unidade alternativa em modo formulário\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            isAtualizando = false;
        }
    }

    public void loadTableValues() {
        if (getCodProd() == -1) {
            isAtualizando = true;
            unidadeAltObsList.clear();
            TbUnidadesAlt.setItems(unidadeAltObsList);
            qtdLinhasTab = unidadeAltObsList.size();
            CbbVolume.setItems(ListaQuerys.getListUnidades());
            CtrlBtns(CtrlStatus.ReloadButton);
            CtrlBtns(CtrlStatus.Limpar);
            isAtualizando = false;
        } else {
            try {
                isAtualizando = true;
                unidadeAltObsList.clear();
                conex = new DBParalelConex("SELECT VOL.CODVOL, VOL.DESCRVOL, VOA.DHCRIACAO, VOA.CODBARRAS, VOA.MULTIDIVID, VOA.QTDMULTIDIVID,\n" +
                        "VOA.DHALTER, VOA.CODUSUALTER, USU.LOGIN, VOA.DHCRIACAO\n" +
                        "FROM TGFVOA VOA\n" +
                        "INNER JOIN TGFVOL VOL\n" +
                        "ON (VOA.CODVOL = VOL.CODVOL)\n" +
                        "INNER JOIN TSIUSU USU\n" +
                        "ON (VOA.CODUSUALTER = USU.CODUSU)\n" +
                        "WHERE VOA.CODPROD = ?");
                conex.addParameter(getCodProd());
                conex.createSet();
                while (conex.rs.next()) {
                    unidadeAltObsList.add(new UnidadeAlt(getCodProd(),
                            conex.rs.getString("CODVOL"),
                            conex.rs.getString("DESCRVOL"),
                            conex.rs.getTimestamp("DHCRIACAO"),
                            conex.rs.getString("CODBARRAS"),
                            conex.rs.getString("MULTIDIVID").charAt(0),
                            conex.rs.getInt("QTDMULTIDIVID"),
                            conex.rs.getString("CODUSUALTER") + " - " + conex.rs.getString("LOGIN"),
                            conex.rs.getTimestamp("DHALTER")));
                }
                TbUnidadesAlt.setItems(unidadeAltObsList);
                qtdLinhasTab = unidadeAltObsList.size();
                CbbVolume.setItems(ListaQuerys.getListUnidades());
                isAtualizando = false;
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar buscar unidades alternativas do produto\n" + ex, ex));
                ModelException.getException().raise();
                unidadeAltObsList.clear();
            } finally {
                conex.desconecta();
            }
        }
    }

    private void Adiciona(UnidadeAlt unidadeAlt) {
        try {
            conex = new DBParalelConex("INSERT INTO TGFVOA\n" +
                    "(CODPROD, CODVOL, DHCRIACAO, MULTIDIVID, QTDMULTIDIVID, CODBARRAS, CODUSUALTER, DHALTER)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?, ?, ?, ?)");
            conex.addParameter(getCodProd());
            conex.addParameter(unidadeAlt.getCodVol());
            conex.addParameter(Timestamp.from(Instant.now()));
            if (unidadeAlt.getMultiDivid().equals("Multiplica")) conex.addParameter("M");
            else if (unidadeAlt.getMultiDivid().equals("Divide")) conex.addParameter("D");
            else System.err.println("Error em Adiciona: " + unidadeAlt.getMultiDivid());
            conex.addParameter(unidadeAlt.getQtdMultiDivid());
            conex.addParameter(unidadeAlt.getCodBarras());
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Unidade Alternativa incluída com sucesso!");
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar unidade alternativa\n" + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar unidade alternativa\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Altera(UnidadeAlt unidadeAlt) {
        try {
            conex = new DBParalelConex("UPDATE TGFVOA\n" +
                    "SET CODVOL = ?,\n" +
                    "MULTIDIVID = ?,\n" +
                    "QTDMULTIDIVID = ?,\n" +
                    "CODBARRAS = ?,\n" +
                    "CODUSUALTER = ?,\n" +
                    "DHALTER = ?\n" +
                    "WHERE CODPROD = ?\n" +
                    "AND CODVOL = ?");
            conex.addParameter(unidadeAlt.getCodVol());
            if (unidadeAlt.getMultiDivid().equals("Multiplica")) conex.addParameter("M");
            else if (unidadeAlt.getMultiDivid().equals("Divide")) conex.addParameter("D");
            else System.err.println("Error em Adiciona: " + unidadeAlt.getMultiDivid());
            conex.addParameter(unidadeAlt.getQtdMultiDivid());
            conex.addParameter(unidadeAlt.getCodBarras());
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(getCodProd());
            conex.addParameter(getDescrVolCached());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Unidade Alternativa alterada com sucesso!");
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar alterar unidade alternativa\n" + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar alterar unidade alternativa\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Remove(ArrayList<String> unidadesExc) {
        if (unidadesExc.isEmpty()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Sem linhas selecionadas, não é possível efetuar a exclusão."));
            ModelDialog.getDialog().raise();
            return;
        }
        ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                "Deseja realmente excluir o(s) volumes: " + unidadesExc.toString()));
        ButtonType[] Btns = new ButtonType[2];
        Btns[0] = new ButtonType("Confirmar");
        Btns[1] = new ButtonType("Cancelar");
        ModelDialogButton.getDialogButton().createButton(Btns);
        if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Cancelar
            ModelDialog.setNewDialog(
                    new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null, "Operação cancelada pelo usuário"));
            ModelDialog.getDialog().raise();
        } else {
            try {
                conex = new DBParalelConex("DELETE TGFVOA WHERE CODPROD = ? AND CODVOL IN " + Functions.NewArrayParameter(unidadesExc) + "");
                conex.addParameter(getCodProd());
                conex.addParameter(unidadesExc);
                conex.run();
                BtnAtualizar.fire();
                setMessage("Unidade(s) excluído(s) com sucesso!");
            } catch (SQLException ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar excluir unidade(s)\n" + ex, ex));
                ModelException.getException().raise();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar excluir unidade(s)\n" + ex, ex));
                ModelException.getException().raise();
            } finally {
                conex.desconecta();
            }
        }
    }

    public void CtrlBtns(CtrlStatus Action) {
        switch (Action) {
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
                isAtualizando = true;
                setUnidadeAlt(new UnidadeAlt());
                showInFormMode(getUnidadeAlt());
                isAtualizando = false;
                break;
            case FormGrade:
                break;
            case Atualizar:
                String cachedUnd = Functions.Nvl(CbbVolume.getValue().toString());
                setCodProd(getCodProd()); //Ele já cria a tabela.
                if (!cachedUnd.equals("")) {
                    for (UnidadeAlt und : TbUnidadesAlt.getItems()) {
                        if (und.getCodVol().equals(cachedUnd)) {
                            TbUnidadesAlt.getSelectionModel().clearSelection();
                            TbUnidadesAlt.getSelectionModel().select(und);
                            cachedUnd = "ZZZ";
                            break;
                        }
                    }
                    if (cachedUnd.length() < 3) { //Produto não está mais na lista
                        CtrlBtns(CtrlStatus.Primeiro);
                    }
                }
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Adicionar:
                if (getCodProd() <= 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Para cadastrar uma unidade alternativa, primeiro registre o produto."));
                    ModelDialog.getDialog().raise();
                    return;
                }
                classStatus.setStatus(ControllerStatus.Adicionando);
                setUnidadeAlt(new UnidadeAlt());
                showInFormMode(getUnidadeAlt());
                BtnAtualizar.setDisable(true);
                BtnAdicionar.setDisable(true);
                BtnSalvar.setDisable(false);
                BtnCancelar.setDisable(false);
                BtnEditar.setDisable(true);
                BtnDuplicar.setDisable(true);
                BtnExcluir.setDisable(true);
                if (!PanelForm.isVisible()) PanelForm.setVisible(true);
                break;
            case Salvar:
                if (getCodProd() <= 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Para registrar uma unidade alternativa, primeiro registre o produto."));
                    ModelDialog.getDialog().raise();
                    return;
                }
                if (validChanges()) {//Valida se tem algum erro
                    switch (classStatus.getStatus()) {
                        case Editando:
                            Altera(getUnidadeAlt());
                            break;
                        case Adicionando:
                            Adiciona(getUnidadeAlt());
                            break;
                        case Nenhum:
                        default:
                            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro no método VolumesAltController.CtrlBtns(" + Action + ")\n" +
                                    "Retorno diferente do esperado, contate o administrado!"));
                            ModelException.getException().raise();
                            break;
                    }
                }
                break;
            case Cancelar:
                if (getCodProd() <= 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Para cancelar o registro de uma unidade alternativa, primeiro registre o produto."));
                    ModelDialog.getDialog().raise();
                    return;
                }
                String cachedUnd2 = Functions.Nvl(CbbVolume.getValue().toString());
                setCodProd(getCodProd()); //Ele já cria a tabela.
                if (!cachedUnd2.equals("")) {
                    for (UnidadeAlt und : TbUnidadesAlt.getItems()) {
                        if (und.getCodVol().equals(cachedUnd2)) {
                            TbUnidadesAlt.getSelectionModel().clearSelection();
                            TbUnidadesAlt.getSelectionModel().select(und);
                            cachedUnd = "ZZZ";
                            break;
                        }
                    }
                    if (cachedUnd2.length() < 3) { //Unidade não está mais na lista
                        CtrlBtns(CtrlStatus.Primeiro);
                    }
                }
                classStatus.setStatus(ControllerStatus.Nenhum);
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Editar:
                if (getCodProd() <= 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Para editar uma unidade alternativa, primeiro registre o produto."));
                    ModelDialog.getDialog().raise();
                    return;
                }
                classStatus.setStatus(ControllerStatus.Editando);
                BtnAtualizar.setDisable(true);
                BtnAdicionar.setDisable(true);
                BtnSalvar.setDisable(false);
                BtnCancelar.setDisable(false);
                BtnEditar.setDisable(true);
                BtnDuplicar.setDisable(true);
                BtnExcluir.setDisable(true);
                BtnPrimeiro.setDisable(true);
                BtnAnterior.setDisable(true);
                BtnAnterior.setDisable(true);
                BtnProximo.setDisable(true);
                BtnUltimo.setDisable(true);
                break;
            case Duplicar:
                if (getCodProd() <= 0) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Para duplicar uma unidade alternativa, primeiro cadastre o produto."));
                    ModelDialog.getDialog().raise();
                    return;
                }
                classStatus.setStatus(ControllerStatus.Adicionando);
                BtnAtualizar.setDisable(true);
                BtnAdicionar.setDisable(true);
                BtnSalvar.setDisable(false);
                BtnCancelar.setDisable(false);
                BtnEditar.setDisable(true);
                BtnDuplicar.setDisable(true);
                BtnExcluir.setDisable(true);
                BtnPrimeiro.setDisable(true);
                BtnAnterior.setDisable(true);
                BtnAnterior.setDisable(true);
                BtnProximo.setDisable(true);
                BtnUltimo.setDisable(true);
                break;
            case Excluir:
                ArrayList<String> unidadesToRemove = new ArrayList<>();
                if (PanelForm.isVisible()) {
                    String unidadeExc = Functions.Nvl(CbbVolume.getValue().toString());
                    if (!unidadeExc.matches("")) {
                        unidadesToRemove.add(unidadeExc);
                    } else {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Sem unidade carregada, não é possível excluir\nFavor verifique"));
                        ModelException.getException().raise();
                        return;
                    }
                } else {
                    TbUnidadesAlt.getSelectionModel().getSelectedItems().forEach(unidadeExc -> unidadesToRemove.add(unidadeExc.getCodVol()));
                }
                Remove(unidadesToRemove);
                break;
            case Primeiro:
                if (qtdLinhasTab == 0) {
                    CtrlBtns(CtrlStatus.Limpar);
                }
                TbUnidadesAlt.getSelectionModel().clearSelection();
                TbUnidadesAlt.getSelectionModel().select(0);
                break;
            case Anterior:
                TbUnidadesAlt.getSelectionModel().clearSelection();
                TbUnidadesAlt.getSelectionModel().select(lastLoadedCodigo - 1);
                break;
            case Proximo:
                TbUnidadesAlt.getSelectionModel().clearSelection();
                TbUnidadesAlt.getSelectionModel().select(lastLoadedCodigo + 1);
                break;
            case Ultimo:
                TbUnidadesAlt.getSelectionModel().clearSelection();
                TbUnidadesAlt.getSelectionModel().select(qtdLinhasTab - 1);
                break;
            default:
                break;
        }
    }

    public void ctrlLinhasTab(int pos) {
        BtnPrimeiro.setDisable(true);
        BtnAnterior.setDisable(true);
        BtnProximo.setDisable(true);
        BtnUltimo.setDisable(true);
        if (qtdLinhasTab > 0) {
            if (pos == 0) {
                BtnProximo.setDisable(false);
                BtnUltimo.setDisable(false);
            } else if (pos + 1 < qtdLinhasTab) {
                BtnPrimeiro.setDisable(false);
                BtnAnterior.setDisable(false);
                BtnProximo.setDisable(false);
                BtnUltimo.setDisable(false);
            } else if (pos + 1 == qtdLinhasTab) {
                BtnPrimeiro.setDisable(false);
                BtnAnterior.setDisable(false);
            }
        }
        lastLoadedCodigo = pos;
    }

    public TableColumn[] getTableColumns() {
        TableColumn[] tbColunas = new TableColumn[8];
        tbColunas[0] = new ModelTableColumn<UnidadeAlt, Integer>("#", "CodVol");
        tbColunas[1] = new ModelTableColumn<UnidadeAlt, String>("Descr. Volume", "DescrVol");
        tbColunas[2] = new ModelTableColumn<UnidadeAlt, String>("Operação", "MultiDivid");
        tbColunas[3] = new ModelTableColumn<UnidadeAlt, String>("Qtd. Operação", "QtdMultiDivid");
        tbColunas[4] = new ModelTableColumn<UnidadeAlt, String>("Cód. Barras", "CodBarras");
        tbColunas[5] = new ModelTableColumn<UnidadeAlt, String>("Data/Hora Ult. Alter", "DHAlter");
        tbColunas[6] = new ModelTableColumn<UnidadeAlt, String>("Usu. Alter", "UsuAlter");
        tbColunas[7] = new ModelTableColumn<UnidadeAlt, String>("Data/Hora Criação", "DHCriacao");
        return tbColunas;
    }

    public Integer getCodProd() {
        return CodProd;
    }

    public void setCodProd(Integer codProd) {
        if (codProd == null || codProd == 0) codProd = -1;
        CodProd = codProd;
        loadTableValues();
    }

    public UnidadeAlt getUnidadeAlt() {
        return unidadeAlt;
    }

    public void setUnidadeAlt(UnidadeAlt unidadeAlt) {
        this.unidadeAlt = unidadeAlt;
    }

    public Boolean validChanges() {
        ArrayList<String> arrayUnid = new ArrayList<>();
        TbUnidadesAlt.getItems().forEach(Und -> arrayUnid.add(Und.getCodVol()));
        arrayUnid.remove(Functions.Nvl(CbbVolume.getValue().toString()));
        arrayUnid.add(getDescrVolCached());
        if (Functions.Nvl(CbbVolume.getValue().toString()).equals(getDescrVolPrincipal())) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Unidade já apontada como principal para este produto!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (arrayUnid.contains(Functions.Nvl(CbbVolume.getValue().toString())) &&
                classStatus.getStatus() == ControllerStatus.Adicionando) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Unidade de volume já cadastrada para este produto!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (arrayUnid.contains(Functions.Nvl(CbbVolume.getValue().toString())) &&
                classStatus.getStatus() == ControllerStatus.Editando &&
                !getDescrVolCached().equals(Functions.Nvl(CbbVolume.getValue().toString()))) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Unidade de volume já cadastrada para este produto!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (Functions.Nvl(CbbVolume.getValue().toString()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Volume não pode ser vazio!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (Functions.Nvl(TxtDescricao.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Descrição do volume não pode ser vazio!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (Functions.Nvl(CbbOper.getValue().toString()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Operação do volume alternativo não pode ser vazia!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (SpnQtdOper.getValueFactory().getValue() > 999 || SpnQtdOper.getValueFactory().getValue() < 1) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Multiplicador do volume tem de estar entre 1 e 999!"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            return true;
        }
    }

    public Boolean notifyEdit(Runnable changes) {
        if (!AcessoTela.getAltera() && classStatus.getStatus() != ControllerStatus.Adicionando) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Usuário sem acessos para efetuar alterações neste campo"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            if (!isAtualizando) BtnEditar.fire();
            changes.run();
            return true;
        }
    }

    public void runEdit(Runnable changes) {
        isAtualizando = true;
        changes.run();
        isAtualizando = false;
    }

    public void setMessage(String mensagem) {
        LblAcao.setText(mensagem);
        LblAcao.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1500),
                ae -> LblAcao.setVisible(false)));
        timeline.play();
    }

    public String getDescrVolPrincipal() {
        return descrVolPrincipal;
    }

    public void setDescrVolPrincipal(String descrVolPrincipal) {
        this.descrVolPrincipal = descrVolPrincipal;
    }

    public String getDescrVolCached() {
        return descrVolCached;
    }

    public void setDescrVolCached(String descrVolCached) {
        this.descrVolCached = descrVolCached;
    }
}
