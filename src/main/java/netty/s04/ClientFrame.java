package netty.s04;

import lombok.SneakyThrows;

import java.awt.*;
import java.awt.event.*;

/**
 * @author czhang@mindpointeye.com
 * @version 1.0
 * @Date 2020/12/10 18:59
 * @descrption
 */
public class ClientFrame extends Frame {
    TextArea ta = new TextArea();
    TextField tf = new TextField();

    Client c=null;
    public ClientFrame() {
        this.setSize(600, 400);
        this.setLocation(1200, 600);
        this.add(ta, BorderLayout.CENTER);
        this.add(tf, BorderLayout.SOUTH);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        tf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //把字符串发送到服务器
                c.send(tf.getText());
                ta.setText(ta.getText() + tf.getText());
                tf.setText("");
            }
        });
        this.setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        c=new Client();
        c.connect();
    }


    public static void main(String[] args) {
        new ClientFrame();
    }
}
