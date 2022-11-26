package com.comeeatme.batch.domain;

import com.comeeatme.batch.domain.core.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "batch_skip_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchSkipLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_skip_log_id")
    private Long id;

    @Column(name = "exception_name")
    private String exceptionName;

    @Column(name = "exception_message", length = 2000)
    private String exceptionMessage;

    @Lob
    @Column(name = "exception_stack_trace")
    private String exceptionStackTrace;

    @Column(name = "item", length = 2000)
    private String item;

    @Builder
    private BatchSkipLog(
            Long id, String exceptionName, String exceptionMessage, String exceptionStackTrace, String item) {
        this.id = id;
        this.exceptionName = exceptionName;
        this.exceptionMessage = exceptionMessage;
        this.exceptionStackTrace = exceptionStackTrace;
        this.item = item;
    }
}
