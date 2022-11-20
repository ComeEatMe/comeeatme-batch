# comeeatme-batch
컴잇미 배치 프로그램

## Feature

- LOCALDATA(지방행정 인허가 데이터개방)의 csv 파일을 다운로드해서 DB의 음식점 데이터를 초기화
- LOCALDATA(지방행정 인허가 데이터개방)의 API를 통해 매일 데이터 업데이트
- 제공받는 데이터에서 부족한 값들은 다른 Open API를 이용해서 보충

## Setting

> 어플리케이션 실행을 위해 필요한 환경 변수

- `DATABASE_URL` : 데이터 베이스 URL
- `DATABASE_USERNAME` : 데이터 베이스 아이디
- `DATABASE_PASSWORD` : 데이터 베이스 비밀번호

## Data

- [LOCAL DATA(지행정 인허가 데이터 개방)](https://www.localdata.go.kr/main.do)
  - 음식점 데이터 csv
  - 음식점 데이터 변경분 API
- [공간정보 오픈플랫폼 오픈API](https://www.vworld.kr/dev/v4api.do)
  - Geocoder API : 주소를 통한 좌표 구하기
  - LOCAL DATA의 좌표가 오차가 크므로 이 API를 통해서 구한 좌표 이용
- [주소기반산업지원서비스](https://business.juso.go.kr/addrlink/main.do?cPath=99MM)
  - 일반 주소 <-> 도로명 주소 변환
  - 일반 주소 or 도로명 주소가 제공되지 않을 경우 사용 
