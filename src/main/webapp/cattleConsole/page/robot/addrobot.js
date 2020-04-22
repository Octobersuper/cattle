layui.use(['form','layer','layedit','laydate','upload'],function(){
    var form = layui.form
    layer = parent.layer === undefined ? layui.layer : top.layer,
        laypage = layui.laypage,
        upload = layui.upload,
        layedit = layui.layedit,
        laydate = layui.laydate,
        $ = layui.jquery;

    form.on("submit(addNews)",function(data){
        var files = document.getElementById("file").files;
        var formData = new FormData();
        if(files.length > 0) {
            for(var i = 0; i < files.length; i++) {
                //append是将信息文件放到formdata中
                formData.append('file', files[i]);
            }
        }
        formData.append('name', data.field.name);
        formData.append('winodds', data.field.winodds);
                $.ajax({
                    type: "post",
                    url: baseUrl+'robot/insert',
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

function xmTanUploadImg(obj) {
    var reader;
    var file;
    for (var i=0;i<obj.files.length;i++){
        file = obj.files[i];
        reader = new FileReader();
        //读取文件过程方法
        reader.onload = function(e) {
            console.log("成功读取....");
            $('#demo2').html('<img src="' + e.target.result + '" class="layui-upload-img">')
            //或者 img.src = this.result;  //e.target == this
        }

        reader.readAsDataURL(file)
    }

}