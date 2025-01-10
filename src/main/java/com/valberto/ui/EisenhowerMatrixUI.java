package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import com.valberto.database.TaskDAO;
import com.valberto.model.Task;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class EisenhowerMatrixUI {
    private Shell shell;
    private Composite mainComposite;
    private Label footerLabel; // Rodapé
    private Map<String, org.eclipse.swt.widgets.List> quadrantLists = new HashMap<>();

    public EisenhowerMatrixUI(Shell shell) {
        this.shell = shell;
    }

    public void createUI() {
        shell.setLayout(new GridLayout(1, false)); // Layout para incluir o rodapé

        // Criar a barra de menu
        MenuBarUI menuBar = new MenuBarUI(shell, this);
        menuBar.createMenu();

        // Criar o layout principal
        mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setLayout(new GridLayout(2, true));
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Criação dos quadrantes
        createQuadrant("Urgente e Importante");
        createQuadrant("Não Urgente, mas Importante");
        createQuadrant("Urgente, mas Não Importante");
        createQuadrant("Nem Urgente, Nem Importante");

        // Carregar as tarefas iniciais
        loadTasks();

        // Criar o rodapé
        createFooter();
    }

    private void createQuadrant(String title) {
        Group group = new Group(mainComposite, SWT.NONE);
        group.setText(title);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        org.eclipse.swt.widgets.List taskList = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
        taskList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Habilitar Drag and Drop
        enableDragAndDrop(taskList, title);

        quadrantLists.put(title, taskList);
    }

    private void enableDragAndDrop(org.eclipse.swt.widgets.List taskList, String currentQuadrant) {
        // Drag Source
        DragSource dragSource = new DragSource(taskList, DND.DROP_MOVE);
        dragSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});
        dragSource.addDragListener(new DragSourceAdapter() {
            @Override
            public void dragSetData(DragSourceEvent event) {
                int selectedIndex = taskList.getSelectionIndex();
                if (selectedIndex >= 0) {
                    event.data = taskList.getItem(selectedIndex);
                }
            }

            @Override
            public void dragFinished(DragSourceEvent event) {
                if (event.detail == DND.DROP_MOVE) {
                    int selectedIndex = taskList.getSelectionIndex();
                    if (selectedIndex >= 0) {
                        taskList.remove(selectedIndex);
                    }
                }
            }
        });

        // Drop Target
        DropTarget dropTarget = new DropTarget(taskList, DND.DROP_MOVE);
        dropTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
        dropTarget.addDropListener(new DropTargetAdapter() {
            @Override
            public void drop(DropTargetEvent event) {
                if (event.data != null) {
                    String taskData = (String) event.data;
                    taskList.add(taskData);

                    // Atualizar o banco de dados
                    updateTaskQuadrant(taskData, currentQuadrant);
                }
            }
        });
    }

    private void updateTaskQuadrant(String taskData, String newQuadrant) {
        try {
            // Extrai o ID da tarefa (supõe que o ID está no início do texto)
            int taskId = extractTaskId(taskData);
            TaskDAO taskDAO = new TaskDAO();
            Task task = taskDAO.getTaskById(taskId);

            if (task != null) {
                task.setQuadrant(newQuadrant);
                taskDAO.updateTask(task);
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            showMessage("Erro", "Falha ao atualizar a tarefa no banco de dados.");
        }
    }

    private int extractTaskId(String taskData) throws NumberFormatException {
        String[] parts = taskData.split(" - ", 2);
        return Integer.parseInt(parts[0]);
    }

    private void loadTasks() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks = taskDAO.getAllTasks();

            for (Task task : tasks) {
                String quadrant = task.getQuadrant();
                if (quadrantLists.containsKey(quadrant)) {
                    quadrantLists.get(quadrant).add(task.toString());
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessage("Erro", "Falha ao carregar as tarefas.");
        }
    }

    public void reloadTasks() {
        // Limpa os quadrantes existentes
        quadrantLists.values().forEach(org.eclipse.swt.widgets.List::removeAll);

        // Recarrega as tarefas do banco de dados
        loadTasks();

        // Atualiza o rodapé
        updateFooter("Tarefas recarregadas com sucesso!");
    }

    private void createFooter() {
        footerLabel = new Label(shell, SWT.BORDER);
        footerLabel.setText("Pronto");
        footerLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    private void updateFooter(String message) {
        if (footerLabel != null && !footerLabel.isDisposed()) {
            footerLabel.setText(message);
        }
    }

    private void showMessage(String title, String message) {
        MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
        messageBox.setText(title);
        messageBox.setMessage(message);
        messageBox.open();
    }

    public Composite getMainComposite() {
        return mainComposite;
    }
}
