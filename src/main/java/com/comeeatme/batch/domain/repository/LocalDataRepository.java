package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.LocalData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalDataRepository extends JpaRepository<LocalData, String> {
}
