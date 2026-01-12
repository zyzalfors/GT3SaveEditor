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

    private static final int _trophiesOffset = 240;
    private static final int _trophiesSize = 4;

    private static final int _bonusCarsOffset = 252;
    private static final int _bonusCarsSize = 4;

    private static final int _langOffset = 264;
    private static final int _langSize = 1;
    public static final Map<String, Integer> languages = Map.of("ES", 0xF9, "IT", 0xFA, "DE", 0xFB, "FR", 0xFC, "EN-GB", 0xFD, "EN-US", 0xFE, "JA", 0xFF);

    private static final int _firstCarOffset = 368;
    private static final int _carSize = 516;

    private static final int _firstLicenseOffset = 68;
    private static final int _licenseSize = 4;
    private static final int _licenses = 6;
    private static final int _testsPerLicense = 8;
    private static final int _licenseSkip = 340;
    public static final Map<String, int[]> licenseProgress = Map.of("None", new int[] {0x00, 0x00, 0x00, 0x00}, "Bronze", new int[] {0xFD, 0xFF, 0xFF, 0xFF},
                                                                    "Silver", new int[] {0xFE, 0xFF, 0xFF, 0xFF}, "Gold", new int[] {0xFF, 0xFF, 0xFF, 0xFF});

    public static enum VALUE {PATH, CRC32, DAYS, RACES, WINS, MONEY, PRIZE, CAR_COUNT, TROPHIES, BONUS_CARS, LANGUAGE};

    public GT3Save(String path) throws Exception {
        _path = path;
        _bytes = Files.readAllBytes(Paths.get(path));
    }

    private int CalcCrc32() {
        int padding = 0;

        for(int i = _bytes.length - 1; i >= 0; i--) {
            if((_bytes[i] & 0xFF) != 0xFF) padding++;
            else break;
        }

        int toOffset = _bytes.length - padding;
        if(toOffset < _headerSize) toOffset = _headerSize;
        byte[] bytes = Arrays.copyOfRange(_bytes, _headerSize, toOffset);

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

        for(int i = 0; i < size; i++) _bytes[offset + i] = bytes[i];
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

        for(int i = 0; i < size; i++) _bytes[offset + i] = bytes[i];
    }

    public String GetStr(VALUE value) {
        switch(value) {
            case PATH:
                return _path;

            case LANGUAGE:
                int b = _bytes[_langOffset] & 0xFF;
                for(String lang : languages.keySet()) {
                    if(((int) languages.get(lang)) == b) return lang;
                }
                break;
        }

        return "";
    }

    public void UpdateStr(VALUE value, String val) {
        switch(value) {
            case LANGUAGE:
                if(languages.containsKey(val)) _bytes[_langOffset] = (byte) ((int) languages.get(val));
                break;
        }
    }

    public void Update() throws Exception {
        UpdateCrc32();
        Files.write(Paths.get(_path), _bytes);
    }
}