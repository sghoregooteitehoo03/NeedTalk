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
![스크린샷](https://github.com/sghoregooteitehoo03/NikeDrawAlarm/blob/master/image/screenshot.png)

## 아키텍쳐 및 라이브러리
- 아키텍처
   - MVVM 패턴: (View - ViewModel - Model)
   - [App Architecture 패턴](https://developer.android.com/topic/architecture/intro): (UI Layer - Domain Layer - Data Layer)
     
- Jetpack
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.
  - WorkManager: 안정적인 백그라운드 작업을 처리하도록 도와줍니다.
  - Paging3: 로컬 데이터베이스나 네트워크에서 가져온 데이터를 페이징하여 데이터를 처리합니다.
  - Navigation: 화면 구성 및 화면전환에 관련된 다양한 기능을 제공합니다.
  - Browser: 앱 내에서 외부 브라우저를 호출하거나 웹뷰를 제공합니다.
  - Room: SQL 기능을 이용하여 데이터베이스를 이용합니다.
  - Datastore: 키-값 유형의 데이터를 읽고 저장하는 데이터 저장소입니다.
  - Compose: 기존의 XML레이아웃을 이용하지 않고, Kotlin 코드를 통해 UI 화면을 제작합니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입을 통해 보일러플레이트 코드를 줄여줍니다.
    
- [Retrofit](https://github.com/square/retrofit): Android 및 Java를 위한 HTTP 클라이언트입니다.

- [Coil](https://github.com/coil-kt/coil), [Picasso](https://github.com/square/picasso): 네트워크로부터 이미지를 로드합니다.

- Custom Views
  - [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar): Jetapck Compose용 Collapsing Toolbar를 제공합니다.
  - [compose-shimmer](https://github.com/valentinilk/compose-shimmer): Jetpack Compose에 shimmer 효과를 제공합니다.
