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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.lang.Nullable;

/**
 *
 * 方法调用的bean工厂
 *
 *
 * FactoryBean 返回一个静态或实例方法调用结果的值。
 *
 * 对于大多数用例而言，
 * 最好只是将容器的内置工厂方法支持用于相同的目的，因为它在转换参数方面更为明智。
 * 尽管当你需要调用一个不返回任何值的方法（例如，强制某种初始化发生的静态类方法）时，
 * 这个工厂bean仍然很有用。该用例不受工厂方法支持，因为需要返回值来获取bean实例。
 * <p>请注意，由于预计主要用于访问工厂方法，因此该工厂默认以<b>单例</ b>方式运行。
 * 拥有bean工厂的getObject的第一个请求将导致方法调用，它的返回值将被缓存用于后续请求。
 * 一个内部setSingleton属性可能被设置为“false”，导致该工厂在每次询问对象时调用目标方法。 <P> <B>

 注意：如果您的目标方法未产生公开结果，请考虑使用MethodInvokingBean，
 这样可避免此MethodInvokingFactoryBean附带的类型确定和生命周期限制。</ b> <p>
 此调用程序支持任何类型的目标方法。可以通过将setTargetMethod属性设置为表示静态方法名称的String来指定静态方法
 ，并使用setTargetClass指定静态方法所在的Class。或者，可以通过将setTargetObject属性设置为目标对象，
 并将setTargetMethod属性指定为要在该目标对象上调用的方法的名称来指定目标实例方法。
 方法调用的参数可以通过设置setArguments参数来指定。
 <p>根据InitializingBean契约，一旦设置了所有属性，此类就取决于afterPropertiesSet（）被调用


 * {@link FactoryBean} which returns a value which is the result of a static or instance
 * method invocation. For most use cases it is better to just use the container's
 * built-in factory method support for the same purpose, since that is smarter at
 * converting arguments. This factory bean is still useful though when you need to
 * call a method which doesn't return any value (for example, a static class method
 * to force some sort of initialization to happen). This use case is not supported
 * by factory methods, since a return value is needed to obtain the bean instance.
 *
 * <p>Note that as it is expected to be used mostly for accessing factory methods,
 * this factory by default operates in a <b>singleton</b> fashion. The first request
 * to {@link #getObject} by the owning bean factory will cause a method invocation,
 * whose return value will be cached for subsequent requests. An internal
 * {@link #setSingleton singleton} property may be set to "false", to cause this
 * factory to invoke the target method each time it is asked for an object.
 *
 * <p><b>NOTE: If your target method does not produce a result to expose, consider
 * {@link MethodInvokingBean} instead, which avoids the type determination and
 * lifecycle limitations that this {@link MethodInvokingFactoryBean} comes with.</b>
 *
 * <p>This invoker supports any kind of target method. A static method may be specified
 * by setting the {@link #setTargetMethod targetMethod} property to a String representing
 * the static method name, with {@link #setTargetClass targetClass} specifying the Class
 * that the static method is defined on. Alternatively, a target instance method may be
 * specified, by setting the {@link #setTargetObject targetObject} property as the target
 * object, and the {@link #setTargetMethod targetMethod} property as the name of the
 * method to call on that target object. Arguments for the method invocation may be
 * specified by setting the {@link #setArguments arguments} property.
 *
 * <p>This class depends on {@link #afterPropertiesSet()} being called once
 * all properties have been set, as per the InitializingBean contract.
 *
 * <p>An example (in an XML based bean factory definition) of a bean definition
 * which uses this class to call a static factory method:
 *
 * <pre class="code">
 * &lt;bean id="myObject" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 *   &lt;property name="staticMethod" value="com.whatever.MyClassFactory.getInstance"/>
 * &lt;/bean></pre>
 *
 * <p>An example of calling a static method then an instance method to get at a
 * Java system property. Somewhat verbose, but it works.
 *
 * <pre class="code">
 * &lt;bean id="sysProps" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 *   &lt;property name="targetClass" value="java.lang.System"/>
 *   &lt;property name="targetMethod" value="getProperties"/>
 * &lt;/bean>
 *
 * &lt;bean id="javaVersion" class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
 *   &lt;property name="targetObject" ref="sysProps"/>
 *   &lt;property name="targetMethod" value="getProperty"/>
 *   &lt;property name="arguments" value="java.version"/>
 * &lt;/bean></pre>
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 21.11.2003
 * @see MethodInvokingBean
 * @see org.springframework.util.MethodInvoker
 */
public class MethodInvokingFactoryBean extends MethodInvokingBean implements FactoryBean<Object> {

	private boolean singleton = true;

	private boolean initialized = false;

	/** Method call result in the singleton case */
	@Nullable
	private Object singletonObject;


	/**
	 * Set if a singleton should be created, or a new object on each
	 * {@link #getObject()} request otherwise. Default is "true".
	 */
	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
		if (this.singleton) {
			this.initialized = true;
			this.singletonObject = invokeWithTargetException();
		}
	}


	/**
	 * Returns the same value each time if the singleton property is set
	 * to "true", otherwise returns the value returned from invoking the
	 * specified method on the fly.
	 */
	@Override
	@Nullable
	public Object getObject() throws Exception {
		if (this.singleton) {
			if (!this.initialized) {
				throw new FactoryBeanNotInitializedException();
			}
			// Singleton: return shared object.
			return this.singletonObject;
		}
		else {
			// Prototype: new object on each call.
			return invokeWithTargetException();
		}
	}

	/**
	 * Return the type of object that this FactoryBean creates,
	 * or {@code null} if not known in advance.
	 */
	@Override
	public Class<?> getObjectType() {
		if (!isPrepared()) {
			// Not fully initialized yet -> return null to indicate "not known yet".
			return null;
		}
		return getPreparedMethod().getReturnType();
	}

	@Override
	public boolean isSingleton() {
		return this.singleton;
	}

}
