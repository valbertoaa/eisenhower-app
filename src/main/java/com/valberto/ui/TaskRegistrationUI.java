package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.valberto.database.TaskDAO;
import com.valberto.model.Task;

public class TaskRegistrationUI {
    private Shell parentShell;
    private Shell dialog;
    private EisenhowerMatrixUI matrixUI;

    public TaskRegistrationUI(Shell parentShell, EisenhowerMatrixUI matrixUI) {
        this.parentShell = parentShell;
        this.matrixUI = matrixUI;
    }

    public void open() {
        dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("Cadastro de Tarefas");
        dialog.setLayout(new GridLayout(2, false));

        // Centraliza a janela
        centerDialog();

        new Label(dialog, SWT.NONE).setText("Título:");
        Text titleText = new Text(dialog, SWT.BORDER);
        titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("Descrição:");
        Text descriptionText = new Text(dialog, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData descriptionGrid = new GridData(SWT.FILL, SWT.FILL, true, false);
        descriptionGrid.heightHint = 150;
        descriptionText.setLayoutData(descriptionGrid);

        new Label(dialog, SWT.NONE).setText("Prazo:");
        DateTime deadlinePicker = new DateTime(dialog, SWT.DATE | SWT.DROP_DOWN);

        new Label(dialog, SWT.NONE).setText("Quadrante:");
        Combo quadrantCombo = new Combo(dialog, SWT.DROP_DOWN | SWT.READ_ONLY);
        quadrantCombo.setItems(new String[]{
            "Urgente e Importante",
            "Não Urgente, mas Importante",
            "Urgente, mas Não Importante",
            "Nem Urgente, Nem Importante"
        });
        quadrantCombo.select(0);

        Composite buttonComposite = new Composite(dialog, SWT.NONE);
        buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
        buttonComposite.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));

        Button saveButton = new Button(buttonComposite, SWT.PUSH);
        saveButton.setText("Salvar");
        saveButton.addListener(SWT.Selection, e -> {
            String title = titleText.getText().trim();
            String description = descriptionText.getText().trim();
            String quadrant = quadrantCombo.getText();
            String deadline = String.format("%04d-%02d-%02d",
                deadlinePicker.getYear(),
                deadlinePicker.getMonth() + 1,
                deadlinePicker.getDay());

            if (title.isEmpty()) {
                MessageBox errorBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
                errorBox.setText("Erro");
                errorBox.setMessage("O título da tarefa é obrigatório.");
                errorBox.open();
                return;
            }

            Task newTask = new Task(title, description, deadline, quadrant);

            try {
                TaskDAO taskDAO = new TaskDAO();
                taskDAO.addTask(newTask);

                if (matrixUI != null) {
                    matrixUI.reloadTasks();
                }

                MessageBox successBox = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
                successBox.setText("Sucesso");
                successBox.setMessage("Tarefa cadastrada com sucesso!");
                successBox.open();
                dialog.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                MessageBox errorBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
                errorBox.setText("Erro");
                errorBox.setMessage("Falha ao salvar a tarefa no banco de dados.");
                errorBox.open();
            }
        });

        Button cancelButton = new Button(buttonComposite, SWT.PUSH);
        cancelButton.setText("Cancelar");
        cancelButton.addListener(SWT.Selection, e -> dialog.close());

        dialog.setSize(600, 400);
        dialog.open();

        Display display = parentShell.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void centerDialog() {
        org.eclipse.swt.graphics.Rectangle parentBounds = parentShell.getBounds();
        org.eclipse.swt.graphics.Rectangle dialogBounds = dialog.getBounds();

        int x = parentBounds.x + (parentBounds.width - dialogBounds.width) / 2;
        int y = parentBounds.y + (parentBounds.height - dialogBounds.height) / 2;

        dialog.setLocation(x, y);
    }
}
