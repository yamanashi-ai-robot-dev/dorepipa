package jp.co.ysk.pepper.pac2017.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.co.ysk.pepper.pac2017.dto.AssessmentResponseDto;
import jp.co.ysk.pepper.pac2017.dto.TypeSelectResponseDto;
import jp.co.ysk.pepper.pac2017.service.AssessmentService;
import jp.co.ysk.pepper.pac2017.service.AsyncAssessmentService;
import jp.co.ysk.pepper.pac2017.service.DataOperationService;
import jp.co.ysk.pepper.pac2017.service.GoogleMailService;

/**
 * RestAPIコントローラクラス.
 */
@RestController
@RequestMapping("/api")
public class WebApiController {

    @Autowired
    private DataOperationService dataOperationService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AsyncAssessmentService asyncAssessmentService;

    @Autowired
    private GoogleMailService googleMailService;

    @Autowired
    private PepperQiController pepperQiController;

    /** 利用実績用ロガー */
    private static final Logger logger = LoggerFactory.getLogger(WebApiController.class);

    /**  */
    @RequestMapping(value = "typeselect")
    public TypeSelectResponseDto playReady(@RequestParam("type") int playTypeCd) {
        TypeSelectResponseDto dto = new TypeSelectResponseDto();
        try {
            dataOperationService.insertNewPlayData(playTypeCd, dto);
            asyncAssessmentService.setParams();
            assessmentService.setParam();
            dto.setResult("OK");
        } catch (Exception e) {
            dto.setResult("NG");
        }
        return dto;
    }

    /**  */
    @RequestMapping(value = "identify", params = "faceid")
    public String identifyByFaceId(@RequestParam("faceid") String faceid) {
        try {
            return dataOperationService.identifyByFaceId(faceid);
        } catch (Exception e) {
            return "error";
        }
    }

    /**  */
    @RequestMapping(value = "identify", params = "voiceid")
    public String identifyByVoiceId(@RequestParam("voiceid") String voiceid) {
        try {
            return dataOperationService.identifyByVoiceId(voiceid);
        } catch (Exception e) {
            return "error";
        }
    }

    /**  */
    @RequestMapping(value = "idlink")
    public String linkDeviceToUser(@RequestParam("deviceid") int deviceId) {
        try {
            dataOperationService.linkDeviceToUser(deviceId);
            return "OK";
        } catch (Exception e) {
            return "NG";
        }
    }

    /**  */
    @RequestMapping(value = "onetimeuser")
    public String linkOneTimeUser(@RequestParam("deviceid") int deviceId) {
        try {
            String name = dataOperationService.linkOneTimeUser(deviceId);
            logger.info("onetimeuser = " + name);
            return name;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "error";
        }
    }

    /**  */
    @RequestMapping(value = "retry")
    public String retryPlaying() {
        try {
            dataOperationService.readyForPlayAgain();
            return "OK";
        } catch (Exception e) {
            return "NG";
        }
    }

    /**  */
    @RequestMapping(value = "play/end")
    public String playEnd() {
        try {
            LocalDateTime now = LocalDateTime.now();
            int playId = dataOperationService.updatePlayEndDt(now, false);
            assessmentService.readyForAssessment(playId);
            assessmentService.handleAssessment(playId);
            return "OK";
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "NG";
        }
    }

    /**  */
    @RequestMapping(value = "play/cancel")
    public String playCancel() {
        try {
            LocalDateTime now = LocalDateTime.now();
            dataOperationService.updatePlayEndDt(now, true);
            return "OK";
        } catch (Exception e) {
            return "NG";
        }
    }

    /**  */
    @RequestMapping(value = "keybordope")
    public String keybordope(@RequestParam("id") Integer deviceId, @RequestParam("deviceid") Integer keyId,
            @RequestParam("status") Integer status) {
        LocalDateTime now = LocalDateTime.now();
        dataOperationService.insertKeyData(deviceId, keyId, status, now);

        logger.info(String.format("タイムスタンプ：%s　ID：%d　デバイスID：%d　ステータス：%d",
                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS").format(now), deviceId, keyId, status));
        return "";
    }

    /**  */
    @RequestMapping(value = "assessment")
    public AssessmentResponseDto assessmentGroupLesson() {
        System.out.println("assessment is called!");
        AssessmentResponseDto dto = new AssessmentResponseDto();
        assessmentService.extractAssessmentResult(dataOperationService.getPlayId(),
                dataOperationService.getPlayTypeCd(), dto);
        if (dataOperationService.getPlayTypeCd() == 0) {
            String msgBody = assessmentService.buildMessageBody(dataOperationService.getPlayId());
            googleMailService.sendMail("どれピパ 演奏結果", msgBody);
        }
        return dto;
    }

    @RequestMapping(value = "svqiconnect/init")
    public String svQiConnectInit() {
        try {
            pepperQiController.reconnect();
            return "OK";
        } catch (Exception e) {
            logger.error("Fail to reconnect. " + e.getMessage());
            System.out.println("Fail to reconnect. " + e.getMessage());
            return "NG";
        }
    }

    @RequestMapping(value = "svqiconnect/check")
    public String svQiConnectCheck() {
        try {
            pepperQiController.checkConnection();
            return "OK";
        } catch (Exception e) {
            logger.error("Fail to checkConnection. " + e.getMessage());
            System.out.println("Fail to initConnection. " + e.getMessage());
            return "NG";
        }
    }

    @RequestMapping(value = "svqiconnect/reconnect")
    public String svQiConnectReconnect() {
        try {
            pepperQiController.reconnect();
            return "OK";
        } catch (Exception e) {
            logger.error("Fail to reconnect. " + e.getMessage());
            System.out.println("Fail to reconnect. " + e.getMessage());
            return "NG";
        }
    }

    @RequestMapping(value = "mailtest")
    public String mailtest() {
        String msgBody = assessmentService.buildMessageBody(dataOperationService.getPlayId());
        googleMailService.sendMail("どれピパ 演奏結果", msgBody);
        return "OK";
    }

    // /** */
    // @RequestMapping(value = "dtsync1")
    // public String dtSync1() {
    // dataOperationService.setSyncStartTime(LocalDateTime.now());
    // dataOperationService.setSyncEndTime(LocalDateTime.now());
    // return "";
    // }
    //
    // /** */
    // @RequestMapping(value = "dtsync2")
    // public String dtSync2(@RequestParam("req") String requestDt,
    // @RequestParam("res") String responseDt) {
    // dataOperationService.syncServerTimes(requestDt, responseDt);
    // return "OK";
    // }
    //
    // /** */
    // @RequestMapping(value = "play/start")
    // public String playStart(@RequestParam("dt") String pepperDt) {
    // try {
    // dataOperationService.updatePlayStartDt(pepperDt);
    // return "OK";
    // } catch (Exception e) {
    // logger.error(e.getMessage());
    // return "NG";
    // }
    // }
}
