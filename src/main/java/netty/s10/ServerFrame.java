package netty.s10;

import javafx.scene.layout.Pane;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/11 17:13
 * @descrption
 */
public class ServerFrame extends Frame {

    Button btnStart=new Button("start");
    TextArea taLeft=new TextArea();
    TextArea taRight=new TextArea();

    public ServerFrame(){
        this.setSize(1600,600);
        this.setLocation(300,30);
        this.add(btnStart,BorderLayout.NORTH);
        Panel p=new Panel(new GridLayout(1,2));
        p.add(taLeft);
        p.add(taRight);
        this.add(p);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        this.setVisible(true);
    }

    public static void main(String[] args){
        new ServerFrame();
    }
}
