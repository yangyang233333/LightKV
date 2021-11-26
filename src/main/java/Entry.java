import java.io.Serializable;

public class Entry implements Serializable {
    public String key;  //Entry的键
    public String value;  //Entry的数据
    public String type;  //Entry的类型："GET", "SET", "DEL"

    public Entry() {
    }

    public Entry(String key, String value, String type) {
        this.key = key;
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
