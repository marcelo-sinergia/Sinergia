package br.com.sinergia.database.dicionario.FilesXML.Tradutor;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static br.com.sinergia.functions.natives.Functions.ToBoo;

public class ReaderDBDic {

    public static Tabela getTabelaByIndex(Integer idxTabela) throws Exception {
        //File fileXML = new File("./br/com/sinergia/database/dicionario/FilesXML/Tabelas/TGFPRO.xml");
        File fileXML = new File("E:\\Projetos\\Sinergia\\src\\br\\com\\sinergia\\database\\dicionario\\FilesXML\\Tabelas\\TGFPRO.xml");
        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
        Document doc = (Document) docBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("Tabela");
        Node node = nodeList.item(idxTabela);
        if (node == null) {
            throw new Exception("Tabela não encontrada para indíce: " + idxTabela);
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            Integer codTabela = Integer.valueOf(element.getAttribute("nuTab"));
            String nomeTabela = element.getAttribute("nomeTab");
            String descrTabela = element.getAttribute("descrTab");
            return new Tabela(idxTabela, codTabela, nomeTabela, descrTabela);
        } else {
            throw new Exception("Tabela encontrada não é ELEMENT_NODE: " + idxTabela);
        }
    }

    public static TabelaFull getTabelaFullByIndex(Integer idxTabela) throws Exception {
        //File fileXML = new File("./br/com/sinergia/database/dicionario/FilesXML/Tabelas/TGFPRO.xml");
        File fileXML = new File("E:\\Projetos\\Sinergia\\src\\br\\com\\sinergia\\database\\dicionario\\FilesXML\\Tabelas\\TGFPRO.xml");
        DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
        Document doc = (Document) docBuilder.parse(fileXML);
        doc.getDocumentElement().normalize();
        NodeList nodeList = doc.getElementsByTagName("Tabela");
        Node node = nodeList.item(idxTabela);
        if (node == null) {
            throw new Exception("Tabela não encontrada para indíce: " + idxTabela);
        }
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            Integer codTabela = Integer.valueOf(element.getAttribute("nuTab"));
            String nomeTabela = element.getAttribute("nomeTab");
            String descrTabela = element.getAttribute("descrTab");
            NodeList nodeListCampo = doc.getElementsByTagName("Campo");
            ArrayList<Campo> arrayCampo = new ArrayList<>();
            for (int index = 0; index < nodeListCampo.getLength(); index++) {
                arrayCampo.add(getCampoTab(nodeListCampo, index));
            }
            arrayCampo.add(getCampoTab(nodeListCampo, 1));
            return new TabelaFull(idxTabela, codTabela, nomeTabela, descrTabela, arrayCampo);
        } else {
            throw new Exception("Tabela encontrada não é ELEMENT_NODE: " + idxTabela);
        }
    }

    public static Campo getCampoTab(NodeList nodeList, Integer idxCampo) throws Exception {
        Node node = nodeList.item(idxCampo);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element elementCampo = (Element) node;
            Integer codCampo = Integer.valueOf(elementCampo.getAttribute("idCampo"));
            String nomeCampo = elementCampo.getAttribute("nomeCampo");
            String descrCampo = elementCampo.getAttribute("descrCampo");
            String pesqCampo = elementCampo.getAttribute("pesqCampo");
            Element elementTipo = (Element) elementCampo.getElementsByTagName("InfoCampo").item(0);
            Integer tipoCampo = Integer.valueOf(elementTipo.getAttribute("tipoCampo"));
            Integer vlrCampo = Integer.valueOf(elementTipo.getAttribute("vlrCampo"));
            Boolean isForeign = ToBoo(elementTipo.getAttribute("foreign"));
            Boolean isNullable = ToBoo(elementTipo.getAttribute("nullable"));
            String Default = elementTipo.getAttribute("default");
            Element elementOpcoes = (Element) elementCampo.getElementsByTagName("Opcoes").item(0);
            Boolean hasOpcoes = elementOpcoes.hasChildNodes();
            Map<String, String> mapOpcoes = new LinkedHashMap<>();
            if (hasOpcoes) {
                int qtdOpcoes = elementOpcoes.getElementsByTagName("Opcao").getLength();
                for (int indexOpcao = 0; indexOpcao < qtdOpcoes; indexOpcao++) {
                    Element elementOpcao = (Element) (Element) elementOpcoes.getElementsByTagName("Opcao").item(indexOpcao);
                    String valor = elementOpcao.getAttribute("valor");
                    String resultado = elementOpcao.getAttribute("resultado");
                    mapOpcoes.put(valor, resultado);
                }
            }
            return new Campo(idxCampo, codCampo, nomeCampo, descrCampo, pesqCampo,
                    tipoCampo, vlrCampo, isForeign, isNullable, Default, hasOpcoes, mapOpcoes);
        } else {
            throw new Exception("Campo encontrado não é ELEMENT_NODE: " + idxCampo);
        }
    }

    private static Object getTagValue(Element element, String nomeTag, Integer idxElement) {
        NodeList nlList = element.getElementsByTagName(nomeTag).item(idxElement).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }

    private static Object getTagValue(Element element, String nomeTag, Integer idxElement, Integer idxSubElement) {
        NodeList nlList = element.getElementsByTagName(nomeTag).item(idxElement).getChildNodes();
        Node nValue = (Node) nlList.item(idxSubElement);
        return nValue.getNodeValue();
    }
}
