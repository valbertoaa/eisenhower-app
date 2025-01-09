package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;

public class MenuBarUI {
    private Shell shell;
    private EisenhowerMatrixUI matrixUI;

    public MenuBarUI(Shell shell, EisenhowerMatrixUI matrixUI) {
        this.shell = shell;
        this.matrixUI = matrixUI;
        createMenu();
    }

    private void createMenu() {
        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);

        MenuItem menuCadastros = new MenuItem(menuBar, SWT.CASCADE);
        menuCadastros.setText("Cadastros");
        Menu cadastrosMenu = new Menu(shell, SWT.DROP_DOWN);
        menuCadastros.setMenu(cadastrosMenu);

        MenuItem incluirTarefa = new MenuItem(cadastrosMenu, SWT.PUSH);
        incluirTarefa.setText("Incluir Tarefas");
        incluirTarefa.addListener(SWT.Selection, event -> {
            TaskRegistrationUI taskRegistration = new TaskRegistrationUI(shell, matrixUI);
            taskRegistration.open();
        });

        MenuItem alterarTarefa = new MenuItem(cadastrosMenu, SWT.PUSH);
        alterarTarefa.setText("Manutenção de Tarefas");
        alterarTarefa.addListener(SWT.Selection, event -> {
            TaskManagementUI taskManagement = new TaskManagementUI(shell, matrixUI);
            taskManagement.open();
        });
    }
}
