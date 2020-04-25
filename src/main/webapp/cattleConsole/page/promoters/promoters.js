layui.use(['form','layer','table','laydate'],function(){
    $ = layui.jquery;
    var layer = layui.layer ,table = layui.table,laydate = layui.laydate,form = layui.form;
    //第一个实例
    table.render({
        elem: '#demo'
        ,id:'tabuser'
        ,height: "auto"
        ,method:"get"
        ,url: baseUrl+'gameRecharge/getc' //数据接口
        ,request: {
            pageName: 'pageNum' //页码的参数名称，默认：page
            ,limitName: 'pageSize' //每页数据量的参数名，默认：limit
        }
        ,page: true //开启分页
        ,cols: [[ //表头
            {field: 'rechargeid', title: 'ID',align:'center',fixed: 'left',width:100,sort:true}
            ,{field: 'user', title: '用户ID',align:'center',sort:true,width:100,templet:function (d) {
                    return d.user.userid;
                }}
            ,{field: 'user', title: '姓名',align:'center',sort:true,width:120,templet:function (d) {
                    return d.user.nickname;
                }}
            ,{field: 'user', title: '用户余额',align:'center',sort:true,width:100,templet:function (d) {
                    return d.user.money;
                }}
            ,{field: 'user', title: '用户保险箱余额',align:'center',sort:true,width:136,templet:function (d) {
                    return d.user.insure;
                }}
            ,{field: 'user', title: '收款账号',align:'center',sort:true,width:200,templet:function (d) {
                    if(d.type==3){
                        return "<span style='color: #0064ff'>支付宝：</span>"+d.user.zfb;
                    }else  if(d.type==4){
                        return "<span style='color: #ff7e00;'>银行卡：</span>"+d.user.bankcard;
                    }
                }}
            ,{field: 'type', title: '类型',align:'center',sort:true,width:150,templet:function (d) {
                    if(d.type==1){
                        return "<span style='color: green'>充值</span>";
                    }else  if(d.type==2){
                        return "<span style='color: #ffb508;'>提现</span>";
                    }else  if(d.type==3){
                        return "<span style='color: #019688;'>支付宝提现</span>";
                    }else  if(d.type==4){
                        return "<span style='color: #ff7e00;'>银行卡提现</span>";
                    }
                }}
            ,{field: 'money', title: '申请金额',align:'center',width:136,sort:true}
            ,{field: 'state', title: '申请情况',align:'center',width:136,sort:true,templet:function (d) {
                    if(d.state==0){
                        return "<span style='color: #807a4c'>审核中</span>";
                    }else  if(d.state==1){
                        return "<span style='color: #2d9602;'>已同意</span>";
                    }else  if(d.state==2){
                        return "<span style='color: red;'>已拒绝</span>";
                    }
                }}
            ,{field: 'createtime', title: '创建时间',align:'center',sort:true, width:160, toolbar: '#createTime'}
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
                    url: baseUrl+"gameRecharge/delete",
                    data:{rechargeid:data.rechargeid},
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
            //询问框
            layer.confirm('确定同意该用户的申请吗？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                var uinfo = {'rechargeid': data.rechargeid , "state" : 1 ,"type":data.type ,"money":data.money,"userid":data.user.userid}
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/gameRecharge/update",
                    data: uinfo,
                    async:false,
                    dataType: 'json',
                    success: function(res){
                        if(res.meta.code===200){
                            //加载层
                            var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
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
        }else if(layEvent === 'edit2') { //编辑
            //询问框
            layer.confirm('确定拒绝该用户的申请吗？', {
                btn: ['确定','取消'] //按钮
            }, function(){
                var uinfo = {'rechargeid': data.rechargeid , "state" : 2 ,'type':data.type,"money":data.money ,"userid":data.user.userid}
                //这里一般是发送修改的Ajax请求
                $.ajax({
                    type: 'post',
                    url: baseUrl+"/gameRecharge/update",
                    data: uinfo,
                    async:false,
                    dataType: 'json',
                    success: function(res){
                        if(res.meta.code===200){
                            //加载层
                            var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
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

})


