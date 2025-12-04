package com.lm.school_bus.repository;

import com.lm.school_bus.entity.StudentTicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentTicketOrderRepository extends JpaRepository<StudentTicketOrder, String> {

    Optional<StudentTicketOrder> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}
