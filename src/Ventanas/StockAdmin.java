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
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author facundolorenzo
 */
public class StockAdmin extends javax.swing.JFrame {

    private static String correo;

    /**
     * Creates new form ControlStockAdmin
     */
    public StockAdmin(String correo) {
        initComponents();
        this.correo = correo;
        setLocationRelativeTo(null);
        setTitle("Control Stock - Sistema Administrador - Panaderia Gloria");
        actualizarStock();
        
    }

    /**
     * Metodos funcionales.
     */
    
    private static void reporteStock() throws SQLException, FileNotFoundException, IOException {

        Workbook book = new XSSFWorkbook(); //se crea un libro
        Sheet sheet = book.createSheet("Productos"); //se crea una hoja de calculos llamada "Productos"

        try {
            //se crean las cabeceras de la tabla en un arreglo de strings
            String[] cabecera = new String[]{"id", "nombre", "cantidad", "precio"}; //luego probar con otros nombres
            
            
//            CellStyle headerStyle = book.createCellStyle();
//            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
//            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            headerStyle.setBorderBottom(BorderStyle.THIN);
//            headerStyle.setBorderLeft(BorderStyle.THIN);
//            headerStyle.setBorderRight(BorderStyle.THIN);
//            headerStyle.setBorderBottom(BorderStyle.THIN);

//            Font font = book.createFont();
//            font.setFontName("Arial");
//            font.setBold(true);
//            font.setColor(IndexedColors.WHITE.getIndex());
//            font.setFontHeightInPoints((short) 12);
//            headerStyle.setFont(font);

            Row filaEncabezados = sheet.createRow(4);

            for (int i = 0; i < cabecera.length; i++) {
                Cell celdaEnzabezado = filaEncabezados.createCell(i);
//                celdaEnzabezado.setCellStyle(headerStyle);
                celdaEnzabezado.setCellValue(cabecera[i]);
            }
            //tooooodo eso para crar las cabeceras de las tablas

            
            
            //ahora vamos a llenar la tabla desde la base de datos
            Connection conn = Conexion.conectar();
            
            int numFilaDatos = 4; //cantidad de filas de datos

//            CellStyle datosEstilo = book.createCellStyle();
//            datosEstilo.setBorderBottom(BorderStyle.THIN);
//            datosEstilo.setBorderLeft(BorderStyle.THIN);
//            datosEstilo.setBorderRight(BorderStyle.THIN);
//            datosEstilo.setBorderBottom(BorderStyle.THIN);

            PreparedStatement ps = conn.prepareStatement("SELECT id, nombre, cantidad, precio FROM stock");
            ResultSet rs = ps.executeQuery();

            int numCol = rs.getMetaData().getColumnCount(); //numero de columnas de la bdd.

            while (rs.next()) {
                Row filaDatos = sheet.createRow(numFilaDatos); //crea una fila por cada tipo de dato.

                for (int i = 1; i < numCol; i++) {

                    Cell CeldaDatos = filaDatos.createCell(i); //crea una celda
//                    CeldaDatos.setCellStyle(datosEstilo);

                    if (i == 1) {
                        CeldaDatos.setCellValue(rs.getInt("id")); //obtiene el id de la bdd.
                    } else if(i == 2){
                        CeldaDatos.setCellValue(rs.getString("nombre")); //obtiene el nombre de la bbd.
                    }
                    else {
                        CeldaDatos.setCellValue(rs.getDouble(i));
                    }
                }
                // TODO ver celda importes
//                Cell celdaImporte = filaDatos.createCell(4);
//                celdaImporte.setCellStyle(datosEstilo);
//                celdaImporte.setCellFormula(String.format("C%d+D%d", numFilaDatos + 1, numFilaDatos + 1));

                numFilaDatos++; //TODO ver

            }
            
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);

            sheet.setZoom(150);

            FileOutputStream fileOut = new FileOutputStream("ReporteStock.xlsx");
            book.write(fileOut);
            fileOut.close();
            conn.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        
    }
    
