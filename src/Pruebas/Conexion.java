/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pruebas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author facundolorenzo
 */
public class Conexion {
    //Conexión Local
    public static Connection conectar(){
        try {
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost/pruebaCarga", "root", "");
            return cn;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error de conexion: "+ e.toString());
        }
        return (null);
    }
}