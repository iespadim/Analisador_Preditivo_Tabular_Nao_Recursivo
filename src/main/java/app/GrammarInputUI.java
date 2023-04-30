package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GrammarInputUI extends JFrame {
    private JTextArea grammarInput;
    private JComboBox<String> fileSelector;
    private JButton submitButton;

    public GrammarInputUI(Consumer<String> callback) {
        setTitle("Analisador Preditivo Tabular");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel para entrada de gramática
        JPanel grammarPanel = new JPanel(new BorderLayout());
        grammarInput = new JTextArea(10,0);
        grammarPanel.add(new JLabel("Escreva aqui sua gramática:"), BorderLayout.NORTH);
        grammarPanel.add(new JScrollPane(grammarInput), BorderLayout.CENTER);

        // Painel para seleção de arquivo
        JPanel filePanel = new JPanel(new BorderLayout());
        fileSelector = new JComboBox<>(listTxtFiles("input"));
        filePanel.add(new JLabel("Ou selecione um arquivo .txt (opcional):"), BorderLayout.NORTH);
        filePanel.add(fileSelector, BorderLayout.CENTER);

        // Painel para botão de envio
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitButton = new JButton("Enviar Dados de Entrada");
        submitButton.addActionListener(new SubmitButtonListener(callback));
        buttonPanel.add(submitButton);

        // Adiciona painéis ao JFrame
        add(grammarPanel, BorderLayout.NORTH);
        add(filePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String[] listTxtFiles(String folder) {
        File inputFolder = new File(folder);
        List<String> txtFiles = new ArrayList<>();
        txtFiles.add("Nenhum");
        if (inputFolder.isDirectory()) {
            txtFiles.addAll(Arrays.asList(inputFolder.list((dir, name) -> name.toLowerCase().endsWith(".txt"))));
        }
        return txtFiles.toArray(new String[0]);
    }

    private class SubmitButtonListener implements ActionListener {
        private final Consumer<String> callback;

        private SubmitButtonListener(Consumer<String> callback) {
            super();
            this.callback = callback;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String grammar = grammarInput.getText().trim();
            if (grammar.isEmpty()) {
                String selectedFile = (String) fileSelector.getSelectedItem();
                if (!selectedFile.equals("Nenhum")) {
                    try {
                        grammar = Files.lines(Paths.get("input", selectedFile)).collect(Collectors.joining("\n"));
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(GrammarInputUI.this,
                                "Erro ao ler o arquivo selecionado.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(GrammarInputUI.this,
                            "Por favor, insira uma gramática ou selecione um arquivo.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

           //processGrammar(grammar);
            callback.accept(grammar);
            dispose();
        }
    }

    // Implemente a lógica de processamento da gramática neste método
    private void processGrammar(String grammar) {
        // ...
        System.out.println(grammar);
    }

    public static void main(String[] args) {
        Consumer<String> oi = (String s) -> System.out.println(s);

        SwingUtilities.invokeLater
                (() -> new GrammarInputUI(oi ));
    }
}

