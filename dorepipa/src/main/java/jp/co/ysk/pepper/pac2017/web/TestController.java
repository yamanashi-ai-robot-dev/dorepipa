package jp.co.ysk.pepper.pac2017.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * トップ画面のコントローラ.
 */
@Controller
@RequestMapping("/")
public class TestController {

    /**
     * トップ画面.
     *
     * @return ビュー名
     */
    @RequestMapping(method = RequestMethod.GET)
    public String top(Model model) {

        return "test";
    }

}
