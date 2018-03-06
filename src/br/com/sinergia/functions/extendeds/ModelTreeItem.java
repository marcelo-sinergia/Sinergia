package br.com.sinergia.functions.extendeds;

import javafx.scene.control.TreeItem;

public class ModelTreeItem<T> extends TreeItem<T> {

    private String Identifier;
    private String[] CoreValues;

    public ModelTreeItem(T value) {
        this.setValue(value);
    }

    public void setCoreValues(String[] coreValues) {
        this.CoreValues = coreValues;
    }

    public String[] getCoreValues() {
        return this.CoreValues;
    }

    public void setIdentifier(String id) {
        this.Identifier = id;
    }

    public String getIdentifier() {
        return this.Identifier;
    }

}
