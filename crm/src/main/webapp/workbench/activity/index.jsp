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
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
	<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>
	<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>


	<script type="text/javascript">

	$(function(){


		$(".time").datetimepicker({
			minView: "month",
			language:  'zh-CN',
			format: 'yyyy-mm-dd',
			autoclose: true,
			todayBtn: true,
			pickerPosition: "bottom-left"
		});

		//为创建按钮绑定事件，打开添加市场活动的模态窗口
		$("#addBtn").click(function () {

			/*

				模态窗口操作：
					找到模态窗口的jquery对象，调用modal方法，为该方法传递参数
															参数有两种取值：
																show：打开模态窗口
																hide：关闭模态窗口

			 */

			//alert("请求发送到后台，取得用户列表数据，为所有者下拉框铺值");

			//$("#createActivityModal").modal("show");

			$.ajax({

				url : "workbench/activity/getUserList.do",
				type : "get",
				dataType : "json",
				success : function (data) {

					/*

						data
							List<User> uList
							[{用户1},{2},{3}...]

							每一个{用户1}
							就是每一个{"id":"","name":""....}


					 */

					/*

						每一个n，就是每一个json对象
						对于当前案例，每一个json对象，就是每一个user对象

					 */

					var html = "<option></option>";

					$.each(data,function (i,n) {

						html += "<option value='"+n.id+"'>"+n.name+"</option>";

					})

					//找到所有者下拉框（select标签对），将以上拼接好的option存储到select标签对中
					$("#create-owner").html(html);

					//取得当前登录用户的id
					/*

						el表达式是可以使用在js代码中的
						但是el表达式必须要套用在字符串引号中

					 */
					var id = "${user.id}";
					$("#create-owner").val(id);

					//以上所有者下拉框中的数据铺完之后，打开模态窗口
					$("#createActivityModal").modal("show");

				}

			})


		})


		//为保存按钮绑定事件，执行市场活动的添加操作
		$("#saveBtn").click(function () {

			$.ajax({

				url : "workbench/activity/save.do",
				data : {

					"owner" : $.trim($("#create-owner").val()),
					"name" : $.trim($("#create-name").val()),
					"startDate" : $.trim($("#create-startDate").val()),
					"endDate" : $.trim($("#create-endDate").val()),
					"cost" : $.trim($("#create-cost").val()),
					"description" : $.trim($("#create-description").val())

				},
				type : "post",
				dataType : "json",
				success : function (data) {

					/*

						data
							{"success":true/false}

					 */

					if(data.success){

						//添加成功后
						//刷新市场活动列表
						pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));
						/*

							清空表单中的信息

							jquery为我们提供了submit()方法，用来提交表单
							但是jquery并没有为我们提供reset()方法，用来重置表单（idea给了我们错误的提示，根本就没有reset方法）
							原生js对象dom，为我们提供了reset()方法

							jquery对象转换为dom对象：
								jquery对象[0]:取得jquery对象中的第一个dom对象

							dom对象转换为jquery对象：
								$(dom)


						 */

						//document.getElementById("activitySaveForm").reset();

						//$("#activitySaveForm")[0].reset();


						//关闭模态窗口
						$("#createActivityModal").modal("hide");

					}else{

						alert("添加操作失败");

					}

				}

			})

		})

		//页面加载完毕后，需要（局部）刷新市场活动信息列表
		//默认第一页，每页展现2条记录
		pageList(1,2);

		//为查询按钮绑定事件，执行市场活动的条件查询操作
		$("#searchBtn").click(function () {

			//将搜索框中的信息保存到隐藏域中
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			pageList(1,2);

		})

		//为全选复选框绑定事件，执行全选操作
		$("#qx").click(function () {

			$("input[name=xz]").prop("checked",this.checked);

		})

		/*$("input[name=xz]").click(function () {

			alert(123);

		})*/

		/*

			以上绑定事件失败，alert123没有触发

			因为所有name=xz的input元素，都是我们自己在js中手动拼接生成的
			对于我们自己手动拼接生成的元素，也被称之为动态生成的元素
			动态生成的元素，不能以传统的方式来绑定事件

			这种元素应该使用on方法来绑定事件
				语法：
				$(需要绑定的元素的有效的父级元素).on(绑定事件的方式,需要绑定的元素,回调函数)

		 */

		$("#activityBody").on("click",$("input[name=xz]"),function () {

			$("#qx").prop("checked",$("input[name=xz]").length==$("input[name=xz]:checked").length)

		})

		//为删除按钮绑定事件，执行市场活动的删除操作（可批量删除）
		$("#deleteBtn").click(function () {

			//id=xxx&id=xxx&id=xxx

			var $xz = $("input[name=xz]:checked");

			if($xz.length==0){

				alert("请选择需要删除的记录");

			}else{

				if(confirm("确定删除所选记录吗？")){

					var param = "";

					for(var i=0;i<$xz.length;i++){

						param += "id="+$($xz[i]).val();

						//如果不是最后一条元素
						if(i<$xz.length-1){

							param += "&";

						}

					}

					//alert(param);

					//以上参数处理完毕后，发出ajax请求，执行市场活动的删除操作
					$.ajax({

						url : "workbench/activity/delete.do",
						data : param,
						type : "post",
						dataType : "json",
						success : function (data) {

							/*

                                data
                                    {"success":true/false}

                             */

							if(data.success){

								//删除成功

								//刷新列表
								//pageList(1,2);
								pageList(1,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));

							}else{

								alert("删除市场活动失败");

							}

						}

					})

				}



			}



		})


		//为修改按钮绑定事件，打开修改操作的模态窗口
		$("#editBtn").click(function () {

			var $xz = $("input[name=xz]:checked");

			if($xz.length==0){

				alert("请选择需要修改的记录");

			}else if($xz.length>1){

				alert("只能选择一条记录执行修改");

			//我们肯定是选了，而且只选了一条
			}else{

				//将选中的记录的id取得
				//虽然是复选框，但是如果能够保证只选中了一条记录，则可以直接使用val方法取得复选框的value值（id值）
				var id = $xz.val();

				/*

					发出ajax请求，为后台传递id值，后台根据id值查询单条记录，将单条记录铺在修改操作的模态窗口中
					打开修改操作的模态窗口

				 */

				$.ajax({

					url : "workbench/activity/getActivityByIdAndUserList.do",
					data : {

						"id" : id

					},
					type : "get",
					dataType : "json",
					success : function (data) {

						/*

							data
								Activity a
								List<User> uList
								{"a":{市场活动},"uList":[{用户1},{2},{3}]}

						 */

						//处理所有者下拉框

						var html = "<option></option>";

						$.each(data.uList,function (i,n) {

							html += "<option value='"+n.id+"'>"+n.name+"</option>";

						})

						$("#edit-owner").html(html);

						//处理修改操作模态窗口中的市场活动的基本信息
						//通过data.a这条市场活动为修改表单中的元素赋值
						//注意赋值赋的是7个值，在原有的6个值的基础之上，还要有一个id值、

						$("#edit-id").val(data.a.id);
						$("#edit-name").val(data.a.name);
						$("#edit-owner").val(data.a.owner);
						$("#edit-startDate").val(data.a.startDate);
						$("#edit-endDate").val(data.a.endDate);
						$("#edit-cost").val(data.a.cost);
						$("#edit-description").val(data.a.description);

						//修改操作模态窗口中的信息都处理完毕后，将模态窗口打开
						$("#editActivityModal").modal("show");

					}

				})

			}

		})


		//为更新按钮绑定事件，执行市场活动的修改操作
		$("#updateBtn").click(function () {

			$.ajax({

				url : "workbench/activity/update.do",
				data : {

					"id" : $.trim($("#edit-id").val()),
					"owner" : $.trim($("#edit-owner").val()),
					"name" : $.trim($("#edit-name").val()),
					"startDate" : $.trim($("#edit-startDate").val()),
					"endDate" : $.trim($("#edit-endDate").val()),
					"cost" : $.trim($("#edit-cost").val()),
					"description" : $.trim($("#edit-description").val())

				},
				type : "post",
				dataType : "json",
				success : function (data) {

					/*

						data
							{"success":true/false}

					 */

					if(data.success){

						//修改成功后
						//刷新市场活动列表
						//pageList(1,2);

						/*

							$("#activityPage").bs_pagination('getOption', 'currentPage')
							执行完操作后，停留在当前页

							$("#activityPage").bs_pagination('getOption', 'rowsPerPage')
							执行完操作后，保持每页设置好的记录数

							执行完修改操作后，停留在修改记录的当前页，维持每页展现的记录数

						 */
						pageList($("#activityPage").bs_pagination('getOption', 'currentPage')
								,$("#activityPage").bs_pagination('getOption', 'rowsPerPage'));



						//关闭模态窗口
						$("#editActivityModal").modal("hide");

					}else{

						alert("修改操作失败");

					}

				}

			})

		})

	});

	/*

		pageList方法：
			发出ajax请求，局部刷新市场活动信息列表

		关于该方法，在哪些情况下需要调用？（执行该方法的入口都有哪些？）
		1.点击左侧菜单"市场活动"，在市场活动列表页展现完毕后，调用该方法，局部刷新市场活动信息列表
		2.点击查询按钮的时候，调用该方法，局部刷新市场活动信息列表
		3.点击分页组件的时候，调用该方法，局部刷新市场活动信息列表
		4.添加操作后，调用该方法，局部刷新市场活动信息列表
		5.修改操作后，调用该方法，局部刷新市场活动信息列表
		6.删除操作后，调用该方法，局部刷新市场活动信息列表

		==============================================================

		关于pageList方法中的两个参数
		pageNo和pageSize
		pageNo：页码
		pageSize：每页展现的记录数
		这两个参数是用来处理前端分页操作使用的
		所有的关系型数据库，对于前端分页的操作，都离不来这两个参数
		这两个参数就是前端分页操作的基础参数
		前端所展现的其他的分页相关的信息，都可以使用这两个参数通过计算得出答案

		==============================================================

		为后台传递哪些参数？
		如果不为后台传递参数
		我们一定是这么查的市场活动列表
		select * from tbl_activity

		需要为后台传递分页相关的参数：
		pageNo
		pageSize

		还需要为后台传递条件查询相关的参数
		name
		owner
		startDate
		endDate

	 */
	function pageList(pageNo,pageSize) {

		//alert("查询并展现市场活动信息列表123");

		//将全选复选框的√灭掉
		$("#qx").prop("checked",false);

		//将隐藏域中的信息取出，重新赋值给搜索框
		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({

			url : "workbench/activity/pageList.do",
			data : {

				"pageNo" : pageNo,
				"pageSize" : pageSize,
				"name" : $.trim($("#search-name").val()),
				"owner" : $.trim($("#search-owner").val()),
				"startDate" : $.trim($("#search-startDate").val()),
				"endDate" : $.trim($("#search-endDate").val())

			},
			type : "get",
			dataType : "json",
			success : function (data) {

				/*

					data
						List<Activity> dataList
						int total ...

						{"total":?,"dataList":[{市场活动1},{2},{3}]}

				 */
				
				var html = "";

				/*var str1 = "'\"\"'";

				var str2 = '"\'\'"';*/


				//每一个n，就是每一个市场活动对象
				$.each(data.dataList,function (i,n) {

					html += '<tr class="active">';
					html += '<td><input type="checkbox" name="xz" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';

				})

				//将以上所有拼接好的tr和td往tBody里面存放
				$("#activityBody").html(html);

				//==============================================================================

				//以上展现完市场活动列表后，由bootstrap的分页插件bs_pagination来实现分页功能

				//计算总页数
				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				$("#activityPage").bs_pagination({
					currentPage: pageNo, // 页码
					rowsPerPage: pageSize, // 每页显示的记录条数
					maxRowsPerPage: 20, // 每页最多显示的记录条数
					totalPages: totalPages, // 总页数
					totalRows: data.total, // 总记录条数

					visiblePageLinks: 3, // 显示几个卡片

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//该函数，在我们点击分页组件的时候，进行触发
					onChangePage : function(event, data){
						pageList(data.currentPage , data.rowsPerPage);
					}
				});




			}

		})


	}

</script>
</head>
<body>

	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>


	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">

					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-id"/>

						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">



								</select>
							</div>
							<label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-name">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>

						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>

					</form>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">

					<form id="activitySaveForm" class="form-horizontal" role="form">


						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">

								</select>
							</div>
							<label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-name">
							</div>
						</div>

						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate">
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-endDate">
							</div>
						</div>
						<div class="form-group">

							<label for="create-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="create-cost">
							</div>
						</div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>

					</form>

				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	

	
	
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="submit" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<!--

						data-toggle="modal"
							触发该按钮，将要打开一个模态窗口（模态框）

						data-target="#createActivityModal"
							指定触发模态窗口的目标，以#id的形式找到需要触发的模态窗口

						点击创建按钮后，在打开模态窗口前，弹出一个alert(123) ???
						可以弹出alert123，但是很难把控弹出的时机
						因为对于模态窗口的操作，我们现在是以属性和属性值的方式写死到了button标签当中

						将来我们的实际项目开发，控制行为的代码，不能像这样以属性属性值的方式写死在标签中
						对于页面行为的把控应该使用js代码来做

					-->
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="qx"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='workbench/activity/detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">

				<div id="activityPage"></div>

			</div>
			
		</div>
		
	</div>
</body>
</html>