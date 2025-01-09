
$.sign_up = function () {
    window.location.href="/sign-up"
}

function validateForm() {
    let loginId = $("#loginId").val();
    let password = $("#password").val();

    // 로그인 ID와 비밀번호 유효성 검사
    if (loginId == "") {
        alert("로그인 ID를 입력하세요.");
        return false;  // 폼 제출을 막음
    }

    if (password == "") {
        alert("비밀번호를 입력하세요.");
        return false;  // 폼 제출을 막음
    }

    if (loginId.length < 5 || loginId.length > 20) {
        alert("로그인 ID는 5~20자 사이여야 합니다.");
        return false;  // 폼 제출을 막음
    }

    if (password.length < 8 || password.length > 20) {
        alert("비밀번호는 8~20자 사이여야 합니다.");
        return false;  // 폼 제출을 막음
    }

    // 폼 제출을 AJAX로 처리
    $.ajax({
        type: "POST",
        url: "/api/auth/login",  // 로그인 API URL
        data: $("#loginForm").serialize(), // 로그인 폼 데이터 직렬화
        success: function(response) {
            // 서버에서 반환된 리디렉션 URL을 가져와서 처리
            window.location.href = response.redirectUrl;  // 서버의 리디렉션 URL로 이동
        },
        error: function(xhr) {
            // 로그인 실패 처리
            const errorMessage = xhr.responseJSON.message; // 서버로부터 받은 오류 메시지
            alert(errorMessage);  // 예: alert로 오류 메시지 출력
        }
    });

    return false; // 폼의 기본 제출을 막음
}
