package br.com.sinergia.models.usage;

import br.com.sinergia.functions.natives.CtrlStatus;
import javafx.scene.control.TableColumn;

public interface CadInterface {

    public void loadClass();

    public void fieldEstructure();

    public void loadTableValues();

    public void CtrlBtns(CtrlStatus action);

    public Boolean validChanges();

    public TableColumn[] getTableColumns();

    public void ctrlLinhasTab(int pos);

    public void runEdit(Runnable changes);

    public Boolean notifyEdit(Runnable changes);

    public void setMessage(String mensagem);
}
