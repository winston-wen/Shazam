package shazam.pcm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Wen Ke on 2016/10/19.
 */
public class PCM16MonoParser {
    public static PCM16MonoData parse(String path) throws IOException {
        File f = new File(path);
        RandomAccessFile rfile = new RandomAccessFile(f, "r");
        if (rfile.length() < 46)
            throw new RuntimeException("Broken WAV file!");

        /**
         * If the first 4 bytes do not equal ASCII text "RIFF"
         */
        rfile.seek(0);
        if (rfile.readInt() != 0x52494646) {
            throw new RuntimeException("The file is not an RIFF file");
        }

        /**
         * If the format field is not "WAVE"
         */
        rfile.seek(8);
        if (rfile.readInt() != 0x57415645) {
            throw new RuntimeException("The file is not a WAVE file");
        }

        /**
         * If the audio is not PCM
         */
        rfile.seek(20);
        if (rfile.readShort() != (short) 0x0100) {
            throw new RuntimeException("The file is not a PCM file");
        }

        /**
         * If the audio has more than one channels
         */
        rfile.seek(22);
        if (rfile.readShort() != (short) 0x0100) {
            throw new RuntimeException("The audio has >1 channels");
        }

        /**
         * If the sample rate is not 44100
         */
        rfile.seek(24);
        if (rfile.readInt() != 0x44AC0000) {
            throw new RuntimeException("The audio's sample rate is not 44100 Hz");
        }

        /**
         * If the bit depth is not 16
         */
        rfile.seek(34);
        if (rfile.readShort() != (short) 0x1000) {
            throw new RuntimeException("The audio's bit depth is not 16-bit");
        }

        /**
         * If the file has extra parameters
         */
        rfile.seek(36);
        if (rfile.readShort() != (short) 0x0000) {
            throw new RuntimeException("The audio has extra parameters in its \"fmt \" block");
        }

        /**
         * Find the data size
         */
        rfile.seek(42);
        int a = rfile.readByte() & 0xFF;
        rfile.seek(43);
        int b = (rfile.readByte() << 8) & 0xFF00;
        rfile.seek(44);
        int c = (rfile.readByte() << 16) & 0xFF0000;
        rfile.seek(45);
        int d = (rfile.readByte() << 24) & 0xFF000000;
        int size = d|c|b|a;
        if (size + 46 != f.length()) {
            throw new RuntimeException("The audio is broken");
        }

        /**
         * read data and generate a PCM16MonoData
         */
        PCM16MonoData data = new PCM16MonoData();
        byte[] raw = new byte[size];
        rfile.seek(46);
        rfile.read(raw);
        data.setRawData(raw);
        return data;
    }
}
