package com.project.poc_stream_gather.service;

import com.project.poc_stream_gather.model.Order;

import java.util.List;
import java.util.stream.Gatherers;

public class BillingService {

    public void process(List<Order> orders) {

        orders.stream()

                .gather(Gatherers.windowFixed(3))

                .forEach(batch -> {

                    System.out.println("Enviando lote");

                    batch.forEach(System.out::println);

                    System.out.println("----------------");
                });
    }
}