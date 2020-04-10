layui.use(['form','layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate,form = layui.form;
    //第一个实例
    table.render({
        elem: '#demo'
        ,id:'tabuser'
        ,height: "auto"
        ,method:"get"
        ,url: baseUrl+'user/getUsers' //数据接口
        ,request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        ,page: true //开启分页
        ,cols: [[ //表头
            {field: 'id', title: 'ID',align:'center',fixed: 'left',width:50,sort:true}
            ,{field: 'wxname', title: '昵称',align:'center',sort:true}
            ,{field: 'phone', title: '电话',align:'center',sort:true}
            ,{field: 'state', title: '账号类型',align:'center',sort:true,templet:function (d) {
                    if(d.state==0){
                        return "<span style='color: green'>正常</span>";
                    }else{
                        return "<span style='color: red;'>冻结</span>";
                    }
                }}
            ,{field: 'isLogin', title: '账号状态',align:'center', width:150,sort:true,templet:function (d) {
                    if(d.isLogin==0){
                        return "<span style='color: red'>未登录</span>";
                    }else{
                        return "<span style='color: green;'>已登录</span>\n" +
                            "\t\t\t<a class=\"layui-btn layui-btn-warm layui-btn-xs\" lay-event=\"down\">修改</a>";
                    }
                }}
            ,{field: 'money', title: '金币余额',event: 'setMoney',align:'center',sort:true,templet:function (d) {
                    if(d.money==null){
                        return "<span>0.00</span>";
                    }else{
                        return d.money+".00";
                    }
                }}
            ,{field: 'remard', title: '签名',align:'center',sort:true}
            ,{field: 'sex', title: '性别',align:'center',sort:true,templet:function (d) {
                    if(d.sex==0){
                        return "<span style='color: green'>女</span>";
                    }else{
                        return "<span style='color: red;'>男</span>";
                    }
                }}
            ,{field: 'address', title: '地址',align:'center',sort:true}
            ,{field: 'ip', title: 'IP',align:'center',sort:true}
            ,{field: 'role', title: '角色',align:'center',sort:true,templet:function (d) {
                    if(d.role==0){
                        return "<span style='color: green'>普通玩家</span>";
                    }else if(d.role==1){
                        return "<span style='color: red;'>推广员</span>";
                    }
                }}
            ,{field: 'createtime', title: '创建时间',align:'center',sort:true, width:160, toolbar: '#createTime'}
            ,{fixed: 'right',field: 'userType', title: '操作', width:100, align:'center',event:"show", toolbar: '#barDemo2'}
            ,{fixed: 'right',field: 'userType', title: '操作', width:150, align:'center', toolbar: '#barDemo'} //这里的toolbar值是模板元素的选择器
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
                    url: baseUrl+"user/delete",
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
        }else if(layEvent === 'down'){ //删除
            layer.confirm('确定将此用户的状态改为离线吗?', function(index){
                layer.close(index);
                //向服务端发送删除指令
                $.ajax({
                    type: 'post',
                    url: baseUrl+"user/update",
                    data:{id:data.id,isLogin:0},
                    dataType: 'json',
                    success: function(res){
                        if(res.meta.code===200){
                            //加载层
                            var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                            setTimeout(function(){
                                obj.update({
                                    isLogin: "<span style='color: red'>离线</span>"
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
        }else if(layEvent === 'setMoney'){//修改金币
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.wxname +'] 的金币余额'
                ,value: data.money
            }, function(value, index){
                layer.close(index);
                var uinfo = {'id': data.id , "money" : value}
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"user/update",
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
                                    money: value+".00"
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
        }else if(layEvent === 'setPassword'){//修改密码
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.wxname +'] 的登录密码'
                ,value: data.password
            }, function(value, index){
                layer.close(index);
                var uinfo = {'id': data.id , "password" : value }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"user/update",
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
        }else if(layEvent === 'myUsers') { //查看下级
            var index = layui.layer.open({
                title: data.wxname+"的下级信息",
                type: 2,
                content: "myUsers.html?id="+data.id,
                success: function (layero, index) {
                    var body = layui.layer.getChildFrame('body', index);
                    body.find("#id").val(data.id);
                    //console.log(data.activeTime)
                    setTimeout(function () {
                        layui.layer.tips('点击此处返回', '.layui-layer-setwin .layui-layer-close', {
                            tips: 3
                        });
                    }, 300)
                }
            });
            layui.layer.full(index);
            //改变窗口大小时，重置弹窗的宽高，防止超出可视区域（如F12调出debug的操作）
            $(window).on("resize",function(){
                layui.layer.full(index);
            })
        }else if(layEvent === 'edit') { //编辑
            var type = 0;
            if (data.type==0){
                type = 1;
            }
            //询问框
            layer.confirm('确定修改用户类型？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                var uinfo = {'id': data.id , "type" : type }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/manage/updateUser",
                    data: uinfo,
                    async:false,
                    dataType: 'json',
                    success: function(res){
                        if(res.code==100){
                            //加载层
                            var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.msg+'',{icon:1,time:1000});index.closed}, 500);
                            setTimeout(function(){ location.reload() }, 1000);
                        }else{
                            //加载层
                            var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                            setTimeout(function(){ layer.msg(''+res.msg+'',{icon:2,time:1000});index.closed}, 1000);
                        }
                    }
                });
            }, function(){
                //取消
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



    //监听锁定操作
    form.on('switch(switchTest)',function(formObj) {
        table.on('tool(test)',function(obj) {
            if (obj.event === 'show') {
                if (formObj.elem.checked == true) {
                    var lock = 0;
                } else {
                    var lock = 1;
                }

            }
            $.ajax({
                type: 'post',
                url: baseUrl+"user/update",
                data: {'id': obj.data.id , "state" : lock },
                async:false,
                dataType: 'json',
                success: function(res){
                    if(res.meta.code==200){
                        //加载层
                        var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                        setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                        if(lock==0){
                            layer.tips('账号已解封', formObj.othis,{
                                tips: 1
                            });
                            obj.update({
                                state: "<span style='color: green'>正常</span>"
                            });
                        }else{
                            layer.tips('账号已冻结', formObj.othis,{
                                tips: 3
                            });
                            obj.update({
                                state: "<span style='color: red'>冻结</span>"
                            });
                        }
                    }else{
                        //加载层
                        var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                        setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                    }
                }
            });
        });
    });

    //监听锁定操作
    form.on('switch(switchTest1)',function(formObj) {
        table.on('tool(test)',function(obj) {
            if (obj.event === 'show1') {
                if (formObj.elem.checked == true) {
                    var lock = 1;
                } else {
                    var lock = 0;
                }

            }
            $.ajax({
                type: 'post',
                url: baseUrl+"user/update",
                data: {'id': obj.data.id , "isPay" : lock },
                async:false,
                dataType: 'json',
                success: function(res){
                    if(res.meta.code==200){
                        //加载层
                        var index = layer.load(0, {shade: false,time:500} ); //0代表加载的风格，支持0-2
                        setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 500);
                        if(lock==0){
                            layer.tips('充值状态已更改', formObj.othis,{
                                tips: 1
                            });
                            obj.update({
                                isPay: "<span style='color: red'>未充值</span>"
                            });
                        }else{
                            layer.tips('充值状态已更改', formObj.othis,{
                                tips: 3
                            });
                            obj.update({
                                isPay: "<span style='color: green'>已充值</span>"
                            });
                        }
                    }else{
                        //加载层
                        var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                        setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                    }
                }
            });
        });
    });


    //获取url中的参数
    function getUrlParam(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
        var r = window.location.search.substr(1).match(reg);  //匹配目标参数
        if (r != null) return unescape(r[2]); return null; //返回参数值
    }

    var id = getUrlParam('id');

    if (id){
        table.reload('tabuser', {
            url: baseUrl+'/user/getUsers'
            ,where: {
                fId: id
            } //设定异步数据接口的额外参数
            //,height: 300
        });
    }

})


