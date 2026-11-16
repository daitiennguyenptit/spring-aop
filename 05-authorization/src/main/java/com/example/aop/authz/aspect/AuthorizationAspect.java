package com.example.aop.authz.aspect;

import com.example.aop.authz.annotation.RequiresRole;
import com.example.aop.authz.context.CurrentUser;
import com.example.aop.authz.context.UserContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Before la du: neu khong du quyen thi nem exception -> method nghiep vu khong bao gio chay.
 * Order cao nhat: kiem tra quyen TRUOC moi aspect khac (cache, transaction...).
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 50)   // sau ExposeInvocationInterceptor (+1), truoc cac aspect khac
public class AuthorizationAspect {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationAspect.class);

    @Before("@annotation(com.example.aop.authz.annotation.RequiresRole) "
            + "|| @within(com.example.aop.authz.annotation.RequiresRole)")
    public void checkRole(JoinPoint jp) {
        RequiresRole required = resolveAnnotation(jp);
        CurrentUser user = UserContext.get();
        if (user == null) {
            throw new UnauthenticatedException();
        }
        boolean allowed = Arrays.stream(required.value()).anyMatch(user.roles()::contains);
        if (!allowed) {
            log.warn("DENY user={} method={} required={} has={}",
                    user.username(), jp.getSignature().toShortString(), Arrays.toString(required.value()), user.roles());
            throw new AccessDeniedException("Missing role " + Arrays.toString(required.value()));
        }
    }

    private static RequiresRole resolveAnnotation(JoinPoint jp) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        Class<?> targetClass = AopUtils.getTargetClass(jp.getTarget());
        Method specific = AopUtils.getMostSpecificMethod(method, targetClass);
        RequiresRole onMethod = AnnotatedElementUtils.findMergedAnnotation(specific, RequiresRole.class);
        return onMethod != null ? onMethod : AnnotatedElementUtils.findMergedAnnotation(targetClass, RequiresRole.class);
    }
}
