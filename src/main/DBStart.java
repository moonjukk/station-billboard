package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBStart {

    Statement stmt = null;
    Connection conn;

    public DBStart() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); //드라이버 로드
            conn = DriverManager.getConnection(
                    "jdbc:mysql:///screen", "moon", "qkrwjdrnjs");
            System.out.println("DB 연결 완료"); // DB 연결완료 창
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 에러");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }

    }

}
