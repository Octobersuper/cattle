layui.use(['layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate;
    //第一个实例
	table.render({
	    elem: '#demo'
	    ,id:'tabuser'
	    ,height: "auto"
	    ,method:"get"
	    ,url: baseUrl+'/robot/get' //数据接口
	    ,request: {
		  pageName: 'pageNum' //页码的参数名称，默认：page
		  ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
		} 
	    ,page: true //开启分页
	    ,cols: [[ //表头
	      	{field: 'id', title: 'ID',align:'center',width:200}
            ,{field: 'name', title: '名称',align:'center'}
	      	,{field: 'img', title: '头像',align:'center' ,event: 'lookimg',templet:"#showimg"}
            ,{field: 'winodds', title: '游戏胜率',align:'center',event: 'setwin',sort:true,templet:function (d) {
                    if(d.winodds=="-1"){
                        return "<span>正常</span>";
                    }else{
                        return d.winodds+"%";
                    }
                }}
	        ,{fixed: 'right',field: 'userType', title: '操作', width:150, align:'left', toolbar: '#barDemo'} //这里的toolbar值是模板元素的选择器
	    ]]
	});
    //监听工具条
	table.on('tool(test)', function(obj){
	  var data = obj.data;
	  var layEvent = obj.event;
	  var tr = obj.tr; //获得当前行 tr 的DOM对象
	 if(layEvent === 'del'){ //删除
	    layer.confirm('真的删除行么', function(index){
	      layer.close(index);
	      //向服务端发送删除指令
	       $.ajax({
			  type: 'post',
			  url: baseUrl+"/robot/delete",
			  data:{id:data.id},
			  dataType: 'json',
			  success: function(res){
                  if(res.meta.code===200){
                      //加载层
                      var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                      setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                      setTimeout(obj.del(), 1000); //删除对应行（tr）的DOM结构，并更新缓存
                  }else{
                      //加载层
                      var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                      setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                  }
			  }
			});
	    });
	  }else if(layEvent === 'lookimg'){
         var arr = subString(data.img);
         var data = "";
         for (var i = 0 ; i < arr.length ; i++){
             data+='<img style="text-align: center" src="'+arr[i]+'">';
         }
         layer.open({
             title: '查看图片'
             ,area: ['50%', '50%']
             ,content: data
         });
     }else if(layEvent === 'setwin'){
         layer.prompt({
             formType: 2
             ,shadeClose:true
             ,title: '修改 ['+ data.name +'] 的游戏胜率'
             ,value: data.winodds
         }, function(value, index){
             layer.close(index);
             var uinfo = {'id': data.id , "winodds" : value }
             //这里一般是发送修改的Ajax请求
             $.ajax({
                 type: 'post',
                 url: baseUrl+"/robot/update",
                 data: uinfo,
                 async:false,
                 dataType: 'json',
                 success: function(res){
                     if(res.meta.code==200){
                         //加载层
                         var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                         setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                         setTimeout(function(){ location.reload() }, 1000);
                     }else{
                         //加载层
                         var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                         setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                     }
                 }
             });
         });
     }else if(layEvent === 'edit') { //编辑
         var index = layui.layer.open({
             title: "修改房间信息",
             type: 2,
             content: "edtroom.html",
             success: function (layero, index) {
                 var body = layui.layer.getChildFrame('body', index);
                 body.find("#id").val(data.id);
                 body.find("#maxnumber").val(data.maxnumber);
                 body.find("#fen").val(data.fen);
                 body.find("#jionfen").val(data.jionfen);
                 body.find("#roomnumber").val(data.roomnumber);
                 body.find(":radio[name='roomtype'][value='" + data.roomtype + "']").prop("checked", "checked");
                 setTimeout(function () {
                     layui.layer.tips('点击此处返回', '.layui-layer-setwin .layui-layer-close', {
                         tips: 3
                     });
                 }, 500)
             }
         });
         layui.layer.full(index);
         //改变窗口大小时，重置弹窗的宽高，防止超出可视区域（如F12调出debug的操作）
         $(window).on("resize",function(){
             layui.layer.full(index);
         })
     }
	});
    $(".addLine_btn").click(function(){
        banneradd();
    })
    function banneradd(edit) {
        var index = layui.layer.open({
            title: "添加房间",
            type: 2,
            content: "addrobot.html",
            success: function (layero, index) {
                var body = layui.layer.getChildFrame('body', index);
                setTimeout(function () {
                    layui.layer.tips('点击此处返回', '.layui-layer-setwin .layui-layer-close', {
                        tips: 3
                    });
                }, 500)
            }
        });
        layui.layer.full(index);
        //改变窗口大小时，重置弹窗的宽高，防止超出可视区域（如F12调出debug的操作）
        $(window).on("resize",function(){
            layui.layer.full(index);
        })
    }
    
    var $ = layui.$, active = {
    	    reload: function(){
    	      var demoReload = $('#demoReload');
    	      
    	      //执行重载
    	      table.reload('tabuser', {
    	        page: {
    	          curr: 1 //重新从第 1 页开始
    	        }
    	        ,where: {
                      roomnumber: demoReload.val()
    	        }
    	      });
    	    }
    	  };
    	  
    	  $('.demoTable .layui-btn').on('click', function(){
    	    var type = $(this).data('type');
    	    active[type] ? active[type].call(this) : '';
    	  });
    	  
})
