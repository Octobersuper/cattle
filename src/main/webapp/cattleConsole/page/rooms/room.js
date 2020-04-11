layui.use(['layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate;
    //第一个实例
	table.render({
	    elem: '#demo'
	    ,id:'tabuser'
	    ,height: "auto"
	    ,method:"get"
	    ,url: baseUrl+'/rooms/get' //数据接口
	    ,request: {
		  pageName: 'pageNum' //页码的参数名称，默认：page
		  ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
		} 
	    ,page: true //开启分页
	    ,cols: [[ //表头
	      	{field: 'id', title: 'ID',align:'center',width:80}
            ,{field: 'roomnumber', title: '房间号',align:'center'}
	      	,{field: 'maxnumber', title: '局数',align:'center'}
            ,{field: 'fen', title: '底分',align:'center'}
            ,{field: 'jionfen', title: '准入分',align:'center'}
            ,{field: 'room_state', title: '房间状态',align:'center',templet:function (d) {
                    if(d.room_state===0){
                        return "空闲";
                    }else{
                        return "游戏中";
                    }
                }}
            ,{field: 'game_number', title: '对战局数',align:'center',templet:function (d) {
                   return d.game_number+"/"+d.maxnumber;
                }}
            ,{field: 'user_number', title: '房间人数',align:'center'}
	      	,{field: 'createTime', title: '创建时间',align:'center',sort:true, toolbar: '#createTime'}
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
			  url: baseUrl+"/rooms/delete",
			  data:{id:data.id,roomnumber:data.roomnumber},
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
                 body.find("#roomnumber").val(data.roomnumber);
                 body.find("#jionfen").val(data.jionfen);
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
            content: "addroom.html",
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