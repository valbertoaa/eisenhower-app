package com.valberto.main;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.valberto.ui.EisenhowerMatrixUI;

public class EisenhowerMatrixApp {
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Gerenciador de Tarefas - Matriz de Eisenhower");
        shell.setSize(800, 600);
        shell.setLayout(new FillLayout()); // Ajusta automaticamente os componentes internos

        // Inicializa a interface principal
        EisenhowerMatrixUI matrixUI = new EisenhowerMatrixUI(shell);
        matrixUI.createUI(); // Cria os quadrantes e o menu

        // Abre a janela principal
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        // Libera os recursos do display
        display.dispose();
        // A titulo de teste add comentário aqui.
    }
}
