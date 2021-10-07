package com.sem.coub.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sem.coub.web.rest.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ATest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(A.class);
        A a1 = new A();
        a1.setId(UUID.randomUUID());
        A a2 = new A();
        a2.setId(a1.getId());
        assertThat(a1).isEqualTo(a2);
        a2.setId(UUID.randomUUID());
        assertThat(a1).isNotEqualTo(a2);
        a1.setId(null);
        assertThat(a1).isNotEqualTo(a2);
    }
}
