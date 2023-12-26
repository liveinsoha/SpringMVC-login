package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.session.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static hello.login.web.session.SessionConst.LOGIN_MEMBER;

@Controller
@RequiredArgsConstructor

@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    //@PostMapping("/login")
    public String login(@ModelAttribute @Validated LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        /**
         * 로그인 컨트롤러는 로그인 서비스를 호출해서 로그인에 성공하면 홈 화면으로 이동하고, 로그인에 실패하면
         * `bindingResult.reject()` 를 사용해서 글로벌 오류( `ObjectError` )를 생성한다. 그리고 정보를 다시 입력하도
         * 록 로그인 폼을 뷰 템플릿으로 사용한다.
         * 필드만 보고 판단하는 게 아니라 객체만으로 되는 에러가 아니다. DB까지 뒤진다음에 에러인지 아닌지 확인할 수 있다.
         * 자바 코드로 검증을 하는 게 낫다
         */
        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다");
            return "login/loginForm";
        }

        log.info("로그인 성공 = {}", loginMember);
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        return "redirect:/";
    }

    //@PostMapping("/login")
    public String loginV2(@ModelAttribute @Validated LoginForm form, BindingResult bindingResult, HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다");
            return "login/loginForm";
        }

        log.info("로그인 성공 = {}", loginMember);
        sessionManager.createSession(loginMember, response);
        //로그인 할 때마다 매번 새로운 sessionId 발급

        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV3(@ModelAttribute @Validated LoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login? {}", loginMember);

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 일치하지 않습니다");
            return "login/loginForm";
        }

        log.info("로그인 성공 = {}", loginMember);
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        session.setAttribute(LOGIN_MEMBER, loginMember);
        /**
         * 에제라서 Member객체 전부를 담았지만, MemberId정도 간단한 정보만 담는 게 좋다
         * 세션에는 최소한의 데이터만 보관해야 한다
         */

        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        expireCookie(response, "memberId");
        //기존의 sessionId는 폐기된다
        return "redirect:/";
    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {
        sessionManager.expire(request);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); //세션 저장소에 있는 데이터를 버린다.
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
