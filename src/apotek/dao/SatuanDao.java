package apotek.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author faheem
 */
public class SatuanDao {
    private Connection conn;
    
    public void setConn(Connection c){
        this.conn=c;
    }
    
    public List<Map<String, Object>> getAllJenis(){
        List<Map<String, Object>> hasil=null;
        //ResultSet rs=conn.createStatement().executeQuery("select * from jenis_barang ");
        
        return hasil;
    }
}
