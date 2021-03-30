package publib.tools;

/**
 * 对应XML元素的类型
 * @version 
 * @author spy
 * 2015年5月6日
 */
public enum XmlFieldType {
	/**
	 * 结点属性
	 */
	ATTR,
	/**
	 * 结点文本
	 */
	TEXT,
	/**
	 * 子结点
	 */
	OBJECT,
	/**
	 * 多个子结点，数组呈现
	 */
	ARRAY
}
