package mobile.security.resolver;

import mobile.security.core.AppUserDetail;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasUserIdAnnotation = parameter.hasParameterAnnotation(CurrentUserId.class);
        boolean hasUserAnnotation = parameter.hasParameterAnnotation(CurrentUser.class);
        return hasUserIdAnnotation || hasUserAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        if (parameter.hasParameterAnnotation(CurrentUserId.class)) {
            CurrentUserId annotation = parameter.getParameterAnnotation(CurrentUserId.class);
            String userId = PrincipalResolver.resolveUserId();
            if (annotation != null && annotation.required() && userId == null) {
                throw new AccessDeniedException("User authentication required");
            }
            return userId;
        }

        if (parameter.hasParameterAnnotation(CurrentUser.class)) {
            CurrentUser annotation = parameter.getParameterAnnotation(CurrentUser.class);
            AppUserDetail user = PrincipalResolver.resolveUser();
            if (annotation != null && annotation.required() && user == null) {
                throw new AccessDeniedException("User authentication required");
            }
            return user;
        }

        return null;
    }
}
