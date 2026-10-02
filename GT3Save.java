package GT3SaveEditor;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class GT3Save {
    private byte[] _data;
    private final String _path;

    private static final int _headerSize = 64;

    private static final int _endOfSaveOffset = 4;
    private static final int _endOfSaveSize = 4;

    private static final int _crc32Offset = 12;
    private static final int _crc32Size = 4;

    private static final int _daysOffset = 64;
    private static final int _daysSize = 4;

    private static final int _racesOffset = 68;
    private static final int _racesSize = 4;

    private static final int _winsOffset = 76;
    private static final int _winsSize = 4;

    private static final int _moneyOffset = 80;
    private static final int _moneySize = 8;

    private static final int _prizeOffset = 88;
    private static final int _prizeSize = 8;

    private static final int _mileageOffset = 96;
    private static final int _mileageSize = 4;
    private static final double _mileageConvFactor = 500.0;

    private static final int _carCountOffset = 112;
    private static final int _carCountSize = 4;

    private static final int _carsSkipsOffset = 116;
    private static final int _carsSkipsSize = 4;
    private static final int _carsSkipSize = 68;

    private static final int _arcadeTracksProgressOffset = 124;
    public static final Map<String, Byte> arcadeTracksProgress = Map.of("None", (byte) 0xFF, "Easy A", (byte) 0xFE, "Easy B", (byte) 0xFD, "Easy C", (byte) 0xFC, "Easy D", (byte) 0xFB, "Easy E", (byte) 0xFA, "Easy F", (byte) 0xF9);

    private static final int _arcadeCarsProgressOffset = 128;
    private static final int _arcadeCarsProgressSkip = 4;
    public static final Map<String, byte[]> arcadeCarsProgress = Map.ofEntries(Map.entry("None",     new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Normal A", new byte[] {(byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Normal B", new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Normal C", new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Normal D", new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Normal E", new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Normal F", new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Hard A",   new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Hard B",   new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Hard C",   new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Hard D",   new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF, (byte) 0xFF}),
                                                                               Map.entry("Hard E",   new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFF}),
                                                                               Map.entry("Hard F",   new byte[] {(byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE}));

    private static final int _arcadeEventEasyProgressSkip = 1720;
    private static final int _difficultArcadeEventSkip = 680;
    private static final int _arcadeEventSkip = 20;
    public static final Map<String, byte[]> arcadeEventProgress = Map.of("None",   new byte[] {(byte) 0xF7, (byte) 0xF7, (byte) 0xF7},
                                                                         "Easy",   new byte[] {(byte) 0xFF, (byte) 0xF7, (byte) 0xF7},
                                                                         "Normal", new byte[] {(byte) 0xF7, (byte) 0xFF, (byte) 0xF7},
                                                                         "Hard",   new byte[] {(byte) 0xF7, (byte) 0xF7, (byte) 0xFF});
    public static final String[] arcadeTracks = new String[] {"Super Speedway", "Mid-Field", "Smokey Mountain", "Swiss Alps", "Trial Mountain", "Mid-Field II", "Smokey Mountain II",
                                                              "Tokyo Route 246", "Grand Valley", "Laguna Seca", "Rome", "Tahiti", "Swiss Alps II", "Trial Mountain II",
                                                              "Deep Forest", "Special Stage Route 5", "Seattle", "Test Course", "Tokyo Route 246 II", "Grand Valley II", "Rome II",
                                                              "Tahiti II", "Tahiti Maze", "Apricot Hill", "Special Stage Route 11", "Deep Forest II", "Special Stage Route 5 II", "Seattle II",
                                                              "Cote d'Azure", "Special Stage Route 5 Wet", "Apricot Hill II", "Special Stage Route 11 II", "Tahiti Maze II", "Special Stage Route 5 Wet II"};

    private static final int _trophiesOffset = 240;
    private static final int _trophiesSize = 4;

    private static final int _bonusCarsOffset = 252;
    private static final int _bonusCarsSize = 4;

    private static final int _langOffset = 264;
    public static final Map<String, Byte> languages = Map.of("ES", (byte) 0xF9, "IT", (byte) 0xFA, "DE", (byte) 0xFB, "FR", (byte) 0xFC, "EN-GB", (byte) 0xFD, "EN-US", (byte) 0xFE, "JA", (byte) 0xFF);

    private static final int _firstCarOffset = 368;
    private static final int _carSize = 516;
    public static final int maxCarCount = 200;
    private static final int _carInfoSize = 8;
    private static final int _carPartSize = 8;
    private static final int _carSettingSize = 4;
    public static final String[] carInfos = new String[] {"Code", "Color", "Type"};
    public static final String[] carParts = new String[] {"Brakes", "Brake Controller", "Chassis", "Engine", "Drivetrain & VCD", "Transmission", "Suspension", "LSD", "Front Tyres", "Rear Tyres",
                                                          "Unknown", "Weight Reduction", "Body & Downforce", "Polish", "Balance", "Displacement", "ECU", "N\\A Tune", "Turbo Tune", "Flywheel",
                                                          "Clutch", "Shaft", "Muffler", "Intercooler", "ASM", "TCS", "Wheels"};
    public static final String[] carSettings = new String[] {"R Gear", "1 Gear", "2 Gear", "3 Gear", "4 Gear", "5 Gear", "6 Gear", "7 Gear", "Final Drive", "Auto Gear",
                                                             "VCD", "Front Brakes", "Rear Brakes", "Front DF", "Rear DF", "Turbo 1", "Turbo 2", "Turbo 3", "Turbo 4", "Turbo 5",
                                                             "Turbo 6", "Front Camber", "Rear Camber", "Front Height", "Rear Height", "Front Toe", "Rear Toe", "Front Springs", "Rear Springs", "Front Grip",
                                                             "Rear Grip", "Front Bound 1", "Front Bound 2", "Front Rebound 1", "Front Rebound 2", "Rear Bound 1", "Rear Bound 2", "Rear Rebound 1", "Rear Rebound 2", "Front Stabilizer",
                                                             "Rear Stabilizer", "Front LSD Init", "Rear LSD Init", "Front LSD Accel", "Rear LSD Accel", "Front LSD Decel", "Rear LSD Decel", "Final Drive", "TCS", "Unknown",
                                                             "Power Modifier", "ASM", "Driver A/MT", "Driver ASM", "Driver TCS", "Unknown", "Fittment 1", "Fittment 2", "Fittment 3", "Travel Meter",
                                                             "Oil 1", "Oil 2", "Dirtyness", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown", "Unknown"};

    private static final int _careerLicenseProgressSkip = 340;
    public static final int testsPerLicense = 8;
    public static final String[] careerLicenses = new String[] {"B", "A", "IB", "IA", "S", "R"};
    public static final Map<String, byte[]> careerLicenseProgress = Map.of("None",   new byte[] {0x00, 0x00, 0x00, 0x00},
                                                                           "Bronze", new byte[] {(byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                                                                           "Silver", new byte[] {(byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                                                                           "Gold",   new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});

    private static final int _careerEventProgressSkip = 4;
    public static final int careerEventCount = 364;
    public static final Map<String, Byte> careerEventProgress = Map.of("None", (byte) 0xF7, "Bronze", (byte) 0xFD, "Silver", (byte) 0xFE, "Gold", (byte) 0xFF);

    public static enum VALUE {END_OF_SAVE, CRC32, DAYS, RACES, WINS, MONEY, PRIZE, MILEAGE, CAR_COUNT, CARS_SKIPS, TROPHIES, BONUS_CARS, LANGUAGE};

    public GT3Save(String path) throws Exception {
        _path = path;
        _data = Files.readAllBytes(Paths.get(path));
    }

    private int CalcCRC32() {
        int toOffset = _headerSize - 1 + GetInt(VALUE.END_OF_SAVE);
        byte[] data = Arrays.copyOfRange(_data, _headerSize, toOffset + 1);

        CRC32 crc32 = new CRC32();
        crc32.update(data);

        return (int) crc32.getValue();
    }

    public void UpdateCRC32() {
        int crc32 = CalcCRC32();
        UpdateInt(VALUE.CRC32, crc32);
    }

    public boolean ValidCRC32() {
        int calcCrc32 = CalcCRC32();
        int crc32 = GetInt(VALUE.CRC32);
        return crc32 == calcCrc32;
    }

    public int GetInt(VALUE value) {
        int offset = 0;
        int size = 0;
        boolean conv = true;

        switch(value) {
            case END_OF_SAVE:
                offset = _endOfSaveOffset;
                size = _endOfSaveSize;
                conv = false;
                break;

            case CRC32:
                offset = _crc32Offset;
                size = _crc32Size;
                conv = false;
                break;

            case DAYS:
                offset = _daysOffset;
                size = _daysSize;
                break;

            case RACES:
                offset = _racesOffset;
                size = _racesSize;
                break;

            case WINS:
                offset = _winsOffset;
                size = _winsSize;
                break;

            case CAR_COUNT:
                offset = _carCountOffset;
                size = _carCountSize;
                break;

            case CARS_SKIPS:
                offset = _carsSkipsOffset;
                size = _carsSkipsSize;
                break;

            case TROPHIES:
                offset = _trophiesOffset;
                size = _trophiesSize;
                break;

            case BONUS_CARS:
                offset = _bonusCarsOffset;
                size = _bonusCarsSize;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        byte[] data = Arrays.copyOfRange(_data, offset, offset + size);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int val = buffer.getInt();

        return conv ? -1 * (val + 1) : val;
    }

    public void UpdateInt(VALUE value, int val) {
        int offset = 0;
        int size = 0;
        boolean conv = true;

        switch(value) {
            case END_OF_SAVE:
                offset = _endOfSaveOffset;
                size = _endOfSaveSize;
                conv = false;
                break;

            case CRC32:
                offset = _crc32Offset;
                size = _crc32Size;
                conv = false;
                break;

            case DAYS:
                offset = _daysOffset;
                size = _daysSize;
                break;

            case RACES:
                offset = _racesOffset;
                size = _racesSize;
                break;

            case WINS:
                offset = _winsOffset;
                size = _winsSize;
                break;

            case CAR_COUNT:
                offset = _carCountOffset;
                size = _carCountSize;
                break;

            case TROPHIES:
                offset = _trophiesOffset;
                size = _trophiesSize;
                break;

            case BONUS_CARS:
                offset = _bonusCarsOffset;
                size = _bonusCarsSize;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        if(conv) val = -1 * (val + 1);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(val);
        byte[] data = buffer.array();
        System.arraycopy(data, 0, _data, offset, size);
    }

    public long GetLong(VALUE value) {
        int offset = 0;
        int size = 0;

        switch(value) {
            case MONEY:
                offset = _moneyOffset;
                size = _moneySize;
                break;

            case PRIZE:
                offset = _prizeOffset;
                size = _prizeSize;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        byte[] data = Arrays.copyOfRange(_data, offset, offset + size);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long val = buffer.getLong();

        return -1 * (val + 1);
    }

    public void UpdateLong(VALUE value, long val) {
        int offset = 0;
        int size = 0;

        switch(value) {
            case MONEY:
                offset = _moneyOffset;
                size = _moneySize;
                break;

            case PRIZE:
                offset = _prizeOffset;
                size = _prizeSize;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        val = -1 * (val + 1);

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(val);
        byte[] data = buffer.array();
        System.arraycopy(data, 0, _data, offset, size);
    }

    public double GetDouble(VALUE value) {
        int offset = 0;
        int size = 0;

        switch(value) {
            case MILEAGE:
                offset = _mileageOffset;
                size = _mileageSize;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        byte[] data = Arrays.copyOfRange(_data, offset, offset + size);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        double val = -1 * ((double) buffer.getInt() / _mileageConvFactor);

        return val;
    }

    public void UpdateDouble(VALUE value, double val) {
        int offset = 0;
        int size = 0;

        switch(value) {
            case MILEAGE:
                offset = _mileageOffset;
                size = _mileageSize;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        int ival = (int) (-val * _mileageConvFactor);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(ival);
        byte[] data = buffer.array();
        System.arraycopy(data, 0, _data, offset, size);
    }

    public String GetStr(VALUE value) {
        switch(value) {
            case LANGUAGE:
                byte b = _data[_langOffset];
                for(String lang : languages.keySet())
                    if(languages.get(lang) == b) return lang;
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }

        return "";
    }

    public void UpdateStr(VALUE value, String val) {
        switch(value) {
            case LANGUAGE:
                if(languages.containsKey(val))
                    _data[_langOffset] = languages.get(val);
                else
                    throw new IllegalArgumentException(String.format("Invalid language: %s", val));
                break;

            default:
                throw new IllegalArgumentException("Invalid value");
        }
    }

    private String[][] GetStrCars(byte[] data, int size, int start) {
        String[][] cars = new String[size][carInfos.length + carParts.length + carSettings.length];
        byte[] infoData = new byte[_carInfoSize];
        byte[] partData = new byte[_carPartSize];
        byte[] settData = new byte[_carSettingSize];

        StringBuilder sb = new StringBuilder();
        int offset = start;

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < carInfos.length; j++) {
                System.arraycopy(data, offset, infoData, 0, _carInfoSize);

                for(byte b : infoData)
                    sb.append(String.format("%02X", b));

                cars[i][j] = sb.toString();
                sb.setLength(0);

                offset += _carInfoSize;
            }

            for(int j = 0; j < carParts.length; j++) {
                System.arraycopy(data, offset, partData, 0, _carPartSize);

                for(byte b : partData)
                    sb.append(String.format("%02X", b));

                cars[i][carInfos.length + j] = sb.toString();
                sb.setLength(0);

                offset += _carPartSize;
            }

            for(int j = 0; j < carSettings.length; j++) {
                System.arraycopy(data, offset, settData, 0, _carSettingSize);

                for(byte b : settData)
                    sb.append(String.format("%02X", b));

                cars[i][carInfos.length + carParts.length + j] = sb.toString();
                sb.setLength(0);

                offset += _carSettingSize;
            }
        }

        return cars;
    }

    private void WriteBinCars(byte[] data, int size, int start, String[][] cars) {
        byte[] carData = new byte[_carSize];
        int offset = start;

        for(int i = 0; i < size; i++) {
            String car = String.join("", cars[i]);
            if(car.length() != _carSize * 2)
                throw new IllegalArgumentException(String.format("Invalid car %d data", i));

            for(int j = 0; j < car.length(); j += 2) {
                int high = Character.digit(car.charAt(j), 16);
                int low = Character.digit(car.charAt(j + 1), 16);

                if(high < 0)
                    throw new IllegalArgumentException(String.format("Invalid car %d data at %d", i, j));
                if(low < 0)
                    throw new IllegalArgumentException(String.format("Invalid car %d data at %d", i, j + 1));

                carData[j / 2] = (byte) ((high << 4) | low);
            }

            System.arraycopy(carData, 0, data, offset, _carSize);
            offset += _carSize;
        }
    }

    public void ExportCareerCars(String[][] cars, String path) throws Exception {
        if(cars.length == 0)
            throw new IllegalArgumentException(String.format("Invalid car data length: %d", cars.length));

        byte[] data = new byte[cars.length * _carSize];
        WriteBinCars(data, cars.length, 0, cars);
        Files.write(Paths.get(path), data);
   }

    public String[][] ImportCareerCars(String path) throws Exception {
         byte[] carData = Files.readAllBytes(Paths.get(path));
         if(carData.length == 0 || carData.length % _carSize != 0)
             throw new IllegalArgumentException(String.format("Invalid file size: %d", carData.length));

         return GetStrCars(carData, carData.length / _carSize, 0);
    }

    public String[][] GetCareerCars() {
        int carCount = GetInt(VALUE.CAR_COUNT);
        return GetStrCars(_data, carCount, _firstCarOffset);
    }

    public void UpdateCareerCars(String[][] cars) {
        int endOfSave = GetInt(VALUE.END_OF_SAVE);
        int carCount = GetInt(VALUE.CAR_COUNT);
        int newCarCount = Math.min(cars.length, maxCarCount);
        int newEndOfSave = endOfSave + (newCarCount - carCount) * _carSize;

        int firstPartSize = _firstCarOffset;
        int newCarPartSize = newCarCount * _carSize;
        int carsEnd = _firstCarOffset + carCount * _carSize;
        int lastPartSize = _data.length - carsEnd;

        byte[] newData = new byte[firstPartSize + newCarPartSize + lastPartSize];
        System.arraycopy(_data, 0, newData, 0, firstPartSize);
        WriteBinCars(newData, newCarCount, _firstCarOffset, cars);
        System.arraycopy(_data, carsEnd, newData, firstPartSize + newCarPartSize, lastPartSize);
        _data = newData;

        UpdateInt(VALUE.CAR_COUNT, newCarCount);
        UpdateInt(VALUE.END_OF_SAVE, newEndOfSave);
   }

    public String[] GetCareerLicenseProgress() {
        int firstCarLicProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS);
        int carLicProgSize = careerLicenseProgress.get("None").length;

        String[] progress = new String[careerLicenses.length * testsPerLicense];
        byte[] data = new byte[carLicProgSize];

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarLicProgOffset + _careerLicenseProgressSkip * i;
            System.arraycopy(_data, offset, data, 0, carLicProgSize);

            progress[i] = "None";
            for(String prog : careerLicenseProgress.keySet())
                if(Arrays.equals(careerLicenseProgress.get(prog), data)) progress[i] = prog;
        }

        return progress;
    }

    public void UpdateCareerLicenseProgress(String[] progress) {
        if(progress.length > careerLicenses.length * testsPerLicense)
            throw new IllegalArgumentException(String.format("Invalid career licence progress length: %d", progress.length));

        int firstCarLicProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS);
        int carLicProgSize = careerLicenseProgress.get("None").length;

        for(int i = 0; i < progress.length; i++) {
            if(!careerLicenseProgress.containsKey(progress[i]))
                throw new IllegalArgumentException(String.format("Invalid career license progress: %s", progress[i]));
            byte[] data = careerLicenseProgress.get(progress[i]);
            int offset = firstCarLicProgOffset + _careerLicenseProgressSkip * i;
            System.arraycopy(data, 0, _data, offset, carLicProgSize);
        }
    }

    public String[] GetCareerEventProgress() {
        int firstCarEvProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip;
        String[] progress = new String[careerEventCount];

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarEvProgOffset + _careerEventProgressSkip * i;
            byte b = _data[offset];

            progress[i] = "None";
            for(String prog : careerEventProgress.keySet())
                if(careerEventProgress.get(prog) == b) progress[i] = prog;
        }

        return progress;
    }

    public void UpdateCareerEventProgress(String[] progress) {
        if(progress.length > careerEventCount)
            throw new IllegalArgumentException(String.format("Invalid career event progress length: %d", progress.length));

        int firstCarEvProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip;

        for(int i = 0; i < progress.length; i++) {
            if(!careerEventProgress.containsKey(progress[i]))
                throw new IllegalArgumentException(String.format("Invalid career event progress: %s", progress[i]));
            int offset = firstCarEvProgOffset + _careerEventProgressSkip * i;
            _data[offset] = careerEventProgress.get(progress[i]);
        }
    }

    public String[] GetArcadeProgress() {
        int firstArcEvEasyProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip + _arcadeEventEasyProgressSkip;

        String[] progress = new String[arcadeTracks.length + 2];
        byte[] data = new byte[arcadeEventProgress.get("None").length];

        for(int i = 0; i < progress.length - 2; i++) {
            for(int j = 0; j < data.length; j++) {
                int offset = firstArcEvEasyProgOffset + _arcadeEventSkip * i + _difficultArcadeEventSkip * j;
                data[j] = _data[offset];
            }

            progress[i] = "None";
            for(String prog : arcadeEventProgress.keySet())
                if(Arrays.equals(arcadeEventProgress.get(prog), data)) progress[i] = prog;
        }

        byte b = _data[_arcadeTracksProgressOffset];

        progress[progress.length - 2] = "None";
        for(String prog : arcadeTracksProgress.keySet())
            if(arcadeTracksProgress.get(prog) == b) progress[progress.length - 2] = prog;

        data = new byte[arcadeCarsProgress.get("None").length];
        for(int i = 0; i < data.length; i++) {
            int offset = _arcadeCarsProgressOffset + _arcadeCarsProgressSkip * i;
            data[i] = _data[offset];
        }

        progress[progress.length - 1] = "None";
        for(String prog : arcadeCarsProgress.keySet())
            if(Arrays.equals(arcadeCarsProgress.get(prog), data)) progress[progress.length - 1] = prog;

        return progress;
    }

    public void UpdateArcadeProgress(String[] progress) {
        if(progress.length > arcadeTracks.length + 2)
            throw new IllegalArgumentException(String.format("Invalid arcade progress length: %d", progress.length));

        int firstArcEvEasyProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip + _arcadeEventEasyProgressSkip;

        for(int i = 0; i < progress.length - 2; i++) {
            if(!arcadeEventProgress.containsKey(progress[i]))
                throw new IllegalArgumentException(String.format("Invalid arcade event progress: %s", progress[i]));
            byte[] data = arcadeEventProgress.get(progress[i]);

            for(int j = 0; j < data.length; j++) {
                int offset = firstArcEvEasyProgOffset + _arcadeEventSkip * i + _difficultArcadeEventSkip * j;
                _data[offset] = data[j];
            }
        }

        if(!arcadeTracksProgress.containsKey(progress[progress.length - 2]))
            throw new IllegalArgumentException(String.format("Invalid arcade tracks progress: %s", progress[progress.length - 2]));
        _data[_arcadeTracksProgressOffset] = arcadeTracksProgress.get(progress[progress.length - 2]);

        if(!arcadeCarsProgress.containsKey(progress[progress.length - 1]))
            throw new IllegalArgumentException(String.format("Invalid arcade cars progress: %s", progress[progress.length - 1]));
        byte[] data = arcadeCarsProgress.get(progress[progress.length - 1]);

        for(int i = 0; i < data.length; i++) {
            int offset = _arcadeCarsProgressOffset + _arcadeCarsProgressSkip * i;
            _data[offset] = data[i];
        }
    }

    public void Update() throws Exception {
        UpdateCRC32();
        Files.write(Paths.get(_path), _data);
    }
}