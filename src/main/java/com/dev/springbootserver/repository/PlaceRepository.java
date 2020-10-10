package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.EPlace;
import com.dev.springbootserver.model.Place;
import com.dev.springbootserver.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {

    boolean existsByNameAndSchoolId(String name, Long schoolId);

    List<Place> findAllBySchool(School school);

    List<Place> findAllBySchoolAndType(School school, EPlace type);
}
