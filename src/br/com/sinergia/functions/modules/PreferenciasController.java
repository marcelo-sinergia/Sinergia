package br.com.sinergia.functions.modules;

import br.com.sinergia.database.conect.DBParalelConex;
import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class PreferenciasController implements Initializable {

    DBParalelConex conex;
    CtrlArquivos ctrlArquivos = new CtrlArquivos();
    private ObservableList<Preferences> MenusPreferences = FXCollections.observableArrayList();
    private ListView<Preferences> ListPreferences = new ListView<>(MenusPreferences);

    @FXML
    private ListView<Preferences> ListN;
    @FXML
    private JFXListView<Preferences> ListFx;

    public static class Preferences {
        private String NamePref;
        private int IndexPref;

        public Preferences(String NamePref, int IndexPref) {
            super();
            this.NamePref = NamePref;
            this.IndexPref = IndexPref;
        }
    }

    public PreferenciasController() {
        ListN = new ListView<>(MenusPreferences);
        ListN.setCellFactory(new Callback<ListView<Preferences>, ListCell<Preferences>>() {
            @Override
            public ListCell<Preferences> call(ListView<Preferences> arg0) {
                return new ListCell<Preferences>() {
                    @Override
                    protected void updateItem(Preferences Pref, boolean bln) {
                        super.updateItem(Pref, bln);
                        if (Pref != null) {
                            VBox Box = new VBox(new Text(Pref.NamePref), new Text(" - "), new Text(""+Pref.IndexPref));
                            Box.setSpacing(4);
                            setGraphic(Box);
                        }
                    }
                };
            }
        });
    }

    private void addNewPrefMenu(Preferences pref) {
        MenusPreferences.add(pref);
    }

    public void show() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}

