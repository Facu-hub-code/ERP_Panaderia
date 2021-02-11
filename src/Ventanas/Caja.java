/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ventanas;

import Conexion.Conexion;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
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
public class Caja extends javax.swing.JFrame {

    private static String correo;
    private static String ID;

    /**
     * Creates new form ControlCajaAdmin
     */
    public Caja(String correo) {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Control de Caja - Sistema - Panaderia Gral. Paz");
        this.correo = correo;
        actualizar();
    }
    /**
     * Escribe actualiza los campos de texto donde figuran montos.
     */
    public void writeLabels(){
        jLabelTotal.setText("Total: " + calcularTotal() + " $");
        jLabelDinero.setText("Dinero en caja: " + calcularEfectivo() + " $");
    }
    
    /**
     * Actualiza la fecha del JDateChooser por c/vez que se invoca.
     */
    public void actualizarFecha() {
        Calendar fechaActual = new GregorianCalendar();
        jDateChooserFecha.setCalendar(fechaActual);
    }
    
    /**
     * Actualiza el estado de la interface luego de ejecutar algun metodo.
     */
    public void actualizar() {
        actualizarFecha();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Monto");
        model.addColumn("Concepto");
        model.addColumn("Usuario");
        model.addColumn("Efectivo");
        jTableCaja.setModel(model);
        String[] datos = new String[6];
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
                datos[5] = rs.getBoolean("efectivo")+"";
                model.addRow(datos);
            }
            cn.close();
            writeLabels();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    public void vaciarTablaCaja(){
        try {
            Connection cn = Conexion.conectar();
        PreparedStatement ps = cn.prepareStatement("TRUNCATE TABLE caja");
        if(ps.execute()) JOptionPane.showMessageDialog(null, "Tabla Caja vacia, agregar dinero para el sgte turno");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
    }
    
    /**
     * Crea un nuevo reporte en formato .xlxs ingresando en cada celda el valor
     * correspondiente que se encuentre en la tabla "caja" de la base de datos
     * "panaderia".
     */
    public static void reporte() throws SQLException, FileNotFoundException, IOException {
        //Se crea una hoja de calculos.
        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet("Caja");
        try {
            // <editor-fold defaultstate="collapsed" desc="Estetica de la hoja de calculos">

            CreationHelper help = book.getCreationHelper();
            Drawing draw = sheet.createDrawingPatriarch();

            ClientAnchor anchor = help.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(1);

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
            String[] cabecera = new String[]{"Id", "Fecha", "Monto", "Concepto", "Usuario", "Efectivo"};

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
            Row filaEncabezados = sheet.createRow(5);

            for (int i = 0; i < cabecera.length; i++) {
                Cell celdaEnzabezado = filaEncabezados.createCell(i);
                celdaEnzabezado.setCellStyle(headerStyle);
                celdaEnzabezado.setCellValue(cabecera[i]);
            }

            Connection conn = Conexion.conectar();
            PreparedStatement ps;
            ResultSet rs;

            //modificar segun la cantidad de datos.
            int numFilaDatos = 6;

            CellStyle datosEstilo = book.createCellStyle();
            datosEstilo.setBorderBottom(BorderStyle.THIN);
            datosEstilo.setBorderLeft(BorderStyle.THIN);
            datosEstilo.setBorderRight(BorderStyle.THIN);
            datosEstilo.setBorderBottom(BorderStyle.THIN);

            // </editor-fold>
            ps = conn.prepareStatement("SELECT * FROM caja");
            rs = ps.executeQuery();

            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos);

                for (int a = 0; a < rs.getMetaData().getColumnCount(); a++) {

                    Cell CeldaDatos = filaDatos.createCell(a);
                    CeldaDatos.setCellStyle(datosEstilo);

                    if (a == 0) {
                        CeldaDatos.setCellValue(rs.getInt(a + 1));
                    } else {
                        CeldaDatos.setCellValue(rs.getString(a + 1));
                    }
                }

                numFilaDatos++;

            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);

            sheet.setZoom(150);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date fecha = new Date();
            String F = sdf.format(fecha);

            FileOutputStream fileOut = new FileOutputStream("Caja: " + F + ".xlsx");
            book.write(fileOut);
            fileOut.close();
            JOptionPane.showMessageDialog(null, "Reporte guardado con el nombre de la fecha.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    /**
     * Modifica cierto valor de la tabla "caja" de la base de datos "panaderia".
     * Lo realiza mediante el ID tomado de la interface.
     */
    public void modificar() {
        try {
            Connection conn = Conexion.conectar();
            PreparedStatement ps = conn.prepareStatement("UPDATE caja SET "
                    + "fecha = ?, monto = ?, concepto = ?, usuario = ?, efectivo = ? WHERE ID ='" + ID + "'");
            ps.setString(1, ((JTextField) jDateChooserFecha.getDateEditor().getUiComponent()).getText());
            ps.setDouble(2, Double.parseDouble(jTextFieldMonto.getText().trim()));
            ps.setString(3, jComboBoxConcepto.getSelectedItem().toString());
            ps.setString(4, "" + correo);
            ps.setBoolean(5, jRadioButtonEfectivo.isSelected());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Modificacion exitosa");
            conn.close();
            actualizar();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la modificacion, por favor intente"
                    + "nuevamente o comuniquese con el administrador.");
        }
    }

    /**
     * Agrega un movimiento a la base de datos y luego calcula el total en el
     * jLabelTotal.
     */
    public void agregarMovimiento() {
        Connection conn = Conexion.conectar();
        //actualizarFecha();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO caja VALUES(?,?,?,?,?,?)");
            ps.setString(1, "0"); //id
            ps.setString(2, ((JTextField) jDateChooserFecha.getDateEditor().getUiComponent()).getText()); //fecha
            ps.setDouble(3, Double.parseDouble(jTextFieldMonto.getText().trim())); //monto
            ps.setString(4, jComboBoxConcepto.getSelectedItem().toString()); //concepto
            ps.setString(5, correo + ""); //usuario
            ps.setBoolean(6, jRadioButtonEfectivo.isSelected());
            ps.executeUpdate();
            
            jTextFieldMonto.setText("Monto");
            JOptionPane.showMessageDialog(null, "Registro exitoso");
            conn.close();
            jLabelTotal.setText("Total: " + calcularTotal() + " $");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    /**
     * Calcula el dinero total de la caja.
     */
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

    public double calcularEfectivo() {
        double efectivo = 0;
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * from caja");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getBoolean("efectivo")) {
                    if ((rs.getString("concepto")).equalsIgnoreCase("Ingreso")) {
                        efectivo += rs.getDouble("monto");
                    } else if ((rs.getString("concepto")).equalsIgnoreCase("Egreso")) {
                        efectivo -= rs.getDouble("monto");
                    }
                }
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return efectivo;
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
        jPanel2 = new javax.swing.JPanel();
        jTextFieldMonto = new javax.swing.JTextField();
        jButtonAgregar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableCaja = new javax.swing.JTable();
        jComboBoxConcepto = new javax.swing.JComboBox<>();
        jButtonReporte = new javax.swing.JButton();
        jLabelTotal = new javax.swing.JLabel();
        jDateChooserFecha = new com.toedter.calendar.JDateChooser();
        jButtonModificar = new javax.swing.JButton();
        jLabelDinero = new javax.swing.JLabel();
        jRadioButtonEfectivo = new javax.swing.JRadioButton();

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
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSalir)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonSalir))
                .addGap(0, 6, Short.MAX_VALUE))
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

        jButtonReporte.setText("Reporte");
        jButtonReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReporteActionPerformed(evt);
            }
        });

        jLabelTotal.setBackground(new java.awt.Color(255, 255, 255));
        jLabelTotal.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(255, 255, 255));
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelTotal.setText("TOTAL:");

        jDateChooserFecha.setDateFormatString("yyyy/MM/dd HH:mm:ss");

        jButtonModificar.setText("Modificar");
        jButtonModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModificarActionPerformed(evt);
            }
        });

        jLabelDinero.setBackground(new java.awt.Color(255, 255, 255));
        jLabelDinero.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabelDinero.setForeground(new java.awt.Color(255, 255, 255));
        jLabelDinero.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelDinero.setText("Dinero en caja:");

        jRadioButtonEfectivo.setText("Efectivo");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButtonAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextFieldMonto)
                            .addComponent(jDateChooserFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBoxConcepto, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelDinero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jRadioButtonEfectivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jButtonReporte, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
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
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButtonEfectivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonReporte)
                        .addGap(61, 61, 61)
                        .addComponent(jLabelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabelDinero, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        SistemaPrincipal sistAdmin = new SistemaPrincipal(correo);
        sistAdmin.setVisible(true);
    }//GEN-LAST:event_jButtonSalirActionPerformed

    //Limpia el campo de texto Monto.
    private void jTextFieldMontoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldMontoMouseClicked
        jTextFieldMonto.setText("");
    }//GEN-LAST:event_jTextFieldMontoMouseClicked

    private void jButtonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarActionPerformed
        agregarMovimiento();
        actualizar();
    }//GEN-LAST:event_jButtonAgregarActionPerformed

    //Evita ingresar datos incorrectos(solo permite nros).
    private void jTextFieldMontoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldMontoKeyTyped
        char c = evt.getKeyChar();
        if (c != '.') {
            if (c < '0' || c > '9') {
                evt.consume();
            }
        }
    }//GEN-LAST:event_jTextFieldMontoKeyTyped

    private void jButtonModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModificarActionPerformed
        modificar();
        actualizar();
    }//GEN-LAST:event_jButtonModificarActionPerformed

    //Selecciona una fila de la tabla Caja.
    private void jTableCajaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableCajaMouseClicked
        int filaSelec = jTableCaja.rowAtPoint(evt.getPoint());
        ID = jTableCaja.getValueAt(filaSelec, 0).toString();
    }//GEN-LAST:event_jTableCajaMouseClicked

    private void jButtonReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReporteActionPerformed
        try {
            reporte();
        } catch (SQLException | IOException ex) {
            Logger.getLogger(Caja.class.getName()).log(Level.SEVERE, null, ex);
        }
        vaciarTablaCaja();
        actualizar();
    }//GEN-LAST:event_jButtonReporteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAgregar;
    private javax.swing.JButton jButtonModificar;
    private javax.swing.JButton jButtonReporte;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JComboBox<String> jComboBoxConcepto;
    private com.toedter.calendar.JDateChooser jDateChooserFecha;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelDinero;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButtonEfectivo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableCaja;
    private javax.swing.JTextField jTextFieldMonto;
    // End of variables declaration//GEN-END:variables
}
