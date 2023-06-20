/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TOBAKU.frame;

import TOBAKU.koneksi.koneksi;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Dimas Fajar
 */
public class Master extends javax.swing.JPanel {

    Connection conn = koneksi.getKoneksi();

    public Master() {
        initComponents();
        mainPanel.removeAll();
        mainPanel.add(dashboardMaster);
        mainPanel.repaint();
        mainPanel.revalidate();
        String searchTerm = txtSearchBarang.getText().trim();
        populateBarangTable(searchTerm);
        populatePelangganTable();
        populateSupplierTable();
        TabelBarang.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                int selectedRow = TabelBarang.getSelectedRow();
                if (selectedRow != -1) {
                    String barangId = (String) TabelBarang.getValueAt(selectedRow, 0);
                    displayBarcodeImage(barangId);
                }
            }
        });
    }

    private void displayBarcodeImage(String barangId) {
        try {
            // Ganti "conn" dengan objek Connection yang sesuai
            String query = "SELECT Barcode FROM barang WHERE BarangID = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, barangId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // Mengambil data barcode dari database
                byte[] barcodeBytes = rs.getBytes("Barcode");

                // Menampilkan gambar barcode pada JLabel
                if (barcodeBytes != null) {
                    ImageIcon barcodeIcon = new ImageIcon(barcodeBytes);
                    Lbarcode.setIcon(barcodeIcon);
                } else {
                    // Jika tidak ada barcode, kosongkan label
                    Lbarcode.setIcon(null);
                }
            } else {
                // Jika tidak ada data barang dengan BarangID yang dipilih
                Lbarcode.setIcon(null);
                JOptionPane.showMessageDialog(this, "Data barang tidak ditemukan.");
            }

            statement.close();
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan dalam mengambil data barcode.");
        }
    }

    private void populateBarangTable(String searchTerm) {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Barang ID");
            model.addColumn("Supplier ID");
            model.addColumn("Nama");
            model.addColumn("Stok");
            model.addColumn("Merek");
            model.addColumn("Kategori");
            model.addColumn("Harga Jual");
            model.addColumn("Harga Beli");
            model.addColumn("Satuan");
            model.addColumn("Barcode");

            String query = "SELECT * FROM barang WHERE Nama LIKE '%" + searchTerm + "%'";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String barangId = rs.getString("BarangID");
                String supplierId = rs.getString("SupplierID");
                String nama = rs.getString("Nama");
                int stok = rs.getInt("Stok");
                String merek = rs.getString("Merek");
                String kategori = rs.getString("Kategori");
                double hargaJual = rs.getDouble("HargaJual");
                double hargaBeli = rs.getDouble("HargaBeli");
                String satuan = rs.getString("Satuan");
                String barcode = rs.getString("Barcode");

                model.addRow(new Object[]{barangId, supplierId, nama, stok, merek, kategori, hargaJual, hargaBeli, satuan, barcode});
            }

            TabelBarang.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populateSupplierTable() {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Supplier ID");
            model.addColumn("Nama Supplier");
            model.addColumn("Alamat");
            model.addColumn("Nomor Supplier");
            model.addColumn("Email Supplier");

            String query = "SELECT * FROM supplier";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String supplierId = rs.getString("SupplierID");
                String namaSupplier = rs.getString("namaSupplier");
                String alamatsupplier = rs.getString("alamatSupplier");
                String nomorSupplier = rs.getString("nomorSupplier");
                String emailSupplier = rs.getString("emailSupplier");

                model.addRow(new Object[]{supplierId, namaSupplier, alamatsupplier, nomorSupplier, emailSupplier});
            }

            TabelSupplier.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void populatePelangganTable() {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Pelanggan ID");
            model.addColumn("Jenis Member");
            model.addColumn("Nama Pelanggan");
            model.addColumn("Alamat");
            model.addColumn("Nomor HP");
            model.addColumn("Email Pelanggan");

            String query = "SELECT * FROM pelanggan";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String pelangganId = rs.getString("PelangganID");
                String jenisMember = rs.getString("jenisMember");
                String namaPelanggan = rs.getString("namaPelanggan");
                String alamat = rs.getString("alamat");
                String nomorHP = rs.getString("nomorHP");
                String emailPelanggan = rs.getString("emailPelanggan");

                model.addRow(new Object[]{pelangganId, jenisMember, namaPelanggan, alamat, nomorHP, emailPelanggan});
            }

            TabelPelanggan.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshBarangTable() {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Barang ID");
            model.addColumn("Supplier ID");
            model.addColumn("Nama");
            model.addColumn("Stok");
            model.addColumn("Merek");
            model.addColumn("Kategori");
            model.addColumn("Harga Jual");
            model.addColumn("Harga Beli");
            model.addColumn("Satuan");
            model.addColumn("Barcode");

            String query = "SELECT * FROM barang";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String barangId = rs.getString("BarangID");
                String supplierId = rs.getString("SupplierID");
                String nama = rs.getString("Nama");
                int stok = rs.getInt("Stok");
                String merek = rs.getString("Merek");
                String kategori = rs.getString("Kategori");
                double hargaJual = rs.getDouble("HargaJual");
                double hargaBeli = rs.getDouble("HargaBeli");
                String satuan = rs.getString("Satuan");
                String barcode = rs.getString("Barcode");

                model.addRow(new Object[]{barangId, supplierId, nama, stok, merek, kategori, hargaJual, hargaBeli, satuan, barcode});
            }

            TabelBarang.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshPelangganTable() {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Pelanggan ID");
            model.addColumn("Jenis Member");
            model.addColumn("Nama Pelanggan");
            model.addColumn("Alamat");
            model.addColumn("Nomor HP");
            model.addColumn("Email Pelanggan");

            String query = "SELECT * FROM pelanggan";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String pelangganId = rs.getString("PelangganID");
                String jenisMember = rs.getString("jenisMember");
                String namaPelanggan = rs.getString("namaPelanggan");
                String alamat = rs.getString("alamat");
                String nomorHP = rs.getString("nomorHP");
                String emailPelanggan = rs.getString("emailPelanggan");

                model.addRow(new Object[]{pelangganId, jenisMember, namaPelanggan, alamat, nomorHP, emailPelanggan});
            }

            TabelPelanggan.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshSupplierTable() {
        try {
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Supplier ID");
            model.addColumn("Nama Supplier");
            model.addColumn("Alamat");
            model.addColumn("Nomor Supplier");
            model.addColumn("Email Supplier");

            String query = "SELECT * FROM supplier";
            ResultSet rs = conn.createStatement().executeQuery(query);

            while (rs.next()) {
                String supplierId = rs.getString("SupplierID");
                String namaSupplier = rs.getString("namaSupplier");
                String alamatSupplier = rs.getString("alamatSupplier");
                String nomorSupplier = rs.getString("nomorSupplier");
                String emailSupplier = rs.getString("emailSupplier");

                model.addRow(new Object[]{supplierId, namaSupplier, alamatSupplier, nomorSupplier, emailSupplier});
            }

            TabelSupplier.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
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
        dashboardMaster = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        databarang = new javax.swing.JPanel();
        btnBarcode = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        btneditbarang = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        tabel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TabelBarang = new javax.swing.JTable();
        txtSearchBarang = new javax.swing.JTextField();
        btnSearchBarang = new javax.swing.JButton();
        Lbarcode = new javax.swing.JLabel();
        dataPelanggan = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        label3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TabelPelanggan = new javax.swing.JTable();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        dataSupplier = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        tabelSupplier = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        TabelSupplier = new javax.swing.JTable();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();

        mainPanel.setBackground(new java.awt.Color(240, 243, 252));
        mainPanel.setLayout(new java.awt.CardLayout());

        dashboardMaster.setBackground(new java.awt.Color(240, 243, 252));
        dashboardMaster.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton1.setText("Data Barang");
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        dashboardMaster.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 141, 240, 50));

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton2.setText("Data Pelanggan");
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        dashboardMaster.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 141, 240, 50));

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton3.setText("Data Supplier");
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        dashboardMaster.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 140, 240, 50));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/databarang.png"))); // NOI18N
        dashboardMaster.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 230, -1, -1));

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/datasupplier.png"))); // NOI18N
        dashboardMaster.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 260, -1, -1));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/datapelanggan.png"))); // NOI18N
        dashboardMaster.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 230, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/aset1.png"))); // NOI18N
        dashboardMaster.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 114, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/aset2.png"))); // NOI18N
        dashboardMaster.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(689, 114, -1, -1));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/aset3.png"))); // NOI18N
        dashboardMaster.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(361, 114, -1, -1));

        mainPanel.add(dashboardMaster, "card2");

        databarang.setBackground(new java.awt.Color(240, 243, 252));
        databarang.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/barcode.png"))); // NOI18N
        btnBarcode.setBorder(null);
        btnBarcode.setBorderPainted(false);
        btnBarcode.setContentAreaFilled(false);
        btnBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarcodeActionPerformed(evt);
            }
        });
        databarang.add(btnBarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 120, -1, -1));

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel21.setText("Data Barang");
        databarang.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/back.png"))); // NOI18N
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        databarang.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 40, -1));

        btneditbarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnedit.png"))); // NOI18N
        btneditbarang.setBorder(null);
        btneditbarang.setBorderPainted(false);
        btneditbarang.setContentAreaFilled(false);
        btneditbarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneditbarangActionPerformed(evt);
            }
        });
        databarang.add(btneditbarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jButton20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnhapus.png"))); // NOI18N
        jButton20.setBorder(null);
        jButton20.setBorderPainted(false);
        jButton20.setContentAreaFilled(false);
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });
        databarang.add(jButton20, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, -1, -1));

        jButton21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnrefresh.png"))); // NOI18N
        jButton21.setBorder(null);
        jButton21.setBorderPainted(false);
        jButton21.setContentAreaFilled(false);
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });
        databarang.add(jButton21, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, -1, -1));

        tabel.setBackground(new java.awt.Color(240, 243, 252));
        tabel.setLayout(new java.awt.CardLayout());

        TabelBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(TabelBarang);

        tabel.add(jScrollPane1, "card2");

        databarang.add(tabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 980, 430));
        databarang.add(txtSearchBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 130, 190, 30));

        btnSearchBarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/search.png"))); // NOI18N
        btnSearchBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchBarangActionPerformed(evt);
            }
        });
        databarang.add(btnSearchBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 130, 40, 30));
        databarang.add(Lbarcode, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 30, 160, 60));

        mainPanel.add(databarang, "card2");

        dataPelanggan.setBackground(new java.awt.Color(240, 243, 252));
        dataPelanggan.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel22.setText("Data Pelanggan");
        dataPelanggan.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/back.png"))); // NOI18N
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        dataPelanggan.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 40, -1));

        label3.setBackground(new java.awt.Color(240, 243, 252));
        label3.setLayout(new java.awt.CardLayout());

        TabelPelanggan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane5.setViewportView(TabelPelanggan);

        label3.add(jScrollPane5, "card2");

        dataPelanggan.add(label3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 980, 430));

        jButton22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnedit.png"))); // NOI18N
        jButton22.setBorder(null);
        jButton22.setBorderPainted(false);
        jButton22.setContentAreaFilled(false);
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });
        dataPelanggan.add(jButton22, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jButton23.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnhapus.png"))); // NOI18N
        jButton23.setBorder(null);
        jButton23.setBorderPainted(false);
        jButton23.setContentAreaFilled(false);
        jButton23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton23ActionPerformed(evt);
            }
        });
        dataPelanggan.add(jButton23, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, -1, -1));

        jButton24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnrefresh.png"))); // NOI18N
        jButton24.setBorder(null);
        jButton24.setBorderPainted(false);
        jButton24.setContentAreaFilled(false);
        jButton24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton24ActionPerformed(evt);
            }
        });
        dataPelanggan.add(jButton24, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, -1, -1));

        mainPanel.add(dataPelanggan, "card2");

        dataSupplier.setBackground(new java.awt.Color(240, 243, 252));
        dataSupplier.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel23.setText("Data Supplier");
        dataSupplier.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 40, -1, -1));

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/back.png"))); // NOI18N
        jButton7.setBorderPainted(false);
        jButton7.setContentAreaFilled(false);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        dataSupplier.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 40, -1));

        tabelSupplier.setBackground(new java.awt.Color(240, 243, 252));
        tabelSupplier.setLayout(new java.awt.CardLayout());

        TabelSupplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Nama depan", "Nama belakang", "Alamat", "Nomor HP", "Email"
            }
        ));
        jScrollPane4.setViewportView(TabelSupplier);

        tabelSupplier.add(jScrollPane4, "card2");

        dataSupplier.add(tabelSupplier, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, 980, 430));

        jButton25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnedit.png"))); // NOI18N
        jButton25.setBorder(null);
        jButton25.setBorderPainted(false);
        jButton25.setContentAreaFilled(false);
        jButton25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton25ActionPerformed(evt);
            }
        });
        dataSupplier.add(jButton25, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, -1, -1));

        jButton26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnhapus.png"))); // NOI18N
        jButton26.setBorder(null);
        jButton26.setBorderPainted(false);
        jButton26.setContentAreaFilled(false);
        jButton26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton26ActionPerformed(evt);
            }
        });
        dataSupplier.add(jButton26, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 120, -1, -1));

        jButton27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/btnrefresh.png"))); // NOI18N
        jButton27.setBorder(null);
        jButton27.setBorderPainted(false);
        jButton27.setContentAreaFilled(false);
        jButton27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton27ActionPerformed(evt);
            }
        });
        dataSupplier.add(jButton27, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 120, -1, -1));

        mainPanel.add(dataSupplier, "card2");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(databarang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(dataPelanggan);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();

        mainPanel.add(dataSupplier);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dashboardMaster);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dashboardMaster);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        mainPanel.removeAll();
        mainPanel.add(dashboardMaster);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        int selectedRow = TabelPelanggan.getSelectedRow();
        if (selectedRow != -1) {
            String pelangganId = (String) TabelPelanggan.getValueAt(selectedRow, 0);
            String jenisMember = (String) TabelPelanggan.getValueAt(selectedRow, 1);
            String namaPelanggan = (String) TabelPelanggan.getValueAt(selectedRow, 2);
            String alamat = (String) TabelPelanggan.getValueAt(selectedRow, 3);
            String nomorHP = (String) TabelPelanggan.getValueAt(selectedRow, 4);
            String emailPelanggan = (String) TabelPelanggan.getValueAt(selectedRow, 5);

            refreshPelangganTable();
            EditPelanggan editp = new EditPelanggan();
            editp.populateFields(pelangganId, jenisMember, namaPelanggan, alamat, nomorHP, emailPelanggan);
            editp.setVisible(true);
        }
    }//GEN-LAST:event_jButton22ActionPerformed

    private void btneditbarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneditbarangActionPerformed
        int selectedRow = TabelBarang.getSelectedRow();
        if (selectedRow != -1) {
            // Ambil nilai data dari JTable sesuai dengan baris yang dipilih
            String barangId = (String) TabelBarang.getValueAt(selectedRow, 0);
            String supplierId = (String) TabelBarang.getValueAt(selectedRow, 1);
            String nama = (String) TabelBarang.getValueAt(selectedRow, 2);
            int stok = (int) TabelBarang.getValueAt(selectedRow, 3);
            String merek = (String) TabelBarang.getValueAt(selectedRow, 4);
            String kategori = (String) TabelBarang.getValueAt(selectedRow, 5);
            double hargaJual = (double) TabelBarang.getValueAt(selectedRow, 6);
            double hargaBeli = (double) TabelBarang.getValueAt(selectedRow, 7);
            String satuan = (String) TabelBarang.getValueAt(selectedRow, 8);

            refreshBarangTable();
            EditBarang edit = new EditBarang();
            edit.populateFields(barangId, supplierId, nama, stok, merek, kategori, hargaJual, hargaBeli, satuan);
            edit.setVisible(true);
        } else {
            // Tampilkan pesan bahwa tidak ada baris yang dipilih
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih.");
        }
    }//GEN-LAST:event_btneditbarangActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        refreshBarangTable();
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        int selectedRow = TabelBarang.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Ambil nilai data dari JTable sesuai dengan baris yang dipilih
                String barangId = (String) TabelBarang.getValueAt(selectedRow, 0);

                // Hapus data dari database
                deleteData(barangId);

                // Hapus baris dari JTable
                DefaultTableModel model = (DefaultTableModel) TabelBarang.getModel();
                model.removeRow(selectedRow);
            }
        } else {
            // Tampilkan pesan bahwa tidak ada baris yang dipilih
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih.");
        }
    }

    private void deleteData(String barangId) {
        try {
            String query = "DELETE FROM barang WHERE BarangID = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, barangId);
            statement.executeUpdate();

            // Tampilkan pesan bahwa data berhasil dihapus
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Tampilkan pesan bahwa terjadi error saat menghapus data
            JOptionPane.showMessageDialog(this, "Terjadi error saat menghapus data.");
        }
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton23ActionPerformed
        int selectedRow = TabelPelanggan.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Ambil nilai data dari JTable sesuai dengan baris yang dipilih
                String pelangganId = (String) TabelPelanggan.getValueAt(selectedRow, 0);

                // Hapus data dari database
                deletePelanggan(pelangganId);

                // Hapus baris dari JTable
                DefaultTableModel model = (DefaultTableModel) TabelPelanggan.getModel();
                model.removeRow(selectedRow);
            }
        } else {
            // Tampilkan pesan bahwa tidak ada baris yang dipilih
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih.");
        }
    }

    private void deletePelanggan(String pelangganId) {
        try {
            String query = "DELETE FROM pelanggan WHERE PelangganID = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, pelangganId);
            statement.executeUpdate();

            // Tampilkan pesan bahwa data berhasil dihapus
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Tampilkan pesan bahwa terjadi error saat menghapus data
            JOptionPane.showMessageDialog(this, "Terjadi error saat menghapus data.");
        }
    }//GEN-LAST:event_jButton23ActionPerformed

    private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton26ActionPerformed
        int selectedRow = TabelSupplier.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Ambil nilai data dari JTable sesuai dengan baris yang dipilih
                String supplierId = (String) TabelSupplier.getValueAt(selectedRow, 0);

                // Hapus data dari database
                deleteSupplier(supplierId);

                // Hapus baris dari JTable
                DefaultTableModel model = (DefaultTableModel) TabelSupplier.getModel();
                model.removeRow(selectedRow);
            }
        } else {
            // Tampilkan pesan bahwa tidak ada baris yang dipilih
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih.");
        }
    }

    private void deleteSupplier(String supplierId) {
        try {
            String query = "DELETE FROM supplier WHERE SupplierID = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, supplierId);
            statement.executeUpdate();

            // Tampilkan pesan bahwa data berhasil dihapus
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Tampilkan pesan bahwa terjadi error saat menghapus data
            JOptionPane.showMessageDialog(this, "Terjadi error saat menghapus data.");
        }
    }//GEN-LAST:event_jButton26ActionPerformed

    private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton24ActionPerformed
        refreshPelangganTable();
    }//GEN-LAST:event_jButton24ActionPerformed

    private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton27ActionPerformed
        refreshSupplierTable();
    }//GEN-LAST:event_jButton27ActionPerformed

    private void jButton25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton25ActionPerformed
        int selectedRow = TabelSupplier.getSelectedRow();
        if (selectedRow != -1) {
            String supplierId = (String) TabelSupplier.getValueAt(selectedRow, 0);
            String namaSupplier = (String) TabelSupplier.getValueAt(selectedRow, 1);
            String alamatSupplier = (String) TabelSupplier.getValueAt(selectedRow, 2);
            String nomorSupplier = (String) TabelSupplier.getValueAt(selectedRow, 3);
            String emailSupplier = (String) TabelSupplier.getValueAt(selectedRow, 4);

            refreshSupplierTable();
            EditSupplier edits = new EditSupplier();
            edits.populateFields(supplierId, namaSupplier, alamatSupplier, nomorSupplier, emailSupplier);
            edits.setVisible(true);
        }
    }//GEN-LAST:event_jButton25ActionPerformed

    private void btnSearchBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchBarangActionPerformed
        String searchTerm = txtSearchBarang.getText().trim(); // Mengambil teks pencarian dari JTextField
        populateBarangTable(searchTerm);
    }//GEN-LAST:event_btnSearchBarangActionPerformed

    private void btnBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarcodeActionPerformed
        
    }//GEN-LAST:event_btnBarcodeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Lbarcode;
    private javax.swing.JTable TabelBarang;
    private javax.swing.JTable TabelPelanggan;
    private javax.swing.JTable TabelSupplier;
    private javax.swing.JButton btnBarcode;
    private javax.swing.JButton btnSearchBarang;
    private javax.swing.JButton btneditbarang;
    private javax.swing.JPanel dashboardMaster;
    private javax.swing.JPanel dataPelanggan;
    private javax.swing.JPanel dataSupplier;
    private javax.swing.JPanel databarang;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel label3;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel tabel;
    private javax.swing.JPanel tabelSupplier;
    private javax.swing.JTextField txtSearchBarang;
    // End of variables declaration//GEN-END:variables
}
