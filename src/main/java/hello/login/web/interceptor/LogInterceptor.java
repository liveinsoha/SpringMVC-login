package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, uuid);

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            /**
             * 핸들러 정보는 어떤 핸들러 매핑을 사용하는가에 따라 달라진다. 스프링을 사용하면 일반적으로 `@Controller` ,
             * `@RequestMapping` 을 활용한 핸들러 매핑을 사용하는데, 이 경우 핸들러 정보로 `HandlerMethod` 가 넘어온다.
             * 여기서 hm.가지고 여러가지 부가 기능들을 수행할 수 있다.(어노테이션 기반 컨트롤러 호출인 경우 instanceof로 확인하고)
             * 핸들러 정보는 어떤 핸들러 매핑을 사용하는가에 따라 달라진다. 스프링을 사용하면 일반적으로 `@Controller` ,
             * `@RequestMapping` 을 활용한 핸들러 매핑을 사용하는데, 이 경우 핸들러 정보로 `HandlerMethod` 가 넘어온다.
             * **ResourceHttpRequestHandler**
             * `@Controller` 가 아니라 `/resources/static` 와 같은 정적 리소스가 호출 되는 경우
             * `ResourceHttpRequestHandler` 가 핸들러 정보로 넘어오기 때문에 타입에 따라서 처리가 필요하다.
             */
        }

        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);
        //여러 핸들러 중 어떤 핸들러를 호출하는지 알 수 있다
        return true; //true 리턴해야 handler가 호출 된다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle[{}] ", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);
        log.info("RESPONSE [{}][{}] \n", logId, requestURI);
        if (ex != null) { //예외가 터진 경우. 오류는 {}없이ㅐ 냥 넣으면 된다
            log.info("afterCompletion error!", ex);
        }
    }


}
