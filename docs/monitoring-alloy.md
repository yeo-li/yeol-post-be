# Grafana Alloy Monitoring

이 애플리케이션은 Spring Boot Actuator의 Prometheus 엔드포인트와 애플리케이션 로그 파일을 Grafana Alloy가 수집하는 구조를 사용한다.

## Application endpoints

- Metrics: `GET /actuator/prometheus`
- Health: `GET /actuator/health`
- Liveness: `GET /actuator/health/liveness`
- Readiness: `GET /actuator/health/readiness`

## Application logs

애플리케이션 로그는 JSON 문자열로 남긴다.

- `info`: 실제 상태 전이가 발생한 경우
- `warn`: 검증 실패, 권한 없음, 리소스 없음 같은 예상 가능한 클라이언트 오류
- `error`: 예상하지 못한 서버 오류 또는 5xx 오류

모든 `/api/**` 요청에는 `x-request-id`가 적용된다. 요청 헤더가 없거나 `A-Z`, `a-z`, `0-9`, `.`, `_`, `-`로 이루어진 1-128자 형식이 아니면 서버가 UUID를 생성하고 응답 헤더에도 같은 값을 내려준다.

예시:

```json
{
  "message": "게시물 좋아요가 반영되었습니다.",
  "event": "POST_LIKED",
  "reason": "APPLIED",
  "requestId": "req-123",
  "postId": 10,
  "userId": 42,
  "postOwnerUserId": 7
}
```

서비스 메서드의 단순 시작/완료 로그나 내부 조회/검증/정제 로그는 남기지 않는다. 분석에 필요한 로그는 사용자가 요청한 행위와 실제로 반영된 도메인 결과 기준으로 남긴다.

예를 들어 게시물 좋아요 요청은 API 요청 로그와 도메인 반영 로그가 같은 `requestId`로 묶인다.

```json
{
  "message": "게시물 좋아요가 반영되었습니다.",
  "event": "POST_LIKED",
  "reason": "APPLIED",
  "requestId": "req-123",
  "postId": 10,
  "userId": 42,
  "postOwnerUserId": 7
}
```

장애 분석 시에는 먼저 `requestId`로 한 요청의 전체 로그를 조회하고, 이후 `event`와 `reason`으로 실패한 행위 또는 마지막으로 반영된 상태를 확인한다.

운영 프로필에서는 관리 포트와 로그 파일 경로가 고정되어 있다.

```yaml
management:
  server:
    port: 8081

logging:
  file:
    name: /var/log/yeol-post/application.log
```

외부 인터넷에는 `/actuator/**`를 공개하지 말고, Alloy가 접근할 수 있는 내부 네트워크에서만 열어야 한다.

## Alloy example

```hcl
prometheus.scrape "yeol_post" {
  targets = [
    {
      __address__ = "localhost:8081",
      app         = "yeol-post",
      env         = "prod",
    },
  ]

  metrics_path    = "/actuator/prometheus"
  scrape_interval = "15s"

  forward_to = [prometheus.remote_write.default.receiver]
}

prometheus.remote_write "default" {
  endpoint {
    url = env("PROMETHEUS_REMOTE_WRITE_URL")
  }
}

local.file_match "yeol_post_logs" {
  path_targets = [
    {
      __path__ = "/var/log/yeol-post/application.log",
      app      = "yeol-post",
      env      = "prod",
    },
  ]
}

loki.source.file "yeol_post" {
  targets    = local.file_match.yeol_post_logs.targets
  forward_to = [loki.write.default.receiver]
}

loki.write "default" {
  endpoint {
    url = env("LOKI_WRITE_URL")
  }
}
```

컨테이너 환경에서 stdout 로그를 수집한다면 `loki.source.file` 대신 컨테이너 런타임 로그 경로나 Kubernetes discovery 설정을 사용한다.
