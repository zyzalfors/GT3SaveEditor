package GT3SaveEditor;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

public class Form extends JFrame {
    private static final String title = "GT3 Save Editor";
    private static final String[] labels = new String[] {"Days:", "Races:", "Wins:", "Cash:", "Prize:", "Car count:", "Trophies:", "Bonus cars:", "Language:"};
    private JTextField[] _textFields = new JTextField[8];
    private JComboBox<String> _combo;
    private JMenuItem _open;
    private JMenuItem _update;
    private JMenuItem _close;
    private GT3Save _save;

    public Form(String path) {
        super(title);
        BuildUI();
        PrintData(path);
    }

    private void BuildUI() {
        _open = new JMenuItem("Open");

        _update = new JMenuItem("Update");
        _update.setEnabled(false);

        _close = new JMenuItem("Close");
        _close.setEnabled(false);

        JMenu menu = new JMenu("File");
        menu.add(_open);
        menu.add(_update);
        menu.add(_close);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        for(int i = 0; i < labels.length - 1; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setBounds(0, i * 30, 80, 20);
            add(label);

            JTextField textField = new JTextField();
            textField.setBounds(80, i * 30, 180, 20);
            textField.setEnabled(false);
            add(textField);

            _textFields[i] = textField;
        }

        int i = labels.length - 1;
        JLabel label = new JLabel(labels[i]);
        label.setBounds(0, i * 30, 80, 20);
        add(label);

        Set<String> langs = new HashSet<String>(GT3Save.languages.keySet());
        langs.add("Unknown");

        _combo = new JComboBox<>(langs.toArray(String[]::new));
        _combo.setBounds(80, i * 30, 180, 20);
        _combo.setEnabled(false);
        add(_combo);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}

        setJMenuBar(menuBar);
        getContentPane().setLayout(null);
        setSize(280, 330);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        AddEventHandlers();
    }

    private void AddEventHandlers() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _open.addActionListener((ActionEvent e) -> OpenSave());
        _update.addActionListener((ActionEvent e) -> UpdateSave());
        _close.addActionListener((ActionEvent e) -> CloseSave());
    }

    private void OpenSave() {
        JFileChooser chooser = new JFileChooser();
        int sel = chooser.showOpenDialog(null);
        if(sel == JFileChooser.APPROVE_OPTION) PrintData(chooser.getSelectedFile().getAbsolutePath());
    }

    private void PrintData(String path) {
        try {
            if(path == null) return;

            _save = new GT3Save(path);
            if(!_save.CheckCrc32()) JOptionPane.showMessageDialog(null, "Invalid checksum", "Error", JOptionPane.INFORMATION_MESSAGE);

            int days = _save.GetDays();
            _textFields[0].setEnabled(true);
            _textFields[0].setText(String.valueOf(days));

            int races = _save.GetRaces();
            _textFields[1].setEnabled(true);
            _textFields[1].setText(String.valueOf(races));

            int wins = _save.GetWins();
            _textFields[2].setEnabled(true);
            _textFields[2].setText(String.valueOf(wins));

            long cash = _save.GetCash();
            _textFields[3].setEnabled(true);
            _textFields[3].setText(String.valueOf(cash));

            long prize = _save.GetPrize();
            _textFields[4].setEnabled(true);
            _textFields[4].setText(String.valueOf(prize));

            int carCount = _save.GetCarCount();
            _textFields[5].setEnabled(true);
            _textFields[5].setEditable(false);
            _textFields[5].setText(String.valueOf(carCount));

            int trophies = _save.GetTrophies();
            _textFields[6].setEnabled(true);
            _textFields[6].setText(String.valueOf(trophies));

            int bonusCars = _save.GetBonusCars();
            _textFields[7].setEnabled(true);
            _textFields[7].setText(String.valueOf(bonusCars));

            String lang = _save.GetLang();
            _combo.setEnabled(true);
            _combo.setSelectedItem(lang);

            _update.setEnabled(true);
            _close.setEnabled(true);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ClearData();
        }
    }

    private void UpdateSave() {
        try {
            if(_save == null) return;

            int days = Integer.valueOf(_textFields[1].getText());
            _save.UpdateDays(days);

            int races = Integer.valueOf(_textFields[2].getText());
            _save.UpdateRaces(races);

            int wins = Integer.valueOf(_textFields[3].getText());
            _save.UpdateWins(wins);

            long cash = Long.valueOf(_textFields[4].getText());
            _save.UpdateCash(cash);

            long prize = Long.valueOf(_textFields[5].getText());
            _save.UpdatePrize(prize);

            int trophies = Integer.valueOf(_textFields[7].getText());
            _save.UpdateTrophies(trophies);

            int bonusCars = Integer.valueOf(_textFields[8].getText());
            _save.UpdateBonusCars(bonusCars);

            String lang = (String) _combo.getSelectedItem();
            _save.UpdateLang(lang);

            _save.Update();
            JOptionPane.showMessageDialog(null, "Save updated", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ClearData() {
        _update.setEnabled(false);
        _close.setEnabled(false);

        for(JTextField textField : _textFields) {
            textField.setText(null);
            textField.setEnabled(false);
        }

        _combo.setEnabled(false);
        _save = null;
    }

    private void CloseSave() {
        UpdateSave();
        ClearData();
    }

}
