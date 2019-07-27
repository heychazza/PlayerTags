package gg.plugins.playertags.command.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Command {
    String[] aliases();
    
    String about();
    
    String permission();
    
    String usage() default "";
}
