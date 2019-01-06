package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import static main.mainWindow.contents_display;


public class manager_login extends JFrame {

    database db = new database();
    JLabel jl1 = new JLabel("데이터베이스 관리자용");
    JLabel jl2 = new JLabel("1차 비밀번호"); // 1차 비밀번호 : rhksflwk (관리자)
    JLabel jl3 = new JLabel("2차 비밀번호"); // 2차 비밀번호 : 1234
    JTextField jf1 = new JTextField(10); // 1차 비밀번호를 입력하는 필드
    JTextField jf2 = new JTextField(10); // 2차 비밀번호를 입력하는 필드
    String passwd1;
    String passwd2;

    public manager_login(){
        setTitle("manager_login");
        setSize(240, 270);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1));

        db.getData();
        db.loginData();

        JPanel jp1 = new JPanel();
        jp1.add(jl1);
        add(jp1);
        JPanel jp2 = new JPanel();
        jp2.add(jl2);
        jp2.add(jf1);
        add(jp2);
        JPanel jp3 = new JPanel();
        jp2.add(jl3);
        jp2.add(jf2);
        add(jp3);
        JPanel jp4 = new JPanel();
        JButton jb1 = new JButton("로그인");
        jb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String passwd_a = jf1.getText();
                String passwd_b = jf2.getText();

                if (passwd_a.equals(passwd1)) {
                    if (passwd_b.equals(passwd2)) {
                        new managerWindow();
                        setVisible(false);
                        dispose();// 창닫음
                    } else
                        JOptionPane.showMessageDialog(null, "로그인 실패 : 관리자만 로그인하시오", "로그인 실패", 2);
                } else
                    JOptionPane.showMessageDialog(null, "로그인 실패 : 관리자만 로그인하시오", "로그인 실패", 2);
            }
        });
        JButton jb2 = new JButton("창닫기");
        jb2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();// 창닫음
            }
        });
        jp4.add(jb1);
        jp4.add(jb2);
        add(jp4);

        setVisible(true);
    }

    class database extends DBStart{
        public void loginData() {
            try{
                ResultSet rs1 = stmt.executeQuery("select * from login");
                rs1.first(); // 커서를 첫번째 행으로 가리킨다.
                passwd1 = rs1.getString("password_first");
                passwd2 = rs1.getString("password_second");
            } catch(SQLException v){
                v.printStackTrace(); // 오류 출력
                System.out.println("로그인 db 오류");
            }
        }
        public void getData(){
            Date date = new Date();
            SimpleDateFormat myInTime = new SimpleDateFormat("HHmm");
            int myTime = Integer.parseInt(myInTime.format(date));
            System.out.println("현재 시간 : "+myTime);

            try{
                ResultSet rs1 = stmt.executeQuery("select * from traindata");
                rs1.first(); // 커서를 첫번째 행으로 가리킨다.

                int i=0;
                int a=0;

                do{
                    if(Integer.parseInt(rs1.getString("time"))>=myTime){
                        contents_display[a][0] = rs1.getString("destination");
                        contents_display[a][1] = rs1.getString("name");
                        contents_display[a][2] = rs1.getString("number");
                        contents_display[a][3] = rs1.getString("time");
                        contents_display[a][4] = rs1.getString("platform");
                        contents_display[a][5] = rs1.getString("delay");
                        a++;
                    }
                    i++;
                }while(rs1.next()); // 데이터 베이스 다음행으로 커서를 옮긴다.
                System.out.println("데이터 로드 성공 : 총 "+ i +" 건");

                // 입력된 데이터값 정렬시키기
                sortArray(contents_display);

            } catch(SQLException v){
                v.printStackTrace(); // 오류 출력
            }
        }
    }


    // 데이터를 내림차순으로 정렬시키는 메소드
    public static void sortArray(Object[][] arr){
        Arrays.sort(arr, new Comparator<Object[]>() {
            public int compare(Object[] arr1, Object[] arr2) {
                if(arr1[3]!=null && arr2[3]!=null) {
                    if( ((Comparable)arr1[3]).compareTo(arr2[3]) > 0 )
                        return 1;
                    else
                        return -1;
                }
                else
                    return 0;
            }
        });
    }

}
