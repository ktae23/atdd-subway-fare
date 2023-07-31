package nextstep.acceptance;

import nextstep.auth.token.TokenResponse;
import nextstep.marker.AcceptanceTest;
import nextstep.member.application.dto.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static nextstep.acceptance.MemberSteps.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@AcceptanceTest
class MemberAcceptanceTest {
    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final int AGE = 20;

    @DisplayName("회원가입을 한다.")
    @Test
    void createMember() {
        // when
        var response = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("회원 정보를 조회한다.")
    @Test
    void getMember() {
        // given
        var createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // when
        var response = 회원_정보_조회_요청(createResponse);

        // then
        회원_정보_조회됨(response, EMAIL, AGE);

    }

    @DisplayName("회원 정보를 수정한다.")
    @Test
    void updateMember() {
        // given
        var createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // when
        var response = 회원_정보_수정_요청(createResponse, "new" + EMAIL, "new" + PASSWORD, AGE);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("회원 정보를 삭제한다.")
    @Test
    void deleteMember() {
        // given
        var createResponse = 회원_생성_요청(EMAIL, PASSWORD, AGE);

        // when
        var response = 회원_삭제_요청(createResponse);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 회원 가입을 생성하고
     * And 로그인을 하고
     * When 토큰을 통해 내 정보를 조회하면
     * Then 내 정보를 조회할 수 있다
     */
    @DisplayName("내 정보를 조회한다.")
    @Test
    void getMyInfo() {
        // given
        회원_생성_요청(EMAIL, PASSWORD, AGE);
        var signInResponse = 로그인(EMAIL, PASSWORD).as(TokenResponse.class);

        // when
        var response = 내_정보_조회_요청(signInResponse.getAccessToken()).as(MemberResponse.class);

        // then
        assertThat(response.getEmail()).isEqualTo(EMAIL);
        assertThat(response.getAge()).isEqualTo(AGE);
    }

    /**
     * Given 회원 가입을 생성하고
     * When 토큰 없이 내 정보를 조회하면
     * Then 403 실패 응답이 돌아 온다
     */
    @DisplayName("정보를 조회했으나 토큰이 없으면 실패.")
    @Test
    void getMyInfoForbidden() {
        // given
        회원_생성_요청(EMAIL, PASSWORD, AGE);
        로그인(EMAIL, PASSWORD).as(TokenResponse.class);

        // when
        assertThat(내_정보_조회_요청(null).statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}