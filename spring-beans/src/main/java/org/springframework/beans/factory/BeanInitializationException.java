/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/**
 *
 * 如果一个bean的实现被建议在它自己的情况下抛出
 工厂感知的初始化代码失败


 * 一个bean实现中建议抛出的异常，如果它自己的工厂感知初始化代码失败的话
 * Exception that a bean implementation is suggested to throw if its own
 * factory-aware initialization code fails.
 *
 *  由bean工厂方法它们自己抛出的异常BeansException应该按照原来的方式传播
 * BeansExceptions thrown by
 * bean factory methods themselves should simply be propagated as-is.
 * 注意：afterPropertiesSet方法或者一个自定义的初始化方法可以抛出任意的异常
 * <p>Note that {@code afterPropertiesSet()} or a custom "init-method"
 * can throw any exception.
 *
 * @author Juergen Hoeller
 * @since 13.11.2003
 * @see BeanFactoryAware#setBeanFactory
 * @see InitializingBean#afterPropertiesSet
 */
@SuppressWarnings("serial")
public class BeanInitializationException extends FatalBeanException {

	/**
	 * Create a new BeanInitializationException with the specified message.
	 * @param msg the detail message
	 */
	public BeanInitializationException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeanInitializationException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeanInitializationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
