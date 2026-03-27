package com.gildedrose.infrastructure.event;

/** Represents an alert message for real-time notification via WebSocket. */
public record AlertMessage(String severity, String title, String message, long timestamp) {}
