/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FrmAR.java
 *
 * Created on 09 Jan 11, 19:57:31
 */

package apotek;

import main.MainForm;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.GeneralFunction;

/**
 *
 * @author cak-ust
 */
public class FrmAR extends javax.swing.JInternalFrame {
    private Connection conn;
    private GeneralFunction fn=new GeneralFunction();
    MyKeyListener kListener=new MyKeyListener();
    MyTableCellEditor cEditor=new MyTableCellEditor();
    private JFormattedTextField jFDate1;
    private boolean isKoreksi=false;
    

    /** Creates new form FrmAR */
    public FrmAR() {
        initComponents();
        jTable1.getColumn("Bayar").setCellEditor(cEditor);
        jTable1.getColumn("Diskon").setCellEditor(cEditor);
        fn.addKeyListenerInContainer(jPanel1, kListener, txtFocusListener);
        fn.addKeyListenerInContainer(jToolBar1, kListener, txtFocusListener);
        jTable1.addKeyListener(kListener);
        jTable1.getTableHeader().setFont(jTable1.getFont());
        jTable1.setRowHeight(20);

        jTable1.getModel().addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                lblTotBayar.setText("0");
                lblTotJual.setText("0");
                lblTotTerbayar.setText("0");
                lblTotSisa.setText("0");
                lblTotDiskon.setText("0");

                double dNilaiJual=0, dTerbayar=0, dSisa=0, dBayar=0, dDiskon=0;
                TableColumnModel col=jTable1.getColumnModel();

                for(int i=0; i< jTable1.getRowCount(); i++){
                    dNilaiJual+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Nilai Penjualan")));
                    dTerbayar+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Terbayar")));
                    dSisa+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Sisa")));
                    dBayar+=fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar")));
                    dDiskon+= fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Diskon")));
                }
                lblTotJual.setText(fn.intFmt.format(dNilaiJual));
                lblTotTerbayar.setText(fn.intFmt.format(dTerbayar));
                lblTotSisa.setText(fn.intFmt.format(dSisa));
                lblTotBayar.setText(fn.intFmt.format(dBayar));
                lblTotDiskon.setText(fn.intFmt.format(dDiskon));
            }
        });
        jTable1.getColumn("Tanggal").setCellRenderer(new MyRowRenderer());
    }

    public void setConn(Connection con){
        this.conn=con;
        fn.setConn(conn);
    }

    public void setKoreksi(boolean b){
        isKoreksi=b;
    }
    
    private void udfInitForm(){
        MaskFormatter fmttgl = null;
        try {
            fmttgl = new MaskFormatter("##/##/####");
        } catch (java.text.ParseException e) {}

        jFDate1 = new JFormattedTextField(fmttgl);
        jFTanggal.setFormatterFactory(jFDate1.getFormatterFactory());

        try{
            ResultSet rs = conn.createStatement().executeQuery("select to_char(current_date, 'dd/MM/yyyy') as tgl2 ");
            if(rs.next()){
                jFTanggal.setText(rs.getString(1));
                jFTanggal.setValue(rs.getString(1));
            }

            rs.close();
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        udfNew();
        txtNoTrx.setEnabled(isKoreksi);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                if(isKoreksi)
                    txtNoTrx.requestFocusInWindow();
                else
                    txtCustomer.requestFocusInWindow();
            }
        });


    }

    private void udfLoadAR(){
        try{
            ((DefaultTableModel)jTable1.getModel()).setNumRows(0);
            ResultSet rs=conn.createStatement().executeQuery(
                    "select * from fn_show_ar('"+txtCustomer.getText()+"') as (sales_no varchar, sales_date date, total double precision, bayar double precision, " +
                    "sisa double precision)");
            while(rs.next()){
                ((DefaultTableModel)jTable1.getModel()).addRow(new Object[]{
                    rs.getString("sales_no"),
                    rs.getDate("sales_date"),
                    rs.getDouble("total"),
                    rs.getDouble("bayar"),
                    rs.getDouble("sisa"),
                    0,
                    0
                });
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }

    private void udfNew() {
        txtNoTrx.setText("");
        txtCustomer.setText(""); txtNamaCustomer.setText("");
        txtKeterangan.setText("");
        ((DefaultTableModel)jTable1.getModel()).setNumRows(0);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Cancel.png"))); // NOI18N
        btnCancel.setText("Cancel");
        btnNew.setEnabled(false);   btnPrint.setEnabled(false);
        btnSave.setEnabled(true);
        txtCustomer.requestFocus();
    }

    public class MyKeyListener extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent evt){

        }

        @Override
        public void keyTyped(KeyEvent evt){
//            if(evt.getSource().equals(txtNamaPasien) && txtNoReg.getText().length()>0)
//                evt.consume();
        }

        @Override
        public void keyPressed(KeyEvent evt) {
            Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                case KeyEvent.VK_F4:{
                //    udfNew();
                    break;
                }
                case KeyEvent.VK_F2:{
                    udfSave();
                    break;
                }
                case KeyEvent.VK_F9:{
//                    if(tblDetail.getRowCount()==0) return;
//                    ((DefaultTableModel)tblHeader.getModel()).addRow(new Object[]{
//                        tblHeader.getRowCount()+1, "T", 0
//                    });
//                    tblHeader.requestFocusInWindow();
//                    tblHeader.requestFocus();
//                    tblHeader.setRowSelectionInterval(tblHeader.getRowCount()-1, tblHeader.getRowCount()-1);
//                    tblHeader.changeSelection(tblHeader.getRowCount()-1, 1, false, false);
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    if(!(ct instanceof JTable)){
                        if (!fn.isListVisible()){
//                            if(evt.getSource() instanceof JTextField && ((JTextField)evt.getSource()).getText()!=null
//                               && ((JTextField)evt.getSource()).getName().equalsIgnoreCase("textEditor")){
//                                if(table.getSelectedColumn()==0){
//                                    //table.setValueAt(((JTextField)evt.getSource()).getText(), table.getSelectedRow(), 0);
//                                    //table.changeSelection(table.getSelectedRow(), 2, false, false);
//                                    //table.setColumnSelectionInterval(2, 2);
//                                }
//                            }

                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                    }else{

                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
//                    if(ct instanceof JTable){
//                        //if(((JTable)ct).getSelectedRow()==0){
//                            Component c = findNextFocus();
//                            if (c==null) return;
//                            if(c.isEnabled())
//                                c.requestFocus();
//                            else{
//                                c = findNextFocus();
//                                if (c!=null) c.requestFocus();;
//                            }
//                        //}
//                    }else{
                        if (!(ct instanceof JTable) && !fn.isListVisible()){
                            Component c = findNextFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }else{
                            fn.lstRequestFocus();
                        }
                        break;
                    //}
                }

                case KeyEvent.VK_UP: {
                    if(ct instanceof JTable){
                        if(((JTable)ct).getSelectedRow()==0){
                            Component c = findPrevFocus();
                            if (c==null) return;
                            if(c.isEnabled())
                                c.requestFocus();
                            else{
                                c = findNextFocus();
                                if (c!=null) c.requestFocus();;
                            }
                        }
                    }else{
                        Component c = findPrevFocus();
                        if (c==null) return;
                        if(c.isEnabled())
                            c.requestFocus();
                        else{
                            c = findNextFocus();
                            if (c!=null) c.requestFocus();;
                        }
                    }
                    break;
                }
//                case KeyEvent.VK_DELETE:{
//                    if(evt.getSource().equals(table) && table.getSelectedRow()>=0){
//                        int iRow[]= table.getSelectedRows();
//                        int rowPalingAtas=iRow[0];
//
//                        TableModel tm= table.getModel();
//
//                        while(iRow.length>0) {
//                            //JOptionPane.showMessageDialog(null, iRow[0]);
//                            ((DefaultTableModel)tm).removeRow(table.convertRowIndexToModel(iRow[0]));
//                            iRow = table.getSelectedRows();
//                        }
//                        table.clearSelection();
//
//                        if(table.getRowCount()>0 && rowPalingAtas<table.getRowCount()){
//                            table.setRowSelectionInterval(rowPalingAtas, rowPalingAtas);
//                        }else{
//                            if(table.getRowCount()>0)
//                                table.setRowSelectionInterval(rowPalingAtas-1, rowPalingAtas-1);
//                            else
//                                table.requestFocus();
//                        }
//                        if(table.getSelectedRow()>=0)
//                            table.changeSelection(table.getSelectedRow(), 0, false, false);
//
//                        if(table.getCellEditor()!=null)
//                            table.getCellEditor().stopCellEditing();
//                    }
//                    break;
//                }
                case KeyEvent.VK_ESCAPE:{
                    btnCancelActionPerformed(null);
                    break;
                }
            }
        }

//        @Override
//        public void keyReleased(KeyEvent evt){
//            if(evt.getSource().equals(txtDisc)||evt.getSource().equals(txtQty)||evt.getSource().equals(txtUnitPrice))
//                GeneralFunction.keyTyped(evt);
//        }

        public Component findNextFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component nextFocus = policy.getComponentAfter(root, c);
                if (nextFocus == null) {
                    nextFocus = policy.getDefaultComponent(root);
                }
                return nextFocus;
            }
            return null;
        }

        public Component findPrevFocus() {
            // Find focus owner
            Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            Container root = c == null ? null : c.getFocusCycleRootAncestor();

            if (root != null) {
                FocusTraversalPolicy policy = root.getFocusTraversalPolicy();
                Component prevFocus = policy.getComponentBefore(root, c);
                if (prevFocus == null) {
                    prevFocus = policy.getDefaultComponent(root);
                }
                return prevFocus;
            }
            return null;
        }
    }

    public class MyTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        private Toolkit toolkit;
        JTextField text= ustTextField;

        int col, row;
        public Component getTableCellEditorComponent(JTable tblDetail, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            row=rowIndex;
            col=vColIndex;
            text=ustTextField;
            text.setName("textEditor");

            text.addKeyListener(kListener);

           text.setBackground(new Color(0,255,204));
           text.addFocusListener(txtFocusListener);
           text.setFont(tblDetail.getFont());
//           text.setVisible(!lookupItem.isVisible());
//            if(lookupItem.isVisible()){
//                return null;
//            }
            text.setText(value==null? "": value.toString());

            if(value instanceof Double || value instanceof Float|| value instanceof Integer){
               text.setText(fn.dFmt.format(value));
            }else
                text.setText(value==null? "":value.toString());
           return text;
        }

        public Object getCellEditorValue() {
            Object o="";//=component.getText();
            Object retVal = 0;
            try {
                if(jTable1.getSelectedColumn()==0){
                    retVal = ((JTextField)text).getText();

                }
                else
                    retVal = fn.udfGetDouble(((JTextField)text).getText());
                return retVal;
            } catch (Exception e) {
                toolkit.beep();
                retVal=0;
            }
            return retVal;
        }

        public boolean isVisible(){
            return text.isVisible();
        }
    }

    JTextField ustTextField = new JTextField() {
        protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
            if (hasFocus()) {
                return super.processKeyBinding(ks, e, condition, pressed);
            } else {
                this.requestFocus();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        processKeyBinding(ks, e, condition, pressed);
                    }
              });
                return true;
            }
        }
    };

    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField ||(((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor")))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);
                if(e.getSource().equals(txtCustomer) && txtCustomer.getText().length()>0)
                    udfLoadAR();


           }
        }


    } ;
    SimpleDateFormat dmy = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            setFont(table.getFont());
            if(isSelected){
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }else{
                setBackground(table.getBackground());
                setForeground(table.getForeground());

            }
            JCheckBox checkBox = new JCheckBox();
            if(value instanceof Date ){
                value=dmy.format(value);
            }if(value instanceof Timestamp ){
                value=dmy.format(value);
            }else if(value instanceof Double ||value instanceof Integer ||value instanceof Float  ){
                setHorizontalAlignment(SwingConstants.RIGHT);
                value=fn.dFmt.format(value);
            }

            setValue(value);
            return this;
        }
    }

    private String getMessageBeforeSave(){
        String sMessage="";
        if(txtCustomer.getText().trim().length()==0){
            if(!txtCustomer.isFocusOwner())
                txtCustomer.requestFocus();
            return "Silakan isi Pelanggan terlebih dulu!";
        }
        if(jTable1.getRowCount()==0){
            jTable1.requestFocusInWindow();

            return "Tabel pembayaran pelanggan masih kosong!";
        }
        if(txtJenisBayar.getText().trim().length()==0){
            if(!txtJenisBayar.isFocusOwner())
                txtJenisBayar.requestFocus();
            return "Silakan isi jenis pembayaran terlebih dulu!";
        }
        if(fn.udfGetDouble(lblTotBayar.getText())+fn.udfGetDouble(lblTotDiskon.getText())==0){
            jTable1.requestFocusInWindow();
            jTable1.changeSelection(0, 5, false, false);
            return "Total pembayaran masih kosong!";
        }

        return sMessage;
    }

    private void udfSave(){
        String sMsg=getMessageBeforeSave();
        if(sMsg.length()>0){
            JOptionPane.showMessageDialog(this, sMsg);
            return;
        }
        try{
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
            ResultSet rs=conn.createStatement().executeQuery("select fn_get_no_ar()");
            if(rs.next())
                txtNoTrx.setText(rs.getString(1));
            else{
                rs.close();
                return;
            }
            TableColumnModel col=jTable1.getColumnModel();
            String sQry="INSERT INTO ar(kode_ar, tanggal, kode_customer, kode_pembayaran, ket_kode_pembayaran, user_tr, " +
                        "time_ins)    VALUES (" +
                        "'"+txtNoTrx.getText()+"', '"+ymd.format(dmy.parse(jFTanggal.getText()))+"', '"+txtCustomer.getText()+"', "
                    + "'"+txtJenisBayar.getText()+"', " +
                        "'"+txtKeterangan.getText()+"'," +
                        "'"+MainForm.sUserName+"', now());\n";
            
            for(int i=0; i<jTable1.getRowCount(); i++){
                sQry+="INSERT INTO ar_detail(kode_ar, no_penjualan, " +
                        "terbayar, diskon, denda) VALUES (" +
                        "'"+txtNoTrx.getText()+"', '"+jTable1.getValueAt(i, col.getColumnIndex("No. Penjualan")).toString()+"', " +
                        fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Bayar")))+", "+fn.udfGetDouble(jTable1.getValueAt(i, col.getColumnIndex("Diskon")))+", " +
                        "0);\n";
            }
            conn.setAutoCommit(false);
            conn.createStatement().executeUpdate(sQry);
            conn.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Simpan pembayaran pelanggan sukses!");
            udfNew();
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }catch(SQLException se){
            try {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                conn.rollback();
                conn.setAutoCommit(true);
                JOptionPane.showMessageDialog(this, se.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(FrmAR.class.getName()).log(Level.SEVERE, null, ex);
            }
        }catch(ParseException se){
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtCustomer = new javax.swing.JTextField();
        txtNamaCustomer = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtNoTrx = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jFTanggal = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtKeterangan = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtJenisBayar = new javax.swing.JTextField();
        lblJenisBayar = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        lblTotBayar = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        lblTotDiskon = new javax.swing.JLabel();
        lblTotSisa = new javax.swing.JLabel();
        lblTotTerbayar = new javax.swing.JLabel();
        lblTotJual = new javax.swing.JLabel();
        lblTotal6 = new javax.swing.JLabel();
        lblTotal7 = new javax.swing.JLabel();
        lblTotal8 = new javax.swing.JLabel();
        lblTotal9 = new javax.swing.JLabel();
        lblTotal10 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnPrint = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Pembayaran Pelanggan");
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
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel1.setText("Jenis Bayar");
        jLabel1.setName("jLabel1"); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 90, 20));

        txtCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtCustomer.setName("txtCustomer"); // NOI18N
        txtCustomer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerKeyReleased(evt);
            }
        });
        jPanel1.add(txtCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 35, 90, 20));

        txtNamaCustomer.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNamaCustomer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNamaCustomer.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNamaCustomer.setEnabled(false);
        txtNamaCustomer.setName("txtNamaCustomer"); // NOI18N
        jPanel1.add(txtNamaCustomer, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 35, 510, 20));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel5.setText("No. Transaksi");
        jLabel5.setName("jLabel5"); // NOI18N
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 90, 20));

        txtNoTrx.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtNoTrx.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtNoTrx.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNoTrx.setEnabled(false);
        txtNoTrx.setName("txtNoTrx"); // NOI18N
        jPanel1.add(txtNoTrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, 140, 20));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel3.setText("Tanggal");
        jLabel3.setName("jLabel3"); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 10, 90, 20));

        jFTanggal.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jFTanggal.setFont(new java.awt.Font("Tahoma", 0, 12));
        jFTanggal.setName("jFTanggal"); // NOI18N
        jPanel1.add(jFTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 10, 110, 20));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Keterangan");
        jLabel6.setName("jLabel6"); // NOI18N
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, 90, 20));

        txtKeterangan.setFont(new java.awt.Font("Tahoma", 0, 12));
        txtKeterangan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKeterangan.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtKeterangan.setName("txtKeterangan"); // NOI18N
        jPanel1.add(txtKeterangan, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 85, 600, 20));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel2.setText("Pelanggan");
        jLabel2.setName("jLabel2"); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 35, 90, 20));

        txtJenisBayar.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        txtJenisBayar.setText("1");
        txtJenisBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtJenisBayar.setName("txtJenisBayar"); // NOI18N
        txtJenisBayar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtJenisBayarKeyReleased(evt);
            }
        });
        jPanel1.add(txtJenisBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 60, 30, 20));

        lblJenisBayar.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblJenisBayar.setText("Tunai");
        lblJenisBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblJenisBayar.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        lblJenisBayar.setEnabled(false);
        lblJenisBayar.setName("lblJenisBayar"); // NOI18N
        jPanel1.add(lblJenisBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 60, 160, 20));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 720, 110));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setFont(new java.awt.Font("Tahoma", 0, 12));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Penjualan", "Tanggal", "Nilai Penjualan", "Terbayar", "Sisa", "Bayar", "Diskon"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 720, 140));

        lblTotBayar.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotBayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotBayar.setText("0");
        lblTotBayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotBayar.setName("lblTotBayar"); // NOI18N
        getContentPane().add(lblTotBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 340, 100, 20));

        lblTotal1.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal1.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal1.setText("Diskon");
        lblTotal1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal1.setName("lblTotal1"); // NOI18N
        lblTotal1.setOpaque(true);
        getContentPane().add(lblTotal1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 320, 100, 20));

        lblTotDiskon.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotDiskon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotDiskon.setText("0");
        lblTotDiskon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotDiskon.setName("lblTotDiskon"); // NOI18N
        getContentPane().add(lblTotDiskon, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 340, 100, 20));

        lblTotSisa.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotSisa.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotSisa.setText("0");
        lblTotSisa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotSisa.setName("lblTotSisa"); // NOI18N
        getContentPane().add(lblTotSisa, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 340, 100, 20));

        lblTotTerbayar.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotTerbayar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotTerbayar.setText("0");
        lblTotTerbayar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotTerbayar.setName("lblTotTerbayar"); // NOI18N
        getContentPane().add(lblTotTerbayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 340, 100, 20));

        lblTotJual.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblTotJual.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotJual.setText("0");
        lblTotJual.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotJual.setName("lblTotJual"); // NOI18N
        getContentPane().add(lblTotJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 340, 100, 20));

        lblTotal6.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal6.setFont(new java.awt.Font("Tahoma", 1, 14));
        lblTotal6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal6.setText("TOTAL  ");
        lblTotal6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblTotal6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal6.setName("lblTotal6"); // NOI18N
        lblTotal6.setOpaque(true);
        getContentPane().add(lblTotal6, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 320, 100, 40));

        lblTotal7.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal7.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal7.setText("Terbayar");
        lblTotal7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal7.setName("lblTotal7"); // NOI18N
        lblTotal7.setOpaque(true);
        getContentPane().add(lblTotal7, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 320, 100, 20));

        lblTotal8.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal8.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal8.setText("Sisa");
        lblTotal8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal8.setName("lblTotal8"); // NOI18N
        lblTotal8.setOpaque(true);
        getContentPane().add(lblTotal8, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 320, 100, 20));

        lblTotal9.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal9.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal9.setText("Bayar");
        lblTotal9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal9.setName("lblTotal9"); // NOI18N
        lblTotal9.setOpaque(true);
        getContentPane().add(lblTotal9, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 320, 100, 20));

        lblTotal10.setBackground(new java.awt.Color(153, 204, 255));
        lblTotal10.setFont(new java.awt.Font("Tahoma", 0, 12));
        lblTotal10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal10.setText("Nilai Penjualan");
        lblTotal10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblTotal10.setName("lblTotal10"); // NOI18N
        lblTotal10.setOpaque(true);
        getContentPane().add(lblTotal10, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 320, 100, 20));

        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setText("New");
        btnNew.setFocusable(false);
        btnNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNew.setName("btnNew"); // NOI18N
        btnNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Save.png"))); // NOI18N
        btnSave.setText("Save");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setName("btnSave"); // NOI18N
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSave);

        btnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/buttons/print-32.png"))); // NOI18N
        btnPrint.setText("Print");
        btnPrint.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnPrint.setName("btnPrint"); // NOI18N
        btnPrint.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPrint);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/close.png"))); // NOI18N
        btnCancel.setText("Exit");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setName("btnCancel"); // NOI18N
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        jToolBar1.add(btnCancel);

        getContentPane().add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-754)/2, (screenSize.height-406)/2, 754, 406);
    }// </editor-fold>//GEN-END:initComponents

    private void txtCustomerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerKeyReleased
        fn.lookup(evt, new Object[]{txtNamaCustomer}, "select kode_customers as kode, coalesce(nama_pasien,'') as nama from customers " +
                "where kode_customers||coalesce(nama_pasien,'') ilike '%"+txtCustomer.getText()+"%'", 500, 200);
}//GEN-LAST:event_txtCustomerKeyReleased

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        udfInitForm();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
}//GEN-LAST:event_btnNewActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        udfSave();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        //printKwitansi(txtNoPO.getText(), false);
}//GEN-LAST:event_btnPrintActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        if(btnCancel.getText().equalsIgnoreCase("cancel")){
            if(getTitle().indexOf("Revision")>0) dispose();
            btnSave.setEnabled(false);
            btnNew.setEnabled(true);
            btnCancel.setText("Exit");
            btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/32/close.png"))); // NOI18N
        
        }else{
            this.dispose();
        }
}//GEN-LAST:event_btnCancelActionPerformed

    private void txtJenisBayarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtJenisBayarKeyReleased
        fn.lookup(evt, new Object[]{lblJenisBayar}, "select kode_jenis as kode, coalesce(jenis_pembayaran,'') as jenis_pembayaran "
                + "from jenis_pembayaran where kode_jenis||coalesce(jenis_pembayaran,'') ilike %"+txtJenisBayar.getText()+"%'", 
                txtJenisBayar.getWidth()+lblJenisBayar.getWidth()+18, 100);
    }//GEN-LAST:event_txtJenisBayarKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnSave;
    private javax.swing.JFormattedTextField jFTanggal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTextField lblJenisBayar;
    private javax.swing.JLabel lblTotBayar;
    private javax.swing.JLabel lblTotDiskon;
    private javax.swing.JLabel lblTotJual;
    private javax.swing.JLabel lblTotSisa;
    private javax.swing.JLabel lblTotTerbayar;
    private javax.swing.JLabel lblTotal1;
    private javax.swing.JLabel lblTotal10;
    private javax.swing.JLabel lblTotal6;
    private javax.swing.JLabel lblTotal7;
    private javax.swing.JLabel lblTotal8;
    private javax.swing.JLabel lblTotal9;
    private javax.swing.JTextField txtCustomer;
    private javax.swing.JTextField txtJenisBayar;
    private javax.swing.JTextField txtKeterangan;
    private javax.swing.JTextField txtNamaCustomer;
    private javax.swing.JTextField txtNoTrx;
    // End of variables declaration//GEN-END:variables

}
