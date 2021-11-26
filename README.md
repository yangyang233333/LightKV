
<div align=center>
<img width="150" height="150" src="./images/logo.jpg" alt="Logo"/>
</div>

### ** LightKV 是一个玩具级KV数据库，无法用于生产环境！**

***

LightKV 基于简单的bitcask模型，代码量不足200行，数据文件布局类似 LSM Tree 中的 WAL 日志，纯Java实现，易于理解和学习。



## 特性

* 支持的数据结构：字符串
* 适合多写少读场景
* 支持数据融合，减少磁盘数据的冗余



## (蛇皮)性能测试，不可当真

和Redis对比，随机插入、查找、删除10W次，耗时如下：

|          | LightKV | Redis |
| -------- | ------- | ----- |
| 耗时(ms) | 2632    | 42705 |



## 微信公众号

欢迎关注微信公众号，一起造轮子。

<img src="./images/wechat.jpg" width="200px" align="left" alt="WeChat"/>
