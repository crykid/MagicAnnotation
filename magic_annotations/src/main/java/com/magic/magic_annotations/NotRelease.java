package com.magic.magic_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by : mr.lu
 * Created at : 2019-05-11 at 19:12
 * Description: 非生产环境
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface NotRelease {

}
