package hello.login.web.argumentResolver;

import hello.login.domain.member.Member;
import hello.login.web.session.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("argumentResolver.supportsParameter 실행");
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberTypeParam = Member.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberTypeParam;
        //true인 경우 아래 argumentResolver를 통해 파라미터 생성한다.
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("argumentResolver.resolveArgument 실행");
        //두 번째 실행하면 supportsParameter는 캐시가 있어서, 실행해도 되겠다고 판단해. // 실행하지 않는다
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return session.getAttribute(SessionConst.LOGIN_MEMBER);
    }
}
