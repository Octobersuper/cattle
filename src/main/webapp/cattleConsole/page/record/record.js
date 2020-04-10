layui.use(['form','layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate,form = layui.form;
    //第一个实例
    table.render({
        elem: '#demo'
        ,id:'tabuser'
        ,height: "auto"
        ,method:"post"
        ,url: baseUrl+'record/getAll' //数据接口
        ,request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        ,page: true //开启分页
        ,cols: [[ //表头
            {field: 'id', title: 'ID',align:'center',fixed: 'left',width:50,sort:true}
            ,{field: 'roomType', title: '房间类型',align:'center',sort:true,templet:function (d) {
                    return d.roomType+"局";
                }}
            ,{field: 'roomMoney', title: '房间底分',align:'center',sort:true}
            ,{field: 'roomNum', title: '房间号',align:'center',sort:true}
            ,{field: 'recordType', title: '游戏类型',align:'center',sort:true,templet:function (d) {
                    if(d.recordType===0){
                        return "<span style='color: #1E9FFF'>簸簸牛牛</span>";
                    }else if(d.recordType===1){
                        return "<span style='color: #ff8e0f'>经典牛牛</span>";
                    }else if(d.recordType===2){
                        return "<span style='color: #8782ff'>推筒子</span>";
                    }
                }}
            ,{field: 'createTime', title: '创建时间',align:'center',sort:true, width:160, toolbar: '#createTime'}
            ,{fixed: 'right',field: 'userType', title: '操作', width:200, align:'center', toolbar: '#barDemo'} //这里的toolbar值是模板元素的选择器
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
                    url: baseUrl+"record/delete",
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
        }else if(layEvent === 'let') { //查看下级
            var index = layui.layer.open({
                title: "房间"+data.roomNum+"的详细信息",
                type: 2,
                area: ["1200px","800px"],
                content: "recordInfo.html",
                success: function (layero, index) {
                    var body = layui.layer.getChildFrame('body', index);
                    body.find("#roomNum").val(data.roomNum);
                    //console.log(data.activeTime)
                    setTimeout(function () {
                        layui.layer.tips('点击此处返回', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    }, 300)
                }
            });
        }
    });


    var $ = layui.$, active = {
        reload: function(){
            var demoReload = $('#demoReload');
            var gameType = $('#gameType');
            var retype = gameType.val();

            //执行重载
            table.reload('tabuser', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    roomNum: demoReload.val(),
                    recordType:retype
                }
            });
        }
    };

    $('.demoTable .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });


})


