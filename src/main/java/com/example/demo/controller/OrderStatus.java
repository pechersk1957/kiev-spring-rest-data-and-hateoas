/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.controller;

/**
 * @author Greg Turnquist
 */
public enum OrderStatus {

	BEING_CREATED, PAID_FOR, FULFILLED, CANCELLED;

	/**
	 * Verify the transition between {@link OrderStatus} is valid. NOTE: This is where any/all rules for state transitions
	 * should be kept and enforced.
	 */
	static boolean valid(OrderStatus currentStatus, OrderStatus newStatus) {

		if (currentStatus == BEING_CREATED) {
			return newStatus == PAID_FOR || newStatus == CANCELLED;
		} else if (currentStatus == PAID_FOR) {
			return newStatus == FULFILLED;
		} else if (currentStatus == FULFILLED) {
			return false;
		} else if (currentStatus == CANCELLED) {
			return false;
		} else {
			throw new RuntimeException("Unrecognized situation.");
		}
	}
}
