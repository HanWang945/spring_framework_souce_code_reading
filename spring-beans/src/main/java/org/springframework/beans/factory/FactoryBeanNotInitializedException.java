/*
 * Copyright 2002-2012 the original author or authors.
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
 *    如果一个bean还没有完成的初始化（比如因为他调用一个循环引用）的时候 调用FactoryBean的getObject方法的时候抛出的异常
 * Exception to be thrown from a FactoryBean's {@code getObject()} method
 * if the bean is not fully initialized yet, for example because it is involved
 * in a circular reference.
 *
 *  注意：与FactoryBean的循环引用不能通过急切地缓存单例实例来解决，就像普通bean一样
 * <p>Note: A circular reference with a FactoryBean cannot be solved by eagerly
 * caching singleton instances like with normal beans.
 * 原因是每个FactoryBean在他返回创建bean之前需要一个完整的初始化，而仅仅只需要初始化特殊的正常bean。
 * 也就说，如果一个合作bean实际在初始化的时候调用他们 而不是仅仅保存引用
 * The reason is that every FactoryBean needs to be fully initialized before it can
 * return the created bean, while only specific normal beans need
 * to be initialized - that is, if a collaborating bean actually invokes
 * them on initialization instead of just storing the reference.
 *
 * @author Juergen Hoeller
 * @since 30.10.2003
 * @see FactoryBean#getObject()
 */
@SuppressWarnings("serial")
public class FactoryBeanNotInitializedException extends FatalBeanException {

	/**
	 * Create a new FactoryBeanNotInitializedException with the default message.
	 */
	public FactoryBeanNotInitializedException() {
		super("FactoryBean is not fully initialized yet");
	}

	/**
	 * Create a new FactoryBeanNotInitializedException with the given message.
	 * @param msg the detail message
	 */
	public FactoryBeanNotInitializedException(String msg) {
		super(msg);
	}

}
