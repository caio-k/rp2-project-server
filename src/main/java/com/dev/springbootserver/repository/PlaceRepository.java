package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    boolean existsByNameAndSchoolId(String name, Long schoolId);

}
