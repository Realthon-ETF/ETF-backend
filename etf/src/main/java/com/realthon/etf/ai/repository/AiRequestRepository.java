package com.realthon.etf.ai.repository;

import com.realthon.etf.ai.domain.AiRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiRequestRepository extends JpaRepository<AiRequest, Long> {
    Optional<AiRequest> findByRequestId(String requestId);
}
