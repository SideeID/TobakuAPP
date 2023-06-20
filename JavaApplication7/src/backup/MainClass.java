package backup;

import TOBAKU.main.*;

/**
 *
 * @author Dimas Fajar
 */
public class MainClass {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                login LoginFrame = new login();
                LoginFrame.setVisible(true);
            }
        });
    }
}
