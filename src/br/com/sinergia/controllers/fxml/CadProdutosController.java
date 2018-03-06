package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.database.dicionario.DDOpcoes;
import br.com.sinergia.database.dicionario.ListaQuerys;
import br.com.sinergia.functions.extendeds.SearchFieldTable;
import br.com.sinergia.functions.extendeds.SearchFieldTree;
import br.com.sinergia.functions.extendeds.tableProperties.ModelTableColumn;
import br.com.sinergia.functions.modules.CtrlAcesso;
import br.com.sinergia.functions.natives.ControllerStatus;
import br.com.sinergia.functions.natives.CtrlStatus;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.models.usage.CadInterface;
import br.com.sinergia.models.usage.Produto;
import com.jfoenix.controls.JFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static br.com.sinergia.functions.natives.Functions.*;


public class CadProdutosController implements Initializable, CadInterface {

    DBParalelConex conex;
    CtrlAcesso AcessoTela;
    Boolean isAtualizando = true;
    Produto cachedProd = new Produto();
    int lastLoadedCodigo = -1, qtdLinhasTab = 0;
    ControllerStatus classStatus = ControllerStatus.Nenhum;
    VolumesAltController UndAltController = null;
    ControleProdController CtrlController = null;
    private ObservableList<Produto> ListProdutos = FXCollections.observableArrayList();

