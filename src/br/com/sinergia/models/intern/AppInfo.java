package br.com.sinergia.models.intern;

import br.com.sinergia.functions.modules.CtrlArquivos;
import javafx.beans.binding.DoubleBinding;
public class AppInfo {

    CtrlArquivos ctrlArquivos = new CtrlArquivos();

    private static AppInfo properties = new AppInfo();

    private String IPMáquina;
    private String NomeMáquina;
    private String VersãoExec;
    private String VersãoBD;
    private DoubleBinding LayoutW;
    private DoubleBinding LayoutH;
    private String ArqTelasFav;

    public static AppInfo getInfo() {
        return properties;
    }

    public static void setInfo(AppInfo properties) {
        AppInfo.properties = properties;
    }

    public String getIPMáquina() {
        return IPMáquina;
    }

    public void setIPMáquina(String IPMáquina) {
        this.IPMáquina = IPMáquina;
    }

    public String getNomeMáquina() {
        return NomeMáquina;
    }

    public void setNomeMáquina(String nomeMáquina) {
        NomeMáquina = nomeMáquina;
    }

    public String getVersãoExec() {
        return VersãoExec;
    }

    public void setVersãoExec(String versãoExec) {
        VersãoExec = versãoExec;
    }

    public String getVersãoBD() {
        return VersãoBD;
    }

    public void setVersãoBD(String versãoBD) {
        VersãoBD = versãoBD;
    }

    public DoubleBinding getLayoutW() {
        return LayoutW;
    }

    public void setLayoutW(DoubleBinding layoutW) {
        LayoutW = layoutW;
    }

    public DoubleBinding getLayoutH() {
        return LayoutH;
    }

    public void setLayoutH(DoubleBinding layoutH) {
        LayoutH = layoutH;
    }

    public String getArqTelasFav() {
        return ArqTelasFav;
    }

    public void setArqTelasFav(String arqTelasFav) {
        ArqTelasFav = arqTelasFav;
    }

}
