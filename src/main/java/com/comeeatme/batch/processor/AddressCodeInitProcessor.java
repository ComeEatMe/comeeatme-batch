package com.comeeatme.batch.processor;

import com.comeeatme.batch.domain.AddressCode;
import com.comeeatme.batch.domain.dto.AddressCodeDto;
import org.springframework.batch.item.ItemProcessor;

import java.util.Objects;

public class AddressCodeInitProcessor implements ItemProcessor<AddressCodeDto, AddressCode> {

//    private final Map<Integer, Integer> codeLenToDepth;
//
//    private final Map<Integer, Integer> depthToCodeLen;
//
//    public AddressCodeInitProcessor() {
//        this.codeLenToDepth = new LinkedHashMap<>(4);
//        this.codeLenToDepth.put(AddressCode.SIDO_CODE_LEN, 1);
//        this.codeLenToDepth.put(AddressCode.SIDO_CODE_LEN + AddressCode.SIGUNGU_CODE_LEN, 2);
//        this.codeLenToDepth.put(AddressCode.EUPMYEONDONG_CODE_LEN, 3);
//        this.codeLenToDepth.put(AddressCode.RI_CODE_LEN, 4);
//        this.depthToCodeLen = codeLenToDepth
//                .entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//    }

    @Override
    public AddressCode process(AddressCodeDto item) throws Exception {
        if (!Objects.equals("존재", item.getDeleted())) {
            return null;
        }

        String address = item.getAddress().replaceAll("\\s+", " ");
        int sidoIdx = address.indexOf(" ");
        String siDo = sidoIdx > 0 ? "" : address;
        String code = item.getCode();
        int depth = Objects.equals(address, siDo) ? 1 : 0;
        return AddressCode.builder()
                .name(siDo)
                .fullName(address)
                .code(code)
                .depth(depth)
                .build();
    }

//    private int parseDepth(String code, String siDo) {
//        for (Map.Entry<Integer, Integer> codeLenDepthEntry : codeLenToDepth.entrySet()) {
//            int codeLen = codeLenDepthEntry.getKey();
//            int codeDepth = codeLenDepthEntry.getValue();
//            int codePrefixLen = AddressCode.TOTAL_CODE_LEN - codeLen;
//            String codePrefix = "0".repeat(codePrefixLen);
//            if (code.endsWith(codePrefix)) {
//                if (Objects.equals("세종특별자치시", siDo)) {
//                    codeDepth -= 1;
//                }
//                return codeDepth;
//            }
//        }
//        throw new IllegalArgumentException("법정동 코드의 depth 를 parsing 할 수 없습니다.");
//    }

}
