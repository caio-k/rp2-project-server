package com.dev.springbootserver.repository;

import com.dev.springbootserver.model.Place;
import com.dev.springbootserver.model.User;
import com.dev.springbootserver.model.UserUsePlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserUsePlaceRepository extends JpaRepository<UserUsePlace, Long> {

    List<UserUsePlace> findAllByPlaceAndCounterGreaterThan(Place place, int counter);

    Optional<UserUsePlace> findByPlaceAndUser(Place place, User user);
}
