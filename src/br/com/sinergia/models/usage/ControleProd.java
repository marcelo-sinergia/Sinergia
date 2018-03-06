package br.com.sinergia.models.usage;

import java.util.ArrayList;

public class ControleProd {

    private Integer CodProd;
    private Integer CodCtrl;
    private String DescrCtrl;
    private Character TipCtrl;
    private ArrayList<String> ListCtrl;

    public ControleProd() {
        super();
        setCodProd(0);
        setCodCtrl(0);
        setDescrCtrl("");
        setTipCtrl('N');
        setListCtrl(new ArrayList<>());
    }

    public ControleProd(Integer codProd, Integer codCtrl, String descrCtrl, String tipCtrl, ArrayList<String> listCtrl) {
        setCodProd(codProd);
        setCodCtrl(codCtrl);
        setDescrCtrl(descrCtrl);
        setTipCtrl(tipCtrl.charAt(0));
        setListCtrl(listCtrl);
    }

    public Integer getCodProd() {
        return CodProd;
    }

    public void setCodProd(Integer codProd) {
        CodProd = codProd;
    }

    public Integer getCodCtrl() {
        return CodCtrl;
    }

    public void setCodCtrl(Integer codCtrl) {
        CodCtrl = codCtrl;
    }

    public String getDescrCtrl() {
        return DescrCtrl;
    }

    public void setDescrCtrl(String descrCtrl) {
        DescrCtrl = descrCtrl;
    }

    public Character getTipCtrl() {
        return TipCtrl;
    }

    public void setTipCtrl(Character tipCtrl) {
        TipCtrl = tipCtrl;
    }

    public ArrayList<String> getListCtrl() {
        return ListCtrl;
    }

    public void setListCtrl(ArrayList<String> listCtrl) {
        ListCtrl = listCtrl;
    }
}
