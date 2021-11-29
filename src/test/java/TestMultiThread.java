public class TestMultiThread {
    public static void main(String[] args) {
        for(int i = 0; i < 2000; i++){
            System.out.println("当前 " + i + "次");
            new TestMultiThread().test1();
        }
    }

    private void test1(){
        TestUser newUser = new TestUser();
        newUser.curHp = 100;

        Thread[] threadArray = new Thread[2];
        for(int i = 0; i < threadArray.length; i++){
            threadArray[i] = new Thread(()->{
                newUser.subcripHp(10);
            });
        }
        threadArray[0].start();
        threadArray[1].start();

        try {
            threadArray[0].join();
            threadArray[1].join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(newUser.curHp != 80) {
            throw new RuntimeException("当前血量错误, curHp = " + newUser.curHp);
        }

        System.out.println("当前血量正确, curHp = " + newUser.curHp);
    }
}
