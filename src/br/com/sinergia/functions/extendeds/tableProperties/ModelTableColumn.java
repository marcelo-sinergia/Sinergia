package br.com.sinergia.functions.extendeds.tableProperties;

import br.com.sinergia.functions.natives.Functions;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ModelTableColumn<S, T> extends TableColumn {

    private String Query;

    public ModelTableColumn(String colTitle, String colProperty) {
        this.setStyle("-fx-alignment: CENTER; -fx-border-color: #F8F8FF;");
        this.setText(colTitle);
        this.setCellValueFactory(new PropertyValueFactory<>(colProperty));
    }

    public TableColumn<S, T> createCheckBoxCell(Boolean isEditable) {
        this.setEditable(isEditable);
        this.setCellFactory(column -> new CheckBoxTableCell<S, T>());
        return this;
    }

    public TableColumn<S, T> createLogicoCell(Boolean isEditable) {
        this.setEditable(isEditable);
        this.setCellFactory(ComboBoxTableCell.forTableColumn(Functions.getLogicoBox()));
        return this;
    }

    public TableColumn<S, T> createSpinnerCell(Boolean isEditable, Runnable exec) {
        return createSpinnerCell(isEditable, -1, -1, exec);
    }

    public TableColumn<S, T> createSpinnerCell(Boolean isEditable, int MinValue, int MaxValue, Runnable exec) {
        this.setEditable(isEditable);
        if (MaxValue < MinValue || MaxValue == -1 || MaxValue <= 0) {
            this.setCellFactory(column -> new SpinnerTableCell<S, T>(isEditable, exec));
        } else {
            this.setCellFactory(column -> new SpinnerTableCell<S, T>(isEditable, MinValue, MaxValue, exec));
        }
        return this;
    }

    public TableColumn<S, T> createTextFieldCell(Boolean isEditable, Runnable exec) {
        this.setEditable(isEditable);
        this.setCellFactory(column -> new TextFieldTbCell<S, T>(isEditable, exec));
        return this;
    }


    public TableColumn<S, T> createImageCell(Boolean isEditable) {
        this.setCellFactory(param -> {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(50);
            imageview.setFitWidth(50);
            TableCell<S, Image> cell = new TableCell<S, Image>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(item);
                    } else {
                        imageview.setImage(null);
                    }
                }
            };
            cell.setGraphic(imageview);
            return cell;
        });
        return this;
    }

    public TableColumn<S, T> createKeyFieldSearch(String Query, TableColumn<S, T> TbColForeing) {
        this.setQuery(Query);
        return this;
    }

    private String getQuery() {
        return Query;
    }

    private void setQuery(String query) {
        Query = query;
    }
}