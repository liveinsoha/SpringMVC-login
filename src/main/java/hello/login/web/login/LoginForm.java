package hello.login.web.login;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginForm {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
}
