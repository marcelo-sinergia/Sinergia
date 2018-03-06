package br.com.sinergia.models.intern;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.Statement;
import javafx.scene.image.Image;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class User {

    private static User user;

    private int CódUsu;
    private int CódSessão;
    private int CódPerfil;
    private int CódEmp;
    private Boolean Ativo;
    private String LoginUsu;
    private String NomeUsu;
    private String SenhaUsu;
    private String CryptSenha;
    private String NomeFantasiaEmp;
    private String RazaoSocialEmp;
    private String CNPJ;
    private Timestamp DHLogin;
    private Image FotoUsu;

    public User(int CódUsu, Boolean Ativo, String LoginUsu, String NomeUsu, Image FotoUsu, int CódPerfil, String SenhaUsu, String CryptSenha,
                int CódEmp, String NomeFantasiaEmp, String RazaoSocialEmp, String CNPJ) {
        setCódUsu(CódUsu);
        setAtivo(Ativo);
        setLoginUsu(LoginUsu);
        setNomeUsu(NomeUsu);
        setFotoUsu(FotoUsu);
        setCódPerfil(CódPerfil);
        setSenhaUsu(SenhaUsu);
        setCryptSenha(CryptSenha);
        setCódEmp(CódEmp);
        setNomeFantasiaEmp(NomeFantasiaEmp);
        setRazaoSocialEmp(RazaoSocialEmp);
        setCNPJ(CNPJ);
        setDHLogin(Timestamp.from(Instant.now()));
    }

    public static User getCurrent() {
        return user;
    }

    public static void setCurrent(User user) {
        User.user = user;
    }

    public void closeSessão() {
        try {
            Statement statement = new Statement("UPDATE TSISES\n" +
                    "SET DHLOGOUT = ?\n"
                    + "WHERE CODSESSAO = ?\n"
                    + "AND CODUSU = ?");
            statement.addParameter(Timestamp.from(Instant.now()));
            statement.addParameter(getCódSessão());
            statement.addParameter(getCódUsu());
            statement.run();
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar finalizar sessão do usuário\n" +
                            ex.getMessage(),
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null,
                    "Erro ao tentar finalizar sessão do usuário\n" +
                            ex.getMessage(),
                    ex));
            ModelException.getException().raise();
        }
    }

    public int getCódUsu() {
        return CódUsu;
    }

    public void setCódUsu(int códUsu) {
        CódUsu = códUsu;
    }

    public String getLoginUsu() {
        return LoginUsu;
    }

    public void setLoginUsu(String loginUsu) {
        LoginUsu = loginUsu;
    }

    public String getNomeUsu() {
        return NomeUsu;
    }

    public void setNomeUsu(String nomeUsu) {
        NomeUsu = nomeUsu;
    }

    public String getSenhaUsu() {
        return SenhaUsu;
    }

    public void setSenhaUsu(String senhaUsu) {
        SenhaUsu = senhaUsu;
    }

    public String getCryptSenha() {
        return CryptSenha;
    }

    public void setCryptSenha(String cryptSenha) {
        CryptSenha = cryptSenha;
    }

    public Boolean getAtivo() {
        return Ativo;
    }

    public void setAtivo(Boolean ativo) {
        Ativo = ativo;
    }

    public int getCódSessão() {
        return CódSessão;
    }

    public void setCódSessão(int códSessão) {
        CódSessão = códSessão;
    }

    public int getCódPerfil() {
        return CódPerfil;
    }

    public void setCódPerfil(int códPerfil) {
        CódPerfil = códPerfil;
    }

    public int getCódEmp() {
        return CódEmp;
    }

    public void setCódEmp(int códEmp) {
        CódEmp = códEmp;
    }

    public String getCNPJ() {
        return CNPJ;
    }

    public void setCNPJ(String CNPJ) {
        this.CNPJ = CNPJ;
    }

    public String getNomeFantasiaEmp() {
        return NomeFantasiaEmp;
    }

    public void setNomeFantasiaEmp(String nomeFantasiaEmp) {
        NomeFantasiaEmp = nomeFantasiaEmp;
    }

    public String getRazaoSocialEmp() {
        return RazaoSocialEmp;
    }

    public void setRazaoSocialEmp(String razaoSocialEmp) {
        RazaoSocialEmp = razaoSocialEmp;
    }

    public Timestamp getDHLogin() {
        return DHLogin;
    }

    public void setDHLogin(Timestamp DHLogin) {
        this.DHLogin = DHLogin;
    }

    public Image getFotoUsu() {
        return FotoUsu;
    }

    public void setFotoUsu(Image fotoUsu) {
        FotoUsu = fotoUsu;
    }
}
