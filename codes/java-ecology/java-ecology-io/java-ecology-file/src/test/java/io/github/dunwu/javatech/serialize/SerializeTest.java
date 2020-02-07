package io.github.dunwu.javatech.serialize;

import io.github.dunwu.javatech.bean.Person;
import io.github.dunwu.tool.util.RandomUtil;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:forbreak@163.com">Zhang Peng</a>
 * @since 2019-11-22
 */
public class SerializeTest {

    public static final int BATCH_SIZE = 100000;

    @Test
    public void testJdkSerialize() throws IOException, ClassNotFoundException {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Person oldPerson = new Person(RandomUtil.randomChineseName(), RandomUtil.randomInt(0, 100));
            byte[] bytes = JdkSerializeDemo.serializ(oldPerson);
            assertThat(bytes).isNotEmpty();
            Person newPerson = JdkSerializeDemo.derialize(bytes, Person.class);
            assertThat(newPerson).isNotNull();
        }
        long end = System.currentTimeMillis();
        System.out.printf("耗时：%s", (end - begin));
    }

    @Test
    public void testFst() throws IOException {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < BATCH_SIZE; i++) {
            Person oldPerson = new Person(RandomUtil.randomChineseName(), RandomUtil.randomInt(0, 100));
            byte[] bytes = FstDemo.serializ(oldPerson);
            assertThat(bytes).isNotEmpty();
            Person newPerson = FstDemo.derialize(bytes, Person.class);
            assertThat(newPerson).isNotNull();
        }
        long end = System.currentTimeMillis();
        System.out.printf("耗时：%s", (end - begin));
    }

}
