package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class manager_dataAdd extends JFrame {

    database db = new database();
    boolean searchFlag = false;
    JTextField downja1 = new JTextField(4); // 열차의 목적지가 입력되는 필드
    JTextField downja2 = new JTextField(4); // 열차의 차량종류가 입력되는 필드
    JTextField downja3 = new JTextField(4); // 열차의 차량번호가 입력되는 필드
    JTextField downja4 = new JTextField(4); // 열차의 출발시간이 입력되는 필드
    JTextField downja5 = new JTextField(4); // 열차의 타는곳이 입력되는 필드
    JTextField downja6 = new JTextField(4); // 열차의 지연시간이 입력되는 필드

    public manager_dataAdd(){
        setTitle("manager");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        addFrame();

        setVisible(true);
    }

    public void addFrame(){
        JPanel cenPan = new JPanel();
        JLabel cenjl1 = new JLabel("데이터를 추가해주세요");
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
        JButton downjb1 = new JButton("추가하기");
        downjb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean checkException = false; // 예외조건들을 체크하기 위한 boolean 변수
                    db.inputData();
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

        public void inputData(){
            String dest = downja1.getText();
            String name = downja2.getText();
            String num = downja3.getText();
            String time = downja4.getText();
            String platform = downja5.getText();
            String delay = downja6.getText();

            String sql = "INSERT traindata (destination, name, number, time, platform, delay)"
                    + "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, dest);
                pstmt.setString(2, name);
                pstmt.setString(3, num);
                pstmt.setString(4, time);
                pstmt.setString(5, platform);
                pstmt.setString(6, delay);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "데이터 추가 완료 : "+num, "데이터 추가", 1);
                setVisible(false);
                dispose();// 창닫음

            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "데이터 추가 에러", "데이터 추가 실패", 2);
            }
        }
    }
}
