package com.yj.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.t4.main.DBManager_Main;

public class YJBoardDAO {
	
	private Connection con;
	private static final YJBoardDAO YJDAO = new YJBoardDAO(DBManager_Main.getDbm().connect());
	
	private YJBoardDAO() {
		//		private Connection con = DBManager_Main.getDbm().connect();
	}
	private YJBoardDAO(Connection con) {
		super();
		this.con = con;
	}

	public static YJBoardDAO getYjdao() {
		return YJDAO;
	}

	// 전체 게시판 글 불러오기
	public void getAllBoard(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String sql = "select*from BOARD_DB order by B_DATE desc";
			

			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			// 가변배열에 넣기
			ArrayList<Board> boards = new ArrayList<Board>();

			Board b = null;

			while (rs.next()) {
				b = new Board(rs.getInt("b_no"), rs.getString("b_cate"), rs.getString("b_title"),
						rs.getString("b_name"), rs.getString("b_txt"), rs.getString("b_img"), rs.getDate("b_date"),
						rs.getString("b_pw"));
				boards.add(b);
			}
			request.setAttribute("boards", boards);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}

	}

	// 게시판 글 등록하기
	public void regBoard(HttpServletRequest request) {
		
		PreparedStatement pstmt = null;

		try {
			String sql = "INSERT INTO BOARD_DB VALUES(BOARD_SEQ.nextval,?,?,?,?,?,sysdate,?)";

			pstmt = con.prepareStatement(sql);

			String saveDirectory = request.getServletContext().getRealPath("fileFolder");

			System.out.println(saveDirectory);

			MultipartRequest mr = new MultipartRequest(request, saveDirectory, 31457280, "UTF-8",
					new DefaultFileRenamePolicy());

			String title = mr.getParameter("title");
			String cate = mr.getParameter("boardType");
			String name = mr.getParameter("name");
			String img = mr.getFilesystemName("fName");
			String txt = mr.getParameter("txt");
				txt=txt.replace("\r\n","<br>");
			String pw = mr.getParameter("password");

			// 값 받고, ?에 셋팅.
			pstmt.setString(1, cate);
			pstmt.setString(2, title);
			pstmt.setString(3, name);
			pstmt.setString(4, txt);
			pstmt.setString(5, img);
			pstmt.setString(6, pw);

			if (pstmt.executeUpdate() == 1) {
				System.out.println("등록성공");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("등록실패");
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, null);
		}
	}

	// 게시글 하나 불러오기
	public void getOneBoard(HttpServletRequest request) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			String sql = "select*from BOARD_DB where b_no = ?";
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, Integer.parseInt(request.getParameter("number")));

			rs = pstmt.executeQuery();

			Board b = null;
			if (rs.next()) {
				b = new Board(
						rs.getInt("b_no"), 
						rs.getString("b_cate"), 
						rs.getString("b_title"),
						rs.getString("b_name"), 
						rs.getString("b_txt"), 
						rs.getString("b_img"), 
						rs.getDate("b_date"),
						rs.getString("b_pw")
						);
				
				String txt = b.getTxt();
					txt=txt.replace("\r\n","<br>");
				b.setTxt(txt);
				
				request.setAttribute("board", b);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}
	}

	
	public void getOneBoardForUpdate(HttpServletRequest request) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			String sql = "select*from BOARD_DB where b_no = ?";
			pstmt = con.prepareStatement(sql);

			pstmt.setInt(1, Integer.parseInt(request.getParameter("number")));

			rs = pstmt.executeQuery();

			Board b = null;
			if (rs.next()) {
				b = new Board(
						rs.getInt("b_no"), 
						rs.getString("b_cate"), 
						rs.getString("b_title"),
						rs.getString("b_name"), 
						rs.getString("b_txt"), 
						rs.getString("b_img"), 
						rs.getDate("b_date"),
						rs.getString("b_pw")
						);
//			줄바꿈처리
				String txt = b.getTxt();
					txt=txt.replace("<br>","\r\n");
				b.setTxt(txt);

				request.setAttribute("board", b);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}
	}
	
	
	
	// 게시글 삭제
	public  void deleteBoard(HttpServletRequest request) {
		PreparedStatement pstmt = null;

		try {
			String sql = "DELETE BOARD_DB WHERE B_NO = ?";

			pstmt = con.prepareStatement(sql);

			String no = request.getParameter("number");

			// 값 받고, ?에 셋팅.
			pstmt.setString(1, no);

			if (pstmt.executeUpdate() == 1) {
				System.out.println("삭제성공");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("삭제실패");
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, null);
		}

	}

	// 글 업데이트하기.
	public  void updateBoard(HttpServletRequest request) {
		PreparedStatement pstmt = null;
		try {
			
			String saveDirectory = request.getServletContext().getRealPath("fileFolder");
			System.out.println(saveDirectory);

			MultipartRequest mr = new MultipartRequest(request, saveDirectory, 31457280, "UTF-8",
					new DefaultFileRenamePolicy());

				if (mr.getFilesystemName("fName")!=null) {
					String sql = "UPDATE BOARD_DB SET b_title= ?, b_cate=?,b_name=?,b_txt=?,b_img=?,b_pw=? WHERE b_NO=?";
					pstmt = con.prepareStatement(sql);

					String title = mr.getParameter("title");
					String cate = mr.getParameter("boardType");
					String name = mr.getParameter("name");
					String txt = mr.getParameter("txt");
						txt=txt.replace("<br>","\r\n");
					String img = mr.getFilesystemName("fName");
					String pw = mr.getParameter("password");
					String number = mr.getParameter("number");

					// 값 받고, ?에 셋팅.
					pstmt.setString(1, title);
					pstmt.setString(2, cate);
					pstmt.setString(3, name);
					pstmt.setString(4, txt);
					pstmt.setString(5, img);
					pstmt.setString(6, pw);
					pstmt.setString(7, number);
					
				} else {

					String sql = "UPDATE BOARD_DB SET b_title= ?, b_cate=?,b_name=?,b_txt=?,b_pw=? WHERE b_NO=?";
					pstmt = con.prepareStatement(sql);

					String title = mr.getParameter("title");
					String cate = mr.getParameter("boardType");
					String name = mr.getParameter("name");
					String txt = mr.getParameter("txt");
						txt=txt.replace("<br>","\r\n");
					String pw = mr.getParameter("password");
					String number = mr.getParameter("number");

					// 값 받고, ?에 셋팅.
					pstmt.setString(1, title);
					pstmt.setString(2, cate);
					pstmt.setString(3, name);
					pstmt.setString(4, txt);
					pstmt.setString(5, pw);
					pstmt.setString(6, number);

				}
				

				if (pstmt.executeUpdate() == 1) {
					System.out.println("등록성공");
				}

				String number = mr.getParameter("number");
				request.setAttribute("number", number);
				
				String cate = mr.getParameter("boardType");
					request.setAttribute("cate", cate);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("등록실패");
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, null);
		}

	}

	// 카테고리 선택으로 글 불러오기
	public  void getBoardCate(HttpServletRequest request) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {

			String userCate = request.getParameter("cate");
			String sql = "";
			String cateSelect = "";


			if (userCate.equals("all")) {
				sql = "select*from BOARD_DB order by B_DATE desc";
				pstmt = con.prepareStatement(sql);

			} else if (userCate.equals("free") || userCate.equals("review")) {
				sql = "select*from BOARD_DB WHERE b_cate like ? order by B_DATE desc";
				if (userCate.equals("free")) {
					cateSelect = "%자유게시판%";
				} else if (userCate.equals("review")) {
					cateSelect = "%후기게시판%";
				}
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, cateSelect);
			}

			rs = pstmt.executeQuery();

			// 가변배열에 넣기
			ArrayList<Board> boards = new ArrayList<Board>();

			Board b = null;

			while (rs.next()) {
				b = new Board(rs.getInt("b_no"), rs.getString("b_cate"), rs.getString("b_title"),
						rs.getString("b_name"), rs.getString("b_txt"), rs.getString("b_img"), rs.getDate("b_date"),
						rs.getString("b_pw"));
				boards.add(b);
			}
			request.setAttribute("boards", boards);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}

	}

	// 페이징관련
	// 마지막페이지 계산
	public void lastPage(HttpServletRequest request) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String cate = request.getParameter("cate");
		String sql = "";
		
		//글이 몇 개인지 찾기
		try {
			
			// if문 처리
			if (cate.equals("all")) {
				sql = "select count(*) from BOARD_DB";
				pstmt = con.prepareStatement(sql);
			} else if (cate.equals("free")||cate.equals("review")) {
				sql = "select count(*) from BOARD_DB WHERE B_CATE LIKE ?";
				if (cate.equals("free")) {
					cate = "%자유게시판%";
				} else if (cate.equals("review")) {
					cate = "%후기게시판%";
				}
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, cate);
			}
		//sql문 실행하기
		rs = pstmt.executeQuery();
		
			if (rs.next()) {
				int total = rs.getInt("count(*)");
				int lastPage = (int) Math.ceil(total/5);
				request.setAttribute("lastpage", lastPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}

	}

	// 페이지 로딩
	public void showPage(HttpServletRequest request) {
		
				PreparedStatement pstmt = null;
				ResultSet rs = null;
			
			try {
				String cate = request.getParameter("cate");
				
				String vpage = request.getParameter("vPage");				
						if (vpage == null) {
							vpage = "1";
						}
				
				int page = Integer.parseInt(vpage);
				
				int bStart = 1;
				int bEnd = 5;
				
				if (page!= 1) {
					bStart = page*5-4;
					bEnd = bStart+4;
				}
				
				
				// 불러오기 시작
				
				
				String sql = "";
				
				if (cate.equals("all")) {
					sql = "select*from (select rownum as rn,b_no, b_cate, b_title, b_name, b_txt, b_img, b_date, b_pw from (select * from BOARD_DB order by b_date desc))where rn between ? and ? ";
					
					pstmt = con.prepareStatement(sql);

					pstmt.setInt(1, bStart);
					pstmt.setInt(2, bEnd);
					
				} else if (cate.equals("free")||cate.equals("review")) {
					sql = "select*from (select rownum as rn,b_no, b_cate, b_title, b_name, b_txt, b_img, b_date, b_pw from (select * from BOARD_DB where B_CATE = ? order by b_date desc)) where rn between ? and ?";
					if (cate.equals("free")) {
								cate = "자유게시판";
							} else if (cate.equals("review")) {
								cate = "후기게시판";
							}
					
					pstmt = con.prepareStatement(sql);
					
					pstmt.setString(1, cate);
					pstmt.setInt(2, bStart);
					pstmt.setInt(3, bEnd);	
				}
				
				rs = pstmt.executeQuery();
				



					ArrayList<Board> boards = new ArrayList<Board>();
					Board b = null;

					while (rs.next()) {
						b = new Board(rs.getInt("b_no"), rs.getString("b_cate"), rs.getString("b_title"),
								rs.getString("b_name"), rs.getString("b_txt"), rs.getString("b_img"), rs.getDate("b_date"),
								rs.getString("b_pw"));

					boards.add(b);
					}

					request.setAttribute("boards", boards);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}

	}
	
	// mj - 메인페이지에 게시판 5개씩 보여주기
	public void showMainPage(HttpServletRequest request) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		try {
			String sql = "select B_NO, B_CATE, B_TITLE from (select * from BOARD_DB order by B_DATE desc) "
					+ "where rownum <= 5";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			ArrayList<Board> boards = new ArrayList<Board>();
			
			while (rs.next()) {
				Board b = new Board();
				b.setNo(rs.getInt("b_no"));
				b.setCate(rs.getString("b_cate"));
				b.setTitle(rs.getString("b_title"));
				
				boards.add(b);
			}
			
			request.setAttribute("boards", boards);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}
	}
	
