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

    return true;  // 폼 제출을 진행
}