package br.com.sinergia.database.dicionario.FilesXML.Tradutor;

public class Tabela {

    private Integer idxTabela;
    private Integer codTabela;
    private String nomeTabela;
    private String descrTabela;

    public Tabela(Integer idxTabela, Integer codTabela, String nomeTabela, String descrTabela) {
        super();
        setIdxTabela(idxTabela);
        setCodTabela(codTabela);
        setNomeTabela(nomeTabela);
        setDescrTabela(descrTabela);
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
}
