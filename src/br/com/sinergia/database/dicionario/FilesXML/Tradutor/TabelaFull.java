package br.com.sinergia.database.dicionario.FilesXML.Tradutor;

import java.util.ArrayList;

public class TabelaFull {

    private Integer idxTabela;
    private Integer codTabela;
    private String nomeTabela;
    private String descrTabela;
    private ArrayList<Campo> campos;

    public TabelaFull(Integer idxTabela, Integer codTabela, String nomeTabela, String descrTabela, ArrayList<Campo> campos) {
        super();
        setIdxTabela(idxTabela);
        setCodTabela(codTabela);
        setNomeTabela(nomeTabela);
        setDescrTabela(descrTabela);
        setCampos(campos);
    }

    public Integer getIdxTabela() {
        return idxTabela;
    }

    public void setIdxTabela(Integer idxTabela) {
        this.idxTabela = idxTabela;
    }

    public Integer getCodTabela() {
        return codTabela;
    }

    public void setCodTabela(Integer codTabela) {
        this.codTabela = codTabela;
    }

    public String getNomeTabela() {
        return nomeTabela;
    }

    public void setNomeTabela(String nomeTabela) {
        this.nomeTabela = nomeTabela;
    }

    public String getDescrTabela() {
        return descrTabela;
    }

    public void setDescrTabela(String descrTabela) {
        this.descrTabela = descrTabela;
    }

    public ArrayList<Campo> getCampos() {
        return campos;
    }

    public void setCampos(ArrayList<Campo> campos) {
        this.campos = campos;
    }
}
