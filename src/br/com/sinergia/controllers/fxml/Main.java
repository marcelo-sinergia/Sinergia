package br.com.sinergia.controllers.fxml;

import br.com.sinergia.database.dicionario.FilesXML.Tradutor.ReaderDBDic;
import br.com.sinergia.database.dicionario.FilesXML.Tradutor.TabelaFull;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            TabelaFull tabTGFPRO = ReaderDBDic.getTabelaFullByIndex(0);
            tabTGFPRO.getCampos().forEach(campo-> {
                System.out.println(campo.getCodCampo() + " - " + campo.getNomeCampo());
                System.out.println(campo.getDescrCampo());
                System.out.println(campo.getTipoCampo());
                System.out.println(campo.getCamposOpcao().keySet());
                System.out.println(campo.getCamposOpcao().values());
            });
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        /*Parent root = FXMLLoader.load(getClass().getResource("/br/com/sinergia/views/Login.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sistema Sinergia");
        stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
        stage.setResizable(false);
        stage.show();
        stage.setY(stage.getY() * 3f / 2f); //Centraliza a tela*/
    }
}
