package br.com.sinergia.models.usage;

public class Estoque {

    private Integer CodProd;
    private Integer CodLocal;
    private Integer CodEmpresa;
    private String DescrProd;
    private String Controle;
    private String DescrLocal;
    private String DescrEmpresa;
    private char TipoEstoque;
    private String strTipoEstoque;
    private Float Estoque;
    private Float Reservado;

    public Estoque(int codProd, int codLocal, int codEmpresa, String descrProd, String controle, String descrLocal, String descrEmpresa,
                   char tipoEstoque, float estoque, float reservado) {
        super();
        setCodProd(codProd);
        setCodLocal(codLocal);
        setCodEmpresa(codEmpresa);
        setDescrProd(descrProd);
        setControle(controle);
        setDescrLocal(descrLocal);
        setDescrEmpresa(descrEmpresa);
        setTipoEstoque(tipoEstoque);
        setEstoque(estoque);
        setReservado(reservado);
    }

    public Integer getCodProd() {
        return CodProd;
    }

    public void setCodProd(Integer codProd) {
        CodProd = codProd;
    }

    public Integer getCodLocal() {
        return CodLocal;
    }

    public void setCodLocal(Integer codLocal) {
        CodLocal = codLocal;
    }

    public Integer getCodEmpresa() {
        return CodEmpresa;
    }

    public void setCodEmpresa(Integer codEmpresa) {
        CodEmpresa = codEmpresa;
    }

    public String getDescrProd() {
        return DescrProd;
    }

    public void setDescrProd(String descrProd) {
        DescrProd = descrProd;
    }

    public String getDescrLocal() {
        return DescrLocal;
    }

    public void setDescrLocal(String descrLocal) {
        DescrLocal = descrLocal;
    }

    public String getDescrEmpresa() {
        return DescrEmpresa;
    }

    public void setDescrEmpresa(String descrEmpresa) {
        DescrEmpresa = descrEmpresa;
    }

    public String getControle() {
        return Controle;
    }

    public void setControle(String controle) {
        Controle = controle;
    }

    public char getTipoEstoque() {
        return TipoEstoque;
    }

    public void setTipoEstoque(char tipoEstoque) {
        switch (tipoEstoque) {
            case 'P':
                setStrTipoEstoque("Próprio");
                break;
            case 'T':
                setStrTipoEstoque("Com Terceiros");
                break;
            case 'A':
                setStrTipoEstoque("Avulso");
                break;
            default:
                System.err.println("Estoque.setTipoEstoque(" + tipoEstoque + ") not programmed!");
                break;
        }
        TipoEstoque = tipoEstoque;
    }

    public Float getEstoque() {
        return Estoque;
    }

    public void setEstoque(Float estoque) {
        Estoque = estoque;
    }

    public Float getReservado() {
        return Reservado;
    }

    public void setReservado(Float reservado) {
        Reservado = reservado;
    }

    public String getStrTipoEstoque() {
        return strTipoEstoque;
    }

    public void setStrTipoEstoque(String strTipoEstoque) {
        this.strTipoEstoque = strTipoEstoque;
    }
}
