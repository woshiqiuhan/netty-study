import org.junit.Test;

public class Main {
    @Test
    public void test() {
        System.out.println(Math.ceil(2.32));
    }


    public String test1() {
        String a = "dsadsad";
        try {
            int b = 1 / 0;
            a = "123213";
        } catch (Exception e) {
            System.out.println("分母不为零");
        }
        System.out.println("2312312");
        return a;
    }
}
