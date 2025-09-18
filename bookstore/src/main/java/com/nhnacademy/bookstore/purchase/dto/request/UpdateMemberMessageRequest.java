package com.nhnacademy.bookstore.purchase.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateMemberMessageRequest(
        @NotNull@Min(0) Long memberMessageId) {
    }
