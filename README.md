# 基于Shazam算法的听歌识曲小程序

程序有两个入口，一个是MusicArchiver，一个是MusicSearcher。目前只能处理PCM WAV，16-bit，单声道，44100Hz的音频，不能访问麦克风，也不支持其他格式的音频。我有一个已经转好格式的乐库，共197首歌，百度云[训练集和测试集音乐 提取码4zk2](http://pan.baidu.com/s/1qXYTDGo#list/path=%2F%E9%9F%B3%E4%B9%90%E5%BA%93)

## MusicArchiver用法
在命令行运行MusicArchiver，按照提示，输入曲库文件夹的路径。回车之后即开始建库。

## MusicSearcher用法
在命令行运行MusicSearcher，按照提示，输入录音文件的路径。回车之后即开始匹配，匹配结果按照打分从高到低排序并打印。