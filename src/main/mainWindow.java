package main;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import javax.swing.*;

public class mainWindow extends JFrame implements Runnable{
    private JPanel jpCenter; // 제목을 나타내는 곳

    database db = new database();

    JPanel content1_1 = new JPanel();
    JPanel content2_1 = new JPanel();
    JPanel content3_1 = new JPanel();
    JPanel content4_1 = new JPanel();
    JPanel content5_1 = new JPanel();
    JPanel content6_1 = new JPanel();

    private JLabel jl1 = new JLabel("행선지");
    private JLabel jl2 = new JLabel("열차이름");
    private JLabel jl3 = new JLabel("열차번호");
    private JLabel jl4 = new JLabel("출발시각");
    private JLabel jl5 = new JLabel("타는곳");
    private JLabel jl6 = new JLabel("승차안내");
    private JLabel jl7 = new JLabel("지연");

    static JLabel timeLabel = new JLabel();

    static  String[][] contents_display = new String[99][6];

    String timePormat,c,d; // 1200의 시간양식을 12:00으로 바꾸는데 사용
    int timeTemp; // 시간을 비교하여 승차중, 승차대기를 나타내기 위해 사용
    int timeTemp1; // 시간을 비교하여 승차중, 승차대기를 나타내기 위해 사용
    int timeTemp2; // 시간을 비교하여 승차중, 승차대기를 나타내기 위해 사용
    String timeFlag; // 시간을 비교하여 승차중, 승차대기를 나타내기 위해 사용
    boolean markFlag = false;// ktx는 노란색으로 표시하기 위해 사용
    boolean watchFlag = false; // 프로세스가 종료되면 스레드도 종료
    Thread thread; // 쓰레드

    static boolean transFlag = false;

    public mainWindow() {
        setTitle("moon2019");
        setSize(1680, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 1));

