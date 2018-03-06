package br.com.sinergia.database.dicionario.FilesXML.Tradutor;

import java.util.LinkedHashMap;
import java.util.Map;

public class Campo {

    private Integer idxCampo;
    private Integer codCampo;
    private Campo_Type tipoCampo;
    private Integer vlrCampo;
    private String nomeCampo;
    private String descrCampo;
    private String pesqCampo;
    private Boolean Foreign;
    private Boolean Nullable;
    private String Default;
    private Boolean Opcao;
    private Map<String, String> camposOpcao = new LinkedHashMap<>();

    /*public Campo(Integer idxCampo, Integer codCampo, Integer tipoCampo, String nomeCampo, String descrCampo,
                 String pesqCampo, Boolean foreign, Boolean nullable, Object Default, Boolean )*/

    public Campo(Integer idxCampo, Integer codCampo, String nomeCampo, String descrCampo, String pesqCampo,
                 Integer tipoCampo, Integer vlrCampo, Boolean foreign, Boolean nullable,
                 String Default, Boolean opcao, Map<String, String> camposOpcao) {
        super();
        setIdxCampo(idxCampo);
        setCodCampo(codCampo);
        setTipoCampo(Campo_Type.getTipoCampo(tipoCampo));
        setVlrCampo(vlrCampo);
        setNomeCampo(nomeCampo);
        setDescrCampo(descrCampo);
        setPesqCampo(pesqCampo);
        setForeign(foreign);
        setNullable(nullable);
        setDefault(Default);
        setOpcao(opcao);
        setCamposOpcao(camposOpcao);
    }

    public Integer getIdxCampo() {
        return idxCampo;
    }

    public void setIdxCampo(Integer idxCampo) {
        this.idxCampo = idxCampo;
    }

    public Integer getCodCampo() {
        return codCampo;
    }

    public void setCodCampo(Integer codCampo) {
        this.codCampo = codCampo;
    }

    public Campo_Type getTipoCampo() {
        return tipoCampo;
    }

    public void setTipoCampo(Campo_Type tipoCampo) {
        this.tipoCampo = tipoCampo;
    }

    public Integer getVlrCampo() {
        return vlrCampo;
    }

    public void setVlrCampo(Integer vlrCampo) {
        this.vlrCampo = vlrCampo;
    }

    public String getNomeCampo() {
        return nomeCampo;
    }

    public void setNomeCampo(String nomeCampo) {
        this.nomeCampo = nomeCampo;
    }

    public String getDescrCampo() {
        return descrCampo;
    }

    public void setDescrCampo(String descrCampo) {
        this.descrCampo = descrCampo;
    }

    public String getPesqCampo() {
        return pesqCampo;
    }

    public void setPesqCampo(String pesqCampo) {
        this.pesqCampo = pesqCampo;
    }

    public Boolean isForeign() {
        return Foreign;
    }

    public void setForeign(Boolean foreign) {
        Foreign = foreign;
    }

    public Boolean isNullable() {
        return Nullable;
    }

    public void setNullable(Boolean nullable) {
        Nullable = nullable;
    }

    public Object getDefault() {
        return Default;
    }

    public void setDefault(String aDefault) {
        Default = aDefault;
    }

    public Boolean getOpcao() {
        return Opcao;
    }

    public void setOpcao(Boolean opcao) {
        Opcao = opcao;
    }

    public Map<String, String> getCamposOpcao() {
        return camposOpcao;
    }

    public void setCamposOpcao(Map<String, String> camposOpcao) {
        this.camposOpcao = camposOpcao;
    }


    public enum Campo_Type {
        NUMBER, DATE, FLOAT, DECIMAL, DOUBLE, VARCHAR2, NENHUM;

        public static Campo_Type getTipoCampo(Integer idx) {
            Campo_Type retorno;
            switch (idx) {
                case 1:
                    retorno = NUMBER;
                    break;
                case 2:
                    retorno = DATE;
                    break;
                case 3:
                    retorno = FLOAT;
                    break;
                case 4:
                    retorno = DECIMAL;
                    break;
                case 5:
                    retorno = DOUBLE;
                    break;
                case 6:
                    retorno = VARCHAR2;
                    break;
                default:
                    retorno = NENHUM;
                    break;
            }
            return retorno;
        }
    }
}
