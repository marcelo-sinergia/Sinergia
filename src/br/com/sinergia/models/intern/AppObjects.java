package br.com.sinergia.models.intern;

import br.com.sinergia.properties.metods.GravaLog;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class AppObjects {

    private static AppObjects appObjects;
    private Stage stageMain;
    private TabPane AbaPane;
    private Accordion AccordionMenu;
    private TitledPane TtpMensagens;
    private Button BtnMensagens;
    private VBox VBFavoritas;
    private VBox VBRecentes;
    private Label LblAddRem;
    private ArrayList<String>[] ListTelas;
    private ArrayList<String> TelasAbertas;
    private Timeline timeline;

    public AppObjects(TabPane AbaPane, Accordion AccordionMenu, Label AddRem, VBox VBRecentes, VBox VBFavoritas) {
        this.AbaPane = AbaPane;
        this.AccordionMenu = AccordionMenu;
        this.LblAddRem = AddRem;
        this.VBRecentes = VBRecentes;
        this.VBFavoritas = VBFavoritas;
        ListTelas = new ArrayList[2];
        ListTelas[0] = new ArrayList<String>(); //favoritas
        ListTelas[1] = new ArrayList<String>(); //recentes
        this.TelasAbertas = new ArrayList<String>();
        timeline = new Timeline();
    }

    public static AppObjects getAppObjects() {
        return appObjects;
    }

    public static void setAppObjects(AppObjects appObjects) {
        AppObjects.appObjects = appObjects;
    }

    public VBox getVBFavoritas() {
        return VBFavoritas;
    }

    public void setVBFavoritas(VBox VBFavoritas) {
        this.VBFavoritas = VBFavoritas;
    }

    public VBox getVBRecentes() {
        return VBRecentes;
    }

    public void setVBRecentes(VBox VBRecentes) {
        this.VBRecentes = VBRecentes;
    }

    public TabPane getAbaPane() {
        return AbaPane;
    }

    public void setAbaPane(TabPane abaPane) {
        AbaPane = abaPane;
    }

    public Accordion getAccordionMenu() {
        return AccordionMenu;
    }

    public void setAccordionMenu(Accordion accordionMenu) {
        AccordionMenu = accordionMenu;
    }

    public ArrayList<String>[] getListTelas() {
        return ListTelas;
    }

    public void setListTelas(ArrayList<String>[] listTelas) {
        ListTelas = listTelas;
    }

    public ArrayList<String> getTelasAbertas() {
        return TelasAbertas;
    }

    public void setTelasAbertas(ArrayList<String> telasAbertas) {
        TelasAbertas = telasAbertas;
    }

    public Label getLblAddRem() {
        return LblAddRem;
    }

    public void setLblAddRem(Label lblAddRem) {
        LblAddRem = lblAddRem;
    }

    public void setMensagem(String Mensagem) {
        if (getLblAddRem() == null) {
            System.out.println("Vazio");
        }
        getLblAddRem().setText(Mensagem);
        getLblAddRem().setVisible(true);
        timeline.stop();
        KeyFrame frame = new KeyFrame(Duration.millis(1500), e -> getLblAddRem().setVisible(false));
        timeline = new Timeline(frame);
        timeline.setCycleCount(1);
        timeline.play();
    }

    public Stage getStageMain() {
        if (stageMain == null) {
            GravaLog.getNewLinha().erro(this.getClass(), "O método AppObjects.getStageMain foi invocado\n" +
                    "Porém, o stage ainda não havia sido populado, isso pode provocar erros graves.");
        }
        return stageMain;
    }

    public void setStageMain(Stage stageMain) {
        this.stageMain = stageMain;
    }

    public TitledPane getTtpMensagens() {
        return TtpMensagens;
    }

    public void setTtpMensagens(TitledPane ttpMensagens) {
        this.TtpMensagens = ttpMensagens;
    }

    public Button getBtnMensagens() {
        return BtnMensagens;
    }

    public void setBtnMensagens(Button btnMensagens) {
        BtnMensagens = btnMensagens;
    }
}
