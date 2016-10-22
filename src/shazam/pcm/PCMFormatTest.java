package shazam.pcm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Wen Ke on 2016/10/19.
 *
 */
public class PCMFormatTest {
    public static void main(String[] args) throws IOException {
        PCM16MonoData data = PCM16MonoParser.parse(new File("data/Volcanoes-piece.wav"));
        PrintWriter out = new PrintWriter(new FileWriter("data/Volcanoes-piece.txt"));
        out.print("[ ");
        for (int i=0; i<data.getSampleNum(); ++i) {
            out.printf("%.2f ", data.getSample(i));
        }
        out.println("]");
        out.flush();
        out.close();
    }
}
