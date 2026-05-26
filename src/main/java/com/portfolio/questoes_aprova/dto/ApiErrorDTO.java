package com.portfolio.questoes_aprova.dto;

import java.time.OffsetDateTime;

public record ApiErrorDTO(OffsetDateTime timestamp, int status, String error, String message, String path) {
}
