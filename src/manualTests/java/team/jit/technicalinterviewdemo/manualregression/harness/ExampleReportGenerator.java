package team.jit.technicalinterviewdemo.manualregression.harness;

import java.nio.file.Path;
import java.nio.file.Paths;

/** Generates deterministic synthetic manual-regression report artifacts without a running app. */
public final class ExampleReportGenerator {

    private ExampleReportGenerator() {}

    public static void main(String[] args) {
        Path root = args.length == 0 ? Paths.get("temp") : Paths.get(args[0]);
        Path output = ReportWriter.writeExample(root);
        System.out.println("[manual-regression] Example report written to " + output.toAbsolutePath());
    }
}
