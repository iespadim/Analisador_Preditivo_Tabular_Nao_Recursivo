package app;

import negocio.Gramatica;
import negocio.Symbol;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TabelaPreditivaUI extends JFrame {
    private Gramatica gramatica;
    private JTable tabelaPreditiva;

    public TabelaPreditivaUI(Gramatica gramatica) {
        this.gramatica = gramatica;
        setTitle("Tabela Preditiva");
        setSize(800, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabelaPreditiva = new JTable();
        JScrollPane scrollPane = new JScrollPane(tabelaPreditiva);
        add(scrollPane, BorderLayout.CENTER);

        atualizaTabelaPreditiva();
    }

    private void atualizaTabelaPreditiva() {
        DefaultTableModel model = new DefaultTableModel();

        model.addColumn("LE \\ LD"); // Coluna vazia para os não terminais
        for (Symbol terminal : gramatica.getTerminais()) {
            model.addColumn(terminal.toString());
        }

        for (Symbol naoTerminal : gramatica.getNaoTerminais()) {
            Object[] rowData = new Object[gramatica.getTerminais().size() + 1];
            rowData[0] = naoTerminal.toString();

            for (int i = 1; i < rowData.length; i++) {
                Symbol terminal = gramatica.getTerminais().get(i - 1);
                List<Symbol> producao = gramatica.getTabelaPreditiva().getRegra(naoTerminal, terminal);
                rowData[i] = producao != null ? producao.toString() : "-";
            }
            model.addRow(rowData);
        }

        tabelaPreditiva.setModel(model);

        // Centralizar o texto nas células
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabelaPreditiva.getColumnCount(); i++) {
            tabelaPreditiva.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}
