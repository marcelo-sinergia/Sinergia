package br.com.sinergia.models.usage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GrupoProd {

    private Integer CodGrupoProd;
    private Integer CodGrupoPai;
    private String DescrGrupoProd;
    private char ValEstoque;
    private Boolean Analitico;

    public GrupoProd() {
        super();
        setCodGrupoProd(0);
        setCodGrupoPai(-1);
        setDescrGrupoProd("");
        setValEstoque('N');
        setAnalitico(true);
    }

    public GrupoProd(Integer codGrupoProd, Integer codGrupoPai, String descrGrupoProd, String valEstoque, boolean analitico) {
        super();
        setCodGrupoProd(codGrupoProd);
        setCodGrupoPai(codGrupoPai);
        setDescrGrupoProd(descrGrupoProd);
        setValEstoque(valEstoque.charAt(0));
        setAnalitico(analitico);
    }

    public Integer getCodGrupoProd() {
        return CodGrupoProd;
    }

    public void setCodGrupoProd(Integer codGrupoProd) {
        CodGrupoProd = codGrupoProd;
    }

    public Integer getCodGrupoPai() {
        return CodGrupoPai;
    }

    public void setCodGrupoPai(Integer codGrupoPai) {
        CodGrupoPai = codGrupoPai;
    }

    public String getDescrGrupoProd() {
        return DescrGrupoProd;
    }

    public void setDescrGrupoProd(String descrGrupoProd) {
        DescrGrupoProd = descrGrupoProd;
    }

    public Boolean getAnalitico() {
        return Analitico;
    }

    public void setAnalitico(Boolean analitico) {
        Analitico = analitico;
    }

    public char getValEstoque() {
        return ValEstoque;
    }

    public void setValEstoque(char valEstoque) {
        ValEstoque = valEstoque;
    }
}
