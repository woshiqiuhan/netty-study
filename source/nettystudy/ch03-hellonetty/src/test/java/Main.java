import io.netty.util.NettyRuntime;
import org.junit.Test;

public class Main {
    @Test
    public void test() {
        System.out.println(NettyRuntime.availableProcessors());
    }
}
