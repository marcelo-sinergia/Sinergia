package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.extendeds.tableProperties.ModelTableColumn;
import br.com.sinergia.functions.modules.CtrlAcesso;
import br.com.sinergia.functions.natives.ControllerStatus;
import br.com.sinergia.functions.natives.CtrlStatus;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.models.usage.CadInterface;
import br.com.sinergia.models.usage.Local;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.natives.Functions.*;

public class CadLocalController implements Initializable, CadInterface {

    DBParalelConex conex;
    CtrlAcesso AcessoTela;
    Boolean isAtualizando = true;
    private Local cachedLocal = new Local();
    int lastLoadedCodigo = -1, qtdLinhasTab = 0;
    ControllerStatus classStatus = ControllerStatus.Nenhum;
    private ObservableList<Local> ListLocais = FXCollections.observableArrayList();

    @FXML
    private AnchorPane PanelFundo, PanelGrade, PanelForm;
    @FXML
    private TableView<Local> TbLocais;
    @FXML
    private JFXButton BtnFormGrade, BtnAtualizar, BtnAdicionar, BtnSalvar, BtnCancelar, BtnEditar, BtnDuplicar, BtnExcluir,
            BtnPrimeiro, BtnAnterior, BtnProximo, BtnUltimo;
    @FXML
    private CheckBox CkbLocPorEmp, CkbPermEntra, CkbPermSai;
    @FXML
    private TextField TxtCodigo, TxtDescricao;
    @FXML
    private JFXDatePicker DtpDHLimtEntra;
    @FXML
    private Label LblAcao;

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
                AcessoTela = new CtrlAcesso("Locais", User.getCurrent().getCódUsu());
            } catch (Error ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage()));
                ModelException.getException().raise();
                Platform.runLater(() -> {
                    try {
                        AppObjects.getAppObjects().getTelasAbertas().remove("Locais"); //ToRemove
                        AppObjects.getAppObjects().getAbaPane().getTabs().remove(AppObjects.getAppObjects().getAbaPane().getSelectionModel().getSelectedIndex());
                    } catch (Exception ex1) {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar finalizar tela, contate o suporte\n" + ex1, ex1));
                        ModelException.getException().raise();
                        System.exit(0);
                    }
                });
            }
            PanelForm.visibleProperty().addListener((obs, oldV, newV) -> {
                if (newV) {
                    PanelGrade.setVisible(false);
                    ImageView ImgFormGrade = new ImageView("/br/com/sinergia/properties/images/Icone_Modo_Formulario.png");
                    ImgFormGrade.setFitWidth(35);
                    ImgFormGrade.setFitHeight(31);
                    BtnFormGrade.setGraphic(ImgFormGrade);
                    BtnFormGrade.setTooltip(new Tooltip("Voltar p/ Modo Grade"));
                } else {
                    PanelGrade.setVisible(true);
                    ImageView ImgFormGrade = new ImageView("/br/com/sinergia/properties/images/Icone_Modo_Grade.png");
                    ImgFormGrade.setFitWidth(35);
                    ImgFormGrade.setFitHeight(31);
                    BtnFormGrade.setGraphic(ImgFormGrade);
                    BtnFormGrade.setTooltip(new Tooltip("Voltar p/ Modo Formulário"));
                }
            });
            PanelForm.setVisible(false); //Mostrar em modo grade
            TbLocais.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            TbLocais.setItems(ListLocais);
            TbLocais.getColumns().addAll(getTableColumns());
            TbLocais.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && newV != oldV) {
                    showInFormMode(newV);
                    ctrlLinhasTab(TbLocais.getSelectionModel().getSelectedIndex());
                } else {
                    CtrlBtns(CtrlStatus.Limpar);
                }
            });
            TbLocais.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    PanelForm.setVisible(true);
                }
            });
            fieldEstructure();
            CtrlBtns(CtrlStatus.ReloadButton);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar aplicar Layout na tela\n" + ex + "\nFavor contate o suporte", ex));
            ModelException.getException().raise();
        }
    }

    @Override
    public void fieldEstructure() {
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
        final ContextMenu contextMenu = new ContextMenu();
        MenuItem dataRemover = new MenuItem("Limpar data");
        dataRemover.setOnAction(e -> {
            DtpDHLimtEntra.setValue(null);
            DtpDHLimtEntra.hide();
        });
        MenuItem dataAtual = new MenuItem("Data de hoje");
        dataAtual.setOnAction(e -> {
            DtpDHLimtEntra.setValue(LocalDate.now());
            DtpDHLimtEntra.hide();
        });
        contextMenu.getItems().addAll(dataRemover, dataAtual);
        DtpDHLimtEntra.setContextMenu(contextMenu);
        MaskField.NumberField(TxtCodigo, 22);
        TxtCodigo.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && classStatus.getStatus() == ControllerStatus.Adicionando && !newV.isEmpty()) {
                getCachedLocal().setCodLocal(getOnlyNumber(newV)); //Para não perder para o FocusLost
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Na criação de novos produtos, o código deste é obtido de forma automática pelo sistema.\n" +
                                "Esta trava, tem a finalidade de evitar registros duplicados."));
                ModelDialog.getDialog().raise();
                TxtCodigo.setText("");
            }
        });
        TxtCodigo.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF && !TxtCodigo.getText().equals("" + getCachedLocal().getCodLocal()) && classStatus.getStatus() == ControllerStatus.Nenhum) {
                if (Nvl(TxtCodigo.getText()).equals("")) {
                    CtrlBtns(CtrlStatus.Limpar);
                    return;
                }
                int cachedCodigo = getOnlyNumber(TxtCodigo.getText());
                for (Local local : TbLocais.getItems()) {
                    if (local.getCodLocal() == cachedCodigo) {
                        cachedCodigo = -3;
                        TbLocais.getSelectionModel().clearSelection();
                        TbLocais.getSelectionModel().select(local);
                        break;
                    }
                }
                if (cachedCodigo >= 0) { //Produto não está na lista
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Local não encontrado para o código: " + cachedCodigo));
                    ModelDialog.getDialog().raise();
                    CtrlBtns(CtrlStatus.Primeiro);
                }
            }
        });
        MaskField.CharField(TxtDescricao, 40);
        TxtDescricao.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (!notifyEdit(() -> getCachedLocal().setDescrLocal(newV)))
                    runEdit(() -> TxtDescricao.setText(oldV));
            }
        });
        CkbLocPorEmp.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV) {
                if (!notifyEdit(() -> getCachedLocal().setLocalPorEmp(StrFromBoo(newV).charAt(0))))
                    runEdit(() -> CkbLocPorEmp.setSelected(oldV));
            }
        });
        CkbPermEntra.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV) {
                if (!notifyEdit(() -> getCachedLocal().setPermEntra(StrFromBoo(newV).charAt(0))))
                    runEdit(() -> CkbPermEntra.setSelected(oldV));
            }
        });
        CkbPermSai.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV) {
                if (!notifyEdit(() -> getCachedLocal().setPermSai(StrFromBoo(newV).charAt(0))))
                    runEdit(() -> CkbPermSai.setSelected(oldV));
            }
        });
        DtpDHLimtEntra.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != oldV) {
                if (!notifyEdit(() -> getCachedLocal().setDHLimitEntra(newV)))
                    runEdit(() -> DtpDHLimtEntra.setValue(oldV));
            }
        });
    }

    @Override
    public void loadTableValues() {
        isAtualizando = true;
        ListLocais.clear();
        try {
            conex = new DBParalelConex("SELECT CODLOCAL, DESCRLOCAL, LOCALPOREMP, PERMENTRA, PERMSAI, DHLIMITENTRA\n" +
                    "FROM TGFLOC\n" +
                    "WHERE CODLOCAL <> 0\n" +
                    "ORDER BY CODLOCAL");
            conex.createSet();
            while (conex.rs.next()) {
                Local localAtual = new Local(
                        conex.rs.getInt(1),
                        conex.rs.getString(2),
                        conex.rs.getString(3),
                        conex.rs.getString(4),
                        conex.rs.getString(5),
                        getToLocalDate(conex.rs.getDate(6)));
                ListLocais.add(localAtual);
            }
            qtdLinhasTab = ListLocais.size();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar locais\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
            isAtualizando = false;
        }
    }

    private void showInFormMode(Local local) {
        isAtualizando = true;
        classStatus.setStatus(ControllerStatus.Nenhum);
        try {
            if (local == null) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar exibir local em modo formulário\n" +
                        "Favor tente novamente."));
                ModelException.getException().raise();
                CtrlBtns(CtrlStatus.Limpar);
                return;
            }
            setCachedLocal(local);
            TxtCodigo.setText(local.getCodLocal().toString());
            TxtDescricao.setText(local.getDescrLocal());
            CkbLocPorEmp.setSelected(ToBoo(local.getLocalPorEmp()));
            CkbPermEntra.setSelected(ToBoo(local.getPermEntra()));
            CkbPermSai.setSelected(ToBoo(local.getPermSai()));
            DtpDHLimtEntra.setValue(local.getDHLimitEntra());
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar exibir local\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            isAtualizando = false;
        }
    }

    private void Adiciona(Local local) {
        try {
            conex = new DBParalelConex("SELECT GET_COD('CODLOCAL', 'TGFLOC') AS COD FROM DUAL");
            conex.createSet();
            conex.rs.next();
            int codProd = conex.rs.getInt("COD");
            conex = new DBParalelConex("INSERT INTO TGFLOC\n" +
                    "(CODLOCAL, DESCRLOCAL, LOCALPOREMP, PERMENTRA, PERMSAI, DHLIMITENTRA)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?, ?)");
            conex.addParameter(codProd);
            conex.addParameter(local.getDescrLocal());
            conex.addParameter(local.getLocalPorEmp());
            conex.addParameter(local.getPermEntra());
            conex.addParameter(local.getPermSai());
            conex.addParameter(local.getDHLimitEntra());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            TxtCodigo.setText(codProd + "");
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Local incluído com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar local\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Altera(Local local) {
        if (local.getCodLocal() == 0) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Não é possível alterar o local padrão 0"));
            ModelDialog.getDialog().raise();
            return;
        }
        try {
            conex = new DBParalelConex("UPDATE TGFLOC\n" +
                    "SET DESCRLOCAL = ?,\n" +
                    "LOCALPOREMP = ?,\n" +
                    "PERMENTRA = ?,\n" +
                    "PERMSAI = ?,\n" +
                    "DHLIMITENTRA = ?\n" +
                    "WHERE CODLOCAL = ?");
            conex.addParameter(local.getDescrLocal());
            conex.addParameter(local.getLocalPorEmp());
            conex.addParameter(local.getPermEntra());
            conex.addParameter(local.getPermSai());
            conex.addParameter(local.getDHLimitEntra());
            conex.addParameter(local.getCodLocal());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Local alterado com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar alterar local\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Remove(ArrayList<Integer> locais) {
        if (locais.isEmpty()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Sem linhas selecionadas, não é possível efetuar a exclusão."));
            ModelDialog.getDialog().raise();
            return;
        }
        ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                "Deseja realmente excluir o(s) local(is) de código: " + locais.toString() + " ?"));
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
                conex = new DBParalelConex("DELETE TGFLOC WHERE CODLOCAL IN " + NewArrayParameter(locais) + "");
                conex.addParameter(locais);
                conex.run();
                BtnAtualizar.fire();
                setMessage("Local(is) excluído(s) com sucesso!");
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar excluir local(is)\n" + ex, ex));
                ModelException.getException().raise();
            } finally {
                conex.desconecta();
            }
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
                showInFormMode(new Local());
                runEdit(() -> TxtCodigo.setText(""));
                break;
            case Atualizar:
                classStatus.setStatus(ControllerStatus.Nenhum);
                int cachedCodigo = getOnlyNumber(TxtCodigo.getText());
                loadTableValues();
                if (cachedCodigo >= 0) {
                    for (Local prod : TbLocais.getItems()) {
                        if (prod.getCodLocal() == cachedCodigo) {
                            TbLocais.getSelectionModel().clearSelection();
                            TbLocais.getSelectionModel().select(prod);
                            cachedCodigo = -3;
                            break;
                        }
                    }
                    if (cachedCodigo >= 0) { //Produto não está mais na lista
                        CtrlBtns(CtrlStatus.Primeiro);
                    }
                }
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Adicionar:
                CtrlBtns(CtrlStatus.Limpar);
                classStatus.setStatus(ControllerStatus.Adicionando);
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
                if (validChanges()) {//Valida se tem algum erro
                    switch (classStatus.getStatus()) {
                        case Editando:
                            Altera(getCachedLocal());
                            break;
                        case Adicionando:
                            Adiciona(getCachedLocal());
                            break;
                        case Nenhum:
                        default:
                            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro no método CadLocalController.CtrlBtns(" + action + ")." + classStatus.getStatus() + "\n" +
                                    "Retorno diferente do esperado, contate o administrado!"));
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
                BtnPrimeiro.setDisable(true);
                BtnAnterior.setDisable(true);
                BtnAnterior.setDisable(true);
                BtnProximo.setDisable(true);
                BtnUltimo.setDisable(true);
                break;
            case Duplicar:
                classStatus.setStatus(ControllerStatus.Adicionando);
                runEdit(() -> TxtCodigo.setText(""));
                getCachedLocal().setCodLocal(0);
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
                if (!PanelForm.isVisible()) PanelForm.setVisible(true);
                break;
            case Excluir:
                ArrayList<Integer> codLocaisToRemove = new ArrayList<>();
                if (PanelForm.isVisible()) {
                    int codToRemove = getOnlyNumber(TxtCodigo.getText());
                    if (codToRemove >= 0) {
                        codLocaisToRemove.add(codToRemove);
                    } else {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Sem local carregado, não é possível excluir\nFavor verifique"));
                        ModelException.getException().raise();
                        return;
                    }
                } else {
                    TbLocais.getSelectionModel().getSelectedItems().forEach(codLocal -> codLocaisToRemove.add((Integer) codLocal.getCodLocal()));
                }
                Remove(codLocaisToRemove);
                break;
            case Primeiro:
                TbLocais.getSelectionModel().clearSelection();
                TbLocais.getSelectionModel().select(0);
                break;
            case Anterior:
                TbLocais.getSelectionModel().clearSelection();
                TbLocais.getSelectionModel().select(lastLoadedCodigo - 1);
                break;
            case Proximo:
                TbLocais.getSelectionModel().clearSelection();
                TbLocais.getSelectionModel().select(lastLoadedCodigo + 1);
                break;
            case Ultimo:
                TbLocais.getSelectionModel().clearSelection();
                TbLocais.getSelectionModel().select(qtdLinhasTab - 1);
                break;
            default:
                System.err.println("CadLocalController.CtrlBtns(" + action + ") not programmed!");
                break;
        }
    }

    @Override
    public Boolean validChanges() {
        if (Nvl(TxtDescricao.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Descrição do local não pode ser vazia!"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        TableColumn[] tbColunas = new TableColumn[6];
        tbColunas[0] = new ModelTableColumn<Local, Integer>("Código", "CodLocal");
        tbColunas[1] = new ModelTableColumn<Local, String>("Descrição", "DescrLocal");
        tbColunas[2] = new ModelTableColumn<Local, Integer>("Por Empresa", "strLocalPorEmp");
        tbColunas[3] = new ModelTableColumn<Local, Integer>("Perm. Entrada", "strPermEntra");
        tbColunas[4] = new ModelTableColumn<Local, Integer>("Perm. Saída", "strPermSai");
        tbColunas[5] = new ModelTableColumn<Local, String>("Data/Hora Limit. Entrada", "FmtdDHLimiteEntra");
        return tbColunas;
    }

    @Override
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
                        "Usuário sem acesso para alterações na tela de Cadastro de Locais"));
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

    public Local getCachedLocal() {
        return cachedLocal;
    }

    public void setCachedLocal(Local cachedLocal) {
        this.cachedLocal = cachedLocal;
    }

    private LocalDate getToLocalDate(Date date) {
        if (date == null) return null;
        else return date.toLocalDate();
    }
}
