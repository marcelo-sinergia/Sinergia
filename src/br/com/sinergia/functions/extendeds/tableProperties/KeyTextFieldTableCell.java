package br.com.sinergia.functions.extendeds.tableProperties;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.cell.TextFieldTableCell;

public class KeyTextFieldTableCell<S, T> extends TextFieldTableCell<S, T> {

    public KeyTextFieldTableCell(String Query) {
        Button BtnKey = new Button("Xix");
        BtnKey.setOnAction(e -> System.out.println("BtnKey fired"));
        BtnKey.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        this.getChildren().add(BtnKey);
    }

}
