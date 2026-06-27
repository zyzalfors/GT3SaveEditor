package GT3SaveEditor;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import GT3SaveEditor.GT3Save.*;

public class Form extends JFrame {
    private static final String _title = "GT3 Save Editor";
    private static final String[] _labels = new String[] {"Path:", "Checksum:", "Days:", "Races:", "Wins:", "Money:", "Prize:", "Car count:", "Trophies:", "Bonus cars:", "Language:"};
    private JTextField[] _texts = new JTextField[_labels.length - 1];
    private JComboBox<String> _langCombo;
    private JTable _carsTable = new JTable(new DefaultTableModel(new String[] {"Code", "Data"}, 0));
    private ArrayList<JComboBox<String>> _licCombos = new ArrayList<JComboBox<String>>();
    private ArrayList<JComboBox<String>> _carEvCombos = new ArrayList<JComboBox<String>>();
    private ArrayList<JComboBox<String>> _arcEvCombos = new ArrayList<JComboBox<String>>();
    private JButton _allGoldLic;
    private JButton _allGoldCarEv;
    private JButton _allHardArcEv;
    private JMenuItem _open;
    private JMenuItem _update;
    private JMenuItem _close;
    private GT3Save _save;

    public Form(String path) {
        super(_title);
        BuildUI();
        PrintData(path);
    }

