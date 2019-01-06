package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class manager_dataModify extends JFrame {

    database db = new database();
    boolean searchFlag = false;
    String searchNum;
    String searchTime;
    JTextField downja1 = new JTextField(4); // 열차의 목적지가 입력되는 필드
    JTextField downja2 = new JTextField(4); // 열차의 차량종류가 입력되는 필드
    JTextField downja3 = new JTextField(4); // 열차의 차량번호가 입력되는 필드
    JTextField downja4 = new JTextField(4); // 열차의 출발시간이 입력되는 필드
    JTextField downja5 = new JTextField(4); // 열차의 타는곳이 입력되는 필드
    JTextField downja6 = new JTextField(4); // 열차의 지연시간이 입력되는 필드

    public manager_dataModify(){
        setTitle("manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1));

        modifyFrame();

        setVisible(true);
    }

    public void modifyFrame(){
        JPanel upPan = new JPanel();
        JLabel upjl1 = new JLabel("열차번호");
        JTextField upja1 = new JTextField(4); // 열차번호를 입력하는 필드
        JLabel upjl2 = new JLabel("출발시각");
        JTextField upja2 = new JTextField(4); // 열차시각 입력하는 필드
        JButton upjb1 = new JButton("검색");
        upjb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String num = upja1.getText();
                String time = upja2.getText();
                db.getData(num,time);
            }
        });
        upPan.add(upjl1);
        upPan.add(upja1);
        upPan.add(upjl2);
        upPan.add(upja2);
        upPan.add(upjb1);
        add(upPan);

        JPanel cenPan = new JPanel();
        JLabel cenjl1 = new JLabel("데이터를 변경해주세요");
        cenPan.add(cenjl1);
        add(cenPan);

        JPanel downPan = new JPanel();
        downPan.setLayout(new GridLayout(3,1));
        JPanel downPan_1 = new JPanel();
        downPan_1.setLayout(new GridLayout(1,6));
        JPanel downPan_2 = new JPanel();
        downPan_2.setLayout(new GridLayout(1,6));
        JPanel downPan_3 = new JPanel();
        JLabel downjl1 = new JLabel("행선지");
        JLabel downjl2 = new JLabel("열차이름");
        JLabel downjl3 = new JLabel("열차번호");
        JLabel downjl4 = new JLabel("출발시간");
        JLabel downjl5 = new JLabel("타는곳");
        JLabel downjl6 = new JLabel("지연");
        JButton downjb1 = new JButton("수정하기");
        downjb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean checkException = false; // 예외조건들을 체크하기 위한 boolean 변수
                    db.modifyData(searchNum,searchTime);
            }
        });
        JButton downjb2 = new JButton("취소하기");
        downjb2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();// 창닫음
            }
        });
        downPan_1.add(downjl1);
        downPan_1.add(downjl2);
        downPan_1.add(downjl3);
        downPan_1.add(downjl4);
        downPan_1.add(downjl5);
        downPan_1.add(downjl6);
        downPan_2.add(downja1);
        downPan_2.add(downja2);
        downPan_2.add(downja3);
        downPan_2.add(downja4);
        downPan_2.add(downja5);
        downPan_2.add(downja6);
        downPan_3.add(downjb1);
        downPan_3.add(downjb2);
        downPan.add(downPan_1);
        downPan.add(downPan_2);
        downPan.add(downPan_3);
        add(downPan);
    }

    class database extends DBStart{

        public void modifyData(String Num,String TIme){

            String dest = downja1.getText();
            String name = downja2.getText();
            String num = downja3.getText();
            String time = downja4.getText();
            String platform = downja5.getText();
            String delay = downja6.getText();

            String sql = "UPDATE traindata SET destination=?, name=?, number=?, time=?, platform=?, delay=? WHERE number=?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setString(1, dest);
                pstmt.setString(2, name);
                pstmt.setString(3, num);
                pstmt.setString(4, time);
                pstmt.setString(5, platform);
                pstmt.setString(6, delay);
                pstmt.setString(7, Num);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "데이터 수정 완료 : "+num, "데이터 수정", 1);
                managerWindow.dataChangeSensingFlag = true;
                setVisible(false);
                dispose();// 창닫음
            } catch(SQLException e1){
                JOptionPane.showMessageDialog(null, "데이터 수정 에러", "데이터 수정 실패", 2);
                e1.printStackTrace(); // 오류 출력
            }
        }

        public void getData(String num,String time){
            try{
                ResultSet rs1 = stmt.executeQuery("select * from traindata");
                rs1.first(); // 커서를 첫번째 행으로 가리킨다.
                do {
                    if (num.equals(rs1.getString("number"))){
                        if (time.equals(rs1.getString("time"))){
                            downja1.setText(rs1.getString("destination"));
                            downja2.setText(rs1.getString("name"));
                            downja3.setText(rs1.getString("number"));
                            downja4.setText(rs1.getString("time"));
                            downja5.setText(rs1.getString("platform"));
                            downja6.setText(rs1.getString("delay"));
                            JOptionPane.showMessageDialog(null, "데이터 조회 완료", "데이터 조회", 1);
                            searchFlag = true;
                            searchNum = num;
                            searchTime = time;
                            break;
                        }
                    }
                } while (rs1.next()); // 데이터베이스를 다움행으로 커서를 옮긴다.
                if(searchFlag==false){
                    downja1.setText("");
                    downja2.setText("");
                    downja3.setText("");
                    downja4.setText("");
                    downja5.setText("");
                    downja6.setText("");
                    JOptionPane.showMessageDialog(null, "존재하지 않는 데이터입니다.", "데이터 조회", 2);
                }
                searchFlag=false;
            } catch(SQLException v){
                v.printStackTrace(); // 오류 출력
            }
        }
    }
}
