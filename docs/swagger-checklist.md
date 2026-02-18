# Swagger 작성 체크리스트

1. 태그 정리
- 컨트롤러마다 `@Tag(name, description)`를 선언해 도메인 단위로 구분한다.

2. 엔드포인트 설명
- 모든 엔드포인트에 `@Operation(summary, description)`를 작성한다.
- `summary`는 짧게, `description`은 동작/조건 중심으로 구체적으로 작성한다.

3. 경로/쿼리/헤더 파라미터 문서화
- `@PathVariable`, `@RequestParam`, `@RequestHeader`에 `@Parameter(description, example)`를 작성한다.
- 필요한 경우 `required` 여부를 명확히 표시한다.

4. 요청 바디 예시
- `@RequestBody`에 실제 요청 구조와 동일한 JSON 예시를 넣는다.
- 필드명(`snake_case`, `camelCase`)이 실제 API와 일치하는지 확인한다.

5. 응답 문서화
- `@ApiResponses`로 상태코드별 응답을 정의한다.
- 성공 응답은 최소 1개 이상의 `ExampleObject`를 제공한다.
- 필요 시 실패 응답도 대표 케이스를 추가한다.

6. DTO 스키마 보강
- 요청/응답 DTO 필드에 `@Schema(description, example)`를 작성한다.
- 타입/포맷(예: `email`, `date-time`), nullable, 길이 제한 등의 제약을 반영한다.

7. 인증/권한 명시
- 인증이 필요한 API는 설명에 인증/권한 조건을 명시한다.
- 보안 스키마를 사용하는 경우 OpenAPI 설정과 일치하도록 관리한다.

8. 일관성 검증
- Swagger 문서와 실제 코드(요청 필드, 응답 필드, 타입, 경로)가 100% 일치하는지 점검한다.
- 의미가 헷갈리는 Boolean 필드는 설명으로 true/false 의미를 분명히 적는다.

9. 검증 루프
- `./gradlew compileJava`와 `./gradlew test`를 실행해 문서화 변경이 코드에 영향을 주지 않는지 검증한다.
- Swagger UI에서 예시 출력/파라미터 설명이 정상 노출되는지 확인한다.

10. 유지보수 원칙
- API/DTO 변경 시 Swagger 문서도 같은 PR에서 함께 수정한다.
- 문서화 누락이 발생하지 않도록 코드 리뷰 체크포인트에 포함한다.
