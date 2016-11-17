package shazam.hash;

/**
 * Created by Administrator on 2016/10/20.
 */
public class Hash {
    public int f1;       // the first frequency peak
    public int f2;       // the second frequency peak
    public int dt;       // the time difference between two frequency peaks
    public int offset;     // the offset to the beginning of music
    public int song_id;         // the name of the song
    
    public Hash() {
        
    }

    public int getHashID() {
        return (dt << 18) | (f1 << 9) | f2;
    }

    public void setHashID(int hashID) {
        dt = hashID >> 18;
        f1 = (hashID & (0x3ffff)) >> 9;
        f2 = hashID & 0x1ff;
    }
}
