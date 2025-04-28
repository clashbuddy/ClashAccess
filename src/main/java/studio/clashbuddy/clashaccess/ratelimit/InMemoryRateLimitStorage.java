package studio.clashbuddy.clashaccess.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRateLimitStorage implements RateLimitStorage{

    private InMemoryRateLimitStorage(){}

    private final static InMemoryRateLimitStorage INSTANCE = new InMemoryRateLimitStorage();

    public static InMemoryRateLimitStorage instance(){
        return INSTANCE;
    }



    private static class Counter {
        private int count;
        private long expireAt;

        Counter(int count, long expireAt) {
            this.count = count;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, Counter> storage = new ConcurrentHashMap<>();

    @Override
    public int incrementAndGet(String key, long windowMillis) {
        long now = System.currentTimeMillis();

        Counter counter = storage.get(key);

        if (counter == null || counter.expireAt < now) {
            // Counter missing or expired → Reset
            Counter newCounter = new Counter(1, now + windowMillis);
            storage.put(key, newCounter);
            return 1;
        }

        // Increment existing counter
        counter.count++;
        return counter.count;
    }

}
