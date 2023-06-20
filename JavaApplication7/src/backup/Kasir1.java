
package backup;

import TOBAKU.koneksi.koneksi;
import TOBAKU.main.login;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author acer
 */
public final class Kasir1 extends javax.swing.JFrame {

    private DefaultTableModel model;
    Connection conn;
    ResultSet rs = null;
    PreparedStatement pst = null;
    private int harga = 0;
    int idbarang = 0;
    int currentTransaction = 0;
    HashMap prm = new HashMap();
    JasperReport JasRep;
    JasperPrint Jaspri;
    JasperDesign JasDes;

    public Kasir1() throws SQLException, ClassNotFoundException { //constructor, fungsi yg pertama kali dipanggil saat kasir dipanggil
        this.conn = koneksi.getKoneksi();
        initComponents();
        loadComboBoxData();
        initSemua();
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Date date = new Date();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");

        txTanggal.setText(s.format(date));
        txTotalBayar.setText("0");
        txBayar.setText("0");
        txKembalian.setText("0");

        onChangeNamaBarang((String) cbNamaBarang.getSelectedItem());
        totalBiaya();
    }

    public void initSemua() throws SQLException {
        
        onChangeNamaBarang((String) cbNamaBarang.getSelectedItem());
        getCurrentTransaction();
        loadCurrentTransactionTable();
    }

