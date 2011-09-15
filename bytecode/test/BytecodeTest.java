import models.Entidad;
import org.junit.Assert;
import org.junit.Test;
import play.test.UnitTest;

public class BytecodeTest extends UnitTest {

	@Test
	public void bytecodetest() {
		Entidad e = new Entidad();
		e.nombre = "Play!";
		Assert.assertEquals("Play!", e.nombre);
	}

}
