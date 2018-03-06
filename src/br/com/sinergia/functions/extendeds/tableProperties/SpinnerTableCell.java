package br.com.sinergia.functions.extendeds.tableProperties;

import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;
import javafx.scene.text.Text;

public class SpinnerTableCell<S, T> extends TableCell<S, T> {

    private Spinner<T> spinnerCell;
    private Text textCell = new Text();
    private Boolean isAtualizando = false;

    public SpinnerTableCell(Boolean isEditable, Runnable exec) {
        loadProperty(isEditable, 0, 0, exec);
    }

    public SpinnerTableCell(Boolean isEditable, int MinValue, int MaxValue, Runnable exec) {
        loadProperty(isEditable, MinValue, MaxValue, exec);
    }

    private void loadProperty(Boolean isEditable, int MinValue, int MaxValue, Runnable exec) {
        if (MaxValue <= 0) {
            this.spinnerCell = new Spinner<T>();
        } else {
            this.spinnerCell = new Spinner<T>(0, MinValue, MaxValue);
        }
        this.spinnerCell.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!isAtualizando) {
                commitEdit(newValue);
                exec.run();
            }
        });
        this.setEditable(isEditable);
        this.spinnerCell.setEditable(isEditable);
        this.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) showSpinnerInCell(true);
        });
        this.spinnerCell.focusedProperty().addListener((obs, oldV, newV) -> {
            if (oldV) showSpinnerInCell(false);
        });
    }

    @Override
    protected void updateItem(T c, boolean empty) {
        isAtualizando = true;
        super.updateItem(c, empty);
        if (empty || c == null) {
            setText(null);
            setGraphic(null);
            return;
        }
        this.spinnerCell.getValueFactory().setValue(c);
        this.textCell.setText(c.toString());
        this.showSpinnerInCell(false);
        isAtualizando = false;
    }

    private void showSpinnerInCell(Boolean show) {
        if (show) {
            this.setGraphic(spinnerCell);
            this.spinnerCell.requestFocus();
        } else {
            this.setGraphic(textCell);
        }
    }
}