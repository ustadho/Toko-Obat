/*
 * LoginUser.java
 *
 * Created on July 6, 2005, 10:49 PM
 */

package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author  super
 */
public class LoginUser extends javax.swing.JFrame {
    KasirCon conn;
    private Timer timer;
    int i,p,q,iCount=0;
    private String sUserId,sName,sShift,sIDphoto;
    private boolean sSuper;
    private Connection con;
    private DefaultListModel lstModel=new DefaultListModel();
    static int authority;
    static String sKota;
    SysConfig sc=new SysConfig();

    
    /** Creates new form KasirLogin */
    public LoginUser() {
        initComponents();
        setIconImage(new ImageIcon(getClass().getResource("/resources/uTorrent.gif")).getImage());
//        jLabel3.setVisible(false);
//        cmbGudang.setVisible(false);

       // this.setState(Frame.ICONIFIED);
    }
    private FocusListener txtFoculListener=new FocusListener() {
        public void focusGained(FocusEvent e) {
           Component c=(Component) e.getSource();
           c.setBackground(g1);
           //c.setForeground(fPutih);
           //c.setCaretColor(new java.awt.Color(255, 255, 255));
        }
        public void focusLost(FocusEvent e) {
            Component c=(Component) e.getSource();
            c.setBackground(fPutih);
            //c.setForeground(fHitam);
        }
   };
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        txtKasir = new javax.swing.JTextField();
        txtPass = new javax.swing.JPasswordField();
        btnOK = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btnCancel = new javax.swing.JButton();
        pBar = new javax.swing.JProgressBar();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        optShiftSiang = new javax.swing.JRadioButton();
        optShiftPagi = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();

        jLabel5.setText("jLabel5");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Login");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(204, 153, 0));
        setForeground(new java.awt.Color(204, 153, 0));
        setLocationByPlatform(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("User");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 50, 70, 20));

        txtKasir.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        txtKasir.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtKasir.setNextFocusableComponent(txtPass);
        txtKasir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtKasirActionPerformed(evt);
            }
        });
        txtKasir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtKasirKeyPressed(evt);
            }
        });
        getContentPane().add(txtKasir, new org.netbeans.lib.awtextra.AbsoluteConstraints(192, 50, 140, 20));

        txtPass.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        txtPass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPassActionPerformed(evt);
            }
        });
        getContentPane().add(txtPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(192, 75, 140, 20));

        btnOK.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });
        btnOK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnOKKeyPressed(evt);
            }
        });
        getContentPane().add(btnOK, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 150, 90, 30));

        jLabel2.setBackground(new java.awt.Color(255, 255, 0));
        jLabel2.setText("    ");
        jLabel2.setOpaque(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 20, 20));

        btnCancel.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 150, 90, 30));
        getContentPane().add(pBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 185, 330, 20));

        jLabel9.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText("SHIFT");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 120, 60, 20));

        jLabel10.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Password");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 75, 70, 20));

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/Icon/Login.png"))); // NOI18N
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 20, 120, 120));

        buttonGroup1.add(optShiftSiang);
        optShiftSiang.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        optShiftSiang.setForeground(new java.awt.Color(0, 0, 204));
        optShiftSiang.setMnemonic('S');
        optShiftSiang.setText("SIANG");
        getContentPane().add(optShiftSiang, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, 70, -1));

        buttonGroup1.add(optShiftPagi);
        optShiftPagi.setFont(new java.awt.Font("DejaVu Sans", 1, 12)); // NOI18N
        optShiftPagi.setForeground(new java.awt.Color(0, 0, 204));
        optShiftPagi.setMnemonic('P');
        optShiftPagi.setSelected(true);
        optShiftPagi.setText("PAGI");
        getContentPane().add(optShiftPagi, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 120, 70, -1));

        jLabel8.setBackground(new java.awt.Color(204, 204, 255));
        jLabel8.setOpaque(true);
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, -2, 360, 210));

        setSize(new java.awt.Dimension(364, 244));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void txtKasirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKasirActionPerformed
        txtPass.setRequestFocusEnabled(true);
    }//GEN-LAST:event_txtKasirActionPerformed

    private void txtKasirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtKasirKeyPressed
        if(evt.getKeyCode()==evt.VK_ENTER){
            txtPass.requestFocusInWindow();
        }
    }//GEN-LAST:event_txtKasirKeyPressed

    private void btnOKKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnOKKeyPressed
        LogMasuk();
    }//GEN-LAST:event_btnOKKeyPressed

    private void txtPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPassActionPerformed
        LogMasuk();
    }//GEN-LAST:event_txtPassActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //image/Login.jpg
       // String fileImage="image/Login.jpg";                
     //   LabelIcon(fileImage,jLabel8);
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        } catch (Exception e) {}
        //SystemConfig sc=new SystemConfig();
        
        conn = new KasirCon("jdbc:postgresql://"+sc.getServerLoc()+":5432/"+sc.getDBName(), "apot3k", "bismillah", this);
        //conn = new KasirCon("jdbc:postgresql://"+sc.getServerLoc()+":5432/"+sc.getDBName(), "joss", "123", this);
        
        if (conn.gettErrLog()==true){
            con=conn.getCon();
        }
        txtKasir.addFocusListener(txtFoculListener);
        txtPass.addFocusListener(txtFoculListener);
        
        i=pBar.getX();            
        timer = new Timer();
        timer.schedule(new DoTick(), 0,5);
        java.text.SimpleDateFormat fdate2 = new java.text.SimpleDateFormat("HH:mm");
        
        Date jamNow = Calendar.getInstance().getTime();            
        System.out.println("JAM: "+fdate2.format(jamNow.getTime()));
        SimpleDateFormat hh=new SimpleDateFormat("HH:mm");

        optShiftPagi.setSelected(hh.format(jamNow).compareTo("14:")<=0? true :false);
        optShiftSiang.setSelected(hh.format(jamNow).compareTo("14:")<=0? false :true);

            //this.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            //this.setExtendedState(MAXIMIZED_BOTH);
            
