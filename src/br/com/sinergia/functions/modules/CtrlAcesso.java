package br.com.sinergia.functions.modules;

import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.natives.Functions;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CtrlAcesso {

    DBParalelConex conex;
    private String Tela;
    private Integer Perfil;
    private Boolean Visualiza;
    private Boolean Altera;
    private Boolean Inseri;
    private Boolean Exclui;
    private List<String> Especial;

    public CtrlAcesso(String tela, Integer codUsu) throws Error {
        getAcesso(tela, codUsu);

    }

    private void getAcesso(String tela, Integer codUsu) throws Error {
        try {
            conex = new DBParalelConex("SELECT PER.PERFIL, PER.VISUALIZA, PER.ALTERA, PER.INSERI, PER.EXCLUI, PER.ESPECIAL\n" +
                    "FROM TSIUSU USU\n" +
                    "INNER JOIN TSIPER PER\n" +
                    "ON (USU.PERFIL = PER.PERFIL)\n" +
                    "WHERE USU.CODUSU = ?\n" +
                    "AND PER.TELA = ?");
            conex.addParameter(codUsu);
            conex.addParameter(tela);
            conex.createSet();
            if (conex.rs.next()) {
                setTela(tela);
                setPerfil(conex.rs.getInt("PERFIL"));
                setVisualiza(Functions.ToBoo(conex.rs.getString("VISUALIZA")));
                setAltera(Functions.ToBoo(conex.rs.getString("ALTERA")));
                setInseri(Functions.ToBoo(conex.rs.getString("INSERI")));
                setExclui(Functions.ToBoo(conex.rs.getString("EXCLUI")));
                if (Functions.Nvl(conex.rs.getString("ESPECIAL")).equals("")) setEspecial(null);
                else setEspecial(Arrays.asList(conex.rs.getString("ESPECIAL").split("||")));
            } else {
                throw new Error("Erro ao tentar buscar acessos do usuário\n" +
                        "Não encontrado acesso de: '" + tela + "' para o usuário: " + codUsu);
            }
        } catch (SQLException ex) {
            throw new Error("Erro ao tentar buscar acessos do usuário\n" + ex, ex);
        } catch (Exception ex) {
            throw new Error("Erro ao tentar buscar acessos do usuário\n" + ex, ex);
        } finally {
            conex.desconecta();
        }
    }

    public String getTela() {
        return Tela;
    }

    public void setTela(String tela) {
        Tela = tela;
    }

    public Integer getPerfil() {
        return Perfil;
    }

    public void setPerfil(Integer perfil) {
        Perfil = perfil;
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

    public List<String> getEspecial() {
        return Especial;
    }

    public void setEspecial(List<String> especial) {
        Especial = especial;
    }
}
