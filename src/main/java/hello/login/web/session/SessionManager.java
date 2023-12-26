package hello.login.web.session;

import hello.login.domain.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {
    /**
     * 웹 쪽에 있는 세션 기능. 세션 기능을 직접 구현해보자.
     * 멤버에 대한 세션을 직접 만들고 세션 저장소에 저장한다.
     * 결국은 쿠키를 사용하는데 키-값 형태로 쿠키이름은 mySessionId로 통일하고 거기에 세션Id가 값으로 저장되어있다
     * 나중에 request에서 cookie를 가지고 요청을 할 때 그 mySessionId에 대한 값을 꺼내어 SessionId를 얻고
     * 세션 저장소에서 이에 해당하는 멤버 객체가 있는지 유효성을 검사한다.
     */

    private static final String SESSION_COOKIE_NAME = "mySessionId";
    Map<String, Object> sessionStore = new ConcurrentHashMap<String, Object>();


    public void createSession(Object value, HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString(); //로그인 할 때마다 매번 새로운 sessionId발급
        sessionStore.put(sessionId, value);
        log.info("createSession : sessionId = {} , value = {},", sessionId, value);
        Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, sessionId);
        response.addCookie(sessionCookie);
    }

    public Object getSession(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie == null) {
            return null;
        }

        return sessionStore.get(cookie.getValue());
    }

    public void expire(HttpServletRequest request) {
        Cookie cookie = findCookie(request, SESSION_COOKIE_NAME);
        if (cookie != null) {

            Member removed = (Member) sessionStore.remove(cookie.getValue()); //기존의 sessionId는 폐기된다
            log.info("expire : sessionId = {} , value = {},", cookie.getValue(), removed);
        }
    }

    private Cookie findCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findFirst()
                .orElse(null);
    }


}
