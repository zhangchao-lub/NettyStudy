package netty.s12;

import netty.s01.Server;

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
    //单例化
    private static class ServerFrameHolder {
        private static final ServerFrame INSTANCE=new ServerFrame();
    }
    public static ServerFrame getInstance() {
        return ServerFrameHolder.INSTANCE;
    }

    Button btnStart=new Button("start");
    TextArea taLeft=new TextArea();
    TextArea taRight=new TextArea();
    ChatServer server=new ChatServer();

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

    }

    public static void main(String[] args){
        ServerFrame.getInstance().setVisible(true);
        ServerFrame.getInstance().server.serverStart();
    }
    public void updateServerMsg(String string){
        this.taLeft.setText(taLeft.getText()+System.getProperty("line.separator")+string);
    }
    public void updateClientMsg(String string){
        this.taRight.setText(taRight.getText()+System.getProperty("line.separator")+string);
    }
}
