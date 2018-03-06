package br.com.sinergia.models.usage;

import javafx.scene.image.Image;

import java.sql.Timestamp;
import java.time.Instant;

public class Produto {

    private int CodProd;
    private int CodGrupoProd;
    private int CodUsuAlter;
    private int DecCusto;
    private int DecVlr;
    private int DecQtd;
    private int CodLocal;
    private char UsoProd;
    private Double PerComGer;
    private Double PerComVen;
    private Double DescMax;
    private Integer VendaMin;
    private Integer VendaMax;
    private Integer EstMin;
    private Integer EstMax;
    private String Ativo;
    private String DescrProd;
    private String Complemento;
    private String Caracteristicas;
    private String DescrGrupoProd;
    private String Unidade;
    private String LoginUsuAlter;
    private String CodBarras;
    private String Referencia;
    private String Marca;
    private String NCM;
    private String DescrLocal;
    private String Promocao;
    private String PermCompra;
    private String PermVenda;
    private String PermConsumo;
    private Timestamp DHCriacao;
    private Timestamp DHAlter;
    private Image ImgProd;

    public Produto() {
        super();
        this.setCodProd(0);
        this.setDescrProd("");
        this.setAtivo("S");
        this.setComplemento("");
        this.setReferencia("");
        this.setMarca("");
        this.setCodGrupoProd(0);
        this.setNCM("");
        this.setCodBarras("");
        this.setUsoProd('R');
        this.setCodLocal(0);
        this.setPromocao("N");
        this.setPermCompra("S");
        this.setPermVenda("S");
        this.setPermConsumo("S");
        this.setCaracteristicas("");
        this.setUnidade("UN");
        this.setDecCusto(0);
        this.setDecQtd(0);
        this.setDecVlr(0);
        this.setVendaMin(0);
        this.setVendaMax(0);
        this.setEstMin(0);
        this.setEstMax(0);
        this.setPerComGer(0.0);
        this.setPerComVen(0.0);
        this.setDescMax(0.0);
        this.setImgProd(null);
    }

    public Produto(int CodProd, int CodGrupoProd, int CodUsuAlter, int DecCusto, int DecVlr, int DecQtd, int CodLocal,
                   Double PerComGer, Double PerComVen, Double DescMax, Integer VendaMin, Integer VendaMax, Integer EstMin, Integer EstMax,
                   String DescrProd, String Complemento, String Caracteristicas, String DescrGrupoProd, String Unidade, String LoginUsuAlter, String CodBarras,
                   String Referencia, String Marca, String UsoProd, String NCM, String DescrLocal,
                   String Ativo, String Promocao, String PermCompra, String PermVenda, String PermConsumo,
                   Timestamp DHCriacao, Timestamp DHAlter, Image ImgProd) {
        super();
        this.setCodProd(CodProd);
        this.setCodGrupoProd(CodGrupoProd);
        this.setCodUsuAlter(CodUsuAlter);
        this.setDecCusto(DecCusto);
        this.setDecVlr(DecVlr);
        this.setDecQtd(DecQtd);
        this.setCodLocal(CodLocal);
        this.setPerComGer(PerComGer);
        this.setPerComVen(PerComVen);
        this.setDescMax(DescMax);
        this.setVendaMin(VendaMin);
        this.setVendaMax(VendaMax);
        this.setEstMin(EstMin);
        this.setEstMax(EstMax);
        this.setAtivo(Ativo);
        this.setDescrProd(DescrProd);
        this.setComplemento(Complemento);
        this.setCaracteristicas(Caracteristicas);
        this.setDescrGrupoProd(DescrGrupoProd);
        this.setUnidade(Unidade);
        this.setLoginUsuAlter(LoginUsuAlter);
        this.setCodBarras(CodBarras);
        this.setReferencia(Referencia);
        this.setMarca(Marca);
        this.setUsoProd(UsoProd.charAt(0));
        this.setNCM(NCM);
        this.setDescrLocal(DescrLocal);
        this.setPromocao(Promocao);
        this.setPermCompra(PermCompra);
        this.setPermVenda(PermVenda);
        this.setPermConsumo(PermConsumo);
        this.setDHCriacao(DHCriacao);
        this.setDHAlter(DHAlter);
        this.setImgProd(ImgProd);
    }

    public int getCodProd() {
        return CodProd;
    }

    public void setCodProd(int codProd) {
        CodProd = codProd;
    }

    public int getCodGrupoProd() {
        return CodGrupoProd;
    }

    public void setCodGrupoProd(int codGrupoProd) {
        CodGrupoProd = codGrupoProd;
    }

    public int getCodUsuAlter() {
        return CodUsuAlter;
    }

