package com.nhnacademy.coupon.messagequeue.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.dto.request.CreateCouponFormRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponRequestPublisher {
    private final RabbitTemplate rabbit;
    private final ObjectMapper objectMapper;
    public void send(CreateCouponFormRequest dto) {

        try {

            String msg = objectMapper.writeValueAsString(dto);

            //exchange, routingKey
            rabbit.convertAndSend("ex.requests", "req.create", msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("메시지 직렬화 실패", e);
        }
    }
}
