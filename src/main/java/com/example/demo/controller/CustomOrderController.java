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


import java.util.List;

import org.springframework.data.rest.webmvc.BasePathAwareController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import static com.example.demo.controller.OrderStatus.*;
import com.example.demo.model.Order;
import com.example.demo.repository.OrderRepository;

/**
 * @author original code was written by Greg Turnquist 
 * @author this refactoring was made by Konstantin Oleinikov 
 */
@BasePathAwareController
public class CustomOrderController implements RepresentationModelProcessor<EntityModel<Order>>{

	private final OrderRepository repository;

	public CustomOrderController(OrderRepository repository) {
		this.repository = repository;
	}

	@PostMapping("/orders/{id}/pay")
	ResponseEntity<?> pay(@PathVariable Long id) {

		Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (valid(order.getOrderStatus(), OrderStatus.PAID_FOR)) {

			order.setOrderStatus(OrderStatus.PAID_FOR);
			return ResponseEntity.ok(repository.save(order));
		}

		return ResponseEntity.badRequest()
				.body("Transitioning from " + order.getOrderStatus() + " to " + OrderStatus.PAID_FOR + " is not valid.");
	}

	@PostMapping("/orders/{id}/cancel")
	ResponseEntity<?> cancel(@PathVariable Long id) {

		Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (valid(order.getOrderStatus(), OrderStatus.CANCELLED)) {

			order.setOrderStatus(OrderStatus.CANCELLED);
			return ResponseEntity.ok(repository.save(order));
		}

		return ResponseEntity.badRequest()
				.body("Transitioning from " + order.getOrderStatus() + " to " + OrderStatus.CANCELLED + " is not valid.");
	}

	@PostMapping("/orders/{id}/fulfill")
	ResponseEntity<?> fulfill(@PathVariable Long id) {

		Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

		if (valid(order.getOrderStatus(), OrderStatus.FULFILLED)) {

			order.setOrderStatus(OrderStatus.FULFILLED);
			return ResponseEntity.ok(repository.save(order));
		}

		return ResponseEntity.badRequest()
				.body("Transitioning from " + order.getOrderStatus() + " to " + OrderStatus.FULFILLED + " is not valid.");
	}

	@Override
	public EntityModel<Order> process(EntityModel<Order> model) {
		Links links = model.getLinks();
		List<Link> listLink = links.toList();
		if (listLink==null || listLink.isEmpty()) {
			//just to be sure that there is at least one link in the list
			return model;
		}
		
		Link self = listLink.get(0);
		String href = self.getHref();
		
		// If PAID_FOR is valid, add a link to the `pay()` method
		if (valid(model.getContent().getOrderStatus(), OrderStatus.PAID_FOR)) {
			Link paidLink = new Link(String.format("%s/%s", href,"pay"),"payment");
			model.add(paidLink);
		}

		// If CANCELLED is valid, add a link to the `cancel()` method
		if (valid(model.getContent().getOrderStatus(), OrderStatus.CANCELLED)) {
			Link cancelLink = new Link(String.format("%s/%s", href,"cancel"),"cancel");
			model.add(cancelLink);
		}

		// If FULFILLED is valid, add a link to the `fulfill()` method
		if (valid(model.getContent().getOrderStatus(), OrderStatus.FULFILLED)) {
			Link fulfilLink = new Link(String.format("%s/%s", href,"fulfill"),"fulfill");
			model.add(fulfilLink);
		}

		return model;
	}
}
