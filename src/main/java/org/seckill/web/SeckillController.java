package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
//@RequestMapping(value="/seckill") //模块/资源/{id}/细分 
public class SeckillController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public String list(Model model) {
        //list.jsp + model = ModelAndView

        List<Seckill> list = seckillService.getSeckillList();

        logger.info("--------------------------------list--------------------------------");
        for(Seckill seckill:list){
            logger.info("seckill="+seckill.toString());
        }
        logger.info("--------------------------------list--------------------------------");


        model.addAttribute("list",list);

        return "list"; //  /WEB-INF/jsp/list.jsp
    }

    @SuppressWarnings("unused")
	@RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {

        logger.info("--------------------------------detail--------------------------------");

        logger.info("seckillId=" + seckillId);

        if (seckillId == null) {
            return "redirect:/seckill/list";
        }

        Seckill seckill = seckillService.getbyId(seckillId);

        logger.info("seckill=" + seckill.toString());

        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);

        logger.info("--------------------------------detail out--------------------------------");

        return "detail";
    }

    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable Long seckillId) {

        SeckillResult<Exposer> result;
        logger.info("### controller exposer is called ###");
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);

            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.info(e.getMessage()+"****error****");
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }


        return result;
    }


    @RequestMapping(value = "/{seckillId}/{md5}/excution",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> excute(@PathVariable("seckillId") Long seckillId,
                                                 @PathVariable("md5") String md5,
                                                 @CookieValue(value = "killPhone",required = false) Long phone) {

        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "该客户未注册！");
        }

        @SuppressWarnings("unused")
		SeckillResult<SeckillExecution> result;


        try {
            SeckillExecution seckillExcution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, seckillExcution);
        } catch (RepeatKillException e) {
            //重复秒杀
            logger.info("## RepeatKillException ##");
            SeckillExecution seckillExcution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, seckillExcution);
        } catch (SeckillCloseException e) {
            //秒杀关闭
            logger.info("## SeckillCloseException ##");

            SeckillExecution seckillExcution = new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(true, seckillExcution);
        } catch (Exception e) {
            //其他所有错误
            logger.info("## " + e.getMessage() + "##");
            SeckillExecution seckillExcution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, seckillExcution);
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/time/now",method=RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Long> time() {

        logger.info("=== SeckillResult is called === ");
        Date now = new Date();
        logger.info("nowtime = " + now.toString() + " |nowtime.gettime=" + now.getTime());
        return new SeckillResult(true, now.getTime());
    }





}
