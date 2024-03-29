/*
 * FrmJenisSupplier.java
 *
 * Created on December 4, 2006, 2:22 PM
 */

package apotek.master;

import apotek.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;
import main.ListRsbm;

/**
 *
 * @author  root
 */
public class FrmJenisSupplier extends javax.swing.JInternalFrame {
    private String idUnit;
    private String nmUnit;
    private String singkatan;
    private String supervisor;
    DefaultTableModel modelJenis;
    private String sClose="close";
    private ListRsbm lst;
    
    static String sID="";
    static String sTgl="";
    private Connection conn;
    private boolean bAsc;
    
    /**
     * Creates new form FrmJenisSupplier
     */
    public FrmJenisSupplier(Connection nCon) {
        initComponents();
        conn=nCon;
    }
    
    public Connection getConn() {
        return conn;
    }
    
    public void setConn(Connection conn) {
        this.conn = conn;
    }
    
    public void initJDBC() {
            
            setBEdit(false);
            setBNew(false);
            
            TableLook();
            udfFilter();
    }
    
    private void TableLook(){
            tblJenisSupp.getColumnModel().getColumn(0).setMaxWidth(70);     //kode
            tblJenisSupp.getColumnModel().getColumn(0).setPreferredWidth(70);
            
            tblJenisSupp.setRowHeight(20);
            for (int i=0;i<tblJenisSupp.getColumnCount();i++){
                tblJenisSupp.getColumnModel().getColumn(i).setCellRenderer(new MyRowRenderer());
            }
            
            if (modelJenis.getRowCount() > 0) {
                tblJenisSupp.changeSelection(0, 0,false,false);                
            }            
     }
    
    public boolean getAsc(){
        if (!bAsc)
            bAsc=true;
        else
            bAsc=false;
        return bAsc;               
    }

    private void udfFilter() {
        String sQry="select * from fn_jenis_supp_list (1, 'all', 'like', '') as (kode_jenis varchar, " +
                "jenis_supplier varchar)";

        try {
            ResultSet rs = conn.createStatement().executeQuery(sQry);
            
            while (rs.next()) {
            modelJenis.addRow(new Object[]{rs.getString("kode_jenis"),
                                        rs.getString("jenis_supplier"),
                                        });
                }
            
            SelectionListener listener = new SelectionListener(tblJenisSupp);
            tblJenisSupp.getSelectionModel().addListSelectionListener(listener);
            tblJenisSupp.getColumnModel().getSelectionModel().addListSelectionListener(listener);
            tblJenisSupp.setRequestFocusEnabled(true);
            
            if (modelJenis.getRowCount() > 0) {
                tblJenisSupp.setRowSelectionInterval(0, 0);
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
    }
    
    public class myColHeaderList extends MouseAdapter{
            public void mouseClicked(MouseEvent evt) {
            JTable table = ((JTableHeader)evt.getSource()).getTable();
            TableColumnModel colModel = table.getColumnModel();
    
            // The index of the column whose header was clicked
            int vColIndex = colModel.getColumnIndexAtX(evt.getX());
            int mColIndex = table.convertColumnIndexToModel(vColIndex);
    
            // Return if not clicked on any column header
            if (vColIndex == -1) {
                return;
            }
            boolean bSt;
            bSt=getAsc();
            sortAllRowsBy(modelJenis, vColIndex, bSt);
            // Determine if mouse was clicked between column heads
            Rectangle headerRect = table.getTableHeader().getHeaderRect(vColIndex);
            if (vColIndex == 0) {
                headerRect.width -= 3;    // Hard-coded constant
            } else {
                headerRect.grow(-3, 0);   // Hard-coded constant
            }
            if (!headerRect.contains(evt.getX(), evt.getY())) {
                // Mouse was clicked between column heads
                // vColIndex is the column head closest to the click
    
                // vLeftColIndex is the column head to the left of the click
                int vLeftColIndex = vColIndex;
                if (evt.getX() < headerRect.x) {
                    vLeftColIndex--;
                }                
            }
        }
    }
    
    public class MyRowRenderer extends DefaultTableCellRenderer implements TableCellRenderer{
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            Color g1 = new Color(239,234,240);//-->>(251,236,177);// Kuning         [251,251,235]
            Color g2 = new Color(239,234,240);//-->>(241,226,167);// Kuning         [247,247,218]
            
             
            Color w1 = new Color(255,255,255);// Putih
            Color w2 = new Color(250,250,250);// Putih Juga
            
            Color h1 = new Color(255,240,240);// Merah muda
            Color h2 = new Color(250,230,230);// Merah Muda
            
            Color g;
            Color w;
            Color h;
            
            if(column%2==0){
                g = g1;
                w = w1;
                h = h1;
            }else{
                g = g2;
                w = w2;
                h = h2;
            }
            setForeground(new Color(0,0,0));
            if (row%2==0){
                setBackground(w);
            }else{
                setBackground(g);
            }
            if(isSelected){
                //setBackground(new Color(51,102,255));
                setBackground(new Color(248,255,167));
                //setForeground(new Color(255,255,255));
            }
            
            setValue(value);
            return this;
        }
    }
    
    public class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;
        ColumnSorter(int colIndex, boolean ascending) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }
        public int compare(Object a, Object b) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get(colIndex);
            Object o2 = v2.get(colIndex);
    