    private void BuildUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}

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

        JTabbedPane pane = new JTabbedPane();
        JPanel genPanel = new JPanel(null);

        int i = 0;
        for(i = 0; i < _texts.length; i++) {
            JLabel label = new JLabel(_labels[i]);
            label.setBounds(10, 5 + i * 30, 80, 20);
            genPanel.add(label);

            JTextField text = new JTextField();
            text.setBounds(80, 5 + i * 30, 300, 20);
            text.setEnabled(false);
            genPanel.add(text);
            _texts[i] = text;
        }

        ArrayList<String> langs = new ArrayList<String>(GT3Save.languages.keySet());
        Collections.sort(langs);

        JLabel langLabel = new JLabel(_labels[i]);
        langLabel.setBounds(10, 5 + i * 30, 80, 20);
        genPanel.add(langLabel);

        _langCombo = new JComboBox<String>(langs.toArray(new String[0]));
        _langCombo.setBounds(80, 5 + i * 30, 300, 20);
        _langCombo.setEnabled(false);
        _langCombo.setFont(_langCombo.getFont().deriveFont(Font.PLAIN));
        genPanel.add(_langCombo);

        JPanel carsPanel = new JPanel(new BorderLayout());
        carsPanel.add(new JScrollPane(_carsTable));

        JPanel licPanel = new JPanel(null);

        ArrayList<String> licProg = new ArrayList<String>(GT3Save.licenseProgress.keySet());
        Collections.sort(licProg);

        for(i = 0; i < GT3Save.licenses.length; i++) {
            JLabel label = new JLabel(GT3Save.licenses[i]);
            label.setBounds(10, 5 + i * 30, 80, 20);
            licPanel.add(label);

            for(int j = 0; j < GT3Save.testsPerLicense; j++) {
                JComboBox<String> combo = new JComboBox<String>(licProg.toArray(new String[0]));
                combo.setBounds(30 + j * 80, 5 + i * 30, 70, 20);
                combo.setEnabled(false);
                licPanel.add(combo);
                _licCombos.add(combo);
            }
        }

        _allGoldLic = new JButton("All gold");
        _allGoldLic.setBounds(10, 5 + i * 30, 80, 20);
        _allGoldLic.setEnabled(false);
        licPanel.add(_allGoldLic);

        JPanel carEvPanel = new JPanel(null);

        ArrayList<String> carEvProg = new ArrayList<String>(GT3Save.careerProgress.keySet());
        Collections.sort(carEvProg);

        for(i = 0; i < GT3Save.careerEventCount / 14; i++) {
            for(int j = 0; j < GT3Save.careerEventCount / 26; j++) {
                JComboBox<String> combo = new JComboBox<String>(carEvProg.toArray(new String[0]));
                combo.setBounds(10 + j * 80, 5 + i * 30, 70, 20);
                combo.setEnabled(false);
                carEvPanel.add(combo);
                _carEvCombos.add(combo);
            }
        }

        _allGoldCarEv = new JButton("All gold");
        _allGoldCarEv.setBounds(10, 5 + i * 30, 80, 20);
        _allGoldCarEv.setEnabled(false);
        carEvPanel.add(_allGoldCarEv);

        JPanel arcEvPanel = new JPanel(null);

        for(i = 0; i < GT3Save.arcadeTracks.length / 2; i++) {
            for(int j = 0; j < GT3Save.arcadeTracks.length / 17; j++) {
                JLabel label = new JLabel(GT3Save.arcadeTracks[i * GT3Save.arcadeTracks.length / 17 + j] + ":");
                label.setBounds(10 + j * 300, 5 + i * 30, 165, 20);
                arcEvPanel.add(label);

                JComboBox<String> combo = new JComboBox<String>(GT3Save.arcadeProgress);
                combo.setBounds(160 + j * 325, 5 + i * 30, 70, 20);
                combo.setEnabled(false);
                arcEvPanel.add(combo);
                _arcEvCombos.add(combo);
            }
        }

        _allHardArcEv = new JButton("All hard");
        _allHardArcEv.setBounds(10, 5 + i * 30, 80, 20);
        _allHardArcEv.setEnabled(false);
        arcEvPanel.add(_allHardArcEv);

        JLabel areaLabel = new JLabel("Area:");
        areaLabel.setBounds(10, 5 + (++i) * 30, 70, 20);
        arcEvPanel.add(areaLabel);

        JComboBox<String> areaCombo = new JComboBox<String>(GT3Save.arcadeAreas);
        areaCombo.setBounds(160, 5 + i * 30, 70, 20);
        areaCombo.setEnabled(false);
        arcEvPanel.add(areaCombo);
        _arcEvCombos.add(areaCombo);

        pane.addTab("General", genPanel);
        pane.addTab("Cars", carsPanel);
        pane.addTab("Licenses", licPanel);
        pane.addTab("Career events", carEvPanel);
        pane.addTab("Arcade events", arcEvPanel);

        add(pane);
        setJMenuBar(menuBar);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setVisible(true);
        AddEventHandlers();
    }

    private void AddEventHandlers() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        _open.addActionListener((ActionEvent e) -> OpenSave());
        _update.addActionListener((ActionEvent e) -> UpdateSave());
        _close.addActionListener((ActionEvent e) -> CloseSave());
        _allGoldLic.addActionListener((ActionEvent e) -> AllGoldLic());
        _allGoldCarEv.addActionListener((ActionEvent e) -> AllGoldCarEv());
        _allHardArcEv.addActionListener((ActionEvent e) -> AllHardArcEv());
    }

    private void OpenSave() {
        JFileChooser chooser = new JFileChooser();
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            PrintData(chooser.getSelectedFile().getAbsolutePath());
    }

    private void PrintData(String path) {
        try {
            if(path == null) return;

            _save = new GT3Save(path);
            if(!_save.CheckCrc32())
                JOptionPane.showMessageDialog(null, "Invalid checksum", "Error", JOptionPane.INFORMATION_MESSAGE);

            _texts[0].setEnabled(true);
            _texts[0].setEditable(false);
            _texts[0].setText(path);

            int checksum = _save.GetInt(VALUE.CRC32);
            _texts[1].setEnabled(true);
            _texts[1].setEditable(false);
            _texts[1].setText(String.format("%08X", checksum));

            int days = _save.GetInt(VALUE.DAYS);
            _texts[2].setEnabled(true);
            _texts[2].setText(String.valueOf(days));

            int races = _save.GetInt(VALUE.RACES);
            _texts[3].setEnabled(true);
            _texts[3].setText(String.valueOf(races));

            int wins = _save.GetInt(VALUE.WINS);
            _texts[4].setEnabled(true);
            _texts[4].setText(String.valueOf(wins));

            long money = _save.GetLong(VALUE.MONEY);
            _texts[5].setEnabled(true);
            _texts[5].setText(String.valueOf(money));

            long prize = _save.GetLong(VALUE.PRIZE);
            _texts[6].setEnabled(true);
            _texts[6].setText(String.valueOf(prize));

            int carCount = _save.GetInt(VALUE.CAR_COUNT);
            _texts[7].setEnabled(true);
            _texts[7].setEditable(false);
            _texts[7].setText(String.valueOf(carCount));

            int trophies = _save.GetInt(VALUE.TROPHIES);
            _texts[8].setEnabled(true);
            _texts[8].setText(String.valueOf(trophies));

            int bonusCars = _save.GetInt(VALUE.BONUS_CARS);
            _texts[9].setEnabled(true);
            _texts[9].setText(String.valueOf(bonusCars));

            String lang = _save.GetStr(VALUE.LANGUAGE);
            _langCombo.setEnabled(true);
            _langCombo.setSelectedItem(lang);

            DefaultTableModel model = (DefaultTableModel) _carsTable.getModel();
            model.setRowCount(0);
            for(Object[] car : _save.GetCars())
                model.addRow(car);

            String[] licProg = _save.GetLicenses();
            for(int i = 0; i < licProg.length; i++) {
                _licCombos.get(i).setEnabled(true);
                _licCombos.get(i).setSelectedItem(licProg[i]);
            }
            _allGoldLic.setEnabled(true);

            String[] carEvProg = _save.GetCareerEvents();
            for(int i = 0; i < carEvProg.length; i++) {
                _carEvCombos.get(i).setEnabled(true);
                _carEvCombos.get(i).setSelectedItem(carEvProg[i]);
            }
            _allGoldCarEv.setEnabled(true);

            String[] arcEvProg = _save.GetArcadeEvents();
            for(int i = 0; i < arcEvProg.length; i++) {
                _arcEvCombos.get(i).setEnabled(true);
                _arcEvCombos.get(i).setSelectedItem(arcEvProg[i]);
            }
            _allHardArcEv.setEnabled(true);

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

            int days = Integer.valueOf(_texts[2].getText());
            _save.UpdateInt(VALUE.DAYS, days);

            int races = Integer.valueOf(_texts[3].getText());
            _save.UpdateInt(VALUE.RACES, races);

            int wins = Integer.valueOf(_texts[4].getText());
            _save.UpdateInt(VALUE.WINS, wins);

            long money = Long.valueOf(_texts[5].getText());
            _save.UpdateLong(VALUE.MONEY, money);

            long prize = Long.valueOf(_texts[6].getText());
            _save.UpdateLong(VALUE.PRIZE, prize);

            int trophies = Integer.valueOf(_texts[8].getText());
            _save.UpdateInt(VALUE.TROPHIES, trophies);

            int bonusCars = Integer.valueOf(_texts[9].getText());
            _save.UpdateInt(VALUE.BONUS_CARS, bonusCars);

            String lang = (String) _langCombo.getSelectedItem();
            _save.UpdateStr(VALUE.LANGUAGE, lang);

            DefaultTableModel model = (DefaultTableModel) _carsTable.getModel();
            StringBuilder car = new StringBuilder();
            for(int i = 0; i < model.getRowCount(); i++) {
                car.append(model.getValueAt(i, 0));
                car.append(model.getValueAt(i, 1));
                _save.UpdateCar(i, car.toString());
            }

            String[] licProg = new String[_licCombos.size()];
            for(int i = 0; i < licProg.length; i++)
                licProg[i] = (String) _licCombos.get(i).getSelectedItem();
            _save.UpdateLicenses(licProg);

            String[] carEvProg = new String[_carEvCombos.size()];
            for(int i = 0; i < carEvProg.length; i++)
                carEvProg[i] = (String) _carEvCombos.get(i).getSelectedItem();
            _save.UpdateCareerEvents(carEvProg);

            String[] arcEvProg = new String[_arcEvCombos.size()];
            for(int i = 0; i < arcEvProg.length; i++)
                arcEvProg[i] = (String) _arcEvCombos.get(i).getSelectedItem();
            _save.UpdateArcadeEvents(arcEvProg);

            _save.Update();
            JOptionPane.showMessageDialog(null, "Save updated", "Info", JOptionPane.INFORMATION_MESSAGE);
            RefreshChecksum();
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void RefreshChecksum() {
        int checksum = _save.GetInt(VALUE.CRC32);
        _texts[1].setText(String.format("%08X", checksum));
    }

    private void AllGoldLic() {
        for(JComboBox<String> combo : _licCombos)
            combo.setSelectedItem("Gold");
    }

    private void AllGoldCarEv() {
        for(JComboBox<String> combo : _carEvCombos)
            combo.setSelectedItem("Gold");
    }

    private void AllHardArcEv() {
        for(JComboBox<String> combo : _arcEvCombos)
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
        ((DefaultTableModel) _carsTable.getModel()).setRowCount(0);

        for(JComboBox<String> combo : _licCombos) {
            combo.setSelectedItem("");
            combo.setEnabled(false);
        }
        _allGoldLic.setEnabled(false);

        for(JComboBox<String> combo : _carEvCombos) {
            combo.setSelectedItem("");
            combo.setEnabled(false);
        }
        _allGoldCarEv.setEnabled(false);

        for(JComboBox<String> combo : _arcEvCombos) {
            combo.setSelectedItem("");
            combo.setEnabled(false);
        }
        _allHardArcEv.setEnabled(false);

        _save = null;
    }

    private void CloseSave() {
        UpdateSave();
        ClearData();
    }
}