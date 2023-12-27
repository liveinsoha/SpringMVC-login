package hello.login.web.filter;

import hello.login.domain.member.Member;
import hello.login.web.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class LoginCheckFilter implements Filter {
    /**
     * 서블릿 필터를 잘 사용한 덕분에 로그인 하지 않은 사용자는 나머지 경로에 들어갈 수 없게 되었다. 공통 관심사를 서블
     * 릿 필터를 사용해서 해결한 덕분에 향후 로그인 관련 정책이 변경되어도 이 부분만 변경하면 된다.
     */

    //얘네들은 세션 검증 제외시키기 위함
    private static final String[] whiteList = {"/", "/login", "/members/add", "/css/*", "/logout"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = ((HttpServletRequest) request).getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터 시작 {}", requestURI);

            if (isCheckPath(requestURI)) {

                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자입니다");

                    //로그인 페이지로 리다이렉트
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    /**
                     *  로그인 페이지로 리다이렉트 후 로그인에 성공한 경우 세션을 가지고 다시 요청했던 URL로 이동하기 위해
                     *  쿼리 파라미터로 requestURL을 넘긴다.
                     */
                    return;
                }
                log.info("사용자 인증 완료");
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        }finally {
            log.info("인증 체크 필터 종료 {}", requestURI); //try에서 return하더라도 finally는 반드시 거쳐나간다.
        }
    }


    private boolean isCheckPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
