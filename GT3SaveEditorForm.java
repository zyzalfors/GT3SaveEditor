package GT3SaveEditor;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import GT3SaveEditor.GT3Save.*;

public class GT3SaveEditorForm extends JFrame {
    private static final String _title = "GT3 Save Editor";
    private ArrayList<JTextField> _texts = new ArrayList<JTextField>();
    private JComboBox<String> _langCombo;
    private JCheckBox _GTCMoneyCheck;
    private JTable _carCarsTable;
    private JMenuItem _copyCarCar;
    private JMenuItem _pasteCarCar;
    private JMenuItem _deleteCarCars;
    private JMenuItem _exportCarCars;
    private JMenuItem _importCarCars;
    private String[] _carData;
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

    public GT3SaveEditorForm(String path) {
        super(_title);
        InitGUI();
        PrintSave(path);
    }

    private void InitGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}

        JTabbedPane pane = new JTabbedPane();
        pane.addTab("General", InitGeneralPanel());
        pane.addTab("Career status", InitCareerStatusPanel());
        pane.addTab("Career cars", InitCareerCarsPanel());
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
        String[] labels = new String[] {"Path", "CRC32", "Language", "GTC money"};
        JPanel panel = new JPanel(null);

        int i;
        for(i = 0; i < labels.length - 2; i++) {
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
        _langCombo.setSelectedIndex(-1);
        _langCombo.setEnabled(false);
        panel.add(_langCombo);

        i++;
        label = new JLabel(labels[i] + ":");
        label.setBounds(10, 5 + i * 30, 80, 20);
        panel.add(label);

        _GTCMoneyCheck = new JCheckBox("");
        _GTCMoneyCheck.setBounds(80, 5 + i * 30, 300, 20);
        _GTCMoneyCheck.setEnabled(false);
        panel.add(_GTCMoneyCheck);

        panel.setPreferredSize(new Dimension(400, 35 + labels.length * 30));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel InitCareerStatusPanel() {
        String[] labels = new String[] {"Days", "Races", "Wins", "Money", "Prize", "Mileage", "Car count", "Trophies", "Bonus cars"};
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

        panel.setPreferredSize(new Dimension(400, 35 + labels.length * 30));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel InitCareerCarsPanel() {
        ArrayList<String> columns = new ArrayList<String>();
        columns.addAll(Arrays.asList(GT3Save.carInfos));
        columns.addAll(Arrays.asList(GT3Save.carParts));
        columns.addAll(Arrays.asList(GT3Save.carSettings));

        JPopupMenu popupMenu = new JPopupMenu();

        _copyCarCar = new JMenuItem("Copy car");
        _copyCarCar.setEnabled(false);

        _pasteCarCar = new JMenuItem("Paste car");
        _pasteCarCar.setEnabled(false);

        _deleteCarCars = new JMenuItem("Delete cars");
        _deleteCarCars.setEnabled(false);

        _exportCarCars = new JMenuItem("Export cars");
        _exportCarCars.setEnabled(false);

        _importCarCars = new JMenuItem("Import cars");
        _importCarCars.setEnabled(false);

        popupMenu.add(_copyCarCar);
        popupMenu.add(_pasteCarCar);
        popupMenu.add(_deleteCarCars);
        popupMenu.add(_exportCarCars);
        popupMenu.add(_importCarCars);

        _carCarsTable = new JTable(new DefaultTableModel(columns.toArray(), 0));
        _carCarsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        _carCarsTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(_carCarsTable);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setComponentPopupMenu(popupMenu);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

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
                combo.setBounds(90 + j * 80, 5 + i * 30, 70, 20);
                combo.setSelectedIndex(-1);
                combo.setEnabled(false);
                panel.add(combo);
                _carLicProgCombos.add(combo);
            }
        }

        _allGoldCarLicProg = new JButton("All gold");
        _allGoldCarLicProg.setBounds(10, 5 + i * 30, 80, 20);
        _allGoldCarLicProg.setEnabled(false);
        panel.add(_allGoldCarLicProg);

        int contentWidth = Math.max(300, 90 + GT3Save.testsPerLicense * 80);
        int contentHeight = Math.max(100, 35 + (GT3Save.careerLicenses.length + 1) * 30);
        panel.setPreferredSize(new Dimension(contentWidth, contentHeight));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel InitCareerEventProgressPanel() {
        JPanel panel = new JPanel(null);

        ArrayList<String> prog = new ArrayList<String>(GT3Save.careerEventProgress.keySet());
        Collections.sort(prog);

        int rows = GT3Save.careerEventCount / 14;
        int cols = GT3Save.careerEventCount / 26;
        int comboWidth = 70;
        int comboSpacingX = 80;
        int comboHeight = 20;
        int comboSpacingY = 30;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                JComboBox<String> combo = new JComboBox<String>(prog.toArray(new String[0]));
                combo.setBounds(10 + j * comboSpacingX, 5 + i * comboSpacingY, comboWidth, comboHeight);
                combo.setSelectedIndex(-1);
                combo.setEnabled(false);
                panel.add(combo);
                _carEvProgCombos.add(combo);
            }
        }

        _allGoldCarEvProg = new JButton("All gold");
        _allGoldCarEvProg.setBounds(10, 5 + rows * comboSpacingY, 80, 20);
        _allGoldCarEvProg.setEnabled(false);
        panel.add(_allGoldCarEvProg);

        int contentWidth = 20 + cols * comboSpacingX;
        int contentHeight = 35 + rows * comboSpacingY;
        panel.setPreferredSize(new Dimension(contentWidth, contentHeight));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private JPanel InitArcadeProgressPanel() {
        JPanel panel = new JPanel(null);

        ArrayList<String> prog = new ArrayList<String>(GT3Save.arcadeEventProgress.keySet());
        Collections.sort(prog);

        int trackCount = GT3Save.arcadeTracks.length;
        int cols = 2;
        int rows = (trackCount + cols - 1) / cols;
        int labelWidth = 165;
        int comboWidth = 80;
        int columnWidth = 325;
        int rowHeight = 30;
        int trackIndex = 0;

        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols && trackIndex < trackCount; j++) {
                JLabel label = new JLabel(GT3Save.arcadeTracks[trackIndex] + ":");
                label.setBounds(10 + j * columnWidth, 5 + i * rowHeight, labelWidth, 20);
                panel.add(label);

                JComboBox<String> combo = new JComboBox<String>(prog.toArray(new String[0]));
                combo.setBounds(160 + j * columnWidth, 5 + i * rowHeight, comboWidth, 20);
                combo.setSelectedIndex(-1);
                combo.setEnabled(false);
                panel.add(combo);
                _arcProgCombos.add(combo);
                trackIndex++;
            }
        }

        int currentRow = rows;
        _allHardArcEvProg = new JButton("All hard");
        _allHardArcEvProg.setBounds(10, 5 + currentRow * rowHeight, 80, 20);
        _allHardArcEvProg.setEnabled(false);
        panel.add(_allHardArcEvProg);

        prog = new ArrayList<String>(GT3Save.arcadeTracksProgress.keySet());
        Collections.sort(prog);
        currentRow++;

        JLabel bonTracksLabel = new JLabel("Bonus tracks:");
        bonTracksLabel.setBounds(10, 5 + currentRow * rowHeight, 120, 20);
        panel.add(bonTracksLabel);

        JComboBox<String> bonTracksCombo = new JComboBox<String>(prog.toArray(new String[0]));

        bonTracksCombo.setBounds(160, 5 + currentRow * rowHeight, comboWidth, 20);
        bonTracksCombo.setSelectedIndex(-1);
        bonTracksCombo.setEnabled(false);

        panel.add(bonTracksCombo);
        _arcProgCombos.add(bonTracksCombo);

        prog = new ArrayList<String>(GT3Save.arcadeCarsProgress.keySet());
        Collections.sort(prog);
        currentRow++;

        JLabel bonCarsLabel = new JLabel("Bonus cars:");
        bonCarsLabel.setBounds(10, 5 + currentRow * rowHeight, 120, 20);
        panel.add(bonCarsLabel);

        JComboBox<String> bonCarsCombo = new JComboBox<String>(prog.toArray(new String[0]));
        bonCarsCombo.setBounds(160, 5 + currentRow * rowHeight, comboWidth, 20);
        bonCarsCombo.setSelectedIndex(-1);
        bonCarsCombo.setEnabled(false);

        panel.add(bonCarsCombo);
        _arcProgCombos.add(bonCarsCombo);

        int contentWidth = 20 + cols * columnWidth;
        int contentHeight = 35 + (currentRow + 1) * rowHeight;
        panel.setPreferredSize(new Dimension(contentWidth, contentHeight));

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel container = new JPanel(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);

        return container;
    }

    private void AddEventHandlers() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) { OnClose(e); }
        });
        _open.addActionListener((ActionEvent e) -> OpenSave());
        _update.addActionListener((ActionEvent e) -> UpdateSave());
        _close.addActionListener((ActionEvent e) -> CloseSave());
        _allGoldCarLicProg.addActionListener((ActionEvent e) -> AllGoldCareerLicenseProgress());
        _allGoldCarEvProg.addActionListener((ActionEvent e) -> AllGoldCareerEventProgress());
        _allHardArcEvProg.addActionListener((ActionEvent e) -> AllHardArcadeEventProgress());
        _copyCarCar.addActionListener((ActionEvent e) -> CopyCareerCar());
        _pasteCarCar.addActionListener((ActionEvent e) -> PasteCareerCar());
        _deleteCarCars.addActionListener((ActionEvent e) -> DeleteCareerCar());
        _exportCarCars.addActionListener((ActionEvent e) -> ExportCareerCars());
        _importCarCars.addActionListener((ActionEvent e) -> ImportCareerCars());
    }

    private boolean AskForLoadedSave() {
        int res = JOptionPane.showOptionDialog(this, "Update current save?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

        switch(res) {
            case JOptionPane.CLOSED_OPTION:
                return false;

            case JOptionPane.YES_OPTION:
                return UpdateSave();

            default:
        }

        return true;
    }

    private void OnClose(WindowEvent e) {
        if(_save != null)
            if(!AskForLoadedSave()) return;
        dispose();
    }

    private void OpenSave() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Open save");
        if(chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        if(_save != null)
            if(!AskForLoadedSave()) return;

        PrintSave(chooser.getSelectedFile().getAbsolutePath());
    }

    private void PrintSave(String path) {
        try {
            if(path == null) return;

            _save = new GT3Save(path);
            if(!_save.ValidCRC32())
                JOptionPane.showMessageDialog(this, "Invalid CRC32", "Info", JOptionPane.INFORMATION_MESSAGE);

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

            double mileage = _save.GetDouble(VALUE.MILEAGE);
            _texts.get(7).setEnabled(true);
            _texts.get(7).setText(String.valueOf(mileage));

            int carCount = _save.GetInt(VALUE.CAR_COUNT);
            _texts.get(8).setEnabled(true);
            _texts.get(8).setEditable(false);
            _texts.get(8).setText(String.valueOf(carCount));

            int trophies = _save.GetInt(VALUE.TROPHIES);
            _texts.get(9).setEnabled(true);
            _texts.get(9).setText(String.valueOf(trophies));

            int bonusCars = _save.GetInt(VALUE.BONUS_CARS);
            _texts.get(10).setEnabled(true);
            _texts.get(10).setText(String.valueOf(bonusCars));

            String lang = _save.GetStr(VALUE.LANGUAGE);
            _langCombo.setEnabled(true);
            _langCombo.setSelectedItem(lang);

            boolean GTCMoney = _save.GetBool(VALUE.GTCMONEY);
            _GTCMoneyCheck.setEnabled(true);
            _GTCMoneyCheck.setSelected(GTCMoney);

            DefaultTableModel model = (DefaultTableModel) _carCarsTable.getModel();
            model.setRowCount(0);
            for(Object[] car : _save.GetCareerCars())
                model.addRow(car);

            int rowCount = _carCarsTable.getRowCount();
            _copyCarCar.setEnabled(rowCount > 0);
            _pasteCarCar.setEnabled(false);
            _deleteCarCars.setEnabled(rowCount > 0);
            _exportCarCars.setEnabled(rowCount > 0);
            _importCarCars.setEnabled(true);
            _carData = new String[_carCarsTable.getColumnCount()];

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
            ClearData();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean UpdateSave() {
        try {
            if(_save == null) return false;

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

            double mileage = Double.valueOf(_texts.get(7).getText());
            _save.UpdateDouble(VALUE.MILEAGE, mileage);

            int trophies = Integer.valueOf(_texts.get(9).getText());
            _save.UpdateInt(VALUE.TROPHIES, trophies);

            int bonusCars = Integer.valueOf(_texts.get(10).getText());
            _save.UpdateInt(VALUE.BONUS_CARS, bonusCars);

            String lang = (String) _langCombo.getSelectedItem();
            _save.UpdateStr(VALUE.LANGUAGE, lang);

            boolean GTCMoney = _GTCMoneyCheck.isSelected();
            _save.UpdateBool(VALUE.GTCMONEY, GTCMoney);

            String[][] cars = GetCareerCars(null);
            _save.UpdateCareerCars(cars);

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

            int crc32 = _save.GetInt(VALUE.CRC32);
            _texts.get(1).setText(String.format("%08X", crc32));

            JOptionPane.showMessageDialog(this, "Save updated", "Info", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void AllGoldCareerLicenseProgress() {
        for(JComboBox<String> combo : _carLicProgCombos)
            combo.setSelectedItem("Gold");
    }

    private void AllGoldCareerEventProgress() {
        for(JComboBox<String> combo : _carEvProgCombos)
            combo.setSelectedItem("Gold");
    }

    private void AllHardArcadeEventProgress() {
        for(int i = 0; i <_arcProgCombos.size() - 2; i++)
            _arcProgCombos.get(i).setSelectedItem("Hard");
    }

    private String[][] GetCareerCars(ArrayList<Integer> rows) {
        DefaultTableModel model = (DefaultTableModel) _carCarsTable.getModel();
        ArrayList<ArrayList<String>> carsList = new ArrayList<ArrayList<String>>();

        for(int i = 0; i < model.getRowCount(); i++) {
            if(rows != null && rows.indexOf(i) == -1) continue;
            ArrayList<String> carData = new ArrayList<String>();

            for(int j = 0; j < model.getColumnCount(); j++)
                carData.add((String) model.getValueAt(i, j));

            carsList.add(carData);
        }

        String[][] cars = new String[carsList.size()][];
        for(int i = 0; i < carsList.size(); i++)
            cars[i] = carsList.get(i).toArray(new String[0]);

        return cars;
    }

    private void CopyCareerCar() {
        int row = _carCarsTable.getSelectedRow();
        if(row < 0 || _carData == null) return;

        for(int i = 0; i < _carCarsTable.getColumnCount(); i++)
            _carData[i] = (String) _carCarsTable.getValueAt(row, i);

        _pasteCarCar.setEnabled(true);
    }

    private void PasteCareerCar() {
        int row = _carCarsTable.getSelectedRow();
        if(row < 0 || _carData == null) return;

        DefaultTableModel model = (DefaultTableModel) _carCarsTable.getModel();
        for(int i = 0; i < _carCarsTable.getColumnCount(); i++)
            model.setValueAt(_carData[i], row, i);
    }

    private void DeleteCareerCar() {
        DefaultTableModel model = (DefaultTableModel) _carCarsTable.getModel();
        int[] rows = _carCarsTable.getSelectedRows();

        for(int i = rows.length - 1; i >= 0; i--)
            model.removeRow(_carCarsTable.convertRowIndexToModel(rows[i]));

        _texts.get(8).setText(String.valueOf(_carCarsTable.getRowCount()));
     }

    private void ExportCareerCars() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export cars");
            if(chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

            ArrayList<Integer> rows = new ArrayList<Integer>();
            for(int row : _carCarsTable.getSelectedRows())
                rows.add(row);

            String[][] cars = GetCareerCars(rows.size() > 0 ? rows : null);
            _save.ExportCareerCars(cars, chooser.getSelectedFile().getAbsolutePath());
            JOptionPane.showMessageDialog(this, String.format("%s car(s) exported", cars.length), "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ImportCareerCars() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Import cars");
            if(chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

            String[][] cars = _save.ImportCareerCars(chooser.getSelectedFile().getAbsolutePath());
            DefaultTableModel model = (DefaultTableModel) _carCarsTable.getModel();
            int numCars = 0;

            for(Object[] car : cars) {
                if(_carCarsTable.getRowCount() == GT3Save.maxCarCount) break;
                model.addRow(car);
                numCars++;
            }

            int rowCount = _carCarsTable.getRowCount();
            _copyCarCar.setEnabled(rowCount > 0);
            _deleteCarCars.setEnabled(rowCount > 0);
            _exportCarCars.setEnabled(rowCount > 0);

            _texts.get(8).setText(String.valueOf(rowCount));
            JOptionPane.showMessageDialog(this, String.format("%d car(s) imported", numCars), "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ClearData() {
        _update.setEnabled(false);
        _close.setEnabled(false);

        for(JTextField text : _texts) {
            text.setText(null);
            text.setEnabled(false);
        }

        _langCombo.setSelectedIndex(-1);
        _langCombo.setEnabled(false);

        _GTCMoneyCheck.setSelected(false);
        _GTCMoneyCheck.setEnabled(false);

        ((DefaultTableModel) _carCarsTable.getModel()).setRowCount(0);
        _copyCarCar.setEnabled(false);
        _pasteCarCar.setEnabled(false);
        _deleteCarCars.setEnabled(false);
        _exportCarCars.setEnabled(false);
        _importCarCars.setEnabled(false);
        _carData = null;

        for(JComboBox<String> combo : _carLicProgCombos) {
            combo.setSelectedIndex(-1);
            combo.setEnabled(false);
        }
        _allGoldCarLicProg.setEnabled(false);

        for(JComboBox<String> combo : _carEvProgCombos) {
            combo.setSelectedIndex(-1);
            combo.setEnabled(false);
        }
        _allGoldCarEvProg.setEnabled(false);

        for(JComboBox<String> combo : _arcProgCombos) {
            combo.setSelectedIndex(-1);
            combo.setEnabled(false);
        }
        _allHardArcEvProg.setEnabled(false);

        _save = null;
    }

    private void CloseSave() {
        if(UpdateSave()) ClearData();
    }
}