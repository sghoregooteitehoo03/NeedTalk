## 대화가 필요해
![그래픽 이미지](https://github.com/sghoregooteitehoo03/NeedTalk/blob/main/image/%E1%84%80%E1%85%B3%E1%84%85%E1%85%A2%E1%84%91%E1%85%B5%E1%86%A8%E1%84%8B%E1%85%B5%E1%84%86%E1%85%B5%E1%84%8C%E1%85%B5.png)

### 가족, 친구, 애인 모두 휴대폰은 잠시 내려놓고, 직접 만든 대화주제들을 통해 서로간의 대화에만 집중 해보세요!

  
대화 중 휴대폰만 바라보는 사람들 때문에 기분 나쁘셨던 경험이 있을까요??  
휴대폰에서 잠시 벗어나 소중한 사람들과 함께하는 순간을 더욱 특별하게 만들어드립니다.  
가족, 친구, 애인과 함께 휴대폰은 잠시 내려놓고 
소중한 이들과 오롯이 대화에만 집중해보세요!

다양한 카테고리의 기본 대화주제들이 기다리고 있어요.
직접 제작한 대화주제들을 이용하여 더욱 즐거운 대화를 해보아요.

## 다운로드
<a href='https://play.google.com/store/apps/details?id=com.sghore.needtalk'><img alt='다운로드하기 Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/ko_badge_web_generic.png' height="80"/></a>

## 아키텍쳐
![아키텍쳐](https://github.com/sghoregooteitehoo03/NeedTalk/blob/main/image/architecture.png)
- ### UI Layer ###
  사용자에게 화면을 그리는 역할을 담당합니다 ViewModel을 통해 전달받은 UI State를 렌더링합니다.  
  사용자 입력은 Event 형태로 받아 ViewModel에 적절한 함수를 호출합니다.
- ### Domain Layer ###
  앱의 핵심 비즈니스 로직을 담당하는 영역입니다. UseCase를 통해 데이터를 UI에서 사용 가능한 형태로 정제하며, 공통 Result 클래스를 정의하여 성공/실패 상태를 명확히 처리하도록 구현하였습니다.
- ### Data Layer ###
  Repository 패턴을 적용하여 Firebase 및 Room DB에 접근합니다. 외부 데이터 소스로부터 데이터를 가져온 뒤 상위 계층에 전달합니다.

## 외부 라이브러리
- Jetpack
  - Compose: 기존 XML 레이아웃을 선언형 UI로 리팩토링하여 직관적이고 유연한 화면을 구현했습니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입(DI)을 통해 객체 간의 결합도를 낮추고 유연한 아키텍처를 설계했습니다.
  - Navigation: 단일 액티비티 구조에서 Compose 기반의 화면 구성 및 원활한 전환을 담당합니다.
  - Paging3: 다른 유저들이 만든 수많은 '대화 주제' 리스트와 과거 대화 기록을 무한 스크롤로 부드럽게 페이징 처리합니다.
  - Room: 사용자의 개인적인 대화 기록과 하이라이트 내역을 오프라인 상태에서도 확인할 수 있도록 로컬에 저장합니다.
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.

- Firebase
  - Firestore: 유저들이 직접 생성하고 공유하는 '대화 주제' 데이터를 클라우드에 실시간으로 저장하고 동기화합니다.

- [Retrofit](https://github.com/square/retrofit): 외부 API와의 안정적인 HTTP 통신을 수행합니다.

- [Nearby](https://developers.google.com/nearby?hl=ko): 블루투스, Wi-Fi, IP, 등을 통해 근처 기기와 통신을 담당합니다.

- [ffmpeg-kti](https://github.com/arthenica/ffmpeg-kit): 녹음된 대화 오디오 파일에서 특정 구간을 잘라내어 '하이라이트'를 추출하고 편집하는 핵심 미디어 처리를 담당합니다.

- kotlinx.serialization: JSON 인코딩 디코딩을 도와줍니다.

- Custom Views
  - [bottomsheetdialog-compose](https://github.com/workspace/bottomsheetdialog-compose): Jetpack Compose용 Bottom Sheet Dialog를 제공합니다.

## 기능
- **대화 기록**  
기록된 대화내역을 통해 다른 이들과 얼마나 대화에 집중하였는지,  
대화 중 놓쳤던 부분을 확인하거나 다시 그 순간을 즐길 수 있습니다.  
소중한 순간을 기록해보세요.  
  
- **하이라이트 제작**  
녹음 된 대화내용 중 하이라이트를 제작하여 다른 이들과 공유해보세요!

- **다양한 대화주제**  
본인만의 대화 주제를 제작할 수 있을 뿐만 아니라 다른 유저들이 만든 다양한 대화주제가 제공되고 있습니다.  
어떤 엉뚱한 질문이나 진지한 이야기든 상관없습니다.  
대화의 세계를 더욱 풍부하게 만들어보세요.

- **타이머/스톱워치 기능**  
대화에 참여한 사용자 모두가 휴대폰을 내려놓으면 실행되는 타이머로  
설정한 시간 동안 서로에게 집중할 수 있게 도와드립니다.

## 스크린샷
![스크린샷](https://github.com/sghoregooteitehoo03/NeedTalk/blob/main/image/screenshot.png)
