package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
