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
    SqlSelect(DataBase db, Class<T> cls) throws DBQuaryException {
        try {
            this.db = db;
            this.cls = cls;
            if (!basicSqlMap.containsKey(cls)) {
                basicSqlMap.put(cls, prepareSql());
            }
        }catch (Exception e){
            throw new DBQuaryException(e);
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
     * @throws DBQuaryException 数据库查询错误
     */
    public Table<T> execute() throws DBQuaryException {
        try {
            String sql = getSql();
            Statement stmt = db.getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return new Table<T>(db, rs, cls);
        }catch (Exception e){
            throw new DBQuaryException(e);
        }
    }
    public DBQuery<T> query()throws DBQuaryException{
        ResultSet rs = getResultSet();
        return new ObjectQuery<T>(rs,db,cls);
    }
    ResultSet getResultSet() throws DBQuaryException {
        try {
            String sql = getSql();
            Statement stmt = db.getConn().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        }catch (Exception e){
            throw new DBQuaryException(e);
        }
    }
    private String getSql(){
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
        return sql;
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
