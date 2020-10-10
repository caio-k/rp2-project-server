package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.Place;
import com.dev.springbootserver.model.UserUsePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserUsePlaceRepository extends JpaRepository<UserUsePlace, Long> {

    List<UserUsePlace> findAllByPlace(Place place);
}
