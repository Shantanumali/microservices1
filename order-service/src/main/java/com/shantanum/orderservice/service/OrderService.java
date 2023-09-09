package com.shantanum.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.shantanum.orderservice.dto.InventoryResponse;
import com.shantanum.orderservice.dto.OrderLineItemsDto;
import com.shantanum.orderservice.dto.OrderRequest;
import com.shantanum.orderservice.model.Order;
import com.shantanum.orderservice.model.OrderLineItems;
import com.shantanum.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	final Logger log = LogManager.getLogger(OrderService.class);
	private final OrderRepository orderRepository;
	private final WebClient.Builder webClientBuilder;
	private final Tracer tracer;

	public String placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());

		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto)
				.toList();

		order.setOrderLineItemsList(orderLineItems);

		List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

		// Call Inventory Service, and place order if product is in
		// stock

		log.info("Calling inventory service");

		Span inventoryServiceLookUp = tracer.nextSpan().name("InventoryServiceLookUp");
		try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookUp.start())) {
			InventoryResponse[] inventoryResponsArray = webClientBuilder.build().get()
					.uri("http://inventory-service/api/inventory",
							uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
					.retrieve().bodyToMono(InventoryResponse[].class).block();

			boolean allProductsInStock = Arrays.stream(inventoryResponsArray).allMatch(InventoryResponse::isInStock);

			if (allProductsInStock && inventoryResponsArray.length > 0) {
				orderRepository.save(order);
				return "Order Placed Successfully";
			} else {
				throw new IllegalArgumentException("Product is not in stock, please try again later");
			}
		} finally {
			inventoryServiceLookUp.end();
		}
	}

	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
}