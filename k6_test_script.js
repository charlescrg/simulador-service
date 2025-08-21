import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '30s', target: 10 }, 
        { duration: '1m', target: 10 },  
        { duration: '30s', target: 0 },  
    ],
};

export default function () {
    let res = http.get('http://localhost:8080/metrics');
    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });
    sleep(1);
}
