layui.use(['form','layer'],function(){
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery;
	
	$(".uname").val(sessionStorage.getItem('username'))
	$(".upwd").val(sessionStorage.getItem('password'))
	$("#uid").val(sessionStorage.getItem('backuserid'))
    $("#account").val(sessionStorage.getItem('account'))
	
    //添加验证规则
    form.verify({
    	username : function(val, item){
            if(val = ""){
                return "账号不能为空";
            }
       	},
       	upwd : function(val, item){
            if(val = ""){
                return "账号不能为空";
            }
       	},
        confirmPwd : function(val, item){
            if(val != $("#newpwd").val()){
                return "两次输入密码不一致，请重新输入！";
            }
        }
    })
    form.on("submit(changeuser)",function(data){
    	$.ajax({
		  type: 'post',
		  url: baseUrl+"gameBacktable/update",
		  data: data.field,
		  dataType: 'json',
		  success: function(res){
		  	if(res.meta.code === 200){
		  		layer.msg(''+res.meta.msg+'',{icon:1,time:1500});
			    sessionStorage.setItem('username', $(".uname").val());
			    setTimeout(function(){
			    	parent.location.reload()
			    },1000)
		  	}else {
		  		layer.msg(''+res.meta.msg+'',{icon:2,time:1500});
		  		return;
		  	}
		  }
		});
		
        return false;
    })


    //控制表格编辑时文本的位置【跟随渲染时的位置】
    $("body").on("click",".layui-table-body.layui-table-main tbody tr td",function(){
        $(this).find(".layui-table-edit").addClass("layui-"+$(this).attr("align"));
    });

})