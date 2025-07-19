---
name: ✨ 기능 추가 요청 (Feature)
about: 새로운 기능을 개발할 때 사용하는 템플릿
title: "feat: [기능 이름]"
labels: [ "feature", "feat" ]
---

## 📌 기능 이름
게시물 작성자 조회 API

## 🧾 기능 설명
사용자가 작성자를 조회하면 작성자 키워드를 포함한 작성자의 모든 게시물을 반환합니다.


## 🛠️ 구현 방식
- [ ] 1. [postService] 작성자 카테고리와 같은 모든 게시물을 조회한다.
- [ ] 2. [postService] posts를 List<PostResponse>로 변환한다.
- [ ] 3. [postController] PostController에서 ResponseEntity로 반환한다.

## ⛔️ 제약 조건
- [ ] query string이 blank면 빈 데이터를 반환해야한다.

## ⚠️ 유의 사항
1. SQL 인젝션에 유의할 것
2. 사용자가 한 번에 많은 요청을 보내지 않도록 할 것
3. 예외상황이 발생하면 적절한 오류 코드와 메세지를 반환할 것
