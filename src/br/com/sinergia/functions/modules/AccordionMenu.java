package br.com.sinergia.functions.modules;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import br.com.sinergia.controllers.dialog.ModelDialog;
import br.com.sinergia.controllers.dialog.ModelException;
import br.com.sinergia.database.conect.DBParalelConex;
import br.com.sinergia.functions.extendeds.ButtonTela;
import br.com.sinergia.models.intern.AppInfo;
import br.com.sinergia.models.intern.AppObjects;
import br.com.sinergia.models.intern.User;
import br.com.sinergia.functions.natives.Functions;
import javafx.scene.control.Alert;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public final class AccordionMenu {

    DBParalelConex conex;
    Map<String, ArrayList> menuMap = new LinkedHashMap<>();
    TitledPane[] titledMenus;
    Boolean noError = true;
    ArrayList<String>[] listaTelas; //0 - Favoritas / 1 - Recentes
    ArrayList<String> allTelas = new ArrayList<>();
    CtrlArquivos ctrlArq = new CtrlArquivos();

    public  AccordionMenu() {
        String Menu;
        Menu = "Acessos";
        ArrayList<String> ListTelas = new ArrayList<>();
        ListTelas.add("Cadastro de Usuários");
        allTelas.add("Cadastro de Usuários");
        ListTelas.add("Controle de Acessos");
        allTelas.add("Controle de Acessos");
        ListTelas.add("Controle de Lembretes");
        allTelas.add("Controle de Lembretes");
        menuMap.put(Menu, ListTelas);
        hasAcesso(Menu, ListTelas);
        if (ListTelas.isEmpty()) {
            menuMap.remove(Menu);
        }
        Menu = "Avançado";
        ListTelas = new ArrayList<>();
        ListTelas.add("Empresas");
        allTelas.add("Empresas");
        ListTelas.add("Parceiros");
        allTelas.add("Parceiros");
        ListTelas.add("Operações");
        allTelas.add("Operações");
        ListTelas.add("Parâmetros");
        allTelas.add("Parâmetros");
        ListTelas.add("ConsultaDB");
        allTelas.add("ConsultaDB");
        menuMap.put(Menu, ListTelas);
        hasAcesso(Menu, ListTelas);
        if (ListTelas.isEmpty()) {
            menuMap.remove(Menu);
        }
        ListTelas = new ArrayList<>();
        Menu = "Cadastros";
        ListTelas.add("Produtos");
        allTelas.add("Produtos");
        ListTelas.add("Serviços");
        allTelas.add("Serviços");
        ListTelas.add("Grupo de Produtos");
        allTelas.add("Grupo de Produtos");
        ListTelas.add("Unidades");
        allTelas.add("Unidades");
        ListTelas.add("Locais");
        allTelas.add("Locais");
        ListTelas.add("Contas");
        allTelas.add("Contas");
        ListTelas.add("Agências");
        allTelas.add("Agências");
        ListTelas.add("Bancos");
        allTelas.add("Bancos");
        ListTelas.add("Transportes");
        allTelas.add("Transportes");
        ListTelas.add("Moedas");
        allTelas.add("Moedas");
        menuMap.put(Menu, ListTelas);
        hasAcesso(Menu, ListTelas);
        if (ListTelas.isEmpty()) {
            menuMap.remove(Menu);
        }
        ListTelas = new ArrayList<>();
        Menu = "Endereços";
        ListTelas.add("Países");
        allTelas.add("Países");
        ListTelas.add("Estados");
        allTelas.add("Estados");
        ListTelas.add("Cidades");
        allTelas.add("Cidades");
        ListTelas.add("Bairros");
        allTelas.add("Bairros");
        ListTelas.add("Logradouros");
        allTelas.add("Logradouros");
        menuMap.put(Menu, ListTelas);
        hasAcesso(Menu, ListTelas);
        if (ListTelas.isEmpty()) {
            menuMap.remove(Menu);
        }
        ListTelas = new ArrayList<>();
        Menu = "Comercial";
        ListTelas.add("Portal de Compras");
        allTelas.add("Portal de Compras");
        ListTelas.add("Portal de Vendas");
        allTelas.add("Portal de Vendas");
        ListTelas.add("Mov. Internas");
        allTelas.add("Mov. Internas");
        ListTelas.add("Tabela de Preços");
        allTelas.add("Tabela de Preços");
        ListTelas.add("Preços de Moedas");
        allTelas.add("Preços de Moedas");
        ListTelas.add("Controle de Estoque");
        allTelas.add("Controle de Estoque");
        menuMap.put(Menu, ListTelas);
        hasAcesso(Menu, ListTelas);
        if (ListTelas.isEmpty()) {
            menuMap.remove(Menu);
        }
        ListTelas = new ArrayList<>();
        Menu = "Financeiro";
        ListTelas.add("Mov. Financeira");
        allTelas.add("Mov. Financeira");
        ListTelas.add("Mov. Bancária");
        allTelas.add("Mov. Bancária");
        ListTelas.add("Saldo de Contas");
        allTelas.add("Saldo de Contas");
        ListTelas.add("Transferências");
        allTelas.add("Transferências");
        menuMap.put(Menu, ListTelas);
        hasAcesso(Menu, ListTelas);
        if (ListTelas.isEmpty()) {
            menuMap.remove(Menu);
        }
        AppInfo.getInfo().setArqTelasFav(ctrlArq.Busca(User.getCurrent().getCódUsu(), "Lista de Telas(Favoritas)"));
        create();
    }

    private void create() {
        noError = true;
        int QtdMenu = menuMap.size();
        titledMenus = new TitledPane[QtdMenu];
        for (int MenuAtual = 0; MenuAtual < QtdMenu; MenuAtual++) {
            String StrMenuAtual = (String) Functions.GetKeyByIndex(menuMap, MenuAtual);
            ArrayList<String> TelasMenu = (ArrayList<String>) Functions.GetValueByIndex(menuMap, MenuAtual);
            int QtdTelas = TelasMenu.size();
            VBox PaneMenu = new VBox();
            PaneMenu.setSpacing(1);
            PaneMenu.prefHeightProperty().bind(AppObjects.getAppObjects().getAccordionMenu().heightProperty());
            for (int TelaAtual = 0; TelaAtual < QtdTelas; TelaAtual++) {
                String StrTelaAtual = TelasMenu.get(TelaAtual);
                ButtonTela BtnTela = new ButtonTela(StrTelaAtual);
                BtnTela.prefWidthProperty().bind(AppObjects.getAppObjects().getAccordionMenu().widthProperty());
                BtnTela.setPrefHeight(40);
                PaneMenu.getChildren().add(BtnTela);
            }
            ImageView IconeMenu = new ImageView("/br/com/sinergia/properties/images/" + StrMenuAtual + ".png");
            IconeMenu.setFitHeight(32); //Redimensiona a imagem para 32x32
            IconeMenu.setFitWidth(32);
            titledMenus[MenuAtual] = new TitledPane(StrMenuAtual, PaneMenu);
            titledMenus[MenuAtual].setGraphic(IconeMenu); //Adiciona o icone ao menu.
        }
        AppObjects.getAppObjects().getAccordionMenu().getPanes().addAll(titledMenus);
    }

    public void filtra(String Pesquisa) {
        if(Pesquisa.equals("")) {
            AppObjects.getAppObjects().getAccordionMenu().getPanes().remove(0, AppObjects.getAppObjects().getAccordionMenu().getPanes().size());
            AppObjects.getAppObjects().getAccordionMenu().getPanes().addAll(titledMenus);
        } else {
            AppObjects.getAppObjects().getAccordionMenu().getPanes().remove(0, AppObjects.getAppObjects().getAccordionMenu().getPanes().size());
            VBox box = new VBox();
            box.setSpacing(1);
            box.prefHeightProperty().bind(AppObjects.getAppObjects().getAccordionMenu().heightProperty());
            for(int Telas = 0; Telas < allTelas.size(); Telas++) {
                String Tela = allTelas.get(Telas);
                if(box.getChildren().size() >= 14) {
                    break;
                }
                if(Functions.translate(Tela).contains(Functions.translate(Pesquisa))) {
                    ButtonTela BtnTela = new ButtonTela(Tela, true); //Não pode ser ButtonTela se não causa conflito na ListaDeFavoritos
                    BtnTela.prefWidthProperty().bind(AppObjects.getAppObjects().getAccordionMenu().widthProperty());
                    BtnTela.setPrefHeight(40);
                    box.getChildren().add(BtnTela);
                }
            }
            TitledPane titledPesquisa = new TitledPane(("'" + Pesquisa + "'"), box);
            ImageView iconePesquisa = new ImageView("/br/com/sinergia/properties/images/Icone_Pesquisa.png");
            iconePesquisa.setFitHeight(24);
            iconePesquisa.setFitWidth(24);
            titledPesquisa.setGraphic(iconePesquisa);
            AppObjects.getAppObjects().getAccordionMenu().getPanes().add(titledPesquisa);
            AppObjects.getAppObjects().getAccordionMenu().setExpandedPane(titledPesquisa);
        }
    }

    private void hasAcesso(String Menu, ArrayList<String> TelasMenu) {
        try {
            conex = new DBParalelConex("SELECT TELA, CASE WHEN VISUALIZA = 'S' THEN 'S' ELSE 'N' END AS ACESSO\n"
                    + "FROM TSIPER\n"
                    + "WHERE TELA IN " + Functions.NewArrayParameter(TelasMenu) + "\n"
                    + "AND PERFIL = ?");
            conex.addParameter(TelasMenu);
            conex.addParameter(User.getCurrent().getCódPerfil());
            if (conex.countRow() != TelasMenu.size()) {
                ArrayList<String> ArrayTelas = new ArrayList<>();
                conex.createSet();
                while (conex.rs.next()) {
                    ArrayTelas.add(conex.rs.getString("TELA"));
                }
                ModelDialog.setNewDialog(new ModelDialog(Alert.AlertType.ERROR, this.getClass(), null, "Erro grave ao tentar autenticar acesso para o Menu: '" + Menu + "'\n"
                        + "Favor contate o suporte imediatamente e verifique abaixo dicas de solucionamento.",
                        "Erro na tabela do banco de dados 'TSIPER', direcionada ao Perfil: '" + User.getCurrent().getCódPerfil() + "'.\n"
                                + "Não foi encontrado uma ou mais telas no Menu: '" + Menu + "' para certificar acessos.\n"
                                + "As telas encontradas foram: " + ArrayTelas.toString() + "\n"
                                + "Deveriam ser encontradas: " + TelasMenu.toString() + "\n"
                                + "Verifique se teve intervenção humana e corriga imediatamente.\n"
                                + "Até que seja ajustado, a aplicação será finalizada para preservação dos dados."));
                ModelDialog.getDialog().raise();
                System.exit(0);
            }
            conex.createSet();
            while (conex.rs.next()) {
                String TelaAtual = conex.rs.getString("TELA");
                String Acesso = conex.rs.getString("ACESSO");
                if (Acesso.equals("N")) {
                    System.out.println("Removed: " + TelaAtual);
                    TelasMenu.remove(TelaAtual);
                    allTelas.remove(TelaAtual);
                }
            }
        } catch (SQLException ex) {
            ModelException.setNewException(new ModelException(this.getClass(), null, "Erro interno no método: MenuAccordio.HasAcesso()\n"
                    + ex.getMessage() + "\nA aplicação será finalizada.", ex));
            ModelException.getException().raise();
            System.exit(0);
        } finally {
            conex.desconecta();
        }
    }

}
