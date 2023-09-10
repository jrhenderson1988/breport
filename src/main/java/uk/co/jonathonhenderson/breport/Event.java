package uk.co.jonathonhenderson.breport;

import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;

public record Event(TestIdentifier identifier, MethodSource methodSource, Outcome outcome) {
  public static Event of(TestIdentifier identifier, MethodSource methodSource, Outcome outcome) {
    return new Event(identifier, methodSource, outcome);
  }

  public enum Outcome {
    SUCCESSFUL,
    ABORTED,
    FAILED,
    SKIPPED
  }
}

