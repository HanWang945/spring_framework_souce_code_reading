/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * 方法调用bean
 * 简单的一个方法调用bean：仅仅调用一个目标方法，不期望一个结果暴露给容器
 * Simple method invoker bean: just invoking a target method, not expecting a result
 * to expose to the container (in contrast to {@link MethodInvokingFactoryBean}).
 *这个调用器支持任意种类的方法。
 * 一个静态方法可能通过setTargetMethod属性特别设置
 *
 *
 * 这个调用者支持任何一种目标方法。
 * 可以通过将{@link #setTargetMethod targetMethod}属性设置为表示静态方法名称的String来指定静态方法，
 * {@link #setTargetClass targetClass}指定静态方法在其上定义的Class。
 * 或者，可以通过将{@link #setTargetObject targetObject}属性设置为目标对象，
 * 并使用{@link #setTargetMethod targetMethod}属性作为要在该目标对象上调用的方法的名称来指定目标实例方法。
 * 方法调用的参数可以通过设置{@link #setArguments arguments}属性来指定。
 * <p>根据InitializingBean契约，一旦设置了所有属性，此类就取决于{@link #afterPropertiesSet（）}被调用
 *
 * <p>This invoker supports any kind of target method. A static method may be specified
 * by setting the {@link #setTargetMethod targetMethod} property to a String representing
 * the static method name, with {@link #setTargetClass targetClass} specifying the Class
 * that the static method is defined on.
 *
 * Alternatively, a target instance method may be
 * specified, by setting the {@link #setTargetObject targetObject} property as the target
 * object, and the {@link #setTargetMethod targetMethod} property as the name of the
 * method to call on that target object. Arguments for the method invocation may be
 * specified by setting the {@link #setArguments arguments} property.
 *
 * <p>This class depends on {@link #afterPropertiesSet()} being called once
 * all properties have been set, as per the InitializingBean contract.
 *
 * <p>An example (in an XML based bean factory definition) of a bean definition
 * which uses this class to call a static initialization method:
 *
 * <pre class="code">
 * &lt;bean id="myObject" class="org.springframework.beans.factory.config.MethodInvokingBean">
 *   &lt;property name="staticMethod" value="com.whatever.MyClass.init"/>
 * &lt;/bean></pre>
 *
 * <p>An example of calling an instance method to start some server bean:
 *
 * <pre class="code">
 * &lt;bean id="myStarter" class="org.springframework.beans.factory.config.MethodInvokingBean">
 *   &lt;property name="targetObject" ref="myServer"/>
 *   &lt;property name="targetMethod" value="start"/>
 * &lt;/bean></pre>
 *
 * @author Juergen Hoeller
 * @since 4.0.3
 * @see MethodInvokingFactoryBean
 * @see org.springframework.util.MethodInvoker
 */
public class MethodInvokingBean extends ArgumentConvertingMethodInvoker
		implements BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

	@Nullable
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	@Nullable
	private ConfigurableBeanFactory beanFactory;


	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
		return ClassUtils.forName(className, this.beanClassLoader);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	/**
	 * Obtain the TypeConverter from the BeanFactory that this bean runs in,
	 * if possible.
	 * @see ConfigurableBeanFactory#getTypeConverter()
	 */
	@Override
	protected TypeConverter getDefaultTypeConverter() {
		if (this.beanFactory != null) {
			return this.beanFactory.getTypeConverter();
		}
		else {
			return super.getDefaultTypeConverter();
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
		invokeWithTargetException();
	}

	/**
	 * Perform the invocation and convert InvocationTargetException
	 * into the underlying target exception.
	 */
	@Nullable
	protected Object invokeWithTargetException() throws Exception {
		try {
			return invoke();
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof Exception) {
				throw (Exception) ex.getTargetException();
			}
			if (ex.getTargetException() instanceof Error) {
				throw (Error) ex.getTargetException();
			}
			throw ex;
		}
	}

}
