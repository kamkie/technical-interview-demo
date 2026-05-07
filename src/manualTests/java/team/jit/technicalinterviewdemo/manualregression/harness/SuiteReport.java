package team.jit.technicalinterviewdemo.manualregression.harness;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Mutable per-suite report; serialized through {@link ReportWriter} once the suite completes. */
public final class SuiteReport {

    private final String suiteName;
    private final Instant startedAt;
    private Instant finishedAt;
    private SuiteResult result = SuiteResult.NOT_RUN;
    private String reason;
    private final Map<String, TestRecord> tests = new LinkedHashMap<>();
    private final List<RequestRecord> requests = new ArrayList<>();
    private final Map<String, String> generatedIdentifiers = new LinkedHashMap<>();
    private final List<String> notes = new ArrayList<>();
    private final List<String> leftoverIdentifiers = new ArrayList<>();

    public SuiteReport(String suiteName, Instant startedAt) {
        this.suiteName = suiteName;
        this.startedAt = startedAt;
    }

    public String suiteName() {
        return suiteName;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Optional<Instant> finishedAt() {
        return Optional.ofNullable(finishedAt);
    }

    public SuiteResult result() {
        return result;
    }

    public Optional<String> reason() {
        return Optional.ofNullable(reason);
    }

    public List<TestRecord> tests() {
        return List.copyOf(tests.values());
    }

    public List<RequestRecord> requests() {
        return List.copyOf(requests);
    }

    public Map<String, String> generatedIdentifiers() {
        return Map.copyOf(generatedIdentifiers);
    }

    public List<String> notes() {
        return List.copyOf(notes);
    }

    public List<String> leftoverIdentifiers() {
        return List.copyOf(leftoverIdentifiers);
    }

    public void recordRequest(RequestRecord record) {
        requests.add(record);
    }

    public void recordTestStarted(String uniqueId, String displayName, Instant startedAt) {
        tests.putIfAbsent(
                uniqueId, new TestRecord(uniqueId, displayName, startedAt, Optional.empty(), "RUNNING", Optional.empty()));
    }

    public void recordTestFinished(
            String uniqueId, String displayName, Instant finishedAt, String outcome, Optional<String> reason) {
        TestRecord started = tests.get(uniqueId);
        Instant startedAt = started == null ? finishedAt : started.startedAt();
        tests.put(uniqueId, new TestRecord(uniqueId, displayName, startedAt, Optional.of(finishedAt), outcome, reason));
    }

    public void recordIdentifier(String key, String value) {
        generatedIdentifiers.put(key, value);
    }

    public void addNote(String note) {
        notes.add(note);
    }

    public void addLeftover(String identifier) {
        leftoverIdentifiers.add(identifier);
    }

    public void markPassed(Instant finishedAt) {
        this.finishedAt = finishedAt;
        this.result = notes.isEmpty() ? SuiteResult.PASSED : SuiteResult.PASSED_WITH_NOTE;
    }

    public void markBlocked(Instant finishedAt, String reason) {
        this.finishedAt = finishedAt;
        this.result = SuiteResult.BLOCKED;
        this.reason = reason;
    }

    public void markFailed(Instant finishedAt, String reason) {
        this.finishedAt = finishedAt;
        this.result = SuiteResult.FAILED;
        this.reason = reason;
    }
}
