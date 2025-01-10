package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class MenuBarUI {
    private Shell shell;
    private EisenhowerMatrixUI matrixUI;

    public MenuBarUI(Shell shell, EisenhowerMatrixUI matrixUI) {
        this.shell = shell;
        this.matrixUI = matrixUI;
    }

    public void createMenu() {
        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);

        // Menu "Cadastros"
        MenuItem menuCadastros = new MenuItem(menuBar, SWT.CASCADE);
        menuCadastros.setText("Cadastros");
        Menu cadastrosMenu = new Menu(shell, SWT.DROP_DOWN);
        menuCadastros.setMenu(cadastrosMenu);

        // Item de menu: Incluir Tarefas
        MenuItem incluirTarefa = new MenuItem(cadastrosMenu, SWT.PUSH);
        incluirTarefa.setText("Incluir Tarefas");
        incluirTarefa.addListener(SWT.Selection, e -> {
            TaskRegistrationUI registrationUI = new TaskRegistrationUI(shell, matrixUI);
            registrationUI.open(); // Abre a janela de cadastro de tarefas
        });

        // Item de menu: Manutenção de Tarefas
        MenuItem manutencaoTarefa = new MenuItem(cadastrosMenu, SWT.PUSH);
        manutencaoTarefa.setText("Manutenção de Tarefas");
        manutencaoTarefa.addListener(SWT.Selection, e -> {
            TaskManagementUI managementUI = new TaskManagementUI(shell, matrixUI);
            managementUI.open(); // Abre a janela de manutenção de tarefas
        });

        // Menu "Relatórios"
        MenuItem menuRelatorios = new MenuItem(menuBar, SWT.CASCADE);
        menuRelatorios.setText("Relatórios");
        Menu relatoriosMenu = new Menu(shell, SWT.DROP_DOWN);
        menuRelatorios.setMenu(relatoriosMenu);

        // Item de menu: Relatório de Tarefas
        MenuItem relatorioTarefa = new MenuItem(relatoriosMenu, SWT.PUSH);
        relatorioTarefa.setText("Relatório de Tarefas");
        relatorioTarefa.addListener(SWT.Selection, e -> {
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            messageBox.setText("Relatório");
            messageBox.setMessage("Relatórios ainda não implementados.");
            messageBox.open();
        });

        // Menu "Ajuda"
        MenuItem menuAjuda = new MenuItem(menuBar, SWT.CASCADE);
        menuAjuda.setText("Ajuda");
        Menu ajudaMenu = new Menu(shell, SWT.DROP_DOWN);
        menuAjuda.setMenu(ajudaMenu);

        // Item de menu: Sobre
        MenuItem sobreItem = new MenuItem(ajudaMenu, SWT.PUSH);
        sobreItem.setText("Sobre");
        sobreItem.addListener(SWT.Selection, e -> {
            MessageBox aboutBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            aboutBox.setText("Sobre");
            aboutBox.setMessage("Aplicativo de Gestão de Tarefas com a Matriz de Eisenhower.\nVersão: 1.0\nDesenvolvido por Valberto.");
            aboutBox.open();
        });
    }
}
