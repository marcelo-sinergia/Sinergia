package br.com.sinergia.controllers.dialog;

import br.com.sinergia.properties.metods.GravaLog;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public final class ModelDialog extends Dialog {

    private static ModelDialog modelDialog;
    private Alert.AlertType TipoAlerta;
    private Class Invocador;
    private String HeaderMsg;
    private String ContentMsg;
    private String TraceMsg;

    public ModelDialog(Alert.AlertType AlertaTipo, Class Invocador, String HeaderMsg, String ContentMsg) {
        setTipoAlerta(AlertaTipo);
        setInvocador(Invocador);
        setHeader(HeaderMsg);
        setContentMsg(ContentMsg);
        setTraceMsg(null);
    }

    public ModelDialog(Alert.AlertType AlertaTipo, Class Invocador, String HeaderMsg, String ContentMsg, String TraceMsg) {
        setTipoAlerta(AlertaTipo);
        setInvocador(Invocador);
        setHeader(HeaderMsg);
        setContentMsg(ContentMsg);
        setTraceMsg(TraceMsg);
    }

    public static ModelDialog getDialog() {
        return modelDialog;
    }

    public static void setNewDialog(ModelDialog NewModelDialog) {
        modelDialog = NewModelDialog;
    }

    public void raise() {
        if (getTipoAlerta() == null) {
            ModelException Error = new ModelException(this.getClass(), null, "Erro no método: ModelDialog\n"
                    + "Foi solicitado um AlertType diferente do esperado: '" + getTipoAlerta() + "'");
            Error.raise();
            return;
        }
        switch (getTipoAlerta()) {
            case INFORMATION:
            case NONE:
                GravaLog.getNewLinha().info(getInvocador(), getContentMsg());
                if(modelDialog.getTraceMsg() != null) {
                    GravaLog.getNewLinha().info(getInvocador(), getTraceMsg());
                }
                break;
            case WARNING:
                GravaLog.getNewLinha().alerta(getInvocador(), getContentMsg());
                if(modelDialog.getTraceMsg() != null) {
                    GravaLog.getNewLinha().alerta(getInvocador(), getTraceMsg());
                }
                break;
            case ERROR:
                GravaLog.getNewLinha().erro(getInvocador(), getContentMsg());
                if(modelDialog.getTraceMsg() != null) {
                    GravaLog.getNewLinha().erro(getInvocador(), getTraceMsg());
                }
                break;
            default: //Confirmation não pode ser lançado como Dialog
                ModelException Error = new ModelException(this.getClass(), null, "Erro no método: ModelDialog\n"
                        + "Foi solicitado um AlertType diferente do esperado: '" + getTipoAlerta() + "'");
                Error.raise();
        }
        Alert Alerta = new Alert(getTipoAlerta());
        if (modelDialog.getTraceMsg() != null) {
            GridPane expContent = new GridPane();
            /*Criamos um trace para dar dicas de como solucionar o problema*/
            Label label = new Label("Veja aqui dicas de como solucionar o ocorrido:");
            TextArea textArea = new TextArea(getTraceMsg());
            textArea.setEditable(false);
            //textArea.setWrapText(true); Já tem de estar quebrada no invocador
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);
            Alerta.getDialogPane().setExpandableContent(expContent);
        }
        Stage stage = (Stage) Alerta.getDialogPane().getScene().getWindow();
        if (getTipoAlerta() == Alert.AlertType.WARNING) {
            stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/properties/images/Icone_Alerta.png").toString()));
        } else if (getTipoAlerta() == Alert.AlertType.ERROR) {
            stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/properties/images/Icone_Error.png").toString()));
        } else {
            stage.getIcons().add(new Image(this.getClass().getResource("/br/com/sinergia/properties/images/Icone_Informação.png").toString()));
        }
        Alerta.setTitle("Sistema Sinergia:");
        Alerta.setGraphic(null);
        Alerta.setHeaderText(HeaderMsg);
        Alerta.setContentText(getContentMsg());
        Alerta.showAndWait();
    }

    private Class getInvocador() {
        return Invocador;
    }

    private void setInvocador(Class Invocador) {
        this.Invocador = Invocador;
    }

    private String getHeaderMsg() {
        return HeaderMsg;
    }

    private String getContentMsg() {
        return ContentMsg;
    }

    private void setContentMsg(String ContentMsg) {
        this.ContentMsg = ContentMsg;
    }

    private void setHeader(String Header) {
        this.HeaderMsg = Header;
    }

    public String getTraceMsg() {
        return TraceMsg;
    }

    public void setTraceMsg(String traceMsg) {
        TraceMsg = traceMsg;
    }

    public Alert.AlertType getTipoAlerta() {
        return TipoAlerta;
    }

    public void setTipoAlerta(Alert.AlertType tipoAlerta) {
        TipoAlerta = tipoAlerta;
    }
}
