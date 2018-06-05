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

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.support.PropertiesLoaderSupport;
import org.springframework.lang.Nullable;

/**
 * 属性工厂bean
 * 允许在bean工厂中将类路径下的可以用的propertise文件当做一个properties实例
 * 通过一个bean引用 可以将其注入到任意的bean Properties类型的属性上。
 * Allows for making a properties file from a classpath location available
 * as Properties instance in a bean factory. Can be used to populate
 * any bean property of type Properties via a bean reference.
 * 支持从一个properties文件内或者在当前FactoryBean设置的本地propertise加载。
 * 创建的Properties实例由本地值和加载的值合并。如果本地propertiese和类路径下没有设置将在初始化的时候抛出异常。
 * <p>Supports loading from a properties file and/or setting local properties
 * on this FactoryBean. The created Properties instance will be merged from
 * loaded and local values. If neither a location nor local properties are set,
 * an exception will be thrown on initialization.
 * 可以创建单例或者每个请求创建一个新的实例。默认是单例。
 * <p>Can create a singleton or a new object on each request.
 * Default is a singleton.
 *
 * @author Juergen Hoeller
 * @see #setLocation
 * @see #setProperties
 * @see #setLocalOverride
 * @see java.util.Properties
 */
public class PropertiesFactoryBean extends PropertiesLoaderSupport
		implements FactoryBean<Properties>, InitializingBean {

	private boolean singleton = true;

	@Nullable
	private Properties singletonInstance;


	/**
	 * Set whether a shared 'singleton' Properties instance should be
	 * created, or rather a new Properties instance on each request.
	 * <p>Default is "true" (a shared singleton).
	 */
	public final void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public final boolean isSingleton() {
		return this.singleton;
	}


	@Override
	public final void afterPropertiesSet() throws IOException {
		if (this.singleton) {
			this.singletonInstance = createProperties();
		}
	}

	@Override
	@Nullable
	public final Properties getObject() throws IOException {
		if (this.singleton) {
			return this.singletonInstance;
		}
		else {
			return createProperties();
		}
	}

	@Override
	public Class<Properties> getObjectType() {
		return Properties.class;
	}


	/**
	 * Template method that subclasses may override to construct the object
	 * returned by this factory. The default implementation returns the
	 * plain merged Properties instance.
	 * <p>Invoked on initialization of this FactoryBean in case of a
	 * shared singleton; else, on each {@link #getObject()} call.
	 * @return the object returned by this factory
	 * @throws IOException if an exception occurred during properties loading
	 * @see #mergeProperties()
	 */
	protected Properties createProperties() throws IOException {
		return mergeProperties();
	}

}
