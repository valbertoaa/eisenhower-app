package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.valberto.database.TaskDAO;
import com.valberto.model.Task;

import java.sql.SQLException;
import java.util.List;

public class TaskManagementUI {
    private Shell parentShell;
    private Shell dialog;
    private Text titleText;
    private Text descriptionText;
    private DateTime deadlinePicker;
    private Combo quadrantCombo;
    private Button statusCheckbox; // Checkbox para o status
    private Task selectedTask; // Tarefa selecionada
    private EisenhowerMatrixUI matrixUI; // Referência à interface principal

    public TaskManagementUI(Shell parentShell, EisenhowerMatrixUI matrixUI) {
        this.parentShell = parentShell;
        this.matrixUI = matrixUI;
    }

    public void open() {
        dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("Manutenção de Tarefas");
        dialog.setLayout(new GridLayout(1, false));

        // Lista de tarefas
        org.eclipse.swt.widgets.List taskList = new org.eclipse.swt.widgets.List(dialog, SWT.BORDER | SWT.V_SCROLL);
        GridData taskListGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        taskListGridData.heightHint = 200; // Aumenta o tamanho da lista
        taskList.setLayoutData(taskListGridData);

        Composite formComposite = new Composite(dialog, SWT.NONE);
        formComposite.setLayout(new GridLayout(2, false));
        formComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Campos de edição
        new Label(formComposite, SWT.NONE).setText("Título:");
        titleText = new Text(formComposite, SWT.BORDER);
        titleText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        new Label(formComposite, SWT.NONE).setText("Descrição:");
        descriptionText = new Text(formComposite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData descriptionGrid = new GridData(SWT.FILL, SWT.FILL, true, false);
        descriptionGrid.heightHint = 120; // Altura maior para o campo de descrição
        descriptionText.setLayoutData(descriptionGrid);

        new Label(formComposite, SWT.NONE).setText("Prazo:");
        deadlinePicker = new DateTime(formComposite, SWT.DATE | SWT.DROP_DOWN);

        new Label(formComposite, SWT.NONE).setText("Quadrante:");
        quadrantCombo = new Combo(formComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        quadrantCombo.setItems(new String[]{
            "Urgente e Importante",
            "Não Urgente, mas Importante",
            "Urgente, mas Não Importante",
            "Nem Urgente, Nem Importante"
        });

        new Label(formComposite, SWT.NONE).setText("Situação:");
        statusCheckbox = new Button(formComposite, SWT.CHECK);
        statusCheckbox.setText("Encerrada");

        Composite buttonComposite = new Composite(dialog, SWT.NONE);
        buttonComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
        buttonComposite.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));

        // Botão Atualizar
        Button updateButton = new Button(buttonComposite, SWT.PUSH);
        updateButton.setText("Atualizar");
        updateButton.addListener(SWT.Selection, e -> {
            if (selectedTask != null) {
                selectedTask.setTitle(titleText.getText().trim());
                selectedTask.setDescription(descriptionText.getText().trim());
                selectedTask.setDeadline(String.format("%04d-%02d-%02d",
                    deadlinePicker.getYear(),
                    deadlinePicker.getMonth() + 1,
                    deadlinePicker.getDay()));
                selectedTask.setQuadrant(quadrantCombo.getText());
                selectedTask.setStatus(statusCheckbox.getSelection());

                try {
                    TaskDAO taskDAO = new TaskDAO();
                    taskDAO.updateTask(selectedTask);
                    loadTasks(taskList);

                    // Atualiza os quadrantes na interface principal
                    if (matrixUI != null) {
                        matrixUI.reloadTasks();
                    }

                    showMessage("Sucesso", "Tarefa atualizada com sucesso!");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showMessage("Erro", "Falha ao atualizar a tarefa.");
                }
            } else {
                showMessage("Aviso", "Nenhuma tarefa selecionada.");
            }
        });

        // Botão Fechar
        Button closeButton = new Button(buttonComposite, SWT.PUSH);
        closeButton.setText("Fechar");
        closeButton.addListener(SWT.Selection, e -> dialog.close());

        // Carregar tarefas ao abrir
        dialog.setSize(800, 600); // Aumenta a altura da janela
        loadTasks(taskList);

        // Configurar evento de clique duplo
        taskList.addListener(SWT.DefaultSelection, e -> {
            int selectedIndex = taskList.getSelectionIndex();
            if (selectedIndex >= 0) {
                try {
                    String taskData = taskList.getItem(selectedIndex);
                    int taskId = extractTaskId(taskData);

                    TaskDAO taskDAO = new TaskDAO();
                    selectedTask = taskDAO.getTaskById(taskId);

                    if (selectedTask != null) {
                        populateForm(selectedTask, titleText, descriptionText, deadlinePicker, quadrantCombo, statusCheckbox);
                    }
                } catch (SQLException | NumberFormatException ex) {
                    ex.printStackTrace();
                    showMessage("Erro", "Falha ao carregar os dados da tarefa.");
                }
            }
        });

        dialog.open();
    }

    private void loadTasks(org.eclipse.swt.widgets.List taskList) {
        taskList.removeAll();
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks = taskDAO.getAllTasks();
            for (Task task : tasks) {
                taskList.add(task.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessage("Erro", "Falha ao carregar as tarefas.");
        }
    }

    private void populateForm(Task task, Text titleText, Text descriptionText, DateTime deadlinePicker, Combo quadrantCombo, Button statusCheckbox) {
        titleText.setText(task.getTitle());
        descriptionText.setText(task.getDescription());
        String[] deadlineParts = task.getDeadline().split("-");
        deadlinePicker.setDate(
            Integer.parseInt(deadlineParts[0]),
            Integer.parseInt(deadlineParts[1]) - 1,
            Integer.parseInt(deadlineParts[2])
        );
        quadrantCombo.setText(task.getQuadrant());
        statusCheckbox.setSelection(task.isStatus());
    }

    private int extractTaskId(String taskData) throws NumberFormatException {
        String[] parts = taskData.split(" - ", 2);
        return Integer.parseInt(parts[0]);
    }

    private void showMessage(String title, String message) {
        MessageBox messageBox = new MessageBox(dialog, SWT.ICON_INFORMATION | SWT.OK);
        messageBox.setText(title);
        messageBox.setMessage(message);
        messageBox.open();
    }
}
