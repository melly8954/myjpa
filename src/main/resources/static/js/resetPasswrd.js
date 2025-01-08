$(document).ready(function () {
    $('#verifyCodeSection').hide();
    $('#send-code-button').on('click', function () {
        let email = $('#email').val();
        // 이메일이 데이터베이스에 있는지 확인
        $.ajax({
            url: '/api/users/check-email',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ mail: email })
        }).done(function (response) {
            if (response) {
                // 로딩 스피너 표시
                $('#loading-spinner').show();
                // 이메일이 존재하면 임시 비밀번호 발송
                $.ajax({
                    url: '/api/users/reset-password',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({ mail: email })
                }).done(function () {
                    $('#verifyCodeSection').slideDown(); // 인증 코드 입력 섹션 표시
                    alert('임시 비밀번호가 이메일로 발송되었습니다. 이메일을 확인해주세요.');
                }).fail(function () {
                    alert('임시 비밀번호 발송에 실패했습니다. 다시 시도해주세요.');
                }).always(function () {
                    $('#loading-spinner').hide(); // 항상 로딩 스피너 숨기기
                });
            } else {
                $('#emailCheckMessage').text('해당 이메일로 가입된 사용자가 없습니다.').show();
            }
        }).fail(function () {
            alert('이메일 확인 중 오류가 발생했습니다. 다시 시도해주세요.');
        });
    });

    $('#verify-temporary-password-button').on('click', function () {
        const email = $('#email').val();
        const tempPassword = $('#temporaryPassword').val();
        // 임시 비밀번호 확인
        $.ajax({
            url: '/api/users/verify-temporary-password',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ mail: email, tempPassword: tempPassword })
        }).done(function (response) {
            if (response === "Verified") {
                alert("임시 비밀번호가 확인되었습니다. 로그인하세요.");
                window.location.href = "/login";
            } else {
                $('#verificationMessage').text("임시 비밀번호가 일치하지 않습니다. 다시 시도하세요.").show();
            }
        }).fail(function () {
            alert("임시 비밀번호 검증에 실패했습니다. 다시 시도하세요.");
        });
    });

});
