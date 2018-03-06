package br.com.sinergia.functions.natives;

import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.controllers.fxml.LoginController;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Functions {

    public static SimpleDateFormat DataHoraFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static SimpleDateFormat DataFormater = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat HoraFormater = new SimpleDateFormat("HH:mm:ss");
    public static SimpleDateFormat HourMinFormater = new SimpleDateFormat("HH:mm");
    public static Map<Integer, Image> MapCachImgUsers = new LinkedHashMap<>();

    public static String formatDate(SimpleDateFormat formater, String value) {
        if (Nvl(value).equals("")) return "";
        else return formater.format(value);
    }

    public static String formatDate(SimpleDateFormat formater, Timestamp value) {
        if (value == null) return "";
        else return formater.format(value);
    }

    public static String Nvl(String Valor) {
        if (Valor == null) {
            return "";
        } else {
            return Valor;
        }
    }

    public static Boolean ToBoo(String Valor) {
        if (Valor.equals("S") || Valor.equals("s") || Valor.equals("Sim")) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean ToBoo(char Valor) {
        if(Valor == 'S') return true;
        else return false;
    }

    public static Boolean ToBoo(int Valor) {
        if (Valor == 0) {
            return false;
        } else if (Valor == 1) {
            return true;
        } else {
            System.err.println("Erro no método ToBoo(" + Valor + ")");
            return false;
        }
    }

    public static String getStrLogico(String Valor) {
        if (Valor.equals("Sim")) {
            return "S";
        } else {
            return "N";
        }
    }

    public static String StrFromBoo(Boolean Valor) {
        if (Valor) {
            return "S";
        } else {
            return "N";
        }
    }

    public static String StrFromBoo(String Valor) {
        if (Valor == "Sim") {
            return "S";
        } else {
            return "N";
        }
    }

    public static String LogicoFromBoo(Boolean Valor, Boolean shorted) {
        if (Valor) {
            if (shorted) {
                return "S";
            }
            return "Sim";
        } else {
            if (shorted) {
                return "N";
            }
            return "Não";
        }
    }

    public static int getLogicNumber(Integer value) {
        if (value == null || value < 0) return -1;
        else return value;
    }

    public static StringBuilder NewArrayParameter(ArrayList Array) {
        StringBuilder parameterBuilder = new StringBuilder();
        parameterBuilder.append("(");
        for (int i = 0; i < Array.size(); i++) {
            parameterBuilder.append("?");
            if (Array.size() > i + 1) {
                parameterBuilder.append(",");
            }
        }
        parameterBuilder.append(")");
        return parameterBuilder;
    }

    public static Object getMapKeyByValue(Map map, Object Value) {
        if (!map.containsValue(Value)) {
            return null;
        }
        for (Object keyS : map.keySet()) {
            if (map.get(keyS).equals(Value) || map.get(keyS) == Value) { //Alguns objetos não passam pelo "=="
                return keyS;
            }
        }
        return null;
    }

    public static Object GetValueByIndex(Map map, int Index) {
        Object Retorno = null;
        int QtdMap = map.size() - 1;
        if (Index > QtdMap) {
            return Retorno;
        } else {
            int Idx = 0;
            for (Object Objeto : map.values()) {
                if (Idx == Index) {
                    Retorno = Objeto;
                    break;
                }
                Idx = Idx + 1;
            }
        }
        return Retorno;
    }

    public static Object GetKeyByIndex(Map map, int Index) {
        Object Retorno = null;
        int QtdKey = map.size() - 1;
        if (Index > QtdKey) {
            return Retorno;
        } else {
            int Idx = 0;
            for (Object Objeto : map.keySet()) {
                if (Idx == Index) {
                    Retorno = Objeto;
                    break;
                }
                Idx = Idx + 1;
            }
        }
        return Retorno;
    }

    public static int GetIndexByKey(Map map, Object Objeto) {
        int retorno = 0;
        for (Object ObjKey : map.keySet()) {
            if (Objeto == ObjKey) {
                return retorno;
            } else {
                retorno = retorno + 1;
            }
        }
        return retorno;
    }

    public static String translate(String Str) {
        Str = Normalizer.normalize(Str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        Str = Str.toUpperCase();
        Str = Str.replace(" ", "");
        Str = Str.replace("/^[\\.-]/", "");
        return Str;
    }

    public static Image getImageUsu(int CódUsu) {
        if (CódUsu < 0) {
            CódUsu = 0;
        }
        if (MapCachImgUsers.containsKey(CódUsu)) {
            return MapCachImgUsers.get(CódUsu);
        } else {
            Image ImgUsu = new Image("/br/com/sinergia/properties/images/default.png");
            DBParalelConex statement = null;
            try {
                statement = new DBParalelConex("SELECT FOTO FROM TSIUSU WHERE CODUSU = ?");
                statement.addParameter(CódUsu);
                statement.createSet();
                statement.rs.next();
                if (statement.rs.getBytes("FOTO") != null) {
                    InputStream input = new ByteArrayInputStream(statement.rs.getBytes("FOTO"));
                    ImgUsu = new Image(input);
                    MapCachImgUsers.put(CódUsu, ImgUsu);
                } else {
                    MapCachImgUsers.put(CódUsu, ImgUsu);
                }
            } catch (SQLException ex) {
                ModelException.setNewException(new ModelException(LoginController.class, null,
                        "Erro ao tentar capturar foto do usuário\n" + ex, ex));
                ModelException.getException().raise();
            } catch (Exception ex) {
                ModelException.setNewException(new ModelException(LoginController.class, null,
                        "Erro ao tentar capturar foto do usuário\n" + ex, ex));
                ModelException.getException().raise();
            } finally {
                statement.desconecta();
                return ImgUsu;
            }
        }
    }

    public static void showLoginStage(Class Invocador) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Invocador.getResource("/br/com/sinergia/views/Login.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.setTitle("Sistema Sinergia");
            stage.getIcons().add(new Image("/br/com/sinergia/properties/images/Icone_Sistema.png"));
            stage.show();
            stage.setY(stage.getY() * 3f / 2f); //Alinha o componente no centro da tela
            User.getCurrent().closeSessão();
        } catch (IOException ex) {
            ModelException.setNewException(new ModelException(Invocador, null,
                    "Ocorreu um erro ao tentar efetuar logout do sistema\n" + ex + "\nO sistema será finalizado.", ex));
            ModelException.getException().raise();
            System.exit(0);
        }
    }

    public static Image ImgFromBytes(byte[] bytesImg) {
        if (bytesImg != null) {
            InputStream input = new ByteArrayInputStream(bytesImg);
            return new Image(input);
        } else {
            return null;
        }
    }

    public static Image ImgFromBytes(byte[] bytesImg, String Default) {
        if (bytesImg != null) {
            InputStream input = new ByteArrayInputStream(bytesImg);
            return new Image(input);
        } else {
            return new Image(Default);
        }
    }

    public static void setLayoutTab(Pane paneToResize) {
        Platform.runLater(() -> {
            paneToResize.prefWidthProperty().bind(AppObjects.getAppObjects().getAbaPane().widthProperty().subtract(5));
            paneToResize.prefHeightProperty().bind(AppObjects.getAppObjects().getAbaPane().heightProperty().subtract(30));
        });
    }

    public static ObservableList<String> getLogicoBox() {
        ObservableList<String> ListValues = FXCollections.observableArrayList();
        ListValues.addAll("Sim", "Não");
        return ListValues;
    }

    public static int getOnlyNumber(String value) {
        value = value.replaceAll("[^0-9]", "");
        if (Nvl(value).equals("")) value = "0";
        return Integer.parseInt(value);
    }

    public static int checkIfExists(String tabela, String coluna, Object valor) {
        int retorno = 0;
        DBParalelConex conex = null;
        try {
            conex = new DBParalelConex("SELECT COUNT(1) FROM " + tabela + " WHERE " + coluna + " = ?");
            conex.addParameter(valor);
            conex.createSet();
            conex.rs.next();
            retorno = conex.rs.getInt(1);
        } catch (Exception ex) {
            ModelException.setNewException(new ModelException(conex.getClass(), null,
                    "Erro ao tentar contar registros de " + tabela + "." + coluna + "\n" + ex, ex));
            ModelException.getException().raise();
        } finally {
            conex.desconecta();
            return retorno;
        }
    }
}