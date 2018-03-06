package br.com.sinergia.functions.extendeds;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.modules.CtrlArquivos;
import br.com.sinergia.models.intern.AppInfo;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public final class ButtonTela extends Button {

    DBParalelConex conex;
    CtrlArquivos ctrlArquivos = new CtrlArquivos();

    public ButtonTela(String Tela) {
        this.setText(Tela);
        this.setOnAction(e -> {
            CallTela(Tela);
        });
        ControlaListTelasFav(2, Tela);
        this.setOnMousePressed((MouseEvent event) -> {
            if ((event.getButton() == event.getButton().SECONDARY)) {
                ControlaListTelasFav(0, Tela);
            }
        });
        loadVBRecentes(true);
    }

    public ButtonTela(String Tela, Boolean BtnSearch) { //Botão de pesquisa, não pode atualizar ListaFavoritos
        this.setText(Tela);
        this.setOnAction(e -> {
            CallTela(Tela);
        });
        this.setOnMousePressed((MouseEvent event) -> {
            if ((event.getButton() == event.getButton().SECONDARY)) {
                ControlaListTelasFav(0, Tela);
            }
        });
        loadVBRecentes(true);
    }

    private void CallTela(String Tela) {
        if (AppObjects.getAppObjects().getTelasAbertas().contains(Tela)) {
            //Já está aberto, seleciona o Pane escolhido
            AppObjects.getAppObjects().getAbaPane().getSelectionModel().select(AppObjects.getAppObjects().getTelasAbertas().indexOf(Tela) + 1);
        } else {
            try { //Adiciona o Pane na Aba de Pane's
                conex = new DBParalelConex("SELECT CAMINHO, LOCALIZADOR\n"
                        + "FROM TSITEL\n"
                        + "WHERE TELA = ?");
                conex.addParameter(Tela);
                conex.createSet();
                conex.rs.next();
                String Localizador = conex.rs.getString("LOCALIZADOR");
                Tooltip Caminho = new Tooltip(conex.rs.getString("CAMINHO"));
                try {
                    if (Localizador == null) {
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.ERROR, this.getClass(), null, "Erro ao tentar encontrar localizador para tela: '" + Tela + "'\n"
                                + "Favor contate o suporte imediatamente.\n"
                                + "A aplicação pode apresentar falhas"));
                        ModelDialog.getDialog().raise();
                    } else {
                        lançaRegistro(Tela);
                        Tab NovaTela = new Tab(Tela);
                        NovaTela.setTooltip(Caminho);
                        NovaTela.setOnCloseRequest(e -> { //Adiciona o CloseEvent na nova Tab
                            AppObjects.getAppObjects().getTelasAbertas().remove(Tela);
                        });
                        FXMLLoader FXMLloader = new FXMLLoader(getClass().getResource(Localizador));
                        Parent Aba = FXMLloader.load();
                        NovaTela.setContent(Aba);
                        AppObjects.getAppObjects().getAbaPane().getTabs().add(NovaTela);
                        AppObjects.getAppObjects().getTelasAbertas().add(Tela);
                        AppObjects.getAppObjects().getAbaPane().getSelectionModel().select(AppObjects.getAppObjects().getTelasAbertas().indexOf(Tela) + 1);
                    }
                } catch (Exception ex) {
                    ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar frame da tela: '" + Tela + "'\n"
                            + ex.getMessage(), ex));
                    ModelException.getException().raise();
                }
            } catch (SQLException ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar frame da tela: '" + Tela + "'\n"
                        + ex.getMessage(), ex));
                ModelException.getException().raise();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar buscar frame da tela: '" + Tela + "'\n"
                        + ex.getMessage(), ex));
                ModelException.getException().raise();
            } finally {
                conex.desconecta();
            }
        }
    }

    private void ControlaListTelasFav(int Controle, String Tela) {
        String ListaRetorno = AppInfo.getInfo().getArqTelasFav();
        if (ListaRetorno == "-1") {
            return; //Deu erro ao tentar buscar o arquivo, então sai fora
        } else if (ListaRetorno == "0") {
            ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Lista de Telas(Favoritas)", " ");
        }
        try {
            switch (Controle) {
                case -1: //Remover, independente
                    if (AppObjects.getAppObjects().getVBFavoritas().getChildren().toString().contains("'" + Tela + "'")) {
                        if (ListaRetorno.contains("|" + Tela)) {
                            String NovaLista = ListaRetorno.replace("||" + Tela, "");
                            ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Lista de Telas(Favoritas)", NovaLista);
                            AppInfo.getInfo().setArqTelasFav(NovaLista);
                        }
                        int IdxTelaRemove = AppObjects.getAppObjects().getListTelas()[0].indexOf(Tela);
                        AppObjects.getAppObjects().getListTelas()[0].remove(IdxTelaRemove);
                        AppObjects.getAppObjects().getVBFavoritas().getChildren().remove(IdxTelaRemove);
                        AppObjects.getAppObjects().setMensagem("Tela: '" + Tela + "' removida com sucesso!");
                    } else {
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null, "O método ControlaListTelasFav(-1) foi invocado para a tela : '" + Tela + "'\n"
                                + "Porém esta tela não estava na Lista de Telas Favoritas!"));
                        ModelDialog.getDialog().raise();
                    }
                    break;
                case 0: //Verifica se tem ou se não tem, e adiciona ou remove
                    if (ListaRetorno.contains("||" + Tela)) {
                        ControlaListTelasFav(-1, Tela);
                    } else {
                        ControlaListTelasFav(1, Tela);
                    }
                    break;
                case 1: //Adicionar, independente
                    if (!AppObjects.getAppObjects().getVBFavoritas().getChildren().toString().contains("'" + Tela + "'")) {
                        AppObjects.getAppObjects().getListTelas()[0].add(Tela);
                        if (!ListaRetorno.contains("||" + Tela)) {
                            String NovaLista = ListaRetorno + "||" + Tela;
                            ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Lista de Telas(Favoritas)", NovaLista);
                            AppInfo.getInfo().setArqTelasFav(NovaLista);
                        }
                        Button NewButton = new Button(Tela);
                        NewButton.setOnAction(e -> CallTela(Tela));
                        NewButton.setOnMousePressed((MouseEvent event) -> {
                            if ((event.getButton() == event.getButton().SECONDARY)) {
                                ControlaListTelasFav(0, Tela);
                            }
                        });
                        NewButton.setStyle("-fx-border-color: #696969");
                        NewButton.prefWidthProperty().bind(AppObjects.getAppObjects().getVBFavoritas().prefWidthProperty().subtract(25));
                        NewButton.prefHeightProperty().bind(AppObjects.getAppObjects().getVBFavoritas().prefHeightProperty().divide(11));
                        AppObjects.getAppObjects().getVBFavoritas().getChildren().add(NewButton);
                        AppObjects.getAppObjects().setMensagem("Tela: '" + Tela + "' adicionada com sucesso!");
                    } else {
                        ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null, "O método ControlaListTelasFav(1) foi invocado para a tela : '" + Tela + "'\n" +
                                "Porém esta tela já estava na Lista de Telas Favoritas!"));
                        ModelDialog.getDialog().raise();
                    }
                    break;
                case 2: //Load inicial, verifica se existe
                    if (ListaRetorno.contains("|" + Tela)) {
                        ControlaListTelasFav(1, Tela);
                    }
                    break;
                default:
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null, "Erro interno no método: ButtonTela.ControlaListTelasFav()\n" +
                            "Foi solicitado um Controle diferente do esperado (-1, 0, 1, 2)"));
                    ModelDialog.getDialog().raise();
                    break;
            }
        } catch (Exception ex) {
            ModelException Error = new ModelException(this.getClass(), null, "Erro interno no método: ButtonTela.ControlaListTelasFav()\n"
                    + ex.getMessage(), ex);
            Error.raise();
        }
    }

    private void lançaRegistro(String Tela) {
        try {
            conex = new DBParalelConex("INSERT INTO TSIREG\n" +
                    "(CODREG, CODUSU, CODSESSAO, DHACESSO, TELA)\n" +
                    "VALUES\n" +
                    "(GET_CODREG(?), ?, ?, ?, ?)");
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.addParameter(User.getCurrent().getCódSessão());
            conex.addParameter(Timestamp.from(Instant.now()));
            conex.addParameter(Tela);
            conex.run();
            loadVBRecentes(false);
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar gravar registro para tela: '" + Tela + "'\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar gravar registro para tela: '" + Tela + "'\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

    private void loadVBRecentes(Boolean isLoadInit) {
        if (isLoadInit) {
            if (AppObjects.getAppObjects().getVBRecentes().getChildren().size() > 0) {
                return;
            }
        }
        try {
            conex = new DBParalelConex("SELECT TELA\n" +
                    "FROM (SELECT MAX(CODREG), TELA\n" +
                    "FROM TSIREG\n" +
                    "WHERE CODUSU = ?\n" +
                    "GROUP BY TELA\n" +
                    "ORDER BY 1 DESC)\n" +
                    "WHERE ROWNUM <= 10");
            conex.addParameter(User.getCurrent().getCódUsu());
            conex.createSet();
            AppObjects.getAppObjects().getVBRecentes().getChildren().remove(0, AppObjects.getAppObjects().getVBRecentes().getChildren().size());
            while (conex.rs.next()) {
                String TelaRec = conex.rs.getString("TELA");
                Button NewButton = new Button(TelaRec);
                NewButton.setOnAction(e -> CallTela(TelaRec));
                NewButton.setOnMousePressed((MouseEvent event) -> {
                    if ((event.getButton() == event.getButton().SECONDARY)) {
                        ControlaListTelasFav(0, TelaRec);
                    }
                });
                NewButton.setStyle("-fx-border-color: #696969");
                NewButton.prefWidthProperty().bind(AppObjects.getAppObjects().getVBRecentes().prefWidthProperty().subtract(25));
                NewButton.prefHeightProperty().bind(AppObjects.getAppObjects().getVBRecentes().prefHeightProperty().divide(11));
                AppObjects.getAppObjects().getVBRecentes().getChildren().add(NewButton);
            }
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar recarregar lista de telas recentes\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar recarregar lista de telas recentes\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
        }
    }

}
