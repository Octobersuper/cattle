layui.use(['layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate;
    //第一个实例
    table.render({
        elem: '#demo'
        ,id:'tabuser'
        ,height: "auto"
        ,method:"get"
        ,limits: [100,200,500]
        ,limit: 100 //每页默认显示的数量
        ,url: baseUrl+'gameDiamondShop/getList' //数据接口
        ,page: false //开启分页
        ,where:{
            dId:$("#id").val()
        }
        ,cols: [[ //表头
            {field: 'id', title: 'ID',align:'center',fixed: 'left',width:100,sort:true}
            ,{field: 'name', title: '名称',align:'center', event: "setname",width:100,sort:true}
            ,{field: 'money', title: '金额',align:'center', event: "setmoney",width:100,sort:true}
        ]]
    });
    //监听工具条
    table.on('tool(test)', function(obj){
        var data = obj.data;
        var layEvent = obj.event;
        var tr = obj.tr; //获得当前行 tr 的DOM对象
        if(layEvent === 'setname'){//
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.name +'] 的商品名称'
                ,value: data.name
            }, function(value, index){
                layer.close(index);
                var uinfo = {'id': data.id , "name" : value}
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/gameDiamondShop/update",
                    data: uinfo,
                    async:false,
                    dataType: 'json',
                    success: function(res){
                        if(res.meta.code==200){
                            //加载层
                            var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                            setTimeout(function(){
                                obj.update({
                                    name: value
                                });
                            }, 1000);
                        }else{
                            //加载层
                            var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                        }
                    }
                });
            });
        }else if(layEvent === 'setmoney'){//
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.name +'] 的商品金额'
                ,value: data.money
            }, function(value, index){
                layer.close(index);
                var uinfo = {'id': data.id , "money" : value}
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/gameDiamondShop/update",
                    data: uinfo,
                    async:false,
                    dataType: 'json',
                    success: function(res){
                        if(res.meta.code==200){
                            //加载层
                            var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                            setTimeout(function(){
                                obj.update({
                                    money: value
                                });
                            }, 1000);
                        }else{
                            //加载层
                            var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                        }
                    }
                });
            });
        }
    });


    var $ = layui.$, active = {
        reload: function(){
            var demoReload = $('#demoReload');

            //执行重载
            table.reload('tabuser', {
                page: {
                    curr: 1 //重新从第 1 页开始
                }
                ,where: {
                    phone: demoReload.val()
                }
            });
        }
    };

    $('.demoTable .layui-btn').on('click', function(){
        var type = $(this).data('type');
        active[type] ? active[type].call(this) : '';
    });
})