//        try{
//            Statement st=con.createStatement();
//            ResultSet rs=st.executeQuery("select kode_resort, coalesce(nama_resort, '') as nama_resort from m_Resort");
//
//            lstModel.removeAllElements();
//
//            int idx=0;
//            lstModel.add(idx, "");
//            cmbGudang.addItem("<Semua Resort>");
//            idx++;
//
//            while(rs.next()){
//                cmbGudang.addItem(rs.getString("nama_resort"));
//                lstModel.add(idx, rs.getString("kode_resort"));
//
//                idx++;
//            }
//
//        }catch(SQLException se){
//            JOptionPane.showMessageDialog(this, se.getMessage());
//        }
    }//GEN-LAST:event_formWindowOpened

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnCancelActionPerformed

    public void setUserID(String newUser){
        sUserId=newUser;
    }
    
    public void setUserName(String userName){
        sName=userName;
    }
       
    
    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        LogMasuk();        
    }//GEN-LAST:event_btnOKActionPerformed

    private void LogMasuk(){
        SysConfig sc = new SysConfig();
        String pass ="";
        char chrPass[] = txtPass.getPassword();
        
        for(int i=0; i<chrPass.length; i++){
            pass = pass+chrPass[i];
            chrPass[i]='0';            
        }
       
        
        boolean sukses= con!=null;
        
        try{
            ResultSet rs=con.createStatement().executeQuery("select * from m_user where username='"+txtKasir.getText()+"' "
                    + "and pwd=md5('"+pass+"')");
            
            if(rs.next()){
                sukses=true;
                sUserId=rs.getString("user_id");
            }else{
                JOptionPane.showMessageDialog(this, "Silakan masukkan nama user & passwod dengan benar!");
                txtKasir.requestFocus();
                txtKasir.setSelectionStart(0);
                txtKasir.setSelectionEnd(txtKasir.getText().length());
                return;
            }
        }catch(SQLException se){
            JOptionPane.showMessageDialog(this, se.getMessage());
        }
        
        
            if (sukses ){          //&& rs.next()      
                sName=txtKasir.getText(); //rs.getString("username").trim();
//                authority=rs.getInt("profile");
                //sKota=lstModel.getElementAt(cmbGudang.getSelectedIndex()).toString();
                
                timer.cancel();
                setVisible(false);                
                MainForm fMain  = new MainForm();
                fMain.setConn(con);
                //fMain.setUserProfile(rs.getInt("profile"));
                fMain.setUserName(sName);
                fMain.setUserID(sUserId);
                fMain.setServerLocation(sc.getServerLoc());
                fMain.setShift(optShiftPagi.isSelected()? "P": "S");
                fMain.udfSetUserMenu();
                fMain.setVisible(true);
            }
//        }catch(SQLException se) {
//            JOptionPane.showMessageDialog(this, se.getMessage());
//        }
        
    }
    
    
    class DoTick extends TimerTask {
      public void run() {
            if ((i)<=pBar.getWidth() || q==0){                
                p=i;
                i++;
                q=i;
            }else{                
                p=q;
                q--;
                if (q==pBar.getX()){
                    i=pBar.getX();
                }                
            }
            jLabel2.setLocation(p, jLabel2.getY());            
      } 
    }
    private void LabelIcon(String aFile,javax.swing.JLabel newlbl) {              
       javax.swing.ImageIcon myIcon = new javax.swing.ImageIcon(getClass().getResource(aFile));
       newlbl.setIcon(myIcon);
    }
    
    /**
     * @param args the command line arguments
     
    public static void main(String args[]) {
        //java.awt.EventQueue.invokeLater(new Runnable() {
        //     public void run() {                
        //        new KasirLoginOny().setVisible(true);
        //    }
        //});
    }
     * 
     * 
     */
    
    Color g1 = new Color(153,255,255);
    
    Color fHitam = new Color(0,0,0);
    Color fPutih = new Color(255,255,255); 
    
    Color crtHitam =new java.awt.Color(0, 0, 0);
    Color crtPutih = new java.awt.Color(255, 255, 255); 
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton optShiftPagi;
    private javax.swing.JRadioButton optShiftSiang;
    private javax.swing.JProgressBar pBar;
    private javax.swing.JTextField txtKasir;
    private javax.swing.JPasswordField txtPass;
    // End of variables declaration//GEN-END:variables
}
