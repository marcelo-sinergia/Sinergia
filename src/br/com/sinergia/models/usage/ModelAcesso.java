package br.com.sinergia.models.usage;

public class ModelAcesso {

    private String Tela;
    private Boolean Visualiza;
    private Boolean Altera;
    private Boolean Inseri;
    private Boolean Exclui;
    private Integer Especial;

    public ModelAcesso(String tela, Boolean visualiza, Boolean altera, Boolean inseri, Boolean exclui, Integer especial) {
        super();
        setTela(tela);
        setVisualiza(visualiza);
        setAltera(altera);
        setInseri(inseri);
        setExclui(exclui);
        setEspecial(especial);
    }

    public String getTela() {
        return Tela;
    }

    public void setTela(String tela) {
        Tela = tela;
    }

    public Boolean getVisualiza() {
        return Visualiza;
    }

    public void setVisualiza(Boolean visualiza) {
        Visualiza = visualiza;
    }

    public Boolean getAltera() {
        return Altera;
    }

    public void setAltera(Boolean altera) {
        Altera = altera;
    }

    public Boolean getInseri() {
        return Inseri;
    }

    public void setInseri(Boolean inseri) {
        Inseri = inseri;
    }

    public Boolean getExclui() {
        return Exclui;
    }

    public void setExclui(Boolean exclui) {
        Exclui = exclui;
    }

    public Integer getEspecial() {
        return Especial;
    }

    public void setEspecial(Integer especial) {
        Especial = especial;
    }
}
