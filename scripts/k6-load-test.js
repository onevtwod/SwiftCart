import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '30s',
};

export default function () {
    const res = http.post('http://localhost:8080/orders', JSON.stringify({
        userId: 'user-1',
        currency: 'USD',
        items: [{ sku: 'SKU-123', qty: 1, unitPrice: 100 }],
    }), { headers: { 'Content-Type': 'application/json', 'Idempotency-Key': Math.random().toString() } });
    check(res, { 'status is 201 or 200': (r) => r.status === 201 || r.status === 200 });
    sleep(1);
}

