package com.vaynenet.www.plugins.datasources;

import java.lang.annotation.*;

/**
 * Created by Joe on 2019/11/28.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    /**
     * 数据源key值
     * @return
     */
    String value();

}
