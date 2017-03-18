package net.khe.db2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by hyc on 2017/3/2.
 * 加载数据库配置文件的类
 * 配置信息：
 * driver:数据库驱动
 * dbType:数据库类型
 * host:主机名
 * port：端口
 * user:用户名
 * passwd:密码
 * dbName:要访问的数据库名
 */
public class DBConfig {
    public final String driver;
    public final String url;
    public final String user;
    public final String passwd;

    /**
     * @param configFile 配置文件
     * @throws IOException 文件读取失败
     */
    public DBConfig(File configFile)throws IOException{
        Properties props = new Properties();
        FileInputStream is = new FileInputStream(configFile);
        props.load(is);
        this.driver = props.getProperty("driver");
        String urlPattern = "jdbc:%s://%s:%s/%s";
        this.url = String.format(urlPattern,
                props.getProperty("dbType"),
                props.getProperty("host"),
                props.getProperty("port"),
                props.getProperty("dbName"));
        this.user = props.getProperty("user");
        this.passwd = props.getProperty("passwd");
    }

    /**
     * @param filename 配置文件文件名
     * @throws IOException 文件读取失败
     */
    public DBConfig(String filename) throws IOException {
        this(new File(filename));
    }
    @Override
    public String toString(){
        return "DBConfig("+
                this.driver+", "+
                this.url+", "+
                this.user+", "+
                this.passwd+")";
    }

    public static void main(String[] args) {
        try {
            DBConfig config = new DBConfig(new File("DBConfig.txt"));
            System.out.println(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
