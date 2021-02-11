/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pruebas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 *
 * @author facundolorenzo
 */
public class PruebaCargaExcel {

    String nombre, apellido, direccion, email;

    public void leerArchivosExcel(String destino) {
        int count = 1;
        try {
            Workbook archivoExcel = Workbook.getWorkbook(new File(destino));
            //Recorre c/hoja
            for (int hojas = 1; hojas < archivoExcel.getNumberOfSheets(); hojas++) {
                Sheet hoja = archivoExcel.getSheet(hojas);
                String dato;
                for (int fila = 1; fila < hoja.getRows(); fila++) {
                    for (int columna = 1; columna < hoja.getColumns(); columna++) {
                        dato = hoja.getCell(columna, fila).getContents();
                        System.out.println("Dato: " + dato);
                        //Instruccion swith que evalua la variable count.
                        switch (count) {
                            case 1:
                                nombre = dato;
                                count++;
                                break;
                            case 2:
                                apellido = dato;
                                count++;
                                break;
                            case 3:
                                direccion = dato;
                                count++;
                                break;
                            case 4:
                                email = dato;
                                count = 1;
                                break;
                        }
                    }
                    try {
                        Connection cn = Conexion.conectar();
                        PreparedStatement ps = cn.prepareStatement("ISNERT INTO datos('Nombre', 'Apellido'"
                                + "'Direccion', 'Email') VALUES ('"+nombre+"', '"+apellido+"', '"+direccion+"', '"+email+"')");
                        ps.executeUpdate();
                        System.out.println(""+ps.executeUpdate());
                        cn.close();
                    } catch (SQLException e) {
                    }
                }
            }
            System.out.println("ver");
        } catch (IOException | BiffException e) {
            System.out.println(e.toString());
        }
    }
    
    public static void main(String[] args){
        PruebaCargaExcel p = new PruebaCargaExcel();
        p.leerArchivosExcel("/Users/facundolorenzo/DocumentosPC/Programacion/Pruebas_Java/PruebaCargaExcel/Excel.xlsx");
    }

}
