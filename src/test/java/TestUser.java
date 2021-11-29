public class TestUser {
    public int curHp;

    synchronized public void subcripHp(int delta) {
        if(delta <= 0) {
            return;
        }

        this.curHp = this.curHp - delta;
    }

}
