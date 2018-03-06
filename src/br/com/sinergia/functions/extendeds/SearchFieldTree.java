package br.com.sinergia.functions.extendeds;

import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.modules.CtrlAtalhos;
import br.com.sinergia.models.intern.AppObjects;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFieldTree {

    private DBParalelConex conex;
    private Stage mainStage = new Stage();
    private TreeView<String> TreeViewRetorno = new TreeView<>();
    private Button[] BtnFrames;
    private String DescrField;
    private Integer QtdFields;
    private List<Integer> FieldsInTree;
    private String Query;
    private String QueryOpt;
    private ArrayList<String> KeyReturn;

    public SearchFieldTree(ImageView searchImage, String descrField, Integer qtdFields, Integer[] fieldsInTree, String query, String queryOpt) {
        super();
        /*
        SELECT CODGRUPOPROD, DESCRGRUPOPROD, ANALITICO FROM....
        qtdFields = 3;
        fieldsinTree = {1} = DESCGRUPOPROD
        */
        setDescrField(descrField);
        setQtdFields(qtdFields);
        setFieldsInTree(fieldsInTree);
        fieldsEstructure(searchImage);
        setQuery(query);
        setQueryOpt(queryOpt);
    }

    private void fieldsEstructure(ImageView imgSearch) {
        Platform.runLater(() -> {
            createTreeFrame();
            imgSearch.setOnMouseClicked(e -> showListSearch());
            TreeViewRetorno.setOnMouseClicked(e -> {
                if (TreeViewRetorno.getSelectionModel().getSelectedItem() != null) {
                    ModelTreeItem treeItem = (ModelTreeItem) TreeViewRetorno.getSelectionModel().getSelectedItem();
                    if (e.getClickCount() == 1) {
                        treeItem.setExpanded(true);
                    } else if (e.getClickCount() > 1) {
                        setKeyReturn(treeItem.getCoreValues());
                        getBtnFrames()[1].fire();
                    }
                }
            });
        });
    }

    private void showListSearch() {
        refreshTreeList();
        getMainStage().show();
    }

    public void refreshTreeList() {
        setKeyReturn(null);
        try {
            conex = new DBParalelConex(getQuery());
            conex.createSet();
            ModelTreeItem<String> rootNode = new ModelTreeItem<>("Raiz");
            TreeViewRetorno.setRoot(rootNode);
            TreeViewRetorno.setShowRoot(false);
            ArrayList<String[]> arrayNode = new ArrayList<>();
            while (conex.rs.next()) {
                ArrayList<String> arrayTemp = new ArrayList<>();
                for (int i = 1; i <= getQtdFields(); i++) {
                    arrayTemp.add(conex.rs.getString(i));
                }
                arrayNode.add(arrayTemp.toArray(new String[arrayTemp.size()]));
            }
            Boolean hasError = false;
            for (String[] Node : arrayNode) {
                try {
                    if (!hasError) PegaNodeFilho(rootNode, Node);
                } catch (Exception ex) {
                    hasError = true;
                    ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar árvore de " + getDescrField() + "\n" +
                            ex.getMessage(), ex));
                    ModelException.getException().raise();
                }
            }
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar árvore de pesquisa\n" + ex, ex));
            ModelException.getException().raise();
            TreeViewRetorno.setRoot(null);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar criar árvore de pesquisa\n" + ex, ex));
            ModelException.getException().raise();
            TreeViewRetorno.setRoot(null);
        } finally {
            conex.desconecta();
        }
    }

    private void PegaNodeFilho(TreeItem<String> rootNode, String[] Node) throws Exception {
        try {
            conex = new DBParalelConex(getQueryOpt());
            conex.addParameter(Node[0]);
            conex.createSet();
            ArrayList<String[]> arrayNode = new ArrayList<>();
            ModelTreeItem<String> treeItem = new ModelTreeItem<>(getNomeNode(Node));
            treeItem.setCoreValues(Node);
            rootNode.getChildren().add(treeItem);
            while (conex.rs.next()) {
                ArrayList<String> arrayTemp = new ArrayList<>();
                for (int i = 1; i <= getQtdFields(); i++) {
                    arrayTemp.add(conex.rs.getString(i));
                }
                arrayNode.add(arrayTemp.toArray(new String[arrayTemp.size()]));
            }
            for (String[] NodeFilho : arrayNode) {
                PegaNodeFilho(treeItem, NodeFilho);
            }
        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    public String getNomeNode(String[] node) {
        int QtdFieldsToShow = getFieldsInTree().size();
        if(QtdFieldsToShow == 0) return "";
        if(QtdFieldsToShow == 1) return node[getFieldsInTree().get(0)];
        else {
            String retorno = "";
            for(int i = 0; i < QtdFieldsToShow; i++) {
                //Se der algum erro aqui, veja se quem chamou está com os indíces corretos
                if(i == 0) retorno = retorno + node[getFieldsInTree().get(i)];
                else retorno = retorno + " - " + node[getFieldsInTree().get(i)];
            }
            return  retorno;
        }
    }


    private void createTreeFrame() {
        Platform.runLater(() -> {
            StackPane newRoot = new StackPane();
            Scene newScene = new Scene(newRoot, 400, 500);
            getMainStage().setScene(newScene);
            getMainStage().setTitle("Pesquisa: " + getDescrField());
            getMainStage().getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Pesquisa.png"));
            getMainStage().initModality(Modality.WINDOW_MODAL);
            getMainStage().initOwner(AppObjects.getAppObjects().getStageMain());
            Button[] BtnFrame = new Button[2];
            BtnFrame[0] = new Button("Escolher");
            BtnFrame[0].setOnAction(e -> {
                if (TreeViewRetorno.getSelectionModel().getSelectedItem() == null) {
                    ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.WARNING, this.getClass(), null,
                            "Selecione uma linha para utilização"));
                    ModelDialog.getDialog().raise();
                } else {
                    ModelTreeItem tree = (ModelTreeItem) TreeViewRetorno.getSelectionModel().getSelectedItem();
                    setKeyReturn(tree.getCoreValues());
                    getBtnFrames()[1].fire();
                }
            });
            BtnFrame[1] = new Button("Sair");
            BtnFrame[1].setOnAction(event ->
                    getMainStage().fireEvent(new WindowEvent(getMainStage(), WindowEvent.WINDOW_CLOSE_REQUEST))
            );
            setBtnFrames(BtnFrame);
            HBox hBox = new HBox(BtnFrame);
            hBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            hBox.setPadding(new Insets(5, 10, 5, 10));
            hBox.setSpacing(7);
            hBox.setStyle("-fx-border-color: LightGray; -fx-background-color: Gainsboro;");
            VBox vBox = new VBox(TreeViewRetorno, hBox);
            TreeViewRetorno.prefWidthProperty().bind(newScene.widthProperty().subtract(20));
            TreeViewRetorno.prefHeightProperty().bind(newScene.heightProperty().subtract(20));
            newRoot.getChildren().add(vBox);
            CtrlAtalhos.getAtalhos().setNew(newScene, "ESC", () -> {
                getBtnFrames()[1].fire();
            });
        });
    }

    public String getDescrField() {
        return DescrField;
    }

    public void setDescrField(String descrField) {
        DescrField = descrField;
    }

    public List<Integer> getFieldsInTree() {
        return FieldsInTree;
    }

    public void setFieldsInTree(Integer[] fieldsInTree) {
        FieldsInTree = Arrays.asList(fieldsInTree);
    }

    public String getQuery() {
        return Query;
    }

    public void setQuery(String query) {
        Query = query;
    }

    public String getQueryOpt() {
        return QueryOpt;
    }

    public void setQueryOpt(String queryOpt) {
        QueryOpt = queryOpt;
    }

    public ArrayList<String> getKeyReturn() {
        return KeyReturn;
    }

    public void setKeyReturn(String[] keyReturn) {
        if (keyReturn == null) KeyReturn = null;
        else KeyReturn = new ArrayList(Arrays.asList(keyReturn));
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

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
}
