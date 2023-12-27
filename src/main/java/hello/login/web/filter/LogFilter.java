package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    /**
     * ServletRequest request` 는 HTTP 요청이 아닌 경우까지 고려해서 만든 인터페이스이다. HTTP를
     * 사용하면 `HttpServletRequest httpRequest = (HttpServletRequest) request;` 와 같이 다운 케스팅 하면 된다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        /**
         * HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러
         * REQUEST 로그 -> 필터 -> 서블릿 -> 컨트롤러 -> RESPONSE 로그
         * 시간을 로그로 찍고 성능 최적화 할 수도 있다.
         * 다음 필터가 있으면 필터를 호출하고, 필터가 없으면 서블릿을 호출한다. 만약 이
         * 로직을 호출하지 않으면 다음 단계로 진행되지 않는다.
         */
        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            chain.doFilter(request, response); //doFilter 연쇄가 없으면 서블릿 호출도 없다.!!
        } catch (Exception e) {
            throw e;
        }finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }

    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