    public void setCodUsuAlter(int codUsuAlter) {
        CodUsuAlter = codUsuAlter;
    }

    public int getDecCusto() {
        return DecCusto;
    }

    public void setDecCusto(int decCusto) {
        DecCusto = decCusto;
    }

    public int getDecVlr() {
        return DecVlr;
    }

    public void setDecVlr(int decVlr) {
        DecVlr = decVlr;
    }

    public int getDecQtd() {
        return DecQtd;
    }

    public void setDecQtd(int decQtd) {
        DecQtd = decQtd;
    }

    public int getCodLocal() {
        return CodLocal;
    }

    public void setCodLocal(int codLocal) {
        CodLocal = codLocal;
    }

    public Double getPerComGer() {
        return PerComGer;
    }

    public void setPerComGer(Double perComGer) {
        PerComGer = perComGer;
    }

    public Double getPerComVen() {
        return PerComVen;
    }

    public void setPerComVen(Double perComVen) {
        PerComVen = perComVen;
    }

    public Double getDescMax() {
        return DescMax;
    }

    public void setDescMax(Double descMax) {
        DescMax = descMax;
    }

    public Integer getVendaMin() {
        return VendaMin;
    }

    public void setVendaMin(Integer vendaMin) {
        VendaMin = vendaMin;
    }

    public Integer getVendaMax() {
        return VendaMax;
    }

    public void setVendaMax(Integer vendaMax) {
        VendaMax = vendaMax;
    }

    public Integer getEstMin() {
        return EstMin;
    }

    public void setEstMin(Integer estMin) {
        EstMin = estMin;
    }

    public Integer getEstMax() {
        return EstMax;
    }

    public void setEstMax(Integer estMax) {
        EstMax = estMax;
    }

    public String getAtivo() {
        return Ativo;
    }

    public void setAtivo(String ativo) {
        Ativo = ativo;
    }

    public String getDescrProd() {
        return DescrProd;
    }

    public void setDescrProd(String descrProd) {
        DescrProd = descrProd;
    }

    public String getComplemento() {
        return Complemento;
    }

    public void setComplemento(String complemento) {
        Complemento = complemento;
    }

    public String getCaracteristicas() {
        return Caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        Caracteristicas = caracteristicas;
    }

    public String getDescrGrupoProd() {
        return DescrGrupoProd;
    }

    public void setDescrGrupoProd(String descrGrupoProd) {
        DescrGrupoProd = descrGrupoProd;
    }

    public String getLoginUsuAlter() {
        return LoginUsuAlter;
    }

    public void setLoginUsuAlter(String loginUsuAlter) {
        LoginUsuAlter = loginUsuAlter;
    }

    public String getCodBarras() {
        return CodBarras;
    }

    public void setCodBarras(String codBarras) {
        CodBarras = codBarras;
    }

    public String getReferencia() {
        return Referencia;
    }

    public void setReferencia(String referencia) {
        Referencia = referencia;
    }

    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public char getUsoProd() {
        return UsoProd;
    }

    public void setUsoProd(char usoProd) {
        UsoProd = usoProd;
    }

    public String getNCM() {
        return NCM;
    }

    public void setNCM(String NCM) {
        this.NCM = NCM;
    }

    public String getDescrLocal() {
        return DescrLocal;
    }

    public void setDescrLocal(String descrLocal) {
        DescrLocal = descrLocal;
    }

    public String getPromocao() {
        return Promocao;
    }

    public void setPromocao(String promocao) {
        Promocao = promocao;
    }

    public String getPermCompra() {
        return PermCompra;
    }

    public void setPermCompra(String permCompra) {
        PermCompra = permCompra;
    }

    public String getPermVenda() {
        return PermVenda;
    }

    public void setPermVenda(String permVenda) {
        PermVenda = permVenda;
    }

    public String getPermConsumo() {
        return PermConsumo;
    }

    public void setPermConsumo(String permConsumo) {
        PermConsumo = permConsumo;
    }

    public Timestamp getDHCriacao() {
        return DHCriacao;
    }

    public void setDHCriacao(Timestamp DHCriacao) {
        this.DHCriacao = DHCriacao;
    }

    public Timestamp getDHAlter() {
        return DHAlter;
    }

    public void setDHAlter(Timestamp DHAlter) {
        this.DHAlter = DHAlter;
    }

    public Image getImgProd() {
        return ImgProd;
    }

    public void setImgProd(Image imgProd) {
        ImgProd = imgProd;
    }

    public String getUnidade() {
        return Unidade;
    }

    public void setUnidade(String unidade) {
        Unidade = unidade;
    }
}
