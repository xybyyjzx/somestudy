<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script>

		$(function () {

			if(window.top!=window){
				window.top.location=window.location;
			}

			//在页面加载完毕后，让用户名的文本框自动获取焦点
			$("#loginAct").focus();

			//在页面加载完毕后，将用户名文本框中的信息清空掉
			$("#loginAct").val("");

			//为登录按钮绑定事件，执行验证登录的操作
			$("#submitBtn").click(function () {

				login();

			})

			//为窗口window对象绑定事件，执行验证登录的操作
			//event:可以取得敲键盘的键位码值
			$(window).keydown(function (event) {

				//如果码值为13，说明敲的是回车键
				if(event.keyCode==13){

					login();

				}

			})


		})

		function login() {

			//alert("123");

			//去除内容左右空格：$.trim(内容)
			var loginAct = $.trim($("#loginAct").val());
			var loginPwd = $.trim($("#loginPwd").val());

			if(loginAct=="" || loginPwd==""){

				$("#msg").html("账号密码不能为空");

				//如果账号密码为空，就不用继续向下验证了，应该及时终止掉该方法
				return false;

			}

			//发出ajax请求，验证账号密码是否正确
			$.ajax({

				url : "settings/user/login.do",
				data : {

					"loginAct" : loginAct,
					"loginPwd" : loginPwd

				},
				type : "post",
				dataType : "json",
				success : function (data) {

					/*

						data
							{"success":true/false,"msg":?}

					 */

					if(data.success){

						//登录成功
						//跳转到工作台的初始页（登录后的欢迎页）
						window.location.href = "workbench/index.jsp";

					}else{

						//登录失败，应该在msg的span区域局部刷新错误信息
						$("#msg").html(data.msg);

					}


				}

			})

		}

	</script>
</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2017&nbsp;动力节点</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
							<span id="msg" style="color: red"></span>
						
					</div>
					<button type="button" id="submitBtn" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>