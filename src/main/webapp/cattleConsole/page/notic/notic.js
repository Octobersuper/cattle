layui.use(['form','layer','layedit','laydate','upload'],function(){
    var form = layui.form
    layer = parent.layer === undefined ? layui.layer : top.layer,
        laypage = layui.laypage,
        upload = layui.upload,
        layedit = layui.layedit,
        laydate = layui.laydate,
        $ = layui.jquery;



    $.ajax({
        type: "get",
        url: baseUrl+"gameNotice/get",
        async: true,
        processData: false,
        contentType: false,
        mimeType: "multipart/form-data",
        success: function(res) {
            jsondata=$.parseJSON(res);
            if(jsondata.meta.code==200){
                //$('#demo2').html('<img src="' + baseUrl + jsondata.data.img + '" class="layui-upload-img">');
                $("#remark").val(jsondata.data.value);
                //$("#title").val(jsondata.data.title);
            }else{
                //加载层
                var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                setTimeout(function(){ layer.msg(''+jsondata.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
            }
        }
    })

    form.on("submit(addNews)",function(data){
        var formData = new FormData();
        /*var files = document.getElementById("file").files;
        //如果有选择图片则上传图片
        console.log(data)
        var formData = new FormData();
        if(files.length > 0) {
            for(var i = 0; i < files.length; i++) {
                //append是将信息文件放到formdata中
                formData.append('uploadFile', files[i]);
            }
            return false;
        }*/
        formData.append('value', data.field.remark);//描述
        formData.append('noticeid',1);//描述
        $.ajax({
            type: "post",
            url: baseUrl+"/gameNotice/update",
            data: formData,
            async: true,
            processData: false,
            contentType: false,
            mimeType: "multipart/form-data",
            success: function(res) {
                jsondata=$.parseJSON(res);
                if(jsondata.meta.code==200){
                    //加载层
                    var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                    setTimeout(function(){ layer.msg(''+jsondata.meta.msg+'',{icon:1,time:1000});index.closed}, 1000);
                    setTimeout(function(){ location.reload() }, 2000);
                }else{
                    //加载层
                    var index = layer.load(0, {shade: false,time:1000} ); //0代表加载的风格，支持0-2
                    setTimeout(function(){ layer.msg(''+jsondata.meta.msg+'',{icon:2,time:1000});index.closed}, 1000);
                }
            }
        })
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