package br.com.sinergia.functions.extendeds;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.modules.CtrlAtalhos;
import br.com.sinergia.models.intern.AppObjects;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


public class SearchFieldTable {

    private DBParalelConex conex;
    private Stage mainStage = new Stage();
    private String DescrField;
    private String Query;
    private Integer QtdFields;
    private ArrayList<String> FieldsName = new ArrayList<>();
    private TableView<String[]> TbViewRetorno = new TableView<String[]>();
    private ArrayList<String> KeyReturn;
    private TableColumn[] TbColunas;
    private Button[] BtnFrames;

    public SearchFieldTable(ImageView searchImage, String descrField, Integer qtdFields, String[] descrCols, String query) {
        super();
        setDescrField(descrField);
        setQtdFields(qtdFields);
        setQuery(query);
        fieldsEstructure(searchImage);
        setFieldsName(new ArrayList(Arrays.asList(descrCols)));
    }

    private void createTableFrame() {
        Platform.runLater(() -> {
            StackPane newRoot = new StackPane();
            Scene newScene = new Scene(newRoot, 600, 500);
            mainStage.setScene(newScene);
            mainStage.setTitle("Pesquisa: " + getDescrField());
            mainStage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Pesquisa.png"));
            mainStage.initModality(Modality.WINDOW_MODAL);
            mainStage.initOwner(AppObjects.getAppObjects().getStageMain());
            Button[] BtnFrame = new Button[2];
            BtnFrame[0] = new Button("Escolher");
            BtnFrame[0].setOnAction(e -> {
                if (TbViewRetorno.getSelectionModel().getSelectedItem() == null) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Selecione uma linha para utilização"));
                    ModelDialog.getDialog().raise();
                } else {
                    String[] objSelected = TbViewRetorno.getSelectionModel().getSelectedItem();
                    setKeyReturn(objSelected);
                    mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                }
            });
            BtnFrame[1] = new Button("Sair");
            BtnFrame[1].setOnAction(event -> mainStage.close());
            setBtnFrames(BtnFrame);
            HBox hBox = new HBox(BtnFrame);
            hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));
            hBox.setSpacing(7);
            hBox.setStyle("-fx-border-color: GhostWhite; -fx-background-color: GhostWhite;");
            HBox boxFiltro = new HBox();
            VBox vBox = new VBox(TbViewRetorno, hBox);
            TbViewRetorno.setTableMenuButtonVisible(true);
            TbViewRetorno.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            TbViewRetorno.prefWidthProperty().bind(newScene.widthProperty().subtract(20));
            TbViewRetorno.prefHeightProperty().bind(newScene.heightProperty().subtract(20));
            newRoot.getChildren().add(vBox);
            CtrlAtalhos.getAtalhos().setNew(newScene, "ESC", () -> {
                mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
            });
        });
    }

    private void fieldsEstructure(ImageView imgSearch) {
        Platform.runLater(() -> {
            createTableFrame();
            imgSearch.setOnMouseClicked(e -> showListSearch());
            TbViewRetorno.setOnMouseClicked(e -> {
                if (e.getClickCount() > 1) {
                    if (TbViewRetorno.getItems().get(TbViewRetorno.getSelectionModel().getSelectedIndex()) != null) {
                        String[] objSelected = TbViewRetorno.getSelectionModel().getSelectedItem();
                        setKeyReturn(objSelected);
                        mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
                    }
                }
            });
        });
    }

    private void showListSearch() {
        refreshTabList();
        mainStage.show();
    }

    public void refreshTabList() {
        setKeyReturn(null);
        try {
            if (getFieldsName() == null || getFieldsName().isEmpty()) {
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Estrutura de colunas não programada para a pesquisa de: " + getDescrField() + "\n" +
                                "Não será possível exibir dados, favor revise."));
                ModelException.getException().raise();
                return;
            } else if (getFieldsName().size() != getQtdFields()) {
                ModelException.setNewException(new ModelException(this.getClass(), null,
                        "Quantidade de colunas programadas diferente da quantidade de colunas retornadas: " + getDescrField() + "\n" +
                                "Não será possível exibir dados, favor revise."));
                ModelException.getException().raise();
                return;
            }
            conex = new DBParalelConex(getQuery());
            conex.createSet();
            int CountRow = conex.countNumRows();
            int CountCol = conex.rs.getMetaData().getColumnCount();
            String[][] DadosTabela = new String[CountRow][CountCol];
            for (int RowAtual = 0; RowAtual < CountRow; RowAtual++) {
                conex.rs.next();
                ArrayList<String> ArrayColuna = new ArrayList<>();
                for (int ColAtual = 1; ColAtual <= CountCol; ColAtual++) {
                    ArrayColuna.add(conex.rs.getString(ColAtual));
                }
                String[] StrArray = new String[ArrayColuna.size()];
                StrArray = ArrayColuna.toArray(StrArray);
                DadosTabela[RowAtual] = ArrayColuna.toArray(StrArray);
            }
            ObservableList<String[]> Dados = FXCollections.observableArrayList();
            Dados.addAll(Arrays.asList(DadosTabela));
            TbViewRetorno.setItems(Dados);
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar tabela de pesquisa\n" + ex, ex));
            ModelException.getException().raise();
            TbViewRetorno.setItems(null);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar tabela de pesquisa\n" + ex, ex));
            ModelException.getException().raise();
            TbViewRetorno.setItems(null);
        } finally {
            conex.desconecta();
        }
    }

    public String getDescrField() {
        return DescrField;
    }

    public void setDescrField(String descrField) {
        DescrField = descrField;
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        Query = query;
    }

    public ArrayList<String> getFieldsName() {
        return FieldsName;
    }

    public void setFieldsName(ArrayList<String> fieldsName) {
        FieldsName = fieldsName;
        TbColunas = new TableColumn[fieldsName.size()];
        Integer index = 0;
        for (String fieldName : fieldsName) {
            Integer finalIndex = index;
            TbColunas[finalIndex] = new TableColumn<>(fieldName);
            TbColunas[finalIndex].setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>, ObservableValue<String>>) p -> new SimpleStringProperty((p.getValue()[finalIndex])));
            TbColunas[finalIndex].setStyle("-fx-alignment: CENTER; -fx-border-color: #F8F8FF;");
            index++;
        }
        TbViewRetorno.getColumns().clear();
        TbViewRetorno.getColumns().addAll(TbColunas);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public ArrayList<String> getKeyReturn() {
        return KeyReturn;
    }

    public void setKeyReturn(String[] keyReturn) {
        if (keyReturn == null) KeyReturn = null;
        else KeyReturn = new ArrayList<>(Arrays.asList(keyReturn));
    }

    public Button[] getBtnFrames() {
        return BtnFrames;
    }

    public void setBtnFrames(Button[] btnFrames) {
        BtnFrames = btnFrames;
    }

    public Integer getQtdFields() {
        return QtdFields;
    }

    public void setQtdFields(Integer qtdFields) {
        QtdFields = qtdFields;
    }

}
