package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.Statement;
import br.com.sinergia.functions.modules.*;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppInfo;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {

    CtrlLembretes userLembretes = new CtrlLembretes();
    CtrlMesagensUsu ctrlMesagensUsu = new CtrlMesagensUsu();
    Statement statement;
    Boolean SemErro = true;
    Timeline timeline;

    @FXML
    private AnchorPane PanePrincipal;
    @FXML
    private Accordion MenuAccordion;
    @FXML
    private TabPane AbaPane;
    @FXML
    private TitledPane TtpMensagens, TtpPropriedades;
    @FXML
    private VBox VBoxRecentes, VBoxFavoritos;
    @FXML
    private JFXTextField TxtPesqTela;
    @FXML
    private HTMLEditor TxtLembrete;
    @FXML
    private ImageView ImgUsu;
    @FXML
    private Button BtnHome, BtnMensagens, BtnPropriedades, BtnSalvarLemb, BtnCancelarLemb;
    @FXML
    private JFXButton BtnAttSenha, BtnPreferencias, BtnDeslogar, BtnSair;
    @FXML
    private Label LblInfoFaixa, LblDataLogin, LblHoraLogin, LblVersaoExec, LblVersaoBD, LblCodSessao, LblPerfil, LblNomeLembrete,
            LblAddRemFavorito, LblDHLembrete;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppChamadas();
        EstruturaCampos();
        LateRunner();
    }

    private void EstruturaCampos() {
        try {
            MaskField.MaxCharField(TxtPesqTela, 30);
            LblNomeLembrete.setText("Olá, " + User.getCurrent().getNomeUsu());
            userLembretes.getLembrete();
            TxtLembrete.setHtmlText(userLembretes.TextoLembrete);
            LblDHLembrete.setText("Data/Hora Alteração: " + Functions.DataHoraFormater.format(userLembretes.DataLembrete));
            LblInfoFaixa.setText("Usuário: " + User.getCurrent().getCódUsu() + " - " + User.getCurrent().getLoginUsu() + " x " +
                    "Empresa: " + User.getCurrent().getCódEmp() + " - " + User.getCurrent().getRazaoSocialEmp());
            LblDataLogin.setText(Functions.DataFormater.format(User.getCurrent().getDHLogin()));
            LblHoraLogin.setText(Functions.HoraFormater.format(User.getCurrent().getDHLogin()));
            LblVersaoExec.setText("V. Sistema: " + AppInfo.getInfo().getVersãoExec());
            LblVersaoBD.setText("V. Banco: " + AppInfo.getInfo().getVersãoBD());
            LblCodSessao.setText("Cód. Sessão: " + User.getCurrent().getCódSessão());
            LblPerfil.setText("Cód Perfil: " + User.getCurrent().getCódPerfil());
            ImageView cachedImgUsu = new ImageView(User.getCurrent().getFotoUsu());
            cachedImgUsu.setFitHeight(100);
            cachedImgUsu.setFitWidth(100);
            ImgUsu.setImage(cachedImgUsu.getImage());
            ctrlMesagensUsu.criaTitledIni();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro interno no método: EstruturaCampos()\n"
                    + ex.getMessage() + "\nA aplicação será finalizada.", ex));
            ModelException.getException().raise();
            System.exit(0);
        }
    }

    private void AppChamadas() {
        try {
            AppObjects.setAppObjects(new AppObjects(AbaPane, MenuAccordion, LblAddRemFavorito, VBoxRecentes, VBoxFavoritos));
            AppObjects.getAppObjects().setTtpMensagens(TtpMensagens);
            AppObjects.getAppObjects().setBtnMensagens(BtnMensagens);
            BtnHome.setOnAction(e -> {
                AppObjects.getAppObjects().getAbaPane().getSelectionModel().select(0);
            });
            AccordionMenu Accordion = new AccordionMenu();
            TxtPesqTela.setOnAction(e -> {
                Accordion.filtra(TxtPesqTela.getText());
            });
            TtpMensagens.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                if (wasExpanded) {
                    TtpMensagens.setVisible(false);
                }
                if (isNowExpanded) {
                    TtpMensagens.setVisible(true);
                }
            });
            TtpPropriedades.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                if (wasExpanded) {
                    TtpPropriedades.setVisible(false);
                }
                if (isNowExpanded) {
                    TtpPropriedades.setVisible(true);
                }
            });
            BtnMensagens.setOnAction(e -> {
                if (TtpMensagens.isVisible()) {
                    TtpMensagens.setExpanded(false);
                    TtpMensagens.setVisible(false);
                } else {
                    TtpMensagens.setExpanded(true);
                    TtpMensagens.setVisible(true);
                }
            });
            BtnPropriedades.setOnAction(e -> {
                if (TtpPropriedades.isVisible()) {
                    TtpPropriedades.setExpanded(false);
                    TtpPropriedades.setVisible(false);
                } else {
                    TtpPropriedades.setExpanded(true);
                    TtpPropriedades.setVisible(true);
                }
            });
            ImgUsu.setOnMouseClicked(e -> changeFotoUsu());
            BtnAttSenha.setOnAction(e -> CtrlSenhaUsu.getCtrlSenhaUsu().createDialogSenha(User.getCurrent().getCódUsu()));
            //BtnPreferencias.setOnAction(e -> Preferencias.show());
            BtnDeslogar.setOnAction(e -> Logout(true));
            BtnSair.setOnAction(e -> Logout(false));
            BtnSalvarLemb.setOnAction(e -> {
                ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                        null,
                        "Deseja realmente salvar o novo lembrete?"));
                ButtonType[] Btns = new ButtonType[2];
                Btns[0] = new ButtonType("Sim");
                Btns[1] = new ButtonType("Cancelar");
                ModelDialogButton.getDialogButton().createButton(Btns);
                if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[0]) {
                    userLembretes.setNewLembrete(TxtLembrete.getHtmlText());
                    if (userLembretes.noError) {
                        LblDHLembrete.setText("Data/Hora Alteração: " + Functions.DataHoraFormater.format(Timestamp.from(Instant.now())));
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(),
                                null, "Novo lembrete salvo com sucesso"));
                        ModelDialog.getDialog().raise();
                    }
                }
            });
            BtnCancelarLemb.setOnAction(e -> {
                ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                        null,
                        "Deseja realmente retomar o último lembrete?"));
                ButtonType[] Btns = new ButtonType[2];
                Btns[0] = new ButtonType("Retomar");
                Btns[1] = new ButtonType("Cancelar");
                ModelDialogButton.getDialogButton().createButton(Btns);
                if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[0]) {
                    TxtLembrete.setHtmlText(userLembretes.TextoLembrete);
                    LblDHLembrete.setText("Data/Hora Alteração: " + Functions.DataHoraFormater.format(userLembretes.DataLembrete));
                }
            });
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro interno no método: AppChamadas()\n"
                    + ex.getMessage() + "\nA aplicação será finalizada.", ex));
            ModelException.getException().raise();
            System.exit(0);
        }
    }

    private void Logout(Boolean createLoginStage) {
        TtpPropriedades.setExpanded(false);
        ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                null,
                "Deseja realmente sair do sistema?"));
        ButtonType[] Btns = new ButtonType[2];
        Btns[0] = new ButtonType("Sim");
        Btns[1] = new ButtonType("Não");
        ModelDialogButton.getDialogButton().createButton(Btns);
        if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) {
            //Não sair
        } else {
            if (AppObjects.getAppObjects().getAbaPane().getTabs().size() > 1) { //Sair
                String arqFecha = CtrlArquivos.getArquivos().Busca(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes");
                if (arqFecha.equals("0") || arqFecha.equals("P")) {//Não existe registro ainda ou perguntar
                    ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                            null,
                            "O sistema detectou que existem telas abertas\n" +
                                    "Caso algumas dessas telas estejam com alteração pendente, as alterações serão desfeitas\n" +
                                    "Dejesa realmente sair ou revisar as telas?"));
                    CheckBox Ckb = new CheckBox("Não perguntar novamente?");
                    Ckb.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                    ModelDialogButton.getDialogButton().addCheckBox(Ckb);
                    Btns[0] = new ButtonType("Sair");
                    Btns[1] = new ButtonType("Revisar");
                    ModelDialogButton.getDialogButton().createButton(Btns);
                    if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[0]) {
                        if (Ckb.isSelected()) {
                            CtrlArquivos.getArquivos().Registra(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes", "S");
                        }
                        User.getCurrent().closeSessão();
                        if (createLoginStage) {
                            AppObjects.getAppObjects().getStageMain().close();
                            Functions.showLoginStage(this.getClass());
                        } else {
                            System.exit(0);
                        }
                    } else {
                        if (Ckb.isSelected()) {
                            CtrlArquivos.getArquivos().Registra(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes", "N");
                        } //Não quis sair
                    }
                } else {
                    if (arqFecha.equals("S")) { //Apenas fecha
                        User.getCurrent().closeSessão();
                        if (createLoginStage) {
                            AppObjects.getAppObjects().getStageMain().close();
                            Functions.showLoginStage(this.getClass());
                        } else {
                            System.exit(0);
                        }
                    } else if (arqFecha.equals("N")) {
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING,
                                this.getClass(), null, "Existem telas ainda abertas, finalize-as primeiro"));
                        ModelDialog.getDialog().raise();
                    } else {
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.ERROR, this.getClass(), null,
                                "Erro no método CtrlArquivos.Finalizar com telas pendentes\n" +
                                        "Reposta diferente do esperado: " + arqFecha));
                        ModelDialog.getDialog().raise();
                    }
                }
            } else {
                User.getCurrent().closeSessão();
                if (createLoginStage) {
                    AppObjects.getAppObjects().getStageMain().close();
                    Functions.showLoginStage(this.getClass());
                } else {
                    System.exit(0);
                }
            }
        }
    }

    private void changeFotoUsu() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar foto p/ usuário :");
        fileChooser.getExtensionFilters().addAll
                (new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
        File file = fileChooser.showOpenDialog(TtpPropriedades.getScene().getWindow());
        if (file != null) {
            try { //Primeiro grava a imagem no banco de dados, só se der certo, que altera no ImgView
                ImageView cachedImgUsu = new ImageView(file.toURI().toString());
                User.getCurrent().setFotoUsu(cachedImgUsu.getImage());
                BufferedImage bImage = SwingFXUtils.fromFXImage(cachedImgUsu.getImage(), null);
                ByteArrayOutputStream s = new ByteArrayOutputStream();
                ImageIO.write(bImage, "png", s);
                statement = new Statement("UPDATE TSIUSU\n" +
                        "SET FOTO = ?\n" +
                        "WHERE CODUSU = ?");
                statement.addParameter(s.toByteArray());
                statement.addParameter(User.getCurrent().getCódUsu());
                statement.run();
                cachedImgUsu.setFitHeight(100);
                cachedImgUsu.setFitWidth(100);
                ImgUsu.setImage(cachedImgUsu.getImage());
            } catch (IOException ex) {
                ModelException.setNewException(new ModelException(this.getClass(),
                        null,
                        "Erro ao tentar alterar a foto do usuário\n" + ex,
                        ex));
                ModelException.getException().raise();
            } catch (SQLException ex) {
                ModelException.setNewException(new ModelException(this.getClass(),
                        null,
                        "Erro ao tentar alterar a foto do usuário\n" + ex,
                        ex));
                ModelException.getException().raise();
            } finally {
                statement.end();
            }
        }
    }

    private void LateRunner() {
        KeyFrame runExecuter = new KeyFrame(Duration.millis(1000), e -> {
            AppObjects.getAppObjects().setStageMain((Stage) BtnSair.getScene().getWindow());
            CtrlAtalhos.getAtalhos().setNew(PanePrincipal.getScene(),
                    "CTRL+F", () -> TxtPesqTela.requestFocus());
            CtrlAtalhos.getAtalhos().setNew(PanePrincipal.getScene(),
                    "CTRL+S", () -> {
                        BtnSalvarLemb.fire();
                    });
            CtrlAtalhos.getAtalhos().setNew(PanePrincipal.getScene(),
                    "CTRL+P", () -> {
                        BtnPropriedades.fire();
                    });
            CtrlAtalhos.getAtalhos().setNew(PanePrincipal.getScene(),
                    "CTRL+W", () -> {
                        int idxTab = AbaPane.getSelectionModel().getSelectedIndex();
                        if (idxTab != 0) {
                            String TelaAberta = AbaPane.getTabs().get(idxTab).getText();
                            AppObjects.getAppObjects().getTelasAbertas().remove(TelaAberta);
                            AbaPane.getTabs().remove(idxTab);
                        }
                    });
        });
        timeline = new Timeline(runExecuter);
        timeline.setCycleCount(1);
        timeline.play();
    }
}
