package TOBAKU.koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class koneksi {

    private static Connection koneksi;

    public static Connection getKoneksi() {
        try {
            if (koneksi == null || koneksi.isClosed()) {
                koneksi = DriverManager.getConnection("jdbc:mysql://localhost/tobakuu", "root", "");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database Tidak Terhubung: " + ex.getMessage());
            return null;
        }
        return koneksi;
    }

    public static void closeKoneksi() {
        if (koneksi != null) {
            try {
                koneksi.close();
                koneksi = null;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Gagal menutup koneksi: " + ex.getMessage());
            }
        }
    }
}
