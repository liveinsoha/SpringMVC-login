package hello.login.domain.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Member {

    private Long id;

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    @NotBlank
    private String name;

    /**
     * 지금은 간단한 예제니까 폼 객체 사용하지 않는다. 멤버도 폼 객체 사용하는 걸 고려하자
     */

}
