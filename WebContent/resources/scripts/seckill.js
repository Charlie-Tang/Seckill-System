//存放交互逻辑
//JavaScript 模块化

var seckill = {
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return '/seckill/time/now';
        },
        exporser: function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        excution:function (seckillId,md5) {
            return '/seckill/'+ seckillId + '/' + md5 + '/excution';
        }
    },

    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },

    handleSeckill: function (seckillId,node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');

        $.post(seckill.URL.exporser(seckillId),{},function (result) {
            //在回调函数中执行交互流程
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀
                    //1获取地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.excution(seckillId, md5);
                    console.log("killUrl ="+killUrl);

                    //绑定一次点击事件
                    $('#killBtn').one('click', function () {
                        //执行秒杀请求
                        //1.先禁用按钮
                        $(this).addClass('disable');
                        //2.发送秒杀请求，进行秒杀
                        $.post(killUrl, {},function (result) {
                            if (result && result['success']) {
                                //alert("## home ##");
                                var killResult = result['data'];
                                console.log("killResult ="+killResult);
                                var state = killResult['state'];
                                console.log("state ="+state);
                                var stateInfo = killResult['stateInfo'];
                                console.log("stateInfo ="+stateInfo);
                                //显示秒杀结果
                                node.html('<sapn class="label label-sucess">' + stateInfo +'</sapn>');
                            }
                        });
                    });
                    node.show();
                } else {
                    var now = exposer['now'];
                    var starttime = exposer['starttime'];
                    var endtime = exposer['endtime'];

                    seckill.countdown(seckillIid,now,starttime,endtime)
                }
            } else {
                console.log('result:' + result);
            }

        });
    },

    countdown: function (seckillId,nowTime,startTime,endTime) {
        var seckillBox = $('#seckill-box');
        //alert("seckillId=" +seckillId +" nowTime=" + nowTime + " startTime=" +startTime + " endTime=" +endTime);
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        }else if (nowTime < startTime) {
            //秒杀尚未开始
            var killTime = new Date(startTime + 1000);

             seckillBox.countdown(killTime, function (event) {

                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');

                seckillBox.html(format);
            }).on('finish.countdown',function () {
                 //获取秒杀地址，执行秒杀
                 alert("Im here ");
                 seckill.handleSeckill(seckillId,seckillBox);
             });
        } else {
            //alert("Im there in ELSE");
            seckill.handleSeckill(seckillId,seckillBox);
        }
    },


    detail: {
        init: function (params) {

            //手机验证和登录
            var killPhone = $.cookie('killPhone');

            //alert("----t0----killPhone="+killPhone);
            //验证手机号
            if (!seckill.validatePhone(killPhone)) {

                //绑定手机号码
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,              //显示弹出层
                    backdrop: 'static',      //禁止位置关闭
                    keyboard: false          //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        //电话写入cookie中  期限是7天
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                        //刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<lable class="label label-danger">手机号错误！</lable>').show(300);
                    }
                });
            }
            //已经登录
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.URL.now(), {}, function (result) {
                //alert("----result----="+result['success'] + "|" +result['data']);
                if (result && result['success']) {
                   // alert("----result inside----="+result['']);
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId, nowTime, startTime, endTime);
                } else {
                    console.log('result:' + result);
                }
            });



        }
    }

}