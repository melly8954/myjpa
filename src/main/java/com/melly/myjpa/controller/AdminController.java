package com.melly.myjpa.controller;

import com.melly.myjpa.common.IResponseController;
import com.melly.myjpa.common.ResponseDto;
import com.melly.myjpa.domain.UserEntity;
import com.melly.myjpa.dto.UserPageResponseDto;
import com.melly.myjpa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminController implements IResponseController {

    private final UserService userService;

    // 모든 사용자 조회
    // 컨트롤러에서 페이지 번호와 크기를 받아서 처리
    @GetMapping("/admin/users")
    public ResponseEntity<ResponseDto> getAllUsers(@RequestParam int page, @RequestParam int size) {
        // 페이지 번호가 1 이상인지 확인
        if (page < 1) {
            return makeResponseEntity(HttpStatus.BAD_REQUEST, "페이지 번호는 1 이상이어야 합니다.", null);
        }

        // 페이지 크기 제한 (예: 최대 100)
        if (size > 100) {
            size = 100;
        }

        Pageable pageable = PageRequest.of(page - 1, size);  // 1-based index를 0-based로 변환

        try {
            Page<UserEntity> allUsers = this.userService.getAllUsers(pageable);

            // 페이징 정보 포함하여 응답
            return makeResponseEntity(HttpStatus.OK, "회원 목록 조회 성공",
                    new UserPageResponseDto<>(allUsers.getContent(), allUsers.getTotalElements(), allUsers.getTotalPages(), allUsers.getNumber()+1, allUsers.getSize()));
        } catch (Exception e) {
            log.error("서버 오류: ", e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", null);
        }
    }
}
