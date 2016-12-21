# 基于Shazam算法的听歌识曲小程序

程序有两个入口，一个是MusicArchiver，一个是MusicSearcher。目前只能处理PCM WAV，16-bit，单声道，44100Hz的音频，不能访问麦克风，也不支持其他格式的音频。

## MusicArchiver用法
在命令行运行MusicArchiver，按照提示，输入曲库文件夹的路径。回车之后即开始建库。

## MusicSearcher用法
在命令行运行MusicSearcher，按照提示，输入录音文件的路径。回车之后即开始匹配，匹配结果按照打分从高到低排序并打印。