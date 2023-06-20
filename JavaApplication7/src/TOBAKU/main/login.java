package TOBAKU.main;


import TOBAKU.koneksi.koneksi;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dimas Fajar
 */
public class login extends javax.swing.JFrame {

    Connection conn = koneksi.getKoneksi();

    /**
     * Creates new form login
     */
    public login() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        username = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton_login = new javax.swing.JButton();
        jButton_Lp = new javax.swing.JButton();
        Password = new javax.swing.JPasswordField();
        jCheckBox = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(212, 231, 252));
        jPanel2.setPreferredSize(new java.awt.Dimension(641, 717));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Username/Email");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 180, -1, -1));

        username.setBackground(new java.awt.Color(34, 73, 87));
        username.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        username.setForeground(new java.awt.Color(255, 255, 255));
        username.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(username, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 210, 370, 41));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Password");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 290, -1, -1));

        jButton_login.setBackground(new java.awt.Color(255, 255, 255));
        jButton_login.setForeground(new java.awt.Color(255, 255, 255));
        jButton_login.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/Login btn.png"))); // NOI18N
        jButton_login.setBorder(null);
        jButton_login.setBorderPainted(false);
        jButton_login.setContentAreaFilled(false);
        jButton_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_loginActionPerformed(evt);
            }
        });
        jPanel2.add(jButton_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 430, 310, 60));

        jButton_Lp.setBackground(new java.awt.Color(240, 243, 252));
        jButton_Lp.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton_Lp.setText("Lupa Password?");
        jButton_Lp.setBorder(new javax.swing.border.MatteBorder(null));
        jButton_Lp.setBorderPainted(false);
        jButton_Lp.setContentAreaFilled(false);
        jButton_Lp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_LpActionPerformed(evt);
            }
        });
        jPanel2.add(jButton_Lp, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 400, 143, -1));

        Password.setBackground(new java.awt.Color(34, 73, 87));
        Password.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Password.setForeground(new java.awt.Color(255, 255, 255));
        Password.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel2.add(Password, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 320, 370, 41));

        jCheckBox.setBackground(new java.awt.Color(212, 231, 252));
        jCheckBox.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jCheckBox.setText("Tampilkan");
        jCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });
        jPanel2.add(jCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 370, -1, -1));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/exit.png"))); // NOI18N
        jButton1.setToolTipText("");
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 10, 33, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("LOGIN");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 90, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/Vectors.png"))); // NOI18N
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 520, -1, -1));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1090, 630));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_loginActionPerformed
        String query = "SELECT * FROM user WHERE userName = '" + username.getText() + "' AND password = '" + String.valueOf(Password.getText()) + "'";

        ResultSet as = null;
        try {
            as = conn.createStatement().executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (as.next()) {
                int level = as.getInt("level");
                if (level == 2) { // Owner
                    Dashboard mn = new Dashboard();
                    mn.setVisible(true);
                    dispose();
                } else if (level == 1) { // Kasir
                    Kasir kasir = new Kasir();
                    kasir.setVisible(true);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Masukkan username dan password dengan benar");
                username.setText("");
                Password.setText("");
            }
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            as.close();
        } catch (SQLException ex) {
            Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton_loginActionPerformed

    private void jButton_LpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_LpActionPerformed
        // TODO add your handling code here:
        lupaPs Lp = new lupaPs();
        Lp.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton_LpActionPerformed

    private void jCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxActionPerformed
        // TODO add your handling code here:
        if (jCheckBox.isSelected()) {
            Password.setEchoChar((char) 0);
        } else {
            Password.setEchoChar('*');
        }
    }//GEN-LAST:event_jCheckBoxActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField Password;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_Lp;
    private javax.swing.JButton jButton_login;
    private javax.swing.JCheckBox jCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
