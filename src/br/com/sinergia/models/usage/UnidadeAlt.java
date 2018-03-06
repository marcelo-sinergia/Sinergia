package br.com.sinergia.models.usage;

import br.com.sinergia.functions.natives.Functions;

import java.sql.Timestamp;

public class UnidadeAlt {
    private Integer CodProd;
    private String CodVol;
    private String DescrVol;
    private String DHCriacao;
    private String CodBarras;
    private String MultiDivid;
    private Integer QtdMultiDivid;
    private String DHAlter;
    private String UsuAlter;

    public UnidadeAlt() {
        super();
        setCodProd(0);
        setCodVol("");
        setDescrVol("");
        setDHCriacao("");
        setCodBarras("");
        setMultiDivid("Multiplica");
        setQtdMultiDivid(1);
        setUsuAlter("");
        setDHAlter("");
    }

    public UnidadeAlt(Integer codProd, String codVol, String descrVol, Timestamp dhCriacao, String codBarras,
                      char multiDivid, Integer qtdMultiDivid, String codUsuAlter, Timestamp dhAlter) {
        super();
        setCodProd(codProd);
        setCodVol(codVol);
        setDescrVol(descrVol);
        setDHCriacao(Functions.DataHoraFormater.format(dhCriacao));
        setCodBarras(codBarras);
        if(multiDivid == 'M') setMultiDivid("Multiplica");
        if(multiDivid == 'D') setMultiDivid("Divide");
        setQtdMultiDivid(qtdMultiDivid);
        setUsuAlter(codUsuAlter);
        setDHAlter(Functions.DataHoraFormater.format(dhAlter));
    }

    public Integer getCodProd() {
        return CodProd;
    }

    public void setCodProd(Integer codProd) {
        CodProd = codProd;
    }

    public String getCodVol() {
        return CodVol;
    }

    public void setCodVol(String codVol) {
        CodVol = codVol;
    }

    public String getDescrVol() {
        return DescrVol;
    }

    public void setDescrVol(String descrVol) {
        DescrVol = descrVol;
    }

    public String getCodBarras() {
        return CodBarras;
    }

    public void setCodBarras(String codBarras) {
        CodBarras = codBarras;
    }

    public Integer getQtdMultiDivid() {
        return QtdMultiDivid;
    }

    public void setQtdMultiDivid(Integer qtdMultiDivid) {
        QtdMultiDivid = qtdMultiDivid;
    }

    public String getDHCriacao() {
        return DHCriacao;
    }

    public void setDHCriacao(String DHCriacao) {
        this.DHCriacao = DHCriacao;
    }

    public String getMultiDivid() {
        return MultiDivid;
    }

    public void setMultiDivid(String multiDivid) {
        MultiDivid = multiDivid;
    }

    public String getDHAlter() {
        return DHAlter;
    }

    public void setDHAlter(String DHAlter) {
        this.DHAlter = DHAlter;
    }

    public String getUsuAlter() {
        return UsuAlter;
    }

    public void setUsuAlter(String usuAlter) {
        UsuAlter = usuAlter;
    }
}
