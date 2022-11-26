package com.comeeatme.batch.domain.repository;

import com.comeeatme.batch.domain.AddressCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressCodeRepository extends JpaRepository<AddressCode, Long> {

    List<AddressCode> findAllByFullNameIn(List<String> fullNames);

    List<AddressCode> findAllByTerminalIsTrue();

    Optional<AddressCode> findByFullNameAndTerminalIsTrue(String fullName);

}
