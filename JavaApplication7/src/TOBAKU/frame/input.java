package TOBAKU.frame;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import TOBAKU.koneksi.koneksi;
import com.barcodelib.barcode.Linear;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dimas Fajar
 */
public class input extends javax.swing.JPanel {

    Connection conn = koneksi.getKoneksi();

    private DefaultTableModel model;
    ResultSet rs = null;
    PreparedStatement pst = null;
    private int harga = 0;
    int idbarang = 0;
    int currentTransaction = 0;
    HashMap prm = new HashMap();

    public input() throws SQLException {
        initComponents();
        loadComboBoxData();

        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");

        txTanggal.setText(s.format(date));
        txTotalBayar.setText("0");
        txBayar.setText("0");
        txKembalian.setText("0");

        onChangeNamaBarang((String) cbNamaBarang.getSelectedItem());
        totalBiaya();

        txIDBarang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertBarang();
            }
        });
    }

    public static String barcodeImagePath;

//======================================================================================================    
    public void initSemua() throws SQLException {
        onChangeNamaBarang((String) cbNamaBarang.getSelectedItem());
        getCurrentTransaction();
        loadCurrentTransactionTable();
    }

    public void getCurrentTransaction() {
        try {
            String sql = "SELECT BeliID FROM transaksi_beli ORDER BY BeliID DESC LIMIT 1";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                currentTransaction = rs.getInt(1);
                //System.out.println(currentTransaction);
                txNoTransaksi.setText(Integer.toString(currentTransaction));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void loadCurrentTransactionTable() {
        model = new DefaultTableModel();
        model.addColumn("ID Barang");
        model.addColumn("Nama");
        model.addColumn("Merk");
        model.addColumn("Jumlah");
        model.addColumn("Satuan");
        model.addColumn("Harga");
        model.addColumn("Diskon");
        model.addColumn("Subtotal");

        try {
            String sql = "SELECT db.BarangID, b.Nama, b.merek, db.Jumlah, b.satuan, db.Harga,"
                    + " db.Diskon, (db.Jumlah * db.Harga) - db.Diskon AS subtotal "
                    + "FROM detail_beli db JOIN transaksi_beli tb ON db.BeliID = "
                    + "tb.BeliID JOIN barang b ON db.BarangID = b.BarangID WHERE "
                    + "db.BeliID = " + currentTransaction + "";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7), rs.getString(8)});
            }
            jTabelT.setModel(model);
            getTotalPrice();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void insertBarang() {
        try {
            String idBarangText = txIDBarang.getText();
            if (!idBarangText.isEmpty()) {
                int idBarang = Integer.parseInt(idBarangText);

                String jumlahBarangText = txJmlhBarang.getText();
                double jumlahBarang;
                if (jumlahBarangText.isEmpty()) {
                    jumlahBarang = 1.0; // Set nilai default jika kolom jumlah barang kosong
                } else {
                    jumlahBarang = Double.parseDouble(jumlahBarangText);
                }

                String diskonText = txDiskon.getText();
                int diskon = 0;
                if (!diskonText.isEmpty()) {
                    diskon = Integer.parseInt(diskonText);
                }

                harga = getHargaBarang(idBarang);

                String sql = "INSERT INTO detail_beli VALUES(?, ?, ?, ?, ?)";
                pst = conn.prepareStatement(sql);
                pst.setInt(1, currentTransaction);
                pst.setInt(2, idBarang);
                pst.setDouble(3, jumlahBarang);
                pst.setInt(4, harga);
                pst.setInt(5, diskon);

                pst.executeUpdate();

                clear2();
                loadCurrentTransactionTable();
                getTotalPrice();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private int getHargaBarang(int idBarang) {
        int harga = 0;
        try {
            String sql = "SELECT hargaJual FROM barang WHERE BarangID = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, idBarang);
            rs = pst.executeQuery();

            if (rs.next()) {
                harga = rs.getInt("hargaJual");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return harga;
    }

    private void getTotalPrice() {
        try {
            String sql = "SELECT SUM((db.Jumlah * db.Harga) - db.Diskon) AS hartot "
                    + "FROM detail_beli db JOIN transaksi_beli tb ON db.BeliID = "
                    + "tb.BeliID JOIN barang b ON db.BarangID = b.BarangID WHERE "
                    + "db.BeliID = " + currentTransaction;
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs.next()) {
                txTotalBayar.setText(Integer.toString(rs.getInt(1)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void getLatest() {
        getCurrentTransaction();
        loadCurrentTransactionTable();
    }

    private void handleDone() {
        try {

            String sql = "UPDATE transaksi_beli SET Total = " + txTotalBayar.getText() + ", "
                    + "bayar = " + txBayar.getText() + " WHERE BeliID = " + currentTransaction;
            pst = conn.prepareStatement(sql);
            System.out.println(sql);
            pst.executeUpdate();

            sql = "INSERT INTO transaksi_beli VALUES (NULL, 4, NOW(), NULL);";
            pst = conn.prepareStatement(sql);
            System.out.println(sql);

            pst.executeUpdate();

            getLatest();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void handleBayar() {
        int total = Integer.valueOf(txTotalBayar.getText());
        int bayar = Integer.valueOf(txBayar.getText());
        int kembalian;

        if (total > bayar) {
            JOptionPane.showMessageDialog(null, "Uang tidak cukup untuk melakukan pembayaran");
        } else {
            kembalian = bayar - total;
            handleDone();
            txKembalian.setText(String.valueOf(kembalian));

            // Menampilkan dialog pesan
            String pesan = "Jumlah total belanjaan:  " + total + "\n" + "Jumlah uang yang dibayarkan: " + bayar + "\n"
                    + "Jumlah uang kembalian: " + kembalian;
            JOptionPane.showMessageDialog(null, pesan);
            clear();
        }
    }

    public void totalBiaya() {
        DefaultTableModel model = (DefaultTableModel) jTabelT.getModel();
        double subtotal;
        int jumlahBaris = jTabelT.getRowCount();
        double totalBiaya = 0;
        double jumlahBarang, hargaBarang, diskonBarang;
        for (int i = 0; i < jumlahBaris; i++) {
            jumlahBarang = Double.parseDouble(jTabelT.getValueAt(i, 3).toString());
            hargaBarang = Integer.parseInt(jTabelT.getValueAt(i, 5).toString());
            diskonBarang = Integer.parseInt(jTabelT.getValueAt(i, 6).toString());
            subtotal = (hargaBarang * jumlahBarang);
            totalBiaya += subtotal;
        }
        int totalBiayaInt = (int) totalBiaya; // Ubah ke tipe data int jika perlu
        txTotalBayar.setText(String.valueOf(totalBiayaInt));
    }

    public void kosong() {
        DefaultTableModel model = (DefaultTableModel) jTabelT.getModel();

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private void loadComboBoxData() {
        try {
            String sql = "SELECT Nama, merek FROM barang";
            pst = conn.prepareStatement(sql);

            rs = pst.executeQuery();

            while (rs.next()) {
                String namaBarang = rs.getString("Nama");
                String merk = rs.getString("merek");
                String data = namaBarang + "-" + merk;
                cbNamaBarang.addItem(data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTextFieldData() {
        String selectedItem = (String) cbNamaBarang.getSelectedItem();
        if (selectedItem != null) {
            String[] data = selectedItem.split("-");
            String namaBarang = data[0];
            String merk = data[1];

            try {
                String sql = "SELECT BarangID FROM barang WHERE Nama = ? AND merek = ?";
                pst = conn.prepareStatement(sql);
                pst.setString(1, namaBarang);
                pst.setString(2, merk);

                rs = pst.executeQuery();

                if (rs.next()) {
                    String idBarang = rs.getString("BarangID");
                    txIDBarang.setText(String.valueOf(idBarang));
                } else {
                    txIDBarang.setText("");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            txIDBarang.setText("");
        }
    }

    public void onChangeNamaBarang(String namaBarang) {
        try {
            String[] split = namaBarang.split("-");

            String sql = "SELECT hargaJual, satuan, merek, BarangID FROM barang WHERE Nama = ? AND merek = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, split[0]);
            pst.setString(2, split[1]);
            rs = pst.executeQuery();

            while (rs.next()) {
                String satuanBarang = rs.getString(2);
                satuan.setText(satuanBarang);
                harga = rs.getInt(1);
                txHarga.setText(String.valueOf(harga));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void clear() {
        txTotalBayar.setText("");
        txBayar.setText("");
        txKembalian.setText("");
    }

    public void clear2() {
        txIDBarang.setText("");
        cbNamaBarang.setSelectedItem(null);
        txJmlhBarang.setText("");
        txHarga.setText("");
        txDiskon.setText("");
    }

//============================================================================================================
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        dashboardBarang = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tambahPelanggan = new javax.swing.JPanel();
        cbMember = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        btnInPelanggan = new javax.swing.JButton();
        txtNamaPelanggan = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        txtAlamat = new javax.swing.JTextField();
        txtNoHP = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
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
        jButton6 = new javax.swing.JButton();
        tambahBarang = new javax.swing.JPanel();
        cbKategori = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        btnInBarang = new javax.swing.JButton();
        txtNamaBarang = new javax.swing.JTextField();
        txtStok = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtHargaBeli = new javax.swing.JTextField();
        txtHargaJual = new javax.swing.JTextField();
        cbSatuan = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        cbSupplier = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        txtMerek = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtBarangID = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        Lbarcode = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        tambahStok = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabelT = new javax.swing.JTable();
        txTotalBayar = new javax.swing.JTextField();
        txBayar = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txKembalian = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txJmlhBarang = new javax.swing.JTextField();
        txHarga = new javax.swing.JTextField();
        txDiskon = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        btntambah = new javax.swing.JButton();
        cbNamaBarang = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        txTanggal = new javax.swing.JTextField();
        satuan = new javax.swing.JLabel();
        txIDBarang = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        txNoTransaksi = new javax.swing.JTextField();
        btnhapus = new javax.swing.JButton();
        btnDone = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(240, 243, 252));
        jPanel1.setMinimumSize(new java.awt.Dimension(989, 514));
        jPanel1.setPreferredSize(new java.awt.Dimension(989, 514));
        jPanel1.setLayout(new java.awt.CardLayout());

        mainPanel.setBackground(new java.awt.Color(240, 243, 252));
        mainPanel.setLayout(new java.awt.CardLayout());

        dashboardBarang.setBackground(new java.awt.Color(240, 243, 252));
        dashboardBarang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton1.setText("Input Barang");
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        dashboardBarang.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 141, 240, 50));

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton2.setText("Input Pelanggan");
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        dashboardBarang.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 141, 240, 50));

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton3.setText("Input Supplier");
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        dashboardBarang.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, 240, 50));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/INbarang.png"))); // NOI18N
        dashboardBarang.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 270, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/INsupplier.png"))); // NOI18N
        dashboardBarang.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 230, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/INpelanggan.png"))); // NOI18N
        dashboardBarang.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 270, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/aset1.png"))); // NOI18N
        dashboardBarang.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 114, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/aset2.png"))); // NOI18N
        dashboardBarang.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(689, 114, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/aset3.png"))); // NOI18N
        dashboardBarang.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(361, 114, -1, -1));

        mainPanel.add(dashboardBarang, "card2");

        tambahPelanggan.setBackground(new java.awt.Color(240, 243, 252));
        tambahPelanggan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cbMember.setForeground(new java.awt.Color(172, 177, 198));
        cbMember.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Prioritas" }));
        tambahPelanggan.add(cbMember, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 250, 380, 40));

        jLabel14.setText("Jenis member");
        tambahPelanggan.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 220, -1, -1));

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/batal.png"))); // NOI18N
        jButton8.setBorderPainted(false);
        jButton8.setContentAreaFilled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        tambahPelanggan.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 550, 90, -1));

        btnInPelanggan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/simpan.png"))); // NOI18N
        btnInPelanggan.setBorderPainted(false);
        btnInPelanggan.setContentAreaFilled(false);
        btnInPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInPelangganActionPerformed(evt);
            }
        });
        tambahPelanggan.add(btnInPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 550, 88, -1));
        tambahPelanggan.add(txtNamaPelanggan, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 250, 510, 40));

        jLabel23.setText("Nama pelanggan");
        tambahPelanggan.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 220, -1, -1));
        tambahPelanggan.add(txtAlamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 360, 270, 40));
        tambahPelanggan.add(txtNoHP, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 360, 300, 40));
        tambahPelanggan.add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 360, 310, 40));

        jLabel25.setText("Alamat");
        tambahPelanggan.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(57, 330, -1, -1));

        jLabel26.setText("Nomor HP");
        tambahPelanggan.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 330, -1, -1));

        jLabel27.setText("Email");
        tambahPelanggan.add(jLabel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 330, -1, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Masukkan Data Pelanggan");
        tambahPelanggan.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/back.png"))); // NOI18N
        jButton7.setBorderPainted(false);
        jButton7.setContentAreaFilled(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        tambahPelanggan.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 40, -1));

        mainPanel.add(tambahPelanggan, "card4");

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
        tambahSupplier.add(txtNamaSupp, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, 450, 40));

        jLabel28.setText("Nama supplier");
        tambahSupplier.add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, -1, -1));
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

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/back.png"))); // NOI18N
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        tambahSupplier.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 40, -1));

        mainPanel.add(tambahSupplier, "card4");

        tambahBarang.setBackground(new java.awt.Color(240, 243, 252));
        tambahBarang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cbKategori.setForeground(new java.awt.Color(172, 177, 198));
        cbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bahan baku", "Alat memasak", "Kardus" }));
        tambahBarang.add(cbKategori, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 220, 150, 40));

        jLabel22.setText("Kategori");
        tambahBarang.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 190, -1, -1));

        jButton14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/batal.png"))); // NOI18N
        jButton14.setBorderPainted(false);
        jButton14.setContentAreaFilled(false);
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        tambahBarang.add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 550, 90, -1));

        btnInBarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/simpan.png"))); // NOI18N
        btnInBarang.setBorderPainted(false);
        btnInBarang.setContentAreaFilled(false);
        btnInBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInBarangActionPerformed(evt);
            }
        });
        tambahBarang.add(btnInBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 550, 88, -1));
        tambahBarang.add(txtNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 220, 200, 40));
        tambahBarang.add(txtStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 220, 200, 40));

        jLabel7.setText("Nama barang");
        tambahBarang.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 190, -1, -1));

        jLabel8.setText("Stok");
        tambahBarang.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 190, -1, -1));

        jLabel9.setText("Merek");
        tambahBarang.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, -1, -1));
        tambahBarang.add(txtHargaBeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 330, 220, 40));
        tambahBarang.add(txtHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 330, 250, 40));

        cbSatuan.setForeground(new java.awt.Color(172, 177, 198));
        cbSatuan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "kg", "ons", "pcs", "pack", "ball", "liter" }));
        cbSatuan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSatuanActionPerformed(evt);
            }
        });
        tambahBarang.add(cbSatuan, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 330, 110, 40));

        jLabel10.setText("Satuan");
        tambahBarang.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 300, -1, -1));

        jLabel11.setText("Harga beli");
        tambahBarang.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 300, -1, -1));

        jLabel12.setText("Harga jual");
        tambahBarang.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 300, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel21.setText("Masukkan Data Barang");
        tambahBarang.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/back.png"))); // NOI18N
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        tambahBarang.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 40, -1));

        cbSupplier.setForeground(new java.awt.Color(172, 177, 198));
        cbSupplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4" }));
        cbSupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSupplierActionPerformed(evt);
            }
        });
        tambahBarang.add(cbSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 160, 40));

        jLabel15.setText("Supplier");
        tambahBarang.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 190, -1, -1));
        tambahBarang.add(txtMerek, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 330, 270, 40));

        jLabel16.setText("Barcode Barang");
        tambahBarang.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 400, -1, -1));
        tambahBarang.add(txtBarangID, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, 120, 40));

        jLabel17.setText("ID Barang");
        tambahBarang.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, -1, -1));
        tambahBarang.add(Lbarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 430, 310, 110));

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnUpStok.png"))); // NOI18N
        jButton4.setBorder(null);
        jButton4.setBorderPainted(false);
        jButton4.setOpaque(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        tambahBarang.add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 130, 120, -1));

        mainPanel.add(tambahBarang, "card4");

        tambahStok.setBackground(new java.awt.Color(240, 243, 252));
        tambahStok.setPreferredSize(new java.awt.Dimension(1020, 640));

        jTabelT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "", "", "", "", "", "", ""
            }
        ));
        jScrollPane1.setViewportView(jTabelT);

        txTotalBayar.setEnabled(false);
        txTotalBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txTotalBayarActionPerformed(evt);
            }
        });

        txBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txBayarActionPerformed(evt);
            }
        });
        txBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txBayarKeyReleased(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel18.setText("Bayar :");

        txKembalian.setEnabled(false);
        txKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txKembalianActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setText("Kembalian :");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel24.setText("Total :");

        jPanel4.setBackground(new java.awt.Color(91, 141, 215));

        txJmlhBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txJmlhBarangActionPerformed(evt);
            }
        });
        txJmlhBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txJmlhBarangKeyReleased(evt);
            }
        });

        txHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txHargaActionPerformed(evt);
            }
        });

        txDiskon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txDiskonActionPerformed(evt);
            }
        });

        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("Nama Barang :");

        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Jumlah :");

        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("Satuan :");

        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("Harga :");

        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Diskon :");

        btntambah.setText("Tambah");
        btntambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btntambahActionPerformed(evt);
            }
        });

        cbNamaBarang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--Pilih--" }));
        cbNamaBarang.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                cbNamaBarangPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });
        cbNamaBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbNamaBarangActionPerformed(evt);
            }
        });
        cbNamaBarang.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                cbNamaBarangPropertyChange(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel38.setText("Tanggal :");

        txTanggal.setEnabled(false);
        txTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txTanggalActionPerformed(evt);
            }
        });

        satuan.setForeground(new java.awt.Color(255, 255, 255));
        satuan.setText("Ini adalah satuan");

        txIDBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txIDBarangActionPerformed(evt);
            }
        });
        txIDBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txIDBarangKeyReleased(evt);
            }
        });

        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("id barang:");

        jLabel40.setText("No. Transaksi :");

        txNoTransaksi.setEnabled(false);
        txNoTransaksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txNoTransaksiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(jLabel39)
                        .addGap(18, 18, 18)
                        .addComponent(txIDBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addComponent(jLabel34)
                                .addGap(21, 21, 21))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel33)
                                .addGap(22, 22, 22)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbNamaBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txJmlhBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel40)
                        .addGap(18, 18, 18)
                        .addComponent(txNoTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(btntambah)
                        .addGap(180, 180, 180))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(satuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel37)
                                    .addComponent(jLabel36))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(79, 79, 79))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel40)
                        .addComponent(txNoTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel38)
                        .addComponent(txTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel35)
                        .addComponent(satuan))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txIDBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel39)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(txDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel33))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txJmlhBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addComponent(btntambah)
                .addContainerGap())
        );

        btnhapus.setText("Hapus");
        btnhapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhapusActionPerformed(evt);
            }
        });

        btnDone.setText("Selesaikan");
        btnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoneActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tambahStokLayout = new javax.swing.GroupLayout(tambahStok);
        tambahStok.setLayout(tambahStokLayout);
        tambahStokLayout.setHorizontalGroup(
            tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tambahStokLayout.createSequentialGroup()
                .addContainerGap(64, Short.MAX_VALUE)
                .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(tambahStokLayout.createSequentialGroup()
                            .addComponent(btnhapus)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel24)
                            .addGap(18, 18, 18)
                            .addComponent(txTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(tambahStokLayout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tambahStokLayout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addGap(18, 18, 18)
                                        .addComponent(txKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tambahStokLayout.createSequentialGroup()
                                        .addComponent(jLabel18)
                                        .addGap(18, 18, 18)
                                        .addComponent(txBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(tambahStokLayout.createSequentialGroup()
                                    .addGap(126, 126, 126)
                                    .addComponent(btnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 967, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47))
            .addGroup(tambahStokLayout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tambahStokLayout.setVerticalGroup(
            tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tambahStokLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnhapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(10, 10, 10)
                .addGroup(tambahStokLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDone)
                .addGap(44, 44, 44))
        );

        mainPanel.add(tambahStok, "card6");

        jPanel1.add(mainPanel, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1085, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahBarang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahPelanggan);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahSupplier);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dashboardBarang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dashboardBarang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dashboardBarang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        cbMember.setSelectedIndex(0);
        txtNamaPelanggan.setText("");
        txtAlamat.setText("");
        txtEmail.setText("");
        txtNoHP.setText("");
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        txtAlamatSupp.setText("");
        txtEmailSupp.setText("");
        txtNamaSupp.setText("");
        txtNomorHpSupp.setText("");
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        txtMerek.setText("");
        cbSatuan.setSelectedIndex(0);
        cbKategori.setSelectedIndex(0);
        txtNamaBarang.setText("");
        txtStok.setText("");
        txtHargaJual.setText("");
        txtHargaBeli.setText("");
        cbSupplier.setSelectedIndex(0);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void cbSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSupplierActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbSupplierActionPerformed

    private void btnInBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInBarangActionPerformed
        int supplierIndex = cbSupplier.getSelectedIndex();
        String supplierValue = cbSupplier.getSelectedItem().toString();
        String nama = txtNamaBarang.getText();
        String stok = txtStok.getText();
        String merek = txtMerek.getText();
        int kategoriIndex = cbKategori.getSelectedIndex();
        String kategoriValue = cbKategori.getSelectedItem().toString();
        String hargajual = txtHargaJual.getText();
        String hargabeli = txtHargaBeli.getText();
        int satuanIndex = cbSatuan.getSelectedIndex();
        String satuanValue = cbSatuan.getSelectedItem().toString();
        int barangId;

        if (supplierIndex == -1 || nama.isEmpty() || stok.isEmpty() || merek.isEmpty()
                || kategoriIndex == -1 || hargajual.isEmpty() || hargabeli.isEmpty() || satuanIndex == -1) {
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

            String query = "INSERT INTO barang (SupplierID, Nama, Stok, Merek, Kategori, HargaJual, HargaBeli, Satuan, BarangID, Barcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setString(1, supplierValue);
            statement.setString(2, nama);
            statement.setString(3, stok);
            statement.setString(4, merek);
            statement.setString(5, kategoriValue);
            statement.setString(6, hargajual);
            statement.setString(7, hargabeli);
            statement.setString(8, satuanValue);

            if (txtBarangID.getText().isEmpty()) {
                // Menghasilkan angka acak 5 digit
                Random random = new Random();
                barangId = 10000 + random.nextInt(90000);
            } else {
                barangId = Integer.parseInt(txtBarangID.getText());
            }

            statement.setInt(9, barangId);

            // Menghasilkan gambar barcode
            Linear barcode = new Linear();
            barcode.setType(Linear.CODE128B);
            barcode.setData(Integer.toString(barangId));
            barcode.setI(11.0f);
            String fname = Integer.toString(barangId);
            barcode.renderBarcode("src/barcode/" + fname + ".png");

            // Mengambil file gambar barcode yang telah disimpan
            File barcodeFile = new File("src/barcode/" + fname + ".png");
            FileInputStream barcodeStream = new FileInputStream(barcodeFile);

            // Mengatur parameter PreparedStatement dengan data gambar barcode
            statement.setBinaryStream(10, barcodeStream, (int) barcodeFile.length());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data berhasil dimasukkan ke dalam database!");
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan!");
                cbSupplier.setSelectedIndex(-1);
                txtNamaBarang.setText("");
                txtStok.setText("");
                txtMerek.setText("");
                cbKategori.setSelectedIndex(-1);
                txtHargaJual.setText("");
                txtHargaBeli.setText("");
                cbSatuan.setSelectedIndex(-1);
                txtBarangID.setText("");

                // Membuat ikon gambar dari file barcode
                Icon barcodeIcon = new ImageIcon(barcodeFile.getAbsolutePath());

                // Mengatur ikon gambar pada JLabel
                Lbarcode.setIcon(barcodeIcon);
            }

            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Driver JDBC tidak ditemukan");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau data yang dimasukkan tidak sesuai");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "File barcode tidak ditemukan atau tidak dapat diakses");
        } catch (Exception ex) {
            Logger.getLogger(input.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnInBarangActionPerformed

    private void btnInPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInPelangganActionPerformed
        String memberValue = cbMember.getSelectedItem().toString();
        String namaPelanggan = txtNamaPelanggan.getText();
        String alamat = txtAlamat.getText();
        String email = txtEmail.getText();
        String noHP = txtNoHP.getText();

        if (memberValue.isEmpty() || namaPelanggan.isEmpty() || alamat.isEmpty() || email.isEmpty() || noHP.isEmpty()) {
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

            String query = "INSERT INTO pelanggan (jenisMember, namaPelanggan, alamat, emailPelanggan, nomorHP) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, memberValue);
            statement.setString(2, namaPelanggan);
            statement.setString(3, alamat);
            statement.setString(4, email);
            statement.setString(5, noHP);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data berhasil dimasukkan ke dalam database!");
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan!");
                cbMember.setSelectedIndex(-1);
                txtNamaPelanggan.setText("");
                txtAlamat.setText("");
                txtEmail.setText("");
                txtNoHP.setText("");
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


    }//GEN-LAST:event_btnInPelangganActionPerformed

    private void btnInSupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInSupplierActionPerformed
        String namaSupplier = txtNamaSupp.getText();
        String AlamatSupp = txtAlamatSupp.getText();
        String NoHpSupp = txtNomorHpSupp.getText();
        String EmailSupp = txtEmailSupp.getText();

        if (namaSupplier.isEmpty() || AlamatSupp.isEmpty() || NoHpSupp.isEmpty() || EmailSupp.isEmpty()) {
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

            String query = "INSERT INTO supplier (namaSupplier, alamatSupplier, nomorSupplier, emailSupplier) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, namaSupplier);
            statement.setString(2, AlamatSupp);
            statement.setString(3, NoHpSupp);
            statement.setString(4, EmailSupp);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Data berhasil dimasukkan ke dalam database!");
                JOptionPane.showMessageDialog(null, "Data berhasil disimpan!");
                txtNamaSupp.setText("");
                txtAlamatSupp.setText("");
                txtNomorHpSupp.setText("");
                txtEmailSupp.setText("");
            }

            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Driver JDBC tidak ditemukan");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Koneksi error atau data yang di masukkan tidak sesuai");
        }

    }//GEN-LAST:event_btnInSupplierActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(tambahStok);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void txTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txTotalBayarActionPerformed
        // TODO add your handling code here:
        totalBiaya();
    }//GEN-LAST:event_txTotalBayarActionPerformed

    private void txBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txBayarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txBayarActionPerformed

    private void txBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txBayarKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txBayarKeyReleased

    private void txKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txKembalianActionPerformed

    private void txJmlhBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txJmlhBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txJmlhBarangActionPerformed

    private void txJmlhBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txJmlhBarangKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txJmlhBarangKeyReleased

    private void txHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txHargaActionPerformed

    private void txDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txDiskonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txDiskonActionPerformed

    private void btntambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahActionPerformed
        // TODO add your handling code here:
        insertBarang();
    }//GEN-LAST:event_btntambahActionPerformed

    private void cbNamaBarangPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbNamaBarangPopupMenuWillBecomeInvisible
        // TODO add your handling code here:
        onChangeNamaBarang((String) cbNamaBarang.getSelectedItem());
    }//GEN-LAST:event_cbNamaBarangPopupMenuWillBecomeInvisible

    private void cbNamaBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNamaBarangActionPerformed

        if (cbNamaBarang.getSelectedIndex() == 0) {
            txIDBarang.setText("");
        } else {
            loadTextFieldData();
        }
    }//GEN-LAST:event_cbNamaBarangActionPerformed

    private void cbNamaBarangPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbNamaBarangPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_cbNamaBarangPropertyChange

    private void txTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txTanggalActionPerformed

    private void txIDBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txIDBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txIDBarangActionPerformed

    private void txIDBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txIDBarangKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txIDBarangKeyReleased

    private void txNoTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txNoTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txNoTransaksiActionPerformed

    private void btnhapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhapusActionPerformed
        int selectRow = jTabelT.getSelectedRow();
        if (selectRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Ambil nilai data dari JTable sesuai dengan baris yang dipilih
                String barangId = (String) jTabelT.getValueAt(selectRow, 0);

                // Hapus baris dari JTable
                DefaultTableModel model = (DefaultTableModel) jTabelT.getModel();
                model.removeRow(selectRow);

                totalBiaya();
                // Hapus data dari database
                try {
                    String sql = "DELETE FROM detail_jual WHERE BarangID = ?";
                    pst = conn.prepareStatement(sql);
                    pst.setString(1, barangId);
                    pst.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } else {
            // Tampilkan pesan bahwa tidak ada baris yang dipilih
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih.");
        }
    }//GEN-LAST:event_btnhapusActionPerformed

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
        if (!txBayar.getText().isEmpty() && !txBayar.getText().equals("0")) {
            handleBayar();
        }
    }//GEN-LAST:event_btnDoneActionPerformed

    private void cbSatuanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSatuanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbSatuanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Lbarcode;
    private javax.swing.JButton btnDone;
    private javax.swing.JButton btnInBarang;
    private javax.swing.JButton btnInPelanggan;
    private javax.swing.JButton btnInSupplier;
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btntambah;
    private javax.swing.JComboBox<String> cbKategori;
    private javax.swing.JComboBox<String> cbMember;
    private javax.swing.JComboBox<String> cbNamaBarang;
    private javax.swing.JComboBox<String> cbSatuan;
    private javax.swing.JComboBox<String> cbSupplier;
    private javax.swing.JPanel dashboardBarang;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabelT;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel satuan;
    private javax.swing.JPanel tambahBarang;
    private javax.swing.JPanel tambahPelanggan;
    private javax.swing.JPanel tambahStok;
    private javax.swing.JPanel tambahSupplier;
    private javax.swing.JTextField txBayar;
    private javax.swing.JTextField txDiskon;
    private javax.swing.JTextField txHarga;
    private javax.swing.JTextField txIDBarang;
    private javax.swing.JTextField txJmlhBarang;
    private javax.swing.JTextField txKembalian;
    private javax.swing.JTextField txNoTransaksi;
    private javax.swing.JTextField txTanggal;
    private javax.swing.JTextField txTotalBayar;
    private javax.swing.JTextField txtAlamat;
    private javax.swing.JTextField txtAlamatSupp;
    private javax.swing.JTextField txtBarangID;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmailSupp;
    private javax.swing.JTextField txtHargaBeli;
    private javax.swing.JTextField txtHargaJual;
    private javax.swing.JTextField txtMerek;
    private javax.swing.JTextField txtNamaBarang;
    private javax.swing.JTextField txtNamaPelanggan;
    private javax.swing.JTextField txtNamaSupp;
    private javax.swing.JTextField txtNoHP;
    private javax.swing.JTextField txtNomorHpSupp;
    private javax.swing.JTextField txtStok;
    // End of variables declaration//GEN-END:variables
}
