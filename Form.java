package GT3SaveEditor;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import GT3SaveEditor.GT3Save.*;

public class Form extends JFrame {
    private static final String _title = "GT3 Save Editor";
    private ArrayList<JTextField> _texts = new ArrayList<JTextField>();
    private JComboBox<String> _langCombo;
    private JTable _carGarTable;
    private ArrayList<JComboBox<String>> _carLicProgCombos = new ArrayList<JComboBox<String>>();
    private ArrayList<JComboBox<String>> _carEvProgCombos = new ArrayList<JComboBox<String>>();
    private ArrayList<JComboBox<String>> _arcProgCombos = new ArrayList<JComboBox<String>>();
    private JButton _allGoldCarLicProg;
    private JButton _allGoldCarEvProg;
    private JButton _allHardArcEvProg;
    private JMenuItem _open;
    private JMenuItem _update;
    private JMenuItem _close;
    private GT3Save _save;

    public Form(String path) {
        super(_title);
        InitUI();
        PrintSave(path);
    }

    private void InitUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("General", InitGeneralPanel());
        pane.addTab("Career status", InitCareerStatusPanel());
        pane.addTab("Career garage", InitCareerGaragePanel());
        pane.addTab("Career license progress", InitCareerLicenseProgressPanel());
        pane.addTab("Career event progress", InitCareerEventProgressPanel());
        pane.addTab("Arcade progress", InitArcadeProgressPanel());

        add(pane);
        setJMenuBar(InitMenu());
        setSize(900, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        AddEventHandlers();
    }

    private JMenuBar InitMenu() {
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

        return menuBar;
    }

    private JPanel InitGeneralPanel() {
        String[] labels = new String[] {"Path", "CRC32", "Language"};
        JPanel panel = new JPanel(null);

        int i;
        for(i = 0; i < labels.length - 1; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setBounds(10, 5 + i * 30, 80, 20);
            panel.add(label);

            JTextField text = new JTextField();
            text.setBounds(80, 5 + i * 30, 300, 20);
            text.setEnabled(false);
            panel.add(text);
            _texts.add(text);
        }

        ArrayList<String> langs = new ArrayList<String>(GT3Save.languages.keySet());
        Collections.sort(langs);

        JLabel label = new JLabel(labels[i] + ":");
        label.setBounds(10, 5 + i * 30, 80, 20);
        panel.add(label);

        _langCombo = new JComboBox<String>(langs.toArray(new String[0]));
        _langCombo.setBounds(80, 5 + i * 30, 300, 20);
        _langCombo.setEnabled(false);
        panel.add(_langCombo);

        return panel;
    }

    private JPanel InitCareerStatusPanel() {
        String[] labels = new String[] {"Days", "Races", "Wins", "Money", "Prize", "Car count", "Trophies", "Bonus cars"};
        JPanel panel = new JPanel(null);

        for(int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setBounds(10, 5 + i * 30, 80, 20);
            panel.add(label);

            JTextField text = new JTextField();
            text.setBounds(80, 5 + i * 30, 300, 20);
            text.setEnabled(false);
            panel.add(text);
            _texts.add(text);
        }

        return panel;
    }

