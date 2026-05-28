package com.beachape;

import static java.util.Objects.requireNonNull;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
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
  private final ReentrantLock lock = new ReentrantLock();
  private final Condition recordAdded = lock.newCondition();

  public InMemoryLogHandler(Predicate<LogRecord> predicate) {
    this.predicate = requireNonNull(predicate);
  }

  final List<ExtLogRecord> records = new CopyOnWriteArrayList<>();

  @Override
  public void publish(LogRecord record) {
    if (predicate.test(record)) {
      records.add((ExtLogRecord) record);
      lock.lock();
      try {
        recordAdded.signalAll();
      } finally {
        lock.unlock();
      }
    }
  }

  /**
   * Wait until at least one record matching the predicate has been published, or the timeout
   * elapses. Access logs are emitted asynchronously after the response has been written, so tests
   * that read the captured records straight after the HTTP call would otherwise race the logger.
   */
  public boolean awaitRecord(Duration timeout) throws InterruptedException {
    long deadlineNanos = System.nanoTime() + timeout.toNanos();
    lock.lock();
    try {
      while (records.isEmpty()) {
        long remaining = deadlineNanos - System.nanoTime();
        if (remaining <= 0) {
          return false;
        }
        recordAdded.awaitNanos(remaining);
      }
      return true;
    } finally {
      lock.unlock();
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
