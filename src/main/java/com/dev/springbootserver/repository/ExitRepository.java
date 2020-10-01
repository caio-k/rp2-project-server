package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.Exit;
import com.dev.springbootserver.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExitRepository extends JpaRepository<Exit, Long> {

    boolean existsByNameAndSchoolId(String name, Long schoolId);

    List<Exit> findAllBySchool(School school);
}
