package br.com.sinergia.database.dicionario;

import java.util.LinkedHashMap;
import java.util.Map;

public class DDOpcoes {

    public static Map<Character, String> getListaUsoProd() {
        Map<Character, String> mapReturn = new LinkedHashMap<>();
        mapReturn.put('B', "Brinde");
        mapReturn.put('C', "Consumo");
        mapReturn.put('R', "Revenda");
        mapReturn.put('E', "Embalagem");
        mapReturn.put('V', "Venda (Fabricação própria)");
        return mapReturn;
    }

    public static Map<Character, String> getListaControle() {
        Map<Character, String> mapReturn = new LinkedHashMap<>();
        mapReturn.put('N', "Sem controle");
        mapReturn.put('L', "Controle por Lista");
        mapReturn.put('V', "Controle por Data de Validade");
        mapReturn.put('S', "Controle por Núm. de Série");
        return mapReturn;
    }

    public static String getDescrControle(Character character) {
        String retorno;
        switch (character) {
            case 'N':
                retorno = "Sem controle";
                break;
            case 'L':
                retorno = "Lista";
                break;
            case 'V':
                retorno = "Data de Validade";
                break;
            case 'S':
                retorno = "Série";
                break;
            default:
                retorno = "Erro";
                break;
        }
        return retorno;
    }

    public static Map<Character, String> getListaValEstoque() {
        Map<Character, String> mapReturn = new LinkedHashMap<>();
        mapReturn.put('N', "Não valida estoque");
        mapReturn.put('L', "Valida pelo Local");
        mapReturn.put('E', "Valida pela Empresa");
        mapReturn.put('A', "Valida pela Empresa/Local");
        return mapReturn;
    }

}
