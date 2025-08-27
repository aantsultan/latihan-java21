package id.latihan.java21.spring.ai;

import org.junit.jupiter.api.Test;

import java.util.StringTokenizer;

class StringTokenizerTest {

    @Test
    void tokenizer() {
        String x = "nama saya budi";
        StringTokenizer tokenizer = new StringTokenizer(x);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            System.out.println(token);
        }
    }

}
