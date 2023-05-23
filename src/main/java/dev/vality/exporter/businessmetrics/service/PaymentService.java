package dev.vality.exporter.businessmetrics.service;

import dev.vality.exporter.businessmetrics.metrics.PaymentsGaugeMetrics;
import dev.vality.exporter.businessmetrics.model.Metric;
import dev.vality.exporter.businessmetrics.repository.PaymentRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${interval.time}")
    private String intervalTime;

    private final PaymentRepository paymentRepository;
    private final List<PaymentsGaugeMetrics> paymentsGaugeMetrics;

    public void registerMetrics(MeterRegistry meterRegistry) {
        var actualPayments = paymentRepository.getPaymentDtoList(intervalTime);
        log.debug("Actual payments by {} seconds interval = {}", intervalTime, actualPayments);
        var gauges = paymentsGaugeMetrics.stream()
                .flatMap(handler -> handler.aggregate(actualPayments).entrySet().stream())
                .map(e -> Gauge.builder(getNameWithSuffix(e.getKey()), e, Map.Entry::getValue)
                        .description(getDescription(e.getKey()))
                        .baseUnit(Metric.PAYMENTS_COUNT.getUnit())
                        .tags(e.getKey()))
                .toList();
        for (var gauge : gauges) {
            gauge.register(meterRegistry);
        }
    }

    private String getDescription(Tags tags) {
        return Metric.PAYMENTS_COUNT.getDescription() + " with using [" + collectTags(tags, ", ") + "] tags";
    }

    private String getNameWithSuffix(Tags tags) {
        return Metric.PAYMENTS_COUNT.getName() + "_" + collectTags(tags, "_");
    }

    private String collectTags(Tags tags, String delimiter) {
        return tags.stream()
                .sorted(Comparator.comparing(Tag::getKey))
                .map(Tag::getKey)
                .collect(Collectors.joining(delimiter));
    }
}