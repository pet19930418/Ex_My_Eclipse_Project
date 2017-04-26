package test_MyBoardServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test_removeFreeNoticeBoardInfo")
public class FreeBoard_Remove_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		System.out.println("자유 게시판 <글 삭제> 서블릿 동작");
		RequestDispatcher rd = null;
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
			System.out.println(">>>>>>>>>>>>>> Remove서블릿 연결");
			
			/*
			 * 안드로이드로부터 삭제해야할 묶여진 데이터를 받습니다.
			 */
			ObjectInputStream ois = new ObjectInputStream(request.getInputStream());
			HashMap<String, String> stringDataMap = (HashMap<String, String>) ois.readObject();
			ois.close();
			int no = Integer.parseInt(stringDataMap.get("no")); // 조회할 번호를 저장
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE b_no=" + no);

			if (rs.next() == false) // 조회결과 없으면 서블릿 종료
				return;
			else // 있으면 해당 데이터를 데이터베이스에서 지운다.
			{
				stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM " + tableName + " WHERE b_no=" + no);
				System.out.println(">>>>>>>>>>>>>> 데이터 삭제 완료!");
			}
			// 여기부터는 삭제한 데이터의 유니크 값 이후 행들의 유니크넘버를 변경해준다.(앞으로 땡겨주는 역할)
			stmt = conn.createStatement();
			stmt.executeUpdate("UPDATE " + tableName + " SET b_no=b_no-1" + " WHERE b_no > " + no);
			System.out.println(">>>>>>>>>>>>>> 데이터 유니크넘버 변경 완료!"+ no);
			
			// 그리고 삭제가 되고 바로 초기화 서블릿을 불러와서 초기화 해준다.
			rd = getServletContext().getRequestDispatcher("/test_addFreeNoticeBoardInitInfo");
			rd.forward(request, response);
			System.out.println(">>>>>>>>>>>>>> 초기화 서블릿 작동 -- 초기화 완료!");
			
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
