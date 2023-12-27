package hello.login.web.interceptor;

import hello.login.domain.member.Member;
import hello.login.web.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    /**
     * 서블릿 필터와 비교해서 코드가 매우 간결하다. 인증이라는 것은 컨트롤러 호출 전에만 호출되면 된다. 따라서
     * `preHandle` 만 구현하면 된다.
     * default는 구현이 되어 있기 떄문에 구현하지 않아도 된다
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 여기를 들어왔다는 의미는 무조건 인증 체크를 해야한다는 의미이다.
         */

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        log.info("사용자 인증 체크 인터셉터 실행[{{}]", requestURI);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            log.info("미인증 사용자 요청");
            response.sendRedirect("/login?redirectURL=" + requestURI);
            return false; //컨트롤러 호출하지 않겠다.
        }
        log.info("사용자 인증 성공");
        return true;
    }
}
