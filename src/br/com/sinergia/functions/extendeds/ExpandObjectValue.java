package br.com.sinergia.functions.extendeds;

import br.com.sinergia.controllers.dialog.ModelDialogButton;
import br.com.sinergia.functions.modules.CtrlAtalhos;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.functions.natives.MaskField;
import br.com.sinergia.models.intern.AppObjects;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ExpandObjectValue extends Dialog {

    private Stage stage = new Stage();
    private TextArea textAreaKey;
    private TextArea textAreaValue = new TextArea();

    public ExpandObjectValue(String title, TextArea textArea, Integer maxLength) {
        setTextAreaKey(textArea);
        expandTextAreaValue(title, maxLength);
    }

    private void expandTextAreaValue(String title, Integer maxLength) {
        Platform.runLater(() -> { //Performance
            StackPane stackPane = new StackPane();
            Scene scene = new Scene(stackPane, 650, 550);
            MaskField.CharField(textAreaValue, maxLength);
            textAreaValue.prefWidthProperty().bind(scene.widthProperty().subtract(20));
            textAreaValue.prefHeightProperty().bind(scene.heightProperty().subtract(20));
            ContextMenu contextMenu = new ContextMenu();
            CheckMenuItem  menuItem1 = new CheckMenuItem("Quebra de linhas");
            menuItem1.setAccelerator(KeyCombination.keyCombination("Ctrl+L"));
            contextMenu.getItems().add(menuItem1);
            menuItem1.selectedProperty().addListener((obs, oldV, newV) ->{
                textAreaValue.setWrapText(newV);
            });
            textAreaValue.setContextMenu(contextMenu);
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(AppObjects.getAppObjects().getStageMain());
            stage.setTitle("Modo Edição: " + title);
            stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Editar.png"));
            Button btnConfEdit = new Button("Salvar");
            btnConfEdit.setOnAction(e -> {
                getTextAreaKey().setText(textAreaValue.getText());
                stage.close();
            });
            Button btnCancEdit = new Button("Cancelar");
            btnCancEdit.setOnAction(e -> {
                if (!Functions.Nvl(textAreaKey.getText()).equals(Functions.Nvl(textAreaValue.getText()))) {
                    ModelDialogButton.setDialogButton(new ModelDialogButton(this.getClass(), Alert.AlertType.WARNING, null,
                            "Deseja realmente cancelar as alterações?\nSe cancelar, as alterações serão perdidas."));
                    ButtonType[] Btns = new ButtonType[2];
                    Btns[0] = new ButtonType("Retornar p/ edição");
                    Btns[1] = new ButtonType("Cancelar alterações");
                    ModelDialogButton.getDialogButton().createButton(Btns);
                    if (ModelDialogButton.getDialogButton().returnChoosed() == Btns[1]) { //Cancelar
                        stage.close();
                    }
                } else {
                    stage.close();
                }
            });
            HBox hBox = new HBox(btnConfEdit, btnCancEdit);
            hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));
            hBox.setSpacing(7);
            hBox.setStyle("-fx-border-color: LightGray; -fx-background-color: Gainsboro;");
            VBox vBox = new VBox(textAreaValue, hBox);
            stackPane.getChildren().add(vBox);
            CtrlAtalhos.getAtalhos().setNew(scene, "ESC", () -> btnCancEdit.fire());
            stage.setOnCloseRequest(e -> btnCancEdit.fire());
        });
    }

    public void showFrame() {
        textAreaValue.setText(getTextAreaKey().getText());
        stage.show();
    }

    public TextArea getTextAreaKey() {
        return textAreaKey;
    }

    public void setTextAreaKey(TextArea textAreaKey) {
        this.textAreaKey = textAreaKey;
    }
}
