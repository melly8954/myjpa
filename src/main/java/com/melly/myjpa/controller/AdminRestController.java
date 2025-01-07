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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AdminRestController implements IResponseController {

    private final UserService userService;

    // 모든 사용자 조회
    // 컨트롤러에서 페이지 번호와 크기를 받아서 처리
    @GetMapping("/admin/users")
    public ResponseEntity<ResponseDto> getAllUsers(@RequestParam int page) {
        // 페이지 번호가 1 이상인지 확인
        if (page < 1) {
            return makeResponseEntity(HttpStatus.BAD_REQUEST, "페이지 번호는 1 이상이어야 합니다.", null);
        }


        int size = 5;

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

    @GetMapping("/admin/{name}")
    public ResponseEntity<ResponseDto> getUserName(@PathVariable String name, @RequestParam int page, @RequestParam(defaultValue = "5") int size) {
        try{
            if(name == null || name.isEmpty()) {
                return makeResponseEntity(HttpStatus.BAD_REQUEST,"이름을 정확히 입력하세요",null);
            }
            // 페이지네이션을 적용하기 위해 Pageable 사용
            Pageable pageable = PageRequest.of(page - 1, size); // 페이지는 0부터 시작하므로, page - 1로 처리
            Page<UserEntity> user = this.userService.getUserByName(name,pageable);
            if(user == null || user.isEmpty()) {
                return makeResponseEntity(HttpStatus.NOT_FOUND,"해당 유저가 조회되지 않습니다.",null);
            }
            return makeResponseEntity(HttpStatus.OK,"해당 유저의 정보를 조회합니다.",
                    new UserPageResponseDto<>(user.getContent(), user.getTotalElements(), user.getTotalPages(), user.getNumber()+1, user.getSize()));
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류 : " + e.getMessage(),null);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable Long id) {
        try{
            if(id == null || id<= 0) {
                return makeResponseEntity(HttpStatus.BAD_REQUEST,"회원 ID > 0 을 만족해야한다.",null);
            }
            this.userService.softDeleteUser(id);
            return makeResponseEntity(HttpStatus.OK,"해당 유저의 계정 삭제 처리 완료",true);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류 : " + e.getMessage(),null);
        }
    }

    @PatchMapping("/admin/{id}/undo")
    public ResponseEntity<ResponseDto> undoUser(@PathVariable Long id) {
        try{
            if(id == null || id<= 0) {
                return makeResponseEntity(HttpStatus.BAD_REQUEST,"회원 ID > 0 을 만족해야한다.",null);
            }
            this.userService.undoDeleteUser(id);
            return makeResponseEntity(HttpStatus.OK,"해당 유저의 계정 삭제를 취소함 ",true);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return makeResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류 : " + e.getMessage(),null);
        }
    }
}
