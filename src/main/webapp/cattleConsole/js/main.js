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
        ,url: baseUrl+'userTable/getUsers' //数据接口
        ,request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        ,page: true //开启分页
        ,cols: [[ //表头
            {field: 'userid', title: 'ID',align:'center',fixed: 'left',width:100,sort:true}
            ,{field: 'nickname', title: '昵称',align:'center',width:100,sort:true}
            ,{field: 'avatarurl', title: '头像',align:'center',width:100, event: 'lookimg',templet:"#showimg"}
            ,{field: 'insure', title: '保险箱余额',align:'center',width:120, event: 'setVb',sort:true}
            ,{field: 'money', title: '主钱包余额',event: 'setMoney',width:120,align:'center',sort:true}
            ,{field: 'backwater', title: '反水比例',align:'center',width:100,event: 'setback',sort:true,templet:function (d) {
                        return d.backwater+"%";
                }}
            ,{field: 'winodds', title: '游戏胜率',align:'center',width:100,event: 'setwin',sort:true,templet:function (d) {
                    if(d.winodds=="-1"){
                        return "<span>正常</span>";
                    }else{
                        return d.winodds+"%";
                    }
                }}
            ,{field: 'fId', title: '上级ID',align:'center',width:100,sort:true,templet:function (d) {
                    if(d.fId==null){
                        return "<span>无上级</span>";
                    }else{
                        return d.fId;
                    }
                }}
            ,{field: 'role', title: '角色',align:'center',width:100,sort:true,templet:function (d) {
                    if(d.role==0){
                        return "<span>普通玩家</span>";
                    }else{
                        return "<span>代理</span>";
                    }
                }}
            ,{field: 'bankcard', title: '银行卡信息',align:'center',width:120,sort:true,templet:function (d) {
                    if(d.bankcard==null){
                        return "<span>无</span>";
                    }else{
                        return d.bankcard;
                    }
                }}
            ,{field: 'zfb', title: '支付宝信息',align:'center',width:120,sort:true,templet:function (d) {
                    if(d.zfb==null || d.zfb==""){
                        return "<span>无</span>";
                    }else{
                        return d.zfb;
                    }
                }}
            ,{field: 'createTime', title: '创建时间',align:'center',width:200,sort:true, toolbar: '#createTime'}
            ,{fixed: 'right',field: 'userType', title: '操作', width:100, align:'center',  event:"show",toolbar: '#barDemo2'} //这里的toolbar值是模板元素的选择器
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
                    url: baseUrl+"admin/delete",
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
            var arr = subString(data.avatarurl);
            var data = "";
            for (var i = 0 ; i < arr.length ; i++){
                data+='<img style="text-align: center" src="'+arr[i]+'">';
            }
            layer.open({
                title: '查看图片'
                ,area: ['50%', '50%']
                ,content: data
            });
        }else if(layEvent === 'look') { //查看下级
            var index = layui.layer.open({
                title: data.nickname+"的下级列表",
                type: 2,
                content: "myUsers/myUsers.html",
                success: function (layero, index) {
                    var body = layui.layer.getChildFrame('body', index);
                    body.find("#id").val(data.userid);
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
        }else if(layEvent === 'setVb'){//修改保险箱余额
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.nickname +'] 的保险箱余额'
                ,value: data.insure
            }, function(value, index){
                layer.close(index);
                var uinfo = {'userid': data.userid , "insure" : value }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/userTable/update",
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
                                    insure: value
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
        }else if(layEvent === 'setMoney'){//修改vB
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.nickname +'] 的主钱包余额'
                ,value: data.money
            }, function(value, index){
                layer.close(index);
                var uinfo = {'userid': data.userid , "money" : value }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/userTable/update",
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
        }else if(layEvent === 'setwin'){
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.nickname +'] 的游戏胜率'
                ,value: data.winodds
            }, function(value, index){
                layer.close(index);
                var uinfo = {'userid': data.userid , "winodds" : value }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/userTable/update",
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
        }else if(layEvent === 'setback'){//
            if(data.role!=1){
                layer.msg("代理才可以设置反水");
                return;
            }
            layer.prompt({
                formType: 2
                ,shadeClose:true
                ,title: '修改 ['+ data.nickname +'] 的返水比例'
                ,value: data.backwater
            }, function(value, index){
                layer.close(index);
                var uinfo = {'userid': data.userid , "backwater" : value }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/userTable/update",
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
                                    backwater: value
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
        }else if(layEvent === 'edit') { //编辑
            var role = 0;
            if (data.role==0){
                role = 1;
            }
            //询问框
            layer.confirm('确定修改用户类型？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                var uinfo = {'userid': data.userid , "role" : role }
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/userTable/update",
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
    layui.form.on('switch(switchTest)',function(formObj) {
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
                url: baseUrl+"userTable/update",
                data: {'userid': obj.data.userid , "state" : lock },
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
})


