package GT3SaveEditor;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class GT3Save {
    private final byte[] _bytes;
    private final String _path;

    private static final int _headerOffset = 0;
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

    private static final int _arcadeAreasOffset = 124;
    private static final int _arcadeAreasSize = 4;
    public static final String[] arcadeAreas = new String[] {"A", "B", "C", "D", "E", "F", "End"};

    private static final int _trophiesOffset = 240;
    private static final int _trophiesSize = 4;

    private static final int _bonusCarsOffset = 252;
    private static final int _bonusCarsSize = 4;

    private static final int _langOffset = 264;
    private static final int _langSize = 1;
    public static final Map<String, Integer> languages = Map.of("ES", 0xF9, "IT", 0xFA, "DE", 0xFB, "FR", 0xFC, "EN-GB", 0xFD, "EN-US", 0xFE, "JA", 0xFF);

    private static final int _firstCarOffset = 368;
    private static final int _carSize = 516;
    private static final int _carCodeSize = 8;
    private static final int _carsSkipSize = 68;

    private static final int _licenseSize = 4;
    private static final int _licenseSkip = 340;
    public static final int testsPerLicense = 8;
    public static final String[] licenses = new String[] {"B", "A", "IB", "IA", "S", "R"};
    public static final Map<String, byte[]> licenseProgress = Map.of("None", new byte[] {0x00, 0x00, 0x00, 0x00},
                                                                     "Bronze", new byte[] {(byte) 0xFD, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                                                                     "Silver", new byte[] {(byte) 0xFE, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                                                                     "Gold", new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});

    private static final int _careerEventSkip = 4;
    public static final int careerEventCount = 364;
    public static final Map<String, Integer> careerProgress = Map.of("None", 0xF7, "Bronze", 0xFD, "Silver", 0xFE, "Gold", 0xFF);

    private static final int _easyArcadeEventSkip = 1720;
    private static final int _difficultArcadeEventSkip = 680;
    private static final int _arcadeEventSkip = 20;
    private static final Map<String, Integer> _arcadeProgress = Map.of("None", 0xF7, "Completed", 0xFF);
    public static final String[] arcadeTracks = new String[] {"Super Speedway", "Mid-Field", "Smokey Mountain", "Swiss Alps", "Trial Mountain", "Mid-Field II", "Smokey Mountain II",
                                                              "Tokyo Route 246", "Grand Valley", "Laguna Seca", "Rome", "Tahiti", "Swiss Alps II", "Trial Mountain II",
                                                              "Deep Forest", "Special Stage Route 5", "Seattle", "Test Course", "Tokyo Route 246 II", "Grand Valley II", "Rome II",
                                                              "Tahiti II", "Tahiti Maze", "Apricot Hill", "Special Stage Route 11", "Deep Forest II", "Special Stage Route 5 II", "Seattle II",
                                                              "Cote d'Azure", "Special Stage Route 5 Wet", "Apricot Hill II", "Special Stage Route 11 II", "Tahiti Maze II", "Special Stage Route 5 Wet II"};
    public static final String[] arcadeProgress = new String[] {"Easy", "Medium", "Hard", "None"};

    public static enum VALUE {END_OF_SAVE, CRC32, DAYS, RACES, WINS, MONEY, PRIZE, CAR_COUNT, CARS_SKIPS, ARCADE_AREAS, TROPHIES, BONUS_CARS, LANGUAGE};

    public GT3Save(String path) throws Exception {
        _path = path;
        _bytes = Files.readAllBytes(Paths.get(path));
    }

    private int CalcCrc32() {
        int toOffset = _headerSize - 1 + GetInt(VALUE.END_OF_SAVE);
        byte[] bytes = Arrays.copyOfRange(_bytes, _headerSize, toOffset + 1);

        CRC32 crc32 = new CRC32();
        crc32.update(bytes);

        return (int) crc32.getValue();
    }

    public void UpdateCrc32() {
        int crc32 = CalcCrc32();
        UpdateInt(VALUE.CRC32, crc32);
    }

    public boolean CheckCrc32() {
        int calcCrc32 = CalcCrc32();
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

            case ARCADE_AREAS:
                offset = _arcadeAreasOffset;
                size = _arcadeAreasSize;
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

            case ARCADE_AREAS:
                offset = _arcadeAreasOffset;
                size = _arcadeAreasSize;
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
                int b = _bytes[_langOffset] & 0xFF;
                for(String lang : languages.keySet()) {
                    if(((int) languages.get(lang)) == b) return lang;
                }
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
                    _bytes[_langOffset] = (byte) ((int) languages.get(val));
                break;

            default:
                break;
        }
    }

    public String[][] GetCars() {
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

    public String[] GetLicenses() {
        int firstLicOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS);

        String[] progress = new String[licenses.length * testsPerLicense];
        byte[] bytes = new byte[_licenseSize];

        for(int i = 0; i < progress.length; i++) {
            int offset = firstLicOffset + _licenseSkip * i;
            System.arraycopy(_bytes, offset, bytes, 0, _licenseSize);

            progress[i] = "None";
            for(String prog : licenseProgress.keySet()) {
                if(Arrays.equals(licenseProgress.get(prog), bytes)) progress[i] = prog;
            }
        }

        return progress;
    }

    public void UpdateLicenses(String[] progress) {
        int firstLicOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS);

        for(int i = 0; i < progress.length; i++) {
            byte[] bytes = licenseProgress.get(progress[i]);
            int offset = firstLicOffset + _licenseSkip * i;
            System.arraycopy(bytes, 0, _bytes, offset, _licenseSize);
        }
    }

    public String[] GetCareerEvents() {
        int firstCarEventOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + licenses.length * testsPerLicense * _licenseSkip;
        String[] progress = new String[careerEventCount];

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarEventOffset + _careerEventSkip * i;
            int b = _bytes[offset] & 0xFF;

            progress[i] = "None";
            for(String prog : careerProgress.keySet()) {
                if(((int) careerProgress.get(prog)) == b) progress[i] = prog;
            }
        }

        return progress;
    }

    public void UpdateCareerEvents(String[] progress) {
        int firstCarEventOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + licenses.length * testsPerLicense * _licenseSkip;

        for(int i = 0; i < progress.length; i++) {
            int offset = firstCarEventOffset + _careerEventSkip * i;
            _bytes[offset] = (byte) ((int) careerProgress.get(progress[i]));
        }
    }

    public String[] GetArcadeEvents() {
        int firstEasyArcEventOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + licenses.length * testsPerLicense * _licenseSkip + _easyArcadeEventSkip;
        String[] progress = new String[arcadeTracks.length + 1];

        for(int i = 0; i < progress.length - 1; i++) {
            progress[i] = "None";

            for(int j = 0; j < arcadeProgress.length - 1; j++) {
                int offset = firstEasyArcEventOffset + _arcadeEventSkip * i + _difficultArcadeEventSkip * j;
                int b = _bytes[offset] & 0xFF;

                String tmpProg = "";
                for(String prog : _arcadeProgress.keySet()) {
                    if(((int) _arcadeProgress.get(prog)) == b) tmpProg = prog;
                }

                if(tmpProg.equals("Completed")) progress[i] = arcadeProgress[j];
            }
        }

        progress[progress.length - 1] = arcadeAreas[GetInt(VALUE.ARCADE_AREAS)];
        return progress;
    }

    public void UpdateArcadeEvents(String[] progress) {
        int firstEasyArcEventOffset = _firstCarOffset + _carSize * GetInt(VALUE.CAR_COUNT) + _carsSkipSize * GetInt(VALUE.CARS_SKIPS) + licenses.length * testsPerLicense * _licenseSkip + _easyArcadeEventSkip;

        for(int i = 0; i < progress.length - 1; i++) {
            for(int j = 0; j < arcadeProgress.length - 1; j++) {
                int offset = firstEasyArcEventOffset + _arcadeEventSkip * i + _difficultArcadeEventSkip * j;

                if(progress[i].equals(arcadeProgress[j]))
                    _bytes[offset] = (byte) ((int) _arcadeProgress.get("Completed"));
                else
                    _bytes[offset] = (byte) ((int) _arcadeProgress.get("None"));
            }
        }

        int i = 0;
        for(i = 0; i < arcadeAreas.length; i++)
            if(arcadeAreas[i].equals(progress[progress.length - 1])) break;

        UpdateInt(VALUE.ARCADE_AREAS, i);
    }

    public void Update() throws Exception {
        UpdateCrc32();
        Files.write(Paths.get(_path), _bytes);
    }
}