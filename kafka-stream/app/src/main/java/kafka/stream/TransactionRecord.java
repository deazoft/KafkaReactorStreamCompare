package kafka.stream;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;

@Entity
@Table(name = "Transactions")
public class TransactionRecord {
    @Id
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "handler")
    private String handler;

    @Column(name = "index")
    private Integer index;

    @Column(name = "Key")
    private String key;

    @Column(name = "thread")
    private String thread;

    @Column(name = "off_set")
    private Long offset;

    @Column(name = "occurred_on",columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime OccurredOn;

    public TransactionRecord setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public TransactionRecord setHandler(String handler) {
        this.handler = handler;
        return this;
    }

    public TransactionRecord setIndex(Integer index) {
        this.index = index;
        return this;
    }

    public TransactionRecord setThread(String thread) {
        this.thread = thread;
        return this;
    }

    public TransactionRecord setKey(String key) {
        this.key = key;
        return this;
    }

    public TransactionRecord setOffset(Long offset) {
        this.offset = offset;
        return this;
    }

    public TransactionRecord setOccurredOn(ZonedDateTime occurredOn) {
        OccurredOn = occurredOn;
        return this;
    }

    public static TransactionRecord getTransaction(String eventId, String handler, Integer index, String key, Long offset, String thread)
    {
       return new TransactionRecord().setEventId(eventId).setHandler(handler).setIndex(index).setKey(key).setOffset(offset)
               .setOccurredOn(ZonedDateTime.now())
               .setThread(thread);
    }
}
