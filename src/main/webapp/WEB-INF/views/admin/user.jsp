<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="../include/header.jsp"%>
<%@include file="./sidebar.jsp"%>
<style>
th, td{
	text-align: center;
}
</style>
<c:if test="${ member_id == null }">
	<script>
		<% response.sendRedirect("/login");%> 
	</script>
</c:if>
<div class="content" style="margin-left: 120px; width: 890px;">
	<h2>회원 목록</h2>
	<table class="table">
		<thead>
			<tr>
				<th>ID</th>
				<th>이름</th>
				<th>성별</th>
				<th>메일</th>
				<th>전화번호</th>
				<th>등급</th>
			</tr>
		</thead>
		<tbody id="member_list">
		</tbody>
	</table>
	<div id="member_page" style="text-align: center;"></div>
	<div class="form-inline">
		<select class="form-control" id="keyword" name="keyword">
			<option value="member_id" selected="selected">ID</option>
			<option value="member_name">이름</option>
		</select> <input type="text" class="form-control" id="search" name="search">
		<input type="submit" class="btn btn-default" value="검색" onclick="getMemberList(1)">
	</div>
</div>
<script type="text/javascript">
	var currentPage = 1;
	var startPage = 1;
	var endPage = 1;
	var totalPage;
	
	function setMemberList(data){
		var result;
		$("#member_list").html("");
		$(data).each(function(){
			result += "<tr><td>"
			+ this.member_id
			+ "</td>"
			+ "<td>"
			+ this.member_name
			+ "</td>"
			+ "<td>"
			+ this.gender
			+ "</td>"
			+ "<td>"
			+ this.email
			+ "</td>"
			+ "<td>"
			+ this.tel
			+ "</td>"
			+ "<td>"
			+ this.grade
			+ "</td>"
			+ "<td>"
			+ "<input type='button' id='btn_del' name='btn_del' class='btn btn-danger' onclick=deleteMember('" + this.member_id + "') value='삭제' />"
			+ "</td></tr>";
		});		
		$("#member_list").html(result);
	}
	
	function getMemberList(page){
		if(page == null){
			page = currentPage;
		}
		$.ajax({
			type : 'get',
			url : '/admin/user/' + page,
			headers : {
				"Content-Type" : "application/json",
			},
			dataType : 'json',
			data : {"keyword" : $("#keyword").val(), "search" : $("#search").val()},
			success : function(result){
				
				endPage = result.paging.endPage; 
	            startPage = result.paging.startPage;
	            if(totalPage<result.paging.totalPage){
	            	getMemberList(result.paging.totalPage);
	            }
	            totalPage = result.paging.totalPage;
	            
				setMemberList(result.member_list);
				setPagePrint(result.paging)
			}
		});
		currentPage = page;
	}
	
	
	getMemberList(currentPage);
	
	function setPagePrint(pm){
		var str = "<ul class='pagination'>";
		if(currentPage > pm.endPage && currentPage > 1){
			getReplyList(currentPage - 1);
		}
		
		if(pm.prev){
			str += "<li> <a onclick='getMemberList("+(pm.startPage-1)+")'>&lt;</a> </li>"
		}
		
		for(var i = pm.startPage; i <= pm.endPage ; i++){
			if(i == pm.criteria.page){
				str += "<li class='active'><a href='#'>" + i + "</a></li>"
			}else{
				str += "<li><a onclick='getMemberList("+i+")' style='cursor:pointer'>" + i + "</a></li>"
			}
		}
		
		if(pm.next){
			str += "<li> <a onclick='getMemberList("+(pm.endPage+1)+")'>&gt;</a> </li>"
		}
		
		str += "</ul>"
		document.getElementById("member_page").innerHTML = str;
	}
	function deleteMember(member_id){
		$.ajax({
			type : 'delete',
			url : '/admin/user/' + member_id,
			headers : { 
				"Content-Type" : "application/json"
				},
			data : '',
			dataType : 'text',
			success : function(result){
				if(result == "SUCCESS"){
					getMemberList(currentPage);
				}
			}
		});
	}
</script>
<%@include file="../include/footer.jsp"%>