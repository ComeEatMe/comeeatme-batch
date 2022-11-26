package com.comeeatme.batch.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "juso_log",
    indexes = @Index(name = "IX_juso_log_local_data_management_num", columnList = "local_data_management_num")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JusoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "juso_log_id")
    private Long id;

    @Column(name = "type", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Column(name = "result", length = 2000, nullable = false)
    private String result;

    @Column(name = "local_data_management_num", length = 45, nullable = false)
    private String localDataManagementNum;

    public static JusoLog.JusoLogBuilder addr() {
        return JusoLog.builder()
                .type(Type.ADDR);
    }

    public static JusoLog.JusoLogBuilder coord() {
        return JusoLog.builder()
                .type(Type.COORD);
    }

    @Builder
    private JusoLog(Long id, Type type, String keyword, String result, String localDataManagementNum) {
        this.id = id;
        this.type = type;
        this.keyword = keyword;
        this.result = result;
        this.localDataManagementNum = localDataManagementNum;
    }

    enum Type {
        ADDR,
        COORD,
    }

}
