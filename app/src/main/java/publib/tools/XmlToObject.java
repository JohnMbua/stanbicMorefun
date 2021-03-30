package publib.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;

public class XmlToObject {

	public static Object xmlToObject(String xmlFile, Class<?> c){
		Object instance = ReflectUtils.newObjectInstance(c);
		XmlNode node = new XmlNode();
		node.decodeXml(xmlFile);
		xmlToObject(node, instance);
		return instance;
	}
	
	public static Object xmlToObject(XmlNode node, Class<?> c){
		Object instance = ReflectUtils.newObjectInstance(c);
		xmlToObject(node, instance);
		return instance;
	}
	public static Object xmlToObject(XmlNode node, Object instance){
		try{
			Field[] fields = instance.getClass().getDeclaredFields();
			
			XmlNode tmpNode = null;
			XmlNode[] arrayNode = null;
			Object obj = null;
			Object array = null;
			
			for (Field f : fields) {
				XmlField xmlField = f.getAnnotation(XmlField.class);
				if (xmlField != null) {
					String fieldName = xmlField.fieldName();
					switch(xmlField.fieldType()){
					case ATTR:
						ReflectUtils.forceSetProperty(instance, f.getName(), 
													converValue(node.attrs.get(fieldName),f.getType()));
						break;
						
					case ARRAY:
						arrayNode = node.getChildren(f.getName());
						array = ReflectUtils.forceGetProperty(instance, f.getName());
						if(array == null){
							array = ReflectUtils.newArrayInstance(f.getType().getComponentType(), arrayNode.length);
							ReflectUtils.forceSetProperty(instance, f.getName(), array);
						}
						int len = Math.min(arrayNode.length, Array.getLength(array));
						for(int i=0; i<len; i++){
							obj = ReflectUtils.newObjectInstance(f.getType().getComponentType());
							xmlToObject(arrayNode[i], obj);
							Array.set(array, i, obj);
						}
						break;
						
					case OBJECT:
						obj = ReflectUtils.forceGetProperty(instance, f.getName());
						if(obj == null){
							obj = ReflectUtils.newObjectInstance(f.getType());
							ReflectUtils.forceSetProperty(instance, f.getName(), obj);
						}
						tmpNode = node.getChild(f.getName());
						if(tmpNode == null){
							throw new Exception("no this xml node:" + f.getName());
						}
						xmlToObject(tmpNode, obj);
						break;
						
					case TEXT:
						break;
						
					default:
						break;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException("Xml to Object fialed!", e);
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	protected static <T extends Object> T converValue(String value, Class<T> targetClazz) {
		if(targetClazz.equals(BigDecimal.class)){
			return (T)new BigDecimal(value);
		}else if(targetClazz.equals(String.class)){
			return (T)value;
		}else if (targetClazz.equals(Integer.class) || targetClazz.equals(int.class)) {
			return (T) Integer.valueOf(value);
		} else if (targetClazz.equals(Boolean.class) || targetClazz.equals(boolean.class)) {
			return (T) Boolean.valueOf(value);
		} else if (targetClazz.equals(Character.class) || targetClazz.equals(char.class)) {
			return (T) Character.valueOf(value.charAt(0));
		} else if (targetClazz.isArray()) {
			return (T) value.getBytes();
		} else {
			throw new RuntimeException("not supported type!"
					+ targetClazz.getName());
		}
	}
}
