package com.beachape;

import static java.util.Objects.requireNonNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

/**
 * In-memory log handler that collects log records based on a predicate.
 */
public class InMemoryLogHandler extends ExtHandler {

  private final Predicate<LogRecord> predicate;

  public InMemoryLogHandler(Predicate<LogRecord> predicate) {
    this.predicate = requireNonNull(predicate);
  }

  final List<ExtLogRecord> records = new CopyOnWriteArrayList<>();

  @Override
  public void publish(LogRecord record) {
    if (predicate.test(record)) {
      records.add((ExtLogRecord) record);
    }
  }

  @Override
  public void flush() {}

  @Override
  public Level getLevel() {
    return Level.FINE;
  }

  @Override
  public void close() {
    this.records.clear();
  }

  public List<String> getMessages() {
    return records.stream()
        .map(record -> MessageFormat.format(record.getMessage(), record.getParameters()))
        .toList();
  }

  public List<ExtLogRecord> getRecords() {
    return records;
  }

  public void clear() {
    this.records.clear();
  }
}
