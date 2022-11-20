package com.comeeatme.batch.writer;

import com.comeeatme.batch.domain.AddressCode;
import org.springframework.batch.item.database.JpaItemWriter;

import javax.persistence.EntityManager;
import java.util.List;

public class AddressCodeSubAddressUpdateWriter extends JpaItemWriter<AddressCode> {

    @Override
    protected void doWrite(EntityManager entityManager, List<? extends AddressCode> items) {
        if (logger.isDebugEnabled()) {
            logger.debug("Writing to JPA with " + items.size() +
                    " parent " + AddressCode.class.getSimpleName() + " items.");
        }

        if (!items.isEmpty()) {
            long addedToContextCount = 0;

            for (AddressCode parent : items) {
                String childCodeParam = parseChildCode(parent.getCode());
                List<AddressCode> children = entityManager.createQuery(
                                "select ac from AddressCode ac " +
                                        "where ac.code like :childCodeParam and ac.code <> :parentCode",
                                AddressCode.class)
                        .setParameter("childCodeParam", childCodeParam)
                        .setParameter("parentCode", parent.getCode())
                        .getResultList();
                boolean terminal = children.isEmpty();
                entityManager.merge(
                        AddressCode.builder()
                                .code(parent.getCode())
                                .parentCode(parent.getParentCode())
                                .name(parent.getName())
                                .fullName(parent.getFullName())
                                .depth(parent.getDepth())
                                .terminal(terminal)
                                .build()
                );

                for (AddressCode child : children) {
                    String childName = child.getFullName()
                            .substring(parent.getFullName().length())
                            .trim();
                    entityManager.merge(
                            AddressCode.builder()
                                    .code(child.getCode())
                                    .parentCode(parent)
                                    .name(childName)
                                    .fullName(child.getFullName())
                                    .depth(parent.getDepth() + 1)
                                    .terminal(child.getTerminal())
                                    .build()
                    );
                    addedToContextCount++;
                }
            }

            if (logger.isDebugEnabled()) {
                logger.debug(addedToContextCount + " entities merged.");
            }
        }
    }

    private String parseChildCode(String code) {
        int[] codeLens = {
                AddressCode.SIDO_CODE_LEN,
                AddressCode.SIGUNGU_CODE_LEN,
                AddressCode.EUPMYEONDONG_CODE_LEN,
                AddressCode.RI_CODE_LEN
        };

        int prefixLen = 0;
        for (int i = 0; i < codeLens.length - 1; i++) {
            int codeLen = codeLens[i];
            prefixLen += codeLen;
            if (code.endsWith("0".repeat(AddressCode.TOTAL_CODE_LEN - prefixLen))) {
                String prefix = code.substring(0, prefixLen);

                int paramLen = codeLens[i + 1];
                String param = "_".repeat(paramLen);

                int postfixLen = AddressCode.TOTAL_CODE_LEN - prefixLen - paramLen;
                String postfix = "0".repeat(postfixLen);

                return prefix + param + postfix;
            }
        }
        return null;
    }

}
