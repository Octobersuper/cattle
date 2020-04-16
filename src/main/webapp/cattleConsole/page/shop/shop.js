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
        ,url: baseUrl+'diamondShop/getList' //数据接口
        ,page: false //开启分页
        ,cols: [[ //表头
            {field: 'id', title: 'ID',align:'center',fixed: 'left',width:100,sort:true}
            ,{field: 'name', title: '标题',align:'center'/*, event: "setname"*/,width:100,sort:true}
            ,{field: 'state', title: '状态',align:'center',width:100,sort:true,templet:function (d) {
                    if(d.state==1){
                        return "<span style='color: green'>已启用</span>";
                    }else{
                        return "<span style='color: red'>已停用</span>";
                    }
                }}
            ,{fixed: 'right',field: 'userType', title: '操作', width:100, align:'center',toolbar: '#barDemo'}
            ,{fixed: 'right',field: 'userType', title: '操作', width:100, align:'center',  event:"show",toolbar: '#barDemo2'} //这里的toolbar值是模板元素的选择器
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
                ,title: '修改 ['+ data.name +'] 的通道名称'
                ,value: data.name
            }, function(value, index){
                layer.close(index);
                var uinfo = {'id': data.id , "name" : value ,"state": data.state}
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/diamondShop/update",
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
        }else if(layEvent === 'look') { //查看下级
            var index = layui.layer.open({
                title: data.name+"的商品配置",
                type: 2,
                content: "shops.html",
                success: function (layero, index) {
                    var body = layui.layer.getChildFrame('body', index);
                    body.find("#id").val(data.id);
                    //console.log(data.activeTime)
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


    //监听锁定操作
    layui.form.on('switch(switchTest)',function(formObj) {
        table.on('tool(test)',function(obj) {
            if (obj.event === 'show') {
                if (formObj.elem.checked == true) {
                    var lock = 1;
                } else {
                    var lock = 0;
                }

            }
            $.ajax({
                type: 'post',
                url: baseUrl+"diamondShop/update",
                data: {'id': obj.data.id , "state" : lock },
                async:false,
                dataType: 'json',
                success: function(res){
                    if(res.meta.code==200){
                        //加载层
                        var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                        setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                        if(lock===1){
                            layer.tips('通道已启用', formObj.othis,{
                                tips: 1
                            });
                        }else if(lock===0){
                            layer.tips('通道已停用', formObj.othis,{
                                tips: 3
                            });
                        }
                        location.reload();
                    }else{
                        //加载层
                        var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                        setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                    }
                }
            });
        });
    });
})


