package br.com.sinergia.functions.extendeds.tableProperties;

import br.com.sinergia.controllers.dialog.ModelException;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class TextFieldTbCell<S, T> extends TableCell {

    private TextField TxtFieldCell = new TextField();
    private Text TxtField = new Text();

    public TextFieldTbCell(Boolean isEditable, Runnable exec) {
        this.setEditable(isEditable);
        this.TxtFieldCell.setEditable(isEditable);
        this.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) showTextField(true);
        });
        this.TxtFieldCell.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV) showTextField(false);
        });
        this.TxtFieldCell.setOnAction(e -> {
            commitEdit(TxtFieldCell.getText());
            exec.run();
        });
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        try {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }
            this.TxtFieldCell.setText((String) item);
            this.TxtField.setText((String) item);
            showTextField(false);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar atualizar valores da tabela\n" +
                    "Favor verifique, operação cancelada para coluna: " + this.getTableColumn().getText()));
            ModelException.getException().raise();
        }
    }

    private void showTextField(Boolean show) {
        if (show) {
            this.setGraphic(TxtFieldCell);
            TxtFieldCell.requestFocus();
        } else {
            this.setGraphic(TxtField);
        }
    }
}
