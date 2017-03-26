package net.khe.db2;

import net.khe.util.Generator;

import java.util.stream.Stream;

/**
 * Created by hyc on 2017/3/26.
 * DBQuary接口，继承生成器接口，并可以使用stream方法产生流
 * @param <T> 生成的元素类型
 */
public interface DBQuery<T> extends Generator<T> {
    /**
     * 该方法用于生成流，可使用java 8 stream接口对数据进行管道式操作
     * @return Stream对象
     */
    Stream<T> stream();
}