    public void getCurrentTransaction() {
        try {
            String sql = "SELECT JualID FROM transaksi_jual ORDER BY JualID DESC LIMIT 1";
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
            String sql = "SELECT dj.BarangID, b.Nama, b.merek, dj.Jumlah, b.satuan, dj.Harga,"
                    + " dj.Diskon, (dj.Jumlah * dj.Harga) - dj.Diskon AS subtotal "
                    + "FROM detail_jual dj JOIN transaksi_jual tj ON dj.JualID = "
                    + "tj.JualID JOIN barang b ON dj.BarangID = b.BarangID WHERE "
                    + "dj.JualID = " + currentTransaction + "";
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

            String sql = "INSERT INTO detail_jual VALUES(?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, (int) (Integer.parseInt(txNoTransaksi.getText())));
            pst.setInt(2, Integer.parseInt(txIDBarang.getText()));
            pst.setDouble(3, jumlahBarang);
            pst.setInt(4, (int) (Integer.parseInt(txHarga.getText())));
            pst.setInt(5, diskon);
            System.out.println(sql);

            pst.executeUpdate();

            clear2();
            loadCurrentTransactionTable();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void getTotalPrice() {
        try {
            String sql = "SELECT SUM((dj.Jumlah * dj.Harga) - dj.Diskon) AS hartot "
                    + "FROM detail_jual dj JOIN transaksi_jual tj ON dj.JualID = "
                    + "tj.JualID JOIN barang b ON dj.BarangID = b.BarangID WHERE "
                    + "dj.JualID = " + currentTransaction;
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

    private void showPDF(int kodetr) {
        try {
            String namaFile = "/struk/Struk.jasper";
            InputStream Report;

            Report = getClass().getResourceAsStream(namaFile);
            System.out.println(namaFile);

            HashMap param = new HashMap();
            param.put("KODE_TR", kodetr);
            JasperPrint JPrint = JasperFillManager.fillReport(Report, param, conn);

            JasperViewer.viewReport(JPrint, false);
        } catch (JRException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void handleDone() {
        try {

            String sql = "UPDATE transaksi_jual SET Total = " + txTotalBayar.getText() + ", "
                    + "bayar = " + txBayar.getText() + " WHERE JualID = " + currentTransaction;
            pst = conn.prepareStatement(sql);
            System.out.println(sql);
            pst.executeUpdate();

            sql = "INSERT INTO transaksi_jual VALUES (NULL, 3, NULL, NOW(), NULL, 0);";
            pst = conn.prepareStatement(sql);
            System.out.println(sql);

            pst.executeUpdate();

            showPDF(currentTransaction);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabelT = new javax.swing.JTable();
        txTotalBayar = new javax.swing.JTextField();
        txBayar = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txKembalian = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txJmlhBarang = new javax.swing.JTextField();
        txHarga = new javax.swing.JTextField();
        txDiskon = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        btntambah = new javax.swing.JButton();
        cbNamaBarang = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        txTanggal = new javax.swing.JTextField();
        satuan = new javax.swing.JLabel();
        txIDBarang = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txNoTransaksi = new javax.swing.JTextField();
        btnhapus = new javax.swing.JButton();
        btnDone = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1280, 720));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(91, 141, 215));
        jPanel1.setPreferredSize(new java.awt.Dimension(438, 1096));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/kasir/Group 2.png"))); // NOI18N

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/Logout.png"))); // NOI18N
        jButton9.setBorderPainted(false);
        jButton9.setContentAreaFilled(false);
        jButton9.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/Logoutpressed.png"))); // NOI18N
        jButton9.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/TOBAKU/icon/Logoutselect.png"))); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 544, Short.MAX_VALUE)
                .addComponent(jButton9)
                .addGap(48, 48, 48))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 720));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1005, 69));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1180, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 69, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(247, 0, 1180, -1));

        jPanel3.setBackground(new java.awt.Color(240, 243, 252));
        jPanel3.setPreferredSize(new java.awt.Dimension(1020, 640));

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

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setText("Bayar :");

        txKembalian.setEnabled(false);
        txKembalian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txKembalianActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel6.setText("Kembalian :");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel7.setText("Total :");

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

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Nama Barang :");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Jumlah :");

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Satuan :");

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Harga :");

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Diskon :");

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

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setText("Tanggal :");

        txTanggal.setEnabled(false);
        txTanggal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txTanggalActionPerformed(evt);
            }
        });

        satuan.setForeground(new java.awt.Color(255, 255, 255));
        satuan.setText("Ini adalah satuan");

        txIDBarang.setEnabled(false);
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

        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("id barang:");

        jLabel4.setText("No. Transaksi :");

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
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txIDBarang, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(102, 102, 102)
                                .addComponent(jLabel10)
                                .addGap(21, 21, 21))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9)
                                .addGap(22, 22, 22)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cbNamaBarang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txJmlhBarang, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel4)
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
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(satuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel12))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txHarga, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(79, 79, 79))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
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
                        .addComponent(jLabel4)
                        .addComponent(txNoTransaksi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txTanggal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel11)
                        .addComponent(satuan))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txIDBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txDiskon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbNamaBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txJmlhBarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(56, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addComponent(btnhapus)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7)
                            .addGap(18, 18, 18)
                            .addComponent(txTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(18, 18, 18)
                                        .addComponent(txKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(18, 18, 18)
                                        .addComponent(txBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(126, 126, 126)
                                    .addComponent(btnDone, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 967, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txTotalBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnhapus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txBayar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txKembalian, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDone)
                .addGap(44, 44, 44))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(247, 72, 1070, 650));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txTotalBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txTotalBayarActionPerformed
        // TODO add your handling code here:
        totalBiaya();
    }//GEN-LAST:event_txTotalBayarActionPerformed

    private void txKembalianActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txKembalianActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txKembalianActionPerformed

    private void txBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txBayarActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txBayarActionPerformed

    private void txJmlhBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txJmlhBarangActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txJmlhBarangActionPerformed

    private void txDiskonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txDiskonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txDiskonActionPerformed

    private void btntambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btntambahActionPerformed
        // TODO add your handling code here:
        insertBarang();
    }//GEN-LAST:event_btntambahActionPerformed

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

    private void cbNamaBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbNamaBarangActionPerformed

        if (cbNamaBarang.getSelectedIndex() == 0) {
            txIDBarang.setText("");
        } else {
            loadTextFieldData();
        }

    }//GEN-LAST:event_cbNamaBarangActionPerformed

    private void txTanggalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txTanggalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txTanggalActionPerformed

    private void cbNamaBarangPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_cbNamaBarangPropertyChange
        // TODO add your handling code here:

    }//GEN-LAST:event_cbNamaBarangPropertyChange

    private void cbNamaBarangPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_cbNamaBarangPopupMenuWillBecomeInvisible
        // TODO add your handling code here:
        onChangeNamaBarang((String) cbNamaBarang.getSelectedItem());
    }//GEN-LAST:event_cbNamaBarangPopupMenuWillBecomeInvisible

    private void txJmlhBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txJmlhBarangKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txJmlhBarangKeyReleased

    private void txIDBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txIDBarangActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txIDBarangActionPerformed

    private void txIDBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txIDBarangKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txIDBarangKeyReleased

    private void txHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txHargaActionPerformed

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDoneActionPerformed
        // TODO add your handling code here:
        if (!txBayar.getText().isEmpty() && !txBayar.getText().equals("0")) {
            handleBayar();
        }

    }//GEN-LAST:event_btnDoneActionPerformed

    private void txBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txBayarKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txBayarKeyReleased

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        int result = JOptionPane.showConfirmDialog(this, "Apakah Anda ingin logout?", "Konfirmasi Logout", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            login lg = new login();
            lg.setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void txNoTransaksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txNoTransaksiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txNoTransaksiActionPerformed

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
            java.util.logging.Logger.getLogger(Kasir1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Kasir1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Kasir1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Kasir1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Kasir1().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(Kasir1.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Kasir1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDone;
    private javax.swing.JButton btnhapus;
    private javax.swing.JButton btntambah;
    private javax.swing.JComboBox<String> cbNamaBarang;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTabelT;
    private javax.swing.JLabel satuan;
    private javax.swing.JTextField txBayar;
    private javax.swing.JTextField txDiskon;
    private javax.swing.JTextField txHarga;
    private javax.swing.JTextField txIDBarang;
    private javax.swing.JTextField txJmlhBarang;
    private javax.swing.JTextField txKembalian;
    private javax.swing.JTextField txNoTransaksi;
    private javax.swing.JTextField txTanggal;
    private javax.swing.JTextField txTotalBayar;
    // End of variables declaration//GEN-END:variables

}
