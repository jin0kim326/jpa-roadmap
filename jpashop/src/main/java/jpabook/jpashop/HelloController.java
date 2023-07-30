package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

    /**
     * 스프링 부트 타임리프 뷰네임 매핑
     * 기본값 : resources:templates/{viewName}.html
     */
    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "HELLO ~");
        return "hello";
    }
}
