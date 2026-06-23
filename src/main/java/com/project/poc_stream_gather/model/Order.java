package com.project.poc_stream_gather.model;

public record Order(
        String id,
        String customer,
        double amount
) {}