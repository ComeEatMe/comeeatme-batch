package com.comeeatme.batch.job;

import com.comeeatme.batch.domain.AddressCode;
import com.comeeatme.batch.domain.LocalData;
import com.comeeatme.batch.domain.repository.AddressCodeRepository;
import com.comeeatme.batch.domain.repository.BatchSkipLogRepository;
import com.comeeatme.batch.domain.repository.JusoLogRepository;
import com.comeeatme.batch.listener.SkipLogSkipListener;
import com.comeeatme.batch.processor.LocalDataRestaurantEntityBuildProcessor;
import com.comeeatme.batch.processor.RestaurantOldAddressProcessor;
import com.comeeatme.batch.processor.RestaurantSkipClosedProcessor;
import com.comeeatme.batch.processor.RestaurantTrimProcessor;
import com.comeeatme.batch.service.JusoService;
import com.comeeatme.batch.service.dto.LocalDataRestaurantDto;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.persistence.EntityManagerFactory;

@Configuration
@RequiredArgsConstructor
public class JobCommonConfig {

    @Bean
    public SkipLogSkipListener skipLogSkipListener(BatchSkipLogRepository batchSkipLogRepository) {
        return new SkipLogSkipListener(batchSkipLogRepository);
    }

    @Bean
    public ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> restaurantSkipClosedProcessor() {
        return new RestaurantSkipClosedProcessor();
    }

    @Bean
    public ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> restaurantTrimProcessor() {
        return new RestaurantTrimProcessor();
    }

    @Bean
    public ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> restaurantOldAddressProcessor() {
        return new RestaurantOldAddressProcessor();
    }

    @Bean
    @Lazy()
    public ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalData> localDataRestaurantEntityBuildProcessor(
            AddressCodeRepository addressCodeRepository, JusoService jusoService, JusoLogRepository jusoLogRepository) {
        return new LocalDataRestaurantEntityBuildProcessor(addressCodeRepository, jusoService, jusoLogRepository);
    }

    @Bean
    public ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalData> localDataRestaurantProcessor(
            ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> restaurantSkipClosedProcessor,
            ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> restaurantTrimProcessor,
            ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalDataRestaurantDto.LocalDataRestaurantDto> restaurantOldAddressProcessor,
            ItemProcessor<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalData> localDataRestaurantEntityBuildProcessor) {
        return new CompositeItemProcessorBuilder<LocalDataRestaurantDto.LocalDataRestaurantDto, LocalData>()
                .delegates(
                        restaurantSkipClosedProcessor,
                        restaurantTrimProcessor,
                        restaurantOldAddressProcessor,
                        localDataRestaurantEntityBuildProcessor
                ).build();
    }

    @Bean
    public ItemWriter<LocalData> localDataRestaurantWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<LocalData>()
                .entityManagerFactory(emf)
                .usePersist(true)
                .build();
    }

    @Bean
    public ItemWriter<AddressCode> addressCodeItemWriter(EntityManagerFactory emf) {
        return new JpaItemWriterBuilder<AddressCode>()
                .entityManagerFactory(emf)
                .usePersist(true)
                .build();
    }

}
