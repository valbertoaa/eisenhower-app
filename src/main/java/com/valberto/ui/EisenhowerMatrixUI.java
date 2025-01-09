package com.valberto.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import com.valberto.database.TaskDAO;
import com.valberto.model.Task;
import com.valberto.util.DiscordWebhook;

import java.sql.SQLException;
import java.util.List;

public class EisenhowerMatrixUI {
    private Shell shell;
    private Composite mainComposite; // Conteúdo principal
    private org.eclipse.swt.widgets.List[] quadrants; // Quadrantes
    private Label statusBar; // Rodapé

    public EisenhowerMatrixUI(Shell shell) {
        this.shell = shell;
    }

    public Composite getMainComposite() {
        return mainComposite;
    }

    public void createUI() {
        if (mainComposite != null && !mainComposite.isDisposed()) {
            return; // Evita recriação da interface
        }

        shell.setLayout(new FillLayout()); // Define o layout do Shell

        mainComposite = new Composite(shell, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 5;
        gridLayout.marginHeight = 5;
        mainComposite.setLayout(gridLayout);

        createMenuBar();

        Composite quadrantsComposite = new Composite(mainComposite, SWT.NONE);
        quadrantsComposite.setLayout(new GridLayout(2, true));
        quadrantsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        quadrants = new org.eclipse.swt.widgets.List[4];
        quadrants[0] = createQuadrant(quadrantsComposite, "Urgente e Importante");
        quadrants[1] = createQuadrant(quadrantsComposite, "Não Urgente, mas Importante");
        quadrants[2] = createQuadrant(quadrantsComposite, "Urgente, mas Não Importante");
        quadrants[3] = createQuadrant(quadrantsComposite, "Nem Urgente, Nem Importante");

        statusBar = new Label(mainComposite, SWT.BORDER);
        statusBar.setText("Carregando tarefas...");
        statusBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        loadTasksFromDatabase();
        shell.layout(true, true);
    }

    private void createMenuBar() {
        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);

        MenuItem menuCadastros = new MenuItem(menuBar, SWT.CASCADE);
        menuCadastros.setText("Cadastros");
        Menu cadastrosMenu = new Menu(shell, SWT.DROP_DOWN);
        menuCadastros.setMenu(cadastrosMenu);

        MenuItem incluirTarefa = new MenuItem(cadastrosMenu, SWT.PUSH);
        incluirTarefa.setText("Incluir Tarefas");
        incluirTarefa.addListener(SWT.Selection, event -> {
            TaskRegistrationUI taskRegistration = new TaskRegistrationUI(shell, this);
            taskRegistration.open();
        });

        MenuItem alterarTarefa = new MenuItem(cadastrosMenu, SWT.PUSH);
        alterarTarefa.setText("Manutenção de Tarefas");
        alterarTarefa.addListener(SWT.Selection, event -> {
            TaskManagementUI taskManagement = new TaskManagementUI(shell, this);
            taskManagement.open();
        });

        MenuItem menuRelatorios = new MenuItem(menuBar, SWT.CASCADE);
        menuRelatorios.setText("Relatórios");
        Menu relatoriosMenu = new Menu(shell, SWT.DROP_DOWN);
        menuRelatorios.setMenu(relatoriosMenu);

        MenuItem relatorioItem = new MenuItem(relatoriosMenu, SWT.PUSH);
        relatorioItem.setText("Relatório de Tarefas");
        relatorioItem.addListener(SWT.Selection, event -> {
            MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            messageBox.setText("Relatórios");
            messageBox.setMessage("A funcionalidade de relatórios será implementada.");
            messageBox.open();
        });
    }

    public void reloadTasks() {
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        try {
            TaskDAO taskDAO = new TaskDAO();
            List<Task> tasks = taskDAO.getAllTasks();

            for (org.eclipse.swt.widgets.List quadrant : quadrants) {
                quadrant.removeAll();
            }

            for (Task task : tasks) {
                int quadrantIndex = getQuadrantIndex(task.getQuadrant());
                if (quadrantIndex >= 0 && quadrantIndex < quadrants.length) {
                    quadrants[quadrantIndex].add(task.toString());
                }
            }

            statusBar.setText("Tarefas carregadas com sucesso!");
            
            //DiscordWebhook.sendMessage("Nova tarefa adicionada: Estudar integração com Discord!");
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusBar.setText("Erro ao carregar tarefas.");
        }
    }

    private int getQuadrantIndex(String quadrant) {
        switch (quadrant) {
            case "Urgente e Importante":
                return 0;
            case "Não Urgente, mas Importante":
                return 1;
            case "Urgente, mas Não Importante":
                return 2;
            case "Nem Urgente, Nem Importante":
                return 3;
            default:
                return -1;
        }
    }

    private org.eclipse.swt.widgets.List createQuadrant(Composite parent, String title) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(title);
        group.setLayout(new FillLayout());

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        group.setLayoutData(gridData);

        return new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
    }
}
