package unach.sindicato.api.util;

import org.junit.jupiter.api.Test;

public interface PersistenceTest {
    @Test void testSave();
    @Test void testFindById();
    @Test void testFindAll();
    @Test void testUpdate();
}
