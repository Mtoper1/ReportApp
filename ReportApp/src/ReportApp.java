import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ReportApp extends JFrame {
    private JPanel mainPanel;
    private JTextArea reportArea;
    private JButton generateButton;
    private JProgressBar progressBar;

    public ReportApp() {
        setTitle("INF.04 - Generator Raportów");
        setContentPane(mainPanel);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Plik");
        JMenuItem saveItem = new JMenuItem("Zapisz Raport");
        JMenuItem exitItem = new JMenuItem("Wyjście");

        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveReport());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        generateButton.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        generateButton.setEnabled(false);
        reportArea.setText("");
        progressBar.setValue(0);

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            protected Void doInBackground() throws Exception {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(300);
                    publish("Wiersz " + i + ": dane raportu\n");
                }
                return null;
            }

            protected void process(List<String> lines) {
                for (String line : lines) {
                    reportArea.append(line);
                    progressBar.setValue(progressBar.getValue() + 10);
                }
            }

            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ReportApp.this, "Błąd generowania raportu.");
                }
                generateButton.setEnabled(true);
            }
        };
        worker.execute();
    }

    private void saveReport() {
        if (reportArea.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Najpierw wygeneruj lub wpisz raport.",
                    "Brak raportu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Pliki tekstowe (*.txt)", "txt"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(this, "Nadpisać istniejący plik?",
                "Zapis raportu", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            Files.writeString(file.toPath(), reportArea.getText());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Nie udało się zapisać pliku.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportApp().setVisible(true));
    }
}
