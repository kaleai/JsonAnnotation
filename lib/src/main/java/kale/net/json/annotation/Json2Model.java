package kale.net.json.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Jack Tony
 * @date 2015/8/13
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Json2Model {

    String modelName();
    
    String jsonStr();

    // custom the model's package name
    String packageName() default "";
}
