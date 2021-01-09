/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ventanas;

import Conexion.Conexion;
import Recursos.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author facundolorenzo
 */
public class VentasAdmin extends javax.swing.JFrame {

    private static String correo;
    private static Producto producto = new Producto();

    /**
     * Creates new form SistemaVentasAdmin
     */
    public VentasAdmin(String correo) {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Control de Ventas - Sistema Administrador - Panaderia Gloria");
        this.correo = correo;
        actualizarProductos();
        actualizarFecha();
    }

    /**
     * Metodos que definen la funcionalidad del sistema
     */
    public void actualizarFecha(){
        Calendar fechaActual = new GregorianCalendar();
        jDateChooserFechaProductos.setCalendar(fechaActual);
    }
    
    public void actualizarProductos() {
        actualizarFecha();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("nombre");
        model.addColumn("precio");
        model.addColumn("cantidad");
        model.addColumn("total");
        model.addColumn("hora");
        model.addColumn("tarjeta");
        jTableVentasGeneral.setModel(model);
        String[] datos = new String[8];
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM VentasGeneral");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                datos[0] = rs.getString(1); //id
                datos[1] = rs.getString(2); //nombre
                datos[2] = rs.getDouble(3) + ""; //precio
                datos[3] = rs.getDouble(4) + ""; //cantidad
                datos[4] = rs.getDouble(5) + "";// total
                datos[5] = rs.getDate(6).toString(); //fecha
                datos[6] = rs.getBoolean(7) + ""; //tarjeta
                model.addRow(datos);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    public void actualizarHelados() {
        actualizarFecha();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("id");
        model.addColumn("nombre");
        model.addColumn("precio");
        model.addColumn("cantidad");
        model.addColumn("total");
        model.addColumn("hora");
        model.addColumn("tarjeta");
        jTableVentasHelados.setModel(model);
        String[] datos = new String[8];
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM VentasHelado");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                datos[0] = rs.getString(1); //id
                datos[1] = rs.getString(2); //nombre
                datos[2] = rs.getDouble(3) + ""; //precio
                datos[3] = rs.getDouble(4) + ""; //cantidad
                datos[4] = rs.getDouble(5) + "";// total
                datos[5] = rs.getDate(6).toString(); //fecha
                datos[6] = rs.getBoolean(7) + ""; //tarjeta
                model.addRow(datos);
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }

    public void cargarProducto(){
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM stock WHERE nombre = ?");
            ps.setString(1, jComboBoxProductos.getSelectedItem().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCantidad(rs.getDouble("cantidad"));
                producto.setPrecio(rs.getDouble("precio"));
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    public void cargarHelado(){
        Connection cn = Conexion.conectar();
        try {
            PreparedStatement ps = cn.prepareStatement("SELECT * FROM stock WHERE nombre = ?");
            ps.setString(1, jComboBoxHelados.getSelectedItem().toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                producto.setId(rs.getInt("id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setCantidad(rs.getDouble("cantidad"));
                producto.setPrecio(rs.getDouble("precio"));
            }
            cn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    public void confirmarProducto() {
        //actualizarFecha();
        cargarProducto();
        if(agregarMovimientoProducto() && modificarStockProducto()){
            agregarVentaProductos();
        }
        else{
            JOptionPane.showMessageDialog(null,"Error en la venta");
        }
        
    }
    
    public void confirmarHelado() {
        actualizarFecha();
        cargarHelado();
        if(agregarMovimientoHelado() && modificarStockHelado()){
            agregarVentaHelados();
        }
        else{
            JOptionPane.showMessageDialog(null,"Error en la venta");
        }
        
    }

    public boolean modificarStockProducto(){
        try {
            String ID = String.valueOf(producto.getId());
            Connection conn = Conexion.conectar();
            PreparedStatement ps = conn.prepareStatement("UPDATE stock SET "
                    + "nombre = ?, cantidad = ?, precio = ? WHERE ID ='" + ID + "'");
            ps.setString(1, producto.getNombre()); //nombre
            double cantidad = (producto.getCantidad())-(Double.parseDouble(jTextFieldCantidadProductos.getText().trim()));
            ps.setDouble(2, cantidad); //cantidad
            ps.setDouble(3,producto.getPrecio()); //precio
            ps.executeUpdate();
            conn.close();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
    }
    
    public boolean modificarStockHelado(){
        try {
            String ID = String.valueOf(producto.getId());
            Connection conn = Conexion.conectar();
            PreparedStatement ps = conn.prepareStatement("UPDATE stock SET "
                    + "nombre = ?, cantidad = ?, precio = ? WHERE ID ='" + ID + "'");
            ps.setString(1, producto.getNombre()); //nombre
            double cantidad = (producto.getCantidad())-(Double.parseDouble(jTextFieldCantidadHelados.getText().trim()));
            ps.setDouble(2, cantidad); //cantidad
            ps.setDouble(3,producto.getPrecio()); //precio
            ps.executeUpdate();
            conn.close();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
    }
    
    public boolean agregarMovimientoProducto(){
        Connection conn = Conexion.conectar();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO caja VALUES(?,?,?,?,?)");
            ps.setString(1, "0"); //id [int]
            ps.setString(2, ((JTextField)jDateChooserFechaProductos.getDateEditor().getUiComponent()).getText()); //fecha [Date]
            double monto = (Double.parseDouble(jTextFieldCantidadProductos.getText()))*(producto.getPrecio());
            ps.setDouble(3, monto); //monto [double]
            ps.setString(4, "Ingreso"); //concepto [varchar]
            ps.setString(5, correo+"");//usuario [varchar]
            ps.executeUpdate();
            conn.close();
            //JOptionPane.showMessageDialog(null,"Movimiento Agregado");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
        
    }
    
    public boolean agregarMovimientoHelado(){
        Connection conn = Conexion.conectar();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO caja VALUES(?,?,?,?,?)");
            ps.setString(1, "0"); //id [int]
            ps.setString(2, ((JTextField)jDateChooserFechaHelados.getDateEditor().getUiComponent()).getText()); //fecha [Date]
            double monto = (Double.parseDouble(jTextFieldCantidadHelados.getText()))*(producto.getPrecio());
            ps.setDouble(3, monto); //monto [double]
            ps.setString(4, "Ingreso"); //concepto [varchar]
            ps.setString(5, correo+"");//usuario [varchar]
            ps.executeUpdate();
            conn.close();
            JOptionPane.showMessageDialog(null,"Movimiento Agregado");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        }
        
    }
    
    public void agregarVentaProductos(){
        Connection conn = Conexion.conectar();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO ventasGeneral VALUES(?,?,?,?,?,?,?)");
            ps.setString(1, "0");
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            ps.setDouble(4, Double.parseDouble(jTextFieldCantidadProductos.getText().trim()));
            double total = (Double.parseDouble(jTextFieldCantidadProductos.getText()))*(producto.getPrecio());
            ps.setDouble(5, total);
            ps.setString(6, ((JTextField)jDateChooserFechaProductos.getDateEditor().getUiComponent()).getText()); //fecha [Date]
            ps.setBoolean(7, jRadioButtonTarjetaProductos.isSelected());
            ps.executeUpdate();
            conn.close();
            JOptionPane.showMessageDialog(null,"Venta agregada correctamente");
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
    }
    
    public void agregarVentaHelados(){
        Connection conn = Conexion.conectar();
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO ventasHelado VALUES(?,?,?,?,?,?,?)");
            ps.setString(1, "0");
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            ps.setDouble(4, Double.parseDouble(jTextFieldCantidadHelados.getText().trim()));
            double total = (Double.parseDouble(jTextFieldCantidadHelados.getText()))*(producto.getPrecio());
            ps.setDouble(5, total);
            ps.setString(6, ((JTextField)jDateChooserFechaHelados.getDateEditor().getUiComponent()).getText()); //fecha [Date]
            ps.setBoolean(7, jRadioButtonTarjetaHelados.isSelected());
            ps.executeUpdate();
            conn.close();
            JOptionPane.showMessageDialog(null,"Venta agregada correctamente");
        } catch (NumberFormatException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
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

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButtonSalir = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanelVentasGeneral = new javax.swing.JPanel();
        jTextFieldCantidadProductos = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVentasGeneral = new javax.swing.JTable();
        jComboBoxProductos = new javax.swing.JComboBox<>();
        jButtonConfirmarVentaProductos = new javax.swing.JButton();
        jRadioButtonTarjetaProductos = new javax.swing.JRadioButton();
        jDateChooserFechaProductos = new com.toedter.calendar.JDateChooser();
        jPanelVentasHelados = new javax.swing.JPanel();
        jTextFieldCantidadHelados = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableVentasHelados = new javax.swing.JTable();
        jComboBoxHelados = new javax.swing.JComboBox<>();
        jButtonConfirmarVentaHelados = new javax.swing.JButton();
        jRadioButtonTarjetaHelados = new javax.swing.JRadioButton();
        jDateChooserFechaHelados = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setBackground(new java.awt.Color(153, 255, 255));
        jLabel4.setFont(new java.awt.Font("Kefa", 1, 24)); // NOI18N
        jLabel4.setForeground(java.awt.Color.lightGray);
        jLabel4.setText("Sistema de Ventas");

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
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButtonSalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jPanelVentasGeneral.setBackground(new java.awt.Color(51, 51, 51));
        jPanelVentasGeneral.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextFieldCantidadProductos.setForeground(java.awt.Color.lightGray);
        jTextFieldCantidadProductos.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldCantidadProductos.setText("Cantidad: gramos o unidad");
        jTextFieldCantidadProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldCantidadProductosMouseClicked(evt);
            }
        });
        jTextFieldCantidadProductos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCantidadProductosKeyTyped(evt);
            }
        });

        jTableVentasGeneral.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableVentasGeneral.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableVentasGeneralMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableVentasGeneral);

        jComboBoxProductos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Factura", "Criollos", "Medias Lunas", "Palmeritas" }));

        jButtonConfirmarVentaProductos.setText("Confirmar");
        jButtonConfirmarVentaProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarVentaProductosActionPerformed(evt);
            }
        });

        jRadioButtonTarjetaProductos.setText("Tarjeta");

        jDateChooserFechaProductos.setDateFormatString("yyyy/MM/dd HH:mm:ss");

        javax.swing.GroupLayout jPanelVentasGeneralLayout = new javax.swing.GroupLayout(jPanelVentasGeneral);
        jPanelVentasGeneral.setLayout(jPanelVentasGeneralLayout);
        jPanelVentasGeneralLayout.setHorizontalGroup(
            jPanelVentasGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentasGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVentasGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelVentasGeneralLayout.createSequentialGroup()
                        .addComponent(jComboBoxProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                        .addComponent(jTextFieldCantidadProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButtonTarjetaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jDateChooserFechaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButtonConfirmarVentaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanelVentasGeneralLayout.setVerticalGroup(
            jPanelVentasGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentasGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVentasGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooserFechaProductos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelVentasGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldCantidadProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRadioButtonTarjetaProductos))
                    .addComponent(jButtonConfirmarVentaProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 608, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("VentasGeneral", jPanelVentasGeneral);

        jTextFieldCantidadHelados.setForeground(java.awt.Color.lightGray);
        jTextFieldCantidadHelados.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextFieldCantidadHelados.setText("Cantidad: gramos o unidad");
        jTextFieldCantidadHelados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextFieldCantidadHeladosMouseClicked(evt);
            }
        });
        jTextFieldCantidadHelados.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCantidadHeladosKeyTyped(evt);
            }
        });

        jTableVentasHelados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableVentasHelados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableVentasHeladosMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTableVentasHelados);

        jComboBoxHelados.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "HeladoVainilla", "HeladoFrutilla", "HeladoAlAgua", "HeladoChocolate" }));

        jButtonConfirmarVentaHelados.setText("Confirmar");
        jButtonConfirmarVentaHelados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConfirmarVentaHeladosActionPerformed(evt);
            }
        });

        jRadioButtonTarjetaHelados.setText("Tarjeta");

        jDateChooserFechaHelados.setDateFormatString("yyyy/MM/dd HH:mm:ss");

        javax.swing.GroupLayout jPanelVentasHeladosLayout = new javax.swing.GroupLayout(jPanelVentasHelados);
        jPanelVentasHelados.setLayout(jPanelVentasHeladosLayout);
        jPanelVentasHeladosLayout.setHorizontalGroup(
            jPanelVentasHeladosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentasHeladosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVentasHeladosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelVentasHeladosLayout.createSequentialGroup()
                        .addComponent(jComboBoxHelados, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextFieldCantidadHelados, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButtonTarjetaHelados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jDateChooserFechaHelados, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonConfirmarVentaHelados, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(247, 247, 247))
                    .addGroup(jPanelVentasHeladosLayout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addContainerGap())))
        );
        jPanelVentasHeladosLayout.setVerticalGroup(
            jPanelVentasHeladosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelVentasHeladosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelVentasHeladosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooserFechaHelados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanelVentasHeladosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBoxHelados, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldCantidadHelados, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jRadioButtonTarjetaHelados))
                    .addComponent(jButtonConfirmarVentaHelados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("VentasHelados", jPanelVentasHelados);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSalirActionPerformed
        this.dispose();
        SistemaAdministrador sistAdmin = new SistemaAdministrador(correo);
        sistAdmin.setVisible(true);
    }//GEN-LAST:event_jButtonSalirActionPerformed

    private void jTextFieldCantidadProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldCantidadProductosMouseClicked
        jTextFieldCantidadProductos.setText("");
    }//GEN-LAST:event_jTextFieldCantidadProductosMouseClicked

    private void jTextFieldCantidadProductosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCantidadProductosKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_jTextFieldCantidadProductosKeyTyped

    private void jButtonConfirmarVentaProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarVentaProductosActionPerformed
        confirmarProducto();
        actualizarProductos();
    }//GEN-LAST:event_jButtonConfirmarVentaProductosActionPerformed

    private void jTableVentasGeneralMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableVentasGeneralMouseClicked
        int filaSelec = jTableVentasGeneral.rowAtPoint(evt.getPoint());
        jComboBoxProductos.setSelectedItem(jTableVentasGeneral.getValueAt(filaSelec, 2).toString());
        jTextFieldCantidadProductos.setText(jTableVentasGeneral.getValueAt(filaSelec, 4).toString());
    }//GEN-LAST:event_jTableVentasGeneralMouseClicked

    private void jTextFieldCantidadHeladosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextFieldCantidadHeladosMouseClicked
        jTextFieldCantidadHelados.setText("");
    }//GEN-LAST:event_jTextFieldCantidadHeladosMouseClicked

    private void jTextFieldCantidadHeladosKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCantidadHeladosKeyTyped
        char c = evt.getKeyChar();
        if (c < '0' || c > '9')
            evt.consume();
    }//GEN-LAST:event_jTextFieldCantidadHeladosKeyTyped

    private void jTableVentasHeladosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableVentasHeladosMouseClicked
        int filaSelec = jTableVentasHelados.rowAtPoint(evt.getPoint());
        jComboBoxHelados.setSelectedItem(jTableVentasHelados.getValueAt(filaSelec, 2).toString());
        jTextFieldCantidadHelados.setText(jTableVentasHelados.getValueAt(filaSelec, 4).toString());
    }//GEN-LAST:event_jTableVentasHeladosMouseClicked

    private void jButtonConfirmarVentaHeladosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConfirmarVentaHeladosActionPerformed
        confirmarProducto();
        actualizarProductos();
    }//GEN-LAST:event_jButtonConfirmarVentaHeladosActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonConfirmarVentaHelados;
    private javax.swing.JButton jButtonConfirmarVentaProductos;
    private javax.swing.JButton jButtonSalir;
    private javax.swing.JComboBox<String> jComboBoxHelados;
    private javax.swing.JComboBox<String> jComboBoxProductos;
    private com.toedter.calendar.JDateChooser jDateChooserFechaHelados;
    private com.toedter.calendar.JDateChooser jDateChooserFechaProductos;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelVentasGeneral;
    private javax.swing.JPanel jPanelVentasHelados;
    private javax.swing.JRadioButton jRadioButtonTarjetaHelados;
    private javax.swing.JRadioButton jRadioButtonTarjetaProductos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableVentasGeneral;
    private javax.swing.JTable jTableVentasHelados;
    private javax.swing.JTextField jTextFieldCantidadHelados;
    private javax.swing.JTextField jTextFieldCantidadProductos;
    // End of variables declaration//GEN-END:variables
}
