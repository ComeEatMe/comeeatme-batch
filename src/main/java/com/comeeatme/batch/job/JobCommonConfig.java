package com.comeeatme.batch.job;

import com.comeeatme.batch.domain.AddressCode;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class JobCommonConfig {

    @Bean
    public ItemWriter<AddressCode> addressCodeItemWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<AddressCode>()
                .entityManagerFactory(emf)
                .usePersist(true)
                .build();
    }

}
