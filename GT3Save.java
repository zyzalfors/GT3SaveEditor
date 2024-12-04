package GT3SaveEditor;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.zip.CRC32;

public class GT3Save {
    private final byte[] _bytes;
    private final String _path;

    private final int _headerOffset = 0;
    private final int _headerSize = 64;

    private final int _crc32Offset = 12;
    private final int _crc32Size = 4;

    private final int _daysOffset = 64;
    private final int _daysSize = 4;

    private final int _racesOffset = 68;
    private final int _racesSize = 4;

    private final int _winsOffset = 76;
    private final int _winsSize = 4;

    private final int _cashOffset = 80;
    private final int _cashSize = 8;

    private final int _prizeOffset = 88;
    private final int _prizeSize = 8;

    private final int _carCountOffset = 112;
    private final int _carCountSize = 4;

    private final int _trophiesOffset = 240;
    private final int _trophiesSize = 4;

    private final int _bonusCarsOffset = 252;
    private final int _bonusCarsSize = 4;

    private final int _langOffset = 264;
    private final int _langSize = 1;

    public final static Map<String, Integer> languages = Map.of("ES", 0xF9, "IT", 0xFA, "DE", 0xFB, "FR", 0xFC, "EN-GB", 0xFD, "EN-US/JA", 0xFE);

    public GT3Save(String path) throws Exception {
        _path = path;
        _bytes = Files.readAllBytes(Paths.get(path));
    }

    private static int Convert(int val) {
        return -1 * (val + 1);
    }

    private static long Convert(long val) {
        return -1 * (val + 1);
    }

    private static byte[] IntToBytes(int val) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(val);
        return buffer.array();
    }

    private static int BytesToInt(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.getInt();
    }

    private static byte[] LongToBytes(long val) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(val);
        return buffer.array();
    }

    private static long BytesToLong(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer.getLong();
    }

    private int CalcCrc32() {
        int paddingSize = 0;
        for(int i = _bytes.length - 1; i >= 0; i--) {
            if((_bytes[i] & 0xFF) != 0xFF) paddingSize++;
            else break;
        }
        int toOffset = _bytes.length - paddingSize;
        if(toOffset < _headerSize) toOffset = _headerSize;
        byte[] crc32Buffer = Arrays.copyOfRange(_bytes, _headerSize, toOffset);
        CRC32 crc32 = new CRC32();
        crc32.update(crc32Buffer);
        return (int) crc32.getValue();
    }

    public boolean CheckCrc32() {
        int calcCrc32 = CalcCrc32();
        byte[] crc32Bytes = Arrays.copyOfRange(_bytes, _crc32Offset, _crc32Offset + _crc32Size);
        int crc32 = BytesToInt(crc32Bytes);
        return crc32 == calcCrc32;
    }

    public void UpdateCrc32() {
        int crc32 = CalcCrc32();
        byte[] crc32Bytes = IntToBytes(crc32);
        for(int i = 0; i < _crc32Size; i++) _bytes[_crc32Offset + i] = crc32Bytes[i];
    }

    public void UpdateDays(int days) {
        int val = Convert(days);
        byte[] bytes = IntToBytes(val);
        for(int i = 0; i < _daysSize; i++) _bytes[_daysOffset + i] = bytes[i];
    }

    public int GetDays() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _daysOffset, _daysOffset + _daysSize);
        return Convert(BytesToInt(bytes));
    }

    public void UpdateRaces(int races) {
        int val = Convert(races);
        byte[] bytes = IntToBytes(val);
        for(int i = 0; i < _racesSize; i++) _bytes[_racesOffset + i] = bytes[i];
    }

    public int GetRaces() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _racesOffset, _racesOffset + _racesSize);
        return Convert(BytesToInt(bytes));
    }

    public void UpdateWins(int wins) {
        int val = Convert(wins);
        byte[] bytes = IntToBytes(val);
        for(int i = 0; i < _winsSize; i++) _bytes[_winsOffset + i] = bytes[i];
    }

    public int GetWins() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _winsOffset, _winsOffset + _winsSize);
        return Convert(BytesToInt(bytes));
    }

    public void UpdateCash(long cash) {
        long val = Convert(cash);
        byte[] bytes = LongToBytes(val);
        for(int i = 0; i < _cashSize; i++) _bytes[_cashOffset + i] = bytes[i];
    }

    public long GetCash() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _cashOffset, _cashOffset + _cashSize);
        return Convert(BytesToLong(bytes));
    }

    public void UpdatePrize(long prize) {
        long val = Convert(prize);
        byte[] bytes = LongToBytes(val);
        for(int i = 0; i < _prizeSize; i++) _bytes[_prizeOffset + i] = bytes[i];
    }

    public long GetPrize() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _prizeOffset, _prizeOffset + _prizeSize);
        return Convert(BytesToLong(bytes));
    }

    public int GetCarCount() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _carCountOffset, _carCountOffset + _carCountSize);
        return Convert(BytesToInt(bytes));
    }

    public void UpdateTrophies(int trophies) {
        int val = Convert(trophies);
        byte[] bytes = IntToBytes(val);
        for(int i = 0; i < _trophiesSize; i++) _bytes[_trophiesOffset + i] = bytes[i];
    }

    public int GetTrophies() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _trophiesOffset, _trophiesOffset + _trophiesSize);
        return Convert(BytesToInt(bytes));
    }

    public void UpdateBonusCars(int cars) {
        int val = Convert(cars);
        byte[] bytes = IntToBytes(val);
        for(int i = 0; i < _bonusCarsSize; i++) _bytes[_bonusCarsOffset + i] = bytes[i];
    }

    public int GetBonusCars() {
        byte[] bytes = Arrays.copyOfRange(_bytes, _bonusCarsOffset, _bonusCarsOffset + _bonusCarsSize);
        return Convert(BytesToInt(bytes));
    }

    public void UpdateLang(String lang) throws Exception {
        int langByte = languages.containsKey(lang) ? languages.get(lang) : 0;
        if(langByte == 0) throw new Exception("Invalid language");
        _bytes[_langOffset] = (byte) langByte;
    }

    public String GetLang() {
        int langByte = _bytes[_langOffset] & 0xFF;
        for(String lang : languages.keySet()) {
            if((int) languages.get(lang) == langByte) return lang;
        }
        return "unknown";
    }

    public void Update() throws Exception {
        UpdateCrc32();
        Files.write(Paths.get(_path), _bytes);
    }

}