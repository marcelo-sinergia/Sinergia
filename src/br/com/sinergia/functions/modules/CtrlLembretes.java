package br.com.sinergia.functions.modules;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.Statement;
import br.com.sinergia.models.intern.User;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class CtrlLembretes {

    Statement statement;
    public String TextoLembrete;
    public Timestamp DataLembrete;
    public Boolean noError = true;

    public void getLembrete() {
        try {
            statement = new Statement("SELECT DHALTER, LEMBRETE FROM TGFLEM WHERE CODUSU = ?");
            statement.addParameter(User.getCurrent().getCódUsu());
            statement.createSet();
            if(statement.rs.next()) {
                DataLembrete = statement.rs.getTimestamp("DHALTER");
                TextoLembrete = statement.rs.getString("LEMBRETE");
            } else {
                DataLembrete = null;
                TextoLembrete = null;
            }
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar buscar lembrete\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar buscar lembrete\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            statement.end();
        }
    }

    public void setNewLembrete(String newLembrete) {
        noError = true;
        try {//Não é preciso ver se é INSERT ou UPDATE, a trigger TRG_INC_TGFLEM já trata
            statement = new Statement("INSERT INTO TGFLEM\n" +
                    "(CODUSU, DHALTER, LEMBRETE)\n" +
                    "VALUES\n" +
                    "(?, ?, ?)");
            statement.addParameter(User.getCurrent().getCódUsu());
            statement.addParameter(java.sql.Timestamp.from(Instant.now()));
            statement.addParameter(newLembrete);
            statement.run();
        } catch (SQLException ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar gravar novo lembrete\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            noError = false;
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar gravar novo lembrete\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            statement.end();
        }
    }
}
