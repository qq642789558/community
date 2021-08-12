package com.dongppman.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

///用来描述这个注解可以用在哪里
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)//运行时才有效
public @interface LoginRequired {

}
