package september.it;

import org.junit.jupiter.api.Test;

public class DisplayEnvTest {

  @Test
  void printDisplayEnv() {
    System.out.println("--- Test Output --- ");

    System.out.println("DISPLAY=" + System.getenv("DISPLAY"));

    System.out.println("WAYLAND_DISPLAY=" + System.getenv("WAYLAND_DISPLAY"));

    System.out.println("-------------------");
  }
}
