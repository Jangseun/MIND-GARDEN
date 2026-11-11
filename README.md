# 마음정원

책·뉴스·대화·일상에서 발견한 깨달음을 기록하고, 기록이 쌓일수록 꽃밭과 정원이 자라는 Android 앱입니다. Java, XML View, SQLiteOpenHelper를 사용하며 인터넷 연결 없이 동작합니다.

힐링 수집 게임의 **빈 공간이 활동에 따라 자라고 감상하는 성장 구조**에서 영감을 받았으며, 특정 게임의 그래픽이나 화면을 복제하지 않습니다. 마음정원의 중심은 깨달음 기록, 가치 키워드별 꽃밭, 자신의 과거 기록을 돌아보는 경험입니다.

## 환경 및 실행

- JDK 21
- Android SDK: `compileSdk 36`, `minSdk 23`
- Gradle Wrapper 8.14.3
- 패키지: `com.mindgarden.app`

Windows PowerShell 또는 터미널에서 프로젝트 루트로 이동한 뒤:

```powershell
.\gradlew.bat assembleDebug
```

APK: `app\build\outputs\apk\debug\app-debug.apk`

USB 디버깅이 켜진 Android 기기 또는 실행 중인 에뮬레이터에는 다음과 같이 설치합니다.

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## 구현 기능

- `책 / 뉴스 / 대화 / 일상 / 기타` 출처 Spinner와 `열정 / 배려 / 꾸준함 / 책임감` 키워드 Spinner
- 여러 줄 EditText 입력 검증, 새 기록 저장, 기록 수정 및 확인 후 삭제
- 최신순 ListView, 항목 선택 후 편집, 목록 즉시 갱신
- SQLite에 기록 영구 저장. 자동 증가 ID와 생성/수정 시각 관리
- 현재 키워드별 기록 수를 기준으로 Lv.0–4 화단 표시
- 전체 기록 3/5/10/15/25개에서 이름표/벤치/울타리/정원등/분수 해금. 한 번 열린 구조물은 기록을 지워도 유지
- 오늘 첫 기록 퀘스트
- 실제 이미지가 없어도 코드로 그린 임시 정원으로 실행 가능
- 시작 화면은 꽃과 해금 구조물이 없는 빈 화단이며, 기록 수에 따라 화단과 구조물이 나타남

돌아보기 기록, 40/60개 구조물, 주간 퀘스트 및 애니메이션은 이번 MVP 범위에서 제외했습니다.

| 가치 키워드 | 정원 영역 | 성장 단계 |
|---|---|---|
| 열정 | 오른쪽 뒤 · 노란 해바라기 | 0: 빈 화단, 1–2: Lv.1, 3–5: Lv.2, 6–9: Lv.3, 10+: Lv.4 |
| 배려 | 왼쪽 앞 · 분홍 코스모스 | 같은 단계 기준 |
| 꾸준함 | 오른쪽 앞 · 클로버와 작은 흰 꽃 | 같은 단계 기준 |
| 책임감 | 왼쪽 뒤 · 보라 라벤더 | 같은 단계 기준 |

누적 구조물은 3개 나무 이름표, 5개 벤치, 10개 울타리, 15개 정원등, 25개 분수입니다. 장미 아치(40개)와 비밀 온실(60개)은 후속 확장 항목입니다. 해금된 구조물은 메모 삭제 후에도 영구 유지됩니다.

## 수업 코드 5개에서 계승한 내용

| 수업 코드 | 배운 요소 | 실제 앱 구현 |
|---|---|---|
| `MainActivity.java` | ViewBinding, 버튼 이벤트, Spinner 및 ArrayAdapter, ListView 항목 선택, `refresh()`/갱신 패턴 | `MemoEditorActivity.java`의 `ActivityMemoEditorBinding`·두 Spinner·등록/수정/초기화 이벤트, `MemoListActivity.java`의 `ActivityMemoListBinding`·ArrayAdapter·항목 선택·`refresh()` |
| `MenuDAO.java` | `SQLiteDatabase`, `ContentValues`, insert/update/delete, Cursor 조회 | `app/src/main/java/com/mindgarden/app/data/MemoDAO.java` |
| `MenuDBHelper.java` | `SQLiteOpenHelper`, DB/테이블 상수, `onCreate()`/`onUpgrade()` | `app/src/main/java/com/mindgarden/app/data/MemoDBHelper.java` |
| `MenuDTO.java` | 데이터 필드, 생성자, getter/setter, `toString()` | `app/src/main/java/com/mindgarden/app/data/MemoDTO.java` |
| `activity_main.xml` | ConstraintLayout, EditText, Spinner, Button, ListView | `activity_memo_editor.xml`의 EditText/Spinner/Button과 `activity_memo_list.xml`의 ListView/Button |

