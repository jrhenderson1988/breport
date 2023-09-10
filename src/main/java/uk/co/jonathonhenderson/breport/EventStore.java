package uk.co.jonathonhenderson.breport;

import java.util.List;

public interface EventStore {
  void add(Event event);

  List<Event> all();

  static EventStore noOp() {
    return new NoOpEventStore();
  }

  static EventStore real() {
    return new RealEventStore();
  }
}

