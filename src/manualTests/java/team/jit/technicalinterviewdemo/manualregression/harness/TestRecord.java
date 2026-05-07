package team.jit.technicalinterviewdemo.manualregression.harness;

import java.time.Instant;
import java.util.Optional;

/** One observed JUnit test outcome for the generated manual-regression checklist. */
public record TestRecord(
        String uniqueId,
        String displayName,
        Instant startedAt,
        Optional<Instant> finishedAt,
        String outcome,
        Optional<String> reason) {}
