package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Created by hyc on 2017/3/18.
 * 表示sql查询操作
 */
public class SqlSelect<T> {
    static Map<Class<?>,String> basicSqlMap = new HashMap<>();
    private Class<T> cls;
    private DataBase db;
    private Set<String> wheres = new HashSet<>();
    private Set<String> orders = new LinkedHashSet<>();
    SqlSelect(DataBase db, Class<T> cls) throws
            KeyNotFoundException,
            ClassNotFoundException {
        this.db = db;
        this.cls = cls;
        if(!basicSqlMap.containsKey(cls)){
            basicSqlMap.put(cls,prepareSql());
        }
    }

    /**
     * 加入where语句进行筛选
     * @param filters where语句
     * @return this
     */
    public SqlSelect where(String... filters){
        wheres.addAll(Arrays.asList(filters));
        return this;
    }

    /**
     * 加入order by语句进行排序
     * @param order order by语句
     * @return this
     */
    public SqlSelect orderBy(String... order){
        orders.addAll(Arrays.asList(order));
        return this;
    }

    /**
     * 执行查询操作
     * @return 数据表
     * @throws SQLException sql异常
     * @throws NoSuchMethodException setter不存在异常，请检查是否缺失setter
     * @throws IllegalAccessException 访问权限冲突，请检查setter和默认构造器的访问权限
     * @throws InstantiationException 实例初始化失败，请检查Bean是否缺少默认构造函数
     * @throws InvocationTargetException 方法调用失败，请检查setter方法签名
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     */
    public Table<T> execute() throws
            SQLException,
            NoSuchMethodException,
            IllegalAccessException,
            InstantiationException,
            InvocationTargetException,
            KeyNotFoundException, ClassNotFoundException {
        String sql = basicSqlMap.get(cls);
        if(!wheres.isEmpty()){
            sql += "\nWHERE "+
                    wheres.stream()
                    .collect(joining(" AND "));
        }
        if(!orders.isEmpty()){
            sql += "\nORDER BY"+
                    orders.stream()
                    .collect(joining(", "));
        }
        Statement stmt = db.getConn().createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return new Table<T>(db,rs,cls);
    }
    private List<String> prepareCols() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = DataBase.lookUp(cls);
        return meta.getFields().stream()
                .map(field->field.getName())
                .collect(toList());
    }
    private List<String> prepareFroms() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = DataBase.lookUp(cls);
        return meta.getTables().stream()
                .collect(toList());
    }
    private String prepareSql() throws
            KeyNotFoundException,
            ClassNotFoundException {
        List<String> cols = prepareCols();
        List<String> froms = prepareFroms();
        String sql = "SELECT "+
                cols.stream()
                .collect(joining(", "))+
                "\nFROM "+
                froms.stream()
                .collect(joining(", "));
        return sql;
    }
}
