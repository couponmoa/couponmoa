# ApiResponse 사용법
- 첫번 째 파라미터에는 data값을, 두번 째 파라미터에는 message값을 넣는다
- 자세한 내용은 com.couponmoa.backend.common.dto.ApiResponse 클래스 참조
  
## ApiResponse.success("Response 메시지");
  ```
@PostMapping("/signup")
public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
    authService.signup(request);
    return ResponseEntity.ok(ApiResponse.success("회원가입에 성공했습니다."));
}
  ```
- 결과
  ```
    {
      “status”: “OK”,
      “code”: 201,
      “message”: “회원가입에 성공했습니다.”,
      “data”: null
    }
  ```
  
## ApiResponse.success(responseData, "Response 메시지");
```
@PostMapping("/signin")
public ResponseEntity<ApiResponse<AccessTokenResponse>> signin(@Valid @RequestBody SigninRequest request) {
    AccessTokenResponse response = authService.signin(request); //토큰값 반환
    return ResponseEntity.ok(ApiResponse.success(response,"로그인에 성공했습니다."));
}
```
- 결과
  ```
    {
      “status”: “OK”,
      “code”: 200,
      “message”: “로그인에 성공했습니다.”,
      “data”: {token}
    }
  ```

# 커스텀 에러 반환
```
  throw new ApplicationException(ErrorCode.INVALID_QUANTITY_IS_ZERO);
```
- ApplicationException(ErrorCode.설정에러명) 형태로 커스텀 에러 반환 가능
- 예시
  ```
  if (findCoupon.getQuantity() <= 0) {
    throw new ApplicationException(ErrorCode.INVALID_QUANTITY_IS_ZERO);
  }
  ```
  
# BaseRepository
- **예외 처리가 되어있는** findById를 BaseRepository를 상속받아서 사용할 수 있음
- 각자 레포지토리 만들 시 JpaRepository 대신 BaseRepository 클래스를 상속받아서 사용 권장
- 예시
  ```
  public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> { ...  }
  ```

# BaseEntity
- **Auditing 적용 되어있는** 날짜 필드를 BaseEntity에서 상속받아서 사용할 수 있음
- createdAt과 modifiedAt 필드가 필요한 엔티티를 만들 시, BaseEntity 클래스를 상속받아서 사용할 것을 권장
- 상속 받을 시, 엔티티 필드에 createdAt과 modifiedAt을 굳이 작성하지 않아도 됨
- 예시
  ```
  @Entity
  @Table(name = "user_coupon")
  public class UserCoupon extends BaseEntity { ... }
  ```

# 이슈관리
![image](https://github.com/user-attachments/assets/5ad21c67-edba-48a9-99a5-20a251bde732)
- 깃허브 이슈 탭에서 이슈관리 가능(깃허브 좌상단)
- 지금 개발중인것을 제목으로 작성
- 이슈 생성 시, 깃허브에서 자동으로 이슈번호 발급
  ![image](https://github.com/user-attachments/assets/fbdc65f8-50f8-4518-82b2-f9575d138c14)
- commit시 아래 예시와 같이 commit 권장 
  ```
  git commit -m "feat(#이슈 번호): 개발 내용"
  ```

# 로그인한 유저 정보 받기
- jwt, spring security를 통해 현재 로그인한 유저의 객체를 받아올 수 있다
- @AuthenticationPrincipal AuthUser user
- 예시
  ```
  @PostMapping("/{couponId}/subscriptions")
  public ResponseEntity<ApiResponse<Void>> subscribeCoupon(@AuthenticationPrincipal AuthUser user) { ... }
  ```
