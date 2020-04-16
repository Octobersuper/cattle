layui.use(['form','layer','layedit','laydate','upload'],function(){
    var form = layui.form
    layer = parent.layer === undefined ? layui.layer : top.layer,
        laypage = layui.laypage,
        upload = layui.upload,
        layedit = layui.layedit,
        laydate = layui.laydate,
        $ = layui.jquery;

    form.on("submit(addNews)",function(data){
        var formData = new FormData();
        formData.append('maxnumber', data.field.maxnumber);
        formData.append('fen', data.field.fen);
        formData.append('jionfen', data.field.jionfen);
        formData.append('roomnumber', data.field.roomnumber);
        formData.append('roomtype', data.field.roomtype);
        formData.append('id', data.field.id);
        $.ajax({
            type: "post",
            url: baseUrl+'rooms/update',
            data: formData,
            processData: false,   // jQuery不要去处理发送的数据
            contentType: false,   // jQuery不要去设置Content-Type请求头
            success: function(res) {
                if(res.meta.code===200){
                    //加载层
                    var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                    setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:1,time:1000});index.closed}, 1000);
                    setTimeout(function(){ parent.location.reload() }, 2000);
                }else{
                    //加载层
                    var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                    setTimeout(function(){ layer.msg(''+res.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                }
            }
        })
        return false;
    })
})