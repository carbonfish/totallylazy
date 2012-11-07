package com.googlecode.totallylazy;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.googlecode.totallylazy.Sequences.repeat;
import static com.googlecode.totallylazy.matchers.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

public class CountLatchTest {
    @Test
    public void supportsIncrementingAndDecrementing() throws Exception {
        final AtomicInteger number = new AtomicInteger();
        final CountLatch latch = new CountLatch();
        latch.countUp();
        assertThat("1", latch.count(), is(1));

        assertThat("2", latch.await(1, TimeUnit.NANOSECONDS), is(false));

        repeat(number).take(5).mapConcurrently(latch.monitor(new Function1<AtomicInteger, Integer>() {
            @Override
            public Integer call(AtomicInteger atomicInteger) throws Exception {
                return atomicInteger.incrementAndGet();
            }
        }));

        latch.countDown();
        latch.await();

        assertThat("4", latch.count(), is(0));
        assertThat("5", number.get(), is(5));
    }
}
