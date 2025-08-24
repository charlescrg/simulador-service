package gov.caixa.simuladorservice.config;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;

public class TracingConfig {

    public static void init() {
        JaegerGrpcSpanExporter jaegerExporter = JaegerGrpcSpanExporter.builder()
                .setEndpoint("localhost:14250") // gRPC exporter (sem http://)
                .build();

        // Nome do serviço que vai aparecer no Jaeger UI
        Resource serviceNameResource = Resource.getDefault()
                .merge(Resource.create(
                        Attributes.of(ResourceAttributes.SERVICE_NAME, "simulador-service")
                ));

        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter).build())
                .setResource(serviceNameResource)
                .build();

        OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    }

    public static Tracer getTracer() {
        return GlobalOpenTelemetry.getTracer("simulador-service");
    }
}
