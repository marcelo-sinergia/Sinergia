package br.com.sinergia.functions.modules;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.Statement;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.models.intern.User;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.Optional;

public class CtrlSenhaUsu {

    private static CtrlSenhaUsu ctrlSenhaUsu = new CtrlSenhaUsu();
    Statement statement;

    private int CódUsuToChange;
    private Boolean noError = true;
    private String SenhaDefault;
    private String ConfirmacaoDefault;


    public static CtrlSenhaUsu getCtrlSenhaUsu() {
        return ctrlSenhaUsu;
    }

    public static void setCtrlSenhaUsu(CtrlSenhaUsu ctrlSenhaUsu) {
        CtrlSenhaUsu.ctrlSenhaUsu = ctrlSenhaUsu;
    }

    public void createDialogSenha(int CódUsuToChange) {
        setCódUsuToChange(CódUsuToChange);
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Alterar senha do usuário");
        dialog.setHeaderText("Digite aqui a nova senha e confirmação");
        // Set the icon (must be included in the project).
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
        dialog.setGraphic(new ImageView(this.getClass().getResource("/br/com/sinergia/properties/images/Icone_Senha.png").toString()));
        // Set the button types.
        ButtonType loginButtonType = new ButtonType("Alterar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        PasswordField TxtSenha = new PasswordField();
        TxtSenha.setText(getSenhaDefault());
        TxtSenha.setPromptText("Nova Senha");
        PasswordField TxtConfirma = new PasswordField();
        TxtConfirma.setText(getConfirmacaoDefault());
        TxtConfirma.setPromptText("Confirmação");
        grid.add(new Label("Nova Senha:"), 0, 0);
        grid.add(TxtSenha, 1, 0);
        grid.add(new Label("Confirmação:"), 0, 1);
        grid.add(TxtConfirma, 1, 1);
        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);
        // Do some validation (using the Java 8 lambda syntax).
        TxtSenha.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        // Request focus on the username field by default.
        Platform.runLater(() -> TxtSenha.requestFocus());
        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(TxtSenha.getText(), TxtConfirma.getText());
            }
            return null;
        });
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(usernamePassword -> {
            if(Functions.Nvl(TxtSenha.getText()).equals("")){
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Nova senha não pode ser vazia"));
                ModelDialog.getDialog().raise();
                setSenhaDefault(TxtSenha.getText());
                setConfirmacaoDefault(TxtConfirma.getText());
                createDialogSenha(getCódUsuToChange());
            }else if(TxtSenha.getText().equals(TxtConfirma.getText())) {
                setNewSenha(TxtSenha.getText());
                if(noError) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.INFORMATION, this.getClass(),
                            null,
                            "Senha alterada com sucesso!"));
                    ModelDialog.getDialog().raise();
                }
            } else {
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(),
                        null,
                        "Nova senha e confirmação de senha divergentes"));
                ModelDialog.getDialog().raise();
                setSenhaDefault(TxtSenha.getText());
                setConfirmacaoDefault(TxtConfirma.getText());
                createDialogSenha(getCódUsuToChange());
            }
        });
    }

    public void changeUserSenha(int CódUsuToChange, String newPassword) {
        setCódUsuToChange(CódUsuToChange);
        setNewSenha(newPassword);
    }

    private void setNewSenha(String Password) {
        noError = true;
        try {
            statement = new Statement("UPDATE TSIUSU\n" +
                    "SET SENHA = MD5(?)\n" +
                    "WHERE CODUSU = ?");
            statement.addParameter(Password);
            statement.addParameter(getCódUsuToChange());
            statement.run();
            statement = new Statement("SELECT MD5(?) AS CRYPT FROM DUAL");
            statement.addParameter(Password);
            statement.createSet();
            statement.rs.next();
            User.getCurrent().setCryptSenha(statement.rs.getString("CRYPT"));
            User.getCurrent().setSenhaUsu(Password);
        } catch (SQLException ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar alterar senha do usuário\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar alterar senha do usuário\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            statement.end();
        }
    }

    private int getCódUsuToChange() {
        return CódUsuToChange;
    }

    private void setCódUsuToChange(int códUsuToChange) {
        CódUsuToChange = códUsuToChange;
    }

    public String getSenhaDefault() {
        return SenhaDefault;
    }

    public void setSenhaDefault(String senhaDefault) {
        SenhaDefault = senhaDefault;
    }

    public String getConfirmacaoDefault() {
        return ConfirmacaoDefault;
    }

    public void setConfirmacaoDefault(String confirmacaoDefault) {
        ConfirmacaoDefault = confirmacaoDefault;
    }
}
