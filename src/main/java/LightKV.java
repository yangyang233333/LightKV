
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class LightKV {
    //数据库
    public File db;
    //数据库路径
    public String db_path;
    //内存中的数据库索引，即key->(offset, length)的映射
    //注意此处为了方便，把(offset, length)包装成String
    public HashMap<String, String> index;
    //写入指针
    public long offset;
    //序列化
    ObjectMapper objectMapper;
    //写入操作: O(1)
    PrintStream printStream;
    //读取操作: O(1)
    RandomAccessFile randomAccessFile;

    LightKV(String db_path) throws IOException {
        this.db_path = db_path;
        this.index = new HashMap<>();
        this.offset = 0;
        this.objectMapper = new ObjectMapper();
        this.db = new File(this.db_path);
//        如果数据库不存在，则创建数据库
        if (!db.exists()) {
            this.db.createNewFile();
        }
        //写入操作
        this.printStream = new PrintStream(new FileOutputStream(db, true));
        //读取操作
        this.randomAccessFile = new RandomAccessFile(this.db_path, "r");
    }

    //插入k-v键值对
    public boolean setCmd(String key, String value) throws IOException {
        //k-v包装成Entry
        Entry entry = new Entry(key, value, "SET");
        //序列化为JSON
        String json = this.objectMapper.writeValueAsString(entry);
        //加索引
        String offset_length = this.offset + "," + json.length();
        this.index.put(key, offset_length);
        this.offset += json.length();
        //写入数据库
        this.printStream.print(json);
        return true;
    }

    //获取键key, 返回null表示不存在
    public Entry getCmd(String key) throws IOException {
        if (index.containsKey(key)) {
            String[] offset_length = index.get(key).split(",");
            //文件指针
            long offset = Long.parseLong(offset_length[0]);
            //键key所在Entry被序列化后的长度
            int length = Integer.parseInt(offset_length[1]);
            //设置读取位置，即从offset处开始读
            randomAccessFile.seek(offset);
            //用来保存Entry
            byte[] entry_barray = new byte[length];
            randomAccessFile.read(entry_barray, 0, length);
            //json反序列化为对象
            String json = new String(entry_barray);
            return objectMapper.readValue(json, Entry.class);
        } else {
            return null;
        }
    }

    //删除键key
    //LSM架构中的删除实际上是通过写入来实现的，具体见LSM相关文章
    public Entry delCmd(String key) throws IOException {
        //删除键, 当键存在时才可以删除
        if (this.index.containsKey(key)) {
            //k-v包装成Entry
            Entry entry = new Entry(key, null, "DEL");
            //序列化为JSON
            String json = this.objectMapper.writeValueAsString(entry);
            //加索引
            String offset_length = this.offset + "," + json.length();
            this.index.put(key, offset_length);
            this.offset += json.length();
            //写入数据库
            this.printStream.print(json);
        }
        Entry res = this.getCmd(key);
        //删除键
        this.index.remove(key);
        return res;
    }

    //数据融合，消除冗余空间
    public void merge() throws IOException {
        //获取所有的键，写入到新的文件中
        Set<String> keys = index.keySet();
        //创建新文件作为数据库载体
        String new_db_path = this.db_path.split("\\.")[0] + "_new.db";
        File new_db = new File(new_db_path);
        long new_offset = 0;
        //写入操作
        PrintStream new_printStream = new PrintStream(new FileOutputStream(new_db, true));
        Entry new_entry;
        String new_json, new_offset_length;
        for (String key : keys) {
            new_entry = this.getCmd(key);
            //序列化
            new_json = objectMapper.writeValueAsString(new_entry);
            //持久化到数据库
            new_printStream.print(new_json);
            //更新索引
            new_offset_length = new_offset + "," + new_json.length();
            index.put(key, new_offset_length);
            new_offset += new_json.length();
        }
        //用新数据库替换旧数据库
        this.db_path = new_db_path;
        this.db =new_db;
        this.offset = new_offset;
        this.printStream = new_printStream;
        this.randomAccessFile = new RandomAccessFile(new_db_path, "r");
    }
}
