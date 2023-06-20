/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TOBAKU.frame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Dimas Fajar
 */
public class EditSupplier extends javax.swing.JFrame {

    /**
     * Creates new form EditSupplier
     */
    public EditSupplier() {
        initComponents();
    }

    private String selectedSupplierId;
    private String selectedNamaSupplier;
    private String selectedAlamatSupplier;
    private String selectedNomorSupplier;
    private String selectedEmailSupplier;

    public void populateFields(String supplierId, String namaSupplier, String alamatSupplier,
            String nomorSupplier, String emailSupplier) {
        selectedSupplierId = supplierId;
        selectedNamaSupplier = namaSupplier;
        selectedAlamatSupplier = alamatSupplier;
        selectedNomorSupplier = nomorSupplier;
        selectedEmailSupplier = emailSupplier;

        // Mengisi kolom-kolom dengan data yang dipilih
        txtSupplierID.setText(selectedSupplierId);
        txtSupplierID.setEditable(false);
        txtNamaSupp.setText(selectedNamaSupplier);
        txtAlamatSupp.setText(selectedAlamatSupplier);
        txtNomorHpSupp.setText(selectedNomorSupplier);
        txtEmailSupp.setText(selectedEmailSupplier);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tambahSupplier = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        btnInSupplier = new javax.swing.JButton();
        txtNamaSupp = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtAlamatSupp = new javax.swing.JTextField();
        txtNomorHpSupp = new javax.swing.JTextField();
        txtEmailSupp = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtSupplierID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        tambahSupplier.setBackground(new java.awt.Color(240, 243, 252));
        tambahSupplier.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/batal.png"))); // NOI18N
        jButton11.setBorderPainted(false);
        jButton11.setContentAreaFilled(false);
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });
        tambahSupplier.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 550, 90, -1));

        btnInSupplier.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/simpan.png"))); // NOI18N
        btnInSupplier.setBorderPainted(false);
        btnInSupplier.setContentAreaFilled(false);
        btnInSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInSupplierActionPerformed(evt);
            }
        });
        tambahSupplier.add(btnInSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 550, 88, -1));
        tambahSupplier.add(txtNamaSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 250, 330, 40));

        jLabel28.setText("Nama supplier");
        tambahSupplier.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, -1, -1));
        tambahSupplier.add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 220, 290, -1));
        tambahSupplier.add(txtAlamatSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 357, 450, 40));
        tambahSupplier.add(txtNomorHpSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 250, 430, 40));
        tambahSupplier.add(txtEmailSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 360, 430, 40));

        jLabel30.setText("Alamat");
        tambahSupplier.add(jLabel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 328, -1, -1));

        jLabel31.setText("Nomor HP");
        tambahSupplier.add(jLabel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 220, -1, -1));

        jLabel32.setText("Email");
        tambahSupplier.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 330, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel19.setText("Masukkan Data Supplier");
        tambahSupplier.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));
        tambahSupplier.add(txtSupplierID, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, 90, 40));

        jLabel1.setText("ID");
        tambahSupplier.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1085, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tambahSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 1085, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 647, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(tambahSupplier, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        txtSupplierID.setText("");
        txtAlamatSupp.setText("");
        txtEmailSupp.setText("");
        txtNamaSupp.setText("");
        txtNomorHpSupp.setText("");
        dispose();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void btnInSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInSupplierActionPerformed
        String supplierId = txtSupplierID.getText();
        String namaSupplier = txtNamaSupp.getText();
        String alamatSupplier = txtAlamatSupp.getText();
        String nomorSupplier = txtNomorHpSupp.getText();
        String emailSupplier = txtEmailSupp.getText();

        if (namaSupplier.isEmpty() || alamatSupplier.isEmpty() || nomorSupplier.isEmpty() || emailSupplier.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Mohon lengkapi semua field!");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/tobakuu";
        String username = "root";
        String password = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Berhasil terhubung ke database!");

            String query = "UPDATE supplier SET namaSupplier=?, alamatSupplier=?, nomorSupplier=?, emailSupplier=? WHERE SupplierID=?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, namaSupplier);
            statement.setString(2, alamatSupplier);
            statement.setString(3, nomorSupplier);
            statement.setString(4, emailSupplier);
            statement.setString(5, supplierId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data berhasil diperbarui di dalam database!");
                JOptionPane.showMessageDialog(null, "Data berhasil diperbarui!");
                txtSupplierID.setText("");
                txtNamaSupp.setText("");
                txtAlamatSupp.setText("");
                txtNomorHpSupp.setText("");
                txtEmailSupp.setText("");
                dispose();
            }

            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Driver JDBC tidak ditemukan");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau data yang dimasukkan tidak sesuai");
        }
    }//GEN-LAST:event_btnInSupplierActionPerformed

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
            java.util.logging.Logger.getLogger(EditSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditSupplier.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EditSupplier().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInSupplier;
    private javax.swing.JButton jButton11;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JPanel tambahSupplier;
    private javax.swing.JTextField txtAlamatSupp;
    private javax.swing.JTextField txtEmailSupp;
    private javax.swing.JTextField txtNamaSupp;
    private javax.swing.JTextField txtNomorHpSupp;
    private javax.swing.JTextField txtSupplierID;
    // End of variables declaration//GEN-END:variables
}
