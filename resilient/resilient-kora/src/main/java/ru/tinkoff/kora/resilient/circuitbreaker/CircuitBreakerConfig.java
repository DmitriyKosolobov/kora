package ru.tinkoff.kora.resilient.circuitbreaker;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import ru.tinkoff.kora.config.common.annotation.ConfigValueExtractor;

import java.time.Duration;
import java.util.Map;

@ConfigValueExtractor
public interface CircuitBreakerConfig {
    String DEFAULT = "default";

    default Map<String, NamedConfig> circuitbreaker() {
        return Map.of();
    }

    default NamedConfig getNamedConfig(@Nonnull String name) {
        final NamedConfig defaultConfig = circuitbreaker().get(DEFAULT);
        final NamedConfig namedConfig = circuitbreaker().getOrDefault(name, defaultConfig);
        if (namedConfig == null)
            throw new IllegalStateException("CircuitBreaker no configuration is provided, but either '" + name + "' or '" + DEFAULT + "' config is required");

        final NamedConfig mergedConfig = merge(namedConfig, defaultConfig);
        if (mergedConfig.failureRateThreshold() == null)
            throw new IllegalStateException("CircuitBreaker 'failureRateThreshold' is not configured in either '" + name + "' or '" + DEFAULT + "' config");
        if (mergedConfig.waitDurationInOpenState() == null)
            throw new IllegalStateException("CircuitBreaker 'waitDurationInOpenState' is not configured in either '" + name + "' or '" + DEFAULT + "' config");
        if (mergedConfig.permittedCallsInHalfOpenState() == null)
            throw new IllegalStateException("CircuitBreaker 'permittedCallsInHalfOpenState' is not configured in either '" + name + "' or '" + DEFAULT + "' config");
        if (mergedConfig.slidingWindowSize() == null)
            throw new IllegalStateException("CircuitBreaker 'slidingWindowSize' is not configured in either '" + name + "' or '" + DEFAULT + "' config");
        if (mergedConfig.minimumRequiredCalls() == null)
            throw new IllegalStateException("CircuitBreaker 'minimumRequiredCalls' is not configured in either '" + name + "' or '" + DEFAULT + "' config");

        if (mergedConfig.minimumRequiredCalls() < 1)
            throw new IllegalArgumentException("CircuitBreaker '" + name + "' minimumRequiredCalls can't be negative, but was " + mergedConfig.minimumRequiredCalls());
        if (mergedConfig.slidingWindowSize() < 1)
            throw new IllegalArgumentException("CircuitBreaker '" + name + "' slidingWindowSize can't be negative, but was " + mergedConfig.slidingWindowSize());
        if (mergedConfig.minimumRequiredCalls() > mergedConfig.slidingWindowSize())
            throw new IllegalArgumentException("CircuitBreaker '" + name + "' minimumRequiredCalls was " + mergedConfig.minimumRequiredCalls()
                                               + " can't be more than slidingWindowSize which is " + mergedConfig.slidingWindowSize());
        if (mergedConfig.permittedCallsInHalfOpenState() > mergedConfig.minimumRequiredCalls())
            throw new IllegalArgumentException("CircuitBreaker '" + name + "' minimumRequiredCalls was " + mergedConfig.minimumRequiredCalls()
                + " can't be more than permittedCallsInHalfOpenState which is " + mergedConfig.permittedCallsInHalfOpenState());
        if (mergedConfig.failureRateThreshold() > 100 || mergedConfig.failureRateThreshold() < 1)
            throw new IllegalArgumentException("CircuitBreaker '" + name + "' failureRateThreshold is percentage and must be in range 1 to 100, but was "
                                               + mergedConfig.failureRateThreshold());

        return mergedConfig;
    }

    private static NamedConfig merge(NamedConfig namedConfig, NamedConfig defaultConfig) {
        if (defaultConfig == null) {
            return namedConfig;
        }

        return new $CircuitBreakerConfig_NamedConfig_ConfigValueExtractor.NamedConfig_Impl(
            namedConfig.failureRateThreshold() == null ? defaultConfig.failureRateThreshold() : namedConfig.failureRateThreshold(),
            namedConfig.waitDurationInOpenState() == null ? defaultConfig.waitDurationInOpenState() : namedConfig.waitDurationInOpenState(),
            namedConfig.permittedCallsInHalfOpenState() == null ? defaultConfig.permittedCallsInHalfOpenState() : namedConfig.permittedCallsInHalfOpenState(),
            namedConfig.slidingWindowSize() == null ? defaultConfig.slidingWindowSize() : namedConfig.slidingWindowSize(),
            namedConfig.minimumRequiredCalls() == null ? defaultConfig.minimumRequiredCalls() : namedConfig.minimumRequiredCalls(),
            namedConfig.failurePredicateName() == null ? defaultConfig.failurePredicateName() : namedConfig.failurePredicateName()
        );
    }

    /**
     * You can use <a href="https://resilience4j.readme.io/docs/circuitbreaker">Resilient4j documentation</a> as a description of how CircuitBreaker works and how similar properties are configution its parts
     * <p>
     * {@link #failureRateThreshold} Configures the failure rate threshold in percentage. If the failure rate is equal to or greater than the threshold, the CircuitBreaker transitions to open and starts short-circuiting calls. The threshold must be greater than 0 and not greater than 100.<br>
     * {@link #waitDurationInOpenState} Configures an interval function with a fixed wait duration which controls how long the CircuitBreaker should stay open, before it switches to half open.<br>
     * {@link #permittedCallsInHalfOpenState} Configures the number of permitted calls that must succeed when the CircuitBreaker is half open.<br>
     * {@link #slidingWindowSize} Configures the sliding window which is used to record the outcome of calls when the CircuitBreaker is closed.<br>
     * {@link #minimumRequiredCalls} Configures the minimum number of calls which are required (per sliding window period) before the CircuitBreaker can calculate the error rate.<br>
     * {@link #failurePredicateName} {@link CircuitBreakerPredicate#name()} default is {@link KoraCircuitBreakerPredicate}<br>
     */
    @ConfigValueExtractor
    interface NamedConfig {
        @Nullable
        Integer failureRateThreshold();

        @Nullable
        Duration waitDurationInOpenState();

        @Nullable
        Integer permittedCallsInHalfOpenState();

        @Nullable
        Long slidingWindowSize();

        @Nullable
        Long minimumRequiredCalls();

        default String failurePredicateName() {
            return KoraCircuitBreakerPredicate.class.getCanonicalName();
        }
    }
}
