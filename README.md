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
