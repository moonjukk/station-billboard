package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import static main.mainWindow.contents_display;


public class managerWindow extends JFrame{
    private JPanel rightPan;
    JPanel scrollPan;
    database db = new database();

    String header[] = {"행선지","열차이름","열차번호","출발시각","타는곳","지연"};
    static String[][] contents = new String[99][6];
    boolean displayFlag = false;
    static boolean dataChangeSensingFlag = false; // 데이터 변화를 감지하는 flag

    public managerWindow() {
        setTitle("manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2));
        leftPan();
        rightPan();
        setVisible(true);
    }

    public void leftPan(){
        inputData();
    }

    // 데이터베이스 값을 받아오는 메소드
    public void inputData(){
        db.getData(); // 데이터베이스에서 값 받아오기
        JTable contentsTable = new JTable(contents,header);
        JScrollPane scrollpane = new JScrollPane(contentsTable);
        scrollpane.setVisible(true);
        add(scrollpane);
    }

    public void rightPan(){
        rightPan = new JPanel();
        rightPan.setBackground(Color.PINK);
        rightPan.setLayout(new GridLayout(2,1));

        upPan();
        downPan();

        add(rightPan);
    }

    public void upPan(){
        JPanel upPan = new JPanel();
        rightPan.add(upPan);
    }

    public void downPan(){
        JPanel downPan = new JPanel();
        downPan.setLayout(new GridLayout(2,2));
        JButton jb1 = new JButton("수정하기");
        jb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new manager_dataModify();
            }
        });
        JButton jb2 = new JButton("추가하기");
        jb2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new manager_dataAdd();
            }
        });
        JButton jb3 = new JButton("삭제하기");
        jb3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new manager_dataDelete();
            }
        });
        JButton jb4 = new JButton("창닫기");
        jb4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();// 창닫음
            }
        });
        downPan.add(jb1);
        downPan.add(jb2);
        downPan.add(jb3);
        downPan.add(jb4);
        rightPan.add(downPan);
    }

    class database extends DBStart{
        public void getData(){
            Date date = new Date();
            SimpleDateFormat myInTime = new SimpleDateFormat("HHmm");
            int myTime = Integer.parseInt(myInTime.format(date));
            //int myTime = 1005;
            System.out.println("현재 시간 : "+myTime);

            try{
                ResultSet rs1 = stmt.executeQuery("select * from traindata");
                rs1.first(); // 커서를 첫번째 행으로 가리킨다.

                int i=0;
                int a=0;

                do{
                    int j=0;

                    contents[i][j] = rs1.getString("destination");
                    j++;
                    contents[i][j] = rs1.getString("name");
                    j++;
                    contents[i][j] = rs1.getString("number");
                    j++;

                    if(displayFlag==false && Integer.parseInt(rs1.getString("time"))>=myTime){

                        contents_display[a][0] = rs1.getString("destination");
                        contents_display[a][1] = rs1.getString("name");
                        contents_display[a][2] = rs1.getString("number");
                        contents_display[a][3] = rs1.getString("time");
                        contents_display[a][4] = rs1.getString("platform");
                        contents_display[a][5] = rs1.getString("delay");
                        a++;
                    }

                    contents[i][j] = rs1.getString("time");
                    j++;
                    contents[i][j] = rs1.getString("platform");
                    j++;
                    contents[i][j] = rs1.getString("delay");
                    j++;

                    i++;
                }while(rs1.next()); // 데이터 베이스 다음행으로 커서를 옮긴다.
                System.out.println("데이터 로드 성공 : 총 "+ i +" 건");

                // 입력된 데이터값 정렬시키기
                sortArray(contents_display);
                sortArray(contents); // 관리자 리스트는 정렬되지 않는 문제

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