function changePW() {
    const currentPassword = $('#currentPassword').val();
    const newPassword = $('#newPassword').val();
    const confirmNewPassword = $('#confirmNewPassword').val();

    // 비밀번호 유효성 검사
    if (!currentPassword || !newPassword || !confirmNewPassword) {
        alert("모든 필드를 입력해주세요.");
        return;
    }

    if (newPassword.length < 5 || newPassword.length > 20) {
        alert('비밀번호는 5~20자 사이여야 합니다.');
        return;
    }

    if (confirmNewPassword.length < 5 || confirmNewPassword.length > 20) {
        alert('비밀번호는 5~20자 사이여야 합니다.');
        return;
    }

    // 비밀번호 일치 여부 확인
    if (newPassword !== confirmNewPassword) {
        alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
        return;
    }

    $.ajax({
        url: '/api/users/password',
        type: 'PATCH',  // 'POST' 대신 'PATCH'로 변경
        contentType: 'application/json',
        data: JSON.stringify({
            currentPassword: currentPassword,
            newPassword: newPassword,
            confirmNewPassword: confirmNewPassword
        })
    }).done(function (data, status, xhr) {
        if (status === "success") {
            console.log(data.responseData);
            alert('비밀번호 변경이 완료되었습니다.\n다시 로그인 하세요.');
            window.location.href = "/login"; // 로그인 페이지로 리다이렉트
        }
    }).fail(function (jqXHR, textStatus, errorThrown) {
        // 요청 실패 시 실행
        console.error("Request failed: " + textStatus + ", " + errorThrown);
        alert('비밀번호 변경에 실패했습니다.\n다시 시도해주세요.');
    });
}
