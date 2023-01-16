package uz.mysite.springsecurityexample.config.annotations;


import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.mysite.springsecurityexample.dto.exceptions.ForbiddenException;
import uz.mysite.springsecurityexample.entity.User;

@Component
@Aspect
public class CheckRoleExecutor {
    @Before(value = "@annotation(checkRole)")
    public void checkRoleExecutor(CheckRole checkRole) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean flag = false;
        for (GrantedAuthority authority : user.getAuthorities()) {
            if (authority.getAuthority().equals(checkRole.value())) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ForbiddenException();
        }

    }
}