public void showMainFreeBoardPage(HttpServletRequest request) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
	
		try {
			String sql = "select B_NO, B_CATE, B_TITLE from (select * from BOARD_DB where B_CATE like '자유게시판' "
					+ "order by B_DATE desc) where rownum <= 5";
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			ArrayList<Board> boards = new ArrayList<Board>();
			
			while (rs.next()) {
				Board b = new Board();
				b.setNo(rs.getInt("b_no"));
				b.setCate(rs.getString("b_cate"));
				b.setTitle(rs.getString("b_title"));
				
				boards.add(b);
			}
			
			request.setAttribute("freeBoards", boards);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBManager_Main.getDbm().close(null, pstmt, rs);
		}
	}


public void showMainReviewBoardPage(HttpServletRequest request) {
	
	PreparedStatement pstmt = null;
	ResultSet rs = null;

	try {
		String sql = "select B_NO, B_CATE, B_TITLE from (select * from BOARD_DB where B_CATE like '후기게시판'"
				+ " order by B_DATE desc) where rownum <= 5";
		pstmt = con.prepareStatement(sql);
		rs = pstmt.executeQuery();
		
		ArrayList<Board> boards = new ArrayList<Board>();
		
		while (rs.next()) {
			Board b = new Board();
			b.setNo(rs.getInt("b_no"));
			b.setCate(rs.getString("b_cate"));
			b.setTitle(rs.getString("b_title"));
			
			boards.add(b);
		}
		
		request.setAttribute("reviewBoards", boards);
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		DBManager_Main.getDbm().close(null, pstmt, rs);
	}
}

}
