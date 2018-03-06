package br.com.sinergia.functions.natives;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.models.usage.ModelAcesso;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class FieldValidAcess {

    static Boolean isAtualizando = false;

    public static void ValidChange(TextField Txt, ControllerStatus Status, ModelAcesso Acesso, Runnable runnable) {
        Txt.textProperty().addListener((obs, oldV, newV) -> {
            if (Status.getStatus() == ControllerStatus.Editando) { //Se já está validado, não tem porque validar novamente
                if (Acesso.getAltera() == false && !isAtualizando) {
                    showMessage(0);
                    Txt.setText(oldV);
                } else if (Acesso.getAltera() == true && !isAtualizando) {
                    runnable.run();
                }
            }
        });
    }

    public static void showMessage(int resp) {
        switch (resp) {
            case 0:
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, null, null, "Usuário sem permissão para alterar"));
                ModelDialog.getDialog().raise();
                break;
            default:
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, null, null, "Usuári121212o sem permissão para alterar"));
                ModelDialog.getDialog().raise();
                break;
        }
    }
}
