package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.Place;
import com.dev.springbootserver.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    boolean existsByNameAndSchoolId(String name, Long schoolId);

    Optional<Place> findByNameAndSchoolId(String name, Long schoolId);

    List<Place> findAllBySchool(School school);
}
