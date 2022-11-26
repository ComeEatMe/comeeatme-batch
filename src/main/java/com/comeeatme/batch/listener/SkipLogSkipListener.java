package com.comeeatme.batch.listener;

import com.comeeatme.batch.domain.BatchSkipLog;
import com.comeeatme.batch.domain.repository.BatchSkipLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class SkipLogSkipListener implements SkipListener<Object, Object> {

    private final BatchSkipLogRepository batchSkipLogRepository;

    @Override
    public void onSkipInRead(Throwable t) {
        log.error("", t);
        saveSkipLog(t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        log.error("item={}", item, t);
        saveSkipLog(t, item);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        log.error("item={}", item, t);
        saveSkipLog(t, item);
    }

    private void saveSkipLog(Throwable t) {
        saveSkipLog(t, null);
    }

    private void saveSkipLog(Throwable t, Object item) {
        batchSkipLogRepository.save(
                BatchSkipLog.builder()
                        .item(Optional.ofNullable(item)
                                .map(Object::toString)
                                .orElse(null))
                        .exceptionName(t.getClass().getName())
                        .exceptionMessage(t.getMessage())
                        .exceptionStackTrace(getStackTraceString(t))
                        .build()
        );
    }

    private String getStackTraceString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

}
