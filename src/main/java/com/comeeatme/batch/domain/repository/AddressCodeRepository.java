package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.AddressCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressCodeRepository extends JpaRepository<AddressCode, Long> {

    List<AddressCode> findAllByFullNameIn(List<String> fullNames);

}
