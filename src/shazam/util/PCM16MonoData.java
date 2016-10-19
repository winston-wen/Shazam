package shazam.util;

/**
 * Created by Wen Ke on 2016/10/19.
 */
public class PCM16MonoData {
    private byte[] rawData;

    byte[] getRawData() {
        return rawData;
    }

    void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }


    /**
     * Get the amplitude value of sample
     *
     * @param sampleID
     * @return
     */
    public double getData(int sampleID) {
        int a = rawData[sampleID << 1] & 0xFF;
        int b = rawData[sampleID << 1 + 1] << 24 >> 16;
        return b | a;
    }
}
