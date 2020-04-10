var baseUrl = "http://localhost:8080/cattle/";

//获取系统时间
var newDate = '';
getLangDate();
//值小于10时，在前面补0
function dateFilter(date){
    if(date < 10){return "0"+date;}
    return date;
}


function getLangDate(){
    var dateObj = new Date(); //表示当前系统时间的Date对象
    var year = dateObj.getFullYear(); //当前系统时间的完整年份值
    var month = dateObj.getMonth()+1; //当前系统时间的月份值
    var date = dateObj.getDate(); //当前系统时间的月份中的日
    var day = dateObj.getDay(); //当前系统时间中的星期值
    var weeks = ["星期日","星期一","星期二","星期三","星期四","星期五","星期六"];
    var week = weeks[day]; //根据星期值，从数组中获取对应的星期字符串
    var hour = dateObj.getHours(); //当前系统时间的小时值
    var minute = dateObj.getMinutes(); //当前系统时间的分钟值
    var second = dateObj.getSeconds(); //当前系统时间的秒钟值
    var timeValue = "" +((hour >= 12) ? (hour >= 18) ? "晚上" : "下午" : "上午" ); //当前时间属于上午、晚上还是下午
    newDate = dateFilter(year)+"年"+dateFilter(month)+"月"+dateFilter(date)+"日 "+" "+dateFilter(hour)+":"+dateFilter(minute)+":"+dateFilter(second);
    document.getElementById("nowTime").innerHTML = "管理员，"+timeValue+"好！ 当前时间为： "+newDate+"　"+week;
    setTimeout("getLangDate()",1000);
}


var cacheStr = window.sessionStorage.getItem("cache"),
    oneLoginStr = window.sessionStorage.getItem("oneLogin");
layui.use(['form','jquery',"layer"],function() {
    var form = layui.form,
        $ = layui.jquery,
        layer = layui.layer ;

    //判断是否web端打开
//  if(!/http(s*):\/\//.test(location.href)){
//      layer.alert("请先将项目部署到 localhost 下再进行访问【建议通过tomcat、webstorm、hb等方式运行，不建议通过iis方式运行】，否则部分数据将无法显示");
//  }else{    //判断是否处于锁屏状态【如果关闭以后则未关闭浏览器之前不再显示】
//      if(window.sessionStorage.getItem("lockcms") != "true" && window.sessionStorage.getItem("showNotice") != "true"){
//      }
//  }

    //判断是否设置过头像，如果设置过则修改顶部、左侧和个人资料中的头像，否则使用默认头像
    if(window.sessionStorage.getItem('userFace') &&  $(".userAvatar").length > 0){
        $("#userFace").attr("src",window.sessionStorage.getItem('userFace'));
        $(".userAvatar").attr("src",$(".userAvatar").attr("src").split("images/")[0] + "images/" + window.sessionStorage.getItem('userFace').split("images/")[1]);
    }else{
        $("#userFace").attr("src","../../images/face.jpg");
    }


    //退出
    $(".signOut").click(function(){
        window.sessionStorage.removeItem("menu");
        menu = [];
        window.sessionStorage.removeItem("curmenu");
    })

    //判断是否修改过系统基本设置，去显示底部版权信息
    if(window.sessionStorage.getItem("systemParameter")){
        systemParameter = JSON.parse(window.sessionStorage.getItem("systemParameter"));
        $(".footer p span").text(systemParameter.powerby);
    }
})
//时间戳转换时间
function timestampToTime2(timestamp) {
    //var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours()< 10 ? '0'+(date.getHours())+ ':' :date.getHours()+ ':' ;
    m = date.getMinutes()< 10 ? '0'+(date.getMinutes())+ ':' : date.getMinutes()+ ':' ;
    s = date.getSeconds()< 10 ? '0'+(date.getSeconds()) : date.getSeconds() ;
//    return Y+M+D+h+m+s;
    return Y+M+D;
}
//时间戳转换时间
function timestampToTime(timestamp) {
    //var date = new Date(timestamp * 1000);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours()< 10 ? '0'+(date.getHours())+ ':' :date.getHours()+ ':' ;
    m = date.getMinutes()< 10 ? '0'+(date.getMinutes())+ ':' : date.getMinutes()+ ':' ;
    s = date.getSeconds()< 10 ? '0'+(date.getSeconds()) : date.getSeconds() ;
    return Y+M+D+h+m+s;
//  return Y+M+D;
}
function timestampToTime11(timestamp) {
    var date = new Date(timestamp);//时间戳为10位需*1000，时间戳为13位的话不需乘1000
    Y = date.getFullYear() + '-';
    M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
    D = date.getDate() + ' ';
    h = date.getHours()< 10 ? '0'+(date.getHours())+ ':' :date.getHours()+ ':' ;
    m = date.getMinutes()< 10 ? '0'+(date.getMinutes())+ ':' : date.getMinutes()+ ':' ;
    s = date.getSeconds()< 10 ? '0'+(date.getSeconds()) : date.getSeconds() ;
    return '<span style="color: #16c2c2;">'+ Y+M+D+h+m+s +'</span>';
}
//时间转换时间戳
function dateTo(strdate){
	var date = new Date(strdate);
	    // 有三种方式获取
//		var time1 = date.getTime();
//		var time2 = date.valueOf();
	    var time3 = Date.parse(date);
//		console.log(time1);//1398250549123
//		console.log(time2);//1398250549123
//		console.log(time3);//1398250549000
	return time3;
}

function subString(d) {
    var arr = d.split(",");
    return arr;
}

function subString2(d) {
    var arr = d.split("-");
    return arr;
}