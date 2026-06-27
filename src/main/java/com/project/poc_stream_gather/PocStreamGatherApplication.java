package com.project.poc_stream_gather;

import com.project.poc_stream_gather.model.Order;
import com.project.poc_stream_gather.own_gather.MyGatherers;
import com.project.poc_stream_gather.service.BillingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PocStreamGatherApplication {

	public static void main(String[] args) {

		var orders = List.of(

				new Order("1", "João", 150),
				new Order("2", "Maria", 220),
				new Order("3", "José", 300),
				new Order("4", "Pedro", 180),
				new Order("5", "Ana", 90),
				new Order("6", "Lucas", 450),
				new Order("7", "Carla", 120)
		);

		var service = new BillingService();

		service.process(orders);


		List.of("Java", "25")

				.stream()

				.gather(MyGatherers.repeat(3))

				.forEach(System.out::println);
	}

}
