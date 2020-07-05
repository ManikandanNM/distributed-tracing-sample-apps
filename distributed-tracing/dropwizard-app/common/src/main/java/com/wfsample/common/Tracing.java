package com.wfsample.common;

import com.uber.jaeger.Configuration;
import com.uber.jaeger.Configuration.ReporterConfiguration;
import com.uber.jaeger.Configuration.SamplerConfiguration;
import com.uber.jaeger.samplers.ConstSampler;

import com.wavefront.sdk.direct.ingestion.WavefrontDirectIngestionClient;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;
import io.opentracing.propagation.TextMapExtractAdapter;
import io.opentracing.tag.Tags;
import okhttp3.Request;

import javax.ws.rs.core.MultivaluedMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.wavefront.opentracing.WavefrontTracer;
import com.wavefront.opentracing.reporting.Reporter;
import com.wavefront.opentracing.reporting.WavefrontSpanReporter;
import com.wavefront.sdk.common.WavefrontSender;
import com.wavefront.sdk.common.application.ApplicationTags;
import com.wavefront.sdk.proxy.WavefrontProxyClient;

public final class Tracing {
  private final static Random RAND = new Random(System.currentTimeMillis());
  private final static String[] ENV_TAGS = new String[] {"staging", "production", "development"};
  private final static String[] LOCATION_TAGS = new String[] {"palo-alto", "san-francisco",
      "new-york"};
  private final static String[] TENANT_TAGS = new String[] {"wavefront", "vmware"};

  private Tracing() {
  }

  public static Tracer init(String service, HashMap<String, String> inputParams) throws IOException {
    WavefrontDirectIngestionClient.Builder wfDirectIngestionClient = new WavefrontDirectIngestionClient.
            Builder(inputParams.get("wavefrontUrl"), inputParams.get("wavefrontToken"))
            .batchSize(Integer.parseInt(inputParams.get("batchSize")))
            .maxQueueSize(Integer.parseInt(inputParams.get("queueSize")))
            .flushIntervalSeconds(Integer.parseInt(inputParams.get("flushInterval")));

    WavefrontSender wavefrontSender = wfDirectIngestionClient.build();
    /**
     * TODO: You need to assign your microservices application a name.
     * For this hackathon, please prepend your name (example: "john") to the beachshirts application,
     * for example: applicationName = "john-beachshirts"
     */
    ApplicationTags applicationTags = new ApplicationTags.Builder(inputParams.get("applicationName"),
            service).build();
    Reporter wfSpanReporter = new WavefrontSpanReporter.Builder().
            withSource("wavefront-tracing-example").build(wavefrontSender);
    WavefrontTracer.Builder wfTracerBuilder = new WavefrontTracer.
            Builder(wfSpanReporter, applicationTags);
    return wfTracerBuilder.build();
  }

  public static Tracer init(String service, String proxyHost, String flushInterval, String applicationName) throws IOException {
    WavefrontProxyClient.Builder wfProxyClientBuilder = new WavefrontProxyClient
            .Builder(proxyHost)
            .metricsPort(2878)
            .tracingPort(30000)
            .distributionPort(40000)
            .flushIntervalSeconds(Integer.parseInt(flushInterval));

    WavefrontSender wavefrontSender = wfProxyClientBuilder.build();
    /**
     * TODO: You need to assign your microservices application a name.
     * For this hackathon, please prepend your name (example: "john") to the beachshirts application,
     * for example: applicationName = "john-beachshirts"
     */
    ApplicationTags applicationTags = new ApplicationTags.Builder(applicationName,
            service).build();
    Reporter wfSpanReporter = new WavefrontSpanReporter.Builder().
            withSource("wavefront-tracing-example").build(wavefrontSender);
    WavefrontTracer.Builder wfTracerBuilder = new WavefrontTracer.
            Builder(wfSpanReporter, applicationTags);
    return wfTracerBuilder.build();
  }

  public static Scope startServerSpan(Tracer tracer, javax.ws.rs.core.HttpHeaders httpHeaders, String operationName) {
    // format the headers for extraction
    MultivaluedMap<String, String> rawHeaders = httpHeaders.getRequestHeaders();
    final HashMap<String, String> headers = new HashMap<>();
    for (String key : rawHeaders.keySet()) {
      headers.put(key, rawHeaders.get(key).get(0));
    }

    Tracer.SpanBuilder spanBuilder;
    try {
      SpanContext parentSpanCtx = tracer.extract(Format.Builtin.HTTP_HEADERS, new TextMapExtractAdapter(headers));
      if (parentSpanCtx == null) {
        spanBuilder = tracer.buildSpan(operationName);
      } else {
        spanBuilder = tracer.buildSpan(operationName).asChildOf(parentSpanCtx);
      }
    } catch (IllegalArgumentException e) {
      spanBuilder = tracer.buildSpan(operationName);
    }
    return appendCustomTags(spanBuilder.withTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_SERVER)).
        startActive(true);
  }

  public static Tracer.SpanBuilder appendCustomTags(Tracer.SpanBuilder spanBuilder) {
    return spanBuilder.withTag("env", ENV_TAGS[RAND.nextInt(ENV_TAGS.length)]).
        withTag("location", LOCATION_TAGS[RAND.nextInt(LOCATION_TAGS.length)]).
        withTag("tenant", TENANT_TAGS[RAND.nextInt(TENANT_TAGS.length)]);
  }

  public static TextMap requestBuilderCarrier(final Request.Builder builder) {
    return new TextMap() {
      @Override
      public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
      }

      @Override
      public void put(String key, String value) {
        builder.addHeader(key, value);
      }
    };
  }
}
