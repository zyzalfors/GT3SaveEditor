package GT3SaveEditor;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class GT3Save {
    private final byte[] _bytes;
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

    private static final int _carCountOffset = 112;
    private static final int _carCountSize = 4;

    private static final int _carsSkipsOffset = 116;
    private static final int _carsSkipsSize = 4;

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
    private static final int _carCodeSize = 8;
    private static final int _carsSkipSize = 68;

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

    public static enum VALUE {END_OF_SAVE, CRC32, DAYS, RACES, WINS, MONEY, PRIZE, CAR_COUNT, CARS_SKIPS, TROPHIES, BONUS_CARS, LANGUAGE};

    public GT3Save(String path) throws Exception {
        _path = path;
        _bytes = Files.readAllBytes(Paths.get(path));
    }

    private int CalcCRC32() {
        int toOffset = _headerSize - 1 + GetInt(VALUE.END_OF_SAVE);
        byte[] bytes = Arrays.copyOfRange(_bytes, _headerSize, toOffset + 1);

        CRC32 crc32 = new CRC32();
        crc32.update(bytes);

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
                return 0;
        }

        byte[] bytes = Arrays.copyOfRange(_bytes, offset, offset + size);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int val = buffer.getInt();

        return conv ? -1 * (val + 1) : val;
    }

    public void UpdateInt(VALUE value, int val) {
        int offset = 0;
        int size = 0;
        boolean conv = true;

        switch(value) {
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

            case TROPHIES:
                offset = _trophiesOffset;
                size = _trophiesSize;
                break;

            case BONUS_CARS:
                offset = _bonusCarsOffset;
                size = _bonusCarsSize;
                break;

            default:
                return;
        }

        if(conv) val = -1 * (val + 1);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(val);
        byte[] bytes = buffer.array();
        System.arraycopy(bytes, 0, _bytes, offset, size);
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
                return 0;
        }

        byte[] bytes = Arrays.copyOfRange(_bytes, offset, offset + size);
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
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
                return;
        }

        val = -1 * (val + 1);

        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(val);
        byte[] bytes = buffer.array();
        System.arraycopy(bytes, 0, _bytes, offset, size);
    }

    public String GetStr(VALUE value) {
        switch(value) {
            case LANGUAGE:
                byte b = _bytes[_langOffset];
                for(String lang : languages.keySet())
                    if(languages.get(lang) == b) return lang;
                break;

            default:
                break;
        }

        return "";
    }

    public void UpdateStr(VALUE value, String val) {
        switch(value) {
            case LANGUAGE:
                if(languages.containsKey(val))
                    _bytes[_langOffset] = languages.get(val);
                break;

            default:
                break;
        }
    }

    public String[][] GetCareerGarage() {
        int carCount = GetInt(VALUE.CAR_COUNT);

        String[][] cars = new String[carCount][2];
        byte[] bytes = new byte[_carSize];

        StringBuilder carCode = new StringBuilder();
        StringBuilder carData = new StringBuilder();

        for(int i = 0; i < carCount; i++) {
            int offset = _firstCarOffset + _carSize * i;
            System.arraycopy(_bytes, offset, bytes, 0, _carSize);

            for(int j = 0; j < _carCodeSize; j++)
                carCode.append(String.format("%02X", bytes[j]));

            cars[i][0] = carCode.toString();
            carCode.setLength(0);

            for(int j = _carCodeSize; j < _carSize; j++)
                carData.append(String.format("%02X", bytes[j]));

            cars[i][1] = carData.toString();
            carData.setLength(0);
        }

        return cars;
    }

    public void UpdateCar(int pos, String car) {
        int carCount = GetInt(VALUE.CAR_COUNT);
        if(pos < 0 || pos > carCount - 1 || car.length() != _carSize * 2) return;

        byte[] bytes = new byte[_carSize];

        for(int i = 0; i < car.length(); i += 2) {
            int high = Character.digit(car.charAt(i), 16);
            int low = Character.digit(car.charAt(i + 1), 16);
            bytes[i / 2] = (byte) ((high << 4) | low);
        }

        int offset = _firstCarOffset + _carSize * pos;
        System.arraycopy(bytes, 0, _bytes, offset, _carSize);
   }

    public String[] GetCareerLicenseProgress() {
        int firstCarLicProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS);
        int carLicProgSize = careerLicenseProgress.get("None").length;

        String[] progress = new String[careerLicenses.length * testsPerLicense];
        byte[] bytes = new byte[carLicProgSize];

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarLicProgOffset + _careerLicenseProgressSkip * i;
            System.arraycopy(_bytes, offset, bytes, 0, carLicProgSize);

            progress[i] = "None";
            for(String prog : careerLicenseProgress.keySet())
                if(Arrays.equals(careerLicenseProgress.get(prog), bytes)) progress[i] = prog;
        }

        return progress;
    }

    public void UpdateCareerLicenseProgress(String[] progress) {
        int firstCarLicProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS);
        int carLicProgSize = careerLicenseProgress.get("None").length;

        for(int i = 0; i < progress.length; i++) {
            byte[] bytes = careerLicenseProgress.get(progress[i]);
            int offset = firstCarLicProgOffset + _careerLicenseProgressSkip * i;
            System.arraycopy(bytes, 0, _bytes, offset, carLicProgSize);
        }
    }

    public String[] GetCareerEventProgress() {
        int firstCarEventProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip;
        String[] progress = new String[careerEventCount];

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarEventProgOffset + _careerEventProgressSkip * i;
            byte b = _bytes[offset];

            progress[i] = "None";
            for(String prog : careerEventProgress.keySet())
                if(careerEventProgress.get(prog) == b) progress[i] = prog;
        }

        return progress;
    }

    public void UpdateCareerEventProgress(String[] progress) {
        int firstCarEventProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip;

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarEventProgOffset + _careerEventProgressSkip * i;
            _bytes[offset] = careerEventProgress.get(progress[i]);
        }
    }

    public String[] GetArcadeProgress() {
        int firstArcEvEasyProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip + _arcadeEventEasyProgressSkip;

        String[] progress = new String[arcadeTracks.length + 2];
        byte[] bytes = new byte[arcadeEventProgress.get("None").length];

        for(int i = 0; i < progress.length - 2; i++) {
            for(int j = 0; j < bytes.length; j++) {
                int offset = firstArcEvEasyProgOffset + _arcadeEventSkip * i + _difficultArcadeEventSkip * j;
                bytes[j] = _bytes[offset];
            }

            progress[i] = "None";
            for(String prog : arcadeEventProgress.keySet())
                if(Arrays.equals(arcadeEventProgress.get(prog), bytes)) progress[i] = prog;
        }

        byte b = _bytes[_arcadeTracksProgressOffset];

        progress[progress.length - 2] = "None";
        for(String prog : arcadeTracksProgress.keySet())
            if(arcadeTracksProgress.get(prog) == b) progress[progress.length - 2] = prog;

        bytes = new byte[arcadeCarsProgress.get("None").length];
        for(int i = 0; i < bytes.length; i++) {
            int offset = _arcadeCarsProgressOffset + _arcadeCarsProgressSkip * i;
            bytes[i] = _bytes[offset];
        }

        progress[progress.length - 1] = "None";
        for(String prog : arcadeCarsProgress.keySet())
            if(Arrays.equals(arcadeCarsProgress.get(prog), bytes)) progress[progress.length - 1] = prog;

        return progress;
    }

    public void UpdateArcadeProgress(String[] progress) {
        int firstArcEvEasyProgOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + careerLicenses.length * testsPerLicense * _careerLicenseProgressSkip + _arcadeEventEasyProgressSkip;

        for(int i = 0; i < progress.length - 2; i++) {
            byte[] bytes = arcadeEventProgress.get(progress[i]);

            for(int j = 0; j < bytes.length; j++) {
                int offset = firstArcEvEasyProgOffset + _arcadeEventSkip * i + _difficultArcadeEventSkip * j;
                _bytes[offset] = bytes[j];
            }
        }

        _bytes[_arcadeTracksProgressOffset] = arcadeTracksProgress.get(progress[progress.length - 2]);

        byte[] bytes = arcadeCarsProgress.get(progress[progress.length - 1]);
        for(int i = 0; i < bytes.length; i++) {
            int offset = _arcadeCarsProgressOffset + _arcadeCarsProgressSkip * i;
            _bytes[offset] = bytes[i];
        }
    }

    public void Update() throws Exception {
        UpdateCRC32();
        Files.write(Paths.get(_path), _bytes);
    }
}