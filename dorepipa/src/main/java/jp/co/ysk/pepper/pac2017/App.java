package jp.co.ysk.pepper.pac2017;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import jp.co.ysk.pepper.pac2017.api.PepperQiController;

/**
 * SpringBootアプリケーションクラス.
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
        PepperQiController pqc = ctx.getBean(PepperQiController.class);
        pqc.init(args);
    }
}