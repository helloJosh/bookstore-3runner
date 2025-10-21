package com.nhnacademy.bookstore.purchase.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;


@Builder
public record UpdatePurchaseMemberRequest(@NotNull String purchaseStatus) {
}
