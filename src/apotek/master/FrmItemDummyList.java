/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmItemDummyList.java
 *
 * Created on May 25, 2012, 10:20:14 AM
 */
package apotek.master;

import main.MainForm;
import java.awt.Component;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import main.GeneralFunction;

/**
 *
 * @author ustadho
 */
public class FrmItemDummyList extends javax.swing.JInternalFrame {
    private Connection conn;
    private MainForm mainForm;
    private Component aThis;
    private GeneralFunction fn=new GeneralFunction();
    
    /** Creates new form FrmItemDummyList */
    public FrmItemDummyList() {
        initComponents();
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int iRow=jTable1.getSelectedRow();
                btnUpdate.setEnabled(iRow>=0);
                btnDelete.setEnabled(iRow>=0);
            }
        });
        
        //jTable1.setFont(new Font("Tahoma", Font.DIALOG, 12));
        jTable1.setRowHeight(22);
    }
    
    public void setMainForm(MainForm mn){
        this.mainForm=mn;
    }
    
    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }
    
    public void udfLoadItem(String sKode){
        int i=0;
        ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
        try{
            ResultSet rs=conn.createStatement().executeQuery(
                    "select i.item_code as kode_barang, coalesce(i.item_name,'') as nama_barang, "
                    + "coalesce(i.nama_paten,'') as nama_paten, "
                    + "coalesce(i.satuan_kecil,'') as uom_jual, coalesce(j.jenis_barang,'') as jenis_barang, "
                    + "coalesce(g.group_name,'') as group_name, coalesce(b.bentuk_name,'') as bentuk_name, "
                    + "coalesce(m.nama_manufaktur,'') as nama_manufaktur, coalesce(i.indikasi,'') as indikasi "
                    + "from barang i "
                    + "left join jenis_barang j on j.kode_jenis=i.kode_jenis "
                    + "left join item_group g on g.group_id=i.group_id "
                    + "left join item_bentuk b on b.bentuk_id=i.bentuk_id " 
                    + "left join item_manufaktur m on m.id=i.manufaktur_id " 
                    + "where i.item_code||coalesce(i.item_name,'')||"
                    + "coalesce(j.jenis_barang,'')||coalesce(g.group_name,'')||coalesce(b.bentuk_name,'')||" +
                    "coalesce(m.nama_manufaktur,'')||coalesce(i.indikasi,'') ilike '%"+txtSearch.getText()+"%' "
                    + "order by 2");
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getString("kode_barang"), 
                    rs.getString("nama_barang"),
                    rs.getString("nama_paten"),
                    rs.getString("uom_jual"),
                    rs.getString("jenis_barang"),
                    rs.getString("group_name"),
                    rs.getString("bentuk_name"),
                    rs.getString("nama_manufaktur"),
                    rs.getString("indikasi"),
                    
                });
                if(rs.getString("kode_barang").equalsIgnoreCase(sKode))
                    i=jTable1.getRowCount()-1;
                
                
            }
            rs.close();
            if(jTable1.getRowCount()>0){
                jTable1.setRowSelectionInterval(i, i);
                jTable1.setModel((DefaultTableModel)fn.autoResizeColWidth(jTable1, (DefaultTableModel)jTable1.getModel()).getModel());
            }
            
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
                    
        }
            
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Daftar Barang");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameOpened(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ProductID", "Nama Barang", "Nama Paten", "Satuan", "Jenis", "Group", "Bentuk", "Manufaktur", "Kegunaan/ Indikasi"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(465, 35, 190, 22));

        jLabel1.setText("Filter :");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 35, 70, 22));

        jToolBar1.setRollover(true);

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/add-32.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/edit-32.png"))); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnUpdate.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnUpdate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });
        jToolBar1.add(btnUpdate);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/delete-32.png"))); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);

        btnExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/exit-32.png"))); // NOI18N
        btnExit.setText("Exit");
        btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExit.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnExit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        jToolBar1.add(btnExit);

        jPanel1.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 275, 60));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)
                .addGap(12, 12, 12))
            .addGroup(layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jScrollPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addGap(1, 1, 1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
    udfLoadItem("");
    aThis=this;
}//GEN-LAST:event_formInternalFrameOpened

private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
    this.dispose();
}//GEN-LAST:event_btnExitActionPerformed

private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
    apotek.master.FrmItemMaster f1=new apotek.master.FrmItemMaster(true);
    if(!mainForm.udfExistForm(f1)){
        mainForm.getDesktopImage().add(f1);
        f1.setConn(conn);
        f1.setSrcForm(aThis);
        //f1.setDesktop(desktopImage2);
        f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
        f1.setVisible(true);
        try{
            f1.setSelected(true);
        } catch(PropertyVetoException PO){
        }
    }else{
        f1.dispose();
    }
}//GEN-LAST:event_btnNewActionPerformed

private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
    udfEditItem();
}//GEN-LAST:event_btnUpdateActionPerformed

private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
    udfDelete();
}//GEN-LAST:event_btnDeleteActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
       udfLoadItem("");
    }//GEN-LAST:event_txtSearchKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(evt.getClickCount()==2){
            udfEditItem();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables

    private void udfEditItem() {
        int iRow=jTable1.getSelectedRow();
        apotek.master.FrmItemMaster f1=new apotek.master.FrmItemMaster(true);
        if(!mainForm.udfExistForm(f1)){
            mainForm.getDesktopImage().add(f1);
            f1.setConn(conn);
            f1.setSrcForm(aThis);
            String sKode=jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("ProductID")).toString();
            f1.setKodeBarang(sKode);
            //f1.setDesktop(desktopImage2);
            f1.setBounds(0, 0, f1.getWidth(), f1.getHeight());
            f1.setVisible(true);
            try{
                f1.setSelected(true);
            } catch(PropertyVetoException PO){
            }
        }else{
            f1.dispose();
        }
    }

    private void udfDelete() {
        int iRow=jTable1.getSelectedRow();
        if(JOptionPane.showConfirmDialog(aThis, "Anda yakin untuk menghapus item ini?", "Hapus item", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            try{
                String sKode=jTable1.getValueAt(iRow, jTable1.getColumnModel().getColumnIndex("ProductID")).toString();
                int i=conn.createStatement().executeUpdate("delete from phar_item where kode_barang='"+sKode+"'");
                if(i>0){
                    JOptionPane.showMessageDialog(this, "Delete item sukses!");
                    udfLoadItem("");
                }
                    
            }catch(SQLException se){
                JOptionPane.showMessageDialog(this, se.getMessage());
            }
        }
    }
}
