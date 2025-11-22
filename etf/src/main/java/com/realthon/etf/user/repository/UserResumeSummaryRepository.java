package com.realthon.etf.user.repository;

import com.realthon.etf.user.domain.User;
import com.realthon.etf.user.domain.UserResumeSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserResumeSummaryRepository extends JpaRepository<UserResumeSummary, Long> {

    Optional<UserResumeSummary> findByUser(User user);

}
