package com.magic.magic_annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by : mr.lu
 * Created at : 2019-05-11 at 19:06
 * Description:只在某个buildType生效
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface OnlyAvailable {

    String value();
}
