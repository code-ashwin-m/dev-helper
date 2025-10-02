package org.ashwin.service.annotations;

import org.ashwin.service.DefaultIdGenerator;
import org.ashwin.service.enums.GenerationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GeneratedId {
    GenerationType strategy() default GenerationType.AUTO;
    Class<? extends IdGenerator> generator() default DefaultIdGenerator.class;
}


