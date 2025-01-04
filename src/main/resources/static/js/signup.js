// 회원가입 버튼 클릭 시 폼 전송 막기
$("#submit").on("click", function (event) {
    event.preventDefault(); // 기본 폼 전송 막기

    // 이메일 인증이 완료되었는지 체크
    if (!validateEmailVerification()) {
        return; // 이메일 인증이 안된 경우 회원가입 진행 안 함
    }

    // 각 유효성 검사 함수 호출
    if( !validateLoginId() ||
        !validatePassword() ||
        !validateName() ||
        !validateNickname() ||
        !validateEmailDuplicate() ||
        !validateEmailVerification() ){
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

// 로그인 ID 중복 확인 버튼 클릭 이벤트
$('#check-loginId-button').on('click', function () {
    validateLoginId();
});

// 로그인 ID 유효성 검사
function validateLoginId() {
    let loginId = $('#loginId').val();

    // 클라이언트에서 최소 길이 체크
    if (loginId.length < 5 || loginId.length > 20) {
        displayValidationMessage('#loginId', '로그인 ID는 5~20자 사이여야 합니다.', 'error');
        return false;
    }

    // 서버에서 중복 확인
    let isValid = false;
    $.ajax({
        type: 'POST',
        url: '/api/users/check-loginId',
        contentType: 'application/json',
        data: JSON.stringify({ loginId: loginId }),
        async: false, // 동기 처리
        success: function (response) {
            if (response) {
                displayValidationMessage('#loginId', '로그인 ID가 이미 존재합니다.', 'error');
                isValid = false;
            } else {
                displayValidationMessage('#loginId', '사용 가능한 로그인 ID입니다.', 'success');
                isValid = true;
            }
        },
        error: function () {
            displayValidationMessage('#loginId', '로그인 ID 확인 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
            isValid = false;
        }
    });

    return isValid;
}

// 비밀번호 유효성 검사
function validatePassword() {
    const password = $('#password').val();
    const confirmPassword = $('#confirmPassword').val();

    // 비밀번호 입력 여부 확인
    if (password === '') {
        displayValidationMessage('#password', '비밀번호를 입력해주세요.', 'error');
        return false;
    }

    // 비밀번호 길이 확인
    if (password.length < 8 || password.length > 20) {
        displayValidationMessage('#password', '비밀번호는 8~20자 사이여야 합니다.', 'error');
        return false;
    }

    // 비밀번호와 확인 비밀번호가 일치하는지 확인
    if (password !== confirmPassword) {
        displayValidationMessage('#password', '비밀번호가 일치하지 않습니다.', 'error');
        return false;
    }

    // 모든 조건이 통과되면 유효한 비밀번호로 판단
    displayValidationMessage('#password', '비밀번호가 유효합니다.', 'success');
    return true;
}

// 이름 유효성 검사
function validateName() {
    const name = $('#name').val();
    if (name.length < 2 || name.length > 100) {
        displayValidationMessage('#name', '이름은 2~100자 사이여야 합니다.', 'error');
        return false;
    } else {
        displayValidationMessage('#name', '이름이 유효합니다.', 'success');
        return true;
    }
}

// 닉네임 중복 확인 버튼 클릭 이벤트
$('#check-nickname-button').on('click', function () {
    validateNickname();
});

// 닉네임 유효성 검사
function validateNickname() {
    let nickname = $('#nickname').val();

    // 닉네임 최소 길이 및 최대 길이 체크
    if (nickname.length < 2 || nickname.length > 30) {
        displayValidationMessage('#nickname', '닉네임은 2~30자 사이여야 합니다.', 'error');
        return false;
    }

    // 서버에서 중복 확인
    let isValid = false;
    $.ajax({
        type: 'POST',
        url: '/api/users/check-nickname',
        contentType: 'application/json',
        data: JSON.stringify({ nickname: nickname }),
        async: false, // 동기 처리
        success: function (response) {
            if (response) {
                displayValidationMessage('#nickname', '닉네임이 이미 존재합니다.', 'error');
                isValid = false;
            } else {
                displayValidationMessage('#nickname', '사용 가능한 닉네임입니다.', 'success');
                isValid = true;
            }
        },
        error: function () {
            displayValidationMessage('#nickname', '닉네임 확인 중 오류가 발생했습니다. 다시 시도해주세요.', 'error');
            isValid = false;
        }
    });

    return isValid;
}

// 이메일 중복 유효성 검사
function validateEmailDuplicate() {
    if ($('#emailCheckMessage').hasClass('error')) {
        displayValidationMessage('#email', ' 이미 사용되고 있는 email 입니다.', 'error');
        return false;
    } else {
        displayValidationMessage('#email', '사용 가능한 email 입니다.', 'success');
        return true;
    }
}

// 이메일 중복 검사
$('#check-email-button').on('click', function() {
    let email = $('#email').val();

    const emailRegex = /^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$/;

    // 이메일 형식 검사
    if (!emailRegex.test(email)) {
        displayValidationMessage('#email', '올바른 이메일 형식을 입력해주세요.', 'error');
        return false;
    }

    // 클라이언트에서 최소 길이 체크
    if (email.length < 10 || email.length > 255) {
        displayValidationMessage('#email', 'email 의 길이는 10~255자 사이여야 합니다.', 'error');
        return false;
    }

    $.ajax({
        type: 'POST',
        url: '/api/users/check-email',
        contentType: 'application/json',
        data: JSON.stringify({ mail: email }),
        success: function(response) {
            if (response) {
                $('#emailCheckMessage').removeClass('success').addClass('error');
                displayValidationMessage('#email', 'email 중복이 확인되었습니다.', 'error');
            } else {
                $('#emailCheckMessage').removeClass('error').addClass('success');
                displayValidationMessage('#email', '사용 가능한 email 입니다.', 'success');
            }
            validateEmailDuplicate();
        },
        error: function(error) {
            $('#emailCheckMessage').removeClass('success').addClass('error');
            displayValidationMessage('#email', 'email 확인 중 오류가 발생했습니다.', 'error');
            validateEmailDuplicate();
        }
    });
});

// 인증 메일 발송
$('#send-code-button').on('click', function() {
    // 이메일 중복 확인을 했는지 확인
    if (!$('#emailCheckMessage').hasClass('success')) {
        alert('email 중복 확인을 진행하지 않으면 코드가 정상적으로 발송되지 않습니다.');
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
            alert('인증 email 이 발송되었습니다. 인증 번호를 확인해주세요.');
            $('#loading-spinner').hide(); // 로딩 스피너 숨기기
        },
        error: function(error) {
            alert('email 발송에 실패했습니다. 다시 시도해주세요.');
            $('#loading-spinner').hide(); // 로딩 스피너 숨기기
        }
    });
});

// 이메일 인증 상태 체크
function validateEmailVerification() {
    const verificationCode = $('#verificationCode').val();

    // 인증 코드가 입력되지 않았으면 이메일 인증이 완료되지 않았다고 판단
    if (!verificationCode || !$('#verificationMessage').hasClass('success')) {
        alert('이메일 인증을 완료해주세요.');
        return false;
    }
    return true;
}

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


