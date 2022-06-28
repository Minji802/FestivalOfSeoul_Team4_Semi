package com.yj.main;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/BoardCateViewC_jp")
public class BoardCateViewC_jp extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//보드 불러오기
//		YJBoardDAO.getYjdao().getBoardCate(request);

		YJBoardDAO.getYjdao().lastPage(request);
		YJBoardDAO.getYjdao().showPage(request);
		
		request.setAttribute("contentPage", "YJ_Board/viewBoard_jp.jsp");
		request.getRequestDispatcher("index_jp.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