    private JPanel InitCareerGaragePanel() {
        _carGarTable = new JTable(new DefaultTableModel(new String[] {"Code", "Data"}, 0));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(_carGarTable));
        return panel;
    }

    private JPanel InitCareerLicenseProgressPanel() {
        JPanel panel = new JPanel(null);

        ArrayList<String> prog = new ArrayList<String>(GT3Save.careerLicenseProgress.keySet());
        Collections.sort(prog);

        int i;
        for(i = 0; i < GT3Save.careerLicenses.length; i++) {
            JLabel label = new JLabel(GT3Save.careerLicenses[i] + ":");
            label.setBounds(10, 5 + i * 30, 80, 20);
            panel.add(label);

            for(int j = 0; j < GT3Save.testsPerLicense; j++) {
                JComboBox<String> combo = new JComboBox<String>(prog.toArray(new String[0]));
                combo.setBounds(30 + j * 80, 5 + i * 30, 70, 20);
                combo.setEnabled(false);
                panel.add(combo);
                _carLicProgCombos.add(combo);
            }
        }

        _allGoldCarLicProg = new JButton("All gold");
        _allGoldCarLicProg.setBounds(10, 5 + i * 30, 80, 20);
        _allGoldCarLicProg.setEnabled(false);
        panel.add(_allGoldCarLicProg);

        return panel;
    }

    private JPanel InitCareerEventProgressPanel() {
        JPanel panel = new JPanel(null);

        ArrayList<String> prog = new ArrayList<String>(GT3Save.careerEventProgress.keySet());
        Collections.sort(prog);

        int i;
        for(i = 0; i < GT3Save.careerEventCount / 14; i++) {
            for(int j = 0; j < GT3Save.careerEventCount / 26; j++) {
                JComboBox<String> combo = new JComboBox<String>(prog.toArray(new String[0]));
                combo.setBounds(10 + j * 80, 5 + i * 30, 70, 20);
                combo.setEnabled(false);
                panel.add(combo);
                _carEvProgCombos.add(combo);
            }
        }

        _allGoldCarEvProg = new JButton("All gold");
        _allGoldCarEvProg.setBounds(10, 5 + i * 30, 80, 20);
        _allGoldCarEvProg.setEnabled(false);
        panel.add(_allGoldCarEvProg);

        return panel;
    }

    private JPanel InitArcadeProgressPanel() {
        JPanel panel = new JPanel(null);

        ArrayList<String> prog = new ArrayList<String>(GT3Save.arcadeEventProgress.keySet());
        Collections.sort(prog);

        int i;
        for(i = 0; i < GT3Save.arcadeTracks.length / 2; i++) {
            for(int j = 0; j < GT3Save.arcadeTracks.length / 17; j++) {
                JLabel label = new JLabel(GT3Save.arcadeTracks[i * GT3Save.arcadeTracks.length / 17 + j] + ":");
                label.setBounds(10 + j * 300, 5 + i * 30, 165, 20);
                panel.add(label);

                JComboBox<String> combo = new JComboBox<String>(prog.toArray(new String[0]));
                combo.setBounds(160 + j * 325, 5 + i * 30, 80, 20);
                combo.setEnabled(false);
                panel.add(combo);
                _arcProgCombos.add(combo);
            }
        }

        _allHardArcEvProg = new JButton("All hard");
        _allHardArcEvProg.setBounds(10, 5 + i * 30, 80, 20);
        _allHardArcEvProg.setEnabled(false);
        panel.add(_allHardArcEvProg);

        prog = new ArrayList<String>(GT3Save.arcadeTracksProgress.keySet());
        Collections.sort(prog);

        JLabel bonTracksLabel = new JLabel("Bonus tracks:");
        bonTracksLabel.setBounds(10, 5 + (++i) * 30, 70, 20);
        panel.add(bonTracksLabel);

        JComboBox<String> bonTracksCombo = new JComboBox<String>(prog.toArray(new String[0]));
        bonTracksCombo.setBounds(160, 5 + i * 30, 80, 20);
        bonTracksCombo.setEnabled(false);
        panel.add(bonTracksCombo);
        _arcProgCombos.add(bonTracksCombo);

        prog = new ArrayList<String>(GT3Save.arcadeCarsProgress.keySet());
        Collections.sort(prog);

        JLabel bonCarsLabel = new JLabel("Bonus cars:");
        bonCarsLabel.setBounds(10, 5 + (++i) * 30, 70, 20);
        panel.add(bonCarsLabel);

        JComboBox<String> bonCarsCombo = new JComboBox<String>(prog.toArray(new String[0]));
        bonCarsCombo.setBounds(160, 5 + i * 30, 80, 20);
        bonCarsCombo.setEnabled(false);
        panel.add(bonCarsCombo);
        _arcProgCombos.add(bonCarsCombo);

        return panel;
    }

    private void AddEventHandlers() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _open.addActionListener((ActionEvent e) -> OpenSave());
        _update.addActionListener((ActionEvent e) -> UpdateSave());
        _close.addActionListener((ActionEvent e) -> CloseSave());
        _allGoldCarLicProg.addActionListener((ActionEvent e) -> AllGoldCarLicProg());
        _allGoldCarEvProg.addActionListener((ActionEvent e) -> AllGoldCarEvProg());
        _allHardArcEvProg.addActionListener((ActionEvent e) -> AllHardArcEvProg());
    }

    private void OpenSave() {
        JFileChooser chooser = new JFileChooser();
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            PrintSave(chooser.getSelectedFile().getAbsolutePath());
    }

    private void PrintSave(String path) {
        try {
            if(path == null) return;

            _save = new GT3Save(path);
            if(!_save.ValidCRC32())
                JOptionPane.showMessageDialog(null, "Invalid CRC32", "Info", JOptionPane.INFORMATION_MESSAGE);

            _texts.get(0).setEnabled(true);
            _texts.get(0).setEditable(false);
            _texts.get(0).setText(path);

            int crc32 = _save.GetInt(VALUE.CRC32);
            _texts.get(1).setEnabled(true);
            _texts.get(1).setEditable(false);
            _texts.get(1).setText(String.format("%08X", crc32));

            int days = _save.GetInt(VALUE.DAYS);
            _texts.get(2).setEnabled(true);
            _texts.get(2).setText(String.valueOf(days));

            int races = _save.GetInt(VALUE.RACES);
            _texts.get(3).setEnabled(true);
            _texts.get(3).setText(String.valueOf(races));

            int wins = _save.GetInt(VALUE.WINS);
            _texts.get(4).setEnabled(true);
            _texts.get(4).setText(String.valueOf(wins));

            long money = _save.GetLong(VALUE.MONEY);
            _texts.get(5).setEnabled(true);
            _texts.get(5).setText(String.valueOf(money));

            long prize = _save.GetLong(VALUE.PRIZE);
            _texts.get(6).setEnabled(true);
            _texts.get(6).setText(String.valueOf(prize));

            int carCount = _save.GetInt(VALUE.CAR_COUNT);
            _texts.get(7).setEnabled(true);
            _texts.get(7).setEditable(false);
            _texts.get(7).setText(String.valueOf(carCount));

            int trophies = _save.GetInt(VALUE.TROPHIES);
            _texts.get(8).setEnabled(true);
            _texts.get(8).setText(String.valueOf(trophies));

            int bonusCars = _save.GetInt(VALUE.BONUS_CARS);
            _texts.get(9).setEnabled(true);
            _texts.get(9).setText(String.valueOf(bonusCars));

            String lang = _save.GetStr(VALUE.LANGUAGE);
            _langCombo.setEnabled(true);
            _langCombo.setSelectedItem(lang);

            DefaultTableModel model = (DefaultTableModel) _carGarTable.getModel();
            model.setRowCount(0);
            for(Object[] car : _save.GetCareerGarage())
                model.addRow(car);

            String[] carLicProg = _save.GetCareerLicenseProgress();
            for(int i = 0; i < carLicProg.length; i++) {
                _carLicProgCombos.get(i).setEnabled(true);
                _carLicProgCombos.get(i).setSelectedItem(carLicProg[i]);
            }
            _allGoldCarLicProg.setEnabled(true);

            String[] carEvProg = _save.GetCareerEventProgress();
            for(int i = 0; i < carEvProg.length; i++) {
                _carEvProgCombos.get(i).setEnabled(true);
                _carEvProgCombos.get(i).setSelectedItem(carEvProg[i]);
            }
            _allGoldCarEvProg.setEnabled(true);

            String[] arcProg = _save.GetArcadeProgress();
            for(int i = 0; i < arcProg.length; i++) {
                _arcProgCombos.get(i).setEnabled(true);
                _arcProgCombos.get(i).setSelectedItem(arcProg[i]);
            }
            _allHardArcEvProg.setEnabled(true);

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

            int days = Integer.valueOf(_texts.get(2).getText());
            _save.UpdateInt(VALUE.DAYS, days);

            int races = Integer.valueOf(_texts.get(3).getText());
            _save.UpdateInt(VALUE.RACES, races);

            int wins = Integer.valueOf(_texts.get(4).getText());
            _save.UpdateInt(VALUE.WINS, wins);

            long money = Long.valueOf(_texts.get(5).getText());
            _save.UpdateLong(VALUE.MONEY, money);

            long prize = Long.valueOf(_texts.get(6).getText());
            _save.UpdateLong(VALUE.PRIZE, prize);

            int trophies = Integer.valueOf(_texts.get(8).getText());
            _save.UpdateInt(VALUE.TROPHIES, trophies);

            int bonusCars = Integer.valueOf(_texts.get(9).getText());
            _save.UpdateInt(VALUE.BONUS_CARS, bonusCars);

            String lang = (String) _langCombo.getSelectedItem();
            _save.UpdateStr(VALUE.LANGUAGE, lang);

            DefaultTableModel model = (DefaultTableModel) _carGarTable.getModel();
            StringBuilder car = new StringBuilder();
            for(int i = 0; i < model.getRowCount(); i++) {
                car.append(model.getValueAt(i, 0));
                car.append(model.getValueAt(i, 1));
                _save.UpdateCar(i, car.toString());
            }

            String[] carLicProg = new String[_carLicProgCombos.size()];
            for(int i = 0; i < carLicProg.length; i++)
                carLicProg[i] = (String) _carLicProgCombos.get(i).getSelectedItem();
            _save.UpdateCareerLicenseProgress(carLicProg);

            String[] carEvProg = new String[_carEvProgCombos.size()];
            for(int i = 0; i < carEvProg.length; i++)
                carEvProg[i] = (String) _carEvProgCombos.get(i).getSelectedItem();
            _save.UpdateCareerEventProgress(carEvProg);

            String[] arcProg = new String[_arcProgCombos.size()];
            for(int i = 0; i < arcProg.length; i++)
                arcProg[i] = (String) _arcProgCombos.get(i).getSelectedItem();
            _save.UpdateArcadeProgress(arcProg);

            _save.Update();

            JOptionPane.showMessageDialog(null, "Save updated", "Info", JOptionPane.INFORMATION_MESSAGE);
            UpdateCRC32();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void UpdateCRC32() {
        int crc32 = _save.GetInt(VALUE.CRC32);
        _texts.get(1).setText(String.format("%08X", crc32));
    }

    private void AllGoldCarLicProg() {
        for(JComboBox<String> combo : _carLicProgCombos)
            combo.setSelectedItem("Gold");
    }

    private void AllGoldCarEvProg() {
        for(JComboBox<String> combo : _carEvProgCombos)
            combo.setSelectedItem("Gold");
    }

    private void AllHardArcEvProg() {
        for(JComboBox<String> combo : _arcProgCombos)
            combo.setSelectedItem("Hard");
    }

    private void ClearData() {
        _update.setEnabled(false);
        _close.setEnabled(false);

        for(JTextField text : _texts) {
            text.setText(null);
            text.setEnabled(false);
        }

        _langCombo.setSelectedItem("");
        _langCombo.setEnabled(false);

        ((DefaultTableModel) _carGarTable.getModel()).setRowCount(0);

        for(JComboBox<String> combo : _carLicProgCombos) {
            combo.setSelectedItem("");
            combo.setEnabled(false);
        }
        _allGoldCarLicProg.setEnabled(false);

        for(JComboBox<String> combo : _carEvProgCombos) {
            combo.setSelectedItem("");
            combo.setEnabled(false);
        }
        _allGoldCarEvProg.setEnabled(false);

        for(JComboBox<String> combo : _arcProgCombos) {
            combo.setSelectedItem("");
            combo.setEnabled(false);
        }
        _allHardArcEvProg.setEnabled(false);

        _save = null;
    }

    private void CloseSave() {
        UpdateSave();
        ClearData();
    }
}