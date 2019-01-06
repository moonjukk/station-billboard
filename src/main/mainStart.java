package main;

/**
 * 만든이 : 문주환
 * 제작 기간 : 19.01.2 ~ 01.6
 * 목적 : 데이터베이스(MySQL)의 사용법 및 자바 Swing 사용
 */
public class mainStart {
    public static void main(String[] args) {
        new DBStart(); // 데이터 베이스 연동
        new manager_login(); // 관리자에 접근하기 위한 로그인
        new mainWindow(); // 전광판
    }
}
