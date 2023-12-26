package hello.login.web.session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

@RestController
@Slf4j
public class SessionController {

    @GetMapping("/session-info")
    public String sessionInfo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "세션이 없습니다";
        }

        //세션에 저장된 여러가지 특성들 출력
        session.getAttributeNames().asIterator().forEachRemaining(name ->
                log.info("name = {}, value = {}", name, session.getAttribute(name))
        );

        log.info("sessionId={}", session.getId());
        log.info("maxInactiveInterval={}", session.getMaxInactiveInterval());
        log.info("creationTime={}", new Date(session.getCreationTime()));
        log.info("lastAccessedTime={}", new Date(session.getLastAccessedTime()));
        log.info("isNew={}", session.isNew());
        return "세션 출력";

        /**
         * 세션의 타임아웃 시간은 해당 세션과 관련된 `JSESSIONID` 를 전달하는 HTTP 요청이 있으면 현재 시간으로 다시 초
         * 기화 된다. 이렇게 초기화 되면 세션 타임아웃으로 설정한 시간동안 세션을 추가로 사용할 수 있다.
         * `session.getLastAccessedTime()` : 최근 세션 접근 시간
         * `LastAccessedTime` 이후로 timeout 시간이 지나면, WAS가 내부에서 해당 세션을 제거한다.
         */
    }
}
