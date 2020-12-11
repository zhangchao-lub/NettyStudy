package netty.s11;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/10 18:59
 * @descrption
 */
public class ClientFrame extends Frame {
    //单例化
    private static class ClientFrameHolder {
        private final static ClientFrame INSTANCE = new ClientFrame();
    }
    public static ClientFrame getInstance() {
        return ClientFrameHolder.INSTANCE;
    }

    TextArea ta = new TextArea();
    TextField tf = new TextField();

    Client c=null;
    private ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(1200, 600);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                c.closeConnect();
                System.exit(0);
            }
        });

        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //把字符串发送到服务器
                c.send(tf.getText());
                tf.setText("");
            }
        });
        this.setVisible(true);

    }

    public static void main(String[] args) {
        ClientFrame frame= ClientFrame.getInstance();
        frame.setVisible(true);
        frame.connectToServer();
    }

    private void connectToServer() {
        c=new Client();
        c.connect();
    }

    public void updateText(String msgAccepted) {
        //换行符 System.getProperty("line.separator")
        ta.setText(ta.getText() +System.getProperty("line.separator")+ msgAccepted);

    }

}
