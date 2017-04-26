package test_MyBoardServer;

//복붙
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 	안드로이드 어플리케이션에서 추가해야할 게시판 글의 내용을 받아서
  	데이터베이스에 추가하는 서블릿(게시판 글 추가 역할)
 	초기화 코드의 주석과 중복되는 내용들은 생략
*/
@WebServlet("/test_addFreeNoticeBoardInfo")
public class FreeBoard_Write_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		/*
		RequestDispatcher rd =request.getRequestDispatcher("/test/test.jsp");
		rd.forward(request, response);*/
		
		System.out.println("자유게시판 <글 추가> 서블릿 동작");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String tableName = "test_freeboard_table";
		try {
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost/test_freeboard_db", // JDBC URL															
					"root", // DBMS 사용자 아이디
					"1234"); // DBMS 사용자 암호
			System.out.println(">>>>>>>>>>>>>> Write서블릿 연결");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SHOW TABLES LIKE " + "'" + tableName + "'");
			if (rs.next() == false) { // 테이블이 있는지 조회, 없다면 새로 생성합니다.
				stmt.executeUpdate("CREATE TABLE " + tableName + "(b_no INT NOT NULL, "
									+ " b_user VARCHAR(20) NOT NULL,"
									+ " b_content VARCHAR(50) NOT NULL, "
									+ " b_date VARCHAR(20) NOT NULL, "
									+ " PRIMARY KEY b_no(b_no));");
				System.out.println(tableName + ">> 테이블이 생성되었습니다. ");
			}
			// 안드로이드 프로그램으로 부터 데이터를 받고
			ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
			HashMap<String, String> stringDataMap = (HashMap<String, String>) ois.readObject();
			// 정보를 데이터베이스에 추가해준다.
			String sql = "INSERT INTO " + tableName
					+ "(b_no, b_user, b_content, b_date) VALUES(?, ?, ?, ?)";
			PreparedStatement psmt = conn.prepareStatement(sql);
			psmt.setInt(1, Integer.parseInt(stringDataMap.get("no")));
			psmt.setString(2, stringDataMap.get("user"));
			psmt.setString(3, stringDataMap.get("content"));
			psmt.setString(4, stringDataMap.get("date"));
			psmt.executeUpdate();
			System.out.println(">>>>>>>>>>>>>> DB에 데이터 INSERT 완료!");
		} catch (Exception e) {
			throw new ServletException(e);
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception e) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
			}
		}
	}
}