            // Treat empty strains like nulls
            if (o1 instanceof String && ((String)o1).length() == 0) {
                o1 = null;
            }
            if (o2 instanceof String && ((String)o2).length() == 0) {
                o2 = null;
            }
    
            // Sort nulls so they appear last, regardless
            // of sort order
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return 1;
            } else if (o2 == null) {
                return -1;
            } else if (o1 instanceof Comparable) {
                if (ascending) {
                    return ((Comparable)o1).compareTo(o2);
                } else {
                    return ((Comparable)o2).compareTo(o1);
                }
            } else {
                if (ascending) {
                    return o1.toString().compareTo(o2.toString());
                } else {
                    return o2.toString().compareTo(o1.toString());
                }
            }
        }
    } 
    
    public void sortAllRowsBy(DefaultTableModel model, int colIndex, boolean ascending) {
        Vector data = model.getDataVector();
        Collections.sort(data, new ColumnSorter(colIndex, ascending));
        model.fireTableStructureChanged();
        TableLook();
    }
    
    private void pesanError(String Err){
        JOptionPane.showMessageDialog(this,Err,"Message",JOptionPane.ERROR_MESSAGE);
    }
    
    public class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent evt) {
            int keyKode = evt.getKeyCode();
            switch(keyKode){
                
                case KeyEvent.VK_F2: {  //Save
                    if (getBEdit())
                        udfUpdateData();
                    
                    break;
                }
                
                case KeyEvent.VK_F3: {  //Search
//                    udfFilter();
                    
                    break;
                }
                
                case KeyEvent.VK_F4: {  //Edit
                    udfEdit();
                    break;
                }
                
                case KeyEvent.VK_F5: {  //New -- Add
                    udfNew();
                    break;
                }
                
                case KeyEvent.VK_F6: {  //Filter
//                    onOpen(cmbFilter.getSelectedItem().toString(),true);
                    break;
                }
                
                case KeyEvent.VK_F12: {  //Delete
                    if (!getBEdit() && tblJenisSupp.getRowCount()>0)
                        udfUpdateData();
                    
                    break;
                }
                case KeyEvent.VK_ENTER : {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {
                    if (!lst.isVisible()){
                        Component c = findNextFocus();
                        c.requestFocus();
                    }else{
                        lst.requestFocus();
                    }
                    }
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                        {                        
                            if (!lst.isVisible()){
			    Component c = findNextFocus();
			    c.requestFocus();
                            }else
                                lst.requestFocus();
                            
                            break;
                    }
                }
                case KeyEvent.VK_UP: {
                    Component ct = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
                    if(!(ct.getClass().getSimpleName().equalsIgnoreCase("JTABLE")))
                    {    
                        Component c = findPrevFocus();
                        c.requestFocus();
                    }
                    break;
                }
                
                //lempar aja ke udfCancel
                case KeyEvent.VK_ESCAPE: {
                    //Jika status button adalah Close
                    if(sClose.equalsIgnoreCase("close")){
                        if(!getBEdit()){
                            if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk Keluar?","Joss Prima go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                dispose();
                            }
                        }
                        else
                            if(JOptionPane.showConfirmDialog(null,"Apakah data disimpan sebelum anda keluar?","Joss Prima go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
                                udfUpdateData();
                            }
                            else
                                dispose();

                            break;
                    }   //Jika cancel
                    else
                        udfCancel();
                }
            }
        }
    }
    
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
//            if (lst.isVisible())
//                lst.setVisible(false);
            
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
//            if (lst.isVisible()) lst.setVisible(false);
            
            return prevFocus;
        }
        return null;
    }
    
    public void setUpBtn(){
        if (getBEdit()) {  //proses edit     
            
            String fileImageSave="/image/Icon/Save.png";
            ButtonIcon(fileImageSave,btnDelete);
            
            String fileImageCancel="/image/Icon/Cancel.png";
            ButtonIcon(fileImageCancel,btnClose);
            
            sClose="cancel";

            btnNew.setEnabled(false);
            btnEdit.setEnabled(false);

            btnDelete.setToolTipText("Save    (F5)");
            btnClose.setToolTipText("Cancel");
            System.out.println(getBEdit());
            
            tblJenisSupp.setEnabled(false);
            txtNama.setEditable(true);
            
            tblJenisSupp.requestFocus();

        } else {   //selain edit & NEW
            String fileImageSave="/image/Icon/Delete.png";
            ButtonIcon(fileImageSave,btnDelete);
            
            String fileImageCancel="/image/Icon/Exit.png";
            ButtonIcon(fileImageCancel,btnClose);
            
            sClose="close";
            
            btnNew.setEnabled(true);
            btnEdit.setEnabled(true);
            
            btnDelete.setToolTipText("Delete     (F12)");
            btnClose.setToolTipText("Close");
            
            tblJenisSupp.setEnabled(true);
            txtNama.setEditable(false);
            
        }        
    }
    
    public void setBNew(Boolean lNew) {
        bNew = lNew;
    }
    
    private void LabelIcon(String aFile,javax.swing.JLabel newlbl) {              
       javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
       newlbl.setIcon(myIcon);
   }
    
    private void ButtonIcon(String aFile,javax.swing.JButton newBtn) {              
       javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
       newBtn.setIcon(myIcon);
   }
    public Boolean getBNew() {
        return bNew;
    }

    public void setBEdit(Boolean lEdit) {
        bEdit = lEdit;
    }
    
    public Boolean getBEdit() {
        return bEdit;
    }
    
    public String getTanggal(){
        Calendar c = Calendar.getInstance();
        String sekarang="";
	try{
	    final Statement stTgl = conn.createStatement();
	    final ResultSet rtgl = stTgl.executeQuery("select now() as tanggal, current_time as jam");
	    if (rtgl.next()){		
		SimpleDateFormat fdateformat = new SimpleDateFormat("dd-MM-yyyy");
            	sekarang =fdateformat.format(rtgl.getDate(1));    
                c.setTimeInMillis(rtgl.getTime(2).getTime());
                //c.set()
	    }
	    rtgl.close();
	    stTgl.close();
	}catch(SQLException sqtgl){System.out.println(sqtgl.getMessage());}

	MaskFormatter fmt = null;
	try {
	    fmt = new MaskFormatter("##-##-####");
	} catch (java.text.ParseException e) {}
        return sekarang;
    }
    
    private void saveUnit(){
        JenisSupplierBean sBean=new JenisSupplierBean();
        sBean.setConn(conn);
        sBean.setKodeJenis(txtKode.getText());
        sBean.setNamaJenis(txtNama.getText());
        
    }
    
    //Modul untuk RowColChange-nya JTable
     public class SelectionListener implements ListSelectionListener {
         JTable table;
         int rowPos;
         int colPos;
    
        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        SelectionListener(JTable table) {
            this.table = table;
        }
        public void valueChanged(ListSelectionEvent e) {
            rowPos = table.getSelectedRow();           
            
            if  (rowPos >=0 && rowPos < table.getRowCount()) {
                txtKode.setText(table.getValueAt(rowPos,0).toString());
                txtNama.setText(table.getValueAt(rowPos,1).toString());
            }
            if (table.getRowCount()==0){
                txtKode.setText("");
                txtNama.setText("");
            }
            
        }
    }
     
    private void udfUpdateData(){
        int hsl=0;
        
        if (getBEdit()) {
            try {
                conn.setAutoCommit(false);
                if (getBNew()) {        //Add
                    Statement st=conn.createStatement();
                    ResultSet rs=st.executeQuery("select fn_save_jenis_supp('', '"+txtNama.getText()+"')");
                    
                    if(rs.next()){
                        txtKode.setText(rs.getString(1));
                        modelJenis.addRow(new Object[]{txtKode.getText(),
                                                       txtNama.getText(),
                                               });
                        setBEdit(false);
                        tblJenisSupp.setRowSelectionInterval(modelJenis.getRowCount()-1, modelJenis.getRowCount()-1);
                        setBEdit(false);
                        setBNew(false);
                        setUpBtn();
                    }else{
                        pesanError("Insert jenis supplier gagal. Silakan coba sekali lagi!");
                        System.out.println(hsl);   
                    }
                    rs.close();
                    st.close();
                    
                    //System.out.println(hsl);
            } else {
                    Statement st=conn.createStatement();
                    ResultSet rs=st.executeQuery("select fn_save_jenis_supp('"+txtKode.getText()+"', '"+txtNama.getText()+"')");
                    
                    if (!rs.next()){
                        pesanError("Gagal Update!");
                        System.out.println(hsl);   
                        
                    }else{
                        tblJenisSupp.setValueAt(txtNama.getText(), tblJenisSupp.getSelectedRow(), 1);
                        System.out.println(hsl);
                        setBEdit(false);
                        setBNew(false);
                        setUpBtn();
                    }
           }

           conn.commit();
           conn.setAutoCommit(true);
           }catch (SQLException e) {
                try{
                    conn.rollback();
                    conn.setAutoCommit(true);
                }catch(SQLException s){}
                System.out.println(e.getMessage());
                pesanError(e.getMessage());  
            }
        
        } else {    //DELETE
            try {
                String  s=tblJenisSupp.getValueAt(tblJenisSupp.getSelectedRow(),1).toString();
                if(JOptionPane.showConfirmDialog(null,"Anda Yakin Untuk mengapus '" + s +"' ?","Joss Prima go Open Sources",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
                
                {    
                        int iPosDel = tblJenisSupp.getSelectedRow();
                        
                        Statement st=conn.createStatement();
                        hsl=st.executeUpdate("delete from jenis_supplier where kode_jenis_supp='"+txtKode.getText()+"'");
                        
                        conn.commit();
                        modelJenis.removeRow(iPosDel);
                        if ( iPosDel < modelJenis.getRowCount() && modelJenis.getRowCount()>0 ) {
                            tblJenisSupp.setRowSelectionInterval(iPosDel, iPosDel);
                            tblJenisSupp.requestFocus();
                    }
                }
            }catch(SQLException se) {
                    try{
                        System.out.println(se.getMessage());
                        conn.rollback();
                        conn.setAutoCommit(true);
                    }catch(SQLException s){
                    pesanError(s.getMessage());
                }
            }
            tblJenisSupp.requestFocus();
        }
    }
    private void udfEdit(){
        if (tblJenisSupp.getRowCount()>0){
            setBEdit(true);
            setBNew(false);
            setUpBtn();
            txtNama.requestFocus();
        }
    }
    
    private void udfNew(){
        setBNew(true);
        setBEdit(true);
        setUpBtn();
        
//        try{
//            Statement st=conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
//            ResultSet rs=st.executeQuery("select fn_get_new_phar_jenis_supplier()");
//            
//            if (rs.next()) txtKode.setText(rs.getString(1));
//            
//            rs.close();
//            st.close();
//            
//        }catch(SQLException se){System.out.println(se.getMessage());}
        txtKode.setText("");
        txtNama.setText("");
        txtNama.requestFocus();
    }
    
    private void udfCancel(){
        if (getBEdit()) {
            setBEdit(false);
            setBNew(false);
            if (tblJenisSupp.getRowCount()>0)
            {
                int rowPos = tblJenisSupp.getSelectedRow();
                txtKode.setText(tblJenisSupp.getValueAt(rowPos,0).toString());
                txtNama.setText(tblJenisSupp.getValueAt(rowPos,1).toString());
            }
            setUpBtn();
            tblJenisSupp.setRequestFocusEnabled(true);
            
        }else{
            this.dispose();
        } 
    }
    
    private void udfLoadCmbFilter(){
        cmbFilter.addItem("All");
        cmbFilter.addItem("Kode");
        cmbFilter.addItem("Jenis Supplier");
    }
    
   private void onOpen(String sQry){
        modelJenis = (DefaultTableModel) tblJenisSupp.getModel();
        int i=0;
        try {
            
//            while(modelJenis.getRowCount()>=1){
//                modelJenis.removeRow(0);
//            }
            modelJenis.setNumRows(0);
            
            Statement st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery(sQry);
                
            System.out.println(sQry);
            while (rs.next()) {
            modelJenis.addRow(new Object[]{rs.getString("kode_jenis"),
                                        rs.getString("jenis_supplier")
                                        });
            }
            if (modelJenis.getRowCount()>0) 
                tblJenisSupp.setRowSelectionInterval(0, 0);
                    
            rs.close();
            st.close();
        } catch (SQLException eswl){ System.out.println(eswl.getMessage());}
        if(i>0){
            tblJenisSupp.requestFocusInWindow();
            tblJenisSupp.setRowSelectionInterval(0,0);
        }
   }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnNew = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        cmbFilter = new javax.swing.JComboBox();
        cmbOperator = new javax.swing.JComboBox();
        txtFilter = new javax.swing.JTextField();
        btnFilter = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblJenisSupp = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        txtKode = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNama = new javax.swing.JTextField();

        setBackground(new java.awt.Color(204, 255, 204));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Jenis Supplier");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameClosed(evt);
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

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/New.png"))); // NOI18N
        btnNew.setToolTipText("New     (F5)");
        btnNew.setBorder(null);
        btnNew.setMaximumSize(new java.awt.Dimension(40, 40));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });
        jToolBar1.add(btnNew);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Edit.png"))); // NOI18N
        btnEdit.setToolTipText("Edit     (F4)");
        btnEdit.setBorder(null);
        btnEdit.setMaximumSize(new java.awt.Dimension(40, 40));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        jToolBar1.add(btnEdit);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Delete.png"))); // NOI18N
        btnDelete.setToolTipText("New     (F12)");
        btnDelete.setBorder(null);
        btnDelete.setMaximumSize(new java.awt.Dimension(40, 40));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        jToolBar1.add(btnDelete);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Exit.png"))); // NOI18N
        btnClose.setToolTipText("New     (F12)");
        btnClose.setBorder(null);
        btnClose.setMaximumSize(new java.awt.Dimension(40, 40));
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        jPanel1.add(jToolBar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 180, 50));

        cmbFilter.setMaximumSize(new java.awt.Dimension(150, 24));
        cmbFilter.setMinimumSize(new java.awt.Dimension(20, 24));
        cmbFilter.setPreferredSize(new java.awt.Dimension(20, 24));
        jToolBar2.add(cmbFilter);

        cmbOperator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", ">=", "<", "<=", "Like" }));
        cmbOperator.setMaximumSize(new java.awt.Dimension(150, 24));
        cmbOperator.setMinimumSize(new java.awt.Dimension(20, 24));
        cmbOperator.setPreferredSize(new java.awt.Dimension(20, 24));
        jToolBar2.add(cmbOperator);

        txtFilter.setMaximumSize(new java.awt.Dimension(200, 24));
        txtFilter.setMinimumSize(new java.awt.Dimension(4, 4));
        jToolBar2.add(txtFilter);

        btnFilter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Filter.png"))); // NOI18N
        btnFilter.setToolTipText("Fiter      (F6)");
        btnFilter.setBorder(null);
        btnFilter.setMaximumSize(new java.awt.Dimension(40, 40));
        btnFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFilterActionPerformed(evt);
            }
        });
        jToolBar2.add(btnFilter);

        txtSearch.setMaximumSize(new java.awt.Dimension(200, 24));
        txtSearch.setMinimumSize(new java.awt.Dimension(4, 4));
        jToolBar2.add(txtSearch);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Icon/Search.png"))); // NOI18N
        btnSearch.setToolTipText("Search     (F3)");
        btnSearch.setBorder(null);
        btnSearch.setMaximumSize(new java.awt.Dimension(40, 40));
        btnSearch.setMinimumSize(new java.awt.Dimension(40, 40));
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        jToolBar2.add(btnSearch);

        jPanel1.add(jToolBar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 0, 464, 50));

        tblJenisSupp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Kode", "Jenis Supplier"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblJenisSupp);

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        txtKode.setBackground(new java.awt.Color(255, 255, 153));
        txtKode.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtKode.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKode.setEnabled(false);
        jPanel2.add(txtKode, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 110, 24));

        jLabel1.setForeground(new java.awt.Color(255, 255, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Kode Jenis");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 14, 75, -1));

        jLabel2.setForeground(new java.awt.Color(255, 255, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Jenis Pasien");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 40, -1, -1));

        txtNama.setBackground(new java.awt.Color(255, 255, 153));
        txtNama.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtNama.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 38, 480, 24));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
            .add(jScrollPane1)
            .add(layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                .add(6, 6, 6)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6))
        );

        setBounds(0, 0, 663, 411);
    }// </editor-fold>//GEN-END:initComponents

    private void formInternalFrameClosed(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameClosed
        
    }//GEN-LAST:event_formInternalFrameClosed

    private void formInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameOpened
        modelJenis=(DefaultTableModel)tblJenisSupp.getModel();
        modelJenis.setNumRows(0);
        
        initJDBC(); 
        udfLoadCmbFilter();
        setBEdit(false);
        setBNew(false);
        
        lst = new ListRsbm();
	lst.setVisible(false);
	lst.setSize(500,150);
	lst.con = conn;
        
        requestFocusInWindow(true);
        tblJenisSupp.requestFocusInWindow();
        
        for(int i=0;i<jToolBar2.getComponentCount();i++){
            Component c = jToolBar2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        for(int i=0;i<jToolBar1.getComponentCount();i++){
            Component c = jToolBar1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton") || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
         
         jScrollPane1.addKeyListener(new MyKeyListener());
         tblJenisSupp.addKeyListener(new MyKeyListener());
         
        System.out.println(jPanel1.getComponentCount());
        for(int i=0;i<jPanel1.getComponentCount();i++){
            Component c = jPanel1.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton")   || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        
        System.out.println(jPanel2.getComponentCount());
        for(int i=0;i<jPanel2.getComponentCount();i++){
            Component c = jPanel2.getComponent(i);
            if(c.getClass().getSimpleName().equalsIgnoreCase("JTEXTFIELD")    || c.getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")
            || c.getClass().getSimpleName().equalsIgnoreCase("JTextArea") || c.getClass().getSimpleName().equalsIgnoreCase("JComboBox")
            || c.getClass().getSimpleName().equalsIgnoreCase("JButton")   || c.getClass().getSimpleName().equalsIgnoreCase("JCheckBox")) {
                //System.out.println(c.getClass().getSimpleName());
                c.addKeyListener(new MyKeyListener());
            }
        }
        
        setUpBtn();
    }//GEN-LAST:event_formInternalFrameOpened

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed

        String sQry="select * from fn_jenis_supp_list (0, 'all' ,'', " +
                    "'"+txtSearch.getText().toUpperCase()+"') as (kode_jenis varchar, " +
                    "jenis_supplier varchar)";
        System.out.println(sQry);
        onOpen(sQry);
        txtSearch.requestFocus();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFilterActionPerformed
        String sField="";
        switch(cmbFilter.getSelectedIndex()){
            case 0:{
                sField="all";
                break;
            }
            case 1:{
                sField="kode_jenis";
                break;
            }
            case 2:{
                sField="jenis_supplier";
                break;
            }
            default:{
                sField="jenis_supplier";
                break;
            }
                
        }
        
        String sQry="select * from fn_jenis_supp_list (1, '"+sField+"'" +
                    ",'"+cmbOperator.getSelectedItem().toString()+"', '"+txtFilter.getText().toUpperCase()+"') as (kode_jenis varchar, " +
                    "jenis_supplier varchar)";

        System.out.println(sQry);
        onOpen(sQry);
        txtSearch.requestFocus();
    }//GEN-LAST:event_btnFilterActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        udfCancel();
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        if (tblJenisSupp.getRowCount()>0 || getBEdit())
            udfUpdateData();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        udfEdit();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        udfNew();
    }//GEN-LAST:event_btnNewActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnFilter;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox cmbFilter;
    private javax.swing.JComboBox cmbOperator;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JTable tblJenisSupp;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtKode;
    private javax.swing.JTextField txtNama;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
    
    private Boolean bNew ;
    private Boolean bEdit;
    
    private FocusListener txtFocusListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
            if(e.getSource() instanceof JTextField || e.getSource() instanceof JFormattedTextField){
                ((JTextField)e.getSource()).setBackground(Color.YELLOW);
                if((e.getSource() instanceof JTextField && ((JTextField)e.getSource()).getName()!=null && ((JTextField)e.getSource()).getName().equalsIgnoreCase("textEditor"))){
                    ((JTextField)e.getSource()).setSelectionStart(0);
                    ((JTextField)e.getSource()).setSelectionEnd(((JTextField)e.getSource()).getText().length());
                }
            }
        }


        public void focusLost(FocusEvent e) {
            if(e.getSource().getClass().getSimpleName().equalsIgnoreCase("JTextField")||
                    e.getSource().getClass().getSimpleName().equalsIgnoreCase("JFormattedTextField")){
                ((JTextField)e.getSource()).setBackground(Color.WHITE);

//                if(e.getSource().equals(txtNoPO) && !fn.isListVisible())
//                    udfLoadItemFromPO();
//                else if(e.getSource().equals(txtNoGR))
//                    udfLoadGR();
           }
        }


    } ;
}
