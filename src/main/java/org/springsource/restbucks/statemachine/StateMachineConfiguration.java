/*
 * Copyright 2018 the original author or authors.
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
package org.springsource.restbucks.statemachine;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Oliver Gierke
 */
public class StateMachineConfiguration {

	private final Map<Class<?>, AggregateStateMachine> machines = new HashMap<>();

	public AggregateStateMachine forAggregate(Class<?> aggregateType) {
		return machines.computeIfAbsent(aggregateType, it -> new AggregateStateMachine(it));
	}

	public boolean hasConfigurationFor(Class<?> aggregateType) {
		return machines.containsKey(aggregateType);
	}

	public AggregateStateMachine getStateMachineFor(Class<?> aggregateType) {

		if (!hasConfigurationFor(aggregateType)) {
			throw new IllegalArgumentException("No state machine found for " + aggregateType.getName());
		}

		return machines.get(aggregateType);
	}

	@RequiredArgsConstructor
	public static class AggregateStateMachine {

		private final @Getter Class<?> aggreagateType;
		private final Map<String, String> methodsToStateTransition = new HashMap<>();

		public String getTransitionFor(Method method) {
			return methodsToStateTransition.get(method.getName());
		}

		public TransitionBuilder withMethod(Method method) {
			return new TransitionBuilder(method.getName());
		}

		public TransitionBuilder withMethod(String methodName) {
			return new TransitionBuilder(methodName);
		}

		@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
		public class TransitionBuilder {

			private final String methodName;

			public AggregateStateMachine boundTo(String transition) {

				AggregateStateMachine.this.methodsToStateTransition.put(methodName, transition);
				return AggregateStateMachine.this;
			}
		}
	}
}
