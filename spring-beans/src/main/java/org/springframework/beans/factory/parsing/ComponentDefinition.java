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

package org.springframework.beans.factory.parsing;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * 组件定义
 * 在一些配置上下文中呈现的描述一组BeanDefinitions和BeanReference的逻辑视图的接口
 * Interface that describes the logical view of a set of {@link BeanDefinition BeanDefinitions}
 * and {@link BeanReference BeanReferences} as presented in some configuration context.
 *随着引入了NamespaceHandler可插入定制XML标记，现在可以为单个逻辑配置实体创建多个BeanDefinition和RuntimeBeanReferences，
 * 以便为终端用户提供更简洁的配置和更方便的服务。
 * <p>With the introduction of {@link org.springframework.beans.factory.xml.NamespaceHandler pluggable custom XML tags},
 * it is now possible for a single logical configuration entity, in this case an XML tag, to
 * create multiple {@link BeanDefinition BeanDefinitions} and {@link BeanReference RuntimeBeanReferences}
 * in order to provide more succinct configuration and greater convenience to end users.
 * 同样，不再假设每个配置的实体映射到一个BeanDefinition
 * As such, it can no longer be assumed that each configuration entity (e.g. XML tag) maps to one {@link BeanDefinition}.
 *
 * 对于那些希望展示可视化或支持配置Spring应用程序的工具供应商和其他用户来说，
 * 有一些机制可以将BeanFactory中的BeanDefinition与配置数据绑定在一起，从而对最终用户具有具体的意义。
 * For tool vendors and other users who wish to present visualization or support for configuring Spring applications
 * it is important that there is some mechanism in place to tie the {@link BeanDefinition BeanDefinitions}
 * in the {@link org.springframework.beans.factory.BeanFactory} back to the configuration data in a way that has concrete meaning to the end user.
 *因此，NamespaceHandler实现能够以ComponentDefinition的形式发布事件，每个逻辑实体被配置
 * As such, {@link org.springframework.beans.factory.xml.NamespaceHandler}
 * implementations are able to publish events in the form of a {@code ComponentDefinition} for each
 * logical entity being configured.
 *
 * 第三方可以使用@link ReaderEventListener订阅这些事件，从而允许以用户为中心的bean元数据视图。
 * Third parties can then {@link ReaderEventListener subscribe to these events},
 * allowing for a user-centric view of the bean metadata.
 *  每个ComponentDefinition都有一个 getsource方法获取源对象，它是特定于配置的
 * <p>Each {@code ComponentDefinition} has a {@link #getSource source object} which is configuration-specific.
 * 在基于XML配置的情况下，这通常是Node，它包含用户提供的配置信息。
 * In the case of XML-based configuration this is typically the {@link org.w3c.dom.Node} which contains the user
 * supplied configuration information.
 *除此之外，每个被暴露在一个ComponentDefinition里面的BeanDefinition也有他自己的 BeanDefinition getSource方法，它可能指向一个不同的、更具体的配置数据集。，
 * In addition to this, each {@link BeanDefinition} enclosed in a
 * {@code ComponentDefinition} has its own {@link BeanDefinition#getSource() source object} which may point
 * to a different, more specific, set of configuration data.
 *除此之外，个别的bean元数据片段像PropertyValues，可能还有一个源对象给出一个更大的细节级别
 * Beyond this, individual pieces of bean metadata such as the {@link org.springframework.beans.PropertyValue PropertyValues}
 * may also have a source object giving an even greater level of detail.
 *  抽取出的源对象通过可以按照需求自定义的SourceExtractor来处理
 * Source object extraction is handled through the
 * {@link SourceExtractor} which can be customized as required.
 *
 * 然而，通过getBeanReferences方法可以直接访问重要的BeanReferences，但是工具可能希望检查所有的BeanDefinition并且聚集完整的一套BeanReference
 *
 * <p>Whilst direct access to important {@link BeanReference BeanReferences} is provided through
 * {@link #getBeanReferences}, tools may wish to inspect all {@link BeanDefinition BeanDefinitions} to gather
 * the full set of {@link BeanReference BeanReferences}.
 *
 * 实现被要求提供所有的BeanReferences，这些引用是用来验证整个逻辑实体的配置的，以及提供完整的用户可视化配置所需的
 * Implementations are required to provide
 * all {@link BeanReference BeanReferences} that are required to validate the configuration of the
 * overall logical entity as well as those required to provide full user visualisation of the configuration.
 *
 * 预计某些@link BeanReference BeanReference对于验证或配置的用户视图并不重要，因此这些引用可能会被拒绝。
 * It is expected that certain {@link BeanReference BeanReferences} will not be important to
 * validation or to the user view of the configuration and as such these may be ommitted.
 *一个工具可能希望通过提供的@link BeanDefinition BeanDefinition来显示任何额外的@link BeanReference BeanReference，但这并不被认为是一个典型的例子。
 * A tool may wish to display any additional {@link BeanReference BeanReferences} sourced through the supplied
 * {@link BeanDefinition BeanDefinitions} but this is not considered to be a typical case.
 *
 *
 * 工具可以通过检查BeanDefinition的方法getRole获取角色的标识符，来决定所包含的BeanDefinition的重要性
 * <p>Tools can determine the important of contained {@link BeanDefinition BeanDefinitions} by checking the
 * {@link BeanDefinition#getRole role identifier}.
 * 这些角色本质上是对工具的一个提示，说明配置提供者认为一个BeanDefinition对于终端用户是如何重要
 * The role is essentially a hint to the tool as to how important the configuration provider believes a {@link BeanDefinition} is to the end user.
 *
 *预料之中的是，工具将不展示所有的BeanDefinition对于一个给定的ComponentDefinition，相反选择要给基于角色的过滤器
 * It is expected that tools will <strong>not</strong> display all {@link BeanDefinition BeanDefinitions} for a given
 * {@code ComponentDefinition} choosing instead to filter based on the role.
 *工具可以选择使这个过滤用户可配置。应该特别注意@link BeanDefinition roleinfrastructure基础设施角色标识符
 * Tools may choose to make this filtering user configurable. Particular notice should be given to the
 * {@link BeanDefinition#ROLE_INFRASTRUCTURE INFRASTRUCTURE role identifier}.
 *  根据角色分类的BeanDefinition是完成的不重要，对于终端用户来讲并且这些仅仅被内部的实现所需要的原因
 * {@link BeanDefinition BeanDefinitions}
 * classified with this role are completely unimportant to the end user and are required only for
 * internal implementation reasons.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 * @see AbstractComponentDefinition
 * @see CompositeComponentDefinition
 * @see BeanComponentDefinition
 * @see ReaderEventListener#componentRegistered(ComponentDefinition)
 */
public interface ComponentDefinition extends BeanMetadataElement {

	/**
	 * Get the user-visible name of this {@code ComponentDefinition}.
	 * <p>This should link back directly to the corresponding configuration data
	 * for this component in a given context.
	 */
	String getName();

	/**
	 * Return a friendly description of the described component.
	 * <p>Implementations are encouraged to return the same value from
	 * {@code toString()}.
	 */
	String getDescription();

	/**
	 * Return the {@link BeanDefinition BeanDefinitions} that were registered
	 * to form this {@code ComponentDefinition}.
	 * <p>It should be noted that a {@code ComponentDefinition} may well be related with
	 * other {@link BeanDefinition BeanDefinitions} via {@link BeanReference references},
	 * however these are <strong>not</strong> included as they may be not available immediately.
	 * Important {@link BeanReference BeanReferences} are available from {@link #getBeanReferences()}.
	 * @return the array of BeanDefinitions, or an empty array if none
	 */
	BeanDefinition[] getBeanDefinitions();

	/**
	 * Return the {@link BeanDefinition BeanDefinitions} that represent all relevant
	 * inner beans within this component.
	 * <p>Other inner beans may exist within the associated {@link BeanDefinition BeanDefinitions},
	 * however these are not considered to be needed for validation or for user visualization.
	 * @return the array of BeanDefinitions, or an empty array if none
	 */
	BeanDefinition[] getInnerBeanDefinitions();

	/**
	 * Return the set of {@link BeanReference BeanReferences} that are considered
	 * to be important to this {@code ComponentDefinition}.
	 * <p>Other {@link BeanReference BeanReferences} may exist within the associated
	 * {@link BeanDefinition BeanDefinitions}, however these are not considered
	 * to be needed for validation or for user visualization.
	 * @return the array of BeanReferences, or an empty array if none
	 */
	BeanReference[] getBeanReferences();

}
