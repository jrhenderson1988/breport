package uk.co.jonathonhenderson.breport;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.platform.engine.ConfigurationParameters;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

// https://github.com/junit-team/junit5/blob/cb3445283961678a18ada304d7a33aba4cc6736d/junit-platform-reporting/src/main/java/org/junit/platform/reporting/open/xml/OpenTestReportGeneratingListener.java
// https://junit.org/junit5/docs/current/user-guide/#running-tests-listeners
// https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom
public class BDDReportListener implements TestExecutionListener {

  private static final String ENABLED_PROPERTY = "path.to.enabled.property";
  private static final boolean DEFAULT_ENABLED = true;

  private EventStore eventStore = EventStore.noOp();

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    var config = testPlan.getConfigurationParameters();
    if (isEnabled(config)) {
      eventStore = EventStore.real();
      System.out.println(">>>> starting tests...");
    }
  }

  @Override
  public void executionSkipped(TestIdentifier identifier, String reason) {
    addMethodEvent(identifier, Event.Outcome.SKIPPED);
  }

  @Override
  public void executionFinished(TestIdentifier identifier, TestExecutionResult result) {
    addMethodEvent(identifier, getOutcome(result));
  }

  private void addMethodEvent(TestIdentifier identifier, Event.Outcome outcome) {
    identifier
        .getSource()
        .filter(source -> source instanceof MethodSource)
        .ifPresent(source -> eventStore.add(Event.of(identifier, (MethodSource) source, outcome)));
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    var output =
        eventStore.all().stream().map(this::getOutputForEvent).collect(Collectors.joining("\n\n"));
    System.out.println(output);
  }

  private String getOutputForEvent(Event event) {
    var title = "%s [%s]".formatted(event.identifier().getDisplayName(), event.outcome());
    var givenWhenThen = getGivenWhenThenLines(event.methodSource());

    return givenWhenThen.isEmpty()
        ? title
        : title + givenWhenThen.stream().collect(Collectors.joining("\n  ", "\n  ", ""));
  }

  private List<String> getGivenWhenThenLines(MethodSource methodSource) {
    var method = methodSource.getJavaMethod();

    var bdd = method.getAnnotation(BDD.class);
    var given = method.getAnnotation(Given.class);
    var when = method.getAnnotation(When.class);
    var then = method.getAnnotation(Then.class);

    var givenText = prefix("Given: ", supply(given, Given::value), supply(bdd, BDD::given));
    var whenText = prefix("When: ", supply(when, When::value), supply(bdd, BDD::when));
    var thenText = prefix("Then: ", supply(then, Then::value), supply(bdd, BDD::then));

    return Stream.of(givenText, whenText, thenText).filter(Objects::nonNull).toList();
  }

  private String prefix(String prefix, String a, String b) {
    if (a != null && !a.isBlank()) {
      return prefix + a;
    } else if (b != null && !b.isBlank()) {
      return prefix + b;
    } else {
      return null;
    }
  }

  private <T, R> R supply(T item, Function<T, R> fn) {
    if (item == null) {
      return null;
    }
    return fn.apply(item);
  }

  private boolean isEnabled(ConfigurationParameters config) {
    return config.getBoolean(ENABLED_PROPERTY).orElse(DEFAULT_ENABLED);
  }

  private boolean isTestMethod(TestIdentifier identifier) {
    return identifier.getSource().map(s -> s instanceof MethodSource).orElse(false);
  }

  private Event.Outcome getOutcome(TestExecutionResult testExecutionResult) {
    return switch (testExecutionResult.getStatus()) {
      case SUCCESSFUL -> Event.Outcome.SUCCESSFUL;
      case ABORTED -> Event.Outcome.ABORTED;
      case FAILED -> Event.Outcome.FAILED;
    };
  }
}
