package shazam.pcm;

/**
 * Created by Wen Ke on 2016/10/19.
 */
public class PCM16MonoData {
    private byte[] rawData;
    private int sampleNum;

    byte[] getRawData() {
        return rawData;
    }

    void setRawData(byte[] rawData) {
        if (rawData.length % 2 != 0)
            throw new RuntimeException("Bad PCM format: odd number of bytes");
        this.rawData = rawData;
        sampleNum = rawData.length / 2;
    }


    /**
     * Get the amplitude value of sample
     *
     * @param sampleID
     * @return
     */
    public double getSample(int sampleID) {
        int a = rawData[sampleID << 1] & 0xFF;
//        int b = rawData[(sampleID << 1) + 1] << 24 >> 16;
        int b = rawData[(sampleID << 1) + 1] << 8;
        return (b | a)/32768.0;
    }

    public int getSampleNum() {
        return sampleNum;
    }
}
