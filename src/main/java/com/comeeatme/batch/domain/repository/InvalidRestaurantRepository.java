package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.InvalidRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidRestaurantRepository extends JpaRepository<InvalidRestaurant, Long> {
}
