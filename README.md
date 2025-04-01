# ApiResponse 사용법
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
      “message”: “회원가입 완료.”,
      “data”: null
    }
  ```
  
## ApiResponse.success(responseData, "Response 메시지");
```
@PostMapping("/signin")
  public ResponseEntity<ApiResponse<AccessTokenResponse>> signin(@Valid @RequestBody SigninRequest request) {
    AccessTokenResponse response = authService.signin(request);
    return ResponseEntity.ok(ApiResponse.success(response,"로그인에 성공했습니다."));
  }
```
- 결과
  ```
{
  “status”: “OK”,
  “code”: 200,
  “message”: “로그인 완료”,
  “data”: {token}
}
  ```
