package com.ust.model;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderRow(long id, String orderNumber, String status, BigDecimal total, String userId, Instant createdAt) {
}


