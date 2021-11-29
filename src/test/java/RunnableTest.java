public class RunnableTest {
    public static void main(String[] args) {
        Runnable runnable = ()->{
            System.out.println("----------");
        };
        runnable.run();
    }
}