    @FXML
    private AnchorPane PanelFundo, PanelForm, PanelGrade;
    @FXML
    private TableView<Produto> TabelaProdutos;
    @FXML
    private TitledPane TtpUnidades, TtpControles;
    @FXML
    private JFXButton BtnFormGrade, BtnAtualizar, BtnAdicionar, BtnSalvar, BtnCancelar, BtnEditar, BtnDuplicar, BtnExcluir,
            BtnPrimeiro, BtnAnterior, BtnProximo, BtnUltimo;
    @FXML
    private Button BtnNovaImagem, BtnLimparImagem;
    @FXML
    private TextField TxtComplemento, TxtCodigo, TxtDescricao, TxtReferencia, TxtCodGrupo, TxtDescrGrupo, TxtDescrUnidade,
            TxtNCM, TxtCodBarras, TxtCodLocal, TxtDescrLocal;
    @FXML
    private Label LblAcao, LblDataHoraCriacao, LblDataHoraAlter, LblUsuAlter, LblImagem;
    @FXML
    private CheckBox CkbAtivo, CkbPromocao, CkbPermCompra, CkbPermVenda, CkbPermConsumo;
    @FXML
    private ComboBox CbbMarca, CbbUnidade, CbbUsoProd;
    @FXML
    private TextArea TxtCaracteristicas;
    @FXML
    private Spinner<Integer> SpnDecCusto, SpnDecQtd, SpnDecVlr, SpnVendaMin, SpnVendaMax, SpnEstMin, SpnEstMax;
    @FXML
    private Spinner<Double> SpnPercComGer, SpnPercComVen, SpnDescMax;
    @FXML
    private ImageView ImgGrupoProd, ImgUnidade, ImgCodLocal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadClass();
        loadTableValues();
    }

    public void loadClass() {
        try {
            setLayoutTab(PanelFundo);
            try {
                AcessoTela = new CtrlAcesso("Produtos", User.getCurrent().getCódUsu());
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
            TabelaProdutos.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            TabelaProdutos.setItems(ListProdutos);
            TabelaProdutos.getColumns().addAll(getTableColumns());
            TabelaProdutos.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                if (newV != null && newV != oldV) {
                    showInFormMode(newV);
                    ctrlLinhasTab(TabelaProdutos.getSelectionModel().getSelectedIndex());
                } else {
                    CtrlBtns(CtrlStatus.Limpar);
                }
            });
            TabelaProdutos.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    PanelForm.setVisible(true);
                }
            });
            TtpUnidades.expandedProperty().addListener((obs, oldV, newV) -> {
                if (newV) loadUnidadesAltProd();
            });
            TtpControles.expandedProperty().addListener((obs, oldV, newV) -> {
                if (newV) loadControlesProd();
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
            BtnLimparImagem.setOnAction(e -> setImagemProd(null));
            BtnNovaImagem.setOnAction(e -> changeImageProd());
            CbbMarca.setItems(ListaQuerys.getListMarcas());
            CbbUnidade.setItems(ListaQuerys.getListUnidades());
            CbbUsoProd.setItems(FXCollections.observableArrayList(new ArrayList(DDOpcoes.getListaUsoProd().values())));
            fieldEstructure();
            CtrlBtns(CtrlStatus.ReloadButton);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar aplicar Layout na tela\n" + ex + "\nFavor contate o suporte", ex));
            ModelException.getException().raise();
        }
    }

    public void fieldEstructure() {
        MaskField.NumberField(TxtCodigo, 22);
        TxtCodigo.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && classStatus.getStatus() == ControllerStatus.Adicionando && !newV.isEmpty()) {
                getProdutoCached().setCodProd(getOnlyNumber(newV)); //Para não perder para o FocusLost
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Na criação de novos produtos, o código deste é obtido de forma automática pelo sistema.\n" +
                                "Esta trava, tem a finalidade de evitar registros duplicados."));
                ModelDialog.getDialog().raise();
                TxtCodigo.setText("");
            }
        });
        TxtCodigo.focusedProperty().addListener((obs, wasF, isF) -> {
            if (wasF && !TxtCodigo.getText().equals("" + getProdutoCached().getCodProd()) && classStatus.getStatus() == ControllerStatus.Nenhum) {
                if (Nvl(TxtCodigo.getText()).equals("")) {
                    CtrlBtns(CtrlStatus.Limpar);
                    return;
                }
                int cachedCodigo = getOnlyNumber(TxtCodigo.getText());
                for (Produto prod : TabelaProdutos.getItems()) {
                    if (prod.getCodProd() == cachedCodigo) {
                        cachedCodigo = -3;
                        TabelaProdutos.getSelectionModel().clearSelection();
                        TabelaProdutos.getSelectionModel().select(prod);
                        break;
                    }
                }
                if (cachedCodigo >= 0) { //Produto não está na lista
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Produto não encontrado para o código: " + cachedCodigo));
                    ModelDialog.getDialog().raise();
                    CtrlBtns(CtrlStatus.Limpar);
                }
            }
        });
        CkbAtivo.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null)
                if (!notifyEdit(() -> getProdutoCached().setAtivo(StrFromBoo(newV))))
                    runEdit(() -> CkbAtivo.setSelected(oldV));
        });
        MaskField.CharField(TxtDescricao, 40);
        TxtDescricao.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (!notifyEdit(() -> getProdutoCached().setDescrProd(newV)))
                    runEdit(() -> TxtDescricao.setText(oldV));
            }
        });
        MaskField.CharField(TxtComplemento, 80);
        TxtComplemento.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setComplemento(newV)))
                    runEdit(() -> TxtComplemento.setText(oldV));
        });
        MaskField.CharField(TxtReferencia, 20);
        TxtReferencia.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setReferencia(newV)))
                    runEdit(() -> TxtReferencia.setText(oldV));
        });
        MaskField.CharField(CbbMarca, 20);
        CbbMarca.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setMarca(newV)))
                    runEdit(() -> CbbMarca.setValue(oldV));
        });
        CbbMarca.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setMarca(newV.toString())))
                    runEdit(() -> CbbMarca.setValue(oldV));
        });
        CbbUnidade.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && oldV != newV) TxtDescrUnidade.setText(ListaQuerys.getDescrVol(newV.toString()));
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setUnidade(newV.toString())))
                    runEdit(() -> CbbUnidade.setValue(oldV));
        });
        MaskField.NumberField(TxtCodGrupo, 22);
        TxtCodGrupo.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setCodGrupoProd(getOnlyNumber(newV))))
                    runEdit(() -> TxtCodGrupo.setText(oldV));
        });
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
                    TxtCodGrupo.setText(keyGrupoProd.getKeyReturn().get(0));
                    TxtDescrGrupo.setText(keyGrupoProd.getKeyReturn().get(1));
                }
                keyGrupoProd.setKeyReturn(null);
            }
        });
        SearchFieldTable keyUnidade = new SearchFieldTable(ImgUnidade, "Unidade", 2,
                new String[]{"Sigla Unidade", "Descr. Unidade"},
                "SELECT CODVOL, DESCRVOL FROM TGFVOL WHERE ROWNUM <= 50");
        keyUnidade.getMainStage().setOnCloseRequest(e -> {
            if (keyUnidade.getKeyReturn() != null) {
                CbbUnidade.setValue(keyUnidade.getKeyReturn().get(0));
                TxtDescrUnidade.setText(keyUnidade.getKeyReturn().get(1));
            }
        });
        MaskField.NCMField(TxtNCM);
        TxtNCM.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setNCM("" + getOnlyNumber(newV))))
                    runEdit(() -> TxtNCM.setText(oldV));
        });
        MaskField.NumberField(TxtCodBarras, 20);
        TxtCodBarras.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setCodBarras(newV)))
                    runEdit(() -> TxtCodBarras.setText(oldV));
        });
        CbbUsoProd.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setUsoProd((Character) getMapKeyByValue(DDOpcoes.getListaUsoProd(), newV))))
                    runEdit(() -> CbbUsoProd.setValue(oldV));
        });
        MaskField.NumberField(TxtCodLocal, 22);
        TxtCodLocal.textProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && !newV.equals(oldV)) {
                if (!notifyEdit(() -> getProdutoCached().setCodLocal(getOnlyNumber(newV))))
                    runEdit(() -> TxtCodLocal.setText(oldV));
            }
        });
        SearchFieldTable keyLocal = new SearchFieldTable(ImgCodLocal, "Cód. Local", 5,
                new String[]{"Cód. Local", "Descr. Local", "Por Empresa", "Perm. Entrada", "Perm. Saída"},
                "SELECT CODLOCAL, DESCRLOCAL, CASE WHEN LOCALPOREMP = 'S' THEN 'Sim' ELSE 'Não' END AS LOCALPOREMP,\n" +
                        "CASE WHEN PERMENTRA = 'S' THEN 'Sim' ELSE 'Não' END AS PERMENTRA,\n" +
                        "CASE WHEN PERMSAI = 'S' THEN 'Sim' ELSE 'Não' END AS PERMSAI\n" +
                        "FROM TGFLOC");
        keyLocal.getMainStage().setOnCloseRequest(e -> {
            if (keyLocal.getKeyReturn() != null) {
                TxtCodLocal.setText(keyLocal.getKeyReturn().get(0));
                TxtDescrLocal.setText(keyLocal.getKeyReturn().get(1));
            }
        });
        CkbPromocao.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setPromocao(StrFromBoo(newV))))
                    runEdit(() -> CkbPromocao.setSelected(oldV));
        });
        CkbPermCompra.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setPermCompra(StrFromBoo(newV))))
                    runEdit(() -> CkbPermCompra.setSelected(oldV));
        });
        CkbPermVenda.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setPermVenda(StrFromBoo(newV))))
                    runEdit(() -> CkbPermVenda.setSelected(oldV));
        });
        CkbPermConsumo.selectedProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setPermConsumo(StrFromBoo(newV))))
                    runEdit(() -> CkbPermConsumo.setSelected(oldV));
        });
        MaskField.CharField(TxtCaracteristicas, 4000);
        TxtCaracteristicas.textProperty().addListener((obs, oldV, newV) -> {
            if (newV != null && !isAtualizando && newV != null && !newV.equals(oldV))
                if (!notifyEdit(() -> getProdutoCached().setCaracteristicas(newV)))
                    runEdit(() -> TxtCaracteristicas.setText(oldV));
            TxtCaracteristicas.setTooltip(new Tooltip("Campo limitado a 4000 caracteres.\n" + (4000 - newV.length()) + " disponível(eis)"));
        });
        MaskField.SpnFieldCtrl(SpnDecCusto, 0, 5);
        SpnDecCusto.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setDecCusto(newV)))
                    runEdit(() -> SpnDecCusto.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnDecQtd, 0, 5);
        SpnDecQtd.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setDecQtd(newV)))
                    runEdit(() -> SpnDecQtd.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnDecVlr, 0, 5);
        SpnDecVlr.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setDecVlr(newV)))
                    runEdit(() -> SpnDecVlr.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnVendaMin, 1, 999999999);
        SpnVendaMin.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setVendaMin(newV)))
                    runEdit(() -> SpnVendaMin.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnVendaMax, 1, 999999999);
        SpnVendaMax.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setVendaMax(newV)))
                    runEdit(() -> SpnVendaMax.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnEstMin, 0, 999999999);
        SpnEstMin.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setEstMin(newV)))
                    runEdit(() -> SpnEstMax.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnEstMax, 0, 999999999);
        SpnEstMax.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setEstMax(newV)))
                    runEdit(() -> SpnEstMax.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnPercComGer, 0.0, 100.0);
        SpnPercComGer.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setPerComGer(newV)))
                    runEdit(() -> SpnPercComGer.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnPercComVen, 0.0, 100.0);
        SpnPercComVen.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setPerComVen(newV)))
                    runEdit(() -> SpnPercComGer.getValueFactory().setValue(oldV));
        });
        MaskField.SpnFieldCtrl(SpnDescMax, 0.0, 100.0);
        SpnDescMax.valueProperty().addListener((obs, oldV, newV) -> {
            if (!isAtualizando && newV != null && newV != oldV)
                if (!notifyEdit(() -> getProdutoCached().setDescMax(newV)))
                    runEdit(() -> SpnDescMax.getValueFactory().setValue(oldV));
        });
    }

    private void showInFormMode(Produto produto) {
        isAtualizando = true;
        classStatus.setStatus(ControllerStatus.Nenhum);
        try {
            if (produto == null) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar exibir produto em modo formulário\n" +
                        "Favor tente novamente."));
                ModelException.getException().raise();
                return;
            }
            setProdutoCached(produto);
            TxtCodigo.setText("" + produto.getCodProd());
            TxtDescricao.setText(produto.getDescrProd());
            CkbAtivo.setSelected(ToBoo(produto.getAtivo()));
            LblDataHoraCriacao.setText(formatDate(DataHoraFormater, produto.getDHCriacao()));
            LblDataHoraAlter.setText(formatDate(DataHoraFormater, produto.getDHAlter()));
            LblUsuAlter.setText(produto.getCodUsuAlter() + " - " + produto.getLoginUsuAlter());
            TxtComplemento.setText(produto.getComplemento());
            TxtReferencia.setText(produto.getReferencia());
            CbbMarca.setValue(produto.getMarca());
            CbbUnidade.setValue(produto.getUnidade());
            TxtCodGrupo.setText("" + produto.getCodGrupoProd());
            TxtDescrGrupo.setText(produto.getDescrGrupoProd());
            TxtNCM.setText(produto.getNCM());
            TxtCodBarras.setText(produto.getCodBarras());
            CbbUsoProd.setValue(DDOpcoes.getListaUsoProd().get(produto.getUsoProd()));
            TxtCodLocal.setText("" + produto.getCodLocal());
            TxtDescrLocal.setText(produto.getDescrLocal());
            CkbPromocao.setSelected(ToBoo(produto.getPromocao()));
            CkbPermCompra.setSelected(ToBoo(produto.getPermCompra()));
            CkbPermVenda.setSelected(ToBoo(produto.getPermVenda()));
            CkbPermConsumo.setSelected(ToBoo(produto.getPermConsumo()));
            TxtCaracteristicas.setText(produto.getCaracteristicas());
            setImagemProd(produto.getImgProd());
            SpnDecCusto.getValueFactory().setValue(produto.getDecCusto());
            SpnDecQtd.getValueFactory().setValue(produto.getDecQtd());
            SpnDecVlr.getValueFactory().setValue(produto.getDecVlr());
            SpnVendaMin.getValueFactory().setValue(produto.getVendaMin());
            SpnVendaMax.getValueFactory().setValue(produto.getVendaMax());
            SpnEstMin.getValueFactory().setValue(produto.getEstMin());
            SpnEstMax.getValueFactory().setValue(produto.getEstMax());
            SpnPercComGer.getValueFactory().setValue(produto.getPerComGer());
            SpnPercComVen.getValueFactory().setValue(produto.getPerComVen());
            SpnDescMax.getValueFactory().setValue(produto.getDescMax());
            loadUnidadesAltProd();
            loadControlesProd();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar exibir produto\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            isAtualizando = false;
        }
    }

    private void Remove(ArrayList<Integer> CodProdutos) {
        if (CodProdutos.isEmpty()) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Sem linhas selecionadas, não é possível efetuar a exclusão."));
            ModelDialog.getDialog().raise();
            return;
        }
        ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), null,
                "Deseja realmente excluir o(s) produto(s) de código: " + CodProdutos.toString() + " ?"));
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
                conex = new DBParalelConex("DELETE TGFPRO WHERE CODPROD IN " + NewArrayParameter(CodProdutos) + "");
                conex.addParameter(CodProdutos);
                conex.run();
                BtnAtualizar.fire();
                setMessage("Produto(s) excluído(s) com sucesso!");
            } catch (SQLException ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar excluir produto(s)\n" + ex, ex));
                ModelException.getException().raise();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar excluir produto(s)\n" + ex, ex));
                ModelException.getException().raise();
            } finally {
                conex.desconecta();
            }
        }
    }

    private void Altera(Produto produto) {
        if(produto.getCodProd() == 0) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Não é permitido alterar o produto padrão 0"));
            ModelException.getException().raise();
            return;
        }
        try {
            conex = new DBParalelConex("UPDATE TGFPRO\n" +
                    "SET DESCRPROD = ?, COMPLEMENTO = ?, CARACTERISTICAS = ?, ATIVO = ?, CODVOL = ?, CODGRUPOPROD = ?, DHALTER = ?, CODUSUALTER = ?,\n" +
                    "CODBARRAS = ?, REFERENCIA = ?, MARCA = ?, USOPROD = ?, IMAGEM = ?, NCM = ?, DECCUSTO = ?, DECVLR = ?, DECQTD = ?, PERCOMGER = ?, PERCOMVEN = ?, DESCMAX = ?,\n" +
                    "VENDAMIN = ?, VENDAMAX = ?, PROMOCAO = ?, ESTMIN = ?, ESTMAX = ?, PERMCOMPRA = ?, PERMVENDA = ?, PERMCONSUMO = ?, CODLOCAL = ?\n" +
                    "WHERE CODPROD = ?");
            conex.addParameter(produto.getDescrProd());
            conex.addParameter(produto.getComplemento());
            conex.addParameter(produto.getCaracteristicas());
            conex.addParameter(produto.getAtivo());
            conex.addParameter(produto.getUnidade());
            conex.addParameter(produto.getCodGrupoProd());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(produto.getCodBarras());
            conex.addParameter(Nvl(produto.getReferencia()));
            conex.addParameter(produto.getMarca());
            conex.addParameter(produto.getUsoProd());
            if (produto.getImgProd() == null) conex.addParameter(null);
            else {
                BufferedImage bImage = SwingFXUtils.fromFXImage(produto.getImgProd(), null);
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                ImageIO.write(bImage, "png", s);
                conex.addParameter(s.toByteArray());
            }
            conex.addParameter(Nvl(produto.getNCM()));
            conex.addParameter(produto.getDecCusto());
            conex.addParameter(produto.getDecVlr());
            conex.addParameter(produto.getDecQtd());
            conex.addParameter(produto.getPerComGer());
            conex.addParameter(produto.getPerComVen());
            conex.addParameter(produto.getDescMax());
            conex.addParameter(produto.getVendaMin());
            conex.addParameter(produto.getVendaMax());
            conex.addParameter(produto.getPromocao());
            conex.addParameter(produto.getEstMin());
            conex.addParameter(produto.getEstMax());
            conex.addParameter(produto.getPermCompra());
            conex.addParameter(produto.getPermVenda());
            conex.addParameter(produto.getPermConsumo());
            conex.addParameter(produto.getCodLocal());
            conex.addParameter(produto.getCodProd());
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Produto alterado com sucesso!");
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar gravar alterações do produto\n" + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar gravar alterações do produto\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void Adiciona(Produto produto) {
        try {
            conex = new DBParalelConex("SELECT GET_COD('CODPROD', 'TGFPRO') AS COD FROM DUAL");
            conex.createSet();
            conex.rs.next();
            int codProd = conex.rs.getInt("COD");
            conex = new DBParalelConex("INSERT INTO TGFPRO\n" +
                    "(CODPROD, DESCRPROD, COMPLEMENTO, CARACTERISTICAS, ATIVO, CODVOL, CODGRUPOPROD, DHCRIACAO, DHALTER, CODUSUALTER,\n" +
                    "CODBARRAS, REFERENCIA, MARCA, USOPROD, IMAGEM, NCM, DECCUSTO, DECVLR, DECQTD, PERCOMGER, PERCOMVEN, DESCMAX,\n" +
                    "VENDAMIN, VENDAMAX, PROMOCAO, ESTMIN, ESTMAX, PERMCOMPRA, PERMVENDA, PERMCONSUMO, CODLOCAL)\n" +
                    "VALUES\n" +
                    "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?)");
            conex.addParameter(codProd);
            conex.addParameter(produto.getDescrProd());
            conex.addParameter(produto.getComplemento());
            conex.addParameter(produto.getCaracteristicas());
            conex.addParameter(produto.getAtivo());
            conex.addParameter(produto.getUnidade());
            conex.addParameter(produto.getCodGrupoProd());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(produto.getCodBarras());
            conex.addParameter(Nvl(produto.getReferencia()));
            conex.addParameter(produto.getMarca());
            conex.addParameter(produto.getUsoProd());
            if (produto.getImgProd() == null) conex.addParameter(null);
            else {
                BufferedImage bImage = SwingFXUtils.fromFXImage(produto.getImgProd(), null);
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                ImageIO.write(bImage, "png", s);
                conex.addParameter(s.toByteArray());
            }
            conex.addParameter(Nvl(produto.getNCM()));
            conex.addParameter(produto.getDecCusto());
            conex.addParameter(produto.getDecVlr());
            conex.addParameter(produto.getDecQtd());
            conex.addParameter(produto.getPerComGer());
            conex.addParameter(produto.getPerComVen());
            conex.addParameter(produto.getDescMax());
            conex.addParameter(produto.getVendaMin());
            conex.addParameter(produto.getVendaMax());
            conex.addParameter(produto.getPromocao());
            conex.addParameter(produto.getEstMin());
            conex.addParameter(produto.getEstMax());
            conex.addParameter(produto.getPermCompra());
            conex.addParameter(produto.getPermVenda());
            conex.addParameter(produto.getPermConsumo());
            conex.addParameter(Nvl("" + produto.getCodLocal()));
            conex.run();
            classStatus.setStatus(ControllerStatus.Nenhum);
            TxtCodigo.setText("" + codProd);
            CtrlBtns(CtrlStatus.Atualizar);
            setMessage("Produto incluído com sucesso!");
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar produto\n" + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar cadastrar produto\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    public void loadTableValues() {
        isAtualizando = true;
        ListProdutos.clear();
        TabelaProdutos.getColumns().forEach(Colunas -> Colunas.setGraphic(null));
        try {
            conex = new DBParalelConex("SELECT PRO.*, GRU.DESCRGRUPOPROD, USU.LOGIN, LOC.DESCRLOCAL\n" +
                    "FROM TGFPRO PRO\n" +
                    "INNER JOIN TGFGRU GRU\n" +
                    "ON (PRO.CODGRUPOPROD = GRU.CODGRUPOPROD)\n" +
                    "INNER JOIN TGFLOC LOC\n" +
                    "ON (PRO.CODLOCAL = LOC.CODLOCAL)\n" +
                    "LEFT JOIN TSIUSU USU\n" +
                    "ON (PRO.CODUSUALTER = USU.CODUSU)\n" +
                    "WHERE PRO.CODPROD <> 0\n" +
                    "ORDER BY PRO.CODPROD");
            conex.createSet();
            while (conex.rs.next()) {
                Produto prodAtual = new Produto(
                        conex.rs.getInt("CODPROD"),
                        conex.rs.getInt("CODGRUPOPROD"),
                        conex.rs.getInt("CODUSUALTER"),
                        conex.rs.getInt("DECCUSTO"),
                        conex.rs.getInt("DECVLR"),
                        conex.rs.getInt("DECQTD"),
                        conex.rs.getInt("CODLOCAL"),
                        conex.rs.getDouble("PERCOMGER"),
                        conex.rs.getDouble("PERCOMVEN"),
                        conex.rs.getDouble("DESCMAX"),
                        conex.rs.getInt("VENDAMIN"),
                        conex.rs.getInt("VENDAMAX"),
                        conex.rs.getInt("ESTMIN"),
                        conex.rs.getInt("ESTMAX"),
                        conex.rs.getString("DESCRPROD"),
                        Nvl(conex.rs.getString("COMPLEMENTO")),
                        Nvl(conex.rs.getString("CARACTERISTICAS")),
                        conex.rs.getString("DESCRGRUPOPROD"),
                        conex.rs.getString("CODVOL"),
                        conex.rs.getString("LOGIN"),
                        conex.rs.getString("CODBARRAS"),
                        conex.rs.getString("REFERENCIA"),
                        conex.rs.getString("MARCA"),
                        conex.rs.getString("USOPROD"),
                        conex.rs.getString("NCM"),
                        conex.rs.getString("DESCRLOCAL"),
                        conex.rs.getString("ATIVO"),
                        conex.rs.getString("PROMOCAO"),
                        conex.rs.getString("PERMCOMPRA"),
                        conex.rs.getString("PERMVENDA"),
                        conex.rs.getString("PERMCONSUMO"),
                        conex.rs.getTimestamp("DHCRIACAO"),
                        conex.rs.getTimestamp("DHALTER"),
                        ImgFromBytes(conex.rs.getBytes("IMAGEM")));
                ListProdutos.add(prodAtual);
            }
            CbbMarca.setItems(ListaQuerys.getListMarcas());
            CbbUnidade.setItems(ListaQuerys.getListUnidades());
            qtdLinhasTab = ListProdutos.size();
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar produtos\n" + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar produtos\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
            isAtualizando = false;
        }
    }

    public TableColumn[] getTableColumns() {
        TableColumn[] tbColunas = new TableColumn[5];
        tbColunas[0] = new ModelTableColumn<Produto, Integer>("#", "CodProd");
        tbColunas[1] = new ModelTableColumn<Produto, String>("Ativo", "Ativo");
        tbColunas[2] = new ModelTableColumn<Produto, String>("Descrição", "DescrProd");
        tbColunas[3] = new ModelTableColumn<Produto, String>("Usado como", "UsoProd");
        tbColunas[4] = new ModelTableColumn<Produto, Integer>("Dec. Custo", "DecCusto");
        /*tbColunas[5] = new ModelTableColumn<Produto, String>("Marca", "Marca", TbColClass.TextField, notifyEdit());
        tbColunas[6] = new ModelTableColumn<Produto, String>("Complemento", "Complemento", TbColClass.TextField, notifyEdit());
        tbColunas[7] = new ModelTableColumn<Produto, String>("Referência", "Referencia", TbColClass.TextField, notifyEdit());
        tbColunas[8] = new ModelTableColumn<Produto, String>("Cód. Grupo Prod.", "Marca", TbColClass.KeyField, notifyEdit());
        tbColunas[9] = new ModelTableColumn<Produto, String>("Desc. Grupo Prod.", "DescrGrupoProd", TbColClass.Null);
        tbColunas[10] = new ModelTableColumn<Produto, String>("Cód. Barras", "CodBarras", TbColClass.TextField, notifyEdit());
        tbColunas[11] = new ModelTableColumn<Produto, String>("NCM", "NCM", TbColClass.TextField, notifyEdit());
        tbColunas[12] = new ModelTableColumn<Produto, String>("Em promoção", "Promocao", TbColClass.Logico, notifyEdit());
        tbColunas[13] = new ModelTableColumn<Produto, Integer>("Cód. Local", "CodLocal", TbColClass.KeyField, notifyEdit());
        tbColunas[14] = new ModelTableColumn<Produto, String>("Descr. Local", "DescrLocal", TbColClass.Null);
        tbColunas[15] = new ModelTableColumn<Produto, String>("Perm. Comprar?", "PermCompra", TbColClass.Logico, notifyEdit());
        tbColunas[16] = new ModelTableColumn<Produto, String>("Perm. Vender?", "PermVenda", TbColClass.Logico, notifyEdit());
        tbColunas[17] = new ModelTableColumn<Produto, String>("Perm. Consumir?", "PermConsumo", TbColClass.Logico, notifyEdit());
        tbColunas[18] = new ModelTableColumn<Produto, Timestamp>("Data/Hora Criação", "DHCriacao", TbColClass.Null);
        tbColunas[19] = new ModelTableColumn<Produto, Timestamp>("Data/Hora Alteração", "DHAlter", TbColClass.Null);
        tbColunas[20] = new ModelTableColumn<Produto, String>("Cód. Usu. Alteração", "CodUsuAlter", TbColClass.Null);
        tbColunas[21] = new ModelTableColumn<Produto, String>("Login Usu. Alteração", "LoginUsuAlter", TbColClass.Null);
        tbColunas[22] = new ModelTableColumn<Produto, Integer>("Dec. Custo", "DecCusto", TbColClass.IntegerSpinner, notifyEdit());
        tbColunas[23] = new ModelTableColumn<Produto, Integer>("Dec. Valor", "DecVlr", TbColClass.IntegerSpinner, notifyEdit());
        tbColunas[24] = new ModelTableColumn<Produto, Integer>("Dec. Qtd.", "DecQtd", TbColClass.IntegerSpinner, notifyEdit());
        tbColunas[25] = new ModelTableColumn<Produto, Double>("% Com. Gerente", "PerComGer", TbColClass.DoubleSpinner, notifyEdit());
        tbColunas[26] = new ModelTableColumn<Produto, Double>("% Com. Vendedor", "PerComVen", TbColClass.DoubleSpinner, notifyEdit());
        tbColunas[27] = new ModelTableColumn<Produto, Double>("Venda Mín.", "VendaMin", TbColClass.DoubleSpinner, notifyEdit());
        tbColunas[28] = new ModelTableColumn<Produto, Double>("Venda Máx.", "VendaMax", TbColClass.DoubleSpinner, notifyEdit());
        tbColunas[29] = new ModelTableColumn<Produto, BigDecimal>("Est. Mín.", "EstMin", TbColClass.IntegerField, notifyEdit());
        tbColunas[30] = new ModelTableColumn<Produto, BigDecimal>("Est. Máx.", "EstMax", TbColClass.IntegerField, notifyEdit());
        tbColunas[31] = new ModelTableColumn<Produto, ImageView>("Imagem", "ImgProd", TbColClass.Image);*/
        return tbColunas;
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
                showInFormMode(new Produto());
                TxtCodigo.setText("");
                LblDataHoraAlter.setText("");
                LblDataHoraCriacao.setText("");
                LblUsuAlter.setText("");
                isAtualizando = false;
                break;
            case FormGrade:
                break;
            case Atualizar:
                classStatus.setStatus(ControllerStatus.Nenhum);
                int cachedCodigo = getOnlyNumber(TxtCodigo.getText());
                loadTableValues();
                if (cachedCodigo >= 0) {
                    for (Produto prod : TabelaProdutos.getItems()) {
                        if (prod.getCodProd() == cachedCodigo) {
                            TabelaProdutos.getSelectionModel().clearSelection();
                            TabelaProdutos.getSelectionModel().select(prod);
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
                classStatus.setStatus(ControllerStatus.Adicionando); //Tem que ser após o showInFormMode
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
                            Altera(getProdutoCached());
                            break;
                        case Adicionando:
                            Adiciona(getProdutoCached());
                            break;
                        case Nenhum:
                        default:
                            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro no método CadProdutosController.CtrlBtns(" + Action + ")." + classStatus.getStatus() + "\n" +
                                    "Retorno diferente do esperado, contate o administrado!"));
                            ModelException.getException().raise();
                            break;
                    }
                }
                break;
            case Cancelar:
                int cachedCodigo2 = getOnlyNumber(TxtCodigo.getText());
                loadTableValues();
                if (cachedCodigo2 >= 0) {
                    for (Produto prod : TabelaProdutos.getItems()) {
                        if (prod.getCodProd() == cachedCodigo2) {
                            TabelaProdutos.getSelectionModel().clearSelection();
                            TabelaProdutos.getSelectionModel().select(prod);
                            cachedCodigo2 = -3;
                            break;
                        }
                    }
                    if (cachedCodigo2 >= 0) { //Produto não está mais na lista
                        CtrlBtns(CtrlStatus.Primeiro);
                    }
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
                TxtCodigo.setText(""); //Ele não ativa o EditMode
                getProdutoCached().setCodProd(0);
                loadUnidadesAltProd();
                loadControlesProd();
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
                ArrayList<Integer> CodProdutosToRemove = new ArrayList<>();
                if (PanelForm.isVisible()) {
                    int codToRemove = getOnlyNumber(TxtCodigo.getText());
                    if (codToRemove >= 0) {
                        CodProdutosToRemove.add(codToRemove);
                    } else {
                        ModelException.setNewException(new ModelException(this.getClass(), null, "Sem produto carregado, não é possível excluir\nFavor verifique"));
                        ModelException.getException().raise();
                        return;
                    }
                } else {
                    TabelaProdutos.getSelectionModel().getSelectedItems().forEach(CodProduto -> CodProdutosToRemove.add((Integer) CodProduto.getCodProd()));
                }
                Remove(CodProdutosToRemove);
                break;
            case Primeiro:
                TabelaProdutos.getSelectionModel().clearSelection();
                TabelaProdutos.getSelectionModel().select(0);
                break;
            case Anterior:
                TabelaProdutos.getSelectionModel().clearSelection();
                TabelaProdutos.getSelectionModel().select(lastLoadedCodigo - 1);
                break;
            case Proximo:
                TabelaProdutos.getSelectionModel().clearSelection();
                TabelaProdutos.getSelectionModel().select(lastLoadedCodigo + 1);
                break;
            case Ultimo:
                TabelaProdutos.getSelectionModel().clearSelection();
                TabelaProdutos.getSelectionModel().select(qtdLinhasTab - 1);
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

    public Boolean validChanges() {
        if (Nvl(getProdutoCached().getDescrProd()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Descrição do produto não pode estar vazia!"));
            ModelDialog.getDialog().raise();
            return false;
        } else if (!Nvl(getProdutoCached().getAtivo()).equals("S") && !Nvl(getProdutoCached().getAtivo()).equals("N")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.ERROR, this.getClass(), null,
                    "Erro: O valor de 'Ativo' está diferente de ('S', 'N)\nVerifique: " + getProdutoCached().getAtivo()));
            ModelDialog.getDialog().raise();
            return false;
        } else if (Nvl(getProdutoCached().getMarca()).equals("")) {
            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                    "Marca do produto não pode estar vazia!"));
            ModelDialog.getDialog().raise();
            return false;
        } else {
            return true;
        }
    }

    private void changeImageProd() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar imagem p/ produto:");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
        File file = fileChooser.showOpenDialog(PanelFundo.getScene().getWindow());
        if (file != null) {
            try {
                setImagemProd(new Image(file.toURI().toString()));
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar atualizar imagem do produto\n" + ex, ex));
                ModelException.getException().raise();
            }
        }
    }

    public void setImagemProd(Image imagemProd) {
        Boolean hasImage = true;
        if (imagemProd == null) {
            hasImage = false;
            imagemProd = new Image("/br/com/sinergia/properties/images/Icon_Sem_Imagem.png");
        }
        BtnLimparImagem.setVisible(hasImage);
        Image finalImagemProd = imagemProd;
        ImageView imageViewProd = new ImageView(finalImagemProd);
        imageViewProd.setFitWidth(356);
        imageViewProd.setFitHeight(352);
        LblImagem.setGraphic(imageViewProd);
        if (!isAtualizando && hasImage) notifyEdit(() -> getProdutoCached().setImgProd(finalImagemProd));
        if (!isAtualizando && !hasImage) notifyEdit(() -> getProdutoCached().setImgProd(null));
    }

    public Boolean notifyEdit(Runnable changes) {
        if (classStatus.getStatus() != ControllerStatus.Adicionando) {
            if (!AcessoTela.getAltera()) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                        "Usuário sem acesso para alterações na tela de Produtos"));
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

    public void runEdit(Runnable changes) {
        isAtualizando = true;
        changes.run();
        isAtualizando = false;
    }

    public void setMessage(String mensagem) {
        LblAcao.setText(mensagem);
        LblAcao.setVisible(true);
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2000), //2 Segundos
                ae -> LblAcao.setVisible(false)));
        timeline.play();
    }

    private void loadUnidadesAltProd() {
        try {
            if (UndAltController == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/com/sinergia/views/VolumesAlt.fxml"));
                AnchorPane root = fxmlLoader.load();
                UndAltController = fxmlLoader.getController();
                UndAltController.setDescrVolPrincipal(Nvl(CbbUnidade.getValue().toString()));
                UndAltController.setCodProd(getOnlyNumber(TxtCodigo.getText()));
                root.prefWidthProperty().bind(TtpUnidades.widthProperty().add(20));
                root.prefHeightProperty().bind(TtpUnidades.heightProperty().subtract(5));
                TtpUnidades.setContent(root);
            } else {
                UndAltController.setCodProd(getLogicNumber(getProdutoCached().getCodProd()));
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar exibir tela de Unidades Alternativas\n" + ex, ex));
            ModelException.getException().raise();
        }
    }

    private void loadControlesProd() {
        try {
            if (CtrlController == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/com/sinergia/views/ControleProd.fxml"));
                AnchorPane root = fxmlLoader.load();
                CtrlController = fxmlLoader.getController();
                CtrlController.setCodProd(getLogicNumber(getProdutoCached().getCodProd()));
                root.prefWidthProperty().bind(TtpControles.widthProperty().add(20));
                root.prefHeightProperty().bind(TtpControles.heightProperty().subtract(5));
                TtpControles.setContent(root);
            } else {
                CtrlController.setCodProd(getLogicNumber(getProdutoCached().getCodProd()));
            }
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar exibir tela de Cadastro de Controle de Produto\n" + ex, ex));
            ModelException.getException().raise();
        }
    }

    private Produto getProdutoCached() {
        return this.cachedProd;
    }

    private void setProdutoCached(Produto prod) {
        this.cachedProd = prod;
    }
}
