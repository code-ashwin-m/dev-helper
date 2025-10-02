package org.ashwin.service.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbConfig {
    String url();
    String username();
    String password();
    String driver() default "com.mysql.cj.jdbc.Driver";
}
