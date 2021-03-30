package publib.tools;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {

	public static Object newArrayInstance(Class<?> c, int len){
		Object array = null;
		array = Array.newInstance(c, len);
		return array;
	}
	
	public static Object newObjectInstance(Class<?> c)
	{
		Object instance = null;
		try {
			instance = c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
	
	public static Field getDeclaredField(Object object, String propertyName)
			throws NoSuchFieldException {
		return getDeclaredField(object.getClass(), propertyName);
	}
	
	@SuppressWarnings("rawtypes")
	public static Field getDeclaredField(Class clazz, String propertyName)
			throws NoSuchFieldException {
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(propertyName);//根据变量名propertyName获得域
			} catch (NoSuchFieldException e) {
			}
		}
		throw new NoSuchFieldException("No such field: " + clazz.getName()
				+ '.' + propertyName);
	}

	public static Object forceGetProperty(Object object, String propertyName)
			throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {

		Field field = getDeclaredField(object, propertyName);

		boolean accessible = field.isAccessible();// 判断是否可以访问
		field.setAccessible(true);// 设置字段可访问，即暴力反射

		Object result = null;

		result = field.get(object);// 在field对象上获取object的值

		field.setAccessible(accessible);// 设置字段是否可以访问
		return result;
	}

	public static void forceSetProperty(Object object, String propertyName,
                                        Object newValue) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

		Field field = getDeclaredField(object, propertyName);
		boolean accessible = field.isAccessible();
		field.setAccessible(true);
		field.set(object, newValue);// 给filed对象的object字段设置value值
		field.setAccessible(accessible);
	}

	public static Object forceInvokeMethod(Object object, String methodName,
                                           Object... params) throws NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
		Class<?>[] types = new Class[params.length];
		for (int i = 0; i < params.length; i++) {
			types[i] = params[i].getClass();
		}

		Class<?> clazz = object.getClass();
		Method method = null;
		for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				method = superClass.getDeclaredMethod(methodName, types);
				break;
			} catch (NoSuchMethodException e) {

			}
		}

		if (method == null)
			throw new NoSuchMethodException("No Such Method:"
					+ clazz.getSimpleName() + methodName);

		boolean accessible = method.isAccessible();
		method.setAccessible(true);
		Object result = null;

		result = method.invoke(object, params);

		method.setAccessible(accessible);
		return result;
	}

}
