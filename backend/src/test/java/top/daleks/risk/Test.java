/*
 * Copyright 2023-present Daleks Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.daleks.risk;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Test {
    @org.junit.Test
    public void map() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(2, "b");
        map.put(1, "a");
        map.put(3, "c");
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }
    }

    @org.junit.Test
    public void classTest() {
        System.out.println(Number.class.isAssignableFrom(Double.class));
        System.out.println(Double.class.isAssignableFrom(Number.class));
    }

    @org.junit.Test
    public void time() throws ParseException, InterruptedException {

        LocalDateTime begin = LocalDateTime.of(2021, 4, 20, 0, 0);
        System.out.println(begin);

        LocalDateTime now = LocalDateTime.of(2021, 4, 20, 8, 4);


        ChronoUnit windowUnit = ChronoUnit.DAYS;
        int timeWindow = 1;
        ChronoUnit spanUnit = ChronoUnit.DAYS;
        int spanWindow = 1;

        long start = spanUnit.between(begin, now) / spanWindow * spanWindow;

        long totalMinutes = windowUnit.getDuration().toMinutes() * timeWindow;
        long spanMinutes = spanUnit.getDuration().toMinutes() * spanWindow;
        long d = 0;
        while (d < totalMinutes) {
            System.out.println(start);
            d += spanMinutes;
            start -= spanWindow;
        }

    }

    @org.junit.Test
    public void name() {
        System.out.println(ChronoUnit.YEARS.getDuration().toMinutes());
        System.out.println(ChronoUnit.YEARS.getDuration().getSeconds() * 5);
    }

    @org.junit.Test
    public void run() {
        ExecutorService service = Executors.newFixedThreadPool(1);
        Future<?> future = service.submit(() -> {
            System.out.println("running");
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("done");
        });
        Object result = null;
        try {
            result = future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(false);
        }
        System.out.println(result);

        try {
            Thread.sleep(12000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void split() {
        String query = "a: 1 and b:2or or c: \"3 and \" not d:1";
        String[] strings = query.split("and|or|not");
        for (String s : strings) {
            System.out.println(s);
        }
    }
}
