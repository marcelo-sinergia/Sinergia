package br.com.sinergia.controllers.fxml;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.ConexaoBD;
import br.com.sinergia.database.conect.GetDataDB;
import br.com.sinergia.database.conect.Statement;
import br.com.sinergia.functions.modules.CtrlArquivos;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppInfo;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.properties.metods.ReadRegedit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    final String KeyPath = "HKLM\\Software\\Sinergia\\DBAlias\\Produção\\";
    CtrlArquivos ctrlArquivos = new CtrlArquivos();
    Statement statement;
    int tentativa = 0;
    Boolean noError = true, isCapsLocked = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
    String versãoExec = "1.0.0";

    @FXML
    private AnchorPane PaneLogin;
    @FXML
    private TextField TxtLogin, TxtSenha;
    @FXML
    private Button BtnAcessar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        propriedades();
        registraInfo();
    }

    /*
    |----------------------------------------------------------------------------------------------------------|
    | O JavaFX não está conseguindo pegar o Locking state do CapsLock nativamente, temos que aplicar contornos.|
    |----------------------------------------------------------------------------------------------------------|
     */

    private void propriedades() {
        MaskField.MaxUpperCharField(TxtLogin, 20);
        MaskField.MaxCharField(TxtSenha, 20);
        TxtLogin.setOnAction((ActionEvent evt) -> {
            autenticarLogin();
        });
        TxtSenha.setOnAction((ActionEvent evt) -> {
            autenticarLogin();
        });
        BtnAcessar.setOnAction((ActionEvent evt) -> {
            autenticarLogin();
        });
        PaneLogin.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.CAPS) {
                isCapsLocked = !isCapsLocked;
                getCaseSensitive(isCapsLocked, TxtSenha.isFocused());
            }
        });
        TxtSenha.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                Toolkit.getDefaultToolkit().setLockingKeyState(KeyEvent.VK_CAPS_LOCK, isCapsLocked);
                getCaseSensitive(isCapsLocked, true);
            } else {
                getCaseSensitive(isCapsLocked, false);
            }
        });
    }

    private void registraInfo() {
        try {
            AppInfo.getInfo().setIPMáquina(InetAddress.getLocalHost().getHostAddress());
            AppInfo.getInfo().setNomeMáquina(InetAddress.getLocalHost().getHostName());
            AppInfo.getInfo().setVersãoExec(versãoExec);
        } catch (Exception ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar registrar informações da máquina\n" + ex.getMessage() + "\nA aplicação será finalizada",
                    ex));
            ModelException.getException().raise();
            System.exit(0);
        }
    }

    private void autenticarLogin() {
        noError = true;
        catchInfoLogin();
        if (noError) {
            if (tentativa < 4) {
                tentativa = tentativa + 1;
            }
            if (tentativa == 4) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Tentativa de login excedida. Máquina bloqueada."));
                ModelDialog.getDialog().raise();
                return;
            } else if (User.getCurrent().getAtivo() == false) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Usuário não está ativo, não pode acessar o sistema"));
                ModelDialog.getDialog().raise();
                return;
            } else if (!User.getCurrent().getSenhaUsu().equals(User.getCurrent().getCryptSenha())) {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Senha digitada inválida"));
                ModelDialog.getDialog().raise();
                return;
            } else {
                try {
                    RegistraSessão();
                    if (noError) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/br/com/sinergia/views/Principal.fxml"));
                        Parent root1 = (Parent) fxmlLoader.load();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root1));
                        stage.setTitle("Sistema Sinergia ( " + User.getCurrent().getCódUsu() + " - " + User.getCurrent().getLoginUsu() + " )");
                        stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
                        stage.setMaximized(true);
                        stage.show();
                        stage.setOnCloseRequest(e -> {
                            ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(),
                                    null,
                                    "Deseja realmente sair do sistema?"));
                            ButtonType[] Btns = new ButtonType[2];
                            Btns[0] = new ButtonType("Sim");
                            Btns[1] = new ButtonType("Não");
                            ModelDialogButton.getDialogButton().createButton(Btns);
                            if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Não sair
                                e.consume();
                            } else {
                                if (AppObjects.getAppObjects().getAbaPane().getTabs().size() > 1) { //Sair
                                    String arqFecha = ctrlArquivos.Busca(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes");
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
                                                ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes", "S");
                                            }
                                            User.getCurrent().closeSessão();
                                        } else {
                                            if (Ckb.isSelected()) {
                                                ctrlArquivos.Registra(User.getCurrent().getCódUsu(), "Finalizar com telas pendentes", "N");
                                            }
                                            e.consume();
                                        }
                                    } else {
                                        if (arqFecha.equals("S")) {
                                            //Apenas fecha
                                            User.getCurrent().closeSessão();
                                        } else if (arqFecha.equals("N")) {
                                            ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING,
                                                    this.getClass(),
                                                    null,
                                                    "Existem telas ainda abertas, finalize-as primeiro"));
                                            ModelDialog.getDialog().raise();
                                            e.consume();
                                        } else {
                                            System.err.println("arqFecha not programmed: " + arqFecha);
                                        }
                                    }
                                } else {
                                    User.getCurrent().closeSessão();
                                }
                            }
                        });
                        Stage stage_old = (Stage) TxtLogin.getScene().getWindow();
                        stage_old.close();
                    }
                } catch (Exception ex) {
                    noError = false;
                    ModelException Error = new ModelException(this.getClass(), null, "Erro ao tentar inicializar tela principal\n"
                            + ex.getMessage(), ex);
                    Error.raise();
                }
            }
        }
    }

    private void catchInfoLogin() {
        try {
            if (TxtLogin.getText() == null || TxtLogin.getText().equals("")) {
                noError = false;
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Login não pode ser vazio"));
                ModelDialog.getDialog().raise();
                return;
            } else if (TxtSenha.getText() == null || TxtSenha.getText().equals("")) {
                noError = false;
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Senha não pode ser vazia"));
                ModelDialog.getDialog().raise();
                return;
            }
            tryConectDB();
            if (noError) {
                ConexaoBD.getInstancia().setIPMaq(ReadRegedit.readRegistry(KeyPath, "IP"));
                ConexaoBD.getInstancia().setPorta(ReadRegedit.readRegistry(KeyPath, "Port"));
                ConexaoBD.getInstancia().setUsuario(ReadRegedit.readRegistry(KeyPath, "User"));
                String CryptDBPassword = ReadRegedit.readRegistry(KeyPath, "Password");
                ConexaoBD.getInstancia().setSenha(uncryptPass(CryptDBPassword));
                statement = new Statement("SELECT COUNT(1) FROM TSIUSU WHERE LOGIN = ?");
                statement.addParameter(TxtLogin.getText());
                statement.createSet();
                statement.rs.next();
                Boolean UserExists = Functions.ToBoo(statement.rs.getInt(1));
                if (!UserExists) {
                    noError = false;
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                            null,
                            "Usuário não encontrado para login digitado"));
                    ModelDialog.getDialog().raise();
                    return;
                } //Se continuou, é porque o usuário existe.
                statement = new Statement("SELECT USU.ATIVO, USU.CODUSU, USU.NOME, USU.LOGIN, USU.FOTO, USU.PERFIL, USU.SENHA, MD5(?) AS CRYPT,\n" +
                        "GET_TSIPAR_T(?, ?) AS VERSAOATUALDB, EMP.CODEMP, EMP.RAZAOSOCIAL, EMP.NOMEFANTASIA, EMP.CNPJ " +
                        "FROM TSIUSU USU\n" +
                        "INNER JOIN TSIEMP EMP\n" +
                        "ON (USU.CODEMP = EMP.CODEMP)" +
                        "WHERE USU.LOGIN = ?");
                statement.addParameter(TxtSenha.getText(), "(Criptografado)");
                statement.addParameter("VERSAOATUALDB");
                statement.addParameter(0);
                statement.addParameter(TxtLogin.getText());
                statement.createSet();
                statement.rs.next();
                AppInfo.getInfo().setVersãoBD(statement.rs.getString("VERSAOATUALDB"));
                Image ImgUsu;
                if (statement.rs.getBytes("FOTO") != null) {
                    InputStream input = new ByteArrayInputStream(statement.rs.getBytes("FOTO"));
                    ImgUsu = new Image(input);
                } else {
                    ImgUsu = new Image("/br/com/sinergia/properties/images/default.png");
                }
                User.setCurrent(new User(statement.rs.getInt("CODUSU"),
                        Functions.ToBoo(statement.rs.getString("ATIVO")),
                        TxtLogin.getText(),
                        statement.rs.getString("NOME"),
                        ImgUsu,
                        statement.rs.getInt("PERFIL"),
                        statement.rs.getString("SENHA"),
                        statement.rs.getString("CRYPT"),
                        statement.rs.getInt("CODEMP"),
                        statement.rs.getString("NOMEFANTASIA"),
                        statement.rs.getString("RAZAOSOCIAL"),
                        statement.rs.getString("CNPJ")));
            }
        } catch (SQLException ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar autenticar login do usuário\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar autenticar login do usuário\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            if (noError) {
                statement.end();
            }
        }
    }

    private String uncryptPass(String StringByted) {
        ArrayList<String> ArrayCryptPass = new ArrayList<String>(Arrays.asList(StringByted.split("-")));
        String DBPassword = "";
        for (String CryptPass : ArrayCryptPass) {
            CryptPass = CryptPass.trim();
            char UnByted = (char) Integer.parseInt(CryptPass, 2);
            DBPassword = DBPassword + UnByted;
        }
        return DBPassword;
    }

    private void tryConectDB() {
        if (noError) {
            try {
                GetDataDB tryDataDB = new GetDataDB(ReadRegedit.readRegistry(KeyPath, "IP"),
                        ReadRegedit.readRegistry(KeyPath, "Port"),
                        ReadRegedit.readRegistry(KeyPath, "User"),
                        uncryptPass(ReadRegedit.readRegistry(KeyPath, "Password")));
                tryDataDB.conectionIsValid();
            } catch (SQLException ex) {
                noError = false;
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Erro ao tentar estabelecer conexão com banco de dados\n" + ex + "\n" +
                                "Caminho: jdbc:oracle:thin:@" + ReadRegedit.readRegistry(KeyPath, "IP") + ":" + ReadRegedit.readRegistry(KeyPath, "Port") + "\n" +
                                "Usuário: " + ReadRegedit.readRegistry(KeyPath, "User") + "\n" +
                                "Senha: (Criptografado)",
                        ex));
                ModelException.getException().raise();
            }
        }
    }

    private void RegistraSessão() {
        try {
            statement = new Statement("SELECT GET_CODSESSAO(?) AS SESSAO FROM DUAL");
            statement.addParameter(User.getCurrent().getCódUsu());
            statement.createSet();
            statement.rs.next();
            User.getCurrent().setCódSessão(statement.rs.getInt("SESSAO"));
            statement = new Statement("INSERT INTO TSISES\n"
                    + "(CODSESSAO, CODUSU, DHLOGIN, IPMAQ, NOMEMAQ, VERSAOEXEC)\n"
                    + "VALUES\n"
                    + "(?, ?, ?, ?, ?, ?)");
            statement.addParameter(User.getCurrent().getCódSessão());
            statement.addParameter(User.getCurrent().getCódUsu());
            statement.addParameter(java.sql.Timestamp.from(java.time.Instant.now()));
            statement.addParameter(AppInfo.getInfo().getIPMáquina());
            statement.addParameter(AppInfo.getInfo().getNomeMáquina());
            statement.addParameter(AppInfo.getInfo().getVersãoExec());
            statement.run();
        } catch (SQLException ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar registrar sessão\n" + ex.getMessage() + "\nA aplicação será finalizada.", ex));
            ModelException.getException().raise();
            System.exit(0);
        } catch (Exception ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar registrar sessão\n" + ex.getMessage() + "\nA aplicação será finalizada.", ex));
            ModelException.getException().raise();
            System.exit(0);
        }
    }

    private void getCaseSensitive(Boolean isCapsOn, Boolean isFocused) {
        if (isFocused && isCapsOn) {
            TxtSenha.setStyle("-fx-border-color: OrangeRed;");
            TxtSenha.setTooltip(new Tooltip("CapsLock Ativado"));
        } else {
            TxtSenha.setStyle("-fx-border-color: null;");
            TxtSenha.setTooltip(null);
        }
    }

}