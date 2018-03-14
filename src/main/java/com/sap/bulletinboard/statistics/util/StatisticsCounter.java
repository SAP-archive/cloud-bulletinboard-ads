package com.sap.bulletinboard.statistics.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.sap.bulletinboard.statistics.models.Statistics;

public class StatisticsCounter {
    private final Map<Long, AtomicLong> map = new HashMap<>();

	public Statistics increment(Long id) {
        AtomicLong value = getOrDefault(id);
        long viewCount = value.incrementAndGet();
        return new Statistics(id, viewCount);
    }

    private AtomicLong getOrDefault(long id) {
        AtomicLong value = map.getOrDefault(id, new AtomicLong());
        map.put(id, value);
        return value;
    }

	public Statistics get(long id) {
        long viewCount = getOrDefault(id).get();
        return new Statistics(id, viewCount);
    }
}
