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

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.context.junit4.SpringRunner;
import top.daleks.risk.utils.JsonUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MongoTest {

    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void names() {
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        System.out.println(collectionNames);
    }

    @Test
    public void save() {
        User user = new User();
        user.setId("id");
        user.setName("spring test");
        user.setAge(18);
        mongoTemplate.save(user);

        List<User> users = mongoTemplate.findAll(User.class);
        System.out.println(JsonUtils.string(users));
    }

    @Setter
    @Getter
    @Document(collection = "test")
    public static class User implements Serializable {
        @Id
        private String id;
        private String name;
        private int age;
    }
}
