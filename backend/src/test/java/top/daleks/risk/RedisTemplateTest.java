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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static top.daleks.risk.dal.RedisConfiguration.REDIS_CHANNEL_TOPIC;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTemplateTest {


    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void increment() {
        String key = "key";
        redisTemplate.opsForValue().increment(key, 1L);
        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println("\n\n\n\n");
    }

    @Test
    public void setGet() {
        String key = "key";
        redisTemplate.opsForValue().set(key, "value");
        redisTemplate.expire(key, 10, TimeUnit.MINUTES);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println("\n\n\n\n");
    }

    @Test
    public void reloadEngineConfig() throws InterruptedException {
        stringRedisTemplate.convertAndSend(REDIS_CHANNEL_TOPIC.getTopic(), "data message");

        Thread.sleep(3000L);
    }
}
