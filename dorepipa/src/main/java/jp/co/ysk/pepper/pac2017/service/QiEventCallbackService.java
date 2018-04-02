package jp.co.ysk.pepper.pac2017.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aldebaran.qi.CallError;
import com.aldebaran.qi.helper.EventCallback;

/**
 * TUserDao
 */
@Service
public class QiEventCallbackService implements EventCallback<Object> {

    private static final Logger logger = LoggerFactory.getLogger(QiEventCallbackService.class);

    @Autowired
    private DataOperationService dataOperationService;

    @Override
    public void onEvent(Object arg) throws InterruptedException, CallError {
        try {
            LocalDateTime now = LocalDateTime.now();
            this.dataOperationService.updatePlayStartDt(now);
        } catch (Exception e) {
            logger.error("Fail to play start. " + e.getMessage());
        }
    }
}
