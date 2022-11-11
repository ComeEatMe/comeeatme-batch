package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.IncompleteRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncompleteRestaurantRepository extends JpaRepository<IncompleteRestaurant, Long> {
}
