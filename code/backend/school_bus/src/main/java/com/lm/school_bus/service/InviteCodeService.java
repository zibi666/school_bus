package com.lm.school_bus.service;

import com.lm.school_bus.repository.StudentTicketOrderRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class InviteCodeService {

    private static final String POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StudentTicketOrderRepository orderRepository;

    public InviteCodeService(StudentTicketOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String generateUniqueCode() {
        String code;
        int attempts = 0;
        do {
            code = randomCode();
            attempts++;
        } while (orderRepository.existsByInviteCode(code) && attempts < 10_000);
        return code;
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            builder.append(POOL.charAt(RANDOM.nextInt(POOL.length())));
        }
        return builder.toString();
    }
}
