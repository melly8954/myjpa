$(document).ready(function() {
    $.loadUserList(); // 바뀐 함수 이름으로 호출

    // 엔터 키로 버튼 클릭 효과 추가
    $("#findName").keypress(function(event) {
        if (event.which === 13) {  // Enter key code
            event.preventDefault();  // 기본 엔터키 동작 방지
            $.showUser();  // 조회 버튼 클릭과 같은 동작 수행
        }
    });

});

$.renderUserList = function(data) {
    let html = "";
    // 'users' 배열을 순회하며 HTML 생성
    data.responseData.users.forEach(function(user) {
        let accountStatus = user.deleteFlag ? "계정이 삭제된 상태입니다." : "계정을 정상적으로 사용 가능합니다.";

        // 계정 삭제 취소 버튼을 deleteFlag가 true일 때만 보이게 처리
        let undoButton = user.deleteFlag
            ? `<button id="undo" onclick="undoDeleteUser(${user.id})">계정 삭제 취소</button>`
            : '';

        html += `
            <div>
                <p>ID: ${user.id} <button onclick="deleteUser(${user.id})">계정 삭제</button> </p>
                <p>로그인 아이디: ${user.loginId}</p>
                <p>이름: ${user.name}</p>
                <p>닉네임: ${user.nickname}</p>
                <p>이메일: ${user.email}</p>
                <p>역할: ${user.role.roleName}</p>
                <p>계정 상태: ${accountStatus} ${undoButton}</p> 
                <p>계정 삭제 시간: ${user.deleteDate}</p>
                <hr>
            </div>`;
    });
    $("#showUsers").html(html);
}


$.loadUserList = function () {
    const page = 1;

    $("#searchPageDiv").hide();  // 검색 결과 페이지네이션 숨기기
    $("#pageDiv").show();  // 기존 페이지네이션 보이기

    $.ajax({
        url: `/api/admin/users?page=${page}`,
        method: "GET",
    }).done(function (data, status, xhr) {
        // ajax 의 요청이 완료되서 응답이 오면 실행한다.
        // data : 실제 응답 데이터
        // status : http 응답 성공 일 경우 "success"
        // xhr : 부가 정보
        console.log("done:data=" + data + ", status=", status, ", xhr=", xhr);
        // 요청 성공 시 실행
        if (status === "success") {
            $.renderUserList(data);  // 사용자 목록 렌더링
            $.makePageUI(data.responseData.totalElements, page, "#pageDiv");   // 페이지네이션 UI 생성
        } else {
            $("#showUsers").html(`<p>오류: ${data.message}</p>`);
        }
    }).fail(function (jqXHR, textStatus, errorThrown) {
        // 요청 실패 시 실행
        console.error("Request failed: " + textStatus + ", " + errorThrown);
        $("#showUsers").html("<p>회원 정보를 불러오는 중 오류가 발생했습니다.</p>");
    });
}

$.makePageUI = function(paramTotal, paramPage, pageDivId, isSearch) {
    const rowsOnePage = 5;  // 페이지 당 항목 수 고정
    let totPage = Math.ceil(paramTotal / rowsOnePage);  // 전체 페이지 수
    let startPage = getStartPage(paramPage);
    let endPage = getEndPage(startPage, paramTotal);
    let prev = (paramPage - 1) < 1 ? 1 : paramPage - 1;
    let next = (paramPage + 1) >= totPage ? totPage : paramPage + 1;

    $(pageDivId).html(""); // 페이지네이션 영역을 비웁니다.
    $(pageDivId).children().remove();

    // 검색일 경우, isSearch 값에 따라 검색용 페이지네이션을 처리
    $(pageDivId).append(`<a onclick="$.searchBoardList(${prev}, '${pageDivId}', ${isSearch});">Prev</a>`);

    if (paramTotal > 0) {
        for (let i = startPage; i <= endPage; i++) {
            if (paramPage === i) {
                $(pageDivId).append(`<a class="active">${i}</a>`);
            } else {
                $(pageDivId).append(`<a onclick="$.searchBoardList(${i}, '${pageDivId}', ${isSearch});">${i}</a>`);
            }
        }

        $(pageDivId).append(`<a onclick="$.searchBoardList(${next}, '${pageDivId}', ${isSearch});">Next</a>`);
    } else {
        // 결과가 없을 때 페이지네이션 버튼 숨기기
        $(pageDivId).html("");
    }
}

function getStartPage(page) {
    let one = 1;
    let ten = Math.floor((page - 1) / 10) * 10;
    let startPage = ten + one;
    return startPage;
}

function getEndPage(startPage, paramTotal) {
    const rowsOnePage = 5;  // 페이지 당 항목 수 고정
    let totPage = Math.ceil(paramTotal / rowsOnePage);
    let tPage = startPage + 9;
    return tPage < totPage ? tPage : totPage;
}

