package tests.projets.agiles.exemple1;

import org.junit.jupiter.api.Test;
import tests.projets.agiles.exemple1.calculs.Calcul;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculTest {
    @Test
    void testMin() {
        Calcul cl = new Calcul();
        int resultat = cl.min(3, 5);
        assertEquals(3, resultat);
    }
}
