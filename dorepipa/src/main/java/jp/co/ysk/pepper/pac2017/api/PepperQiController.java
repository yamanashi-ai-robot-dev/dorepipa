package jp.co.ysk.pepper.pac2017.api;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.aldebaran.qi.Application;
import com.aldebaran.qi.CallError;
import com.aldebaran.qi.Callback;
import com.aldebaran.qi.Future;
import com.aldebaran.qi.Session;
import com.aldebaran.qi.helper.EventCallback;
import com.aldebaran.qi.helper.proxies.ALMemory;

import jp.co.ysk.pepper.pac2017.service.DataOperationService;

@Controller
public class PepperQiController {

    private static final Logger logger = LoggerFactory.getLogger(PepperQiController.class);

    @Autowired
    private DataOperationService dataOperationService;

    // @Autowired
    // private QiEventCallbackService qiEventCallbackService;

    /** サービスアカウントユーザ */
    @Value("${dorepipa.robot.url}")
    private String robotUrl;

    private Application application;

    private ALMemory memory;

    private long subscriptionId = -1;

    public void init(String[] args) {
        application = new Application(args);
    }

    public void initConnection() throws Exception {
        startConnection(0);
    }

    public void checkConnection() throws Exception {
        if (!application.session().isConnected()) {
            application.stop();
            startConnection(0);
        }
    }

    public void reconnect() throws Exception {
        if (application.session().isConnected()) {
            stopConnection();
        }
        startConnection(0);
    }

    private void startConnection(int cnt) throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        boolean[] success = { false };

        Session session = application.session();

        session.connect(robotUrl).addCallback(new Callback<Void>() {
            @Override
            public void onComplete(Future<Void> arg0, Object[] arg1) {
            }

            @Override
            public void onFailure(Future<Void> arg0, Object[] arg1) {
                logger.error("Connection to Pepper is failed;");
                cdl.countDown();
            }

            @Override
            public void onSuccess(Future<Void> arg0, Object[] arg1) {
                try {
                    memory = new ALMemory(session);
                    reactToEvents();
                    success[0] = true;
                } catch (Exception e) {
                    logger.error("Connection to Pepper is failed;");
                } finally {
                    cdl.countDown();
                }
            }
        });

        cdl.await(60, TimeUnit.SECONDS);
        if (success[0]) {
            logger.info("Connection is succeeded;");
            System.out.println("Connection is succeeded;");
        } else {
            throw new Exception("Connection to Pepper is failed;");
        }

    }

    // private void reactToEvents() throws Exception {
    // memory.subscribeToEvent("MyApp/DorePipa/sv/play/start",
    // "PepperQiController", "callbackLogic");
    // }

    private void reactToEvents() throws Exception {
        subscriptionId = memory.subscribeToEvent("MyApp/DorePipa/sv/play/start", new EventCallback<Object>() {

            @Override
            public void onEvent(Object arg) throws InterruptedException, CallError {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    dataOperationService.updatePlayStartDt(now);
                } catch (Exception e) {
                    logger.error("Fail to play start. " + e.getMessage());
                }
            }
        });
    }

    // private void reactToEvents() throws Exception {
    // subscriptionId = memory.subscribeToEvent("MyApp/DorePipa/sv/play/start",
    // qiEventCallbackService);
    // }

    private void stopConnection() throws Exception {
        if (application.session().isConnected()) {
            if (memory != null && subscriptionId != -1) {
                memory.unsubscribeToEvent(subscriptionId);
                memory = null;
                subscriptionId = -1;
                application.session().close();
            }
        } else {
            logger.info("stopConnection is called, but connection has already stopped.");
        }
    }
}