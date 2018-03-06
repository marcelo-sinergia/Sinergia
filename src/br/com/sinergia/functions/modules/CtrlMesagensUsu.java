package br.com.sinergia.functions.modules;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.Statement;
import br.com.sinergia.functions.natives.Functions;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import javafx.animation.Timeline;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CtrlMesagensUsu {

    Statement statement;
    private Timeline timeline;
    ListMensagem listMensagem;
    private int QtdMsgNaoVisualizadas = 0, CódMsgLastVisualizada = 0;
    private Map<Integer, ArrayList<String>> MapMsg = new LinkedHashMap<>();
    //CódMensagem x Visualizada||Prioridade||Título||Mensagem||CódUsuRem||LoginRem;

    public void criaTitledIni() {
        listMensagem = new ListMensagem(AppObjects.getAppObjects().getTtpMensagens());
        try {
            statement = new Statement("SELECT MSG.CODMSG, MSG.VISUALIZADA, MSG.PRIORIDADE, MSG.TITULO,\n" +
                    " MSG.MENSAGEM, MSG.DHALTER,\n" +
                    "CASE WHEN MSG.CODUSUREM IS NULL THEN -1 ELSE MSG.CODUSUREM END AS CODUSUREM,\n" +
                    "CASE WHEN MSG.CODUSUREM IS NULL THEN 'Desconhecido' ELSE USU.LOGIN END AS LOGINREM\n" +
                    "FROM TRIMSG MSG\n" +
                    "LEFT JOIN TSIUSU USU\n" +
                    "ON MSG.CODUSUREM = USU.CODUSU\n" +
                    "WHERE MSG.CODUSU = ?\n" +
                    "AND ROWNUM <= 20\n" +
                    "ORDER BY MSG.CODMSG");
            statement.addParameter(User.getCurrent().getCódUsu());
            int countRow = statement.countRow();
            if (countRow == 0) {
                return;
            } else {
                statement.createSet();
                while (statement.rs.next()) {
                    listMensagem.setNewMessage(new ListMensagem.UniqueMessageList(
                            statement.rs.getInt("CODMSG"),
                            statement.rs.getInt("PRIORIDADE"),
                            statement.rs.getInt("CODUSUREM"),
                            Functions.getImageUsu(statement.rs.getInt("CODUSUREM")),
                            statement.rs.getString("LOGINREM"),
                            Functions.DataHoraFormater.format(statement.rs.getTimestamp("DHALTER")),
                            statement.rs.getString("TITULO"),
                            statement.rs.getString("MENSAGEM"),
                            Functions.ToBoo(statement.rs.getString("VISUALIZADA"))));
                }
            }
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar buscar mensagens do usuário\n" + ex,
                    ex));
            ModelException.getException().raise();
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(this.getClass(),
                    null,
                    "Erro ao tentar buscar mensagens do usuário\n" + ex,
                    ex));
            ModelException.getException().raise();
        } finally {
            statement.end();
        }
    }
}
