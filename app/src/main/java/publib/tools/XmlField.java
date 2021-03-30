package publib.tools;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记对应XML属性
 * @version 
 * @author spy
 * 2015年5月6日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlField {
	public String fieldName();
	
	public XmlFieldType fieldType() default XmlFieldType.ATTR;
}
