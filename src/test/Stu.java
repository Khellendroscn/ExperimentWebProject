package test;

import net.khe.db2.annotations.*;

/**
 * Created by hyc on 2017/3/17.
 */
@DBTable
public class Stu{
    @SqlString(9)
    @Constraints(primaryKey = true,alloNull = false)
    private String id;
    @SqlString
    private String name;
    @SqlInt
    private int age;
    @Foreign("test.Class_")
    @SqlChars(2)
    private String classId;
    public Stu(){}
    public Stu(String id, String name, int age, String classId){
        this.id = id;
        this.name = name;
        this.age = age;
        this.classId = classId;
    }
    @Override
    public String toString(){
        return String.format("Stu(%s,%s,%d,%s)",id,name,age,classId);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }
}