from GT2Save import GT2Save

class GT2SaveParser:
    SC_MAGIC = b"\x53\x43"
    MC_MAGIC = b"\x4D\x43"
    PSV_MAGIC = b"\x00\x56\x53\x50"
    GME_MAGIC = b"\x31\x32\x33\x2D\x34\x35\x36\x2D\x53\x54\x44"

    SC_HEADER_SIZE = 512
    MC_HEADER_SIZE = 128
    PSV_HEADER_SIZE = 132
    GME_HEADER_SIZE = 3904

    MC_BLOCK_COUNT = 16
    MC_BLOCK_SIZE = 8192
    MC_SIZE = 131072

    RAW_SAVE_SIZE = 31904

    SERIALS = {"SCES-02380": "EU", "SCES-12380": "EU",
               "SCUS-94455": "US", "SCUS-94488": "US", "SCUS-94588": "US",
               "SCPS-10116": "JP", "SCPS-10117": "JP", "SCPS-45457": "JP", "SCPS-45458": "JP", "SCAJ-01001": "JP", "SCAJ-01002": "JP", "SCPS-91326": "JP", "SCPS-91327": "JP"}


    def __init__(self, path):
        self.path = path

        with open(self.path, "rb") as f:
            self.data = bytearray(f.read())

        self.parse()


    def parse(self):
        self.format = "Unknown"
        self.saves = []

        if len(self.data) >= self.SC_HEADER_SIZE + self.RAW_SAVE_SIZE:
            if self.data.startswith(self.SC_MAGIC):
                self.format = "SC"
                self.saves.append(GT2Save(self.SC_HEADER_SIZE, "Unknown", "Unknown", self.data))
                return

        if len(self.data) >= self.PSV_HEADER_SIZE + self.SC_HEADER_SIZE + self.RAW_SAVE_SIZE:
            if self.data.startswith(self.PSV_MAGIC):
                serial = self.data[102:112].decode("ASCII")

                if serial in self.SERIALS:
                    self.format = "PSV"
                    self.saves.append(GT2Save(self.PSV_HEADER_SIZE + self.SC_HEADER_SIZE, serial, self.SERIALS[serial], self.data))
                    return

        gmeShift = None
        if len(self.data) >= self.MC_SIZE and self.data.startswith(self.MC_MAGIC):
            self.format = "MC"
            gmeShift = 0
        elif len(self.data) >= self.GME_HEADER_SIZE + self.MC_SIZE and self.data.startswith(self.GME_MAGIC):
            self.format = "GME"
            gmeShift = self.GME_HEADER_SIZE

        if gmeShift is not None:
            for i in range(1, self.MC_BLOCK_COUNT):
                headerOffset = gmeShift + self.MC_HEADER_SIZE * i
                headerBytes = self.data[headerOffset:headerOffset + self.MC_HEADER_SIZE]

                try:
                    serial = headerBytes[12:22].decode("ASCII")

                except Exception:
                    continue

                if headerBytes[0] == 0x51 and serial in self.SERIALS:
                    self.saves.append(GT2Save(gmeShift + self.MC_BLOCK_SIZE * i + self.SC_HEADER_SIZE, serial, self.SERIALS[serial], self.data))

        if len(self.saves) == 0:
            raise ValueError("No GT2 save found")


    def read(self, i):
        if i < 0 or i >= len(self.saves):
            raise ValueError(f"Invalid save index '{i}'")

        save = self.saves[i]
        return {"Path": self.path,
                "Serial": save.serial,
                "Region": save.region,
                "Language": save.get(GT2Save.DataType.LANGUAGE),
                "Arcade progress": save.get(GT2Save.DataType.ARCADE_PROGRESS),
                "Days": save.get(GT2Save.DataType.DAYS),
                "Races": save.get(GT2Save.DataType.RACES),
                "Wins": save.get(GT2Save.DataType.WINS),
                "Sum of best rankings": save.get(GT2Save.DataType.SUM_OF_BEST_RANKINGS),
                "Sum of rankings": save.get(GT2Save.DataType.SUM_OF_RANKINGS),
                "Prize": save.get(GT2Save.DataType.PRIZE),
                "Event progress": save.get(GT2Save.DataType.EVENT_PROGRESS),
                "Ending movie": save.get(GT2Save.DataType.ENDING_MOVIE),
                "License progress": save.get(GT2Save.DataType.LICENSE_PROGRESS),
                "Car count": save.get(GT2Save.DataType.CAR_COUNT),
                "Cars": save.get(GT2Save.DataType.CARS),
                "Money": save.get(GT2Save.DataType.MONEY),
                "CRC32": save.get(GT2Save.DataType.CRC32)}


    def update(self, data, i):
        if i < 0 or i >= len(self.saves):
            raise ValueError(f"Invalid save index '{i}'")

        save = self.saves[i]
        save.update(GT2Save.DataType.LANGUAGE, data["Language"])
        save.update(GT2Save.DataType.ARCADE_PROGRESS, data["Arcade progress"])
        save.update(GT2Save.DataType.DAYS, data["Days"])
        save.update(GT2Save.DataType.RACES, data["Races"])
        save.update(GT2Save.DataType.WINS, data["Wins"])
        save.update(GT2Save.DataType.SUM_OF_BEST_RANKINGS, data["Sum of best rankings"])
        save.update(GT2Save.DataType.SUM_OF_RANKINGS, data["Sum of rankings"])
        save.update(GT2Save.DataType.PRIZE, data["Prize"])
        save.update(GT2Save.DataType.EVENT_PROGRESS, data["Event progress"])
        save.update(GT2Save.DataType.ENDING_MOVIE, data["Ending movie"])
        save.update(GT2Save.DataType.LICENSE_PROGRESS, data["License progress"])
        save.update(GT2Save.DataType.CARS, data["Cars"])
        save.update(GT2Save.DataType.MONEY, data["Money"])
        save.updateCrc32()

        with open(self.path, "wb") as f:
            f.write(self.data)


    def getSaveCount(self):
        return len(self.saves)