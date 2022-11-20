package com.comeeatme.batch.domain;

import com.comeeatme.batch.domain.core.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

import static java.util.Objects.isNull;

/**
 * 행정표준코드관리시스템: https://www.code.go.kr/stdcode/regCodeL.do
 * Download url : https://www.code.go.kr/etc/codeFullDown.do?codeseId=법정동코드
 */
@Entity
@Table(name = "address_code",
        uniqueConstraints = {
                @UniqueConstraint(name = "UK_address_code_full_name", columnNames = "full_name")
        }, indexes = {
            @Index(name = "IX_address_code_name", columnList = "name"),
            @Index(name = "IX_address_code_depth", columnList = "depth")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddressCode extends BaseTimeEntity implements Persistable<String> {

    public static final int SIDO_CODE_LEN = 2;
    public static final int SIGUNGU_CODE_LEN =  3;
    public static final int EUPMYEONDONG_CODE_LEN = 3;
    public static final int RI_CODE_LEN = 2;

    public static final int TOTAL_CODE_LEN =
            SIDO_CODE_LEN + SIGUNGU_CODE_LEN + EUPMYEONDONG_CODE_LEN + RI_CODE_LEN;


    @Id
    @Column(name = "code", length = 15, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_code", foreignKey = @ForeignKey(name = "FK_address_code_parent_code"))
    private AddressCode parentCode;

    @Column(name = "name", length = 15)
    private String name;

    @Column(name = "full_name", length = 65, nullable = false)
    private String fullName;

    @Column(name = "depth", nullable = false)
    private Integer depth;

    @Builder
    private AddressCode(
            String code,
            AddressCode parentCode,
            String name,
            String fullName,
            Integer depth) {
        this.code = code;
        this.parentCode = parentCode;
        this.name = name;
        this.fullName = fullName;
        this.depth = depth;
    }

    @Override
    public String getId() {
        return getCode();
    }

    @Override
    public boolean isNew() {
        return isNull(getCreatedAt());
    }

}

