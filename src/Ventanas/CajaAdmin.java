/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ventanas;

import Conexion.Conexion;
import com.toedter.calendar.JDateChooserBeanInfo;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author facundolorenzo
 */
public class CajaAdmin extends javax.swing.JFrame {

    private static String correo;
    private static String ID;

    /**
     * Creates new form ControlCajaAdmin
     */
    public CajaAdmin(String correo) {
        initComponents();
        this.correo = correo;
        jLabelUser.setText(correo);
        jLabelTotal.setText("Total: " + calcularTotal() + " $");
        setLocationRelativeTo(null);
        setTitle("Control de Caja - Sistema Administrador - Panaderia Gloria");
        actualizarFecha();
        actualizar();
    }

    /**
     * Metodos funcionales.
     */
    
    private static void reporte() throws SQLException, FileNotFoundException, IOException {

        //Se crea una hoja de calculos.
        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Caja");

        try {
            
            //Se crea y se maquilla la celda del titulo.
            CellStyle tituloEstilo = book.createCellStyle();
            //Se centra el texto.
            tituloEstilo.setAlignment(HorizontalAlignment.CENTER);
            tituloEstilo.setVerticalAlignment(VerticalAlignment.CENTER);
            //Establece el estilo de la fuente.
            Font fuenteTitulo = book.createFont();
            fuenteTitulo.setFontName("Arial");
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            tituloEstilo.setFont(fuenteTitulo);
            //Crea una fila para los titulos.
            Row filaTitulo = sheet.createRow(1);
            Cell celdaTitulo = filaTitulo.createCell(1);
            celdaTitulo.setCellStyle(tituloEstilo);
            celdaTitulo.setCellValue("Reporte de Caja");
            sheet.addMergedRegion(new CellRangeAddress(1, 2, 1, 3)); //resize

            //se crean las cabeceras de la tabla
            String[] cabecera = new String[]{"Id", "Fecha", "Monto", "Concepto", "Usuario"};

            CellStyle headerStyle = book.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            Font font = book.createFont();
            font.setFontName("Arial");
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 12);
            headerStyle.setFont(font);
            //cambiar segun cantidad de datos
            Row filaEncabezados = sheet.createRow(4);

            for (int i = 0; i < cabecera.length; i++) {
                Cell celdaEnzabezado = filaEncabezados.createCell(i);
                celdaEnzabezado.setCellStyle(headerStyle);
                celdaEnzabezado.setCellValue(cabecera[i]);
            }
            
            Connection conn = Conexion.conectar();
            PreparedStatement ps;
            ResultSet rs;

            //modificar segun la cantidad de datos.
            int numFilaDatos = 5;
            
            CellStyle datosEstilo = book.createCellStyle();
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            datosEstilo.setBorderLeft(BorderStyle.THIN);
            datosEstilo.setBorderRight(BorderStyle.THIN);
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            
            ps = conn.prepareStatement("SELECT id, fecha, monto, concepto, usuario FROM caja");
            rs = ps.executeQuery();

            int numCol = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos);

                for (int a = 0; a < numCol; a++) {

                    Cell CeldaDatos = filaDatos.createCell(a);
                    CeldaDatos.setCellStyle(datosEstilo);

                    if (a == 0) {
                        CeldaDatos.setCellValue(rs.getInt(a + 1));
                    } else {
                        CeldaDatos.setCellValue(rs.getString(a + 1));
                    }
                }
//                Cell celdaImporte = filaDatos.createCell(4);
//                celdaImporte.setCellStyle(datosEstilo);
//                celdaImporte.setCellFormula(String.format("C%d+D%d", numFilaDatos + 1, numFilaDatos + 1));

