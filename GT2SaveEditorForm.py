import tkinter
from tkinter import filedialog, messagebox, simpledialog, ttk
from widgets import Button, Checkbox, Combobox, PopupMenu, Tab, Table, Textbox
from GT2SaveParser import GT2SaveParser
from GT2Save import GT2Save

class GT2SaveEditorForm(tkinter.Tk):

    def __init__(self, path):
        super().__init__()
        self.data = {}
        self.initGUI()
        self.open(path, False)


    def initGUI(self):
        self.title("GT2 Save Editor")
        self.initMenu()
        self.initTabs()
        self.geometry("950x450")


    def initMenu(self):
        menuBar = tkinter.Menu(self)

        fileMenu = tkinter.Menu(menuBar, tearoff = False)
        fileMenu.add_command(label = "Open", command = lambda: self.open(None, True))
        fileMenu.add_command(label = "Update", state = "disabled", command = self.update)
        fileMenu.add_command(label = "Close", state = "disabled", command = self.close)
        self.fileMenu = fileMenu

        slotMenu = tkinter.Menu(menuBar, tearoff = False)
        slotMenu.add_command(label = "Change", state = "disabled", command = self.change)
        self.slotMenu = slotMenu

        menuBar.add_cascade(label = "File", menu = fileMenu)
        menuBar.add_cascade(label = "Slot", menu = slotMenu)
        self.config(menu = menuBar)


    def initTabs(self):
        nb = ttk.Notebook(self)
        nb.pack(expand = True, fill = "both")
        self.buttons = []
        self.initGeneralTab(nb)
        self.initStatusTab(nb)
        self.initCarsTab(nb)
        self.initLicenseProgressTab(nb)
        self.initEventProgressTab(nb)
        self.initArcadeProgress(nb)


    def initGeneralTab(self, nb):
        tab = Tab(nb, 300, 150)
        nb.add(tab, text = "General")
        vals = tuple(GT2Save.LANGUAGES.keys())

        for i, name in enumerate(["Path", "Serial", "Region", "CRC32", "Language"]):
            label = tkinter.Label(tab.content, text = f"{name}:")
            label.place(x = 10, y = 5 + i * 30)

            if name != "Language":
                text = Textbox(tab.content, 25)
                text.place(x = 80, y = 5 + i * 30)
                text.setState(False)
                self.data[name] = text

            else:
                combo = Combobox(tab.content, vals, 30)
                combo.place(x = 80, y = 5 + i * 30)
                combo.setState(False)
                self.data[name] = combo


    def initStatusTab(self, nb):
        tab = Tab(nb, 300, 250)
        nb.add(tab, text = "Status")

        for i, name in enumerate(["Days", "Races", "Wins", "Sum of best rankings", "Sum of rankings", "Prize", "Car count", "Money"]):
            label = tkinter.Label(tab.content, text = f"{name}:")
            label.place(x = 10, y = 5 + i * 30)

            text = Textbox(tab.content, 25)
            text.place(x = 140, y = 5 + i * 30)
            text.setState(False)
            self.data[name] = text


    def initCarsTab(self, nb):
        tab = ttk.Frame(nb)
        nb.add(tab, text = "Cars")
        self.carsTable = Table(tab, tuple(GT2Save.CAR_PROPERTIES.keys()))
        tab.rowconfigure(0, weight = 1)
        tab.columnconfigure(0, weight = 1)


    def initLicenseProgressTab(self, nb):
        tab = Tab(nb, 930, 210)
        nb.add(tab, text = "License progress")

        vals = tuple(GT2Save.LICENSE_PROGRESS.keys())
        k = 0
        self.data["License progress"] = {}

        for i, licName in enumerate(GT2Save.LICENSE_PROGRESS_OFFSETS):
            label = tkinter.Label(tab.content, text = f"{licName}:")
            label.place(x = 10, y = 5 + i * 30)
            self.data["License progress"][licName] = []

            for j in range(GT2Save.TESTS_PER_LICENSE):
                combo = Combobox(tab.content, vals, 10)
                combo.place(x = 80 + j * 85, y = 5 + i * 30)
                combo.setState(False)
                self.data["License progress"][licName].append(combo)

            k = i

        k += 1
        button = Button(tab.content, "All gold", self.allGoldLicenseProgress)
        button.place(x = 10, y = 5 + k * 30)
        button.setState(False)
        self.buttons.append(button)


    def allGoldLicenseProgress(self):
        for licName in self.data["License progress"]:
            for combo in self.data["License progress"][licName]:
                combo.setVal("Gold")


    def initEventProgressTab(self, nb):
        tab = Tab(nb, 700, 970)
        nb.add(tab, text = "Event progress")

        vals = tuple(GT2Save.EVENT_PROGRESS.keys())
        rowCount = GT2Save.EVENT_COUNT // 8
        colCount = GT2Save.EVENT_COUNT // 31
        k = 0
        self.data["Event progress"] = []

        for i in range(rowCount):
            for j in range(colCount):
                combo = Combobox(tab.content, vals, 10)
                combo.place(x = 10 + j * 85, y = 5 + i * 30)
                combo.setState(False)
                self.data["Event progress"].append(combo)

            k = i

        k += 1
        button = Button(tab.content, "All 1st", self.allFirstEventProgress)
        button.place(x = 10, y = 5 + k * 30)
        button.setState(False)
        self.buttons.append(button)


    def allFirstEventProgress(self):
        for combo in self.data["Event progress"]:
            combo.setVal("1st")


    def initArcadeProgress(self, nb):
        tab = Tab(nb, 780, 280)
        nb.add(tab, text = "Arcade progress")

        vals = tuple(GT2Save.ARCADE_PROGRESS.keys())
        trackCount = len(GT2Save.ARCADE_TRACKS)
        colCount = 3
        rowCount = (trackCount + colCount - 1) // colCount
        t, k = 0, 0
        self.data["Arcade progress"] = []

        for i in range(rowCount):
            for j in range(colCount):
                if t == trackCount: break

                label = tkinter.Label(tab.content, text = f"{GT2Save.ARCADE_TRACKS[t]}:")
                label.place(x = 10 + j * 270, y = 5 + i * 30)

                combo = Combobox(tab.content, vals, 10)
                combo.place(x = 145 + j * 270, y = 5 + i * 30)
                combo.setState(False)
                self.data["Arcade progress"].append(combo)

                t += 1

            k = i

        k += 1
        button = Button(tab.content, "All hard", self.allHardArcadeProgress)
        button.place(x = 10, y = 5 + k * 30)
        button.setState(False)
        self.buttons.append(button)

        k += 1
        label = tkinter.Label(tab.content, text = "Ending movie:")
        label.place(x = 10, y = 5 + k * 30)

        check = Checkbox(tab.content)
        check.place(x = 145, y = 5 + k * 30)
        check.setState(False)
        self.data["Ending movie"] = check


    def allHardArcadeProgress(self):
        for combo in self.data["Arcade progress"]:
            combo.setVal("Hard")


    def setGUI(self, active, clear):
        state = "normal" if active else "disabled"
        self.fileMenu.entryconfig(1, state = state)
        self.fileMenu.entryconfig(2, state = state)
        self.fileMenu.entryconfig(3, state = state)
        self.slotMenu.entryconfig(1, state = state)

        for name in self.data:
            if isinstance(self.data[name], list):
                for entry in self.data[name]:
                    entry.setState(active)
                    if clear: entry.setVal("")

            elif isinstance(self.data[name], dict):
                for entry in self.data[name]:
                    if isinstance(self.data[name][entry], list):
                        for val in self.data[name][entry]:
                            val.setState(active)
                            if clear: val.setVal("")

                    else:
                        self.data[name][entry].setState(active)
                        if clear: self.data[name][entry].setVal("")

            elif name not in ["Path", "Serial", "Region", "CRC32", "Car count"]:
                self.data[name].setState(active)
                if clear: self.data[name].setVal("")

            else:
                if clear: self.data[name].setVal("")

        if clear: self.carsTable.clearRows()

        for entry in self.buttons:
            entry.setState(active)


    def open(self, path, ask):
        if ask: path = filedialog.askopenfilename(title = "Open save")
        if not path: return

        try:
            self.parser = GT2SaveParser(path)
            self.setGUI(True, False)
            self.slot = 0
            self.print()

        except Exception as e:
            self.parser = None
            self.setGUI(False, True)
            messagebox.showerror("Error", str(e))


    def print(self):
        saveData = self.parser.read(self.slot)
        for name in saveData:
            if isinstance(saveData[name], int) or isinstance(saveData[name], str):
                self.data[name].setVal(saveData[name])

            elif name in ["Arcade progress", "Event progress"]:
                for i in range(len(saveData[name])):
                    self.data[name][i].setVal(saveData[name][i])

            elif name == "License progress":
                for licName in saveData[name]:
                    for i in range(len(saveData[name][licName])):
                        self.data[name][licName][i].setVal(saveData[name][licName][i])

            elif name == "Cars":
                self.carsTable.clearRows()
                for car in saveData[name]:
                    self.carsTable.insertRow(car)


    def change(self):
        if self.slot == self.parser.getSaveCount() - 1: self.slot = 0
        else: self.slot += 1
        self.print()


    def update(self):
        if not self.parser: return

        saveData = {}
        for name in self.data:
            if isinstance(self.data[name], Checkbox) or isinstance(self.data[name], Combobox) or isinstance(self.data[name], Textbox):
                saveData[name] = self.data[name].getVal()

            elif name in ["Arcade progress", "Event progress"]:
                saveData[name] = []
                for entry in self.data[name]:
                    saveData[name].append(entry.getVal())

            elif name == "License progress":
                saveData[name] = {}
                for licName in self.data[name]:
                    saveData[name][licName] = []
                    for entry in self.data[name][licName]:
                        saveData[name][licName].append(entry.getVal())

        saveData["Cars"] = self.carsTable.getAllRows()

        try:
            self.parser.update(saveData, self.slot)
            self.print()
            messagebox.showinfo("Info", "Save updated")
            return True

        except Exception as e:
            messagebox.showerror("Error", str(e))
            return False


    def close(self):
        if self.update(): self.setGUI(False, True)