package shazam.util;

import java.io.IOException;

/**
 * Created by Wen Ke on 2016/10/19.
 */
public class PCMFormatTest {
    public static void main(String[] args) throws IOException {
        PCM16MonoData data = PCM16MonoParser.parse("data/Volcanoes-piece.wav");
    }
}
