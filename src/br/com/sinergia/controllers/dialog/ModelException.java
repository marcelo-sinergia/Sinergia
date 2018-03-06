package br.com.sinergia.controllers.dialog;

import br.com.sinergia.properties.metods.GravaLog;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ModelException extends Exception {

    private static ModelException modelException;
    Class Invocador;
    String Header;
    String ContentMsg;
    private Boolean HasException;

    public ModelException(Class Invocador, String HeaderMsg, String ContentMsg) {
        setInvocador(Invocador);
        setHeader(HeaderMsg);
        setContentMsg(ContentMsg);
        setHasException(false);
    }

    public ModelException(Class Invocador, String HeaderMsg, String ContentMsg, Exception Error) {
        setInvocador(Invocador);
        setHeader(HeaderMsg);
        setContentMsg(ContentMsg);
        this.initCause(Error);
        setHasException(true);
    }

    public static ModelException getException() {
        return modelException;
    }

    public static void setNewException(ModelException NewModelException) {
        modelException = NewModelException;
    }

    public void raise() {
        GravaLog.getNewLinha().erro(getInvocador(), getContentMsg());
        GridPane expContent = new GridPane();
        if (getHasException()) {
            GravaLog.getNewLinha().gravaTraceException(getInvocador(), this);
            /*Criamos uma expensão pois, foi uma exception e tem stacktrace
            Quando não é exception, ou seja, erro criado, não tem porque criar*/
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            this.printStackTrace(pw);
            String exceptionText = sw.toString();
            Label label = new Label("Veja o caminho completo do erro:");
            TextArea textArea = new TextArea(exceptionText);
            textArea.setEditable(false);
            //textArea.setWrapText(true); Questão de dentição da mensagem de erro
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
        }
        Alert Alerta = new Alert(Alert.AlertType.ERROR);
        Alerta.setTitle("Sistema Sinergia:");
        Stage stage = (Stage) Alerta.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/properties/images/Icone_Error.png").toString()));
        Alerta.setHeaderText(getHeader());
        Alerta.setContentText(getContentMsg());
        Alerta.setGraphic(null);
        if (getHasException()) {
            Alerta.getDialogPane().setExpandableContent(expContent);
        }
        Alerta.showAndWait();
        stage.setY(stage.getY() * 3f / 2f); //On center
    }

    private Class getInvocador() {
        return Invocador;
    }

    private void setInvocador(Class Invocador) {
        this.Invocador = Invocador;
    }

    private String getHeader() {
        return Header;
    }

    private void setHeader(String Header) {
        this.Header = Header;
    }

    private String getContentMsg() {
        return ContentMsg;
    }

    private void setContentMsg(String ContentMsg) {
        this.ContentMsg = ContentMsg;
    }

    private Boolean getHasException() {
        return HasException;
    }

    private void setHasException(Boolean HasException) {
        this.HasException = HasException;
    }

}
