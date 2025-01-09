package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.valberto.database.TaskDAO;
import com.valberto.model.Task;

import java.sql.SQLException;
import java.util.List;

public class TaskManagementUI {
    private Shell parentShell;
    private Shell dialog;
    private EisenhowerMatrixUI matrixUI;

    public TaskManagementUI(Shell parentShell, EisenhowerMatrixUI matrixUI) {
        this.parentShell = parentShell;
        this.matrixUI = matrixUI;
    }

    public void open() {
        dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("Manutenção de Tarefas");
        dialog.setLayout(new GridLayout(2, false));

        // Centraliza a janela
        centerDialog();

        // Lista de tarefas
        org.eclipse.swt.widgets.List taskList = new org.eclipse.swt.widgets.List(dialog, SWT.BORDER | SWT.V_SCROLL);
        taskList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

        // Campos de edição
        new Label(dialog, SWT.NONE).setText("Título:");
        Text titleText = new Text(dialog, SWT.BORDER);
        titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(dialog, SWT.NONE).setText("Descrição:");
        Text descriptionText = new Text(dialog, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData descriptionGrid = new GridData(SWT.FILL, SWT.FILL, true, false);
        descriptionGrid.heightHint = 100;
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

        // Botões de ação
        Composite buttonComposite = new Composite(dialog, SWT.NONE);
        buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
        buttonComposite.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false, 2, 1));

        Button updateButton = new Button(buttonComposite, SWT.PUSH);
        updateButton.setText("Atualizar");
        updateButton.addListener(SWT.Selection, e -> {
            int selectedIndex = taskList.getSelectionIndex();
            if (selectedIndex >= 0) {
                String taskText = taskList.getItem(selectedIndex);
                try {
                    int taskId = extractTaskId(taskText);

                    Task updatedTask = new Task(
                        taskId,
                        titleText.getText().trim(),
                        descriptionText.getText().trim(),
                        String.format("%04d-%02d-%02d", deadlinePicker.getYear(),
                            deadlinePicker.getMonth() + 1, deadlinePicker.getDay()),
                        quadrantCombo.getText()
                    );

                    TaskDAO taskDAO = new TaskDAO();
                    taskDAO.updateTask(updatedTask);

                    taskList.setItem(selectedIndex, updatedTask.toString());
                    matrixUI.reloadTasks();

                    MessageBox successBox = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
                    successBox.setText("Sucesso");
                    successBox.setMessage("Tarefa atualizada com sucesso!");
                    successBox.open();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MessageBox errorBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("Erro");
                    errorBox.setMessage("Falha ao atualizar a tarefa.");
                    errorBox.open();
                }
            } else {
                MessageBox warningBox = new MessageBox(dialog, SWT.ICON_WARNING | SWT.OK);
                warningBox.setText("Aviso");
                warningBox.setMessage("Nenhuma tarefa selecionada.");
                warningBox.open();
            }
        });

        Button deleteButton = new Button(buttonComposite, SWT.PUSH);
        deleteButton.setText("Excluir");
        deleteButton.addListener(SWT.Selection, e -> {
            int selectedIndex = taskList.getSelectionIndex();
            if (selectedIndex >= 0) {
                String taskText = taskList.getItem(selectedIndex);
                try {
                    int taskId = extractTaskId(taskText);

                    TaskDAO taskDAO = new TaskDAO();
                    taskDAO.deleteTask(taskId);
                    taskList.remove(selectedIndex);
                    matrixUI.reloadTasks();

                    MessageBox successBox = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
                    successBox.setText("Sucesso");
                    successBox.setMessage("Tarefa excluída com sucesso!");
                    successBox.open();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    MessageBox errorBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("Erro");
                    errorBox.setMessage("Falha ao excluir a tarefa.");
                    errorBox.open();
                }
            } else {
                MessageBox warningBox = new MessageBox(dialog, SWT.ICON_WARNING | SWT.OK);
                warningBox.setText("Aviso");
                warningBox.setMessage("Nenhuma tarefa selecionada.");
                warningBox.open();
            }
        });

        Button closeButton = new Button(buttonComposite, SWT.PUSH);
        closeButton.setText("Fechar");
        closeButton.addListener(SWT.Selection, e -> dialog.close());

        dialog.setSize(600, 500);
        loadTasks(taskList, titleText, descriptionText, deadlinePicker, quadrantCombo);
        dialog.open();

        Display display = parentShell.getDisplay();
        while (!dialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void loadTasks(org.eclipse.swt.widgets.List taskList, Text titleText, Text descriptionText, DateTime deadlinePicker, Combo quadrantCombo) {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks = taskDAO.getAllTasks();
            for (Task task : tasks) {
                taskList.add(task.toString());
            }

            taskList.addListener(SWT.Selection, e -> {
                int selectedIndex = taskList.getSelectionIndex();
                if (selectedIndex >= 0) {
                    try {
                        String taskText = taskList.getItem(selectedIndex);
                        int taskId = extractTaskId(taskText);
                        Task selectedTask = taskDAO.getTaskById(taskId);

                        titleText.setText(selectedTask.getTitle());
                        descriptionText.setText(selectedTask.getDescription());
                        String[] deadlineParts = selectedTask.getDeadline().split("-");
                        deadlinePicker.setDate(
                            Integer.parseInt(deadlineParts[0]),
                            Integer.parseInt(deadlineParts[1]) - 1,
                            Integer.parseInt(deadlineParts[2])
                        );
                        quadrantCombo.setText(selectedTask.getQuadrant());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
            MessageBox errorBox = new MessageBox(dialog, SWT.ICON_ERROR | SWT.OK);
            errorBox.setText("Erro");
            errorBox.setMessage("Falha ao carregar as tarefas.");
            errorBox.open();
        }
    }

    private void centerDialog() {
        Rectangle parentBounds = parentShell.getBounds();
        Rectangle dialogBounds = dialog.getBounds();

        int x = parentBounds.x + (parentBounds.width - dialogBounds.width) / 2;
        int y = parentBounds.y + (parentBounds.height - dialogBounds.height) / 2;

        dialog.setLocation(x, y);
    }

    private int extractTaskId(String taskText) throws NumberFormatException {
        String[] parts = taskText.split(" - ", 2);
        return Integer.parseInt(parts[0]);
    }
}
