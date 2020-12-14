package netty.s13;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/14 17:38
 * @descrption
 */
public class TankMsg {
    public int x,y;

    public TankMsg(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "TankMsg{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
