package br.com.sinergia.functions.modules;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.properties.metods.GravaLog;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CtrlAtalhos {

    private static CtrlAtalhos ctrlAtalhos = new CtrlAtalhos();
    private Map<Scene, ArrayList<String>> MapAtalhos = new LinkedHashMap<>();
    private ArrayList<String> ArrayAtalhos = new ArrayList<>();

    public static CtrlAtalhos getAtalhos() {
        return ctrlAtalhos;
    }

    public static void setAtalhos(CtrlAtalhos ctrlAtalhos) {
        CtrlAtalhos.ctrlAtalhos = ctrlAtalhos;
    }

    public void setNew(Scene sceneToAdd, String KeyComb, Runnable exec) {
        Platform.runLater(() -> {
            try {
                if (MapAtalhos.containsKey(sceneToAdd)) {
                    ArrayAtalhos = MapAtalhos.get(sceneToAdd);
                    if (ArrayAtalhos.contains(KeyComb)) {
                        GravaLog.getNewLinha().erro(this.getClass(),
                                "Método CtrlAtalhos.getAtalhos.setNew() invocado para um atalho já existente");
                        GravaLog.getNewLinha().erro(this.getClass(),
                                "Atalho invocado para: " + sceneToAdd.toString() + " x KeyCombination: " + KeyComb);
                    } else {
                        ArrayAtalhos.add(KeyComb);
                        MapAtalhos.put(sceneToAdd, ArrayAtalhos);
                        sceneToAdd.getAccelerators().put(KeyCombination.keyCombination(KeyComb), exec);
                    }
                } else {
                    ArrayAtalhos.clear();
                    ArrayAtalhos.add(KeyComb);
                    MapAtalhos.put(sceneToAdd, ArrayAtalhos);
                    sceneToAdd.getAccelerators().put(KeyCombination.keyCombination(KeyComb), exec);
                }
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(this.getClass(), null, "Erro ao tentar adicionar atalho\n" +
                        "Cena: " + sceneToAdd + " x Atalho: " + KeyComb + "\n" +
                        "O atalho será descontinuado. Contate o suporte.\n" + ex, ex));
                ModelException.getException().raise();
            }
        });
    }
}