참고한 원본은 사용자가 전달한 `maeum_garden_class_reference` 폴더의 다섯 파일입니다. 원본의 수기 ID 입력은 자동 증가 DB ID로 바꾸고, 수정/삭제 결과는 영향 행 수가 0보다 큰지 확인합니다. Cursor는 try-with-resources로 닫으며, 원본의 무조건 테이블 삭제 방식은 사용하지 않습니다.

## 이미지 에셋 교체

총 22개 에셋을 `app/src/main/res/drawable-nodpi/`에 지정한 이름 그대로 넣고 다시 빌드하면 앱이 이름으로 리소스를 찾아 사용합니다. 꽃밭 이미지를 제공하면 해당 키워드의 현재 Lv.1–4 이미지로 교체되고, 구조물 이미지는 누적 해금 조건을 만족할 때 정원에 추가됩니다. 이미지가 아직 없으면 각 레이어는 자체 임시 그림으로 표시되므로 에셋 없이 빌드할 수 있습니다. 이 디렉터리는 에셋을 넣을 위치를 표시하기 위해 비워 두었습니다.

| 용도 | 파일명 |
|---|---|
| 기본 배경 (1) | `garden_base.webp` |
| 열정 화단 | `garden_passion_lv1.png`–`garden_passion_lv4.png` |
| 배려 화단 | `garden_care_lv1.png`–`garden_care_lv4.png` |
| 꾸준함 화단 | `garden_consistency_lv1.png`–`garden_consistency_lv4.png` |
| 책임감 화단 | `garden_responsibility_lv1.png`–`garden_responsibility_lv4.png` |
| 해금 구조물 (5) | `garden_sign.png`, `garden_bench.png`, `garden_fence.png`, `garden_lamp.png`, `garden_fountain.png` |

정원 논리 캔버스는 360×400이며 배경은 같은 비율을 권장합니다. `GardenSceneView`는 그림 영역 안에서 비율을 유지해 중앙에 배치하고, x/y/너비/높이를 각각 논리 캔버스에 고정합니다.

| 레이어 | x | y | 너비 | 높이 |
|---|---:|---:|---:|---:|
| 책임감 화단 | 18 | 152 | 120 | 78 |
| 열정 화단 | 220 | 152 | 120 | 78 |
| 배려 화단 | 12 | 258 | 145 | 105 |
| 꾸준함 화단 | 202 | 258 | 145 | 105 |
| 이름표 | 15 | 322 | 65 | 55 |
| 벤치 | 52 | 212 | 86 | 70 |
| 울타리 | 40 | 120 | 280 | 65 |
| 정원등 | 270 | 205 | 50 | 100 |
| 분수 | 143 | 160 | 74 | 90 |

렌더 순서는 기본 배경 → 울타리 → 뒤쪽 화단 → 분수/벤치/정원등 → 앞쪽 화단 → 이름표입니다. 이 순서로 원거리부터 전경까지 레이어를 겹칩니다. 구조물의 위치와 크기는 캔버스에 고정되어 있고 사용자 임의 배치는 없습니다. 오브젝트는 투명 배경으로 제작하고 이미지 안에 글자를 넣지 않습니다. 앱은 이름표에 `마음정원`을 직접 그립니다. 장미 아치와 온실은 1차 구현 대상이 아니며, 투명 PNG/WebP만 사용해 주세요. Android 리소스 파일명은 소문자와 밑줄을 사용합니다.

## 주요 파일

```text
app/src/main/java/com/mindgarden/app/
  MainActivity.java
  MemoEditorActivity.java
  MemoListActivity.java
  data/MemoDTO.java
  data/MemoDAO.java
  data/MemoDBHelper.java
  garden/GardenRules.java
  garden/GardenSceneView.java
  garden/UnlockRepository.java
app/src/main/res/layout/
  activity_garden.xml
  activity_memo_editor.xml
  activity_memo_list.xml
  item_memo.xml
```

## 수동 확인 순서

1. 처음 실행해 `0개의 작은 씨앗` 및 빈 정원을 확인합니다.
2. `씨앗 심기`에서 출처·키워드·내용을 선택하고 저장해 목록/홈에 반영되는지 확인합니다.
3. 저장한 기록을 목록에서 눌러 내용을 수정하고, 다시 열어 삭제합니다.
4. 같은 키워드로 1, 3, 6, 10개를 기록해 화단 레벨 변화를 확인합니다.
5. 전체 3, 5, 10, 15, 25개 도달 시 구조물 해금 및 재실행 후 유지 여부를 확인합니다.
6. 앱을 닫았다 다시 열어 기록이 남아 있는지 확인합니다.