    public void filtrarDatos(String valor) {
        Connection conn = Conexion.conectar();
        String[] titulos = {"ID", "Nombre", "Cantidad", "Precio"};
        String[] registros = new String[4];
        DefaultTableModel model = new DefaultTableModel(null, titulos);
        String sql = "SELECT * FROM stock WHERE nombre LIKE '%" + valor + "%'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                registros[0] = rs.getString("id");
                registros[1] = rs.getString("nombre");
                registros[2] = rs.getString("cantidad");
                registros[3] = rs.getString("precio");
                model.addRow(registros);
            }
            jTableStock.setModel(model);
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    public void agregarStock() {
        Connection conn = Conexion.conectar();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO stock VALUES(?,?,?,?)");
            ps.setString(1, "0"); //id [int]
            ps.setString(2, jTextFieldNombre.getText()); //nombre [varchar]
            ps.setInt(3, Integer.parseInt(jTextFieldCantidad.getText().trim())); //cantidad [int]
            ps.setDouble(4, Double.parseDouble(jTextFieldPrecio.getText().trim())); //precio [decimal]
            ps.executeUpdate();
            limpiarDatos();
            JOptionPane.showMessageDialog(null, "Registro exitoso");
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    public void modificarStock() {
        try {
            String ID = jTextFieldID.getText().trim();
            Connection conn = Conexion.conectar();
            PreparedStatement ps = conn.prepareStatement("UPDATE stock SET "
                    + "nombre = ?, cantidad = ?, precio = ? WHERE ID ='" + ID + "'");
            ps.setString(1, jTextFieldNombre.getText().trim()); //nombre [varchar]
            ps.setDouble(2, Double.parseDouble(jTextFieldCantidad.getText().trim())); //cantidad [double]
            ps.setDouble(3, Double.parseDouble(jTextFieldPrecio.getText().trim())); //precio [double]
            ps.executeUpdate();
            limpiarDatos();
            JOptionPane.showMessageDialog(null, "Modificacion exitosa");
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    public void eliminarStock() {
        Connection conn = Conexion.conectar();
        int filaSelec = jTableStock.getSelectedRow();
        try {
            String sql = "DELETE FROM stock where id =" + jTableStock.getValueAt(filaSelec, 0);
            PreparedStatement ps = conn.prepareStatement(sql);
            int n = ps.executeUpdate();
            if (n >= 0) {
                JOptionPane.showMessageDialog(null, "Producto Eliminado");
            }
            limpiarDatos();
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar");
        }
    }

    public void actualizarStock() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Cantidad");
        model.addColumn("Precio $");
        jTableStock.setModel(model);
        String[] datos = new String[4];
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM stock");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                datos[0] = rs.getString(1);//id [int]
                datos[1] = rs.getString(2);//nombre [varchar]
                datos[2] = rs.getInt(3) + "";//cantidad[int]
                datos[3] = rs.getDouble(4) + "";//precio[decimal]
                model.addRow(datos);
            }
            limpiarDatos();
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "" + e.toString());
        }
    }

    public void limpiarDatos() {
        jTextFieldID.setText("id");
        jTextFieldNombre.setText("nombre");
        jTextFieldCantidad.setText("cantidad");
        jTextFieldPrecio.setText("precio");
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
        jTextFieldCantidad = new javax.swing.JTextField();
        jTextFieldID = new javax.swing.JTextField();
        jTextFieldNombre = new javax.swing.JTextField();
        jButtonAgregar = new javax.swing.JButton();
        jButtonModificar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableStock = new javax.swing.JTable();
        jButtonEliminar = new javax.swing.JButton();
        jTextFieldPrecio = new javax.swing.JTextField();
        jButtonReporte = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setBackground(new java.awt.Color(153, 255, 255));
        jLabel4.setFont(new java.awt.Font("Kefa", 1, 24)); // NOI18N
        jLabel4.setForeground(java.awt.Color.lightGray);
        jLabel4.setText("Control de Stock");

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
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonSalir)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextFieldCantidad.setForeground(java.awt.Color.lightGray);
        jTextFieldCantidad.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldCantidad.setText("Cantidad");
        jTextFieldCantidad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldCantidadMouseClicked(evt);
            }
        });
        jTextFieldCantidad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCantidadKeyTyped(evt);
            }
        });

        jTextFieldID.setForeground(java.awt.Color.lightGray);
        jTextFieldID.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldID.setText("ID");
        jTextFieldID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldIDMouseClicked(evt);
            }
        });
        jTextFieldID.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldIDKeyTyped(evt);
            }
        });

        jTextFieldNombre.setForeground(java.awt.Color.lightGray);
        jTextFieldNombre.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldNombre.setText("Nombre");
        jTextFieldNombre.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldNombreMouseClicked(evt);
            }
        });
        jTextFieldNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldNombreKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNombreKeyTyped(evt);
            }
        });

        jButtonAgregar.setText("Agregar");
        jButtonAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAgregarActionPerformed(evt);
            }
        });

        jButtonModificar.setText("Modificar");
        jButtonModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModificarActionPerformed(evt);
            }
        });

        jTableStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableStock.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableStockMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableStock);

        jButtonEliminar.setText("Eliminar");
        jButtonEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEliminarActionPerformed(evt);
            }
        });

        jTextFieldPrecio.setForeground(java.awt.Color.lightGray);
        jTextFieldPrecio.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldPrecio.setText("Precio");
        jTextFieldPrecio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldPrecioMouseClicked(evt);
            }
        });
        jTextFieldPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldPrecioKeyTyped(evt);
            }
        });

        jButtonReporte.setText("Reporte");
        jButtonReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonReporteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jTextFieldPrecio, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextFieldID, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextFieldNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                    .addComponent(jButtonReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 847, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextFieldID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(272, 272, 272)
                        .addComponent(jButtonAgregar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonReporte, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonModificar, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonEliminar))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Botones funcionales.
    private void jButtonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalirActionPerformed
        this.dispose();
        SistemaAdministrador sistAdmin = new SistemaAdministrador(correo);
        sistAdmin.setVisible(true);
    }//GEN-LAST:event_jButtonSalirActionPerformed

    private void jButtonAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAgregarActionPerformed
        agregarStock();
        actualizarStock();
    }//GEN-LAST:event_jButtonAgregarActionPerformed

    private void jTextFieldIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldIDMouseClicked
        jTextFieldID.setText("");
    }//GEN-LAST:event_jTextFieldIDMouseClicked

    private void jTextFieldNombreMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldNombreMouseClicked
        jTextFieldNombre.setText("");
    }//GEN-LAST:event_jTextFieldNombreMouseClicked

    private void jTextFieldCantidadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldCantidadMouseClicked
        jTextFieldCantidad.setText("");
    }//GEN-LAST:event_jTextFieldCantidadMouseClicked

    private void jTextFieldPrecioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldPrecioMouseClicked
        jTextFieldPrecio.setText("");
    }//GEN-LAST:event_jTextFieldPrecioMouseClicked

    private void jButtonModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonModificarActionPerformed
        modificarStock();
        actualizarStock();
    }//GEN-LAST:event_jButtonModificarActionPerformed

    private void jButtonEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEliminarActionPerformed
        eliminarStock();
        actualizarStock();
    }//GEN-LAST:event_jButtonEliminarActionPerformed

    //Validadores de campos
    private void jTextFieldIDKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldIDKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_jTextFieldIDKeyTyped

    private void jTextFieldCantidadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCantidadKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_jTextFieldCantidadKeyTyped

    private void jTextFieldPrecioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPrecioKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_jTextFieldPrecioKeyTyped

    private void jTextFieldNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNombreKeyTyped
        char c = evt.getKeyChar();
        if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
            evt.consume();
    }//GEN-LAST:event_jTextFieldNombreKeyTyped

    private void jTextFieldNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNombreKeyReleased
        this.filtrarDatos(jTextFieldNombre.getText().trim());
    }//GEN-LAST:event_jTextFieldNombreKeyReleased

    private void jTableStockMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableStockMouseClicked
        int filaSelec = jTableStock.rowAtPoint(evt.getPoint());
        // id nombre cantidad precio
        jTextFieldID.setText(jTableStock.getValueAt(filaSelec, 0).toString());
        jTextFieldNombre.setText(jTableStock.getValueAt(filaSelec, 1).toString());
        jTextFieldCantidad.setText(jTableStock.getValueAt(filaSelec, 2).toString());
        jTextFieldPrecio.setText(jTableStock.getValueAt(filaSelec, 3).toString());
    }//GEN-LAST:event_jTableStockMouseClicked

    private void jButtonReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReporteActionPerformed
        try {
            reporteStock();
        } catch (SQLException ex) {
            Logger.getLogger(StockAdmin.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StockAdmin.class.getName()).log(Level.SEVERE, null, ex);
        }
        actualizarStock();
    }//GEN-LAST:event_jButtonReporteActionPerformed

    
    
    public static void main(String[] args) throws SQLException, IOException {
        reporteStock();
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonAgregar;
    private javax.swing.JButton jButtonEliminar;
    private javax.swing.JButton jButtonModificar;
    private javax.swing.JButton jButtonReporte;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableStock;
    private javax.swing.JTextField jTextFieldCantidad;
    private javax.swing.JTextField jTextFieldID;
    private javax.swing.JTextField jTextFieldNombre;
    private javax.swing.JTextField jTextFieldPrecio;
    // End of variables declaration//GEN-END:variables
}
