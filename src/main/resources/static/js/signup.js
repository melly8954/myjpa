// 회원가입 버튼 클릭 시 폼 전송 막기
$("#submit").on("click", function (event) {
    event.preventDefault(); // 기본 폼 전송 막기

    // 각 유효성 검사 함수 호출
    if (!validatePassword() || !validateEmailDuplicate() || !validateEmailVerification()) {
        return; // 유효하지 않으면 함수 종료
    }

    const formData = {
        loginId: $("#loginId").val(),
        password: $("#password").val(),
        confirmPassword: $("#confirmPassword").val(),
        name: $("#name").val(),
        nickname: $("#nickname").val(),
        email: $("#email").val()
    };

    $.ajax({
        url: "/api/users/register",
        method: "POST",
        contentType: "application/json",
        data: JSON.stringify(formData)
    }).done(function (data) {
        if (data.message === "회원 가입 성공!") {
            alert("회원가입이 성공적으로 완료되었습니다!");
            window.location.href = "/";
        } else {
            alert("회원가입 실패: " + data.message);
        }
    }).fail(function (jqXHR, textStatus, errorThrown) {
        // 응답에서 오류 메시지 추출하여 출력
        let errorMessage = "회원가입 실패: 입력란을 모두 작성하시길 바랍니다.";

        if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
            errorMessage = jqXHR.responseJSON.message; // 서버에서 보낸 메시지를 사용
        }

        console.error("Request failed: " + textStatus + ", " + errorThrown);
        alert(errorMessage); // 에러 메시지 출력
    });
});

// 비밀번호 유효성 검사
function validatePassword() {
    const password = $('#password').val();
    const confirmPassword = $('#confirmPassword').val();
    if (password === '') {
        displayValidationMessage('#password', '비밀번호를 입력해주세요.', 'error');
        return false;
    } else if (password !== confirmPassword) {
        displayValidationMessage('#password', '비밀번호가 일치하지 않습니다.', 'error');
        return false;
    } else {
        displayValidationMessage('#password', '비밀번호가 유효합니다.', 'success');
        return true;
    }
}

// 이메일 중복 유효성 검사
function validateEmailDuplicate() {
    if ($('#emailCheckMessage').hasClass('error')) {
        displayValidationMessage('#email', '이메일 중복 확인이 필요합니다.', 'error');
        return false;
    } else {
        displayValidationMessage('#email', '사용 가능한 이메일입니다.', 'success');
        return true;
    }
}

// 이메일 인증 상태 체크
function validateEmailVerification() {
    if (!$('#verificationMessage').hasClass('success')) {
        alert('이메일 인증을 완료해주세요.');
        return false;
    }
    return true;
}

// 유효성 검사 메시지 표시 함수
function displayValidationMessage(inputId, message, status) {
    const span = $('<span>').addClass('validation-message').text(message);
    if (status === 'error') {
        span.css('color', 'red');
    } else {
        span.css('color', 'green');
    }

    // 기존의 메시지가 있다면 제거하고 새로운 메시지로 갱신
    $(inputId).next('.validation-message').remove();
    $(inputId).after(span);
}

// 이메일 중복 검사
$('#check-email-button').on('click', function() {
    let email = $('#email').val();

    $.ajax({
        type: 'POST',
        url: '/api/users/check-email',
        contentType: 'application/json',
        data: JSON.stringify({ mail: email }),
        success: function(response) {
            if (response) {
                $('#emailCheckMessage').text("아이디가 이미 존재합니다.").removeClass('success').addClass('error');
                displayValidationMessage('#email', '이메일 중복이 확인되었습니다.', 'error');
            } else {
                $('#emailCheckMessage').text("사용 가능한 아이디입니다.").removeClass('error').addClass('success');
                displayValidationMessage('#email', '사용 가능한 이메일입니다.', 'success');
            }
            validateEmailDuplicate();
        },
        error: function(error) {
            $('#emailCheckMessage').text('이메일 확인 중 오류가 발생했습니다. 다시 시도해주세요.').removeClass('success').addClass('error');
            displayValidationMessage('#email', '이메일 확인 중 오류가 발생했습니다.', 'error');
            validateEmailDuplicate();
        }
    });
});

// 인증 메일 발송
$('#send-code-button').on('click', function() {
    // 이메일 중복 확인을 했는지 확인
    if (!$('#emailCheckMessage').hasClass('success')) {
        alert('이메일 중복 확인을 진행하지 않으면 코드가 정상적으로 발송되지 않습니다.');
        return; // 이메일 중복 확인을 하지 않았으면 코드 발송을 중단
    }

    let email = $('#email').val();

    // 로딩 스피너 표시
    $('#loading-spinner').show();

    $.ajax({
        type: 'POST',
        url: '/api/users/mail',
        contentType: 'application/json',
        data: JSON.stringify({ mail: email }),
        success: function(response) {
            $('#verifyCodeSection').show();
            alert('인증 메일이 발송되었습니다. 인증 번호를 확인해주세요.');
            $('#loading-spinner').hide(); // 로딩 스피너 숨기기
        },
        error: function(error) {
            alert('메일 발송에 실패했습니다. 다시 시도해주세요.');
            $('#loading-spinner').hide(); // 로딩 스피너 숨기기
        }
    });
});

// 인증 코드 확인
$('#verify-code-button').on('click', function() {
    let email = $('#email').val();
    let code = $('#verificationCode').val();

    $.ajax({
        type: 'POST',
        url: '/api/users/verify-code',
        contentType: 'application/json',
        data: JSON.stringify({ mail: email, code: code }),
        success: function(response) {
            if (response === 'Verified') {
                alert('인증이 성공적으로 완료되었습니다.');
                $('#verificationMessage').removeClass('error').addClass('success'); // 성공 클래스를 추가
                displayValidationMessage('#verificationCode', '인증 성공', 'success');
            } else {
                alert('인증 실패. 올바른 코드를 입력하세요.');
                $('#verificationMessage').removeClass('success').addClass('error'); // 실패 클래스를 추가
                displayValidationMessage('#verificationCode', '인증 코드가 올바르지 않습니다.', 'error');
            }
        },
        error: function(error) {
            alert('인증 실패. 다시 시도해주세요.');
            $('#verificationMessage').removeClass('success').addClass('error'); // 실패 클래스를 추가
            displayValidationMessage('#verificationCode', '인증 실패. 다시 시도해주세요.', 'error');
        }
    });
});
