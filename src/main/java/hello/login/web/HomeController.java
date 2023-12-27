package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentResolver.Login;
import hello.login.web.session.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@CookieValue애노테이션은 Cookie값이 원래 String이지만 Long타입으로 반환하여 제공한다.
    //@GetMapping("/")
    public String home(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
        if (memberId == null) {
            return "home";
        }

        Member findMember = memberRepository.findById(memberId);
        if (findMember == null) {
            return "home";
        }

        model.addAttribute("member", findMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeV2(HttpServletRequest request, Model model) {
        Member findMember = (Member) sessionManager.getSession(request);
        if (findMember == null) {
            log.info("not valid SessionId");
            return "home";
        }

        log.info("valid SessionId");
        model.addAttribute("member", findMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeV3(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            log.info("세션 자체가 없다");
            return "home";
        }

        //세션
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        if (loginMember == null) {
            log.info("세션 있지만 데이터 없다");
            return "home";
        }

        log.info("세션 데이터 있다");
        model.addAttribute("member", loginMember);
        return "loginHome";
    }


    /**
     * @SessionAttirbute -> 참고로 이 기능은 세션을 생성하지 않는다.
     * 세션 값이 없을 수도 있으니 required는 false이다.
     */
    //@GetMapping("/")
    public String homeV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {
        if (loginMember == null) {
            return "home";
        }

        log.info("세션 데이터 있다");
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    @GetMapping("/")
    public String homeV3ArgumentResolver(@Login Member loginMember, Model model) {
        if (loginMember == null) {
            return "home";
        }

        log.info("세션 데이터 있다");
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}