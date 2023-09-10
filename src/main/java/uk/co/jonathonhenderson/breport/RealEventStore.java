package uk.co.jonathonhenderson.breport;

import java.util.ArrayList;
import java.util.List;

public class RealEventStore implements EventStore {
  private final List<Event> events;

  public RealEventStore() {
    events = new ArrayList<>();
  }

  @Override
  public void add(Event event) {
    events.add(event);
  }

  @Override
  public List<Event> all() {
    return List.copyOf(events);
  }
}

