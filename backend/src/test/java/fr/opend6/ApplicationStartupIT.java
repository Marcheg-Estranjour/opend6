package fr.opend6;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ApplicationStartupIT {

    @Autowired
    JdbcClient jdbc;

    @Test
    void applies_flyway_migrations_at_startup() {
        List<String> versions = jdbc.sql("""
                select version from flyway_schema_history
                where success order by installed_rank""")
            .query(String.class)
            .list();

        assertThat(versions).containsExactly("1", "2");
    }

    @Test
    void loads_the_three_genres() {
        Long genres = jdbc.sql("select count(*) from genre").query(Long.class).single();

        assertThat(genres).isEqualTo(3L);
    }
}