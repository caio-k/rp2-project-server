package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.School;
import com.dev.springbootserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    Optional<School> findSchoolByRepresentativeUser(User user);

    boolean existsByName(String name);

    boolean existsByRepresentativeUser(User user);
}
