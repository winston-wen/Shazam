package shazam.hash;

/**
 * Created by Administrator on 2016/10/20.
 */
public class ShazamHash {
    private int f1;       // the first frequency peak
    private int f2;       // the second frequency peak
    private int dt;       // the time difference between two frequency peaks
    private int offset;     // the offset to the beginning of music
    private int song_id;         // the name of the song

    public ShazamHash(int f1, int f2, int dt, int offset, int song_id) {
        this.f1 = f1;
        this.f2 = f2;
        this.dt = dt;
        this.offset = offset;
        this.song_id = song_id;
    }

    public ShazamHash() {

    }

    public int getF1() {
        return f1;
    }

    public void setF1(int f1) {
        this.f1 = f1;
    }

    public int getF2() {
        return f2;
    }

    public void setF2(int f2) {
        this.f2 = f2;
    }

    public int getDt() {
        return dt;
    }

    public void setDt(int dt) {
        this.dt = dt;
    }

    public int getHashID() {
        return (dt << 18) | (f1 << 9) | f2;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSong_id() {
        return song_id;
    }

    public void setSong_id(int song_id) {
        this.song_id = song_id;
    }

    public void setHashID(int hashID) {
        dt = hashID >> 18;
        f1 = (hashID & (0x3ffff)) >> 9;
        f2 = hashID & 0x1ff;
    }
}
