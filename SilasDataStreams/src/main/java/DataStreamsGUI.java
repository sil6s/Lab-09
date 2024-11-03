package main.java;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class DataStreamsGUI extends JFrame {
    private JTextArea originalTextArea;
    private JTextArea filteredTextArea;
    private JTextField searchField;
    private JButton loadButton;
    private JButton searchButton;
    private JButton quitButton;
    private final FileProcessor fileProcessor;

    public DataStreamsGUI() {
        setTitle("Data Streams Processor");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 240));

        fileProcessor = new FileProcessor();

        initComponents();
        addComponents();
        addListeners();
    }

    private void initComponents() {
        originalTextArea = new JTextArea();
        filteredTextArea = new JTextArea();
        searchField = new JTextField(20);
        loadButton = new JButton("Load File");
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);
        searchButton.setEnabled(false);
        originalTextArea.setLineWrap(true);
        filteredTextArea.setLineWrap(true);

        Font textFont = new Font("SansSerif", Font.PLAIN, 14);
        originalTextArea.setFont(textFont);
        filteredTextArea.setFont(textFont);
        searchField.setFont(textFont);

        styleButton(loadButton, new Color(70, 130, 180));
        styleButton(searchButton, new Color(60, 179, 113));
        styleButton(quitButton, new Color(205, 92, 92));
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
    }

    private void addComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel textAreasPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        textAreasPanel.setOpaque(false);
        textAreasPanel.add(createTextAreaPanel("Original Text", originalTextArea));
        textAreasPanel.add(createTextAreaPanel("Filtered Text", filteredTextArea));
        mainPanel.add(textAreasPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setOpaque(false);
        controlPanel.add(new JLabel("Search: "));
        controlPanel.add(searchField);
        controlPanel.add(loadButton);
        controlPanel.add(searchButton);
        controlPanel.add(quitButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createTextAreaPanel(String title, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 600));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setOpaque(false);
        return panel;
    }

    private void addListeners() {
        loadButton.addActionListener(this::loadFile);
        searchButton.addActionListener(this::searchFile);
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void loadFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.exists()) {
                JOptionPane.showMessageDialog(this, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String content = Files.readString(selectedFile.toPath(), StandardCharsets.UTF_8);
                if (content.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "File is empty.", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    originalTextArea.setText(content);
                    searchButton.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "File loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchFile(ActionEvent e) {
        String searchString = searchField.getText();
        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search string.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String originalContent = originalTextArea.getText();
        if (originalContent.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No content to search. Please load a file first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String filteredContent = fileProcessor.filterContent(originalContent, searchString);
            if (filteredContent.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matches found for: " + searchString, "Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                filteredTextArea.setText(filteredContent);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error filtering content: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            new DataStreamsGUI().setVisible(true);
        });
    }
}