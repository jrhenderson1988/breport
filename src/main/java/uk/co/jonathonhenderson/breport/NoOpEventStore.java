package uk.co.jonathonhenderson.breport;

import java.util.List;

public class NoOpEventStore implements EventStore {
  @Override
  public void add(Event event) {}

  @Override
  public List<Event> all() {
    return List.of();
  }
}

