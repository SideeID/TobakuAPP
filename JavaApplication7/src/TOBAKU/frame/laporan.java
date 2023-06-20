package TOBAKU.frame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import TOBAKU.koneksi.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class laporan extends javax.swing.JPanel {

    Connection conn = koneksi.getKoneksi();

    public laporan() throws SQLException {
        initComponents();
        tampilkanJumlahTransaksi();
        tampilkanJumlahKeuntungan();
        cbList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Hari Ini", "Minggu Ini", "30 Hari Terakhir"}));
        cbList.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbListItemStateChanged(evt);
            }
        });
    }

    private void tampilkanJumlahTransaksi() {
        String query = "SELECT JumlahTransaksi FROM view_jumlah_transaksi";

        try (PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int jumlahTransaksi = resultSet.getInt("JumlahTransaksi");
                jLabelJumlahTransaksi.setText(Integer.toString(jumlahTransaksi));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau query tidak valid");
        }
    }

    private void tampilkanJumlahKeuntungan() {
        String query = "SELECT TotalKeuntungan FROM total_keuntungan";

        try (PreparedStatement statement = conn.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                int jumlahKeuntungan = resultSet.getInt("TotalKeuntungan");
                jLabelTotalKeuntungan.setText(Integer.toString(jumlahKeuntungan));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau query tidak valid");
        }
    }

    private void cbListItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            String selectedOption = cbList.getSelectedItem().toString();

            if (selectedOption.equals("Hari Ini")) {
                ShowHariIniData();
            } else if (selectedOption.equals("Minggu Ini")) {
                ShowMingguIniData();
            } else if (selectedOption.equals("30 Hari Terakhir")) {
                ShowBulanIniData();
            }
        }
    }

    private void ShowHariIniData() {
        String query = "SELECT BarangTerlaris, TotalKeuntungan, JumlahTransaksi FROM hari_ini";

        try (PreparedStatement statement = conn.prepareStatement(query);
                ResultSet reader = statement.executeQuery()) {

            if (reader.next()) {
                String bestseller = reader.getString("BarangTerlaris");
                String profit = reader.getString("TotalKeuntungan");
                String transactions = reader.getString("JumlahTransaksi");

                LabelBestseller.setText(bestseller);
                LabelProfit.setText(profit);
                LabelTransaksi.setText(transactions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau query tidak valid");
        }
    }

    private void ShowMingguIniData() {
        String query = "SELECT BarangTerlaris, TotalKeuntungan, JumlahTransaksi FROM minggu_ini";

        try (PreparedStatement statement = conn.prepareStatement(query);
                ResultSet reader = statement.executeQuery()) {

            if (reader.next()) {
                String bestseller = reader.getString("BarangTerlaris");
                String profit = reader.getString("TotalKeuntungan");
                String transactions = reader.getString("JumlahTransaksi");

                LabelBestseller.setText(bestseller);
                LabelProfit.setText(profit);
                LabelTransaksi.setText(transactions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau query tidak valid");
        }
    }

    private void ShowBulanIniData() {
        String query = "SELECT BarangTerlaris, TotalKeuntungan, JumlahTransaksi FROM bulan_ini";

        try (PreparedStatement statement = conn.prepareStatement(query);
                ResultSet reader = statement.executeQuery()) {

            if (reader.next()) {
                String bestseller = reader.getString("BarangTerlaris");
                String profit = reader.getString("TotalKeuntungan");
                String transactions = reader.getString("JumlahTransaksi");

                LabelBestseller.setText(bestseller);
                LabelProfit.setText(profit);
                LabelTransaksi.setText(transactions);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau query tidak valid");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        dashboardLaporan = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cbList = new javax.swing.JComboBox<>();
        LabelTransaksi = new javax.swing.JLabel();
        LabelProfit = new javax.swing.JLabel();
        LabelBestseller = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelTotalKeuntungan = new javax.swing.JLabel();
        jLabelJumlahTransaksi = new javax.swing.JLabel();

        mainPanel.setBackground(new java.awt.Color(240, 243, 252));
        mainPanel.setLayout(new java.awt.CardLayout());

        dashboardLaporan.setBackground(new java.awt.Color(240, 243, 252));
        dashboardLaporan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(122, 122, 122));
        jLabel13.setText("TOBAKU");
        dashboardLaporan.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 510, 160, 40));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(122, 122, 122));
        jLabel15.setText("TOBAKU");
        dashboardLaporan.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 380, 160, 40));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(122, 122, 122));
        jLabel14.setText("Total transaksi di");
        dashboardLaporan.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 480, 190, 40));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(122, 122, 122));
        jLabel12.setText("Total profit di");
        dashboardLaporan.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 350, 160, 40));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel9.setText("Transaksi");
        dashboardLaporan.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 450, -1, -1));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(122, 122, 122));
        jLabel11.setText("TOBAKU");
        dashboardLaporan.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 250, 160, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel8.setText("Profit");
        dashboardLaporan.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 320, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(122, 122, 122));
        jLabel10.setText("Barang terlaku di");
        dashboardLaporan.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 220, 190, 40));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setText("Best seller");
        dashboardLaporan.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 190, -1, -1));

        cbList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hari ini", "Minggu ini", "30 Hari Terakhir" }));
        cbList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbListActionPerformed(evt);
            }
        });
        dashboardLaporan.add(cbList, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 220, 120, 40));

        LabelTransaksi.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        LabelTransaksi.setText("0");
        dashboardLaporan.add(LabelTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 478, 280, 50));

        LabelProfit.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        LabelProfit.setText("0");
        dashboardLaporan.add(LabelProfit, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 348, 280, 50));

        LabelBestseller.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        LabelBestseller.setText("0");
        dashboardLaporan.add(LabelBestseller, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 220, 280, 50));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Rp.");
        dashboardLaporan.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 180, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Rp.");
        dashboardLaporan.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 310, -1, -1));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Rp.");
        dashboardLaporan.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 440, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/assetlaporan.png"))); // NOI18N
        dashboardLaporan.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel2.setText("<HTML><u>Total profit</u></HTML>");
        dashboardLaporan.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 10, 150, 60));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setText("<HTML><u>Total transaksi</u></HTML>");
        dashboardLaporan.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, 180, 60));

        jLabelTotalKeuntungan.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabelTotalKeuntungan.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTotalKeuntungan.setText("12344");
        dashboardLaporan.add(jLabelTotalKeuntungan, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 70, 160, 40));

        jLabelJumlahTransaksi.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabelJumlahTransaksi.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelJumlahTransaksi.setText("12344");
        dashboardLaporan.add(jLabelJumlahTransaksi, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 160, 40));

        mainPanel.add(dashboardLaporan, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1022, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbListActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelBestseller;
    private javax.swing.JLabel LabelProfit;
    private javax.swing.JLabel LabelTransaksi;
    private javax.swing.JComboBox<String> cbList;
    private javax.swing.JPanel dashboardLaporan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelJumlahTransaksi;
    private javax.swing.JLabel jLabelTotalKeuntungan;
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
