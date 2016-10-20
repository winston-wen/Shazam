/**
 * Created by Administrator on 2016/10/20.
 * Querying the inverted index, and grading the musics.
 *
 */
package shazam.search;

/*
TODO:
每次匹配到指纹的时候可以直接把对应歌曲的名称作为key插入到一个map中（如果不存在）。
这个map是的key是歌名，value是一个arraylist<int>，是一个一对多的map。
value的每个int代表两个指纹的时间差。
而打分的目的就是给这些int做个直方图，直方图的最大区间的样本数越多，打分越高。
*/