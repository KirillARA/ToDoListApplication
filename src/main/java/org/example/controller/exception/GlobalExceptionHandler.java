package org.example.controller.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler implements ErrorController{

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @RequestMapping("/error")
    public String redirectToSpecificErrorPage(HttpServletResponse response){
        switch (HttpStatus.valueOf(response.getStatus())){
            case FORBIDDEN -> {
                return "redirect:/error/403";
            }
            case NOT_FOUND -> {
                return "redirect:/error/404";
            }
            default -> {
                return "redirect:/error/500";
            }
        }
    }

    @RequestMapping("/error/500")
    public String getCommonErrorPage(){
        return "public/error/common-error-page";
    }

    @RequestMapping("/error/403")
    public String getForbiddenErrorPage(){
        return "public/error/forbidden-error-page";
    }

    @RequestMapping("/error/404")
    public String getNotFoundErrorPage(){
        return "public/error/not-found-error-page";
    }

    @ExceptionHandler(Throwable.class)
    public String handleThrowable(Throwable t){
        log.error("Unhandled exception", t);
        return "redirect:/error/500";
    }
}
