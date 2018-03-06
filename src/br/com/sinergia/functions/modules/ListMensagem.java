package br.com.sinergia.functions.modules;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class ListMensagem {

    DBParalelConex statement;
    private ObservableList<UniqueMessageList> Messages = FXCollections.observableArrayList();
    ListView<UniqueMessageList> ListMessage = new ListView<>(Messages);
    private TitledPane PaneMessages;
    private ScrollPane scrollPane = new ScrollPane();
    private int CódÚltMsg = 0;
    private int QtdMsgNãoVisualizadas = 0;
    private Timeline loopGetNewMessages;

    public ListMensagem(TitledPane paneMessages) {
        this.PaneMessages = paneMessages;
        this.PaneMessages.setContent(scrollPane);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        ListMessage.setOnMouseClicked(e -> {
            if (e.getClickCount() > 1) {
                if (ListMessage.getItems() != null
                        && ListMessage.getItems().get(ListMessage.getSelectionModel().getSelectedIndex()) != null) {
                    showUnique(ListMessage.getItems().get(ListMessage.getSelectionModel().getSelectedIndex()));
                }
            }
        });
        ListMessage.setCellFactory(new Callback<ListView<UniqueMessageList>, ListCell<UniqueMessageList>>() {
            @Override
            public ListCell<UniqueMessageList> call(ListView<UniqueMessageList> arg0) {
                return new ListCell<UniqueMessageList>() {
                    @Override
                    protected void updateItem(UniqueMessageList Msg, boolean bln) {
                        super.updateItem(Msg, bln);
                        if (Msg != null) {
                            ListMessage.setId("" + Msg.getCódMsg());
                            ImageView ImgRem = new ImageView(Msg.getImgFotoRem());
                            ImgRem.setFitHeight(50);
                            ImgRem.setFitWidth(42);
                            Text TxtTítulo = new Text(Msg.getTítuloMsg());
                            TxtTítulo.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
                            TxtTítulo.setFill(Color.BLACK);
                            ImageView ImgStatusMsg;
                            if (Msg.getVisualizada()) {
                                ImgStatusMsg = new ImageView("/br/com/sinergia/properties/images/Icone_Msg_Visualizada.png");
                                setStyle("-fx-control-inner-background: LightGray");
                            } else {
                                ImgStatusMsg = new ImageView("/br/com/sinergia/properties/images/Icone_Msg_Pendente.png");
                                setStyle("-fx-control-inner-background: White");
                            }
                            ImgStatusMsg.setFitWidth(16);
                            ImgStatusMsg.setFitHeight(16);
                            Text TxtLogin = new Text(Msg.getLoginRem());
                            TxtLogin.setFont(Font.font("System", 12));
                            Text TxtDHMsg = new Text(Msg.getDataHoraMsg());
                            TxtDHMsg.setFont(Font.font("System", FontPosture.ITALIC, 12));
                            TxtDHMsg.setFill(Color.valueOf("#807c7c"));
                            HBox Box = new HBox(TxtTítulo, new Text(" "), ImgStatusMsg);
                            HBox Box1 = new HBox(TxtLogin);
                            HBox Box2 = new HBox(TxtDHMsg);
                            VBox Box3 = new VBox(Box, Box1, Box2);
                            HBox hBox = new HBox(ImgRem, Box3);
                            hBox.setSpacing(4);
                            setGraphic(hBox);
                        }
                    }
                };
            }
        });
        VBox root = new VBox();
        root.getChildren().add(ListMessage);
        root.setFillWidth(true);
        scrollPane.setContent(root);
        KeyFrame toDoGetNewMessages = new KeyFrame(Duration.millis(1500), e -> getNewMessages());
        loopGetNewMessages = new Timeline(toDoGetNewMessages);
        loopGetNewMessages.setCycleCount(Timeline.INDEFINITE);
        loopGetNewMessages.play();
    }

    public int getCódÚltMsg() {
        return CódÚltMsg;
    }

    public void setCódÚltMsg(int códÚltMsg) {
        CódÚltMsg = códÚltMsg;
    }

    public int getQtdMsgNãoVisualizadas() {
        return QtdMsgNãoVisualizadas;
    }

    public void setQtdMsgNãoVisualizadas(int qtdMsgNãoVisualizadas) {
        QtdMsgNãoVisualizadas = qtdMsgNãoVisualizadas;
    }

    public static class UniqueMessageList {
        private int CódMsg;
        private int PrioridadeMsg;
        private int CódUsuRem;
        private Image ImgFotoRem;
        private String LoginRem;
        private String DataHoraMsg;
        private String TítuloMsg;
        private String Mensagem;
        private Boolean Visualizada;

        public UniqueMessageList(int códMsg, int prioridadeMsg, int códUsuRem, Image imgFotoRem, String loginRem,
                                 String dataHoraMsg, String títuloMsg, String mensagem, Boolean visualizada) {
            super();
            this.setCódMsg(códMsg);
            this.setPrioridadeMsg(prioridadeMsg);
            this.setCódUsuRem(códUsuRem);
            this.setImgFotoRem(imgFotoRem);
            this.setLoginRem(loginRem);
            this.setDataHoraMsg(dataHoraMsg);
            this.setTítuloMsg(títuloMsg);
            this.setMensagem(mensagem);
            this.setVisualizada(visualizada);
        }

        public int getCódMsg() {
            return CódMsg;
        }

        public void setCódMsg(int códMsg) {
            CódMsg = códMsg;
        }

        public Image getImgFotoRem() {
            return ImgFotoRem;
        }

        public void setImgFotoRem(Image imgFotoRem) {
            ImgFotoRem = imgFotoRem;
        }

        public String getLoginRem() {
            return LoginRem;
        }

        public void setLoginRem(String loginRem) {
            LoginRem = loginRem;
        }

        public String getDataHoraMsg() {
            return DataHoraMsg;
        }

        public void setDataHoraMsg(String dataHoraMsg) {
            DataHoraMsg = dataHoraMsg;
        }

        public String getTítuloMsg() {
            return TítuloMsg;
        }

        public void setTítuloMsg(String títuloMsg) {
            TítuloMsg = títuloMsg;
        }

        public Boolean getVisualizada() {
            return Visualizada;
        }

        public void setVisualizada(Boolean visualizada) {
            Visualizada = visualizada;
        }

        public String getMensagem() {
            return Mensagem;
        }

        public void setMensagem(String mensagem) {
            Mensagem = mensagem;
        }

        public int getPrioridadeMsg() {
            return PrioridadeMsg;
        }

        public void setPrioridadeMsg(int prioridadeMsg) {
            PrioridadeMsg = prioridadeMsg;
        }

        public int getCódUsuRem() {
            return CódUsuRem;
        }

        public void setCódUsuRem(int códUsuRem) {
            CódUsuRem = códUsuRem;
        }
    }

    public void setNewMessage(UniqueMessageList newMessage) {
        loopGetNewMessages.stop();
        Messages.add(0, newMessage);
        if (newMessage.getCódMsg() > getCódÚltMsg()) {
            setCódÚltMsg(newMessage.getCódMsg());
        }
        if (!newMessage.getVisualizada()) {
            attQtdMsgVisualizadas(true);
            if (newMessage.getPrioridadeMsg() == 1) {
                Platform.runLater(() -> showUnique(newMessage)); //Tem que ser runlater se não causa NPE
            }
        }
        loopGetNewMessages.play();
    }

    private void showUnique(UniqueMessageList Msg) {
        if (!Msg.getVisualizada()) {
            setMsgVisualizada(Msg.getCódMsg());
            Msg.setVisualizada(true);
            ListMessage.refresh();
        }
        Alert Mensagem = new Alert(Alert.AlertType.INFORMATION);
        ImageView ImgRemetente = new ImageView(Functions.getImageUsu(Msg.getCódUsuRem()));
        ImgRemetente.setFitWidth(48);
        ImgRemetente.setFitHeight(48);
        Mensagem.setGraphic(ImgRemetente);
        Mensagem.setTitle("Sistema Sinergia - Leitura de mensagem recebida");
        Stage stageMsg = (Stage) Mensagem.getGraphic().getScene().getWindow();
        stageMsg.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
        Mensagem.setHeaderText(Msg.getTítuloMsg());
        Mensagem.setContentText(Msg.getMensagem());
        Mensagem.show();
    }

    private void setMsgVisualizada(int CódMensagem) {
        try {
            statement = new DBParalelConex("UPDATE TRIMSG\n" +
                    "SET VISUALIZADA = 'S',\n" +
                    "DHVISUALIZADA = SYSDATE\n" +
                    "WHERE CODMSG = ?");
            statement.addParameter(CódMensagem);
            statement.run();
            attQtdMsgVisualizadas(false);
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar marcar mensagem como visualizada\n" + ex, ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar marcar mensagem como visualizada\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            statement.desconecta();
        }
    }

    private void getNewMessages() {
        Platform.runLater(() -> {
            Boolean hasNewMessages = false;
            try {
                statement = new DBParalelConex("SELECT COUNT(1) AS COUNT\n" +
                        "FROM TRIMSG\n" +
                        "WHERE CODUSU = ?\n" +
                        "AND CODMSG > ?", true);
                statement.addParameter(User.getCurrent().getCódUsu());
                statement.addParameter(getCódÚltMsg());
                statement.createSet();
                statement.rs.next();
                if (statement.rs.getInt("COUNT") > 0) {
                    hasNewMessages = true;
                } else {
                    return;
                }
                AppObjects.getAppObjects().getBtnMensagens().setOnAction(evt -> {
                    if (PaneMessages.isVisible()) {
                        PaneMessages.setExpanded(false);
                        PaneMessages.setVisible(false);
                    } else {
                        PaneMessages.setExpanded(true);
                        PaneMessages.setVisible(true);
                    }
                });
            } catch (SQLException ex) {
                loopGetNewMessages.stop();
                if (Functions.HourMinFormater.format(User.getCurrent().getDHLogin()).
                        matches(Functions.HourMinFormater.format(Timestamp.from(Instant.now())))) { //Erro na hora do login
                    ModelException.setNewException(new ModelException(this.getClass(),
                            null,
                            "Erro ao tentar verificar se há novas mensagens\n" + ex,
                            ex));
                    ModelException.getException().raise();
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                            "Devido ao erro ocorrido anteriormente, interrompemos " +
                                    "a busca de mensagens, retomarei a busca em 5 minutos."));
                    ModelDialog.getDialog().raise();
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(300000),
                            ae -> loopGetNewMessages.play()));
                    timeline.play();
                } else {
                    AppObjects.getAppObjects().getBtnMensagens().setText("Erro");
                    AppObjects.getAppObjects().getBtnMensagens().setOnAction(e -> {
                        AppObjects.getAppObjects().getBtnMensagens().setText("Erro");
                        AppObjects.getAppObjects().getBtnMensagens().setOnAction(evt -> {
                            if (PaneMessages.isVisible()) {
                                PaneMessages.setExpanded(false);
                                PaneMessages.setVisible(false);
                            } else {
                                PaneMessages.setExpanded(true);
                                PaneMessages.setVisible(true);
                            }
                            ModelException.setNewException(new ModelException(this.getClass(),
                                    null,
                                    "Erro ao tentar verificar se há novas mensagens\n" + ex,
                                    ex));
                            ModelException.getException().raise();
                            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                                    "Devido ao erro ocorrido anteriormente, interrompemos " +
                                            "a busca de mensagens, retomarei a busca em 5 minutos."));
                            ModelDialog.getDialog().raise();
                            Timeline timeline = new Timeline(new KeyFrame(
                                    Duration.millis(300000),
                                    ae -> loopGetNewMessages.play()));
                            timeline.play();
                        });
                    });
                }
            } catch (Exception ex) {
                loopGetNewMessages.stop();
                if (Functions.HourMinFormater.format(User.getCurrent().getDHLogin()).
                        matches(Functions.HourMinFormater.format(Instant.now()))) { //Erro na hora do login
                    ModelException.setNewException(new ModelException(this.getClass(),
                            null,
                            "Erro ao tentar verificar se há novas mensagens\n" + ex,
                            ex));
                    ModelException.getException().raise();
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                            "Devido ao erro ocorrido anteriormente, interrompemos " +
                                    "a busca de mensagens, retomarei a busca em 5 minutos."));
                    ModelDialog.getDialog().raise();
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(300000),
                            ae -> loopGetNewMessages.play()));
                    timeline.play();
                } else {
                    AppObjects.getAppObjects().getBtnMensagens().setText("Erro");
                    AppObjects.getAppObjects().getBtnMensagens().setOnAction(e -> {
                        AppObjects.getAppObjects().getBtnMensagens().setText("Erro");
                        AppObjects.getAppObjects().getBtnMensagens().setOnAction(evt -> {
                            if (PaneMessages.isVisible()) {
                                PaneMessages.setExpanded(false);
                                PaneMessages.setVisible(false);
                            } else {
                                PaneMessages.setExpanded(true);
                                PaneMessages.setVisible(true);
                            }
                            ModelException.setNewException(new ModelException(this.getClass(),
                                    null,
                                    "Erro ao tentar verificar se há novas mensagens\n" + ex,
                                    ex));
                            ModelException.getException().raise();
                            loopGetNewMessages.stop();
                            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(), null,
                                    "Devido ao erro ocorrido anteriormente, interrompemos " +
                                            "a busca de mensagens, retomarei a busca em 5 minutos."));
                            ModelDialog.getDialog().raise();
                            Timeline timeline = new Timeline(new KeyFrame(
                                    Duration.millis(300000),
                                    ae -> loopGetNewMessages.play()));
                            timeline.play();
                        });
                    });
                }
            } finally {
                statement.desconecta();
            }
            if (hasNewMessages) {
                try {
                    statement = new DBParalelConex("SELECT MSG.CODMSG, MSG.VISUALIZADA, MSG.PRIORIDADE, MSG.TITULO,\n" +
                            " MSG.MENSAGEM, MSG.DHALTER,\n" +
                            "CASE WHEN MSG.CODUSUREM IS NULL THEN -1 ELSE MSG.CODUSUREM END AS CODUSUREM,\n" +
                            "CASE WHEN MSG.CODUSUREM IS NULL THEN 'Desconhecido' ELSE USU.LOGIN END AS LOGINREM\n" +
                            "FROM TRIMSG MSG\n" +
                            "LEFT JOIN TSIUSU USU\n" +
                            "ON MSG.CODUSUREM = USU.CODUSU\n" +
                            "WHERE MSG.CODUSU = ?\n" +
                            "AND MSG.CODMSG > ?\n" +
                            "ORDER BY MSG.CODMSG DESC", true);
                    statement.addParameter(User.getCurrent().getCódUsu());
                    statement.addParameter(getCódÚltMsg());
                    int countRow = statement.countRow();
                    if (countRow == 0) {
                        return;
                    } else {
                        statement.createSet();
                        while (statement.rs.next()) {
                            setNewMessage(new UniqueMessageList(
                                    statement.rs.getInt("CODMSG"),
                                    statement.rs.getInt("PRIORIDADE"),
                                    statement.rs.getInt("CODUSUREM"),
                                    Functions.getImageUsu(statement.rs.getInt("CODUSUREM")),
                                    statement.rs.getString("LOGINREM"),
                                    Functions.DataHoraFormater.format(statement.rs.getTimestamp("DHALTER")),
                                    statement.rs.getString("TITULO"),
                                    statement.rs.getString("MENSAGEM"),
                                    Functions.ToBoo(statement.rs.getString("VISUALIZADA"))));
                        }
                    }
                } catch (SQLException ex) {
                    ModelException.setNewException(new ModelException(this.getClass(), null,
                            "Erro ao tentar buscar mensagens do usuário\n" + ex, ex));
                    ModelException.getException().raise();
                } catch (Exception ex) {
                    ModelException.setNewException(new ModelException(this.getClass(), null,
                            "Erro ao tentar buscar mensagens do usuário\n" + ex, ex));
                    ModelException.getException().raise();
                } finally {
                    statement.desconecta();
                }
            }
        });
    }

    private void attQtdMsgVisualizadas(Boolean Adding) {
        if (Adding) {
            setQtdMsgNãoVisualizadas(getQtdMsgNãoVisualizadas() + 1);
        } else {
            setQtdMsgNãoVisualizadas(getQtdMsgNãoVisualizadas() - 1);
        }
        if (getQtdMsgNãoVisualizadas() == 0) {
            AppObjects.getAppObjects().getStageMain().setTitle("Sistema Sinergia ( " + User.getCurrent().getCódUsu() + " - " + User.getCurrent().getLoginUsu() + " )");
            AppObjects.getAppObjects().getBtnMensagens().setText(null);
        } else {
            AppObjects.getAppObjects().getStageMain().setTitle("[ " + getQtdMsgNãoVisualizadas() + " ] Sistema Sinergia ( " + User.getCurrent().getCódUsu() + " - " + User.getCurrent().getLoginUsu() + " )");
            AppObjects.getAppObjects().getBtnMensagens().setText("" + getQtdMsgNãoVisualizadas());
        }
    }
}
