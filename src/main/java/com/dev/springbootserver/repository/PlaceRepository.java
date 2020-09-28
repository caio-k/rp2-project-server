package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    boolean existsByNameAndSchoolId(String name, Long schoolId);

    Optional<Place> findByNameAndSchoolId(String name, Long schoolId);
}
