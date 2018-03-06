package br.com.sinergia.models.usage;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.functions.natives.Functions;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Local {

    private Integer CodLocal;
    private String DescrLocal;
    private char LocalPorEmp;
    private String strLocalPorEmp;
    private char PermEntra;
    private String strPermEntra;
    private char PermSai;
    private String strPermSai;
    private LocalDate DHLimitEntra;
    private String FmtdDHLimiteEntra;

    public Local() {
        super();
        this.setCodLocal(0);
        this.setDescrLocal("");
        this.setLocalPorEmp('S');
        this.setPermEntra('S');
        this.setPermSai('S');
        this.setDHLimitEntra(null);
        this.setFmtdDHLimiteEntra("");
    }

    public Local(Integer codLocal, String descrLocal, String localPorEmp, String permEntra, String permSai, LocalDate DHLimitEntra) {
        super();
        if (!localPorEmp.matches("[S]|[N]")) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Valor de Local.LocalPorEmp diferente de 'S' ou 'N'\n" +
                    "Valor obtido: " + localPorEmp + ". Favor contate o suporte!"));
            ModelException.getException().raise();
        }
        if (!permEntra.matches("[S]|[N]")) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Valor de Local.PermEntra diferente de 'S' ou 'N'\n" +
                    "Valor obtido: " + localPorEmp + ". Favor contate o suporte!"));
            ModelException.getException().raise();
        }
        if (!permSai.matches("[S]|[N]")) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Valor de Local.PermSai diferente de 'S' ou 'N'\n" +
                    "Valor obtido: " + localPorEmp + ". Favor contate o suporte!"));
            ModelException.getException().raise();
        }
        this.setCodLocal(codLocal);
        this.setDescrLocal(descrLocal);
        this.setLocalPorEmp(localPorEmp.charAt(0));
        this.setPermEntra(permEntra.charAt(0));
        this.setPermSai(permSai.charAt(0));
        this.setDHLimitEntra(DHLimitEntra);
    }

    public Integer getCodLocal() {
        return CodLocal;
    }

    public void setCodLocal(Integer codLocal) {
        CodLocal = codLocal;
    }

    public String getDescrLocal() {
        return DescrLocal;
    }

    public void setDescrLocal(String descrLocal) {
        DescrLocal = descrLocal;
    }

    public char getLocalPorEmp() {
        return LocalPorEmp;
    }

    public void setLocalPorEmp(char localPorEmp) {
        if(localPorEmp == 'S') this.setStrLocalPorEmp("Sim");
        else this.setStrLocalPorEmp("Não");
        LocalPorEmp = localPorEmp;
    }

    public char getPermEntra() {
        return PermEntra;
    }

    public void setPermEntra(char permEntra) {
        if(permEntra == 'S') this.setStrPermEntra("Sim");
        else this.setStrPermEntra("Não");
        PermEntra = permEntra;
    }

    public char getPermSai() {
        return PermSai;
    }

    public void setPermSai(char permSai) {
        if(permSai == 'S') this.setStrPermSai("Sim");
        else this.setStrPermSai("Não");
        PermSai = permSai;
    }

    public String getFmtdDHLimiteEntra() {
        return FmtdDHLimiteEntra;
    }

    public void setFmtdDHLimiteEntra(String fmtdDHLimiteEntra) {
        FmtdDHLimiteEntra = fmtdDHLimiteEntra;
    }

    public String getStrLocalPorEmp() {
        return strLocalPorEmp;
    }

    public void setStrLocalPorEmp(String strLocalPorEmp) {
        this.strLocalPorEmp = strLocalPorEmp;
    }

    public String getStrPermEntra() {
        return strPermEntra;
    }

    public void setStrPermEntra(String strPermEntra) {
        this.strPermEntra = strPermEntra;
    }

    public String getStrPermSai() {
        return strPermSai;
    }

    public void setStrPermSai(String strPermSai) {
        this.strPermSai = strPermSai;
    }

    public LocalDate getDHLimitEntra() {
        return DHLimitEntra;
    }

    public void setDHLimitEntra(LocalDate DHLimitEntra) {
        if(DHLimitEntra == null) this.setFmtdDHLimiteEntra("");
        else this.setFmtdDHLimiteEntra(Functions.formatDate(Functions.DataFormater, Timestamp.valueOf(DHLimitEntra.atStartOfDay())));
        this.DHLimitEntra = DHLimitEntra;
    }
}
