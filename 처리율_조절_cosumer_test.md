# Throttling Consumer Test with K6


### Test 시나리오
- 동시 1000건의 결제 요청을 호출
- 요청을 받은 api서버는 결제 정보를 저장 후 `command-request-order` 메세지 발행
- message를 받는 comsumer 서버는 결제 정보 유효성 검사 후 `event-comfirm-order` 메세지 발행
- 결제 승인 메세지를 받은 comsumer 서버는 2초 대기 후 완료 처리
- 처리 중간에 permits를 64 -> 8 -> 32 로 변경
- 변경 이후 처리량이 변하는지 확인
````javaScript
import http from 'k6/http'
import {check} from 'k6'

// ...

export default async () => {
    // 동시에 1000건의 요청
    const responses = await Promise.all( 
        [...Array(1000).keys()].map(idx => postAsync(url, body))
    )

    check(responses, { 'all status was 200': (respList) => respList.filter(r => r.status !== 200).length === 0 })
}

// 비동기 요청 함수 
const postAsync = async (url, body) => {
    return new Promise((resolve) => {
        const resp = http.post(url, JSON.stringify(body), {
            headers: { 'Content-Type': 'application/json' },
          })

        resolve(resp)
    })
}
````

permits: 8 (동시에 8개 처리)
<img src="https://github.com/ykh8383633/asynchronousPaymentSystem/assets/86603009/1b31fdc6-8f47-4330-975b-0edb54dafede" width="400px"/>

