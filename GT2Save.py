import binascii, enum

class GT2Save:
    SC_HEADER_SIZE = 512

    LANGUAGE_OFFSET = 0
    LANGUAGES = {"JA": 0, "EN-US": 1, "EN-GB": 2, "FR": 3, "DE": 4, "IT": 5, "ES": 6}

    ARCADE_PROGRESS_OFFSET = 184
    ARCADE_PROGRESS = {"None": 0, "Easy": 1, "Normal": 2, "Hard": 4}
    ARCADE_TRACKS = ["Rome", "Rome Short", "Rome Night", "Seattle", "Seattle Short", "Super Speedway", "Laguna Seca",
                     "Midfield", "Apricot Hill", "Red Rock Valley", "Tahiti Road", "High Speed Ring", "Autumn Ring", "Trial Mountain",
                     "Deep Forest", "Grand Valley", "Grand Valley East", "Special Stage Route 5", "Clubman Stage Route 5", "Grindelwald", "Test Course"]

    DAYS_OFFSET = 248
    DAYS_SIZE = 4

    RACES_OFFSET = 256
    RACES_SIZE = 4

    WINS_OFFSET = 260
    WINS_SIZE = 4

    SUM_OF_BEST_RANKINGS_OFFSET = 264
    SUM_OF_BEST_RANKINGS_SIZE = 4

    SUM_OF_RANKINGS_OFFSET = 268
    SUM_OF_RANKINGS_SIZE = 4

    PRIZE_OFFSET = 276
    PRIZE_SIZE = 4

    EVENT_PROGRESS_OFFSET = 280
    EVENT_PROGRESS = {"None": 0, "1st": 1, "2nd": 2, "3rd": 3, "4th": 4, "5th": 5, "6th": 6}
    EVENT_COUNT = 248

    ENDING_MOVIE_OFFSET = 533
    ENDING_MOVIE_SIZE = 1

    LICENSE_PROGRESS_OFFSETS = {"S": 5145, "IA": 6785, "IB": 8425, "IC": 10065, "A": 11705, "B": 13345}
    LICENSE_PROGRESS = {"None": 0, "Kid": 1, "Bronze": 2, "Silver": 3, "Gold": 4}
    TESTS_PER_LICENSE = 10
    LICENSE_SKIP = 164

    CAR_COUNT_OFFSET = 15476
    CAR_COUNT_SIZE = 1
    MAX_CAR_COUNT = 100

    FIRST_CAR_OFFSET = 15480
    CAR_SIZE = 164
    CAR_PROPERTIES = {"Code 1": (0, 4), "Color": (4, 1), "Wheels 1": (8, 4), "Brakes": (12, 2), "Brake Controller": (14, 2), "Steer": (16, 2), "Chassis": (18, 2), "Engine": (20, 2),
                      "Drivetrain": (22, 2), "Transmission": (24, 2), "Suspension": (26, 2), "LSD": (28, 2), "Front Tyres": (30, 2), "Rear Tyres": (32, 2), "Weight Reduction": (34, 2), "Race Modify": (36, 2),
                      "Port Polish": (38, 2), "Engine Balancing": (40, 2), "Displacement Increase": (42, 2), "ECU": (44, 2), "N\\A Tune": (46, 2), "Turbo Tune": (48, 2), "Flywheel": (50, 2), "Clutch": (52, 2),
                      "Shaft": (54, 2), "Muffler": (56, 2), "Intercooler": (58, 2), "ASC": (60, 2), "TCS": (62, 2), "Wheels 2": (64, 2), "Power Modifier": (66, 2), "R Gear": (68, 2),
                      "1 Gear": (70, 2), "2 Gear": (72, 2), "3 Gear": (74, 2), "4 Gear": (76, 2), "5 Gear": (78, 2), "6 Gear": (80, 2), "7 Gear": (82, 2), "Final Gear": (84, 2),
                      "Auto Gear": (86, 1), "Split Torque": (87, 1), "Front Brakes": (88, 1), "Rear Brakes": (89, 1), "Front Downforce": (90, 1), "Rear Downforce": (91, 1), "Turbo 1": (92, 4), "Turbo 2": (96, 2),
                      "Front Camber": (98, 1), "Rear Camber": (99, 1), "Front Height": (100, 1), "Rear Height": (101, 1), "Front Toe": (102, 1), "Rear Toe": (103, 1), "Front Spring Rate": (104, 1), "Rear Spring Rate": (105, 1),
                      "Front Spring Frequency": (106, 1), "Rear Spring Frequency": (107, 1), "Front Bound": (108, 2), "Front Rebound": (110, 2), "Rear Bound": (112, 2), "Rear Rebound": (114, 2), "Front Stabilizer": (116, 1), "Rear Stabilizer": (117, 1),
                      "Front Initial LSD": (118, 1), "Rear Initial LSD": (119, 1), "Front Accel LSD": (120, 1), "Rear Accel LSD": (121, 1), "Front Decel LSD": (122, 1), "Rear Decel LSD": (123, 1), "ASC Level": (124, 1), "TCS Level": (125, 1),
                      "Engine Sound 1": (126, 2), "Engine Sound 2": (128, 1), "Manufacturer": (129, 1), "Aspiration": (130, 1), "Gear Auto Setting At Last Gear": (131, 1), "Final Drive Ratio At Last Gear": (132, 2), "Front Tyres Upgrade Level At Last Gear": (134, 2), "Rear Tyres Upgrade Level At Last Gear": (136, 2),
                      "Code 2": (140, 4), "Car Price": (144, 4), "Displayed Weight": (148, 2), "Displayed Torque": (150, 2), "Displayed Horsepower And Flags": (152, 2), "Purchased Parts": (154, 8), "Cleanliness": (162, 2)}

    MONEY_OFFSET = 31880
    MONEY_SIZE = 4

    CRC32_OFFSET = 31900
    CRC32_SIZE = 4

    class DataType(enum.Enum):
        LANGUAGE = 0
        ARCADE_PROGRESS = 1
        DAYS = 2
        RACES = 3
        WINS = 4
        SUM_OF_BEST_RANKINGS = 5
        SUM_OF_RANKINGS = 6
        PRIZE = 7
        EVENT_PROGRESS = 8
        ENDING_MOVIE = 9
        LICENSE_PROGRESS = 10
        CAR_COUNT = 11
        CARS = 12
        MONEY = 13
        CRC32 = 14


    def __init__(self, startOffset, serial, region, data):
        self.startOffset = startOffset
        self.serial = serial
        self.region = region
        self.data = data


    def getLanguage(self):
        b = self.data[self.startOffset + self.LANGUAGE_OFFSET]
        return next((lang for lang, byte in self.LANGUAGES.items() if b == byte), "")


    def updateLanguage(self, lang):
        if lang not in self.LANGUAGES:
            raise ValueError(f"Invalid language '{lang}'")

        self.data[self.startOffset + self.LANGUAGE_OFFSET] = self.LANGUAGES[lang]


    def getArcadeProgress(self):
        progress = []

        for i in range(len(self.ARCADE_TRACKS)):
            b = self.data[self.startOffset + self.ARCADE_PROGRESS_OFFSET + i]
            progress.append(next((prog for prog, byte in self.ARCADE_PROGRESS.items() if b == byte), "None"))

        return progress


    def updateArcadeProgress(self, progress):
        if len(progress) != len(self.ARCADE_TRACKS):
            raise ValueError(f"Invalid length '{len(progress)}' for arcade progress")

        for i in range(len(self.ARCADE_TRACKS)):
            if progress[i] not in self.ARCADE_PROGRESS:
                raise ValueError(f"Invalid arcade progress '{progress[i]}'")

            self.data[self.startOffset + self.ARCADE_PROGRESS_OFFSET + i] = self.ARCADE_PROGRESS[progress[i]]


    def getVal(self, valOffset, valSize, signed):
        return int.from_bytes(bytes = self.data[self.startOffset + valOffset:self.startOffset + valOffset + valSize], byteorder = "little", signed = signed)


    def updateVal(self, valOffset, valSize, signed, val):
        valBytes = int(val).to_bytes(length = valSize, byteorder = "little", signed = signed)
        for i in range(valSize):
            self.data[self.startOffset + valOffset + i] = valBytes[i]


    def getEventProgress(self):
        progress = []

        for i in range(self.EVENT_COUNT // 2):
            b = self.data[self.startOffset + self.EVENT_PROGRESS_OFFSET + i]

            n = b & 0x0F
            progress.append(next((prog for prog, nibble in self.EVENT_PROGRESS.items() if n == nibble), "None"))

            n = (b >> 4) & 0x0F
            progress.append(next((prog for prog, nibble in self.EVENT_PROGRESS.items() if n == nibble), "None"))

        return progress


    def updateEventProgress(self, progress):
        if len(progress) != self.EVENT_COUNT:
            raise ValueError(f"Invalid length '{len(progress)}' for event progress")

        for i in range(0, self.EVENT_COUNT, 2):
            if progress[i] not in self.EVENT_PROGRESS:
                raise ValueError(f"Invalid event progress '{progress[i]}'")
            if progress[i + 1] not in self.EVENT_PROGRESS:
                raise ValueError(f"Invalid event progress '{progress[i + 1]}'")

            n1, n2 = self.EVENT_PROGRESS[progress[i]], self.EVENT_PROGRESS[progress[i + 1]]
            self.data[self.startOffset + self.EVENT_PROGRESS_OFFSET + i // 2] = n1 | (n2 << 4)


    def getLicenseProgress(self):
        progress = {}

        for licName in self.LICENSE_PROGRESS_OFFSETS:
            progress[licName] = []

            for i in range(self.TESTS_PER_LICENSE):
                b = self.data[self.startOffset + self.LICENSE_PROGRESS_OFFSETS[licName] + self.LICENSE_SKIP * i]
                progress[licName].append(next((prog for prog, byte in self.LICENSE_PROGRESS.items() if b == byte), "None"))

        return progress


    def updateLicenseProgress(self, progress):
        if set(progress.keys()) != set(self.LICENSE_PROGRESS_OFFSETS.keys()):
            raise ValueError("Invalid license progress")

        for licName in progress:
            if len(progress[licName]) != self.TESTS_PER_LICENSE:
                raise ValueError(f"Invalid length '{len(progress[licName])}' for progress of license '{licName}'")

            for i in range(self.TESTS_PER_LICENSE):
                prog = progress[licName][i]
                if prog not in self.LICENSE_PROGRESS:
                    raise ValueError(f"Invalid license progress '{prog}' for license '{licName}'")

                self.data[self.startOffset + self.LICENSE_PROGRESS_OFFSETS[licName] + self.LICENSE_SKIP * i] = self.LICENSE_PROGRESS[prog]


    def getCars(self):
        carCount = min(self.get(GT2Save.DataType.CAR_COUNT), self.MAX_CAR_COUNT)
        cars = []

        for i in range(carCount):
            car = {}

            for propName in self.CAR_PROPERTIES:
                prop = self.CAR_PROPERTIES[propName]
                propOffset, propSize = prop[0], prop[1]
                car[propName] = self.data[self.startOffset + self.FIRST_CAR_OFFSET + self.CAR_SIZE * i + propOffset:self.startOffset + self.FIRST_CAR_OFFSET + self.CAR_SIZE * i + propOffset + propSize].hex().upper()

            cars.append(car)

        return cars


    def updateCars(self, cars):
        newCarCount = min(len(cars), self.MAX_CAR_COUNT)
        self.update(GT2Save.DataType.CAR_COUNT, newCarCount)

        for i in range(self.CAR_SIZE * self.MAX_CAR_COUNT):
            self.data[self.startOffset + self.FIRST_CAR_OFFSET + i] = 0

        for i in range(newCarCount):
            car = cars[i]
            if set(car.keys()) != set(self.CAR_PROPERTIES.keys()):
                raise ValueError(f"Invalid car {i} data")

            for propName in car:
                prop = self.CAR_PROPERTIES[propName]
                propOffset, propSize = prop[0], prop[1]
                propVal = car[propName]

                if len(propVal) != propSize * 2:
                    raise ValueError(f"Invalid length '{len(propVal)}' for car property '{propName}' of car {i} data")

                propValBytes = bytearray.fromhex(propVal)
                for j in range(propSize):
                    self.data[self.startOffset + self.FIRST_CAR_OFFSET + self.CAR_SIZE * i + propOffset + j] = propValBytes[j]


    def calcCrc32(self):
        return binascii.crc32(self.data[self.startOffset - self.SC_HEADER_SIZE:self.startOffset + self.CRC32_OFFSET])


    def updateCrc32(self):
        self.update(GT2Save.DataType.CRC32, self.calcCrc32())


    def validCrc32(self):
        return self.get(GT2Save.DataType.CRC32) == self.calcCrc32()


    def get(self, dataType):
        match dataType:
            case GT2Save.DataType.LANGUAGE:
                return self.getLanguage()

            case GT2Save.DataType.ARCADE_PROGRESS:
                return self.getArcadeProgress()

            case GT2Save.DataType.DAYS:
                return self.getVal(self.DAYS_OFFSET, self.DAYS_SIZE, True)

            case GT2Save.DataType.RACES:
                return self.getVal(self.RACES_OFFSET, self.RACES_SIZE, True)

            case GT2Save.DataType.WINS:
                return self.getVal(self.WINS_OFFSET, self.WINS_SIZE, True)

            case GT2Save.DataType.SUM_OF_BEST_RANKINGS:
                return self.getVal(self.SUM_OF_BEST_RANKINGS_OFFSET, self.SUM_OF_BEST_RANKINGS_SIZE, True)

            case GT2Save.DataType.SUM_OF_RANKINGS:
                return self.getVal(self.SUM_OF_RANKINGS_OFFSET, self.SUM_OF_RANKINGS_SIZE, True)

            case GT2Save.DataType.PRIZE:
                return self.getVal(self.PRIZE_OFFSET, self.PRIZE_SIZE, True)

            case GT2Save.DataType.EVENT_PROGRESS:
               return self.getEventProgress()

            case GT2Save.DataType.ENDING_MOVIE:
                return bool(self.getVal(self.ENDING_MOVIE_OFFSET, self.ENDING_MOVIE_SIZE, False))

            case GT2Save.DataType.LICENSE_PROGRESS:
                return self.getLicenseProgress()

            case GT2Save.DataType.CAR_COUNT:
                return self.getVal(self.CAR_COUNT_OFFSET, self.CAR_COUNT_SIZE, False)

            case GT2Save.DataType.CARS:
                return self.getCars()

            case GT2Save.DataType.MONEY:
                return self.getVal(self.MONEY_OFFSET, self.MONEY_SIZE, True)

            case GT2Save.DataType.CRC32:
                return self.getVal(self.CRC32_OFFSET, self.CRC32_SIZE, False)

        raise ValueError("Invalid data type")


    def update(self, dataType, data):
        match dataType:
            case GT2Save.DataType.LANGUAGE:
                self.updateLanguage(data)
                return

            case GT2Save.DataType.ARCADE_PROGRESS:
                self.updateArcadeProgress(data)
                return

            case GT2Save.DataType.DAYS:
                self.updateVal(self.DAYS_OFFSET, self.DAYS_SIZE, True, data)
                return

            case GT2Save.DataType.RACES:
                self.updateVal(self.RACES_OFFSET, self.RACES_SIZE, True, data)
                return

            case GT2Save.DataType.WINS:
                self.updateVal(self.WINS_OFFSET, self.WINS_SIZE, True, data)
                return

            case GT2Save.DataType.SUM_OF_BEST_RANKINGS:
                self.updateVal(self.SUM_OF_BEST_RANKINGS_OFFSET, self.SUM_OF_BEST_RANKINGS_SIZE, True, data)
                return

            case GT2Save.DataType.SUM_OF_RANKINGS:
                self.updateVal(self.SUM_OF_RANKINGS_OFFSET, self.SUM_OF_RANKINGS_SIZE, True, data)
                return

            case GT2Save.DataType.PRIZE:
                self.updateVal(self.PRIZE_OFFSET, self.PRIZE_SIZE, True, data)
                return

            case GT2Save.DataType.EVENT_PROGRESS:
               self.updateEventProgress(data)
               return

            case GT2Save.DataType.ENDING_MOVIE:
                self.updateVal(self.ENDING_MOVIE_OFFSET, self.ENDING_MOVIE_SIZE, False, data)
                return

            case GT2Save.DataType.LICENSE_PROGRESS:
                self.updateLicenseProgress(data)
                return

            case GT2Save.DataType.CAR_COUNT:
                self.updateVal(self.CAR_COUNT_OFFSET, self.CAR_COUNT_SIZE, False, data)
                return

            case GT2Save.DataType.CARS:
                self.updateCars(data)
                return

            case GT2Save.DataType.MONEY:
                self.updateVal(self.MONEY_OFFSET, self.MONEY_SIZE, True, data)
                return

            case GT2Save.DataType.CRC32:
                self.updateVal(self.CRC32_OFFSET, self.CRC32_SIZE, False, data)
                return

        raise ValueError("Invalid data type")