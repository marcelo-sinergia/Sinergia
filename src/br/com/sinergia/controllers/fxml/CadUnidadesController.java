package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.database.dicionario.ListaQuerys;
import br.com.sinergia.functions.extendeds.tableProperties.ModelTableColumn;
import br.com.sinergia.functions.modules.CtrlAcesso;
import br.com.sinergia.functions.natives.ControllerStatus;
import br.com.sinergia.functions.natives.CtrlStatus;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.models.usage.CadInterface;
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

import static br.com.sinergia.functions.natives.Functions.*;

public class CadUnidadesController implements Initializable, CadInterface {

    private DBParalelConex conex;
    private CtrlAcesso AcessoTela;
    private Boolean isAtualizando = true;
    private Integer qtdLinhasTab = 0, lastLoadedCodigo = 0;
    private Unidade UnidadeCached = new Unidade();
    private ControllerStatus classStatus = ControllerStatus.Nenhum;
    private ObservableList<Unidade> ListUnidades = FXCollections.observableArrayList();

    @FXML
    private AnchorPane PanelFundo, PanelGrade, PanelForm;
    @FXML
    private TableView<Unidade> TbUnidade;
    @FXML
    private JFXButton BtnFormGrade, BtnAtualizar, BtnAdicionar, BtnSalvar, BtnCancelar, BtnEditar, BtnDuplicar, BtnExcluir,
            BtnPrimeiro, BtnAnterior, BtnProximo, BtnUltimo;
    @FXML
    private TextField TxtUnidade, TxtDescrUnidade;
    @FXML
    private Label LblAcao, LblDataHoraCriacao, LblDataHoraAlter, LblUsuAlter;

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
                AcessoTela = new CtrlAcesso("Unidades", User.getCurrent().getCódUsu());
            } catch (Error ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, ex.getMessage()));
                ModelException.getException().raise();
                Platform.runLater(() -> {
                    try {
                        AppObjects.getAppObjects().getTelasAbertas().remove("Unidades"); //ToRemove
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
            PanelForm.setVisible(false);
            TbUnidade.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            TbUnidade.setItems(ListUnidades);
            TbUnidade.getColumns().addAll(getTableColumns());
            fieldEstructure();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar aplicar Layout na tela de Unidades\n" + ex + "\n" +
                            "Favor contate o suporte", ex));
            ModelException.getException().raise();
        }
    }

    @Override
    public void fieldEstructure() {
        TbUnidade.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                showInFormMode(newV);
                ctrlLinhasTab(TbUnidade.getSelectionModel().getSelectedIndex());
            } else {
                CtrlBtns(CtrlStatus.Limpar);
            }
        });
        TbUnidade.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                PanelForm.setVisible(true);
            }
        });
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
        MaskField.CharField(TxtUnidade, 2);
        TxtUnidade.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null) {
                if (classStatus.getStatus() != ControllerStatus.Adicionando) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Não é possível alterar a sigla de uma unidade após cadastrada\n" +
                                    "Caso seja necessária, exclua esta e cadastre uma nova"));
                    ModelDialog.getDialog().raise();
                    runEdit(() -> TxtUnidade.setText(oldV));
                } else if (!notifyEdit(() -> getUnidadeCached().setCodVol(newV)))
                    runEdit(() -> TxtUnidade.setText(oldV));
            }
        });
        MaskField.CharField(TxtDescrUnidade, 25);
        TxtDescrUnidade.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null) {
                if (!notifyEdit(() -> getUnidadeCached().setDescrVol(newV)))
                    runEdit(() -> TxtDescrUnidade.setText(oldV));
            }
        });
    }

    @Override
    public void loadTableValues() {
        isAtualizando = true;
        ListUnidades.clear();
        try {
            conex = new DBParalelConex("SELECT VOL.CODVOL, VOL.DESCRVOL, VOL.DHCRIACAO, VOL.DHALTER, VOL.CODUSU, USU.LOGIN\n" +
                    "FROM TGFVOL VOL\n" +
                    "LEFT JOIN TSIUSU USU\n" +
                    "ON (VOL.CODUSU = USU.CODUSU)");
            conex.createSet();
            while (conex.rs.next()) {
                ListUnidades.add(new Unidade(
                        conex.rs.getString(1),
                        conex.rs.getString(2),
                        conex.rs.getTimestamp(3),
                        conex.rs.getTimestamp(4),
                        conex.rs.getInt(5),
                        Nvl(conex.rs.getString(6))
                ));
            }
            qtdLinhasTab = ListUnidades.size();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar tabela de Unidades\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            isAtualizando = false;
            conex.desconecta();
        }
    }

    private void showInFormMode(Unidade unidade) {
        isAtualizando = true;
        setUnidadeCached(unidade);
        TxtUnidade.setText(unidade.getCodVol());
        TxtDescrUnidade.setText(unidade.getDescrVol());
        LblDataHoraCriacao.setText(unidade.getDHCriacao());
        LblDataHoraAlter.setText(unidade.getDHAlter());
        LblUsuAlter.setText(unidade.getUsuAlter());
        isAtualizando = false;
    }

    private void Adiciona(Unidade unidade) {
        try {
            conex = new DBParalelConex("INSERT INTO TGFVOL\n" +
                    "(CODVOL, DESCRVOL, DHCRIACAO, DHALTER, CODUSU)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?)");
            conex.addParameter(unidade.getCodVol());
            conex.addParameter(unidade.getDescrVol());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Unidade incluída com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar unidade\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Altera(Unidade unidade) {
        try {
            conex = new DBParalelConex("UPDATE TGFVOL\n" +
                    "SET DESCRVOL = ?,\n" +
                    "DHALTER = ?,\n" +
                    "CODUSU = ?\n" +
                    "WHERE CODVOL = ?");
            conex.addParameter(unidade.getDescrVol());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(unidade.getCodVol());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Unidade alterada com sucesso!");
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar alterar unidade\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Remove(ArrayList<String> unidades) {
        if (unidades.isEmpty()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Sem linhas selecionadas, não é possível efetuar a exclusão."));
            ModelDialog.getDialog().raise();
            return;
        }
        if(unidades.contains("UN")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Não é permitido excluir a unidade UN."));
            ModelDialog.getDialog().raise();
            return;
        }
        ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                "Deseja realmente excluir a(s) unidade(s): " + unidades.toString()));
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
                conex = new DBParalelConex("DELETE TGFVOL WHERE CODVOL IN " + NewArrayParameter(unidades) + "");
                conex.addParameter(unidades);
                conex.run();
                BtnAtualizar.fire();
                setMessage("Unidade(s) excluída(s) com sucesso!");
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
                showInFormMode(new Unidade());
                break;
            case FormGrade:
                break;
            case Atualizar:
                String cachedUnd = Nvl(TxtUnidade.getText());
                loadTableValues();
                if (!cachedUnd.equals("")) {
                    for (Unidade und : TbUnidade.getItems()) {
                        if (und.getCodVol().equals(cachedUnd)) {
                            TbUnidade.getSelectionModel().clearSelection();
                            TbUnidade.getSelectionModel().select(und);
                            cachedUnd = "";
                            break;
                        }
                    }
                    if (!cachedUnd.equals("")) { //Produto não está mais na lista
                        CtrlBtns(CtrlStatus.Primeiro);
                    }
                } else {
                    CtrlBtns(CtrlStatus.Primeiro);
                }
                CtrlBtns(CtrlStatus.ReloadButton);
                break;
            case Adicionar:
                classStatus.setStatus(ControllerStatus.Adicionando);
                CtrlBtns(CtrlStatus.Limpar);
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
                            Altera(getUnidadeCached());
                            break;
                        case Adicionando:
                            Adiciona(getUnidadeCached());
                            break;
                        case Nenhum:
                        default:
                            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro no método CadProdutosController.CtrlBtns(" + action + ")\n" +
                                    "Retorno diferente do esperado, contate o administrador!"));
                            ModelException.getException().raise();
                            break;
                    }
                }
                break;
            case Cancelar:
                String cachedUnd2 = Nvl(TxtUnidade.getText());
                loadTableValues();
                if (!cachedUnd2.equals("")) {
                    for (Unidade und : TbUnidade.getItems()) {
                        if (und.getCodVol().equals(cachedUnd2)) {
                            TbUnidade.getSelectionModel().clearSelection();
                            TbUnidade.getSelectionModel().select(und);
                            cachedUnd2 = "";
                            break;
                        }
                    }
                    if (!cachedUnd2.equals("")) { //Produto não está mais na lista
                        CtrlBtns(CtrlStatus.Primeiro);
                    }
                } else {
                    CtrlBtns(CtrlStatus.Primeiro);
                }
                CtrlBtns(CtrlStatus.ReloadButton);
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
                runEdit(() -> {
                    TxtUnidade.clear();
                    getUnidadeCached().setCodVol("");
                });
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
                ArrayList<String> undToRemove = new ArrayList<>();
                if (PanelForm.isVisible()) {
                    if (!Nvl(TxtUnidade.getText()).equals("")) {
                        undToRemove.add(TxtUnidade.getText());
                    } else {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Sem unidade carregada, não é possível excluir\nFavor verifique"));
                        ModelException.getException().raise();
                        return;
                    }
                } else {
                    TbUnidade.getSelectionModel().getSelectedItems().forEach(undToExc -> undToRemove.add(undToExc.getCodVol()));
                }
                Remove(undToRemove);
                break;
            case Primeiro:
                TbUnidade.getSelectionModel().clearSelection();
                TbUnidade.getSelectionModel().select(0);
                break;
            case Anterior:
                TbUnidade.getSelectionModel().clearSelection();
                TbUnidade.getSelectionModel().select(lastLoadedCodigo - 1);
                break;
            case Proximo:
                TbUnidade.getSelectionModel().clearSelection();
                TbUnidade.getSelectionModel().select(lastLoadedCodigo + 1);
                break;
            case Ultimo:
                TbUnidade.getSelectionModel().clearSelection();
                TbUnidade.getSelectionModel().select(qtdLinhasTab - 1);
                break;
            default:
                System.err.println("CadUnidadesController.CtrlBtns(" + action + ") not programmed!");
                break;
        }
    }

    @Override
    public Boolean validChanges() {
        if (ListaQuerys.getListUnidades().contains(TxtUnidade.getText())
                && classStatus.getStatus() == ControllerStatus.Adicionando) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Unidade com sigla " + TxtUnidade.getText() + " já cadastrada!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (Nvl(TxtDescrUnidade.getText()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Descrição da Unidade não pode ser vazia!"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public TableColumn[] getTableColumns() {
        TableColumn[] tbColunas = new TableColumn[5];
        tbColunas[0] = new ModelTableColumn<Unidade, String>("Sigla", "CodVol");
        tbColunas[1] = new ModelTableColumn<Unidade, String>("Descrição", "DescrVol");
        tbColunas[2] = new ModelTableColumn<Unidade, String>("Data/Hora Criação", "DHCriacao");
        tbColunas[3] = new ModelTableColumn<Unidade, String>("Data/Hora Alteração", "DHAlter");
        tbColunas[4] = new ModelTableColumn<Unidade, String>("Usu. Alteração", "UsuAlter");
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
                        "Usuário sem acesso para alterações na tela de Unidades"));
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

    public Unidade getUnidadeCached() {
        return UnidadeCached;
    }

    public void setUnidadeCached(Unidade unidadeCached) {
        UnidadeCached = unidadeCached;
    }

    public class Unidade {
        private String CodVol;
        private String DescrVol;
        private String DHCriacao;
        private String DHAlter;
        private Integer CodUsuAlter;
        private String LoginAlter;
        private String UsuAlter;

        public Unidade() {
            setCodVol("");
            setDescrVol("");
            setDHCriacao("");
            setDHAlter("");
            setCodUsuAlter(0);
            setLoginAlter("");
            setUsuAlter("");
        }

        public Unidade(String codvol, String descrVol, Timestamp dhCriacao, Timestamp dhAlter, Integer codUsuAlter, String loginAlter) {
            setCodVol(codvol);
            setDescrVol(descrVol);
            setDHCriacao(formatDate(DataHoraFormater, dhCriacao));
            setDHAlter(formatDate(DataHoraFormater, dhAlter));
            setCodUsuAlter(codUsuAlter);
            setLoginAlter(loginAlter);
            if (!loginAlter.equals("")) setUsuAlter(codUsuAlter + " - " + loginAlter);
            else setUsuAlter("");
        }

        public String getCodVol() {
            return CodVol;
        }

        public void setCodVol(String codVol) {
            CodVol = codVol;
        }

        public String getDescrVol() {
            return DescrVol;
        }

        public void setDescrVol(String descrVol) {
            DescrVol = descrVol;
        }

        public String getDHCriacao() {
            return DHCriacao;
        }

        public void setDHCriacao(String DHCriacao) {
            this.DHCriacao = DHCriacao;
        }

        public String getDHAlter() {
            return DHAlter;
        }

        public void setDHAlter(String DHAlter) {
            this.DHAlter = DHAlter;
        }

        public String getLoginAlter() {
            return LoginAlter;
        }

        public void setLoginAlter(String loginAlter) {
            LoginAlter = loginAlter;
        }

        public Integer getCodUsuAlter() {
            return CodUsuAlter;
        }

        public void setCodUsuAlter(Integer codUsuAlter) {
            CodUsuAlter = codUsuAlter;
        }

        public String getUsuAlter() {
            return UsuAlter;
        }

        public void setUsuAlter(String usuAlter) {
            UsuAlter = usuAlter;
        }
    }
}