                numFilaDatos++;

            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);

            sheet.setZoom(150);

            FileOutputStream fileOut = new FileOutputStream("ReporteCaja.xlsx");
            book.write(fileOut);
            fileOut.close();
            JOptionPane.showMessageDialog(null, "Reporte de Caja guardado");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    public void actualizarFecha() {
        Calendar fechaActual = new GregorianCalendar();
        jDateChooserFecha.setCalendar(fechaActual);
    }

    public void agregarMovimiento() {
        Connection conn = Conexion.conectar();
        //actualizarFecha();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO caja VALUES(?,?,?,?, ?)");
            ps.setString(1, "0"); //id
            ps.setString(2, ((JTextField) jDateChooserFecha.getDateEditor().getUiComponent()).getText()); //fecha
            ps.setDouble(3, Double.parseDouble(jTextFieldMonto.getText().trim())); //monto
            ps.setString(4, jComboBoxConcepto.getSelectedItem().toString()); //concepto
            ps.setString(5, correo + ""); //usuario
            ps.executeUpdate();
            jTextFieldMonto.setText("Monto");
            JOptionPane.showMessageDialog(null, "Registro exitoso");
            conn.close();
            jLabelTotal.setText("Total: " + calcularTotal() + " $");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    public double calcularTotal() {
        double total = 0;
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * from caja");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if ((rs.getString("concepto")).equalsIgnoreCase("Ingreso")) {
                    total += rs.getDouble("monto");
                } else if ((rs.getString("concepto")).equalsIgnoreCase("Egreso")) {
                    total -= rs.getDouble("monto");
                }
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return total;
    }

    public void actualizar() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("fecha");
        model.addColumn("monto");
        model.addColumn("concepto");
        model.addColumn("usuario");
        jTableCaja.setModel(model);
        String[] datos = new String[5];
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM caja");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                datos[0] = rs.getString(1);//numero
                datos[1] = rs.getDate("fecha").toString();//fecha
                datos[2] = rs.getDouble(3) + "";//monto
                datos[3] = rs.getString(4);//concepto
                datos[4] = rs.getString(5);//usuario
                model.addRow(datos);
            }
            cn.close();
            jLabelTotal.setText("Total: " + calcularTotal() + " $");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    public void limpiar() {
        jComboBoxConcepto.setSelectedItem(null);
        jTextFieldMonto.setText("Monto");
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
        jLabel4 = new javax.swing.JLabel();
        jButtonSalir = new javax.swing.JButton();
        jLabelUser = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTextFieldMonto = new javax.swing.JTextField();
        jButtonAgregar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCaja = new javax.swing.JTable();
        jComboBoxConcepto = new javax.swing.JComboBox<>();
        jButtonActualizar1 = new javax.swing.JButton();
        jLabelTotal = new javax.swing.JLabel();
        jDateChooserFecha = new com.toedter.calendar.JDateChooser();
        jButtonModificar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setBackground(new java.awt.Color(153, 255, 255));
        jLabel4.setFont(new java.awt.Font("Kefa", 1, 24)); // NOI18N
        jLabel4.setForeground(java.awt.Color.lightGray);
        jLabel4.setText("Control de Caja");

        jButtonSalir.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jButtonSalir.setText("Salir");
        jButtonSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelUser, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButtonSalir)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonSalir))
                        .addGap(0, 6, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabelUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextFieldMonto.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jTextFieldMonto.setForeground(java.awt.Color.lightGray);
        jTextFieldMonto.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldMonto.setText("Monto");
        jTextFieldMonto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldMontoMouseClicked(evt);
            }
        });
        jTextFieldMonto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldMontoKeyTyped(evt);
            }
        });

        jButtonAgregar.setText("Agregar");
        jButtonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarActionPerformed(evt);
            }
        });

        jTableCaja.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableCaja.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableCajaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableCaja);

        jComboBoxConcepto.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jComboBoxConcepto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ingreso", "Egreso" }));

        jButtonActualizar1.setText("Reporte");
        jButtonActualizar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActualizar1ActionPerformed(evt);
            }
        });

        jLabelTotal.setBackground(new java.awt.Color(255, 255, 255));
        jLabelTotal.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTotal.setText("TOTAL:");

        jDateChooserFecha.setDateFormatString("yyyy/MM/dd HH:mm:ss");

        jButtonModificar.setText("Modificar");
        jButtonModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModificarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextFieldMonto)
                    .addComponent(jDateChooserFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBoxConcepto, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonActualizar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButtonModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 830, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBoxConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooserFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonModificar)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonAgregar)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonActualizar1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(5, 5, 5))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Funcionalidad
     */
    private void jButtonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalirActionPerformed
        this.dispose();
        SistemaAdministrador sistAdmin = new SistemaAdministrador(correo);
        sistAdmin.setVisible(true);
    }//GEN-LAST:event_jButtonSalirActionPerformed

    private void jTextFieldMontoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldMontoMouseClicked
        jTextFieldMonto.setText("");
    }//GEN-LAST:event_jTextFieldMontoMouseClicked

    private void jButtonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarActionPerformed
        agregarMovimiento();
        actualizar();
    }//GEN-LAST:event_jButtonAgregarActionPerformed

    private void jTextFieldMontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMontoKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_jTextFieldMontoKeyTyped

    private void jButtonModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModificarActionPerformed
        try {
            Connection conn = Conexion.conectar();
            //ID: 1-fecha 2-monto 3-concepto 4-usuario
            PreparedStatement ps = conn.prepareStatement("UPDATE caja SET "
                    + "fecha = ?, monto = ?, concepto = ?, usuario = ? WHERE ID ='" + ID + "'");
            ps.setString(1, ((JTextField) jDateChooserFecha.getDateEditor().getUiComponent()).getText());
            ps.setDouble(2, Double.parseDouble(jTextFieldMonto.getText().trim())); 
            ps.setString(3, jComboBoxConcepto.getSelectedItem().toString());
            ps.setString(4, ""+correo);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Modificacion exitosa");
            conn.close();
            actualizar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
    }//GEN-LAST:event_jButtonModificarActionPerformed
    }
    private void jTableCajaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCajaMouseClicked
        int filaSelec = jTableCaja.rowAtPoint(evt.getPoint());
        ID = jTableCaja.getValueAt(filaSelec, 0).toString();
    }//GEN-LAST:event_jTableCajaMouseClicked

    private void jButtonActualizar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualizar1ActionPerformed
        try {
            reporte();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(CajaAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        actualizar();
    }//GEN-LAST:event_jButtonActualizar1ActionPerformed

     public static void main(String[] args) throws SQLException, IOException {
        reporte();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActualizar1;
    private javax.swing.JButton jButtonAgregar;
    private javax.swing.JButton jButtonModificar;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JComboBox<String> jComboBoxConcepto;
    private com.toedter.calendar.JDateChooser jDateChooserFecha;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCaja;
    private javax.swing.JTextField jTextFieldMonto;
    // End of variables declaration//GEN-END:variables
}
