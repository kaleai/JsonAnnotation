package kale.net.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import kale.net.json.constant.DefaultValue;

/**
 * @author Jack Tony
 * @date 2015/8/13
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Json2Model {

    String modelName() default DefaultValue.ROOT_CLASS;
    
    String jsonStr() default DefaultValue.NULL;

    String packageName() default DefaultValue.NULL;
}