        mkPan();

    }

    public void mkPan(){
        titlePan();
        mainPan();

        content1();
        content2();
        content3();
        content4();
        content5();
        content6();

        setVisible(true);
    }

    public void titlePan(){
        JPanel titlePan = new JPanel();
        titlePan.setLayout(new GridLayout(1,3));

        JPanel titleLeft = new JPanel();
        titleLeft.setBackground(Color.BLACK);
        titlePan.add(titleLeft);

        JPanel titleCenter = new JPanel();
        titleCenter.setBackground(Color.BLACK);
        JLabel titleLabel = new JLabel("열 차 출 발 안 내");
        titleLabel.setForeground(Color.GREEN);
        titleLabel.setFont(new Font(null, Font.BOLD, 50));
        titleCenter.add(titleLabel);
        titlePan.add(titleCenter);
        JPanel titleRight = new JPanel();
        titleRight.setBackground(Color.BLACK);
        thread = new Thread(this); // 시계쓰레드 작동
        thread.start(); // 스레드 작동
        titleRight.add(timeLabel);
        titlePan.add(titleRight);
        add(titlePan);
    }

    public void whatTime(){
        Date date = new Date();
        SimpleDateFormat myInTime = new SimpleDateFormat("HHmmss");
        String myTime = myInTime.format(date);
        int time = Integer.parseInt(myTime);
        int H = time/10000;
        int M = time/100 - H*100;
        int S = time - H*10000 - M*100;
        timeLabel.setText(H + "시 " + M + "분 " + S + "초");
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font(null, Font.BOLD, 30));
        timeTemp = H*100 + M;
        timeTemp1 = H;
        timeTemp2 = M;
        // 3초마다 데이터 업데이트 실행하기 위한 flag 값 변경
        if(S%3==0){
            transFlag = true;
        }
        else{
            transFlag = false;
        }
    }

    // 전광판에 보이는 시간형식을 00:00으로
    public void makeTimePormat(String arr){
        if(arr!=null){
            int makeTime = Integer.parseInt(arr);
            int a = makeTime/100;
            if(a<10) {
                c = "0" + a;
            }
            else {
                c = Integer.toString(a);
            }
            int b = makeTime - a*100;
            if(b<10){
                d = "0" + b;
            }
            else{
                d = Integer.toString(b);
            }
            timePormat = c + ":" + d;
        }
        else{
            timePormat="";
        }
    }

    // 승차준비인지(승차 5분이내) 승차중(같은시간)인지 구별하는 메소드
    public void distinctionTime(String arr){
        if(arr!=null){
            int trainTime = Integer.parseInt(arr);
            int traintTimeTemp1 = trainTime/100;
            int traintTimeTemp2 = trainTime - traintTimeTemp1*100;

            if( (trainTime-timeTemp <= 5 && trainTime-timeTemp>0) || (traintTimeTemp1-timeTemp1 == 1 && traintTimeTemp2-timeTemp2+100 <=45 && traintTimeTemp2-timeTemp2+100>=41)){
                timeFlag = "승차대기";
            }
            else if(trainTime-timeTemp == 0){
                timeFlag = "승차중";
            }
            else{
                timeFlag = "";
            }
        }
        else{
            timeFlag = "";
        }
    }

    // ktx종류의 열차는 노란색으로 표시하기 위해 사용하는 메소드
    public void ktxMarkYellow(String arr){
        if(arr.equals("KTX") || arr.equals("KTX-산천") || arr.equals("SRT")){
            markFlag = true;
        }
        else
            markFlag = false;
    }

    // 목차를 나타내는 패널부분
    public void mainPan() {
        jpCenter = new JPanel();
        jpCenter.setBackground(Color.BLACK);
        jpCenter.setLayout(new GridLayout(1, 7));

        jl1.setForeground(Color.GREEN);
        jl1.setFont(new Font(null, Font.BOLD, 40));
        jl2.setForeground(Color.GREEN);
        jl2.setFont(new Font(null, Font.BOLD, 40));
        jl3.setForeground(Color.GREEN);
        jl3.setFont(new Font(null, Font.BOLD, 40));
        jl4.setForeground(Color.GREEN);
        jl4.setFont(new Font(null, Font.BOLD, 40));
        jl5.setForeground(Color.GREEN);
        jl5.setFont(new Font(null, Font.BOLD, 40));
        jl6.setForeground(Color.GREEN);
        jl6.setFont(new Font(null, Font.BOLD, 40));
        jl7.setForeground(Color.GREEN);
        jl7.setFont(new Font(null, Font.BOLD, 40));

        JPanel jp1 = new JPanel();
        jp1.setBackground(Color.BLACK);
        jp1.add(jl1);
        JPanel jp2 = new JPanel();
        jp2.setBackground(Color.BLACK);
        jp2.add(jl2);
        JPanel jp3 = new JPanel();
        jp3.setBackground(Color.BLACK);
        jp3.add(jl3);
        JPanel jp4 = new JPanel();
        jp4.setBackground(Color.BLACK);
        jp4.add(jl4);
        JPanel jp5 = new JPanel();
        jp5.setBackground(Color.BLACK);
        jp5.add(jl5);
        JPanel jp6 = new JPanel();
        jp6.setBackground(Color.BLACK);
        jp6.add(jl6);
        JPanel jp7 = new JPanel();
        jp7.setBackground(Color.BLACK);
        jp7.add(jl7);

        jpCenter.add(jp1);
        jpCenter.add(jp2);
        jpCenter.add(jp3);
        jpCenter.add(jp4);
        jpCenter.add(jp5);
        jpCenter.add(jp6);
        jpCenter.add(jp7);


        add(jpCenter);
    }

    public void content1(){
        content1_1.setBackground(Color.BLACK);
        content1_1.setLayout(new GridLayout(1, 7));

        JLabel jl1_1 = new JLabel(contents_display[0][0]);
        jl1_1.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl1_2 = new JLabel(contents_display[0][1]);
        jl1_2.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl1_3 = new JLabel(contents_display[0][2]);
        jl1_3.setFont(new Font(null, Font.BOLD, 40));
        makeTimePormat(contents_display[0][3]);
        JLabel jl1_4 = new JLabel(timePormat);
        jl1_4.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl1_5 = new JLabel(contents_display[0][4]);
        jl1_5.setFont(new Font(null, Font.BOLD, 40));
        distinctionTime(contents_display[0][3]);
        JLabel jl1_6 = new JLabel(timeFlag);
        jl1_6.setForeground(Color.RED);
        jl1_6.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl1_7 = new JLabel(contents_display[0][5]);
        jl1_7.setFont(new Font(null, Font.BOLD, 40));

        ktxMarkYellow(contents_display[0][1]); // ktx 종류의 열차는 노란색으로 표시
        if(markFlag==true){
            jl1_1.setForeground(Color.YELLOW);
            jl1_2.setForeground(Color.YELLOW);
            jl1_3.setForeground(Color.YELLOW);
            jl1_4.setForeground(Color.YELLOW);
            jl1_5.setForeground(Color.YELLOW);
            jl1_7.setForeground(Color.YELLOW);
        }
        else{
            jl1_1.setForeground(Color.GREEN);
            jl1_2.setForeground(Color.GREEN);
            jl1_3.setForeground(Color.GREEN);
            jl1_4.setForeground(Color.GREEN);
            jl1_5.setForeground(Color.GREEN);
            jl1_7.setForeground(Color.GREEN);
        }

        JPanel jp1_1 = new JPanel();
        jp1_1.setBackground(Color.BLACK);
        jp1_1.add(jl1_1);
        JPanel jp1_2 = new JPanel();
        jp1_2.setBackground(Color.BLACK);
        jp1_2.add(jl1_2);
        JPanel jp1_3 = new JPanel();
        jp1_3.setBackground(Color.BLACK);
        jp1_3.add(jl1_3);
        JPanel jp1_4 = new JPanel();
        jp1_4.setBackground(Color.BLACK);
        jp1_4.add(jl1_4);
        JPanel jp1_5 = new JPanel();
        jp1_5.setBackground(Color.BLACK);
        jp1_5.add(jl1_5);
        JPanel jp1_6 = new JPanel();
        jp1_6.setBackground(Color.BLACK);
        jp1_6.add(jl1_6);
        JPanel jp1_7 = new JPanel();
        jp1_7.setBackground(Color.BLACK);
        jp1_7.add(jl1_7);

        content1_1.add(jp1_1);
        content1_1.add(jp1_2);
        content1_1.add(jp1_3);
        content1_1.add(jp1_4);
        content1_1.add(jp1_5);
        content1_1.add(jp1_6);
        content1_1.add(jp1_7);

        add(content1_1);
    }

    public void content2(){
        content2_1.setBackground(Color.BLACK);
        content2_1.setLayout(new GridLayout(1, 7));

        JLabel jl2_1 = new JLabel(contents_display[1][0]);
        jl2_1.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl2_2 = new JLabel(contents_display[1][1]);
        jl2_2.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl2_3 = new JLabel(contents_display[1][2]);
        jl2_3.setFont(new Font(null, Font.BOLD, 40));
        makeTimePormat(contents_display[1][3]);
        JLabel jl2_4 = new JLabel(timePormat);
        jl2_4.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl2_5 = new JLabel(contents_display[1][4]);
        jl2_5.setFont(new Font(null, Font.BOLD, 40));
        distinctionTime(contents_display[1][3]);
        JLabel jl2_6 = new JLabel(timeFlag);
        jl2_6.setForeground(Color.RED);
        jl2_6.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl2_7 = new JLabel(contents_display[1][5]);
        jl2_7.setFont(new Font(null, Font.BOLD, 40));

        ktxMarkYellow(contents_display[1][1]); // ktx 종류의 열차는 노란색으로 표시
        if(markFlag==true){
            jl2_1.setForeground(Color.YELLOW);
            jl2_2.setForeground(Color.YELLOW);
            jl2_3.setForeground(Color.YELLOW);
            jl2_4.setForeground(Color.YELLOW);
            jl2_5.setForeground(Color.YELLOW);
            jl2_7.setForeground(Color.YELLOW);
        }
        else{
            jl2_1.setForeground(Color.GREEN);
            jl2_2.setForeground(Color.GREEN);
            jl2_3.setForeground(Color.GREEN);
            jl2_4.setForeground(Color.GREEN);
            jl2_5.setForeground(Color.GREEN);
            jl2_7.setForeground(Color.GREEN);
        }

        JPanel jp2_1 = new JPanel();
        jp2_1.setBackground(Color.BLACK);
        jp2_1.add(jl2_1);
        JPanel jp2_2 = new JPanel();
        jp2_2.setBackground(Color.BLACK);
        jp2_2.add(jl2_2);
        JPanel jp2_3 = new JPanel();
        jp2_3.setBackground(Color.BLACK);
        jp2_3.add(jl2_3);
        JPanel jp2_4 = new JPanel();
        jp2_4.setBackground(Color.BLACK);
        jp2_4.add(jl2_4);
        JPanel jp2_5 = new JPanel();
        jp2_5.setBackground(Color.BLACK);
        jp2_5.add(jl2_5);
        JPanel jp2_6 = new JPanel();
        jp2_6.setBackground(Color.BLACK);
        jp2_6.add(jl2_6);
        JPanel jp2_7 = new JPanel();
        jp2_7.setBackground(Color.BLACK);
        jp2_7.add(jl2_7);

        content2_1.add(jp2_1);
        content2_1.add(jp2_2);
        content2_1.add(jp2_3);
        content2_1.add(jp2_4);
        content2_1.add(jp2_5);
        content2_1.add(jp2_6);
        content2_1.add(jp2_7);

        add(content2_1);
    }

    public void content3(){
        content3_1.setBackground(Color.BLACK);
        content3_1.setLayout(new GridLayout(1, 7));

        JLabel jl3_1 = new JLabel(contents_display[2][0]);
        jl3_1.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl3_2 = new JLabel(contents_display[2][1]);
        jl3_2.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl3_3 = new JLabel(contents_display[2][2]);
        jl3_3.setFont(new Font(null, Font.BOLD, 40));
        makeTimePormat(contents_display[2][3]);
        JLabel jl3_4 = new JLabel(timePormat);
        jl3_4.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl3_5 = new JLabel(contents_display[2][4]);
        jl3_5.setFont(new Font(null, Font.BOLD, 40));
        distinctionTime(contents_display[2][3]);
        JLabel jl3_6 = new JLabel(timeFlag);
        jl3_6.setForeground(Color.RED);
        jl3_6.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl3_7 = new JLabel(contents_display[2][5]);
        jl3_7.setFont(new Font(null, Font.BOLD, 40));

        ktxMarkYellow(contents_display[2][1]); // ktx 종류의 열차는 노란색으로 표시
        if(markFlag==true){
            jl3_1.setForeground(Color.YELLOW);
            jl3_2.setForeground(Color.YELLOW);
            jl3_3.setForeground(Color.YELLOW);
            jl3_4.setForeground(Color.YELLOW);
            jl3_5.setForeground(Color.YELLOW);
            jl3_7.setForeground(Color.YELLOW);
        }
        else{
            jl3_1.setForeground(Color.GREEN);
            jl3_2.setForeground(Color.GREEN);
            jl3_3.setForeground(Color.GREEN);
            jl3_4.setForeground(Color.GREEN);
            jl3_5.setForeground(Color.GREEN);
            jl3_7.setForeground(Color.GREEN);
        }


        JPanel jp3_1 = new JPanel();
        jp3_1.setBackground(Color.BLACK);
        jp3_1.add(jl3_1);
        JPanel jp3_2 = new JPanel();
        jp3_2.setBackground(Color.BLACK);
        jp3_2.add(jl3_2);
        JPanel jp3_3 = new JPanel();
        jp3_3.setBackground(Color.BLACK);
        jp3_3.add(jl3_3);
        JPanel jp3_4 = new JPanel();
        jp3_4.setBackground(Color.BLACK);
        jp3_4.add(jl3_4);
        JPanel jp3_5 = new JPanel();
        jp3_5.setBackground(Color.BLACK);
        jp3_5.add(jl3_5);
        JPanel jp3_6 = new JPanel();
        jp3_6.setBackground(Color.BLACK);
        jp3_6.add(jl3_6);
        JPanel jp3_7 = new JPanel();
        jp3_7.setBackground(Color.BLACK);
        jp3_7.add(jl3_7);

        content3_1.add(jp3_1);
        content3_1.add(jp3_2);
        content3_1.add(jp3_3);
        content3_1.add(jp3_4);
        content3_1.add(jp3_5);
        content3_1.add(jp3_6);
        content3_1.add(jp3_7);

        add(content3_1);
    }

    public void content4(){
        content4_1.setBackground(Color.BLACK);
        content4_1.setLayout(new GridLayout(1, 7));

        JLabel jl4_1 = new JLabel(contents_display[3][0]);
        jl4_1.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl4_2 = new JLabel(contents_display[3][1]);
        jl4_2.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl4_3 = new JLabel(contents_display[3][2]);
        jl4_3.setFont(new Font(null, Font.BOLD, 40));
        makeTimePormat(contents_display[3][3]);
        JLabel jl4_4 = new JLabel(timePormat);
        jl4_4.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl4_5 = new JLabel(contents_display[3][4]);
        jl4_5.setFont(new Font(null, Font.BOLD, 40));
        distinctionTime(contents_display[3][3]);
        JLabel jl4_6 = new JLabel(timeFlag);
        jl4_6.setForeground(Color.RED);
        jl4_6.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl4_7 = new JLabel(contents_display[3][5]);
        jl4_7.setFont(new Font(null, Font.BOLD, 40));

        ktxMarkYellow(contents_display[3][1]); // ktx 종류의 열차는 노란색으로 표시
        if(markFlag==true){
            jl4_1.setForeground(Color.YELLOW);
            jl4_2.setForeground(Color.YELLOW);
            jl4_3.setForeground(Color.YELLOW);
            jl4_4.setForeground(Color.YELLOW);
            jl4_5.setForeground(Color.YELLOW);
            jl4_7.setForeground(Color.YELLOW);
        }
        else{
            jl4_1.setForeground(Color.GREEN);
            jl4_2.setForeground(Color.GREEN);
            jl4_3.setForeground(Color.GREEN);
            jl4_4.setForeground(Color.GREEN);
            jl4_5.setForeground(Color.GREEN);
            jl4_7.setForeground(Color.GREEN);
        }

        JPanel jp4_1 = new JPanel();
        jp4_1.setBackground(Color.BLACK);
        jp4_1.add(jl4_1);
        JPanel jp4_2 = new JPanel();
        jp4_2.setBackground(Color.BLACK);
        jp4_2.add(jl4_2);
        JPanel jp4_3 = new JPanel();
        jp4_3.setBackground(Color.BLACK);
        jp4_3.add(jl4_3);
        JPanel jp4_4 = new JPanel();
        jp4_4.setBackground(Color.BLACK);
        jp4_4.add(jl4_4);
        JPanel jp4_5 = new JPanel();
        jp4_5.setBackground(Color.BLACK);
        jp4_5.add(jl4_5);
        JPanel jp4_6 = new JPanel();
        jp4_6.setBackground(Color.BLACK);
        jp4_6.add(jl4_6);
        JPanel jp4_7 = new JPanel();
        jp4_7.setBackground(Color.BLACK);
        jp4_7.add(jl4_7);

        content4_1.add(jp4_1);
        content4_1.add(jp4_2);
        content4_1.add(jp4_3);
        content4_1.add(jp4_4);
        content4_1.add(jp4_5);
        content4_1.add(jp4_6);
        content4_1.add(jp4_7);

        add(content4_1);
    }

    public void content5(){
        content5_1.setBackground(Color.BLACK);
        content5_1.setLayout(new GridLayout(1, 7));

        JLabel jl5_1 = new JLabel(contents_display[4][0]);
        jl5_1.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl5_2 = new JLabel(contents_display[4][1]);
        jl5_2.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl5_3 = new JLabel(contents_display[4][2]);
        jl5_3.setFont(new Font(null, Font.BOLD, 40));
        makeTimePormat(contents_display[4][3]);
        JLabel jl5_4 = new JLabel(timePormat);
        jl5_4.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl5_5 = new JLabel(contents_display[4][4]);
        jl5_5.setFont(new Font(null, Font.BOLD, 40));
        distinctionTime(contents_display[4][3]);
        JLabel jl5_6 = new JLabel(timeFlag);
        jl5_6.setForeground(Color.RED);
        jl5_6.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl5_7 = new JLabel(contents_display[4][5]);
        jl5_7.setFont(new Font(null, Font.BOLD, 40));

        ktxMarkYellow(contents_display[4][1]); // ktx 종류의 열차는 노란색으로 표시
        if(markFlag==true){
            jl5_1.setForeground(Color.YELLOW);
            jl5_2.setForeground(Color.YELLOW);
            jl5_3.setForeground(Color.YELLOW);
            jl5_4.setForeground(Color.YELLOW);
            jl5_5.setForeground(Color.YELLOW);
            jl5_7.setForeground(Color.YELLOW);
        }
        else{
            jl5_1.setForeground(Color.GREEN);
            jl5_2.setForeground(Color.GREEN);
            jl5_3.setForeground(Color.GREEN);
            jl5_4.setForeground(Color.GREEN);
            jl5_5.setForeground(Color.GREEN);
            jl5_7.setForeground(Color.GREEN);
        }

        JPanel jp5_1 = new JPanel();
        jp5_1.setBackground(Color.BLACK);
        jp5_1.add(jl5_1);
        JPanel jp5_2 = new JPanel();
        jp5_2.setBackground(Color.BLACK);
        jp5_2.add(jl5_2);
        JPanel jp5_3 = new JPanel();
        jp5_3.setBackground(Color.BLACK);
        jp5_3.add(jl5_3);
        JPanel jp5_4 = new JPanel();
        jp5_4.setBackground(Color.BLACK);
        jp5_4.add(jl5_4);
        JPanel jp5_5 = new JPanel();
        jp5_5.setBackground(Color.BLACK);
        jp5_5.add(jl5_5);
        JPanel jp5_6 = new JPanel();
        jp5_6.setBackground(Color.BLACK);
        jp5_6.add(jl5_6);
        JPanel jp5_7 = new JPanel();
        jp5_7.setBackground(Color.BLACK);
        jp5_7.add(jl5_7);

        content5_1.add(jp5_1);
        content5_1.add(jp5_2);
        content5_1.add(jp5_3);
        content5_1.add(jp5_4);
        content5_1.add(jp5_5);
        content5_1.add(jp5_6);
        content5_1.add(jp5_7);

        add(content5_1);
    }

    public void content6(){
        content6_1.setBackground(Color.BLACK);
        content6_1.setLayout(new GridLayout(1, 7));

        JLabel jl6_1 = new JLabel(contents_display[5][0]);
        jl6_1.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl6_2 = new JLabel(contents_display[5][1]);
        jl6_2.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl6_3 = new JLabel(contents_display[5][2]);
        jl6_3.setFont(new Font(null, Font.BOLD, 40));
        makeTimePormat(contents_display[5][3]);
        JLabel jl6_4 = new JLabel(timePormat);
        jl6_4.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl6_5 = new JLabel(contents_display[5][4]);
        jl6_5.setFont(new Font(null, Font.BOLD, 40));
        distinctionTime(contents_display[5][3]);
        JLabel jl6_6 = new JLabel(timeFlag);
        jl6_6.setForeground(Color.RED);
        jl6_6.setFont(new Font(null, Font.BOLD, 40));
        JLabel jl6_7 = new JLabel(contents_display[5][5]);
        jl6_7.setFont(new Font(null, Font.BOLD, 40));

        ktxMarkYellow(contents_display[5][1]); // ktx 종류의 열차는 노란색으로 표시
        if(markFlag==true){
            jl6_1.setForeground(Color.YELLOW);
            jl6_2.setForeground(Color.YELLOW);
            jl6_3.setForeground(Color.YELLOW);
            jl6_4.setForeground(Color.YELLOW);
            jl6_5.setForeground(Color.YELLOW);
            jl6_7.setForeground(Color.YELLOW);
        }
        else{
            jl6_1.setForeground(Color.GREEN);
            jl6_2.setForeground(Color.GREEN);
            jl6_3.setForeground(Color.GREEN);
            jl6_4.setForeground(Color.GREEN);
            jl6_5.setForeground(Color.GREEN);
            jl6_7.setForeground(Color.GREEN);
        }

        JPanel jp6_1 = new JPanel();
        jp6_1.setBackground(Color.BLACK);
        jp6_1.add(jl6_1);
        JPanel jp6_2 = new JPanel();
        jp6_2.setBackground(Color.BLACK);
        jp6_2.add(jl6_2);
        JPanel jp6_3 = new JPanel();
        jp6_3.setBackground(Color.BLACK);
        jp6_3.add(jl6_3);
        JPanel jp6_4 = new JPanel();
        jp6_4.setBackground(Color.BLACK);
        jp6_4.add(jl6_4);
        JPanel jp6_5 = new JPanel();
        jp6_5.setBackground(Color.BLACK);
        jp6_5.add(jl6_5);
        JPanel jp6_6 = new JPanel();
        jp6_6.setBackground(Color.BLACK);
        jp6_6.add(jl6_6);
        JPanel jp6_7 = new JPanel();
        jp6_7.setBackground(Color.BLACK);
        jp6_7.add(jl6_7);

        content6_1.add(jp6_1);
        content6_1.add(jp6_2);
        content6_1.add(jp6_3);
        content6_1.add(jp6_4);
        content6_1.add(jp6_5);
        content6_1.add(jp6_6);
        content6_1.add(jp6_7);

        add(content6_1);
    }

    // 데이터 최신화를 위한 쓰레드 메소드
    public void run() {
        while (true) {
            if (watchFlag)
                break; // b가 true이면(프로세스가 종료되면)
            whatTime();
            // 3초마다 transFlag가 true가 되어 밑에 구문 수행
            if(transFlag==true){
                db.getData();
                content1_1.removeAll();
                content2_1.removeAll();
                content3_1.removeAll();
                content4_1.removeAll();
                content5_1.removeAll();
                content6_1.removeAll();
                content1();
                content2();
                content3();
                content4();
                content5();
                content6();
                transFlag = false;
            }


            try {
                Thread.sleep(1000); // 1초마다 시작해야해서
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    // 3초마다 데이터 베이스를 읽어보고 업데이트
    class database extends DBStart{
        public void getData(){
            try{
                ResultSet rs1 = stmt.executeQuery("select * from traindata");
                rs1.first(); // 커서를 첫번째 행으로 가리킨다.
                int i=0;
                int a=0;

                do{
                    int j=0;
                    managerWindow.contents[i][j] = rs1.getString("destination");
                    j++;
                    managerWindow.contents[i][j] = rs1.getString("name");
                    j++;
                    managerWindow.contents[i][j] = rs1.getString("number");
                    j++;
                    if(Integer.parseInt(rs1.getString("time"))>=timeTemp){
                        contents_display[a][0] = rs1.getString("destination");
                        contents_display[a][1] = rs1.getString("name");
                        contents_display[a][2] = rs1.getString("number");
                        contents_display[a][3] = rs1.getString("time");
                        contents_display[a][4] = rs1.getString("platform");
                        contents_display[a][5] = rs1.getString("delay");
                        a++;
                    }
                    managerWindow.contents[i][j] = rs1.getString("time");
                    j++;
                    managerWindow.contents[i][j] = rs1.getString("platform");
                    j++;
                    managerWindow.contents[i][j] = rs1.getString("delay");
                    j++;

                    i++;
                }while(rs1.next()); // 데이터 베이스 다음행으로 커서를 옮긴다.

                // 입력된 데이터값 정렬시키기
                sortArray(contents_display);
                sortArray(managerWindow.contents);

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