$.searchBoardList = function(page, pageDivId, isSearch) {
    const rowsOnePage = 5;  // 페이지 당 항목 수 고정
    let url = isSearch
        ? `/api/admin/${$("#findName").val()}?page=${page}&size=${rowsOnePage}`  // 검색 시 URL 처리
        : `/api/admin/users?page=${page}&size=${rowsOnePage}`;  // 기본 페이지 처리

    $.ajax({
        url: url,
        method: "GET",
    }).done(function(data,status,xhr) {
        if (status === "success") {
            $.renderUserList(data);  // 사용자 목록 렌더링
            $.makePageUI(data.responseData.totalElements, page, pageDivId, isSearch); // 페이지 UI 갱신
        }
    }).fail(function(jqXHR, textStatus, errorThrown) {
        console.error("Request failed: " + textStatus + ", " + errorThrown);
        $("#showUsers").html("<p>회원 정보를 불러오는 중 오류가 발생했습니다.</p>");
    });
}

// 유효성 검사 함수
$.validateName = function(name) {
    if (name.trim() === "") {
        alert("이름을 입력해주세요.");
        return false;
    }
    if (name.length < 2 || name.length > 100) {
        alert("이름은 2자 이상 100자 이하로 입력해주세요.");
        return false;
    }
    return true;
};

$.showUser = function () {
    const name = $("#findName").val()
    const page = 1;  // 페이지 1부터 시작
    const rowsOnePage = 5;  // 페이지 당 항목 수 고정

    // 유효성 검사 함수 호출
    if (!$.validateName(name)) {
        return; // 유효성 검사 실패 시 종료
    }

    $("#pageDiv").hide();  // 기존 페이지네이션 숨기기
    $("#searchPageDiv").show();  // 검색 결과 페이지네이션 보기

    $.ajax({
        url: `/api/admin/${name}?page=${page}&size=${rowsOnePage}`,
        method: "GET",
    }).done(function (data, status, xhr) {
        if (status === "success") {
            $.renderUserList(data);  // 사용자 목록 렌더링
            // 페이지네이션 UI 생성, 검색 페이지네이션을 위한 isSearch 매개변수 추가
            $.makePageUI(data.responseData.totalElements, page, "#searchPageDiv", true);
        } else {
            $("#showUsers").html(`<p>오류: ${data.message}</p>`);
        }
    }).fail(function (jqXHR, textStatus, errorThrown) {
        // 요청 실패 시 실행
        console.error("Request failed: " + textStatus + ", " + errorThrown);
        if (jqXHR.status === 404) {
            $("#showUsers").html(`<p>검색하신 ${name} 라는 이름의 회원은 존재하지 않습니다.</p>`);
            $.makePageUI(0, 1,"#searchPageDiv");  // 404일 때 페이지네이션을 0으로 처리
        } else {
            $("#showUsers").html("<p>회원 정보를 불러오는 중 오류가 발생했습니다.</p>");
            $.makePageUI(0, 1,"#searchPageDiv");
        }
    });
}

// 사용자 계정 삭제
function deleteUser(id) {
    if (confirm(`${id}번 계정을 삭제 하시겠습니까?`)) {
        $.ajax({
            url: `/api/admin/${id}`,
            type: 'DELETE',
        }).done(function (data, status, xhr){
            if (status === "success") {
                console.log(data.responseData);
                // 현재 페이지 번호 가져오기
                const pageDivId = $("#searchPageDiv").is(":visible") ? "#searchPageDiv" : "#pageDiv"; // 현재 보이는 페이지 영역 구분
                const currentPage = parseInt($(pageDivId + " .active").text()) || 1; // 현재 페이지 번호 가져오기, 기본은 1페이지
                const isSearch = $("#searchPageDiv").is(":visible"); // 검색 중인지 여부 확인
                $.searchBoardList(currentPage, pageDivId, isSearch); // 삭제 후 현재 페이지로 목록 갱신
            }
        }).fail(function (jqXHR, textStatus, errorThrown) {
            // 요청 실패 시 실행
            console.error("Request failed: " + textStatus + ", " + errorThrown);
        });
    } else {
        // 취소 버튼을 누른 경우
        console.log('버튼 실행이 취소 되었습니다.');
    }
}

function undoDeleteUser(id){
    if (confirm(`삭제된 ${id}번 계정을 복구 하시겠습니까?`)) {
        $.ajax({
            url: `/api/admin/${id}/undo`,
            type: 'PATCH',
        }).done(function (data, status, xhr){
            if (status === "success") {
                console.log(data.responseData);
                // 현재 페이지 번호 가져오기
                const pageDivId = $("#searchPageDiv").is(":visible") ? "#searchPageDiv" : "#pageDiv"; // 현재 보이는 페이지 영역 구분
                const currentPage = parseInt($(pageDivId + " .active").text()) || 1; // 현재 페이지 번호 가져오기, 기본은 1페이지
                const isSearch = $("#searchPageDiv").is(":visible"); // 검색 중인지 여부 확인
                $.searchBoardList(currentPage, pageDivId, isSearch); // 삭제 후 현재 페이지로 목록 갱신
            }
        }).fail(function (jqXHR, textStatus, errorThrown) {
            // 요청 실패 시 실행
            console.error("Request failed: " + textStatus + ", " + errorThrown);
        });
    } else {
        // 취소 버튼을 누른 경우
        console.log('버튼 실행이 취소되었습니다.');
    }
}