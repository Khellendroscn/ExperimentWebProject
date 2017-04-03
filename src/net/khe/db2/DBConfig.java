package net.khe.db2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by hyc on 2017/3/2.
 * �������ݿ������ļ�����
 * ������Ϣ��
 * driver:���ݿ�����
 * dbType:���ݿ�����
 * host:������
 * port���˿�
 * user:�û���
 * passwd:����
 * dbName:Ҫ���ʵ����ݿ���
 */
public class DBConfig {
    public final String driver;
    public final String url;
    public final String user;
    public final String passwd;

    /**
     * @param configFile �����ļ�
     * @throws IOException �ļ���ȡʧ��
     */
    public DBConfig(File configFile)throws IOException{
        Properties props = new Properties();
        FileInputStream is = new FileInputStream(configFile);
        props.load(is);
        this.driver = props.getProperty("driver");
        String charset = props.getProperty("charset");
        String urlPattern = "jdbc:%s://%s:%s/%s";
        String url = String.format(urlPattern,
                props.getProperty("dbType"),
                props.getProperty("host"),
                props.getProperty("port"),
                props.getProperty("dbName"));
        if(charset!=null){
            url+="?use?useUnicode=true&amp;characterEncoding="+charset;
        }
        this.url = url;
        this.user = props.getProperty("user");
        this.passwd = props.getProperty("passwd");
    }

    /**
     * @param filename �����ļ��ļ���
     * @throws IOException �ļ���ȡʧ��
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
