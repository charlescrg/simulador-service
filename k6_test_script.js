import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://simulador-service:8080';
const TOKEN = __ENV.TOKEN;

const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${TOKEN}`,
};

export default function () {
    // Criar simulação
    const criarPayload = JSON.stringify({
        valorDesejado: 1000.00,
        prazo: 2
    });

    let resCriar = http.post(`${BASE_URL}/api/v1/simulacoes`, criarPayload, { headers });
    let okCriar = check(resCriar, {
        'Criar Simulação: status 200': (r) => r.status === 200
    });
    if (!okCriar) {
        console.error(`❌ Criar Simulação falhou: status=${resCriar.status}, body=${resCriar.body}`);
    }

    // Listar simulações
    let resListar = http.get(`${BASE_URL}/api/v1/simulacoes/listar`, { headers });
    let okListar = check(resListar, {
        'Listar Simulações: status 200': (r) => r.status === 200
    });
    if (!okListar) {
        console.error(`❌ Listar Simulações falhou: status=${resListar.status}, body=${resListar.body}`);
    }

    // Valores por produto/dia
    let resValores = http.get(`${BASE_URL}/api/v1/simulacoes/valores-por-produto-dia`, { headers });
    let okValores = check(resValores, {
        'Valores por Produto/Dia: status 200': (r) => r.status === 200
    });
    if (!okValores) {
        console.error(`❌ Valores por Produto/Dia falhou: status=${resValores.status}, body=${resValores.body}`);
    }

    // Métricas/telemetria
    let resMetrics = http.get(`${BASE_URL}/api/v1/telemetria/listar`, { headers });
    let okMetrics = check(resMetrics, {
        'Métricas: status 200': (r) => r.status === 200
    });
    if (!okMetrics) {
        console.error(`❌ Métricas falhou: status=${resMetrics.status}, body=${resMetrics.body}`);
    }

    sleep(1);
}
