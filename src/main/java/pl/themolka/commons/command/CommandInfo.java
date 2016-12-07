package pl.themolka.commons.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandInfo {
    String[] name();

    String description() default "No description provided.";

    int min() default 0;

    String[] flags() default "";

    String usage() default "";

    boolean userOnly() default false;

    String permission() default "";

    String completer() default "";
}
