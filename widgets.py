import tkinter
from tkinter import ttk

class Button(tkinter.Button):

    def __init__(self, parent, text, command):
        super().__init__(master = parent, text = text, command = command)


    def setState(self, active):
        state = "normal" if active else "disabled"
        self.config(state = state)


class Checkbox(tkinter.Checkbutton):

    def __init__(self, parent):
        super().__init__(master = parent, onvalue = 1, offvalue = 0)


    def setVal(self, val):
        self.select() if val else self.deselect()


    def getVal(self):
        return bool(self.getvar(self.cget("variable")))


    def setState(self, active):
        state = "normal" if active else "disabled"
        self.config(state = state)


class Combobox(ttk.Combobox):

    def __init__(self, parent, vals, width):
        super().__init__(master = parent, width = width, values = vals)


    def setVal(self, val):
        self.set(val)


    def getVal(self):
        return self.get()


    def setState(self, active):
        state = "readonly" if active else "disabled"
        self.config(state = state)


class PopupMenu(tkinter.Menu):

    def __init__(self, table):
        super().__init__(master = table, tearoff = False)
        self.table = table
        self.add_command(label = "Copy car", command = self.copyCar)
        self.add_command(label = "Paste car", command = self.pasteCar)
        self.add_command(label = "Delete cars", command = self.deleteCars)


    def copyCar(self):
        self.table.storeSelectedRow()


    def pasteCar(self):
        self.table.insertStoredRow()


    def deleteCars(self):
        self.table.deleteSelectedRows()


class Tab(ttk.Frame):

    def __init__(self, parent, width, height):
        super().__init__(master = parent)

        self.canvas = tkinter.Canvas(master = self, highlightthickness = 0, bd = 0)
        self.content = ttk.Frame(master = self.canvas)

        vbar = ttk.Scrollbar(master = self, orient = "vertical", command = self.canvas.yview)
        hbar = ttk.Scrollbar(master = self, orient = "horizontal", command = self.canvas.xview)

        self.canvas.configure(yscrollcommand = vbar.set, xscrollcommand = hbar.set)
        self.canvas.create_window(0, 0, window = self.content, anchor = "nw")

        self.canvas.grid(row = 0, column = 0, sticky = "nsew")
        vbar.grid(row = 0, column = 1, sticky = "ns")
        hbar.grid(row = 1, column = 0, sticky = "ew")

        self.rowconfigure(0, weight = 1)
        self.columnconfigure(0, weight = 1)

        self.content.bind("<Configure>", lambda e: self.canvas.configure(scrollregion = self.canvas.bbox("all")))
        self.content.configure(width = width, height = height)


class Table(ttk.Treeview):

    def __init__(self, parent, columns):
        super().__init__(master = parent, columns = columns, show = "headings")

        for column in columns:
            self.heading(column, text = column)
            self.column(column, width = 110, minwidth = 80, anchor = "center")

        yscroll = ttk.Scrollbar(master = parent, orient = "vertical", command = self.yview)
        yscroll.grid(row = 0, column = 1, sticky = "ns")

        xscroll = ttk.Scrollbar(master = parent, orient = "horizontal", command = self.xview)
        xscroll.grid(row = 1, column = 0, sticky = "ew")

        self.configure(yscrollcommand = yscroll.set, xscrollcommand = xscroll.set)
        self.grid(row = 0, column = 0, sticky = "nsew")

        self.bind("<Double-1>", self.editCell)
        self.bind("<Button-3>", self.showPopup)
        self.popupMenu = PopupMenu(self)
        self.storedRow = None


    def editCell(self, event):
        row = self.identify_row(event.y)
        col = self.identify_column(event.x)

        if not row or not col: return

        colIndex = int(col[1:]) - 1
        columns = self["columns"]

        if colIndex >= len(columns): return

        bbox = self.bbox(row, col)
        if not bbox: return

        x, y, width, height = bbox
        column = columns[colIndex]

        entry = ttk.Entry(self)
        entry.insert(0, self.set(row, column))
        entry.select_range(0, tkinter.END)
        entry.place(x = x, y = y, width = width, height = height)
        entry.focus_set()

        def save(event = None):
            self.set(row, column, entry.get())
            entry.destroy()

        def cancel(event = None):
            entry.destroy()

        entry.bind("<Return>", save)
        entry.bind("<FocusOut>", save)
        entry.bind("<Escape>", cancel)


    def showPopup(self, event):
        rowId = self.identify_row(event.y)
        if not rowId: return

        if rowId not in self.selection():
            self.selection_set(rowId)

        sel = self.selection()
        hasRows = bool(self.get_children())
        self.popupMenu.entryconfigure("Copy car", state = "normal" if len(sel) == 1 else "disabled")
        self.popupMenu.entryconfigure("Paste car", state = "normal" if (len(sel) == 1 and self.hasStoredRow()) else "disabled")
        self.popupMenu.entryconfigure("Delete cars", state = "normal" if len(sel) > 0 else "disabled")

        try:
            self.popupMenu.tk_popup(event.x_root, event.y_root)
        finally:
            self.popupMenu.grab_release()


    def clearRows(self):
        self.delete(*self.get_children())


    def insertRow(self, data):
        self.insert("", "end", values = tuple(data.get(col, "") for col in self["columns"]))


    def insertStoredRow(self):
        if self.storedRow is None: return
        sel = self.selection()
        if len(sel) != 1: return
        rowId = sel[0]
        self.item(rowId, values = tuple(self.storedRow.get(col, "") for col in self["columns"]))


    def getAllRows(self):
        return [dict(zip(self["columns"], self.item(rowId, "values"))) for rowId in self.get_children()]


    def storeSelectedRow(self):
        sel = self.selection()
        if len(sel) != 1: return
        rowId = sel[0]
        self.storedRow = dict(zip(self["columns"], self.item(rowId, "values")))


    def hasStoredRow(self):
        return self.storedRow is not None


    def deleteSelectedRows(self):
        for rowId in self.selection():
            self.delete(rowId)


class Textbox(tkinter.Text):

    def __init__(self, parent, width):
        super().__init__(master = parent, height = 1, width = width, wrap = tkinter.NONE)
        self.bind("<Return>", lambda _: "break")


    def setVal(self, val):
        state = self.cget("state")
        self.config(state = "normal")
        self.delete("1.0", "end")
        self.insert("1.0", val)
        self.config(state = state)


    def getVal(self):
        return self.get("1.0", "end-1c")


    def setState(self, active):
        state = "normal" if active else "disabled"
        self.config(state = state)