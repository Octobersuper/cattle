layui.use(['form','layer','jquery'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,form = layui.form;
    //登录按钮
    form.on("submit(login)",function(data){
    	$.ajax({
		  type: 'post',
		  url: baseUrl+"gameBacktable/login",
		  data: data.field,
		  dataType: 'json',
		  success: function(res){
		  	if(res.meta.code == 200){
		  		// 设置值
			    sessionStorage.setItem('id', res.data.id);
			    sessionStorage.setItem('username', res.data.backname);
//			    sessionStorage.setItem('password', res.data.password);
			    // 取值
		  		$("#loginBtn").text("登录中...").attr("disabled","disabled").addClass("layui-disabled");
		        setTimeout(function(){
		            window.location.href = "../../index.html";
		        },1000);
		  	}else {
		  		layer.msg(''+res.meta.msg+'',{icon:2,time:1500});
		  		return;
		  	}
		  }
		});
        return false;
    })

    //表单输入效果
    $(".loginBody .input-item").click(function(e){
        e.stopPropagation();
        $(this).addClass("layui-input-focus").find(".layui-input").focus();
    })
    $(".loginBody .layui-form-item .layui-input").focus(function(){
        $(this).parent().addClass("layui-input-focus");
    })
    $(".loginBody .layui-form-item .layui-input").blur(function(){
        $(this).parent().removeClass("layui-input-focus");
        if($(this).val() != ''){
            $(this).parent().addClass("layui-input-active");
        }else{
            $(this).parent().removeClass("layui-input-active");
        }
    })
})
