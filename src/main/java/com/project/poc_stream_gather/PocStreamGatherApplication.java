package com.project.poc_stream_gather;

import com.project.poc_stream_gather.model.Order;
import com.project.poc_stream_gather.own_gather.MyGatherers;
import com.project.poc_stream_gather.service.BillingService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class PocStreamGatherApplication {

	public static void main(String[] args) {

		System.out.println("New Gather windowFixed:");

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

		/*
			Saida:

			New Gather windowFixed:
			Enviando lote
			Order[id=1, customer=João, amount=150.0]
			Order[id=2, customer=Maria, amount=220.0]
			Order[id=3, customer=José, amount=300.0]
			----------------
			Enviando lote
			Order[id=4, customer=Pedro, amount=180.0]
			Order[id=5, customer=Ana, amount=90.0]
			Order[id=6, customer=Lucas, amount=450.0]
			----------------
			Enviando lote
			Order[id=7, customer=Carla, amount=120.0]
		 */


		List.of("Java", "25")

				.stream()

				.gather(MyGatherers.repeat(3))

				.forEach(System.out::println);

		/*
			Saida:
			Java
			Java
			Java
			25
			25
			25
		 */

		System.out.println();
		System.out.println("New Gather runningSum:");


		Stream.of(10, 20, 30, 40)

			 .gather(MyGatherers.runningSum())

			 .forEach(System.out::println);

		/*
			New Gather runningSum:
			10
			30
			60
			100
		 */

		System.out.println();
		System.out.println("New Gather distinctCustom:");

		Stream.of("Java",
				"Java",
				"Spring",
				"Spring",
				"Spring",
				"Java",
				"Docker",
				"Docker",
				"Kubernetes"
				)
				.gather(MyGatherers.distinctCustom())
				.forEach(System.out::println);

			/*
				Saida:

				New Gather distinctCustom:
				Java
				Spring
				Docker
				Kubernetes
			 */

			System.out.println();
			System.out.println("New Gather batch:");

			Stream.of(1,2,3,4,5,6,7)
				.gather(MyGatherers.batch(3))
				.forEach(System.out::println);

			/*
			New Gather batch:
			[1, 2, 3]
			[4, 5, 6]
			[7]
			 */
		}


Stream.of(10, 20, 30, 40, 50)
      .gather(RunningAverageGatherers.runningAverage())
      .forEach(System.out::println);
}
